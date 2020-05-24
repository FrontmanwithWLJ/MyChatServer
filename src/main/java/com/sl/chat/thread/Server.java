package com.sl.chat.thread;

import com.sl.chat.bean.ServerConfig;
import com.sl.chat.bean.Message;
import com.sl.chat.bean.UserInfo;
import com.sl.chat.callback.ShowMessageCallBack;
import com.sl.chat.exception.ChatRoomFullException;
import com.sl.chat.form.server.ServerEntranceForm;
import com.sl.chat.manager.ChatManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 整个套接字的初始化监听
 */
public class Server extends Thread {
    private final ChatManager manager = ChatManager.getInstance();
    private ServerConfig serverConfig;
    private boolean target = true;

    public Server(ServerConfig serverConfig, ShowMessageCallBack callBack) {
        this.serverConfig = serverConfig;
        manager.setShowMessageCallBack(callBack);
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverConfig.getPort());
            manager.onReceiveMsg(new Message(new UserInfo(-1, "系统消息"), "服务器已开启"), false);
//            serverSocket.bind(8,50);
            while (target) {
                if (manager.full()) {
                    //房间已满不接受新的加入请求
                    Thread.sleep(2000);
                    continue;
                }
                Socket socket = serverSocket.accept();
                if (!target)//手动跳出循环，关闭套接zi
                    break;
                try {
                    manager.addPerson(socket);
                } catch (ChatRoomFullException e) {
                    System.out.println("房间已爆满");
                    OutputStream outputStream = null;
                    try {
                        outputStream = socket.getOutputStream();
                        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                        writer.write("房间已满");
                    } catch (IOException ioException) {
                    } finally {
                        try {
                            if (outputStream != null)
                                outputStream.close();
                            if (socket != null)
                                socket.close();
                        } catch (IOException ioException) {
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            manager.onReceiveMsg(new Message(new UserInfo(-1, "系统消息"), "服务器已关闭"), false);
            //清空聊天室
            manager.removeAll();
            //保证线程被中断也能正常关闭套接字
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //测试端口是否可用
    public synchronized static void test(int port, ServerEntranceForm.TestPort listener, boolean jump) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
            listener.finish(true, "该端口可用", jump);
        } catch (IOException e) {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            listener.finish(false, e.getMessage(), jump);
        }
    }

    public void close() {
        target = false;
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", serverConfig.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        manager.onReceiveMsg(new Message(new UserInfo(-1, "系统消息"), "服务器已关闭"), false);
    }
}
