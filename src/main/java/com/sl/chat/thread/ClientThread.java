package com.sl.chat.thread;

import com.sl.chat.bean.Message;
import com.sl.chat.bean.UserInfo;
import com.sl.chat.callback.ReceiveMessageListen;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientThread extends Thread {
    private Socket client;
    private BufferedReader reader = null;
    private OutputStreamWriter writer = null;

    //客户端的识别信息
    private UserInfo info;
    //客户端发来的消息
    private Message receiveMsg;
    private ReceiveMessageListen listener = null;

    public ClientThread(int id, Socket socket, ReceiveMessageListen listener){
        info = new UserInfo();
        info.setId(id);

        client = socket;
        this.listener = listener;
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream(),StandardCharsets.UTF_8));
            writer = new OutputStreamWriter(client.getOutputStream(),StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建就开始运行
        start();
    }
    @Override
    public void run(){
        if (client == null){
            System.out.println("断开连接");
            return;
        }
        sendMsg(new Message("欢迎进入我的聊天室\n请输入昵称:"));
        try {
            String name = reader.readLine();
            info = new UserInfo();
            receiveMsg = new Message();
            info.setName(name);
            receiveMsg.setSource(info);
            sendMsg(new Message("亲爱的 "+name+" 你好，你现在可以开始聊天了，快和大家打个招呼吧\n提示输入 exit 退出房间哦\n"));
            listener.onReceiveMsg(receiveMsg.setMsg( "加入聊天室"),false);
            while (true){
                String s = reader.readLine();
                    if ("exit".equals(s)){
                        sendMsg(new Message("Bye-bye!\n"));
                        listener.onReceiveMsg(receiveMsg.setMsg(""),true);
                        break;
                    }
                    listener.onReceiveMsg(receiveMsg.setMsg(s),false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //手动发送退出指令
            listener.onReceiveMsg(receiveMsg.setMsg(""),true);
        }
        finally {
            close();
        }
    }

    /**
     * 向客户端发送消息
     * @param msg 当source为空时，是服务器在发消息
     */
    public void sendMsg(Message msg){
        try {
            String json = msg.toJson();
            writer.write(json,0,json.length());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getClient() {
        return client;
    }

    private void close(){
        try {
            writer.close();
            reader.close();
            client.close();
            writer = null;
            reader = null;
            client=null;
        } catch (IOException e) { }
    }
}
