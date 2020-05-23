package com.sl.chat.callback;

import com.sl.chat.bean.Message;

public interface ReceiveMessageListen {
    /**
     * 收到客户端发送的消息时调用
     * @param msg 接受的消息,内含发送者的信息，以及发送时间
     */
    void onReceiveMsg(Message msg, boolean exit);
}
