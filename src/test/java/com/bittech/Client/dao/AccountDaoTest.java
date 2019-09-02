package com.bittech.Client.dao;

import com.bittech.Client.entity.User;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountDaoTest {
 private  AccountDao accountDao = new AccountDao();
    @Test
    public void userRegister() {
        User user =new User();
        user.setUsername("hehe");
        user.setPassword("456");
        user.setBrief("kong");
        boolean b =accountDao.userReg(user);
        Assert.assertTrue(b);
    }

    @Test
    public void userLogin() {
        User user =accountDao.userLogin("test","123");
        Assert.assertNotNull(user);
    }
}