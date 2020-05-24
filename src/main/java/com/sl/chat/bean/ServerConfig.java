package com.sl.chat.bean;

import com.sl.chat.json.JsonAble;

/**
 * 服务端配置信息
 */
public class ServerConfig extends JsonAble {
    private String logDir;
    private int port;
    private String password;

    public ServerConfig(){}
    public ServerConfig(String logDir, String password, int port){
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
