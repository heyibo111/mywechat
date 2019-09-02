package com.bittech.Server;

import com.bittech.util.CommUtil;
import com.bittech.vo.MessageVo;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {
    private static final Integer PORT;
    static {
        Properties pro = CommUtil.loadProperties("socket.properties");
        PORT = Integer.valueOf(pro.getProperty("PORT"));
    }
    //1.服务端缓存所有连接客户的对象
    private  static Map<String,Socket> clients = new ConcurrentHashMap<>();
    //缓存所有群名称以及群中的成员姓名
    private  static Map<String,Set<String>> groupInfo= new ConcurrentHashMap<>();
    //服务端具体处理客户端请求的任务
    private static class  ExecuteClient implements Runnable{
       private  Socket client;
       private Scanner in;
       private PrintStream out;
       public ExecuteClient(Socket client) {
            this.client = client;
           try {
               this.in=new Scanner(client.getInputStream());
               this.out=new PrintStream(client.getOutputStream(),true,"UTF-8");
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
        @Override
        public void run() {
        while(true){
            if(in.hasNext()){
                String  strFromClient = in.nextLine();
                MessageVo msgFromClient = (MessageVo) CommUtil.json2Object(strFromClient,MessageVo.class);
                if(msgFromClient.getType().equals(1)){
                    //注册新用户
                    String userName = msgFromClient.getContent();
                    //服务端：
                    //2.将当前所有在线用户发回给新用户  response
                    //3.将新用户上线信息发给所有其他用户（上线提醒）
                    //1.保存新用户上线信息  request

                    //将当前聊天室在线好友信息发回给新用户
                    Set<String> names = clients.keySet();
                    MessageVo msg2Client = new MessageVo();
                    msg2Client.setType(1);
                    msg2Client.setContent(CommUtil.object2json(names));
                    out.println(CommUtil.object2json(msg2Client));
                    //将新用户的上线信息发给其他用户
                    String loginMsg = "newLogin:" + userName ;
                    for(Socket socket: clients.values()){
                        try {
                            PrintStream out = new PrintStream(socket.getOutputStream(), true, "UTF-8") ;
                                out.println(loginMsg);
                            }catch (UnsupportedEncodingException e){
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                        }
                    }
                    //将新用户信息保存到当前的服务端缓存
                    System.out.println(userName+"上线了！");
                    clients.put(userName,client);
                    System.out.println("当前聊天室在线人数为："+clients.size());
                }else if(msgFromClient.getType().equals(2)){
                    //私聊信息；
                    String friendName = msgFromClient.getTo();
                    Socket socket = clients.get(friendName);
                    try {
                        PrintStream out = new PrintStream(socket.getOutputStream(),true,"UTF-8");
                       MessageVo msgToClient = new MessageVo();
                       msgToClient.setType(2);
                       msgToClient.setContent(msgFromClient.getContent());
                       out.println(CommUtil.object2json(msgToClient));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(msgFromClient.getType().equals(3)){
                    //注册群信息
                    String groupName = msgFromClient.getContent();
                    Set<String> friends = (Set<String>) CommUtil.json2Object(msgFromClient.getTo(),Set.class);
                    groupInfo.put(groupName,friends);
                    System.out.println("注册群成功！当前共有"+groupInfo.size()+"个群");
                }else if(msgFromClient.getType().equals(4)){
                    //type：4
                    //content：sendName-msg
                    //to：群名称
                    String groupName = msgFromClient.getTo();
                    Set<String> friends = groupInfo.get(groupName);
                    //将群聊信息转发到相应客户端

                    Iterator<String> iterator = friends.iterator();
                    while(iterator.hasNext()){
                        String clinetName = iterator.next();
                        Socket client = clients.get(clinetName);
                        try {
                            PrintStream out = new PrintStream(client.getOutputStream(),
                                            true,"UTF-8");
                            //type：4
                            //content：sendName-msg
                            //to：群名称-[群好友列表]
                            MessageVo messageVo = new MessageVo();
                            messageVo.setType(4);
                            messageVo.setContent(msgFromClient.getContent());
                            messageVo.setTo(groupName+"-"+CommUtil.object2json(friends));
                            out.println(CommUtil.object2json(messageVo));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        }
    }

    public static void main(String[] args) throws IOException {
        //1.建立基站
        ServerSocket server = new ServerSocket(PORT);
        //2.新建线程
        ExecutorService exectors = Executors.newFixedThreadPool(50);
        for(int i = 0;i < 50; i++ ){
            System.out.println("等待客户端连接...");
            Socket client = server.accept();
            System.out.println("有新的连接，端口号为："+client.getPort());
            exectors.submit(new ExecuteClient(client));
        }

    }
}
