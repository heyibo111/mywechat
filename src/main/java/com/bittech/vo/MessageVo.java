package com.bittech.vo;
//服务端与客户端通信的载体

import lombok.Data;

@Data
public class MessageVo {
    //告知服务端要进行的操作，eg：1表示新用户注册，2 表示私聊  3表示群聊等
    private  Integer type;

    //服务端与客户端聊天的具体内容
    private String content;

    //聊天信息发送的目标客户端名称   私聊/群聊
    private String to;


}
