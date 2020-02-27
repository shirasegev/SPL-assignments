#ifndef ACTION_H_
#define ACTION_H_

#include <string>
#include <iostream>
using namespace std;

class Session;

enum ActionStatus {
    PENDING, COMPLETED, ERROR
};

class BaseAction {
public:
    BaseAction(); // Constructor

    // Implementing copy constructor to all successors
    // who has private fields (that we added)
    virtual ~BaseAction(); // Destructor

    ActionStatus getStatus() const;
    virtual void act(Session& sess) = 0;
    virtual string toString() const = 0;

    // All Base action's "childes" implement a clone virtual function
    // in order to create a sub class from a base class pointer
    // (initialise a certain action from its' "father" class)
    // In addition, to use it correctly, we added a constructor
    // and a copy constructor to each action.
    virtual BaseAction* clone() = 0;

protected:
    void complete();
    void error(const string& errorMsg);
    string getErrorMsg() const;

    string buildString(const string& action) const;

private:
    string errorMsg;
    ActionStatus status;
};

class CreateUser : public BaseAction {
public:
    CreateUser(const string& userName, const string& recAlg);

    virtual CreateUser* clone();

    virtual void act(Session& sess);
    virtual string toString() const;

private:
    string userName;
    string recAlg;
};

class ChangeActiveUser : public BaseAction {
public:
    ChangeActiveUser(const string& userName);

    virtual ChangeActiveUser* clone();

    virtual void act(Session& sess);
    virtual string toString() const;

private:
    string userName;
};

class DeleteUser : public BaseAction {
public:
    DeleteUser(const string& userName);

    virtual DeleteUser* clone();

    virtual void act(Session & sess);
    virtual string toString() const;

private:
    string userName;
};

class DuplicateUser : public BaseAction {
public:
    DuplicateUser(const string& userName1, const string& userName2);

    virtual DuplicateUser* clone();

    virtual void act(Session & sess);
    virtual string toString() const;

private:
    string userName1;
    string userName2;
};

class PrintContentList : public BaseAction {
public:
    PrintContentList();

    virtual PrintContentList* clone();

    virtual void act (Session& sess);
    virtual string toString() const;
};

class PrintWatchHistory : public BaseAction {
public:
    PrintWatchHistory();

    virtual PrintWatchHistory* clone();

    virtual void act (Session& sess);
    virtual string toString() const;
};

class Watch : public BaseAction {
public:
    Watch(const long &contentId);

    virtual Watch* clone();

    virtual void act(Session& sess);
    virtual string toString() const;

private:
    long contentId;
};

class PrintActionsLog : public BaseAction {
public:
    PrintActionsLog();

    virtual PrintActionsLog* clone();

    virtual void act(Session& sess);
    virtual string toString() const;
};

class Exit : public BaseAction {
public:
    Exit();

    virtual Exit* clone();

    virtual void act(Session& sess);
    virtual string toString() const;
};
#endif