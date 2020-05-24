package com.sl.chat.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    //为0则获取当前时间
    public static String fromLong(long t){
        if (t == 0){
            t = System.currentTimeMillis();
        }
        Date date = new Date(t);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return simpleDateFormat.format(date);
    }
}
