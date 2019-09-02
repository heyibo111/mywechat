package com.bittech.Client.service;

import com.bittech.util.CommUtil;
import com.bittech.vo.MessageVo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CreateGroupGUI {
    private JPanel createGroupPanel;
    private JPanel checkBoxPanel;
    private JTextField groupNameText;
    private JButton confromBtn;
    private String myName;
    private Connect2Server connect2Server;
    private Set<String> friends;
    private FriendList friendList;
    public CreateGroupGUI(Set<String> friends ,
                          String myName, Connect2Server connect2Server, FriendList friendList){
        this.friends=friends;
        this.connect2Server=connect2Server;
        this.myName =myName;
        this.friendList=friendList;
        JFrame frame = new JFrame("创建群组");
        frame.setContentPane(createGroupPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //1.动态的添加checkBox
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel,BoxLayout.Y_AXIS));//垂直布局
        Iterator<String> iterator = friends.iterator();
        while (iterator.hasNext()){
            String friendName = iterator.next();
            JCheckBox checkBox=new JCheckBox(friendName);
           checkBoxPanel.add(checkBox);
        }
        checkBoxPanel.revalidate();//刷新

        //  提交信息按键
        confromBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //1.获取群名称
                String groupName = groupNameText.getText();
                //2.获取选中的好友名称
                Set<String> selectedFriends = new HashSet<>();
                //3.获取checkboxpanel下的所有组件
                Component[] components = checkBoxPanel.getComponents();
                for(Component component: components){
                    //向下转型
                    JCheckBox checkBox =(JCheckBox) component;
                    if(checkBox.isSelected()){
                        selectedFriends.add(checkBox.getText());
                    }
                }
                //加上自己
                selectedFriends.add(myName);
                //4.将群名称与选择的好友发送到服务端
                //type3
                //content：群名称
                //to：[选中的人的集合]
                MessageVo messageVo = new MessageVo();
                messageVo.setType(3);
                messageVo.setContent(groupName);
                messageVo.setTo(CommUtil.object2json(selectedFriends));
                try {
                    PrintStream out = new PrintStream(connect2Server.getOut(),true,"UTF-8");
                    out.println(CommUtil.object2json(messageVo));
                } catch (UnsupportedEncodingException el) {
                    el.printStackTrace();
                }
                frame.setVisible(false);
                //返回好友列表，展示当前的群名
                friendList.addGroupInfo(groupName,selectedFriends);
                friendList.reloadGroupList();
            }
        });
    }


}
