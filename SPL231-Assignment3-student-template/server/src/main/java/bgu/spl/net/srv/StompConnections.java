package bgu.spl.net.srv;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import bgu.spl.net.impl.stomp.TopicManagerSingleton;


public class StompConnections<T> implements Connections<T>{

    TreeMap<Integer,ConnectionHandler> connections;
    AtomicInteger running_id;
    public StompConnections() 
    {
        connections = new TreeMap<Integer,ConnectionHandler>();
        running_id = new AtomicInteger(0);
    }    

    @Override
    public void disconnect(int connectionId) {
        connections.remove(connectionId);
        
    }
    public Integer get_current_id()
    {
        return running_id.get();
    }
    public void connect(ConnectionHandler ch)
    {
        connections.put(running_id.incrementAndGet(),ch);
    }

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler con = connections.get(connectionId);
        if(con == null)
            return false;
        else
            con.send(msg);
        return true;
    }
    @Override
    public void send(String channel, T msg) {
        Vector<Entry<Integer, Integer>> list_of_usrs = TopicManagerSingleton.get_topic_manager().get_list_of_users_subscribed_to_topic(channel);
        for (Entry<Integer,ConnectionHandler> iterable_element : connections.entrySet()) {
            for (Entry<Integer,Integer> usr : list_of_usrs)
            if( iterable_element.getKey() == usr.getValue())
                iterable_element.getValue().send(msg);
        }
    }

    public boolean is_id_closed(int connectionId)
    {
        if(connections.get(connectionId) == null)
            return true;
        return false;
    }
    
}
