package com.bittech.Client.service;

import com.bittech.util.CommUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

//封装了客户端与服务端建立的连接以及输入流与输出流
public class Connect2Server {
    private  static  final int PORT;
    private  static  final String IP;
    private Socket client;
    private InputStream in;
    private OutputStream out;

    static {
        Properties pros = CommUtil.loadProperties("socket.properties");
        PORT = Integer.parseInt(pros.getProperty("PORT"));
        IP = pros.getProperty("IP");
    }
    public  Connect2Server(){
        //与服务器建立连接
        try {
            client= new Socket(IP,PORT);
            this.in=client.getInputStream();
            this.out=client.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }
}
