#include "MessageFactory.hpp"


std::string MessageFactory::create_connect(std::string accept_version,std::string host_name,std::string login,std::string passcode)
{
    std::string msg = "CONNECT\naccept-version:"+ accept_version +"\nhost:"+host_name+"\nlogin:"+login+"\npasscode:"+passcode+"\n\n\0";
    return msg;
}

std::string MessageFactory::create_disconnect(User *connected_user)
{
   std::string msg = "DISCONNECT\nrecipt:"+ std::to_string(recipt_id);
   msg += "\n\n\0";
   connected_user->set_disconnect_recipt_id(std::to_string(recipt_id));
   recipt_id++;
   return msg;
}


std::string MessageFactory::create_subscribe(std::string topic_name, std::string subscription_id)
{
    std::string msg = "SUBSCRIBE\nrecipt:"+ std::to_string(recipt_id);
    msg += "\ndestination:" + topic_name +"\nid:"+subscription_id+ "\n\n\0";
    recipt_id++;
    return msg;
}

std::string MessageFactory::create_unsubscribe(std::string subscription_id)
{
    std::string head = "UNSUBSCRIBE\nrecipt:";
    std::string msg =  head + std::to_string(recipt_id);
    msg += "\nid:"+subscription_id+ "\n\n\0";
    recipt_id++;
    return msg;
}

std::string MessageFactory::create_send_message(std::string topic_name,std::string report)
{
    std::string msg = "SEND\nrecipt:"+ std::to_string(recipt_id);
    recipt_id++;
    msg += "\ndestination:" + topic_name +"\n\n" + report + "\n\n\0";
    return msg;
}
