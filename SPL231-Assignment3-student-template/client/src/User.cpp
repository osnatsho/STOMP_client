// #include <string>
#include "../include/User.h"


User::User(std::string name) : name(name),topic_idByClient_map(),receiptId_returned_map(),topic_events_map(),updates_from_user_about_game(),subscription_id(0),DisconnectReceiptId("---")
{
    // this->name = name;
    // this->subscription_id = 0;
    // this->DisconnectReceiptId = "---";
}
User::~User()
{
    
}
void User::setName(std::string name)
{
    this->name = name;
}
void User::subscribeTopic(std::string &topic,std::string subscription_id)
{
    topic_idByClient_map.insert(std::make_pair(topic,subscription_id));
}

std::string User::unsubscribeTopic(std::string &topic)
{
    try{
    std::string subscription_id = topic_idByClient_map.at(topic);
    topic_idByClient_map.erase(topic);
    return subscription_id;
    }
    catch(std::exception e)
    {
        return "-1";
    }
   
}


void User::update_game_from_user(std::string user_name,std::string game,Event *event)
{
    USERGAME ug;
    ug.game = game;
    ug.user = user_name;
    ug.event = event;
    updates_from_user_about_game.push_back(ug);
}


void User::construct_summary(std::map<std::string,std::string> &map,std::map<std::string,std::string> event_map)
{
            std::map<std::string,std::string>::iterator it;
            for(it =  event_map.begin() ; it !=  event_map.end(); it++)
            {
                try
                {
                    map.at(it->first) = it->second;
                }
                catch(std::exception e){
                    map.insert(std::make_pair(it->first,it->second));
                }
            }
            
}


void User::summary(std::string user_name,std::string game,std::string filename)
{
    // std::sort(updates_from_user_about_game.begin(),updates_from_user_about_game.end(),[](const USERGAME ud1,const USERGAME ud2){ 
    //     if(ud1.halftime && !ud2.halftime)
    //         return 1;
    //     if(!ud1.halftime && ud2.halftime)
    //         return -1;
    //     else
    //         if(ud1.time > ud1.time)
    //             return 1;
    //         return -1;
    //     });
    std::string output;
    std::map<std::string,std::string> game_stats_map;
    std::map<std::string,std::string> teama_stats_map;
    std::map<std::string,std::string> teamb_stats_map;
    std::map<std::string,std::string> event_stats_map;
    std::string event_stats;
    for(USERGAME ug :updates_from_user_about_game)
    {
        if(ug.game.compare(game) == 0 && ug.user.compare(user_name) == 0)
        {
          construct_summary(game_stats_map,ug.event->get_game_updates());
          construct_summary(teama_stats_map,ug.event->get_team_a_updates());
          construct_summary(teamb_stats_map,ug.event->get_team_b_updates());
          event_stats_map.insert(std::make_pair(std::to_string(ug.event->get_time()) + " - " + ug.event->get_name() ,ug.event->get_discription()));
        }
    }

    game = my_replaceALL(game,"_"," vs ");
    output+= game+ "\n";
    output+="Game stats:\nGeneral stats:\n";

    std::map<std::string,std::string>::iterator it;
    for(it =  game_stats_map.begin() ; it !=  game_stats_map.end(); it++)
    {
        output += "\t"+ it->first + ":" + it->second + "\n";
    }
    int seperator_token = game.find(" vs ");
    output+= game.substr(0,seperator_token) + " stats:\n";
    for(it =  teama_stats_map.begin() ; it !=  teama_stats_map.end(); it++)
    {
        output += "\t" + it->first + ":" + it->second + "\n";
    }
    output+= game.substr(seperator_token+4,std::string::npos) + " stats:\n";
    for(it =  teamb_stats_map.begin() ; it !=  teamb_stats_map.end(); it++)
    {
        output += "\t" + it->first + ":" + it->second + "\n";
    }
    
    output+= "Game event reports:\n";
    for(it =  event_stats_map.begin() ; it !=  event_stats_map.end(); it++)
    {
        output += it->first + ":\n\n" + it->second + "\n";
    }
    
            
    std::cout << output;
    //write to file output
    std::ofstream outdata;
    outdata.open(filename);
    if(!outdata)
    {
        std::cout<<"file could not be opened!" << std::endl;
        return;
    }
    outdata << output;
    outdata.close();
}

 std::vector<std::string> User::report_game(std::string filename)
 {
    std::vector<std::string> events_output;
    try{
    auto n_and_e = parseEventsFile(filename);
    std::string result ="";
    for(Event e : n_and_e.events)
    {
        result += "user:"+ this->name + "\n";
        result += "team a:"+ n_and_e.team_a_name + "\n";
        result += "team b:"+ n_and_e.team_b_name + "\n";
        std::string topic = n_and_e.team_a_name + "_" + n_and_e.team_b_name;
        try
        {
            topic_events_map.at(topic);
        }
        catch(std::exception e)
        {
            topic_events_map.insert(std::make_pair(topic,new std::vector<Event>()));
        }

        topic_events_map.at(topic)->push_back(e);
        result += "event name:"+ e.get_name() + "\n";
        result += "time:"+ std::to_string(e.get_time());
        result += + "\n";
        result += "general game updates:\n";
        for(auto itr : e.get_game_updates())
        {
            result += "\t"+itr.first + ":" + itr.second + "\n";
        }
        result += "team a updates: "+ e.get_team_a_name() + "\n";
        for(auto itr : e.get_team_a_updates())
        {
            result += "\t"+itr.first + ":" + itr.second + "\n";
        }
         result += "team b updates: "+ e.get_team_b_name() + "\n";
        for(auto itr : e.get_team_b_updates())
        {
            result += "\t"+itr.first + ":" + itr.second + "\n";
        }
        result += "description:"+ e.get_discription() + "\n\n";
        events_output.push_back(result);
        result = "";
    }
    return events_output;
    }
    catch(std::exception e)
    {
        std::cout <<"\nfile" + filename+ " doesn't exist!" << std::endl;
        events_output.push_back("error");
        return events_output;
    }
 }


void User::set_disconnect_recipt_id(std::string id)
{
    DisconnectReceiptId = id;
}
std::string User::get_disconnect_recipt_id()
{
    return DisconnectReceiptId;
}

std::string my_replaceALL(std::string str,const std::string & from, const std::string& to)
{
    size_t start_pos = 0;
    while((start_pos = str.find(from,start_pos)) != std::string::npos){
        str.replace(start_pos,from.length(),to);
        start_pos+= to.length();
    }
    return str;
}