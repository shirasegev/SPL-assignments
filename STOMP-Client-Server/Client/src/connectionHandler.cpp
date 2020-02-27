#include <connectionHandler.h>

using boost::asio::ip::tcp;
using namespace std;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

// Constructor
ConnectionHandler::ConnectionHandler(): host_(), port_(), io_service_(), socket_(io_service_), sendMtx(){}

// Destructor
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect(string host, short port) {
    host_ = host;
    port_ = port;

    cout << "Starting connect to "
        << host_ << ":" << port_ << endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (exception& e) {
        cerr << "Connection failed (Error: " << e.what() << ')' << endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (exception& e) {
        cerr << "recv failed (Error: " << e.what() << ')' << endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (exception& e) {
        cerr << "recv failed (Error: " << e.what() << ')' << endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(string& line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(string& line) {
    return sendFrameAscii(line, '\n');
}

bool ConnectionHandler::getFrameAscii(string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
	do {
		if(!getBytes(&ch, 1)){
			return false;
		}
		if(ch!='\0') {
            frame.append(1, ch);
        }
	} while (delimiter != ch);
    } catch (exception& e) {
	    cerr << "recv failed2 (Error: " << e.what() << ')' << endl;
	    return false;
    }
    return true;
}

bool ConnectionHandler::sendFrameAscii(const string& frame, char delimiter) {
	unique_lock<mutex> lock(sendMtx);
	bool result = sendBytes(frame.c_str()+delimiter, frame.length()+1);
	if(!result) {
	    return false;
	}
	return true;
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    cout << "closing" << endl;
    try{
        socket_.close();
    }catch (...) {
        cout << "closing failed: connection already closed" << endl;
    }
}