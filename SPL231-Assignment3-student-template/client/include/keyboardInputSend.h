#pragma once

#include "../include/ConnectionHandler.h"
#include "../include/StateControler.h"
#include "../include/StateSingleton.h"
#include <thread>

class keyboardInputSend {
public:
    keyboardInputSend(int id, ConnectionHandler& connectionHandler,StateControler *sc);
    void run();
private:
    const int id;
    ConnectionHandler &CH;
    StateControler *sc;
};
