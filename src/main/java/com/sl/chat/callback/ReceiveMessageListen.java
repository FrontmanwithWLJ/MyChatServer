package com.sl.chat.callback;

import com.sl.chat.bean.Message;

public interface ReceiveMessageListen {
    /**
     * 收到客户端发送的消息时调用
     * @param id 客户端的识别码，对应list中的坐标
     * @param msg 接受的消息,内含消息来源
     */
    void onReceiveMsg(int id, Message msg, boolean exit);
}
