package bgu.spl.net.impl.stomp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class MyEchoClient {
    StompEncoderDecoder SED = new StompEncoderDecoder();
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private Socket sock;
    public MyEchoClient() throws UnknownHostException, IOException
    {
        sock = new Socket("127.0.0.1", 7777);
        in = new BufferedInputStream(sock.getInputStream());
        out = new BufferedOutputStream(sock.getOutputStream());
    }
    public void procces(String[] args) throws IOException {
        
        if (args.length == 0) {
            args = new String[]{"localhost", "hello"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        
        Random rand = new Random();
        System.out.println("sending message to server");
        StompMessage mymsg = new StompMessage();
        mymsg.setCommand("CONNECT");
        mymsg.addHeaderlines("login:tomer"+ rand.nextInt());
        //mymsg.addHeaderlines("accept-version:1.2");
        mymsg.addHeaderlines("passcode:1234");
        StompMessage mymsg2 = new StompMessage();
        mymsg2.setCommand("SUBSCRIBE");
        mymsg2.addHeaderlines("recipt:2");
        mymsg2.addHeaderlines("destination:football");
        mymsg2.addHeaderlines("id:1");
        StompMessage mymsg3 = new StompMessage();
        mymsg3.setCommand("SEND");
        mymsg3.addHeaderlines("recipt:1");
        mymsg3.addHeaderlines("destination:football");
        mymsg3.addFrameBody("Game started!");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("done sleeping");
        //String nmsg = new String(msg,StandardCharsets.UTF_8); 
        out.write(SED.encode(mymsg.get_raw_message()));
        out.flush();
        out.write(SED.encode(mymsg2.get_raw_message()));
        out.flush();
        out.write(SED.encode(mymsg3.get_raw_message()));
        out.flush();

        //System.out.println("awaiting response");
        //String line = in.readLine();
        //System.out.println("message from server: " + line);
        
    }

    public static void main(String args[])
    {
        try {
            MyEchoClient mec = new MyEchoClient();
            mec.procces(args);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
