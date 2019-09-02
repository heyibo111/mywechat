package com.bittech.Client.service;

import com.bittech.Client.dao.AccountDao;
import com.bittech.Client.entity.User;
import com.bittech.util.CommUtil;
import com.bittech.vo.MessageVo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Set;

public class User_Login {
    private JPanel loginPanel;
    private JPanel labelPanel;
    private JPanel userNamePanel;
    private JTextField userNameText;
    private JLabel password;
    private JPanel passwordPanel;
    private JPanel btnPanel;
    private JButton regBtn;
    private JButton loginBtn;
    private JPasswordField passwordText;
    private JLabel userName;
    private AccountDao accountDao = new AccountDao();

    public User_Login() {
        JFrame frame = new JFrame("用户登陆");
        frame.setContentPane(loginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        //居中显示
        frame.pack();
        frame.setVisible(true);
        //点击注册按钮，弹出注册页面
        regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserReg();
            }
        });
        //点击登陆按钮，验证用户输入是否正确
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取用户输入
                String userName = userNameText.getText();
                String password = String.valueOf(passwordText.getPassword());
                User user = accountDao.userLogin(userName,password);
                if(user!=null){
                    //登陆成功
                    JOptionPane.showMessageDialog(null,"登陆成功！",
                            "提示信息",JOptionPane.INFORMATION_MESSAGE);
                    //与服务器建立连接
                     Connect2Server connect2Server = new Connect2Server();
                    //1.与服务器建立连接，将自己的用户名与Socket保存到服务端缓存
                     MessageVo  messageVo = new MessageVo();
                     messageVo.setType(1);
                     messageVo.setContent(userName);
                     String msgJson = CommUtil.object2json(messageVo);
                    try {
                        //发送信息
                        PrintStream out =
                                new PrintStream(connect2Server.getOut(),true,"UTF-8");
                        out.println(msgJson);
                     //读取服务端发回的响应，加载用户列表(读取所有的在线好友信息)
                        Scanner in = new Scanner(connect2Server.getIn());
                        if(in.hasNext()) {
                            String jsonStr = in.nextLine();
                            MessageVo msgFromServer = (MessageVo) CommUtil.json2Object(jsonStr, MessageVo.class);
                            Set<String> names = (Set<String>) CommUtil.json2Object(msgFromServer.getContent(),Set.class);
                            System.out.println("在线好友名称为：" + names);
                            //将登陆界面不可见，加载好友列表界面
                            frame.setVisible(false);
                            //跳转到好友界面时需要传递用户名，与服务器建立的连接，所有在线的好友信息
                            new FriendList(userName,connect2Server,names);
                        }
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    //2.读取服务端的所有在线好友信息
                    //3.新建一个后台线程不断读取服务器发来的信息
                    //跳转到用户列表页面：
                }else{
                    JOptionPane.showMessageDialog(null,"登陆失败！",
                            "错误信息",JOptionPane.ERROR_MESSAGE);
                    //保留当前的登陆界面
                }
            }
        });
    }

    public static void main(String[] args) {
        new User_Login();
    }
}
