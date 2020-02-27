#ifndef USER_H_
#define USER_H_

#include <vector>
#include <string>
#include <unordered_set>
#include <unordered_map>
class Watchable;
class Session;

using namespace std;

class User{
public:
    User(const string& name); // Constructor

    // Implementing rule of 5 for User and its' successors,
    // who are managing resources (to make a deep copy of Watchable objects for history vector).
    virtual ~User(); // Destructor
    User(const User& other); // Copy constructor
    User(User&& other); // Move constructor
    User& operator=(const User& other); // Copy assignment operator
    User& operator=(User&& other); // Move assignment operator

    virtual Watchable* getRecommendation(Session& s) = 0;
    string getName() const;
    vector<Watchable*> get_history() const;

    // Newly implemented

    // All User's "childes" implement a clone virtual function
    // in order to create a sub class from a base class pointer
    // (initialise a certain action from its' "father" class)
    virtual User* clone() = 0;
    virtual User* dupUser(const string &newName) = 0;

    void addHistory(Watchable *item);

protected:
    vector<Watchable*> history;

    // Newly implemented

    void copy(const User& other);
    void steal(User& other);
    void clean();

    bool watched(Watchable* item);

public:
    void setName(const string &name);

private:
    string name;
};

// Length Recommender User

class LengthRecommenderUser : public User {
public:
    LengthRecommenderUser(const string& name); // Constructor

    // Implementing rule of 5
    LengthRecommenderUser(const LengthRecommenderUser& other); // Copy constructor
    virtual ~LengthRecommenderUser(); // Destructor
    LengthRecommenderUser(LengthRecommenderUser &&other); // Move constructor
    LengthRecommenderUser& operator=(const LengthRecommenderUser& other); // Copy assignment operator
    LengthRecommenderUser& operator=(LengthRecommenderUser&& other); // Move assignment operator

    virtual LengthRecommenderUser* clone();
    virtual LengthRecommenderUser* dupUser(const string &newName);

    virtual Watchable* getRecommendation(Session& s);

    double getAverage();
private:
};

// Rerun Recommender User
class RerunRecommenderUser : public User {
public:
    RerunRecommenderUser(const string& name); // Constructor

    // Implementing rule of 5
    RerunRecommenderUser(const RerunRecommenderUser& other); // Copy constructor
    virtual ~RerunRecommenderUser(); // Destructor
    RerunRecommenderUser(RerunRecommenderUser&& other); // Move constructor
    RerunRecommenderUser& operator=(const RerunRecommenderUser& other); // Copy assignment operator
    RerunRecommenderUser& operator=(RerunRecommenderUser&& other); // Move assignment operator

    virtual RerunRecommenderUser* clone();
    virtual RerunRecommenderUser* dupUser(const string &newName);

    virtual Watchable* getRecommendation(Session& s);

private:
    int lastRecommended;
};

// Genre Recommender User

class GenreRecommenderUser : public User {
public:
    GenreRecommenderUser(const string& name); // Constructor

    // Implementing rule of 5
    GenreRecommenderUser(const GenreRecommenderUser& other); // Copy constructor
    virtual ~GenreRecommenderUser(); // Destructor
    GenreRecommenderUser(GenreRecommenderUser&& other); // Move constructor
    GenreRecommenderUser& operator=(const GenreRecommenderUser& other); // Copy assignment operator
    GenreRecommenderUser& operator=(GenreRecommenderUser&& other); // Move assignment operator

    virtual GenreRecommenderUser* clone();
    virtual GenreRecommenderUser* dupUser(const string &newName);

    virtual Watchable* getRecommendation(Session& s);

    vector<string> getPopularTags();
private:
};
#endif