package com.sl.chat.thread;

import com.google.gson.Gson;
import com.sl.chat.bean.ClientConfig;
import com.sl.chat.bean.LinkInfo;
import com.sl.chat.bean.Message;
import com.sl.chat.bean.UserInfo;
import com.sl.chat.callback.ShowMessageCallBack;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client extends Thread {
    private UserInfo userInfo;
    private ClientConfig config;
    //向窗口返回消息
    private ShowMessageCallBack showMessageCallBack;
    private Socket socket;
    private BufferedReader reader;
    private OutputStreamWriter writer;
    //标记运行状态
    private boolean target = true;

    public Client(ClientConfig config, ShowMessageCallBack callBack) {
        this.config = config;
        this.showMessageCallBack = callBack;
        try {
            socket = new Socket(config.getIpAddr(), config.getPort());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            callBack.show(new Message(new UserInfo(-1,"系统消息"),"拒绝连接,请核对后重试"));
            return;
        }
        start();
    }

    @Override
    public void run() {
        try {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            String json = new LinkInfo(config.getUsername(),config.getPassword()).toJson();
            //发送密码验证
            sendMsg(json);
            String t = reader.readLine();//等待服务器响应.返回的是一个UserInfo的json
            userInfo = gson.fromJson(t, UserInfo.class);
            //不为空则说明密码正确
            if (userInfo != null) {
                while (target) {
                    StringBuilder tmp = new StringBuilder(reader.readLine());
                    while (!tmp.toString().endsWith("}")){
                        tmp.append(reader.readLine());
                    }
                    try {
                        showMessageCallBack.show(gson.fromJson(tmp.toString(), Message.class));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } else {
                showMessageCallBack.show(new Message(new UserInfo(-1, "系统消息"), "密码错误，请退出当前界面重试", System.currentTimeMillis()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void sendMsg(String msg) {
        try {
            if (writer == null)return;
            msg += "\n";
            writer.write(msg, 0, msg.length());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        sendMsg("exit");
        close();
    }

    public void close() {
        target = false;
        try {
            if (writer != null)
                writer.close();
            if (reader != null)
                reader.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
