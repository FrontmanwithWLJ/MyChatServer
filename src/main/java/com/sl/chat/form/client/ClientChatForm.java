package com.sl.chat.form.client;

import com.sl.chat.bean.Message;
import com.sl.chat.callback.ShowMessageCallBack;

import javax.swing.*;

public class ClientChatForm {
    private JList jList;
    private JTextField textBox;
    private JButton close;
    private JButton send;
    private JPanel jPanel;
    private JScrollPane jScrollPane;

    //消息集合
    private DefaultListModel<Message> model;
    private ShowMessageCallBack callBack = message -> {
        if (model != null)
            model.addElement(message);
    };



    private void createUIComponents() {
        model = new DefaultListModel<>();
        jList = new JList<>(model);
        jScrollPane = new JScrollPane(jList);
    }
}
