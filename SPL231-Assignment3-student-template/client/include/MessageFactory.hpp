#pragma once

#include <string>
#include "../include/User.h"
 
static int recipt_id = 0;

class MessageFactory 
{
    public:
    static std::string create_connect(std::string accept_version,std::string host_name,std::string login,std::string passcode);
    static std::string create_disconnect(User *connected_user);
    static std::string create_subscribe(std::string topic_name, std::string subscription_id);
    static std::string create_unsubscribe(std::string subscription_id);
    static std::string create_send_message(std::string topic_name,std::string report);
};
