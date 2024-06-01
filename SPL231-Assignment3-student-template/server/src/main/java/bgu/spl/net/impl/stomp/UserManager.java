package bgu.spl.net.impl.stomp;

import java.util.TreeMap;
import java.util.Vector;



class UserManager
{
    TreeMap<Integer,UserRegister> user_client_connection;
    Vector<UserRegister> user_data_base;
    public UserManager()
    {
        user_client_connection = new TreeMap<Integer,UserRegister>();
        user_data_base = new Vector<UserRegister>();
    }

    public void user_connect(String username,Integer connection_id)
    {
        UserRegister searchuser = null;
        for(UserRegister user : user_data_base)
            if(user.getUserName().equals(username))
                 searchuser = user;
        searchuser.connect();
        user_client_connection.put(connection_id,searchuser);
    }

    public void subscribe_current_user(String topic_name,Integer connection_id)
    {
        UserRegister user = user_client_connection.get(connection_id);
        user.subscribe(topic_name);
    }

    public void user_disconnect(Integer connection_id)
    {
        UserRegister uid = user_client_connection.get(connection_id);
        uid.disconnect();
        user_client_connection.remove(connection_id);
    }

    public boolean user_exists(String username)
    {
        for (UserRegister iterable_element : user_data_base) {
            if(iterable_element.getUserName().equals(username))
            return true;
        }
        return false;
    }

    public boolean is_user_connected(String name)
    {
        for (UserRegister iterable_element : user_data_base) {
            if(iterable_element.getUserName().equals(name))
                return iterable_element.get_connected();
        }
        return false;
    }

    public boolean isPasswordFittingToUser(String username, String password) {
        for (UserRegister iterable_element : user_data_base) {
            if(iterable_element.getUserName().equals(username))
                if(iterable_element.getPassword().equals(password))
                    return true;
                else
                    return false;
        }
        return false;
    }
    public void CreateUser(String username, String password) {
        UserRegister new_user =  new UserRegister(username,password);
        user_data_base.add(new_user);
    }

    public boolean is_user_subscribed_to_topic(String topic_name,Integer ClientID)
    {
        UserRegister fetch_user = user_client_connection.get(ClientID);
        return fetch_user.is_subscribed_to_topic(topic_name);
    }

    public boolean is_client_user_connected(Integer ClientID) {
        UserRegister fetch_user = user_client_connection.get(ClientID);
        if(fetch_user == null)
            return false;
        return true;
    }

    public Vector<String> getSubScribedTopicNames(Integer connection_id) {
        UserRegister user = user_client_connection.get(connection_id);
        return user.getSubscribedTopicList();
    }
}
