package bgu.spl.net.impl.stomp;

public class StompMessageFactory {
    
    private static StompMessage generated_connected_message = null;
    private static int message_id = 0;
    public static void set_up_connected_message()
    {
        generated_connected_message = new StompMessage();
        generated_connected_message.setCommand("CONNECTED");
        generated_connected_message.setHeaderlines("version:1.2");
    }   
    public static StompMessage create_connected_message()
    {
        set_up_connected_message();
        return generated_connected_message;
    }

    public static StompMessage create_error_message(String outCheckMessage)
    {
        StompMessage error_message = new StompMessage();
        error_message.setCommand("ERROR");
        error_message.setFramebody(outCheckMessage);
        return error_message;
        //return ("ERROR\n"+outCheckMessage+ "\n" + '\u0000');
    }

    public static StompMessage create_message_message(String subscription,String destination,String body)
    {
        StompMessage new_message = new StompMessage();
        new_message.setCommand("MESSAGE");
        new_message.setHeaderlines("subscription:" + subscription);
        new_message.addHeaderlines("message-id:" + message_id);
        new_message.addHeaderlines("destination:"+destination);
        new_message.setFramebody(body);
        message_id++;
        return new_message;
    }
    public static StompMessage create_receipt_message(String recipt_id,String message)
    {
        StompMessage new_message = new StompMessage();
        new_message.setCommand("RECIPT");
        new_message.setHeaderlines("recipt-id:" + recipt_id);
        new_message.addFrameBody(message);
        return new_message;
    }

    public static StompMessage proccess_message(String str)
    {
        StompMessage empty_message = new StompMessage();
        String[] results = str.split("\n");
        empty_message.setCommand(results[0]);
        int i = 1;
        while(i < results.length && !(results[i].equals("")) ){
            empty_message.addHeaderlines(results[i]);
            i++;
        }
        while(i < results.length ){
            empty_message.addFrameBody(results[i]);
            i++;
        }
        return empty_message;
    }
    /*
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */

    private StompMessage generated_connect_message = null;
        
    public void set_up_connect_message(String username,String password)
    {
        generated_connect_message = new StompMessage();
        generated_connect_message.setCommand("CONNECT");
        generated_connect_message.setHeaderlines("accept-version:1.2");
        generated_connect_message.setHeaderlines("stomp.cs.host: bgu.ac.il");
        generated_connect_message.setHeaderlines("login:"+username);
        generated_connect_message.setHeaderlines("passcode:"+password);
    }   
    public StompMessage create_connect_message()
    {
        return generated_connect_message;
    }
    void create_send_message(){

    }

    void create_subscribe_message()
    {

    }
    void create_unsubscribe_message()
    {
        
    }
    public StompMessage create_disconnect_message()
    {
        return generated_connect_message;
    }


    
   
    
}
