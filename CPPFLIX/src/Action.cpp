#include "../include/Action.h"
#include "../include/User.h"
#include "../include/Session.h"
#include "../include/Watchable.h"
using namespace std;

// Base action

// Constructor
// Initialize all actions' status to PENDING
BaseAction::BaseAction():
    errorMsg(),
    status(ActionStatus::PENDING) {
}

// Destructor
BaseAction::~BaseAction() {
}

ActionStatus BaseAction::getStatus() const {
    return status;
}

// Update an action status after completing it
void BaseAction::complete() {
    status = ActionStatus::COMPLETED;
}

string BaseAction::getErrorMsg() const {
    return errorMsg;
}

void BaseAction::error(const std::string &errorMsg) {
    status = ActionStatus::ERROR;
    this->errorMsg = errorMsg;
    cout << "Error - " << this->errorMsg << endl;
}

// Helper: to all action classes "toString()" function
string BaseAction::buildString(const string& action) const {
    if (status==ActionStatus::ERROR)
        return action + " ERROR: " + errorMsg;
    else if (status==ActionStatus::PENDING)
        return action + " PENDING";
    else
        return action + " COMPLETED";
}

// Create User

// Constructor
CreateUser::CreateUser(const string &userName, const string &recAlg):
    BaseAction(),
    userName(userName),
    recAlg(recAlg) {
}

// Clone
CreateUser* CreateUser::clone() {
    return new CreateUser(*this);
}

void CreateUser::act(Session &sess) {
    // Make sure valid input
    if (recAlg != "len" && recAlg != "rer" && recAlg != "gen")
        error("The 3-letter code is invalid");
    else if (sess.userExists(userName))
        error("The new user name is already taken");

    else { //the input is valid.
        // Create a new user according to its' preferred recommendation algorithm
        User *user;
        // Length Recommender User
        if (recAlg == "len") {
           user = new LengthRecommenderUser(userName);
        }
        // Rerun Recommender User
        else if (recAlg == "rer") {
            user = new RerunRecommenderUser(userName);
        }
        else { // Genre Recommender User
            user = new GenreRecommenderUser(userName);
        }
        sess.addUser(userName, user);
        // Declare the action as COMPLETE
        complete();
    }
}

string CreateUser::toString() const {
    return buildString("CreateUser");
}

// Change Active User

// Constructor
ChangeActiveUser::ChangeActiveUser(const string& userName):
    BaseAction(),
    userName(userName) {
}

ChangeActiveUser* ChangeActiveUser::clone() {
    return new ChangeActiveUser(*this);
}

void ChangeActiveUser::act(Session &sess) {
    User *user = sess.getUser(userName);
    if (user == nullptr) // A user with the given name is not exists
        error("the user doesn't exist");
    else {
        sess.setActiveUser(user);
        // Declare the action as COMPLETE
        complete();
    }
}

string ChangeActiveUser::toString() const {
    return buildString("ChangeActiveUser");
}

// Delete User

// Constructor
DeleteUser::DeleteUser(const string& userName):
    BaseAction(),
    userName(userName) {
}

DeleteUser* DeleteUser::clone() {
    return new DeleteUser(*this);
}

void DeleteUser::act(Session &sess) {
    if (sess.userExists(userName)) { // Make sure valid input

        // Delete user by removing it from user map
        auto userMapIter = sess.getUserMap().find(userName);
        delete userMapIter->second;
        sess.getUserMap().erase(userName);

        // Declare the action as COMPLETE
        complete();
    }
    else { // A user with the given name is not exists
        error("The user doesn't exist");
    }
}

string DeleteUser::toString() const {
    return buildString("DeleteUser");
}

// Duplicate User

// Constructor
DuplicateUser::DuplicateUser(const string& userName1, const string& userName2):
    BaseAction(),
    userName1(userName1),
    userName2(userName2) {
}

DuplicateUser* DuplicateUser::clone() {
    return new DuplicateUser(*this);
}

void DuplicateUser::act(Session &sess) {
    // Make sure valid input
    if (!sess.userExists(userName1)) {
        error("The user doesn't exist");
    }
    else if (sess.userExists(userName2)) {
        error("There is already a user with that name");
    }
    else {
        User* from = sess.getUser(userName1);

        // Even though clone creates a new user,
        // it is handled by the session
        // that is why rule of 5 is implemented there
        User* newUser = from->dupUser(userName2);

        // Add the copied user to user map
        sess.addUser(userName2, newUser);

        // Declare the action as COMPLETE
        complete();
    }
}

string DuplicateUser::toString() const {
    return buildString("DuplicateUser");
}

// Print Content List

// Constructor
PrintContentList::PrintContentList(): BaseAction() {
}

PrintContentList* PrintContentList::clone() {
    return new PrintContentList(*this);
}

void PrintContentList::act(Session &sess) {
    for(Watchable *iter: sess.getContent()) {
        cout << to_string(iter->getId()) << ". " << iter->toString() << " " << iter->getLength() << " minutes " << iter->tagsToString() <<  endl;
    }
    // Declare the action as COMPLETE
    complete();
}

string PrintContentList::toString() const {
    return buildString("PrintContentList");
}

// Print Watch History

// Constructor
PrintWatchHistory::PrintWatchHistory() : BaseAction() {
}

PrintWatchHistory* PrintWatchHistory::clone() {
    return new PrintWatchHistory(*this);
}

void PrintWatchHistory::act(Session &sess) {
    int i=1;
    cout << "Watch History for " << sess.getActiveUser()->getName() << endl;
    for(auto &watched: sess.getActiveUser()->get_history()){
        cout << to_string(i) << ". " << watched->toString() << endl;
        i++;
    }
    // Declare the action as COMPLETE
    complete();
}

string PrintWatchHistory::toString() const {
    return buildString("PrintWatchHistory");
}

// Watch

// Constructor
Watch::Watch(const long &contentId):
    BaseAction(),
    contentId(contentId) {
}

Watch* Watch::clone() {
    return new Watch(*this);
}

void Watch::act(Session &sess) {
    Watchable *content = sess.getContent()[contentId-1];
    cout << "Watching " << content->toString() << endl;

    // After watching a certain content,
    // we update the users' watch history by adding it
    sess.getActiveUser()->addHistory(content);

    // Declare the action as COMPLETE after insert the watched content to history
    // "Watch" action is recursive, and that is why we first declare the current action as complete,
    // and then initialize another call to the method, if needed
    complete();

    // Prepare the next recommendation
    // according to assignments' instructions
    // and users' recommendation algorithm
    Watchable *next = content->getNextWatchable(sess);
    if (next != nullptr) {
        cout << "We recommend watching " << next->toString() << " Continue Watching[y/n]" << endl;
        string input;
        getline(cin, input);
        if (input == "y") {
            // Executing a new watch command
            auto action = new Watch(next->getId());
            action->act(sess);
            sess.getActionsLog().insert(sess.getActionsLog().begin(), action);
        }
    }
}

string Watch::toString() const {
    return buildString("Watch");
}

// Print Action Log

// Constructor
PrintActionsLog::PrintActionsLog(): BaseAction() {
}

PrintActionsLog* PrintActionsLog::clone() {
    return new PrintActionsLog(*this);
}

void PrintActionsLog::act(Session &sess) {
    for(BaseAction *iter: sess.getActionsLog())
        cout << iter->toString() << endl;
    // Declare the action as COMPLETE
    complete();
}

string PrintActionsLog::toString() const {
    return buildString("PrintActionsLog");
}

// Exit

// Constructor
Exit::Exit() : BaseAction() {
}

Exit* Exit::clone() {
    return new Exit(*this);
}

void Exit::act(Session &sess) {
    sess.setExit(true);
    // Declare the action as COMPLETE
    complete();
}

string Exit::toString() const {
    return buildString("Exit");
}