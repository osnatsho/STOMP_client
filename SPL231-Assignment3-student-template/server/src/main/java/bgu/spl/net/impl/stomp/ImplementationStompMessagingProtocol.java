package bgu.spl.net.impl.stomp;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.StompConnections;


public class ImplementationStompMessagingProtocol<msg> implements MessagingProtocol<msg>{

    StompConnections connections;
    int connection_id;
    @Override
    public boolean shouldTerminate() {
        return connections.is_id_closed(connection_id);
    }

    public void start(int connectionId, Connections<msg> connections) {
        this.connections = (StompConnections) connections;
        this.connection_id = connectionId;
    }

    @Override
    public void process(msg message) {
        StompMessage new_msg = StompMessageFactory.proccess_message((String)message);
        SingeltonResponseManager.get_response_manager().generateResponse(new_msg,connection_id);
        String response = "";
        boolean disconnect = false;
        while(!response.equals("empty")){
            response = SingeltonResponseManager.get_response_manager().send_to_channel();
            if(response.equals("self")){
                System.out.println((String)message);
                this.connections.send(connection_id, SingeltonResponseManager.get_response_manager().get_queued_message(connection_id));
            }
            else if(response.equals("disconnect"))
            {
                disconnect = true;
            }
            else if(response.equals("empty"))
            {
                continue;
            }
            else if(response.equals( "Error"))
            {
                this.connections.send(connection_id, SingeltonResponseManager.get_response_manager().get_queued_message(connection_id));
                disconnect = true;
            }
            else{
                this.connections.send(response, SingeltonResponseManager.get_response_manager().get_queued_message(connection_id));
            }
        }
        if(disconnect){
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.connections.disconnect(connection_id);
        }
    }

   
}