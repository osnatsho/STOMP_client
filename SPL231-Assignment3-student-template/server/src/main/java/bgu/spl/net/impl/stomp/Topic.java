package bgu.spl.net.impl.stomp;

import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

class Topic
{
    Queue<StompMessage> topic;
    String name;
    TreeMap<Integer,Integer> user_client_id;
    public Topic(String name)
    {
        this.name = name;
        user_client_id = new TreeMap<Integer,Integer>();
        topic = new LinkedList();
    }
    public String getName()
    {
        return name;
    }

    public Integer get_user_id_via_connection_id(Integer connection_id)
    {
        for(Entry<Integer,Integer> entry :user_client_id.entrySet())
        {
            if(entry.getValue() == connection_id)
                return entry.getKey();
        }
        return null;
    }

    public void append_topic(StompMessage msg)
    {
        topic.add(msg);
    }
    public boolean is_user_subscribed(Integer user_id)
    {
        if(user_client_id.get(user_id) != null) return true;
        return false;
    }
    public boolean subscribe(Integer user_id,Integer client_id)
    {
        Integer is_id_exsits = user_client_id.get(user_id);
        if(is_id_exsits == null)
        {
            user_client_id.put(user_id,client_id);
            return true;
        }
        else{
            return false;
        }
    }
    public Vector<Entry<Integer,Integer>> get_subscribed_vector() {
        Vector<Entry<Integer,Integer>> res = new Vector<Entry<Integer,Integer>>();
        for(Entry<Integer,Integer> entry : user_client_id.entrySet())
            res.add(entry);
        return res;
    }

    public void unsubscribe_connection(Integer connection_id) {
        Vector<Integer>removevec = new Vector<Integer>();
        for(Entry<Integer,Integer> entry:  user_client_id.entrySet())
        {
            if(entry.getValue().equals(connection_id))
            {
                removevec.add(entry.getKey());
            }
        }
        for(Integer in : removevec)
        {
            user_client_id.remove(in);
        }
    }

    public boolean unsubscribe(Integer user_id) {
        Integer is_id_exsits = user_client_id.get(user_id);
        if(is_id_exsits == null)
        {
            return false;
        }
        else{
            user_client_id.remove(is_id_exsits);
            return true;
        }
    }
}