package bgu.spl.net.impl.stomp;

public class ExtractionUtility {
    public static String extractDetail(String get_raw_message, String string) {
        int token_index = get_raw_message.indexOf(string);
        if(token_index == -1)
            return "";
        token_index+=1;
        token_index += string.length();
        int end_token_index = get_raw_message.indexOf('\n',token_index);
        return get_raw_message.substring(token_index,end_token_index);
    }

    public static String extractHeaderLine(String get_raw_message,String keyword)
    {
        int token_index = get_raw_message.indexOf(keyword + ":");
        int end_token_index = get_raw_message.indexOf('\n',token_index);
        return get_raw_message.substring(token_index,end_token_index);
    }

    public static boolean doesReciptExistsInMsg(String get_raw_message) {
        int token_index = get_raw_message.indexOf("recipt" + ":");
        if(token_index != -1)
            return true;
        return false;
    }
}
