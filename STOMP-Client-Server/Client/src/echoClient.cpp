#include <thread>
#include <vector>
#include <set>
#include <connectionHandler.h>
#include <stompFrame.h>
#include <echoClient.h>

using namespace std;

void echoClient::deleteConnectionHandler(){
	delete connectionHandler;
	connectionHandler = nullptr;
}

string echoClient::handleLoginCommand(vector<string> tokens){
    map<string, string> headers;
    size_t pos = tokens[1].find(":");
    string host = tokens[1].substr(0,pos);
    int port = stoi(tokens[1].substr(pos+1));
    username = tokens[2];

    // Build the STOMP frame
    headers["host"] = host;
    headers["login"] = username;
    headers["passcode"] = tokens[3];
    headers["accept-version"] = "1.2";
    stompFrame frame("CONNECT", headers, "");

    // Create a new connection, if needed
    if (connectionHandler == nullptr) {
        connectionHandler = new ConnectionHandler();
        if (!connectionHandler->connect(host, port)) {
            cout << "Could not connect to server" << endl;
			deleteConnectionHandler();
        }
        else {
            networkLoopThread = new thread(&echoClient::networkLoop, this, connectionHandler);
        }
    }
    return frame.toString();
}

string echoClient::handleJoinCommand(vector<string> tokens){
    map<string, string> headers;
    string receiptId = to_string(receipt_id++);

    // Build the STOMP frame
    string genre = tokens[1];
    unique_lock<mutex> gnrLock(genresMtx);
    genres[genre] = genre_id++;
	headers["id"] = to_string(genres[genre]);
	gnrLock.unlock();
    headers["destination"] = genre;
    headers["receipt"] = receiptId;

    unique_lock<mutex> jgLock(joinedGanreMtx);
    joinedGanres.insert(genre);
    jgLock.unlock();

    // new stompFrame because we want to keep this type of sent frames
    // to use its' information when handling receipt command
    stompFrame* frame = new stompFrame("SUBSCRIBE", headers, "");

	unique_lock<mutex> scLock(sentCommandsMtx);
	// Update the state of sentCommands with the frame
	sentCommands[receiptId] = frame;
	scLock.unlock();

	unique_lock<mutex> invLock(inventoryMtx);
	// When joining a club, create the needed collections for that genre
	set<string>* books = inventory[genre];
    if (books == nullptr) {
		inventory[genre] = new set<string>();
    }
	invLock.unlock();
	unique_lock<mutex> pendLock(pendingBorrowsMtx);
	books = pendingBorrows[genre];
	if (books == nullptr) {
		pendingBorrows[genre] = new set<string>();
	}
	pendLock.unlock();

	return frame->toString();
}

string echoClient::handleExitCommand(vector<string> tokens){
    map<string, string> headers;
    string receiptId = to_string(receipt_id++);

	string genre = tokens[1];


    // Build the STOMP frame
    unique_lock<mutex> jgLock(joinedGanreMtx);
	if (joinedGanres.find(genre) == joinedGanres.end()){
		return "";
	}
	else {
        joinedGanres.erase(genre);
    }
	jgLock.unlock();
    unique_lock<mutex> genLock(genresMtx);
	headers["id"] = to_string(genres.find(genre)->second);
    genLock.unlock();
    headers["receipt"] = receiptId;

    // new stompFrame because we want to keep this type of sent frames
    // to use its' information when handling receipt command
    stompFrame* frame = new stompFrame("UNSUBSCRIBE", headers, "");

	unique_lock<mutex> sntLock(sentCommandsMtx);
    // Update the state of sentCommands with the frame
    sentCommands[receiptId] = frame;
    sntLock.unlock();

    return frame->toString();
}

string echoClient::handleLogoutCommand(vector<string> tokens){
    map<string, string> headers;
    string receiptId = to_string(receipt_id++);

    headers["receipt"] = receiptId;
    stompFrame* frame = new stompFrame("DISCONNECT", headers, "");

	unique_lock<mutex> sntLock(sentCommandsMtx);
    sentCommands[receiptId] = frame;
	sntLock.unlock();

	loggedIn = false;

	return frame->toString();
}

string echoClient::handleAddCommand(vector<string> tokens){
    map<string, string> headers;
    string genre = tokens[1];

    string book;
    unsigned long i;
    for (i = 2; i < tokens.size(); i++){
        book += tokens[i] + " ";
    }
    book = book.substr(0, book.length()-1);

    headers["destination"] = genre;
    string body = username + ADD_BOOK_TEXT + book;
    stompFrame frame("SEND", headers, body);

	unique_lock<mutex> invLock(inventoryMtx);
    // Add the book to my inventory list
    set<string>* books = inventory[genre];
    // If the client is not a part of this genre book club,
    // we just add the book without sending a message
    if (books == nullptr) {
        books = new set<string>();
        inventory[genre] = books;
		books->insert(book);
        return "";
    }
    books->insert(book);
	invLock.unlock();
    unique_lock<mutex> jgLock(joinedGanreMtx);
    if(joinedGanres.find(genre) == joinedGanres.end()){
        return "";
    }
    jgLock.unlock();
    return frame.toString();
}

string echoClient::handleBorrowCommand(vector<string> tokens){
    map<string, string> headers;
    string genre = tokens[1];

	string book;
	for (size_t i = 2; i < tokens.size(); i++){
		book += tokens[i] + " ";
	}
	book = book.substr(0, book.length()-1);

    headers["destination"] = genre;
    string body = username + BORROW_BOOK_TEXT + book;
    stompFrame frame("SEND", headers, body);

    unique_lock<mutex> jgLock(joinedGanreMtx);
    if (joinedGanres.find(genre) == joinedGanres.end()){
    	return "";
    }
    jgLock.unlock();

	unique_lock<mutex> pendLock(pendingBorrowsMtx);
    // Add book to pending borrows
    pendingBorrows[genre]->insert(book);
	pendLock.unlock();
    return frame.toString();
}

string echoClient::handleReturnCommand(vector<string> tokens) {
	map<string, string> headers;
	string genre = tokens[1];

	string book;
	for (size_t i = 2; i < tokens.size(); i++) {
		book += tokens[i] + " ";
	}
	book = book.substr(0, book.length() - 1);

	// If the client is not a part of this genre book club, we won't send a message
    unique_lock<mutex> jgLock(joinedGanreMtx);
    if (joinedGanres.find(genre) == joinedGanres.end()){
        return "";
    }
    jgLock.unlock();

	headers["destination"] = genre;
	unique_lock<mutex> borLock(borrowedBooksMtx);
	string body = "Returning " + book + " to " + borrowedBooks[book];
	stompFrame frame("SEND", headers, body);

	// Remove the book from my inventory list
	borrowedBooks.erase(book);
    borLock.unlock();
	unique_lock<mutex> invLock(inventoryMtx);
    inventory[genre]->erase(book);
	invLock.unlock();
    return frame.toString();
}

string echoClient::handleStatusCommand(vector<string> tokens) {
    map<string, string> headers;
    string genre = tokens[1];
    headers["destination"] = genre;
    string body = BOOK_STATUS_TEXT;
    stompFrame frame("SEND", headers, body);
    unique_lock<mutex> jgLock(joinedGanreMtx);
    if (joinedGanres.find(genre) == joinedGanres.end()){
        return "";
    }
    jgLock.unlock();
    return frame.toString();
}

bool echoClient::handleReceiptCommand(stompFrame frame){
    bool exit = false;
	unique_lock<mutex> sntLock(sentCommandsMtx);
    stompFrame* sentFrame = sentCommands[frame.getHeaders()["receipt-id"]];
    sentCommands.erase(frame.getHeaders()["receipt-id"]);

	sntLock.unlock();

    if (sentFrame->getCommand() == "SUBSCRIBE") {
        cout << "Joined club " << sentFrame->getHeaders()["destination"] << endl;
    }

    else if (sentFrame->getCommand() == "UNSUBSCRIBE") {
		unique_lock<mutex> gnrLock(genresMtx);
    	map<string,int>::iterator i;
    	for(i = genres.begin(); i != genres.end(); i++){
			if(i->second == stoi(sentFrame->getHeaders()["id"]))
				break;
    	}
    	gnrLock.unlock();

        cout << "Exited club " << i->first << endl;
    }

    else if (sentFrame->getCommand() == "DISCONNECT") {
        cout << "Disconnected" << endl;
        // Close the connection
        exit = true;
		deleteConnectionHandler();
    }
    delete sentFrame;
    return exit;
}

void echoClient::handleMessageCommand(stompFrame frame) {
    string message = frame.getBody();

    size_t pos;
    string user;
    string book;
    string genre = frame.getHeaders()["destination"];
    map<string, string> headers;
    headers["destination"] = genre;

    cout << message << endl;

    // {user} has added the book {book}
    if ((pos = message.find(ADD_BOOK_TEXT)) != string::npos) {
        user = message.substr(0,pos);
        book = message.substr(pos+ADD_BOOK_TEXT.length());
    }

    // {user} wish to borrow {book}
    else if ((pos = message.find(BORROW_BOOK_TEXT)) != string::npos) {
        user = message.substr(0,pos);
        book = message.substr(pos+BORROW_BOOK_TEXT.length());

		unique_lock<mutex> invLock(inventoryMtx);
        // Check if I have the book
        set<string>* books = inventory[genre];
        bool hasBook = (books->find(book) != books->end());
		invLock.unlock();
        if (hasBook) {
            stompFrame frame("SEND", headers, username + HAS_BOOK_TEXT + book);
            connectionHandler->sendFrameAscii(frame.toString(), '\0');
        }
    }

    // {user} has {book}
    else if ((pos = message.find(HAS_BOOK_TEXT)) != string::npos) {
        user = message.substr(0,pos);
        book = message.substr(pos+HAS_BOOK_TEXT.length());

		unique_lock<mutex> pendLock(pendingBorrowsMtx);
        // Do I still want the book (did I already receive it)
        set<string>* pending = pendingBorrows[genre];
        if (pending->find(book) != pending->end()) {
            stompFrame frame("SEND", headers, TAKING_TEXT + book + " from " + user);
            connectionHandler->sendFrameAscii(frame.toString(), '\0');
            unique_lock<mutex> invLock(inventoryMtx);
            // Add book to inventory and to borrowedBooks
            set<string>* books = inventory[genre];
            books->insert(book);
			invLock.unlock();

			unique_lock<mutex> borLock(borrowedBooksMtx);
            borrowedBooks[book] = user;
			borLock.unlock();
            // Remove from list of pending borrows
            pending->erase(book);
        }
        pendLock.unlock();
    }

    // Taking {book} from {user}
    else if (message.substr(0,TAKING_TEXT.length()) == TAKING_TEXT) {
        pos = message.rfind(" from ");
        user = message.substr(pos + 6);
        book = message.substr(TAKING_TEXT.length(), pos-TAKING_TEXT.length());

        if (user == username) {
            // Remove book from inventory
			unique_lock<mutex> invLock(inventoryMtx);
            inventory[genre]->erase(book);
            invLock.unlock();
        }
    }

    // Returning {book} to {user}
    else if (message.substr(0,RETURNING_TEXT.length()) == RETURNING_TEXT) {
        pos = message.rfind(" to ");
        user = message.substr(pos+4);
        book = message.substr(RETURNING_TEXT.length(), pos-RETURNING_TEXT.length());

        if (user == username) {
			unique_lock<mutex> invLock(inventoryMtx);
            // Return book to inventory
            inventory[genre]->insert(book);
            invLock.unlock();
        }
    }

    // book status
    else if ((message.find(BOOK_STATUS_TEXT)) != string::npos) {

        // Create the body according to the inventory
        string body = username + ":";

		unique_lock<mutex> invLock(inventoryMtx);
        set<string>* books = inventory[genre];
        set<string>::iterator it;
        for (it=books->begin(); it!=books->end(); ++it)
            body += *it + ", ";
        invLock.unlock();
        if (body[body.length()-1] == ' ') {
            body = body.substr(0,body.length()-2);
        }
        stompFrame frame("SEND", headers, body);
        connectionHandler->sendFrameAscii(frame.toString(), '\0');
    }
}

bool echoClient::handleErrorCommand(stompFrame frame){
    cout << frame.getHeaders()["message"] << endl;
    return true;
}

void echoClient::handleConnectedCommand(stompFrame frame){
    loggedIn = true;
    cout << "Login successful" << endl;
}

/*
 * Handle a command received from the user:
 * - Parse the command
 * - Create the appropriate Stomp frame
 * - Save needed state
 * - Convert the frame to a string and return to be sent
 */
string echoClient::handleUserCommand(string commandLine) {

    vector<string> tokens;
    size_t pos;
    while ((pos = commandLine.find(" ")) != string::npos) {
        tokens.push_back(commandLine.substr(0, pos));
        commandLine.erase(0, pos + 1);
    }
    tokens.push_back(commandLine);

    string command = tokens[0];

    if (command == "login") {
        return handleLoginCommand(tokens);
    }
    else if (!loggedIn) {
		cout << "Must login first." << endl;
		return "";
	}
	else if (command == "join") {
		return handleJoinCommand(tokens);
	}
	else if (command == "exit") {
		return handleExitCommand(tokens);
	}
	else if (command == "logout") {
		return handleLogoutCommand(tokens);
	}
	else if (command == "add") {
		return handleAddCommand(tokens);
	}
	else if (command == "borrow") {
		return handleBorrowCommand(tokens);
	}
	else if (command == "return") {
		return handleReturnCommand(tokens);
	}
	else if (command == "status") {
		return handleStatusCommand(tokens);
	}
	else {
		cout << "Invalid command." << endl;
		return "";
	}
}

/*
 * Handle a Stomp message received from the server, based on the message type.
 */
bool echoClient::handleServerMessage(string message) {
    stompFrame frame(message);
    bool exit = false;
    string command = frame.getCommand();

    if (command == "RECEIPT") {
        exit = handleReceiptCommand(frame);
    }
    else if (command == "ERROR") {
		exit = handleErrorCommand(frame);
    }
    else if (command == "CONNECTED") {
        handleConnectedCommand(frame);
    }
    else if (command == "MESSAGE") {
        handleMessageCommand(frame);
    }
    return exit;
}

// Constructor
echoClient::echoClient() :
        sentCommands(),
        borrowedBooks(),
        inventory(),
        pendingBorrows(),
        genres(),
        joinedGanres(),
        sentCommandsMtx(),
        borrowedBooksMtx(),
        inventoryMtx(),
        pendingBorrowsMtx(),
        genresMtx(),
        joinedGanreMtx(),
        username(),
        networkLoopThread() {
}

// Destructor
echoClient::~echoClient() {

    map<string,stompFrame*>::iterator sentCommandsIterator;
    for(sentCommandsIterator = sentCommands.begin(); sentCommandsIterator != sentCommands.end(); sentCommandsIterator++){
        delete sentCommandsIterator->second;
    }
    sentCommands.clear();

    map<string,set<string>*>::iterator inventoryIterator;
    for(inventoryIterator = inventory.begin(); inventoryIterator != inventory.end(); inventoryIterator++){
        delete inventoryIterator->second;
    }
    inventory.clear();

    map<string,set<string>*>::iterator pendingBorrowsIterator;
    for(pendingBorrowsIterator = pendingBorrows.begin(); pendingBorrowsIterator != pendingBorrows.end(); pendingBorrowsIterator++){
        delete pendingBorrowsIterator->second;
    }
    pendingBorrows.clear();

    joinedGanres.clear();
    borrowedBooks.clear();
    genres.clear();
    username.clear();
}

void echoClient::networkLoop(ConnectionHandler* connectionHandler) {
    exitNetworkLoop = false;
    while (!exitNetworkLoop) {

		// Get a message from the server
		string answer;
		if (!connectionHandler->getFrameAscii(answer, '\0')) {
			cout << "Disconnected. Exiting..." << endl;
			break;
		}

		exitNetworkLoop = handleServerMessage(answer);
	}
	deleteConnectionHandler();
}

// keyboard
void echoClient::userLoop(){
    while (1) {
        // Read a command from the user
        const short bufSize = 1024;
        char buf[bufSize];
        cin.getline(buf, bufSize);
        string line(buf);

        if (exitNetworkLoop) {
            networkLoopThread->join();
            delete networkLoopThread;
        }

        if (line == "bye") {
            return;
        }

        // Handle the command from the user
        string frame = handleUserCommand(line);
        if(frame.length() != 0) {
            // If the handling return a response frame, send it
			if (connectionHandler == nullptr) {
				cout << "Not connected. Need to login first." << endl;
				continue;
			}

			if (!connectionHandler->sendFrameAscii(frame, '\0')) {
				cout << "Disconnected. Exiting..." << endl;
				break;
			}
		}
    }
}

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    echoClient *client = new echoClient();
    client->userLoop();
    delete client;
    return 0;
}