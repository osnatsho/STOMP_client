package bgu.spl.net.api;

import bgu.spl.net.srv.Connections;

public interface MessagingProtocol<T> {
 
    /**
     * process the given message 
     * @param msg the received message
     * @return the response to send or null if no response is expected by the client
     */
    void process(T msg);
    

    //might need
    void start(int connectionId, Connections<T> connections);
    //
    
    /**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
 
}