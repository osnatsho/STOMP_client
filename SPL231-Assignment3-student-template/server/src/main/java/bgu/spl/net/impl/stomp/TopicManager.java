package bgu.spl.net.impl.stomp;

import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

public class TopicManager {
    Vector<Topic> topic_list;
    TreeMap<Integer,String> user_topic;

    public TopicManager()
    {
        topic_list = new Vector<Topic>();
        user_topic = new TreeMap<Integer,String>();
    }

    synchronized public void append_topic(String topic_name, StompMessage body)
    {
        for(Topic tp : topic_list)
        if(tp.getName().equals(topic_name))
            tp.append_topic(body);
    }
    synchronized public boolean is_subscribed(Integer client_id)
    {
        String topic = user_topic.get(client_id);
        if(topic != null)
        for (Topic tp : topic_list) {
            if(tp.getName().equals(topic))
                return tp.is_user_subscribed(client_id);
        }
        return false;
    }
    synchronized public boolean subscribe(String topic_name,Integer subscription_id,Integer client_id)
    {
        Topic t = null;
        for (Topic iterable_element : topic_list) {
            if(iterable_element.getName().equals(topic_name))
                t = iterable_element;
        }
        if(t == null)
        {
            t = new Topic(topic_name);
            topic_list.add(t);
        }
        user_topic.put(subscription_id,topic_name);
        return t.subscribe(subscription_id,client_id);
    }
    synchronized public void unsubscribe_user(Integer connection_id)
    {
        for (Topic iterable_element : topic_list) {
            iterable_element.unsubscribe_connection(connection_id);
        }
    }
    synchronized public String unsubscribe(Integer user_id)
    {
        String topic_name = user_topic.get(user_id);
        Topic t = null;
        for (Topic iterable_element : topic_list) {
            if(iterable_element.getName().equals(topic_name))
                t = iterable_element;
        }
        if(t == null)
        {
            return "false";
        }
        user_topic.remove(user_id);
        boolean res = t.unsubscribe(user_id);
        if(res)
            return topic_name;
        else
            return "false";
    }

    synchronized public Vector<Entry<Integer,Integer>> get_list_of_users_subscribed_to_topic(String topic_name)
    {
        Topic t = null;
        for (Topic iterable_element : topic_list) {
            if(iterable_element.getName().equals(topic_name))
                t = iterable_element;
        }
        if(t == null)
        {
            return null;
        }
        return t.get_subscribed_vector();
    }

    synchronized public Integer get_user_id_via_connection_id_from_topic(String topic,Integer connection_id)
    {
        for (Topic iterable_element : topic_list) {
            if(iterable_element.getName().equals(topic))
                return iterable_element.get_user_id_via_connection_id(connection_id);
        }
        return null;
    }
}
