package com.sl.chat.bean;

import com.sl.chat.callback.JsonAble;

public class UserInfo implements JsonAble {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toJson() {
        StringBuilder b = new StringBuilder();
        b.append("{\"name\": ").append(name).append("}");
        return b.toString();
    }
}
