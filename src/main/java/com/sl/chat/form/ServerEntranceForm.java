package com.sl.chat.form;

import com.google.gson.Gson;
import com.sl.chat.bean.Config;
import com.sl.chat.manager.ThreadPoolManager;
import com.sl.chat.thread.Server;
import com.sl.chat.util.FileUtil;
import com.sl.chat.util.StringUtil;

import javax.swing.*;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.concurrent.locks.ReentrantLock;

import static java.awt.Cursor.E_RESIZE_CURSOR;

public class ServerEntranceForm {
    private JPanel jPanel;
    private JTextField runLog;      //运行日志目录
    private JButton chooseFile;     //选择文件夹按钮
    private JTextField password;    //密码
    private JFormattedTextField port;        //端口号
    private JButton run;            //运行按钮
    private JButton test;           //测试端口是否可用
    private JLabel portMsg;         //回调显示端口信息
    private JFileChooser fileChooser;//选择文件夹
    private Config config;          //配置信息
    private final ReentrantLock fileLock = new ReentrantLock();//避免运行按钮被重复按下
    //测试端口的回调
    private final TestPort callBack = new TestPort() {
        @Override
        public void finish(boolean enable, String target,boolean jump) {
            portMsg.setText(target);
            if (enable&&jump)
                runServer();
        }
    };

    public ServerEntranceForm() {
        init();
    }

    private void init() {
        //加载配置文件
        loadConfig();
        JFrame frame = new JFrame("EntranceForm");
        frame.setContentPane(jPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        /**
         * runlog目录选择
         */
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooseFile.addActionListener(e -> fileChooser.showOpenDialog(jPanel));
        fileChooser.addActionListener(e -> runLog.setText(fileChooser.getSelectedFile().getAbsolutePath()));

        //测试端口是否可用
        test.addActionListener(e -> {
            int portNum = getPort();
            if (portNum < 1024 || portNum > 49151)
                callBack.finish(false, "端口越界",false);
            else {
                ThreadPoolManager.getInstance().run(() -> {
                    test(portNum, callBack,false);
                });
            }
        });
        run.addActionListener((e) -> {
            portMsg.setText("正在启动中...");
            ThreadPoolManager.getInstance().run(() -> {
                int portNum = getPort();
                if (portNum > 1023 && portNum < 49152) {
                    test(portNum, callBack,true);
                }
            });
        });
        //一系列初始化完毕才显示窗口
        frame.setVisible(true);
    }

    private int getPort() {
        if ("".equals(port.getText())) {
            JOptionPane.showConfirmDialog(jPanel, "请输入端口号", "提示", JOptionPane.YES_NO_OPTION);
            return -1;
        }
        return Integer.parseInt(port.getText());
    }

    public synchronized void test(int port, ServerEntranceForm.TestPort listener,boolean jump) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
            listener.finish(true, "该端口可用",jump);
        } catch (IOException e) {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            listener.finish(false, e.getMessage(),jump);
        }
    }

    /**
     * 在显示窗口前先加载本地数据
     * 从工作目录下的MyChatServerConfig.json读取配置
     */
    private void loadConfig() {
        File dir = new File("");
        String json = FileUtil.read(dir.getAbsolutePath(), "MyChatServerConfig.json");
        if ("".equals(json)) return;
        Gson gson = new Gson();
        Config config = gson.fromJson(json, Config.class);
        if (config == null) return;
        runLog.setText(config.getLogDir());
        password.setText(config.getPassword());
        port.setText(String.valueOf(config.getPort()));
    }

    private void runServer() {
        if (StringUtil.isNullOrEmpty(runLog.getText(),port.getText(),password.getText())){
            portMsg.setText("运行参数是必要的请补充完整");
            return;
        }
        saveConfig();
        //todo jump
    }


    private void saveConfig() {
        //避免二次点击
        if (fileLock.isLocked()) {
            return;
        }
        fileLock.lock();
        String pwd = password.getText();
        String logDir = runLog.getText();
        int port = getPort();
        if (config == null)
            config = new Config(logDir,pwd, port);
        else {
            config.setLogDir(logDir);
            config.setPassword(pwd);
            config.setPort(port);
        }
        ThreadPoolManager.getInstance().run(() -> {
            FileUtil.write(new File("").getAbsolutePath(), "MyChatServerConfig.json", config.toJson(), false);
            fileLock.unlock();
        });
    }

    private void createUIComponents() {
        NumberFormat numberFormat = DecimalFormat.getNumberInstance();
        //限制整数位数 0-5 如果设置4-5这个交互很傻逼,
        numberFormat.setMinimumIntegerDigits(0);
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

    public interface TestPort {
        void finish(boolean enable, String target,boolean jump);
    }
}
