package com.bittech.Client.service;

import com.bittech.util.CommUtil;
import com.bittech.vo.MessageVo;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

public class GroupChatGUI {
    private JTextArea readFromServer;
    private JPanel groupChatGUIpanel;
    private JTextField send2Server;
    private JPanel friendPanel;
    private JFrame frame;
    private Set<String> friends;
    private  String  groupName;
    private Connect2Server connect2Server;
    private String myName;

    public GroupChatGUI(String groupName, Set<String> friends,Connect2Server connect2Server ,String myName){
        this.myName=myName;
        this.groupName=groupName;
        this.friends=friends;
        this.connect2Server=connect2Server;
        frame = new JFrame(groupName);
        frame.setContentPane(groupChatGUIpanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //1.好友列表的展示
        friendPanel.setLayout(new BoxLayout(friendPanel,BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while(iterator.hasNext()){
        String friendName=iterator.next();
        JLabel label = new JLabel(friendName);
        friendPanel.add(label);
        }
        //2.文本框发送事件
        send2Server.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(send2Server.getText());
                //输入回车
                if(e.getKeyCode()==KeyEvent.VK_ENTER);
                //type：4
                //content：sendName-msg
                //to：群名称
                String str=sb.toString();
                MessageVo messageVo = new MessageVo();
                messageVo.setType(4);
                messageVo.setContent(myName+"-"+str);
                messageVo.setTo(groupName);
                try {
                    PrintStream out = new PrintStream(connect2Server.getOut(),true,"UTF-8");
                    out.println(CommUtil.object2json(messageVo));
                    send2Server.setText("");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
    public void readFromServer(String msg){
        readFromServer.append(msg+"\n");
    }

    public JFrame getFrame() {
        return frame;
    }
}
