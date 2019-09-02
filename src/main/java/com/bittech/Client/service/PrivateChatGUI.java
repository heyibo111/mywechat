package com.bittech.Client.service;

import com.bittech.util.CommUtil;
import com.bittech.vo.MessageVo;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class PrivateChatGUI {
    private JPanel privateChatPanel;
    private JTextArea readFromServer;
    private JTextField sendToServer;
    private JFrame frame;
    private String friendName;
    private String myName;
    private Connect2Server connect2Server;

    //type:2
    //content:senderName-msg
    //to : friendName
   public  PrivateChatGUI(String friendName,String myName,Connect2Server connect2Server) {
       this.friendName = friendName;
       this.myName = myName;
       this.connect2Server = connect2Server;
       frame = new JFrame("与" + friendName + "私聊中...");
       frame.setContentPane(privateChatPanel);
       frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);//不等把窗口直接退出，用HIDE_ON_CLOSE 阴藏并不关闭
       frame.setSize(400, 400);
       frame.setVisible(true);

       //添加一个输入框的事件，敲回车发送信息
       sendToServer.addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
               //将按的哪些键记录下来
               StringBuffer sb = new StringBuffer();
               sb.append(sendToServer.getText());
               //当按回车键时发送信息
               if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                   String str = sb.toString();
                   MessageVo messageVo = new MessageVo();
                   messageVo.setType(2);
                   messageVo.setContent(myName + "-" + str);
                   messageVo.setTo(friendName);
                   try {
                       PrintStream out = new PrintStream(connect2Server.getOut(),
                               true, "UTF-8");
                       out.println(CommUtil.object2json(messageVo));
                       //刷新自己的信息读取框
                       readFromServer.append(myName + "说：" + str+"\n");
                       //清空输入框
                       sendToServer.setText("");
                   } catch (UnsupportedEncodingException e1) {
                       e1.printStackTrace();
                   }
               }
           }
       });
   }

   public void readFromServer (String msg){
       readFromServer.append(msg+"\n");
   }

   public JFrame getFrame(){
       return frame;
   }
}
