
CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude -lstdc++
LDFLAGS:=-lboost_system -lpthread 

all: StompWCIClient

StompWCIClient: bin/ConnectionHandler.o bin/event.o bin/keyboardInputSend.o bin/MessageFactory.o bin/ResponseManager.o bin/socketReader.o bin/StompClient.o bin/User.o bin/StateControler.o
	g++ -o bin/StompWCIClient bin/ConnectionHandler.o bin/event.o bin/keyboardInputSend.o bin/MessageFactory.o bin/ResponseManager.o bin/socketReader.o bin/StompClient.o bin/User.o bin/StateControler.o $(LDFLAGS)

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/event.o: src/event.cpp
	g++ $(CFLAGS) -o bin/event.o src/event.cpp

bin/keyboardInputSend.o: src/keyboardInputSend.cpp
	g++ $(CFLAGS) -o bin/keyboardInputSend.o src/keyboardInputSend.cpp

bin/MessageFactory.o: src/MessageFactory.cpp
	g++ $(CFLAGS) -o bin/MessageFactory.o src/MessageFactory.cpp

bin/ResponseManager.o: src/ResponseManager.cpp
	g++ $(CFLAGS) -o bin/ResponseManager.o src/ResponseManager.cpp

bin/socketReader.o: src/socketReader.cpp
	g++ $(CFLAGS) -o bin/socketReader.o src/socketReader.cpp

bin/StateControler.o: src/StateControler.cpp
	g++ $(CFLAGS) -o bin/StateControler.o src/StateControler.cpp

bin/StompClient.o: src/StompClient.cpp
	g++ $(CFLAGS) -o bin/StompClient.o src/StompClient.cpp

bin/User.o: src/User.cpp
	g++ $(CFLAGS) -o bin/User.o src/User.cpp

	


.PHONY: clean
clean:
	rm -f bin/*
	
