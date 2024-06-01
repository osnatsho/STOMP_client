package bgu.spl.net.impl.stomp;

public class TopicManagerSingleton {
    public static TopicManager topicmanager = null;

    public static TopicManager get_topic_manager()
    {
        if(topicmanager == null){
            topicmanager = new TopicManager();
            return topicmanager;
        }
        else
           return topicmanager;
    }

  
}
