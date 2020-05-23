package com.sl.chat.form;

import com.sl.chat.bean.Message;
import com.sl.chat.bean.UserInfo;
import com.sl.chat.renderer.MessageRenderer;

import javax.swing.*;
import java.awt.*;

public class ServerManagerForm {
    private JButton stop;
    private JPanel jPanel;
    private JList<Message> jList;
    private JScrollPane jScrollPanel;
    private static DefaultListModel<Message> model;

    public static void main(String[] args) {
        new ServerManagerForm();
    }

    public ServerManagerForm(){
        init();
    }
    private void init(){
        JFrame frame = new JFrame("ServerManagerForm");
        frame.setContentPane(jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        jList.setCellRenderer(new MessageRenderer());
        model.addElement(new Message(new UserInfo(1,"石浪"),"你好世界",System.currentTimeMillis()));
        model.addElement(new Message(new UserInfo(2,"阿宝"),"你好世界",System.currentTimeMillis()));
        stop.addActionListener((e)->{
            model.addElement(new Message(new UserInfo(-1,"系统消息"),"欢迎大家",System.currentTimeMillis()));
            //form.jList.setModel(model);
        });
        frame.setVisible(true);

    }

    private void createUIComponents() {
        model = new DefaultListModel<Message>();
        jList = new JList<Message>(model);
        jScrollPanel = new JScrollPane(jList);
    }
}
