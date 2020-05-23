package com.sl.chat.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 利用反射实现javabean转json
 * 所有涉及到的javabean全部都需要继承于JsonAble
 * 调用toJson方法即可将javabean转化成json类型的字符串
 * 相同的javabean分别使用反射的方法和手写函数toJson
 * 执行10000次耗时如下: 单位毫秒
 * reflect   time   :   202
 * common    time   :   28
 */
public abstract class JsonAble {
    public String toJson(){
        Class cla;
        try {
            cla = Class.forName(this.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("not found:"+this.getClass().getName());
            return null;
        }
        StringBuilder json = new StringBuilder();
        StringBuilder methodName = new StringBuilder();
        json.append("{");
        //获取该类的所有属性
        Field[] fields = cla.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            json.append("\"");
            json.append(field.getName());
            json.append("\":");

            methodName.append("get");
            //javabean  方法名大写第一个字母
            methodName.append(field.getName().substring(0, 1).toUpperCase());
            methodName.append(field.getName().substring(1));
            Method method = null;
            try {
                method = cla.getMethod(methodName.toString());
            } catch (NoSuchMethodException e) {
                //清空方法字符串
                methodName.setLength(0);
                e.printStackTrace();
            }
            try {
                Object tmp = method.invoke(this);
                if (tmp instanceof String) {
                    json.append("\"").append(tmp.toString()).append("\"");
                }else if (tmp instanceof JsonAble){
                    JsonAble jsonAble = (JsonAble) tmp;
                    json.append(jsonAble.toJson());
                } else if(tmp instanceof ArrayList){
                    json.append(ListToJson((List<JsonAble>) tmp));
                }else if(tmp==null){
                    json.append("null");
                } else {
                    json.append(tmp.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i<(fields.length-1))
                json.append(",");
            methodName.setLength(0);
        }
        json.append("}");
        return json.toString();
    }

    private String ListToJson(List<JsonAble> list) throws Exception {
        StringBuilder b = new StringBuilder();
        if (list == null){
            b.append("null");
            return b.toString();
        }
        b.append("[");
        for (int i = 0; i < list.size(); i++) {
            JsonAble o = list.get(i);
            if (o != null){
                b.append(o.toJson());
                if (i != list.size()-1)b.append(",");
            }else {
                b.setLength(0);
                throw new Exception("object is not extends JsonAble");
            }
        }
        b.append("]");
        return b.toString();
    }
}
