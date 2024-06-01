package bgu.spl.net.impl.stomp;

import java.util.Vector;

public class UserRegister {
    private boolean connected;
    private String name;
    private String password;
    private Vector<String> subscribed_topic_names;
    public UserRegister(String username,String password)
    {
        this.name = username;
        this.password = password;
        this.connected = true;
        subscribed_topic_names = new Vector<String>();
    }
    public boolean get_connected()
    {
        return connected;
    }

    public void disconnect()
    {
        connected = false;
    }

    public void connect()
    {
        connected = true;
    }
    public String getUserName()
    {
        return name;
    }
    public String getPassword() {
        return password;
    }
    public void subscribe(String topic_name)
    {
        subscribed_topic_names.add(topic_name);
    }
    public boolean is_subscribed_to_topic(String name) {
        for (String iterable_element : subscribed_topic_names) {
            if(iterable_element.equals(name))
                return true;
        }
        return false;
    }
    public Vector<String> getSubscribedTopicList() {
        return subscribed_topic_names;
    }
}
