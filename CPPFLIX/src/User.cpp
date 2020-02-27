#include "../include/User.h"
#include "../include/Session.h"
#include "../include/Watchable.h"
#include <cmath>
#include <algorithm>
using namespace std;

// User

// Constructor
User::User(const string &name):
    history(), name(name) {
}

// Destructor
User::~User() {
    clean();
}

// Copy constructor
User::User(const User &other) :
    history(),
    name(other.name) {
    copy(other);
}

// Move constructor
User::User(User &&other) :
    history(),
    name(other.name) {
    steal(other);
}

// Copy assignment operator
User& User::operator=(const User& other) {
    if (&other != this) {
        clean();
        copy(other);
    }
    return *this;
}

// Move assignment operator
User& User::operator=(User&& other) {
    if (&other != this) {
        clean();
        steal(other);
    }
    return *this;
}

// Helper

void User::clean() {
    for (Watchable *x: history) {
        delete x;
    }
    history.clear();
}

void User::steal(User &other) {
    history = other.history;
    other.history.clear();
}

void User::copy(const User &other) {
    for (Watchable *watchable : other.history) {
        history.push_back(watchable->clone());
    }
}

// Getters
string User::getName() const {
    return name;
}
vector<Watchable*> User::get_history() const {
    return history;
}

// Setter
void User::setName(const string &name) {
    User::name = name;
}

// Helpers
void User::addHistory(Watchable *item) {
    history.push_back(item->clone());
}

bool User::watched(Watchable *item) {
    for (Watchable *x: history) {
        if (x->getId() == item->getId()) {
            return true;
        }
    }
    return false;
}

// Length Recommender User

// Constructor
LengthRecommenderUser::LengthRecommenderUser(const string &name) :
    User(name) {
}

// Copy constructor
LengthRecommenderUser::LengthRecommenderUser(const LengthRecommenderUser &other) :
    User(other) {
}

// Destructor
LengthRecommenderUser::~LengthRecommenderUser() {
}

// Move constructor
LengthRecommenderUser::LengthRecommenderUser(LengthRecommenderUser &&other) :
    User(other) {
}

// Copy assignment operator
LengthRecommenderUser& LengthRecommenderUser::operator=(const LengthRecommenderUser &other) {
    if (&other != this) {
        clean();
        copy(other);
    }
    return *this;
}

// Move assignment operator
LengthRecommenderUser& LengthRecommenderUser::operator=(LengthRecommenderUser &&other) {
    if (&other != this) {
        clean();
        steal(other);
    }
    return *this;
}

LengthRecommenderUser* LengthRecommenderUser::clone() {
    return new LengthRecommenderUser(*this);
}

LengthRecommenderUser* LengthRecommenderUser::dupUser(const string& newName) {
    auto newUser = new LengthRecommenderUser(*this);
    newUser->setName(newName);
    return newUser;
}

// Find next episode/movie to be recommended by this recommendation algorithm
// (closest to average length, and not watched yet by the user)
Watchable* LengthRecommenderUser::getRecommendation(Session &s){
    double average = getAverage();
    int minDiff = INT32_MAX;
    Watchable *next = nullptr;
    for (Watchable *x : s.getContent()) {
        if (!watched(x)) {
            int diff = abs(x->getLength() - average);
            if (diff < minDiff){
                minDiff = diff;
                next = x;
            }
        }
    }
    return next;
}

// Helper: Calculate all users' watch history length average
double LengthRecommenderUser::getAverage() {
    int totalTimeSeen = 0;
    int numOfSeen = 0;
    for (Watchable *seen : history) {
        totalTimeSeen += seen->getLength();
        numOfSeen++;
    }
    return totalTimeSeen/numOfSeen;
}

// Rerun Recommender User

// Constructor
RerunRecommenderUser::RerunRecommenderUser(const string &name):
    User(name),
    lastRecommended(0) {
}

// Copy constructor
RerunRecommenderUser::RerunRecommenderUser(const RerunRecommenderUser &other) :
    User(other),
    lastRecommended(other.lastRecommended) {
}

// Destructor
RerunRecommenderUser::~RerunRecommenderUser() {
}

// Move constructor
RerunRecommenderUser::RerunRecommenderUser(RerunRecommenderUser &&other) :
    User(other),
    lastRecommended(other.lastRecommended) {
}

// Copy assignment operator
RerunRecommenderUser& RerunRecommenderUser::operator=(const RerunRecommenderUser &other) {
    if (&other != this) {
        clean();
        copy(other);
    }
    lastRecommended = other.lastRecommended;
    return *this;
}

// Move assignment operator
RerunRecommenderUser& RerunRecommenderUser::operator=(RerunRecommenderUser &&other) {
    if (&other != this) {
        clean();
        steal(other);
    }
    lastRecommended = other.lastRecommended;
    return *this;
}

RerunRecommenderUser* RerunRecommenderUser::clone() {
    return new RerunRecommenderUser(*this);
}

RerunRecommenderUser* RerunRecommenderUser::dupUser(const string& newName) {
    auto newUser = new RerunRecommenderUser(*this);
    newUser->setName(newName);
    return newUser;
}

// Find next episode/movie to be recommended by this recommendation algorithm
Watchable* RerunRecommenderUser::getRecommendation(Session &s) {
    lastRecommended = (lastRecommended + 1) % get_history().size();
    return get_history()[lastRecommended];;
}

// Genre Recommender User

// Constructor
GenreRecommenderUser::GenreRecommenderUser(const string &name) :
        User(name) {
}

// Copy constructor
GenreRecommenderUser::GenreRecommenderUser(const GenreRecommenderUser &other) :
    User(other) {
}

// Destructor
GenreRecommenderUser::~GenreRecommenderUser() {
}

// Move constructor
GenreRecommenderUser::GenreRecommenderUser(GenreRecommenderUser &&other) :
    User(other) {
}

// Copy assignment operator
GenreRecommenderUser& GenreRecommenderUser::operator=(const GenreRecommenderUser &other) {
    if (&other != this) {
        clean();
        copy(other);
    }
    return *this;
}

// Move assignment operator
GenreRecommenderUser& GenreRecommenderUser::operator=(GenreRecommenderUser &&other) {
    if (&other != this) {
        clean();
        steal(other);
    }
    return *this;
}

GenreRecommenderUser* GenreRecommenderUser::clone() {
    return new GenreRecommenderUser(*this);
}

GenreRecommenderUser* GenreRecommenderUser::dupUser(const string& newName) {
    auto newUser = new GenreRecommenderUser(*this);
    newUser->setName(newName);
    return newUser;
}

// Find next episode/movie to be recommended by this recommendation algorithm
// (has one of the most popular tags, and not watched yet by the user)
Watchable* GenreRecommenderUser::getRecommendation(Session &s) {
    vector<string> popularTags = getPopularTags();
    for (string tag: popularTags) {
        for (Watchable *watchable: s.getContent()) {
            bool exists = false;
            for (string itemTag: watchable->getTags()) {
                if (itemTag == tag) {
                    exists = true;
                    break;
                }
            }
            if (exists && !watched(watchable)) {
                return watchable;
            }
        }
    }
    return nullptr;
}

vector<string> GenreRecommenderUser::getPopularTags() {
    // Creat an unordered map, which contains all different tags
    // in the user's watch history,
    // and its' number of appearances
    unordered_map <string, int> tagsAppearance;
    for (Watchable *watchable: history) {
        for (string tag: watchable->getTags()) {
            auto userMapIter = tagsAppearance.find(tag);
            if (userMapIter != tagsAppearance.end()) {
                userMapIter->second++;
            }
            else {
                tagsAppearance.insert(make_pair(tag, 1));
            }
        }
    }

    // Copy the unordered map into a vector in order to sort it
    vector<pair<string, int>> tagsVector;
    for (auto userMapIter: tagsAppearance){
        tagsVector.push_back(make_pair(userMapIter.first, userMapIter.second));
    }

    // Sort vector by appearances (using Lambda)
    sort(tagsVector.begin(), tagsVector.end(),
         [](const pair<string, int> &a, const pair<string, int> & b) -> bool {
        if (a.second == b.second) {
            // If same number of appearances, sort by lexicographic order
            return a.first > b.first;
        }
        else {
            return a.second > b.second;
        }
    });

    // Convert pair vector to string vector
    vector<string> sortedTags;
    for (auto pair: tagsVector){
        sortedTags.push_back(pair.first);
    }
    return sortedTags;
}