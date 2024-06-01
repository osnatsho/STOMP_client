package bgu.spl.net.impl.stomp;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import bgu.spl.net.impl.stomp.StompMessage.message_type;

public class ResponseManager {
    TreeMap<Integer,LinkedList<StompMessage>> ClientMesssageQueues;
    Vector<String> commands;
    String recipt_message;
    public ResponseManager()
    {
        ClientMesssageQueues = new TreeMap<Integer,LinkedList<StompMessage>>();
        commands = new Vector<String>();
        recipt_message = "";
    };

    synchronized String checkMessageValidity(StompMessage msg,int connection_id)
    { 
        if(msg.classify_message() == message_type.CONNECT){
            return check_connect_conditions(msg);
        }
        else if(msg.classify_message() == message_type.SEND)
        {
            return check_send_conditions(msg,connection_id);
        }
        else if(msg.classify_message() == message_type.SUBSCRIBE)
        {
            return check_subscribe_conditions(msg,connection_id);
        }
        else if(msg.classify_message() == message_type.UNSUBSCRIBE)
        {
            return check_unsubscribe_conditions(msg,connection_id);
        }
        else if(msg.classify_message() == message_type.DISCONNECT)
        {
            return check_disconnect_conditions(msg);
        }
        else{
            return "Message command is invalid";
        }
    }

    synchronized private String check_disconnect_conditions(StompMessage msg) {
        if(msg.get_raw_message().indexOf("recipt") == -1)
            return "No recipt field in the header!";
        return "true";
    }

    synchronized private String check_unsubscribe_conditions(StompMessage msg,Integer connection_id) {
        String detail = ExtractionUtility.extractDetail(msg.get_headers(), "id");
        if(detail.equals(""))
            return "Bad StompMessage";
        Integer user_id = Integer.valueOf(detail);
        if(!TopicManagerSingleton.get_topic_manager().is_subscribed(user_id))
            return "User not subscribed to topic, unsubscription impossible!";
        
        return "true";
    }

    private String check_subscribe_conditions(StompMessage msg, Integer connection_id) {
        String detail = ExtractionUtility.extractDetail(msg.get_headers(), "id");
        if(detail.equals(""))
            return "Bad StompMessage";

        detail = ExtractionUtility.extractDetail(msg.get_headers(), "destination");
        if(detail.equals(""))
            return "Bad StompMessage";
        Integer user_id = Integer.valueOf(detail);
        if(TopicManagerSingleton.get_topic_manager().is_subscribed(user_id))
            return "User already subscribed to topic!";
        
        return "true";
    }

    synchronized private String check_send_conditions(StompMessage msg,int connection_id) {
        if(!UserManagerSingleton.get_user_manager().is_client_user_connected(connection_id))
                return "User is not connected!";
        
        String detail = ExtractionUtility.extractDetail(msg.get_headers(), "destination");
        if(detail.equals(""))
            return "Bad StompMessage";
        String Destination = detail;
        if(!(UserManagerSingleton.get_user_manager().is_user_subscribed_to_topic(Destination, connection_id)))
            return "User not subscribed to topic!";
        return "true";
    }
    
    synchronized private String check_connect_conditions(StompMessage msg) {
        String username = ExtractionUtility.extractDetail(msg.get_headers(),"login");
        String password = ExtractionUtility.extractDetail(msg.get_headers(),"passcode");
        String accept_version = ExtractionUtility.extractDetail(msg.get_headers(),"accept-version");
        String host = ExtractionUtility.extractDetail(msg.get_headers(),"host");
        if(username.equals("") || password.equals("") || accept_version.equals("") || host.equals(""))
            return "Bad StompMessage";
        if(!(UserManagerSingleton.get_user_manager().user_exists(username))){
            UserManagerSingleton.get_user_manager().CreateUser(username,password);
            return "true";
        }

        if(UserManagerSingleton.get_user_manager().is_user_connected(username))
            return "User already logged in";

        if(!(UserManagerSingleton.get_user_manager().isPasswordFittingToUser(username,password)))
            return "Wrong password";
        
        return "true";
    }

    synchronized public String GetClientMessage(Long client_id)
    {
        return ClientMesssageQueues.get(client_id).pop().get_raw_message();
    }
    synchronized public LinkedList<StompMessage> GetClientMessages(Long client_id)
    {
        return ClientMesssageQueues.get(client_id);
    }

    synchronized public void generateResponse(StompMessage msg,int connection_id)
    {
        LinkedList<StompMessage> working_queue = null;
        LinkedList<StompMessage> does_client_exist = ClientMesssageQueues.get(connection_id);
        if(does_client_exist == null){
            LinkedList<StompMessage> this_queue = new LinkedList<>();
            ClientMesssageQueues.put(connection_id,this_queue);
            working_queue = this_queue;
        }
        else
            working_queue = does_client_exist;
        String outCheckMessage = checkMessageValidity(msg,connection_id);
        if(outCheckMessage != "true"){
            StompMessage Error = StompMessageFactory.create_error_message(""); // create error message instead
            if(ExtractionUtility.doesReciptExistsInMsg(msg.get_raw_message())) 
                Error.addHeaderlines("recipt-id:"+ExtractionUtility.extractDetail(msg.get_raw_message(),"recipt"));
            Error.addHeaderlines("message:"+ outCheckMessage);
            Error.addFrameBody("The messsage that generated the Error was:");
            Error.addFrameBody("*************");
            Error.addFrameBody(msg.get_raw_message());
            Error.addFrameBody("*************");
            working_queue.add(Error);
            commands.add("Error");
            for(String topicname : UserManagerSingleton.get_user_manager().getSubScribedTopicNames(connection_id))
                TopicManagerSingleton.get_topic_manager().unsubscribe_user(connection_id);
            UserManagerSingleton.get_user_manager().user_disconnect(connection_id);
            return;
        }

        if(msg.classify_message() == message_type.CONNECT){
            String username = ExtractionUtility.extractDetail(msg.get_raw_message(), "login");
            UserManagerSingleton.get_user_manager().user_connect(username,connection_id);
            StompMessage resultMessage = StompMessageFactory.create_connected_message();
            working_queue.add(resultMessage);
            commands.add("self");
        }
        else if(msg.classify_message() == message_type.SEND)
        {
            recipt_message = "report works!";
            String topic_name = ExtractionUtility.extractDetail(msg.get_raw_message(), "destination");
            TopicManagerSingleton.get_topic_manager().append_topic(topic_name,msg);
            String to_deconstruct = msg.get_raw_message();
            String destination= ExtractionUtility.extractDetail(to_deconstruct, "destination") ;
            String username = String.valueOf(TopicManagerSingleton.get_topic_manager().get_user_id_via_connection_id_from_topic(destination,connection_id));
            String splitmsg[] = to_deconstruct.split("\n");
            int i = 1;
            while(i < splitmsg.length && !splitmsg[i].equals(""))
            {
                i++;
            }
            String body = "";
            while(i < splitmsg.length && !splitmsg[i].equals("\u0000"))
            {
                body += splitmsg[i]+"\n";
                i++;
            }
            StompMessage resultMessage = StompMessageFactory.create_message_message(username, destination, body);
            working_queue.add(resultMessage);
            commands.add(destination);
        }
        else if(msg.classify_message() == message_type.SUBSCRIBE)
        {
            String topic_name = ExtractionUtility.extractDetail(msg.get_raw_message(), "destination");
            recipt_message = "Joined channel " + topic_name;
            Integer subscription_id = Integer.valueOf(ExtractionUtility.extractDetail(msg.get_raw_message(), "id"));
            TopicManagerSingleton.get_topic_manager().subscribe(topic_name,subscription_id,connection_id);
            UserManagerSingleton.get_user_manager().subscribe_current_user(topic_name,connection_id);
        }
        else if(msg.classify_message() == message_type.UNSUBSCRIBE)
        {
            Integer user_id = Integer.valueOf(ExtractionUtility.extractDetail(msg.get_raw_message(), "id"));
            String topic_name = TopicManagerSingleton.get_topic_manager().unsubscribe(user_id);
            recipt_message = "Exited channel " + topic_name;
            //commands.add("self");
        }
        else // DISCONNECT
        {
            TopicManagerSingleton.get_topic_manager().unsubscribe_user(connection_id);
            UserManagerSingleton.get_user_manager().user_disconnect(connection_id);
            commands.add("disconnect");
        }  
        
        if(msg.get_raw_message().indexOf("recipt") != -1){
            String recipt_id = ExtractionUtility.extractDetail(msg.get_raw_message(), "recipt");
            StompMessage resultMessage = StompMessageFactory.create_receipt_message(recipt_id,recipt_message);
            recipt_message = "";
            try{
            working_queue.add(resultMessage);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
            commands.add("self");
        }
    }

    public String send_to_channel() {
        if(commands.isEmpty())
            return "empty";
        return commands.remove(0);
    }

    public Object get_queued_message(int connection_id) {
        LinkedList<StompMessage> messageQueue = ClientMesssageQueues.get(connection_id);
        if(messageQueue.isEmpty())
            return null;
        return messageQueue.pop().get_raw_message();
    }
}
