#include "../include/Watchable.h"
#include "../include/Session.h"
#include "../include/User.h"
using namespace std;

// Constructor
Watchable::Watchable(long id, int length, const vector<string> &tags) :
    id(id), length(length), tags(tags) {
}

// Destructor
Watchable::~Watchable() {
    tags.clear();
}

// Helper
string Watchable::tagsToString() {
    string s = "[";
    for (string tag: tags) {
        s += tag + ", ";
    }
    if (s.length() > 1) {
        s = s.substr(0, s.length() - 2); // Remove the last ", "
    }
    s += "]"; // Add a closing ]
    return s;
}

// Getters
const long Watchable::getId() const {
    return id;
}
int Watchable::getLength() const {
    return length;
}
vector<string> Watchable::getTags() const {
    return tags;
}

// Movie

// Constructor
Movie::Movie(long id, const string &name, int length, const vector<string> &tags):
    Watchable(id, length, tags), name(name) {
}

Movie *Movie::clone() {
    return new Movie(*this);
}

string Movie::toString() const {
    return name;
}

// According to assignment's instructions, after watching a movie,
// next watchable is declared by the active user recommendation algorithm
Watchable* Movie::getNextWatchable(Session &sess) const {
    return sess.getActiveUser()->getRecommendation(sess);
}

// Episode

// Constructor
Episode::Episode(long id, const string &seriesName, int length, int season, int episode, const vector<string> &tags):
    Watchable(id, length, tags), seriesName(seriesName), season(season), episode(episode), nextEpisodeId(id+1) {
}

Episode *Episode::clone() {
    return new Episode(*this);
}

string Episode::toString() const {
    return "" + seriesName + " S" + to_string(season) + "E" + to_string(episode);
}

// According to assignment's instructions, after watching an episode,
// next watchable would be the next episode at the TV show (same season or next one)
// if there is one
// else, a recommendation will declared by the active user recommendation algorithm
Watchable* Episode::getNextWatchable(Session &sess) const {
    if (nextEpisodeId > 0)
        return sess.getContent()[nextEpisodeId - 1];
    else
        return sess.getActiveUser()->getRecommendation(sess);
}

void Episode::setNextEpisodeId(long nextEpisodeId) {
    this->nextEpisodeId = nextEpisodeId;
}