package bgu.spl.net.impl.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        LineMessageEncoderDecoder LED = new LineMessageEncoderDecoder();
        if (args.length == 0) {
            args = new String[]{"localhost", "hello"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket(args[0], 7777);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

            System.out.println("sending message to server");
            byte[] msg = LED.encode("CONNECT\naccept-version:1.2\nlogin:tomer\npasscode:abs\n\n" +'\u0000');
            String newstr = "";
            for (byte b : msg) {
                System.out.println(""+b);
                newstr += ""+b + " ";
            }
            System.out.println(newstr);
            //String nmsg = new String(msg,StandardCharsets.UTF_8); 
            out.write(newstr);
            out.newLine();
            out.flush();

            System.out.println("awaiting response");
            String line = in.readLine();
            System.out.println("message from server: " + line);
        }
    }
}
