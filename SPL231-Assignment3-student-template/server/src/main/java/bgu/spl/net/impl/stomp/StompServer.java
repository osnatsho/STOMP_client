package bgu.spl.net.impl.stomp;
import bgu.spl.net.srv.Server;

public class StompServer {
    //mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.stomp.StompServer"-Dexec.args="<port> tpc"
    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        String serverType = args[1];
        switch (serverType){
            case("tpc"):{
                System.out.println("tpc works");
                Server.threadPerClient(
                        port,
                        () -> new ImplementationStompMessagingProtocol(), //protocol factory
                        () -> new StompEncoderDecoder() //stomp encoder decoder factory
                ).serve();
                break;
            }
            case("reactor"):
            {
                System.out.println("reactor works");
                Server.reactor(
                        Runtime.getRuntime().availableProcessors(),
                        port,
                        () -> new ImplementationStompMessagingProtocol(), //protocol factory
                        () -> new StompEncoderDecoder() //stomp encoder decoder factory
                ).serve();
                break;
            }
        }
    }
}