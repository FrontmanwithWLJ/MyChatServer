package com.sl.chat;

import com.sl.chat.manager.ChatManager;
import com.sl.chat.thread.ServerThread;

//todo 实现注册接口,可以改个可视化界面,数据写入数据库
//todo 完善本地化日志
//todo 把命令command写完
public class Main {

    public static void main(String[] args) {
	// write your code here
        //由manager统一管理
        //new MainForm();
        System.out.println("正在准备启动服务器");
        ChatManager manager = ChatManager.getInstance();
        System.out.println("服务器管理工具已启动\n正在做最后的准备工作。。。");
        new ServerThread(manager).start();
        System.out.println("服务器开始工作");
        help();
    }

    public static void command(char ch){
        switch (ch){

        }
    }

    public static void help(){
        System.out.println(
                "\n***************" +
                "-h get help"+
                "-l get log"+
                "-L get number list" +
                "-c [integer (1-500)] reset the number max count "
                );
    }
}

