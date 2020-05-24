package com.sl.chat.form.client;

import com.sl.chat.bean.ClientConfig;
import com.sl.chat.bean.Message;
import com.sl.chat.bean.UserInfo;
import com.sl.chat.callback.ShowMessageCallBack;
import com.sl.chat.renderer.MessageRenderer;
import com.sl.chat.thread.Client;
import com.sl.chat.util.StringUtil;
import javafx.scene.control.ScrollBar;

import javax.swing.*;

public class ClientChatForm {
    private JFrame frame;
    private JList jList;
    private JTextField textBox;
    private JButton close;
    private JButton send;
    private JPanel jPanel;
    private JScrollPane jScrollPane;
    //消息渲染
    private MessageRenderer renderer;
    //客户端线程
    private Client client;
    //上个窗口
    private ClientEntranceForm clientEntranceForm;
    //配置信息
    private ClientConfig config;
    //消息集合
    private DefaultListModel<Message> model;
    private ShowMessageCallBack callBack = new ShowMessageCallBack() {
        @Override
        public void show(Message message) {
            if (model!=null) {
                model.addElement(message);
                JScrollBar scrollbar = jScrollPane.getVerticalScrollBar();
                scrollbar.setValue(scrollbar.getMaximum());
            }
        }

        @Override
        public void setId(int id) {
            if (renderer != null)
                renderer.setId(id);
        }
    };

    public ClientChatForm(ClientEntranceForm clientEntranceForm, ClientConfig config){
        this.clientEntranceForm = clientEntranceForm;
        this.config = config;
        init();
    }

    private void init(){
        frame = new JFrame("聊天窗口");
        frame.setContentPane(jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();
        //初始化套接字，此时已经开始运行了
        client = new Client(config,callBack);
        renderer = new MessageRenderer(-2);
        jList.setCellRenderer(renderer);

        textBox.addActionListener(e->{
            send.doClick();
        });

        //发送消息
        send.addActionListener(e->{
            String msg = textBox.getText();
            if (!StringUtil.isNullOrEmpty(msg))//空消息不发送
                client.sendMsg(msg,true);
            textBox.setText("");
        });
        close.addActionListener(e->{
            client.exit();
            close();
        });

        frame.setVisible(true);
    }

    private void createUIComponents() {
        model = new DefaultListModel<>();
        jList = new JList<>(model);
        jScrollPane = new JScrollPane(jList);
    }

    private void close(){
        clientEntranceForm.setMsg("断开连接");
        clientEntranceForm.onBack();
        frame.dispose();
    }
}
