CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude -pthread
LFLAGS:=-lboost_system -pthread

all: EchoClient

EchoClient: bin/connectionHandler.o bin/echoClient.o bin/stompFrame.o
	g++ -o bin/EchoClient bin/connectionHandler.o bin/echoClient.o bin/stompFrame.o $(LFLAGS)

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/stompFrame.o: src/stompFrame.cpp
	g++ $(CFLAGS) -o bin/stompFrame.o src/stompFrame.cpp
        
.PHONY: clean
clean:
	rm -f bin/*

memleak: all
	valgrind --leak-check=full --show-reachable=yes bin/EchoClient