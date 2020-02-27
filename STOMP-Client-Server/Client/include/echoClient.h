#ifndef BOOST_ECHO_CLIENT_ECHOCLIENT_H
#define BOOST_ECHO_CLIENT_ECHOCLIENT_H

#include <string>
#include <map>
#include <set>
#include <stompFrame.h>
#include <connectionHandler.h>
#include <thread>
#include <mutex>

using namespace std;

class echoClient {

private:

    // data structures
    map<string, stompFrame*> sentCommands;
    map<string, string> borrowedBooks;
    map<string, set<string>*> inventory;
    map<string, set<string>*> pendingBorrows;
    map<string, int> genres;
    set<string> joinedGanres;

    // thread safe
	mutex sentCommandsMtx;
	mutex borrowedBooksMtx;
	mutex inventoryMtx;
	mutex pendingBorrowsMtx;
	mutex genresMtx;
	mutex joinedGanreMtx;

	// protocol
    int receipt_id = 1;
    int genre_id = 1;

    string username;

    const string ADD_BOOK_TEXT = " has added the book ";
    const string BORROW_BOOK_TEXT = " wish to borrow ";
    const string HAS_BOOK_TEXT = " has ";
    const string BOOK_STATUS_TEXT = "book status";
    const string TAKING_TEXT = "Taking ";
    const string RETURNING_TEXT = "Returning ";

    ConnectionHandler* connectionHandler = nullptr;
    thread* networkLoopThread;

    bool exitNetworkLoop = false;
    bool loggedIn = false;

    // handleUserCommand
    string handleLoginCommand(vector<string> tokens);
    string handleJoinCommand(vector<string> tokens);
    string handleExitCommand(vector<string> tokens);
    string handleLogoutCommand(vector<string> tokens);
    string handleAddCommand(vector<string> tokens);
    string handleBorrowCommand(vector<string> tokens);
    string handleReturnCommand(vector<string> tokens);
    string handleStatusCommand(vector<string> tokens);

    // handleServerMessage
    bool handleReceiptCommand(stompFrame frame);
    void handleMessageCommand(stompFrame frame);
    bool handleErrorCommand(stompFrame frame);
    void handleConnectedCommand(stompFrame frame);
	void deleteConnectionHandler();

protected:

    echoClient(const echoClient&);
    echoClient operator=(const echoClient&);

public:

    string handleUserCommand(string commandLine);
    bool handleServerMessage(string message);
    void networkLoop(ConnectionHandler* connectionHandler);
    void userLoop();

    echoClient();
    virtual ~echoClient();
};

#endif