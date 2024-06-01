package bgu.spl.net.srv;

public class ConnectionsSingelton {
    public static StompConnections connections = null;

    public static StompConnections get_connections_instance()
    {
        if(connections == null){
            connections = new StompConnections();
            return connections;
        }
        else
           return connections;
    }

  
}
