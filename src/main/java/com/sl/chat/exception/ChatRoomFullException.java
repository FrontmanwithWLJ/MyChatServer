package com.sl.chat.exception;

public class ChatRoomFullException extends Exception{
    public ChatRoomFullException(){
        this("the chat room is full");
    }
    public ChatRoomFullException(String e){
        super(e);
    }
}
