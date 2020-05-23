package com.sl.chat.thread;

import com.sl.chat.callback.ReceiveMessageListen;
import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket client;
    private BufferedReader reader = null;
    private OutputStreamWriter writer = null;

    //客户端的识别信息
    private int id = -1;
    private String name = "";
    private ReceiveMessageListen listener = null;

    public ClientThread(int id, Socket socket, ReceiveMessageListen listener){
        this.id = id;
        client = socket;
        this.listener = listener;
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new OutputStreamWriter(client.getOutputStream());
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
        sendMsg("欢迎进入我的聊天室\n请输入昵称:");
        try {
            while (true){
                String s = reader.readLine();
//                StringBuilder str = new StringBuilder();
//                int ch;
//                str.append(reader.readLine());
//                while ((ch =in.read())!=-1){
//                    str.append((char) ch);
//                }
                if (name.equals("")){
                    name = s;
                    sendMsg("亲爱的 "+name+" 你好，你现在可以开始聊天了，快和大家打个招呼吧\n提示输入 exit 退出房间哦\n");
                    listener.onReceiveMsg(id,name,"加入聊天室",false);
                }else {
                    if ("exit".equals(s) || s == null){
                        sendMsg("Bye-bye!\n");
                        listener.onReceiveMsg(id,name,"",true);
                        break;
                    }
                    listener.onReceiveMsg(id,name,s,false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //手动发送退出指令
            listener.onReceiveMsg(id,name,"",true);
        }
        finally {
            close();
        }
    }

    public void sendMsg(String msg){
        try {
            writer.write(msg,0,msg.length());
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

    @Override
    public String toString(){
        return id+"\t"+name;
    }
}
