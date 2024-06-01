#include "../include/ResponseManager.hpp"


std::vector<std::string> ResponseManager::split(std::string input,std::string delimiter)
{
    std::vector<std::string> output;
    auto start_token = 0;
    auto end_token = 0;
    while(end_token != std::string::npos)
    {
        end_token = input.find(delimiter,start_token);
        output.push_back(input.substr(start_token,end_token-start_token));
        start_token = end_token + delimiter.length();
    }
    
    return output;
}

bool ResponseManager::is_empty(std:: string inp)
{if(inp.compare("")==0) return true;
return false;}

std::vector<std::string> ResponseManager::parseInput(std::string input_string,StateControler *sc,User **connected_user)
{
    std::vector<std::string> split_input = split(input_string," ");

    split_input.erase(std::remove_if(split_input.begin(),split_input.end(),ResponseManager::is_empty),split_input.end());
    
    if(split_input[0].compare("login") == 0)
    {   
        std::vector<std::string> stomp_messages;
		*connected_user = new User(split_input[2]);
        std::string st = split_input[1];
        std::vector<std::string> host_port = split(st,":");
        if(!host_format_check(host_port[0])){
            stomp_messages.push_back( std::string("bad ip inserted"));
            return stomp_messages;
        }
        if(!port_format_check(host_port[1])){
            stomp_messages.push_back( std::string("bad port inserted"));
            return stomp_messages;
        }
        set_host(host_port[0]);
        set_port(std::stoi(host_port[1]));
        stomp_messages.push_back(MessageFactory::create_connect(accept_version,host_name,split_input[2],split_input[3]));
        return stomp_messages; 
    }
    else if(split_input[0].compare("join") == 0)
    {
        std::vector<std::string> stomp_messages;
        if(split_input.size() != 2){
            stomp_messages.push_back( std::string("there must be just one argument in join command!"));
            return stomp_messages;
        }
       
        std::srand(std::time(nullptr));
        auto x = std::rand();
        std::stringstream ss;
        ss << x;
        (*connected_user)->subscribeTopic(split_input[1], ss.str());
        sc->more_waiting_message();
        stomp_messages.push_back(MessageFactory::create_subscribe(split_input[1],ss.str()));
        return stomp_messages; 
    }
    else if(split_input[0].compare("report") == 0)
    {
        chdir("..");
        chdir("SPL231-Assignment3-student-template");
        chdir("client");
        chdir("data");
        std::vector<std::string> stomp_messages;
        std::vector<std::string> game_report_vec = (*connected_user)->report_game(split_input[1]);
        for(std::string game_report : game_report_vec){
            if(game_report == "error"){
                stomp_messages.push_back("Invalid command");
                return stomp_messages;
            }
            int token = game_report.find("team a:");
            token += 7;
            int end_token = game_report.find("\n",token);
            std::string team_a = game_report.substr(token,end_token-token);
            token = game_report.find("team b:");
            token += 7;
            end_token = game_report.find("\n",token);
            std::string team_b = game_report.substr(token,end_token-token);
            std::string topic_name = team_a +"_"+ team_b;
            sc->more_waiting_message();
            sc->more_waiting_message();
            stomp_messages.push_back(MessageFactory::create_send_message(topic_name,game_report));
        }
        return stomp_messages;
    }
    else if(split_input[0].compare("exit") == 0)
    {
        std::vector<std::string> stomp_messages;
        if(split_input.size() != 2){
             stomp_messages.push_back("there must be just one argument in exit command!");
            return stomp_messages;
        }
        
        sc->more_waiting_message();
        std::string subscription_id = (*connected_user)->unsubscribeTopic(split_input[1]);
        stomp_messages.push_back(MessageFactory::create_unsubscribe(subscription_id));
        return stomp_messages; 
    }
    else if(split_input[0].compare("summary") == 0)
    {
        (*connected_user)->summary(split_input[2],split_input[1],split_input[3]);
        std::vector<std::string> stomp_messages;
        stomp_messages.push_back("ok");
        return stomp_messages; 
    }
    else if(split_input[0].compare("logout") == 0)
    {
        std::vector<std::string> stomp_messages;
        if(split_input.size() > 2){
            stomp_messages.push_back("there are too many arguments in disconnect command");
            return stomp_messages;
        }
        sc->set_logout(true);
        sc->more_waiting_message();
        sc->set_waiting_for_logout(true);
        std::cout << "logout successful";
        stomp_messages.push_back(MessageFactory::create_disconnect(*connected_user));
        return stomp_messages; 
    }
    std::vector<std::string> stomp_messages;
    stomp_messages.push_back(std::string("Invalid command"));
    return stomp_messages; 
}
std::string ResponseManager::parseServerMessage(std::string input_string,StateControler *sc,User **connected_user)
{
    std::vector<std::string> split_input = split(input_string,"\n");
    split_input.erase(std::remove_if(split_input.begin(),split_input.end(),ResponseManager::is_empty),split_input.end());
    
    if(split_input[0].compare("CONNECTED") == 0)
    {
        std::cout<<"Login successful" << std::endl;
    }
    else if(split_input[0].compare("MESSAGE") == 0)
    {
        std::string time = my_extract(input_string,"time:","\n");
        std::string game_a = my_extract(input_string,"team a:","\n");
        std::string game_b = my_extract(input_string,"team b:","\n");
        std::string game = game_a +"_" + game_b;
        std::string user = my_extract(input_string,"user:","\n");
        bool halftime = (std::stoi(time) > 2700) ? true : false;

        std::map<std::string,std::string> game_updates;
        std::map<std::string,std::string> team_a_updates;
        std::map<std::string,std::string> team_b_updates;

        std::string clean_updates;

        std::string general_updates = my_extract(input_string,"general game updates:\n","team a");
        if(general_updates.compare("")){
            std::vector<std::string> game_updates_vec = split(general_updates,"\n");
            for (std::string str : game_updates_vec)
            {
                if(str.compare("")==0)
                    continue;
                std::vector<std::string> key_val = split(str,":");
                clean_updates = key_val[0].substr(1,std::string::npos);
                game_updates.insert(std::make_pair(clean_updates,key_val[1]));
            }
        }
        std::string a_updates = my_extract(input_string,"team a updates: " + game_a + "\n" ,"team b");
        if(a_updates.compare("")){
            std::vector<std::string> a_updates_vec = split(a_updates,"\n");
            for (std::string str : a_updates_vec)
            {
                if(str.compare("")==0)
                    continue;
                std::vector<std::string> key_val = split(str,":");
                clean_updates = key_val[0].substr(1,std::string::npos);
                team_a_updates.insert(std::make_pair(clean_updates,key_val[1]));
            }
        }
        
        std::string b_updates = my_extract(input_string,"team b updates: " + game_b + "\n","description");
        if(b_updates.compare("")){
            std::vector<std::string> b_updates_vec = split(b_updates,"\n");
            for (std::string str : b_updates_vec)
            {
                if(str.compare("")==0)
                    continue;
                std::vector<std::string> key_val = split(str,":");
                clean_updates = key_val[0].substr(1,std::string::npos);
                team_b_updates.insert(std::make_pair(clean_updates,key_val[1]));
            }
        }
        std::string description = my_extract(input_string,"description:","\0");
        int itime = std::stoi(time);
        std::string event_name = my_extract(input_string,"event name:","\n");
        Event *event = new Event(game_a,game_b,event_name,itime,game_updates,team_a_updates,team_b_updates,description);
    
        (*connected_user)->update_game_from_user(user,game,event);
    }
    else if(split_input[0].compare("ERROR") == 0)
    {
        int token = input_string.find("message:");
        token += 8;
        int end_token = input_string.find("\n",token);
        std::string the_error = input_string.substr(token,end_token-token);
        if(the_error.compare("User already logged in") == 0)
        {
            std::cout<<"User already logged in"<<std::endl;
        }
        else if(the_error.compare("Wrong password") == 0)
        {
            std::cout<<"Wrong password"<<std::endl;
        }
        else if(the_error.compare("User not subscribed to topic!") == 0)
        {
            std::cout<<"User not subscribed to topic"<<std::endl;
        }
        else if(the_error.compare("User not subscribed to topic, unsubscription impossible!") == 0)
        {
            std::cout<<"User not subscribed to topic"<<std::endl;
        }
        else if(the_error.compare("No recipt field in the header!") == 0)
        {
            std::cout<<"No recipt field in disconnect" <<std::endl;
        }
        else
        {
            std::cout<<"Unrecognied error, please contact server manager" <<std::endl;
        }
        sc->set_logout(true);
        return "error";
    }
    else if(split_input[0].compare("RECIPT") == 0)
    {
        int token = input_string.find("recipt-id:");
        token += 10;
        int end_token = input_string.find("\n",token);
        std::string rec_id = input_string.substr(token,end_token-token);

        token = input_string.find("\n\n");
        token += 2;
        end_token = input_string.find("\n",token);
        std::string output =  input_string.substr(token,end_token-token);
        std::cout << output << std::endl;

        return rec_id;
    }
    else
    {
        std::cout << "Unrecognized message came from server" <<std::endl;
    }
    return "idle";
}


bool ResponseManager::port_format_check(std::string port)
{
    if(port.length() > 5)
        return false;
    return true;
}
bool ResponseManager::host_format_check(std::string host)
{
    std::vector<std::string> check_vector = split(host,".");
    if(check_vector.size() != 4)
        return false;
    for(int i = 0; i < 4; i++){
        try{
            std::stoi(check_vector[i]);
        }
        catch(std::exception e)
        {
            return false;
        }
    }
    return true;
}

void ResponseManager::set_host(std::string ip){
    connection_ip = ip;
}
void ResponseManager::set_port(int port){
    connection_port = port;
}

std::string ResponseManager::get_host(){
    return connection_ip;
}
int ResponseManager::get_port(){
    return connection_port;
}

std::string my_extract(std::string origin,std::string start,std::string end)
{
        int token = origin.find(start);
        token += start.size();
        int end_token = origin.find(end,token);
        if(end.compare("\0") == 0)
                end_token = std::string::npos;
        std::string res = origin.substr(token,end_token-token);
        if( token == end_token)
            return "";
        return res;
}