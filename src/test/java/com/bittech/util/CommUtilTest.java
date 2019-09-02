package com.bittech.util;

import com.bittech.Client.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class CommUtilTest {

    @Test
    public void loadProperties() {
        Properties pro= CommUtil.loadProperties("db.properties");
        Assert.assertNotNull(pro);
    }
    @Test
    public void obj2json(){
        User user= new User();
        user.setId(1);
        user.setUsername("test");
        user.setPassword("123");
        user.setBrief("帅");
        String str = CommUtil.object2json(user);
        System.out.println(str);
    }

    @Test
    public void json2Obj(){

    }
}