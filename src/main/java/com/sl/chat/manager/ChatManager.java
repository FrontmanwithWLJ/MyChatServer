package com.sl.chat.manager;

import com.sl.chat.bean.ServerConfig;
import com.sl.chat.bean.Message;
import com.sl.chat.bean.UserInfo;
import com.sl.chat.callback.ShowMessageCallBack;
import com.sl.chat.exception.ChatRoomFullException;
import com.sl.chat.callback.ReceiveMessageListen;
import com.sl.chat.thread.ClientThread;
import com.sl.chat.util.Log;
import com.sl.chat.util.TimeUtil;

import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * IoDH 单例模式
 */
public class ChatManager implements ReceiveMessageListen {
    //配置信息
    private ServerConfig serverConfig;
    //聊天人数
    private int count = 50;
    //每个线程都是一个客户端
    private ArrayList<ClientThread> personList = new ArrayList<>();
    //向客户端推送消息
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2,
            5,
            5000,
            TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(46));

    private ShowMessageCallBack showMessageCallBack = null;

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
    public void delPerson(int id, String name) {
        personList.remove(id);
    }

    //清空房间
    public void removeAll() {
        if (serverConfig != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("服务器强制关闭:").append(TimeUtil.fromLong(0)).append("\n\t当前在线人数：").append(personList.size());
            Log.toFile(serverConfig.getLogDir(), stringBuilder.toString());
        }
        personList.forEach(client -> {
            client.close();
            personList.remove(client);
        });
    }

    //负责向其他的客户端发送消息
    @Override
    public void onReceiveMsg(Message msg, boolean exit) {
        threadPool.execute(() -> {
            String msgTmp = msg.getMsg();
            UserInfo userInfo = msg.getSource();
            //退出指令
            if (exit) {
                msgTmp = "退出房间\n";
                //释放资源
                delPerson(userInfo.getId(), userInfo.getName());
                //把退出指令修改成系统消息推送给其他人
                msg.setMsg(msg.getSource().getName() + msgTmp);
                msg.setSource(new UserInfo(-1, "系统消息"));
            }
            //不发送空消息
            if ("".equals(msgTmp)) return;
            if (showMessageCallBack != null)
                showMessageCallBack.show(msg);
            for (int i = 0; i < personList.size(); i++) {
                if (i != userInfo.getId()) personList.get(i).sendMsg(msg);
            }
        });
    }

    public void setShowMessageCallBack(ShowMessageCallBack showMessageCallBack) {
        this.showMessageCallBack = showMessageCallBack;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
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