package com.sl.chat.bean;

import com.sl.chat.json.JsonAble;

public class UserInfo extends JsonAble {
    //用户登录先后的序号,-1为系统
    private int id;
    private String name;

    public UserInfo(){}
    public UserInfo(int id,String name){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    @Override
//    public String toJson() {
//        StringBuilder b = new StringBuilder();
//        b.append("{\"name\": ").append(name).append("}");
//        return b.toString();
//    }
}
