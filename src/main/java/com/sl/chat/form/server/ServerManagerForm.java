package com.sl.chat.form.server;

import com.sl.chat.bean.ServerConfig;
import com.sl.chat.bean.Message;
import com.sl.chat.callback.ShowMessageCallBack;
import com.sl.chat.renderer.MessageRenderer;
import com.sl.chat.thread.Server;
import com.sl.chat.util.Log;
import com.sl.chat.util.TimeUtil;

import javax.swing.*;

public class ServerManagerForm {
    //结束服务
    private JButton stop;
    private JFrame frame;
    private JPanel jPanel;
    private JList<Message> jList;
    private JScrollPane jScrollPane;
    //上个窗口的实例
    private ServerEntranceForm serverEntranceForm;
    //前一个窗口传递过来的配置信息
    private ServerConfig serverConfig;
    //消息集合
    private DefaultListModel<Message> model;
    //服务器线程实例
    private Server server;
    //显示消息
    private ShowMessageCallBack showMessageCallBack = new ShowMessageCallBack() {
        @Override
        public void show(Message message) {
            if (model != null) {
                model.addElement(message);
                JScrollBar scrollbar = jScrollPane.getVerticalScrollBar();
                scrollbar.setValue(scrollbar.getMaximum());
            }
        }
        @Override
        public void setId(int id) { }
    };

    public ServerManagerForm(ServerEntranceForm entranceForm, ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.serverEntranceForm = entranceForm;
        init();
    }

    private void init() {
        Log.toFile(serverConfig.getLogDir(), "启动服务器：" + TimeUtil.fromLong(0) + "\n");
        frame = new JFrame("聊天管理");
        frame.setContentPane(jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        //设置关闭按钮失效
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        jList.setCellRenderer(new MessageRenderer(-2));

        stop.addActionListener((e) -> {
            stopServer();
            close();
        });
        frame.setVisible(true);
        startServer();
    }

    private void createUIComponents() {
        model = new DefaultListModel<>();
        jList = new JList<>(model);
        jScrollPane = new JScrollPane(jList);
    }

    //启动
    private synchronized void startServer() {
        if (server == null || server.isInterrupted())
            server = new Server(serverConfig, showMessageCallBack);
        server.start();
    }

    //关闭
    private synchronized void stopServer() {
        server.close();
    }

    public void close() {
        Log.toFile(serverConfig.getLogDir(), "关闭服务器：" + TimeUtil.fromLong(0) + "\n");
        //恢复上一个窗口的状态
        serverEntranceForm.onBack();
        //关闭窗口
        frame.dispose();

    }
}
