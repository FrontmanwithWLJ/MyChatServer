package com.sl.chat.form.client;

import com.google.gson.Gson;
import com.sl.chat.bean.ClientConfig;
import com.sl.chat.util.FileUtil;

import javax.swing.*;
import javax.swing.text.InternationalFormatter;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static java.awt.Cursor.E_RESIZE_CURSOR;

public class ClientEntranceForm {
    private JFrame frame;
    private JTextField ipAddr;
    private JTextField username;
    private JFormattedTextField port;
    private JButton run;
    private JLabel msg;
    private JPasswordField password;
    private JPanel jPanel;
    //配置信息
    private ClientConfig clientConfig;

    public ClientEntranceForm(){
        init();
    }

    private void init(){
        frame = new JFrame("ClientEntranceForm");
        frame.setContentPane(new ClientEntranceForm().jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        //运行
        run.addActionListener(e->{
            saveConfig();
            runClient();
        });


        //加载信息
        loadConfig();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        NumberFormat numberFormat = DecimalFormat.getNumberInstance();
        //限制整数位数 0-5 如果设置4-5这个交互很傻逼,
        //numberFormat.setMinimumIntegerDigits(0);
        numberFormat.setMaximumIntegerDigits(5);
        //取消逗号分组
        numberFormat.setGroupingUsed(false);
        InternationalFormatter internationalFormatter = new InternationalFormatter(numberFormat);
        //禁止非法输入 字符之类的
        internationalFormatter.setAllowsInvalid(false);
//        internationalFormatter.setMinimum(1024);
//        internationalFormatter.setMaximum(49151);
        port = new JFormattedTextField(internationalFormatter);
        port.setCursor(new Cursor(E_RESIZE_CURSOR));
    }

    private void loadConfig(){
        String json = FileUtil.read(new File("").getAbsolutePath(),"MyChatClientConfig.json");
        try {
            clientConfig = new Gson().fromJson(json, ClientConfig.class);
        }catch (Exception e){e.printStackTrace();}
        if (clientConfig!=null){
            port.setText(String.valueOf(clientConfig.getPort()));
            username.setText(clientConfig.getUsername());
            password.setText(clientConfig.getPassword());
            ipAddr.setText(clientConfig.getIpAddr());
        }
    }
    private void saveConfig(){
        if (clientConfig == null)
            clientConfig = new ClientConfig();
        String pwd = password.getText();
        String tmp = port.getText();
        if ("".equals(tmp))return;
        int portNum = Integer.parseInt(port.getText());
        String usernameText = username.getText();
        String ip = ipAddr.getText();

        clientConfig.setIpAddr(ip);
        clientConfig.setPassword(pwd);
        clientConfig.setUsername(usernameText);
        clientConfig.setPort(portNum);
        FileUtil.write(new File("").getAbsolutePath(),"MyChatClientConfig.json",clientConfig.toJson(),false);
    }

    /**
     * 启动客户端
     */
    private void runClient(){
        //隐藏当前窗口，打开其他窗口
        frame.setVisible(false);
    }


    public void onBack(){
        frame.setVisible(true);
    }
}
