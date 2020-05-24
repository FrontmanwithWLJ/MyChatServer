package com.sl.chat.thread;

import com.google.gson.Gson;
import com.sl.chat.bean.LinkInfo;
import com.sl.chat.bean.Message;
import com.sl.chat.bean.UserInfo;
import com.sl.chat.callback.ReceiveMessageListen;
import com.sl.chat.util.StringUtil;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 由chatmanager管理，这个类用来管理客户端向服务端发起连接的套接字
 * 并不是处理客户端
 */
public class ClientThread extends Thread {
    private Socket client;
    private BufferedReader reader = null;
    private OutputStreamWriter writer = null;

    //客户端的识别信息
    private UserInfo info;
    //客户端发来的消息
    private Message receiveMsg;
    private ReceiveMessageListen listener = null;
    //密码
    private String password = "";
    private Gson gson = new Gson();


    public ClientThread(int id, String password, Socket socket, ReceiveMessageListen listener) {
        info = new UserInfo();
        info.setId(id);
        this.password = password;
        this.listener = listener;
        client = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            writer = new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //创建就开始运行
        start();
    }

    @Override
    public void run() {
        if (client == null) {
            System.out.println("断开连接");
            return;
        }
        try {
            String t = reader.readLine();
            LinkInfo linkInfo = gson.fromJson(t, LinkInfo.class);
            if (linkInfo != null && password.equals(linkInfo.getPassword())) {//验证密码
                String name = linkInfo.getUsername();
                info = new UserInfo();
                receiveMsg = new Message();
                info.setName(name);
                //将此客户端的基础信息返回
                sendMsg(info.toJson());
                receiveMsg.setSource(info);
                sendMsg(new Message(new UserInfo(-1, "系统消息"), "亲爱的 " + name + " 你好，你现在可以开始聊天了，快和大家打个招呼吧\n提示输入 exit 退出房间哦!").toJson());
                listener.onReceiveMsg(receiveMsg.setMsg("加入聊天室"), false);
                while (true) {
                    //前面验证的过程用json
                    //后面普通消息就直接用字符串
                    String s = reader.readLine();
                    if ("exit".equals(s)) {
                        sendMsg(new Message("Bye-bye!").toJson());
                        listener.onReceiveMsg(receiveMsg.setMsg(""), true);
                        break;
                    }
                    if (!StringUtil.isNullOrEmpty(s))
                        listener.onReceiveMsg(receiveMsg.setMsg(s), false);
                }
            } else {
                sendMsg(new Message(new UserInfo(-1, "系统消息"), "密码错误").toJson());
            }
        } catch (IOException e) {
            e.printStackTrace();
            //手动发送退出指令
            listener.onReceiveMsg(receiveMsg.setMsg(""), true);
        } finally {
            close();
        }
    }

    /**
     * 向客户端发送消息
     *
     * @param msg 当source为空时，是服务器在发消息
     */
    public synchronized void sendMsg(String msg) {
        try {
            if (writer == null || StringUtil.isNullOrEmpty(msg)) return;
            msg += "\n";
            writer.write(msg, 0, msg.length());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getClient() {
        return client;
    }

    public void close() {
        try {
            if (writer != null)
                writer.close();
            if (reader != null)
                reader.close();
            if (client != null)
                client.close();
        } catch (IOException e) {
        }
    }
}
