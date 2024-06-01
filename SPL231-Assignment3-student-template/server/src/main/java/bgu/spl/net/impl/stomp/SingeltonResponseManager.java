package bgu.spl.net.impl.stomp;

public class SingeltonResponseManager {
    public static ResponseManager responsemanager = null;

    public static ResponseManager get_response_manager()
    {
        if(responsemanager == null){
            responsemanager = new ResponseManager();
            return responsemanager;
        }
        else
           return responsemanager;
    }

  
}
