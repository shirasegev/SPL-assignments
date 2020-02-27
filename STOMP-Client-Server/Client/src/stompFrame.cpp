#include "stompFrame.h"

stompFrame::stompFrame(string frame): command_(), headers_(), body_() {
    unsigned int pos = frame.find("\n\n");
    string start = frame.substr(0, pos+1);
    body_ = frame.substr(pos+2);
    if(body_.length() > 0) {
        while (body_[body_.length() - 1] == '\n') {
            body_ = body_.substr(0, body_.length() - 1);
        }
    }

    pos = start.find("\n");
    command_ = start.substr(0, pos);
    start.erase(0, pos + 1);

    while (start.length() != 0) {
        pos = start.find("\n");
        string header = start.substr(0, pos);
        start.erase(0, pos + 1);
        unsigned int colonPos = header.find(":");
        headers_[header.substr(0,colonPos)] = header.substr(colonPos+1);
    }
}

string stompFrame::toString() {
    string output = command_ + '\n';
    for (auto const& header : headers_)
    {
        output += (header.first + ':' + header.second + '\n');
    }
    if(body_.length() != 0)
    	output += ('\n' + body_ + '\n');
    else
    	output += ('\n');
    return output;
}

stompFrame::stompFrame(string command, map<string, string> headers, string body):
    command_(command),
    headers_(headers),
    body_(body)
{}