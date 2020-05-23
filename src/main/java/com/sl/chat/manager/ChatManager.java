package com.sl.chat.manager;

import com.sl.chat.bean.Message;
import com.sl.chat.bean.UserInfo;
import com.sl.chat.exception.ChatRoomFullException;
import com.sl.chat.callback.ReceiveMessageListen;
import com.sl.chat.thread.ClientThread;

import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * IoDH 单例模式
 */
public class ChatManager implements ReceiveMessageListen {
    //聊天人数
    private int count = 50;
    //每个线程都是一个客户端
    private ArrayList<ClientThread> personList = new ArrayList<>();
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2,
            5,
            5000,
            TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(46));

    private ChatManager() {
        this(50);
    }

    private ChatManager(int count) {
        this.count = count;
    }

    public static ChatManager getInstance() {
        return HolderClass.instance;
    }

    public void addPerson(Socket socket) throws ChatRoomFullException {
        if (full()) {
            throw new ChatRoomFullException();
        }
        personList.add(new ClientThread(personList.size(), socket, this));
    }

    public Boolean full() {
        return personList.size() == count;
    }

    //ClientThread 自身调用 客户端断开连接时
    public void delPerson(int id,String name) {
        personList.remove(id);
        System.out.println(id + "号客户端" + name + "断开连接\n");
    }

//    @Override
//    public void onReceiveMsg(int id, String name, final String msg, boolean exit) {
//        threadPool.execute(() -> {
//            String msgTmp = msg;
//            //退出指令
//            if (exit) {
//                msgTmp = "退出房间\n";
//                //释放资源
//                delPerson(id,name);
//            }
//            System.out.println(name + ":" + msgTmp);
//            //不发送空消息
//            if ("".equals(msgTmp))return;
//            for (int i = 0; i < personList.size(); i++) {
//                if (i != id) personList.get(i).sendMsg("*-* "+name + ":" + msgTmp + "\n");
//            }
//        });
//    }

    //负责向其他的客户端发送消息
    @Override
    public void onReceiveMsg(int id, Message msg, boolean exit) {
        threadPool.execute(() -> {
            String msgTmp = msg.getMsg();
            UserInfo userInfo = msg.getName();
            //退出指令
            if (exit) {
                msgTmp = "退出房间\n";
                //释放资源
                delPerson(id,userInfo.getName());
            }
            System.out.println(userInfo.getName() + ":" + msgTmp);
            //不发送空消息
            if ("".equals(msgTmp))return;
            for (int i = 0; i < personList.size(); i++) {
                if (i != id) personList.get(i).sendMsg("*-* "+userInfo.getName() + ":" + msgTmp + "\n");
            }
        });
    }

    /**
     * HolderClass 没有作为静态的成员变量直接实例化，
     * 当调用getInstance的时候第一次加载HolderClass
     * 会初始化静态成员instance，由java虚拟机保证其线程安全
     */
    private static class HolderClass {
        public final static ChatManager instance = new ChatManager();
    }
}