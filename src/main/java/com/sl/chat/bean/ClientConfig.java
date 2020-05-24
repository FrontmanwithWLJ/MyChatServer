package com.sl.chat.bean;

import com.sl.chat.json.JsonAble;

/**
 * 客户端配置信息
 */
public class ClientConfig extends JsonAble {
    private int port;
    private String username;
    private String ipAddr;
    private String password;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
