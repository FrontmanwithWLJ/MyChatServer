package com.sl.chat.thread;

import com.sl.chat.exception.ChatRoomFullException;
import com.sl.chat.manager.ChatManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private final int PORT = 18616;
    private ChatManager manager;

    public ServerThread(ChatManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
//            serverSocket.bind(8,50);
            while (true) {
                if (manager.full()){
                    //房间已满不接受新的加入请求
                    Thread.sleep(2000);
                    continue;
                }
                Socket socket = serverSocket.accept();
                try {
                    manager.addPerson(socket);
                } catch (ChatRoomFullException e) {
                    new Thread(() -> {
                        System.out.println("房间已爆满");
                        OutputStream outputStream = null;
                        try {
                            outputStream = socket.getOutputStream();
                            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                            writer.write("房间已满");
                        } catch (IOException ioException) { }
                        finally {
                            try {
                                if (outputStream != null)
                                    outputStream.close();
                                if (socket != null)
                                    socket.close();
                            } catch (IOException ioException) { }
                        }
                    }).start();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
