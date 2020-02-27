#include "../include/Session.h"
#include "../include/json.hpp"
#include "../include/Watchable.h"
#include "../include/User.h"
#include <fstream>

using json = nlohmann::json;
using namespace std;

// Constructor
Session::Session(const string &configFilePath):
        content(), actionsLog(), userMap(), activeUser(nullptr), exitLoop(false) {
    // In order to initialize the class' vectors, we use JSON parser
    ifstream ifs(configFilePath);
    json j = json::parse(ifs);

    // Insert all movies first
    json movies = j["movies"];
    int id = 1;
    for (json::iterator it = movies.begin(); it != movies.end(); it++) {
        json movie = it.value();
        Movie *newMovie = new Movie(id++, movie["name"], movie["length"], movie["tags"]);
        content.push_back(newMovie);
    }

    // Insert all TV series next
    json series = j["tv_series"];
    for (json::iterator it = series.begin(); it != series.end(); it++) {
        json tvShow = it.value();
        vector<int> seasons = tvShow["seasons"];
        int numSeasons = (int)seasons.size();
        for (int i = 1; i <= numSeasons; i++) {
            for(int j = 1 ; j <= seasons[i-1]; j++) {
                Episode *newEpisode = new Episode(id++, tvShow["name"], tvShow["episode_length"], i, j, tvShow["tags"]);
                // Set next episode id
                if(i == numSeasons && j == seasons[i-1]) {
                    // The case of last episode at last season
                    newEpisode->setNextEpisodeId(0);
                }
                else {
                    newEpisode->setNextEpisodeId(id);
                }
                content.push_back(newEpisode);
            }
        }
    }

    // Create default user
    activeUser = new LengthRecommenderUser("default");
    addUser(activeUser->getName(), activeUser);
}

// Destructor
Session::~Session() {
    clean();
}

// Copy Constructor
Session::Session(const Session& other) : content(), actionsLog(), userMap(), activeUser(), exitLoop(false) {
    copy(other);
}

// Move constructor
Session::Session(Session&& other) : content(), actionsLog(), userMap(), activeUser(), exitLoop(false) {
    steal(other);
}

// Copy assignment operator
Session& Session::operator=(const Session& other) {
    if (&other != this) {
        clean();
        copy(other);
    }
    return *this;
}

// Move assignment operator
Session& Session::operator=(Session&& other) {
    if (&other != this) {
        clean();
        steal(other);
    }
    return *this;
}

// Helpers

void Session::clean() {
    for (Watchable *watchable : content) {
        delete watchable;
    }
    content.clear();
    for (BaseAction *action: actionsLog) {
        delete action;
    }
    actionsLog.clear();
    for (auto it: userMap) {
        delete it.second;
    }
    userMap.clear();
    activeUser = nullptr;
}

void Session::copy(const Session& other) {
    for (Watchable *watchable : other.content) {
        content.push_back(watchable->clone());
    }
    for (BaseAction *action: other.actionsLog) {
        actionsLog.push_back(action->clone());
    }
    for (auto it: other.userMap) {
        userMap.insert(make_pair(it.first, it.second->clone()));
    }
    activeUser = getUser(other.activeUser->getName());
}

void Session::steal(Session& other) {
    // Copy the objects from other to this
    content = other.content;
    actionsLog = other.actionsLog;
    userMap = other.userMap;
    activeUser = other.activeUser;

    // Clear the other object fields after "stealing" it
    other.content.clear();
    other.actionsLog.clear();
    other.userMap.clear();
    other.activeUser = nullptr;
}

void Session::start() {
    cout << "SPLFLIX is now on!" << endl;

    // Main loop is initialized.
    // as long as the user don't initialize the exit command
    // it continues running and receives users' commands.
    // Each command has its' syntax
    // so by receiving it, we use the split function
    // and initialize the required action, according to the input
    exitLoop = false;
    while(!exitLoop) {

        string input;
        getline(cin, input);
        vector<string> tokens = split(input);

        BaseAction *action = nullptr;
        // Create user
        if (tokens[0] == "createuser") {
            action = new CreateUser(tokens[1], tokens[2]);
        }

        // Change Active User
        if (tokens[0] == "changeuser") {
            action = new ChangeActiveUser(tokens[1]);
        }

        // Delete User
        if (tokens[0] == "deleteuser") {
            action = new DeleteUser(tokens[1]);
        }

        // Duplicate User
        if (tokens[0] == "dupuser") {
            action = new DuplicateUser(tokens[1], tokens[2]);
        }

        // Print content
        if (tokens[0] == "content") {
            action = new PrintContentList();
        }

        // Print Watch History
        if (tokens[0] == "watchhist") {
            action = new PrintWatchHistory();
        }

        // Print Actions Log
        if (tokens[0] == "log") {
            action = new PrintActionsLog();
        }

        // Watch
        if (tokens[0] == "watch") {
            action = new Watch(stol(tokens[1]));
        }
        // Exit main loop
        if (tokens[0] == "exit") {
            action = new Exit();
        }

        if (action != nullptr) { // Valid input
            // Call "act" function according to user's command
            // Update actions log vector afterwards
            action->act(*this);

            // Insert the action to the beginning of action log vector
            // in order to print action log correctly, when declared
            actionsLog.insert(actionsLog.begin(), action);
        }
    }
}

// Helper
vector<string> Session::split(const string &s) {
    vector<string> tokens;
    string word;
    for (char x : s) {
        if (x == ' ') {
            tokens.push_back(word);
            word = "";
        }
        else {
            word += x;
        }
    }
    if (!word.empty()) {
        tokens.push_back(word);
    }
    return tokens;
}

// User Manipulation

//insert new user to map by name and pointer to user.
void Session::addUser(const string &name, User* user) {
    userMap.insert(make_pair(name,user));
}

bool Session::userExists(const string &userName) {
    auto userMapIter = userMap.find(userName);
    return userMapIter != userMap.end();
}

User* Session::getUser(const string &userName) {
    auto userMapIter = userMap.find(userName);
    if (userMapIter == userMap.end()) {
        return nullptr;
    }
    else {
        return userMapIter->second;
    }
}

// Getters & Setters
vector<Watchable *> &Session::getContent() {
    return content;
}
vector<BaseAction *> &Session::getActionsLog() {
    return actionsLog;
}
unordered_map<string, User *> &Session::getUserMap() {
    return userMap;
}
User *Session::getActiveUser() {
    return activeUser;
}
void Session::setActiveUser(User *user) {
    activeUser = user;
}
void Session::setExit(bool exit) {
    exitLoop = exit;
}