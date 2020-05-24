package com.sl.chat.callback;

import com.sl.chat.bean.Message;

/**
 * 将消息回调给窗体类，显示消息
 */
public interface ShowMessageCallBack {
    void show(Message message);
}
