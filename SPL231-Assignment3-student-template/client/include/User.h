#pragma once

#include <vector>
#include <unordered_map>
#include "../include/json.hpp"
#include <tuple>
#include "../include/event.h"

#include <algorithm>
#include <iostream> 
#include <fstream> 

typedef struct t 
{
    std::string user ="";
    std::string game ="";
    Event *event = nullptr;
}USERGAME;

class User {
private:
    std::string name;
    std::unordered_map <std::string, std::string> topic_idByClient_map;
    std::unordered_map <int, std::string> receiptId_returned_map;
    std::unordered_map <std::string,std::vector<Event>*> topic_events_map;
    std::vector<USERGAME> updates_from_user_about_game;
    int subscription_id;
    std::string DisconnectReceiptId;
public:
    User(std::string name);
    ~User();
    void setName(std::string name);
    void subscribeTopic(std::string &topic,std::string subscription_id); 
    std::string unsubscribeTopic(std::string &topic);  
    void update_game_from_user(std::string user_name,std::string game,Event *event);
    std::vector<std::string>report_game(std::string filename);
    void construct_summary(std::map<std::string,std::string> &map,std::map<std::string,std::string> event_map);
    void summary(std::string user_name,std::string game,std::string filename);
    void set_disconnect_recipt_id(std::string id);
    std::string  get_disconnect_recipt_id();
};


std::string my_replaceALL(std::string str,const std::string & from, const std::string& to);