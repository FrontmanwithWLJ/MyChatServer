package com.sl.chat.form.server;

import com.google.gson.Gson;
import com.sl.chat.bean.ServerConfig;
import com.sl.chat.manager.ThreadPoolManager;
import com.sl.chat.thread.Server;
import com.sl.chat.util.FileUtil;
import com.sl.chat.util.StringUtil;

import javax.swing.*;
import javax.swing.text.InternationalFormatter;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.locks.ReentrantLock;

public class ServerEntranceForm {
    private JFrame frame;
    private JPanel jPanel;
    private JTextField runLog;      //运行日志目录
    private JButton chooseFile;     //选择文件夹按钮
    private JTextField password;    //密码
    private JFormattedTextField port;        //端口号
    private JButton run;            //运行按钮
    private JButton test;           //测试端口是否可用
    private JLabel portMsg;         //回调显示端口信息
    private JFileChooser fileChooser;//选择文件夹
    private ServerConfig serverConfig;          //配置信息
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
        frame = new JFrame("EntranceForm");
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
                    Server.test(portNum, callBack,false);
                });
            }
        });
        run.addActionListener((e) -> {
            portMsg.setText("正在启动中...");
            ThreadPoolManager.getInstance().run(() -> {
                int portNum = getPort();
                if (portNum > 1023 && portNum < 49152) {
                    Server.test(portNum, callBack,true);
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

    /**
     * 在显示窗口前先加载本地数据
     * 从工作目录下的MyChatServerConfig.json读取配置
     */
    private void loadConfig() {
        File dir = new File("");
        String json = FileUtil.read(dir.getAbsolutePath(), "MyChatServerConfig.json");
        if ("".equals(json)) return;
        Gson gson = new Gson();
        try {
            serverConfig = gson.fromJson(json, ServerConfig.class);
        }catch (Exception e){e.printStackTrace();}
        if (serverConfig == null) return;
        runLog.setText(serverConfig.getLogDir());
        password.setText(serverConfig.getPassword());
        port.setText(String.valueOf(serverConfig.getPort()));
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
        if (serverConfig == null)
            serverConfig = new ServerConfig(logDir,pwd, port);
        else {
            serverConfig.setLogDir(logDir);
            serverConfig.setPassword(pwd);
            serverConfig.setPort(port);
        }
        ThreadPoolManager.getInstance().run(() -> {
            FileUtil.write(new File("").getAbsolutePath(), "MyChatServerConfig.json", serverConfig.toJson(), false);
            fileLock.unlock();
        });
    }

    private void runServer() {
        if (StringUtil.isNullOrEmpty(runLog.getText(),port.getText(),password.getText())){
            portMsg.setText("运行参数是必要的请补充完整");
            return;
        }
        saveConfig();
        //隐藏当前窗口
        frame.setVisible(false);
        ServerManagerForm serverManagerForm = new ServerManagerForm(this, serverConfig);
    }

    public void onBack(){
        frame.setVisible(true);
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
    }

    public interface TestPort {
        void finish(boolean enable, String target,boolean jump);
    }
}
