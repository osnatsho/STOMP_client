// #include <iostream>
// #include "../include/ConnectionHandler.h"
// #include "../include/MessageFactory.hpp"

// using namespace std;

// using boost::asio::ip::tcp;

// int main(int argc, char *argv[]) {
// 	ConnectionHandler clientHandler("127.0.0.1", (short)7777);
// 	clientHandler.connect();
// 	std::string msg = MessageFactory::create_connect("1.2","bgu","1","tomer","123");
// 	clientHandler.sendLine(msg);
// }

#include <stdlib.h>
#include "../include/User.h"
#include "../include/ConnectionHandler.h"
#include "../include/socketReader.h"
#include "../include/keyboardInputSend.h"
#include <mutex>
#include <thread>
#include "../include/StateSingleton.h"
#include "../include/StateControler.h"
#include <unistd.h>


int main (int argc, char *argv[]) {

    //User* user = new User();
    ConnectionHandler connectionHandler ("127.0.0.1",7777);
    
    StateControler *sc = StateSingleton::get_state_controler();

    socketReader socketReader_task (1,connectionHandler,sc);
    keyboardInputSend keyboardIS_task(2,connectionHandler,sc);
  
    //while(!sc->get_shutdown()){        // will shutdown when got a specific string after logged out
    std::thread keyboardIS_thread(&keyboardInputSend::run, &keyboardIS_task);
    std::thread socketReader_thread(&socketReader::run, &socketReader_task);

    socketReader_thread.join();
    keyboardIS_thread.join();

    delete sc;
    //}

    //delete user;
    return 0;
}