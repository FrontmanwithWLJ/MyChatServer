package com.sl.chat.bean;

import com.sl.chat.callback.JsonAble;

public class Message implements JsonAble {
    /**
     * 消息来源
     */
    private UserInfo source;
    /**
     * 消息
     */
    private String msg;
    /**
     * 发送时间，客户端时间
     */
    private Long time;

    public UserInfo getName() {
        return source;
    }

    public void setName(UserInfo source) {
        this.source = source;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toJson() {
        StringBuilder b = new StringBuilder();
        b.append("{\"msg\":\"").append(msg).append("\",\"date\":\"").append(time).append("\",\"source\":").append(source.toJson()).append("}");
        return b.toString();
    }
}
