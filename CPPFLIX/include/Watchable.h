#ifndef WATCHABLE_H_
#define WATCHABLE_H_

#include <string>
#include <vector>
using namespace std;

class Session;

class Watchable {
public:
    // This class, and its' successors, are not managing resources
    // (All new objects that are created here are managed by session).
    // That is why we chose not to implement all rule of 5 for this class
    // and the default copy constructor is enough for a completion to rule of 3
    Watchable(long id, int length, const vector<string>& tags); // Constructor
    virtual ~Watchable();   // Destructor
    virtual string toString() const = 0;
    virtual Watchable* getNextWatchable(Session&) const = 0;

    // Newly created

    // All Watchable's "childes" implement a clone virtual function
    // in order to create a sub class from a base class pointer
    // (initialise a certain action from its' "father" class)
    virtual Watchable* clone() = 0;

    string tagsToString();

    // Getters & setters
    const long getId() const;
    int getLength() const;
    vector<string> getTags() const;

protected:

private:
    const long id;
    int length;
    vector<string> tags;
};

class Movie : public Watchable{
public:
    Movie(long id, const string& name, int length, const vector<string>& tags);
    virtual string toString() const;
    virtual Watchable* getNextWatchable(Session&) const;

    virtual Movie* clone();

private:
    string name;
};


class Episode: public Watchable{
public:
    Episode(long id, const string& seriesName, int length, int season, int episode ,const vector<string>& tags);
    virtual string toString() const;
    virtual Watchable* getNextWatchable(Session&) const;

    void setNextEpisodeId(long nextEpisodeId);

    virtual Episode* clone();

private:
    string seriesName;
    int season;
    int episode;
    long nextEpisodeId;
};
#endif