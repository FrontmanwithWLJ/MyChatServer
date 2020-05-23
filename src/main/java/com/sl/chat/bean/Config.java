package com.sl.chat.bean;

import com.sl.chat.json.JsonAble;

/**
 * 程序配置信息
 */
public class Config extends JsonAble {
    private String logDir;
    private int port;
    private String password;

    public Config(){}
    public Config(String logDir,String password,int port){
        this.logDir= logDir;
        this.password = password;
        this.port = port;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
