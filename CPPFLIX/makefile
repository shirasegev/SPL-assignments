# Define some Makefile variables for the compiler and compiler flags
# variables are later used in the Makefile: $()
CC = g++
CFLAGS  = -g -Wall -Weffc++ -std=c++11
LFLAGS  = -L/usr/lib

# target
all: bin/splflix

bin/splflix: bin/Action.o bin/Watchable.o bin/Session.o bin/User.o bin/Main.o
	@echo 'Building target: splflix'
	$(CC) -o bin/splflix bin/Action.o bin/Watchable.o bin/Session.o bin/User.o bin/Main.o $(LFLAGS)


# Depends on the source and header files
bin/Action.o: src/Action.cpp
	@echo 'Building target: Action.o'
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Action.o src/Action.cpp

# Depends on the source and header files
bin/Watchable.o: src/Watchable.cpp
	@echo 'Building target: Watchable.o'
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Watchable.o src/Watchable.cpp

# Depends on the source and header files
bin/Session.o: src/Session.cpp
	@echo 'Building target: Session.o'
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Session.o src/Session.cpp

# Depends on the source and header files
bin/User.o: src/User.cpp
	@echo 'Building target: User.o'
	$(CC) $(CFLAGS) -c -Iinclude -o bin/User.o src/User.cpp

# Depends on the source and header files
bin/Main.o: src/Main.cpp
	@echo 'Building target: Main.o'
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Main.o src/Main.cpp



# Helpers

#Clean the build directory
clean:
	rm -f bin/*

# Check memory leak using valgrind
memleak: all
	valgrind --leak-check=full --show-reachable=yes bin/splflix config1.json

# Create zip file as described at assignment specifications
zip: clean
	rm -f 208825349-205874142.zip
	zip -r 208825349-205874142.zip makefile src include bin