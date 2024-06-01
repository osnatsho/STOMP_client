cmake_minimum_required(VERSION 3.10.2)
project(Boost_Echo_Client)

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -pthread")

set(CMAKE_CXX_STANDARD 14)

include_directories(.)
include_directories(include)
include_directories(src)

link_libraries("-lboost_system")

add_executable(client
        makefile src/StompClient.cpp include/StateSingleton.h include/StateControler.h include/ConnectionHandler.h include/event.h include/json.hpp include/keyboardInputSend.h include/MessageFactory.hpp include/ResponseManager.hpp include/socketReader.h include/User.h src/ConnectionHandler.cpp src/event.cpp src/keyboardInputSend.cpp src/MessageFactory.cpp src/ResponseManager.cpp src/socketReader.cpp src/StateControler.cpp src/User.cpp)