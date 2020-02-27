#ifndef BOOST_ECHO_CLIENT_STOMPFRAME_H
#define BOOST_ECHO_CLIENT_STOMPFRAME_H

#include <string>
#include <map>

using namespace std;

class stompFrame {
private:
    string command_;
    map<string, string> headers_;
    string body_;

public:
    stompFrame(string frame);
    stompFrame(string command, map<string, string> headers, string body);

    string getCommand() {return command_;}
    string getBody() {return body_;}
    map<string, string> getHeaders() {return headers_;}

    string toString();
};

#endif // BOOST_ECHO_CLIENT_STOMPFRAME_H