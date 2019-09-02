package com.bittech.Client.service;

import com.bittech.Client.dao.AccountDao;
import com.bittech.Client.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserReg {
    private JPanel regPanel;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JTextField breifText;
    private JButton confimBtn;

    private AccountDao accountDao = new AccountDao();


    public UserReg() {
        JFrame frame = new JFrame("用户注册");
        frame.setContentPane(regPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        //点击提交按钮触发此方法
        confimBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             //1.获取界面上三个控件的内容
             String userName = userNameText.getText();
             String password=String.valueOf(passwordText.getPassword());
             String brief = breifText.getText();
             //2.调用Dao层方法将信息持久化到数据库
                User user = new User();
                user.setUsername(userName);
                user.setPassword(password);
                user.setBrief(brief);
                System.out.println(user);
                if(accountDao.userReg(user)){
                    //弹出提示框提示用户信息注册成功
                    //返回登陆界面
                    JOptionPane.showMessageDialog(null,
                            "注册成功","成功信息",JOptionPane.INFORMATION_MESSAGE);

                    frame.setVisible(false);
                }else{
                    //弹出提示框告知用户注册失败
                    //保留当前的注册页面
                    JOptionPane.showMessageDialog(null,
                            "注册失败！","失败信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


}
