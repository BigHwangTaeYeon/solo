package videoChat.solo.com.util;

import java.text.SimpleDateFormat;

public class GetDate {
    public static String getCurrentTime(String timeFormat){
        return new SimpleDateFormat(timeFormat).format(System.currentTimeMillis());
    }
}
