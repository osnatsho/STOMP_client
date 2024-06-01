package bgu.spl.net.impl.stomp;

import java.io.Serializable;

public class StompMessage implements Serializable
{
    public static enum message_type{CONNECT,ERROR,SUBSCRIBE,UNSUBSCRIBE,SEND,DISCONNECT,RECIPT,CONNECTED,MESSAGE}
    private String command ="";
    private String headerlines = "";
    private String framebody = "";
    private message_type m_type = message_type.ERROR;
    public StompMessage()
    {
        ;
    }
    public StompMessage(StompMessage other)
    {
        command = other.command;
        headerlines= other.headerlines;
        framebody = other.framebody;
    }

    // to delete
    public StompMessage(String msg)
    {
        StompMessage tempMessage = StompMessageFactory.proccess_message(msg);
        command = tempMessage.command;
        headerlines= tempMessage.headerlines;
        framebody = tempMessage.framebody;
    }
    public void setHeaderlines(String msg) {
        headerlines = msg + "\n";
    }
    void setCommand(String str)
    {
       command = str;
    }
    void addHeaderlines(String str)
    {
        headerlines+= str + "\n";
    }

    void setFramebody(String str)
    {
        framebody = str +"\n";
    }
    void addFrameBody(String str)
    {
        framebody += str + "\n";
    }

    String get_raw_message()
    {
        return "" + command + "\n" + headerlines + "\n" + framebody + '\u0000';
    }

    String get_headers()
    {
        return headerlines;
    }
    
    public message_type classify_message()
    {
       
        if(command.equals("CONNECT")){
            m_type = message_type.CONNECT;
        }
        else if(command.equals("ERROR")){
            m_type = message_type.ERROR;
        }
        else if(command.equals("SEND")){
            m_type = message_type.SEND;
        }
        else if(command.equals("MESSAGE")){
            m_type = message_type.MESSAGE;
        }
        else if(command.equals("SUBSCRIBE")){
            m_type = message_type.SUBSCRIBE;
        }
        else if(command.equals("UNSUBSCRIBE")){
            m_type = message_type.UNSUBSCRIBE;
        }
        else if(command.equals("DISCONNECT")){
            m_type = message_type.DISCONNECT;
        }
         else if(command.equals("CONNECTED")){
            m_type = message_type.CONNECTED;
        }
        else if(command.equals("RECIPT")){
            m_type = message_type.RECIPT;
        }
        return m_type; 
    }

}