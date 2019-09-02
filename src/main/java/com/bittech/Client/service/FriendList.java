package com.bittech.Client.service;

import com.bittech.util.CommUtil;
import com.bittech.vo.MessageVo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FriendList {
    private JPanel friendListPanel;
    private JScrollPane friendPanel;
    private JButton createGroupBtn;
    private JScrollPane groupPanel;
    private String myName;
    private Connect2Server connect2Server;
    private Set<String> names;
     // 缓存所有的私聊界面
     private Map<String,PrivateChatGUI> privateChatGUIMap = new ConcurrentHashMap<>();
    //缓存当前客户端的群聊信息
     private Map<String,Set<String>> groupInfo = new ConcurrentHashMap<>();
     //缓存当前客户端群聊界面
    private Map<String,GroupChatGUI> groupChatGUIMap = new ConcurrentHashMap<>();

    //后台线
     private class daemoTask implements  Runnable{
        private Scanner scanner = new Scanner(connect2Server.getIn());
        @Override
        public void run() {
            while (true){
                if(scanner.hasNext()){
                    String strFromServer = scanner.nextLine();
                    if(strFromServer.startsWith("newLogin:")){
                        //好友上线提醒
                        String newFriend = strFromServer.split(":")[1];
                        JOptionPane.showMessageDialog(null,
                                newFriend+"上线了！","上线提醒",JOptionPane.INFORMATION_MESSAGE);

                        names.add(newFriend);
                        //刷新好友列表
                        reloadFriendList();
                    }
                    if(strFromServer.startsWith("{")){
                        //此时是个json字符串
                        MessageVo messageVoFromClient = (MessageVo) CommUtil.json2Object(strFromServer,MessageVo.class);
                        if(messageVoFromClient.getType().equals(2)){
                            //私聊信息
                            String senderName = messageVoFromClient.getContent().split("-")[0];
                            String senderMsg=messageVoFromClient.getContent().split("-")[1];
                            if(privateChatGUIMap.containsKey(senderName)){
                                PrivateChatGUI privateChatGUI = privateChatGUIMap.get(senderName);
                                privateChatGUI.getFrame().setVisible(true);
                                privateChatGUI.readFromServer(senderName+"说："+senderMsg);
                            }else{
                                PrivateChatGUI privateChatGUI = new PrivateChatGUI(senderName,myName,connect2Server);
                                privateChatGUIMap.put(senderName,privateChatGUI);
                                privateChatGUI.readFromServer(senderName+"说："+senderMsg);
                            }
                        }else if(messageVoFromClient.getType().equals(4)){
                            //type：4
                            //content：sendName-msg
                            //to：群名称-[群好友列表]
                            String senderName = messageVoFromClient.getContent().split("-")[0];
                            String groupMsg = messageVoFromClient.getContent().split("-")[1];
                            String groupName = messageVoFromClient.getTo().split("-")[0];
                            if(groupInfo.containsKey(groupName)){
                                if(groupChatGUIMap.containsKey(groupName)){
                                    GroupChatGUI groupChatGUI =groupChatGUIMap.get(groupName);
                                    groupChatGUI.getFrame().setVisible(true);
                                    groupChatGUI.readFromServer(senderName+"说："+groupMsg);
                                }else{
                                    Set<String> friends = groupInfo.get(groupName);
                                    GroupChatGUI groupChatGUI =new GroupChatGUI(groupName,friends,connect2Server,myName);
                                    groupChatGUIMap.put(groupName,groupChatGUI);
                                    groupChatGUI.readFromServer(senderName+"说："+groupMsg);
                                }
                            }else{
                                //1.将群信息以及群成员添加到本客户端列表
                                Set<String> friends = (Set<String>) CommUtil.json2Object(messageVoFromClient.getTo().split("-")[1],Set.class);
                                addGroupInfo(groupName,friends);
                                reloadGroupList();
                                //2.唤起群聊界面
                                GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,friends,connect2Server,myName);
                                groupChatGUIMap.put(groupName,groupChatGUI);
                                groupChatGUI.readFromServer(senderName+"说："+groupMsg);

                            }
                        }

                    }
                }
            }
        }
    }

    private class  privateLabelAction  implements MouseListener{
        private String labelName;
        public  privateLabelAction (String labelName){
            this.labelName = labelName;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            //判断缓存中是否有指定的私聊界面
            if(privateChatGUIMap.containsKey(labelName)){
                PrivateChatGUI privateChatGUI = privateChatGUIMap.get(labelName);
                privateChatGUI.getFrame().setVisible(true);
            }else{
                PrivateChatGUI privateChatGUI = new PrivateChatGUI(labelName,myName,connect2Server);
                privateChatGUIMap.put(labelName,privateChatGUI);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private class GroupLabelAction implements  MouseListener{
         private String groupName;

        public GroupLabelAction(String groupName) {
            this.groupName = groupName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(groupChatGUIMap.containsKey(groupName)){
                GroupChatGUI groupChatGUI = groupChatGUIMap.get(groupName);
                groupChatGUI.getFrame().setVisible(true);
            }else{
                Set<String> friends = groupInfo.get(groupName);
                GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,friends,connect2Server,myName);
                groupChatGUIMap.put(groupName,groupChatGUI);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

   public FriendList(String myName , Connect2Server connect2Server , Set<String> names){
       this.myName=myName;
       this.connect2Server=connect2Server;
       this.names=names;
       JFrame frame = new JFrame(myName);
       frame.setContentPane(friendListPanel);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(400,400);
       frame.setLocationRelativeTo(null);
       frame.setVisible(true);
       reloadFriendList();
       //新启动一个后台线程不断监听服务器发来的信息
       Thread daemonThread = new Thread(new daemoTask());
       daemonThread.setDaemon(true);
       daemonThread.start();
       //点击创建群组弹出创建界面
       createGroupBtn.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               new CreateGroupGUI(names,myName,connect2Server,FriendList.this);
           }
       });
   }
    //动态添加好友列表
    public void reloadFriendList(){
       JPanel friendLabelPanel = new JPanel();
       JLabel[] labels = new JLabel[names.size()];
      //迭代遍历Set集合
       Iterator<String> iterator = names.iterator();

     //设置标签为纵向布局
       friendLabelPanel.setLayout(new BoxLayout(friendLabelPanel,BoxLayout.Y_AXIS));

       int i = 0;
       while (iterator.hasNext()){
         String labelname = iterator.next();
         labels[i] = new JLabel(labelname);

         //给每个标签附加按钮的点击事件
         labels[i].addMouseListener(new privateLabelAction(labelname));
         friendLabelPanel.add(labels[i]);
         i++;
      }
     this.friendPanel.setViewportView(friendLabelPanel); //将friendlabelpanel添加到friendPanel中

     //设置滚动条为垂直的滚动条
     this.friendPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

     this.friendPanel.revalidate();// 刷新一下
 }
    //刷新列表的群聊信息
    public void reloadGroupList(){
        JPanel jPanel= new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        Set<String> groupNames = groupInfo.keySet();
        Iterator<String> iterator = groupNames.iterator();
        while(iterator.hasNext()){
            String groupName = iterator.next();
            JLabel label = new JLabel(groupName);
            label.addMouseListener(new GroupLabelAction(groupName));
            jPanel.add(label);
        }
        groupPanel.setViewportView(jPanel);
        groupPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        groupPanel.revalidate();//刷新
    }
    public void addGroupInfo(String groupName,Set<String> friends){
        groupInfo.put(groupName,friends);
  }
}
