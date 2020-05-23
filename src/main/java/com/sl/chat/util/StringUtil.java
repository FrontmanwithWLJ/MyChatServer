package com.sl.chat.util;

public class StringUtil {
    public static boolean isNullOrEmpty(String s){
        if (s==null)return true;
        return "".equals(s);
    }
    public static boolean isNullOrEmpty(String ...strings){
        for (String string : strings) {
            if (isNullOrEmpty(string))return true;
        }
        return false;
    }
}
