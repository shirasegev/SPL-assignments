#ifndef SESSION_H_
#define SESSION_H_

#include <vector>
#include <unordered_map>
#include <string>
#include "Action.h"

using namespace std;

class User;
class Watchable;

class Session{
public:
    Session(const string &configFilePath); // Constructor

    // Implementing Rule of 5
    virtual ~Session(); // Destructor

    Session(const Session& other); // Copy Constructor
    Session(Session&& other); // Move Constructor
    Session& operator=(const Session& other); // Assignment operator
    Session& operator=( Session&& other); // Move Assignment operator

    void start();

    // Newly implemented

    // User manipulation
    void addUser(const string&, User*);
    bool userExists(const string& name);
    User* getUser(const string &userName);

    // Getters & setters
    vector<Watchable *> &getContent();
    vector<BaseAction *> &getActionsLog();
    unordered_map<string, User *> &getUserMap();
    void setActiveUser(User *activeUser);
    User* getActiveUser();

    void setExit(bool exit);

private:
    vector<Watchable*> content;
    vector<BaseAction*> actionsLog;
    unordered_map<string, User*> userMap;
    User* activeUser;

    bool exitLoop;

    // Newly implemented
    void steal(Session &other);
    void copy(const Session &other);
    void clean();

    static vector<string> split(const string &s);
};
#endif