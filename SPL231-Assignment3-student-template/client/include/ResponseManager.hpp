#pragma once

#include <iostream>
#include <string>
#include <sstream>
#include <vector>
#include <thread>
#include "../include/StateControler.h"
#include "../include/User.h"
#include "../include/MessageFactory.hpp"
#include <algorithm>
#include <unistd.h>

static std::string accept_version = "1.2";;
static std::string host_name = "stomp.cs.bgu.ac.il";
static std::string connection_ip = "127.0.0.1";
static int connection_port = 7777;

class ResponseManager
{
     public:
        static std::vector<std::string> split(std::string input,std::string delimiter);
        static bool host_format_check(std::string host);
        static bool port_format_check( std::string port);
        static std::vector<std::string> parseInput(std::string income,StateControler *sc,User **connected_user);
        static std::string parseServerMessage(std::string income,StateControler *sc,User **connected_user);
        static bool is_empty(std:: string inp);
        static void set_host(std::string);
        static void set_port(int port);
        static std::string get_host();
        static int get_port();
};


std::string my_extract(std::string origin,std::string start,std::string end);
