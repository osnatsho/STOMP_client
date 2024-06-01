package bgu.spl.net.impl.stomp;

public class UserManagerSingleton {
    public static UserManager usermanager = null;

    public static UserManager get_user_manager()
    {
        if(usermanager == null){
            usermanager = new UserManager();
            return usermanager;
        }
        else
           return usermanager;
    }
}
