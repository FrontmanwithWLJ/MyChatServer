package com.sl.chat.util;

public class Log {
    //日志写入文件
    public static void toFile(String path,String msg){
        String fileName = "runLog.log";
        FileUtil.write(path,fileName,msg,true);
    }
}
