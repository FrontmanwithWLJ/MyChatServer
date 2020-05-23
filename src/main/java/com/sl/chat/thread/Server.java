package com.sl.chat.thread;

import com.sl.chat.bean.Config;
import com.sl.chat.exception.ChatRoomFullException;
import com.sl.chat.form.ServerEntranceForm;
import com.sl.chat.manager.ChatManager;
import com.sl.chat.manager.ThreadPoolManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private int port;
    private final ChatManager manager;
    private Config config;

    public Server(ChatManager manager, int port,Config config) {
        this.manager = manager;
        this.port = port;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
//            serverSocket.bind(8,50);
            while (true) {
                if (manager.full()) {
                    //房间已满不接受新的加入请求
                    Thread.sleep(2000);
                    continue;
                }
                Socket socket = serverSocket.accept();
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
        }
    }
}
