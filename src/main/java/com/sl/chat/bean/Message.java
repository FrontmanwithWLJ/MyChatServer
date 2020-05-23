package com.sl.chat.bean;

import com.sl.chat.json.JsonAble;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message extends JsonAble {
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
    public Message(){}
    public Message(String msg){
        this(new UserInfo(-1,"系统消息"),msg);
    }
    public Message(UserInfo source,String msg){
        this(source,msg,System.currentTimeMillis());
    }
    public Message(UserInfo source){
        this(source,"",System.currentTimeMillis());
    }

    public Message(UserInfo source,String msg,Long time) {
        this.source = source;
        this.msg = msg;
        this.time = time;
    }

    public UserInfo getSource() {
        return source;
    }

    public void setSource(UserInfo source) {
        this.source = source;
    }

    public String getMsg() {
        return msg;
    }

    //更新消息时，时间也跟随刷新
    public Message setMsg(String msg) {
        this.msg = msg;
        this.time = System.currentTimeMillis();
        return this;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getHeader(){
        return source.getName() + "\t" + fromLong(time);
    }

    public String fromLong(long t){
        Date date = new Date(t);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return simpleDateFormat.format(date);
    }
//    @Override
//    public String toJson() {
//        StringBuilder b = new StringBuilder();
//        b.append("{\"msg\":\"").append(msg).append("\",\"date\":\"").append(time).append("\",\"source\":").append(source.toJson()).append("}");
//        return b.toString();
//    }
}
