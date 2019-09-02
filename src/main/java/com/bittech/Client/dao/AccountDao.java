package com.bittech.Client.dao;

import com.bittech.Client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

public class AccountDao extends BasedDao {
    public boolean userReg(User user) {
        //insert
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getConnection();
            String sql = "INSERT INTO user(username, password, brief)" +
                    " VALUES (?,?,?)";
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//执行sql语句受影响的行数是几

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, DigestUtils.md5Hex(user.getPassword()));
            preparedStatement.setString(3, user.getBrief());

            int row = preparedStatement.executeUpdate();
            if (row == 1)
                return true;
        } catch (SQLException e) {
            System.err.println("用户注册失败");
            e.printStackTrace();
        } finally {
            closeResources(connection, preparedStatement);
        }
        return false;
    }
    public  User  userLogin(String  userName , String password){
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try{
                connection=getConnection();
                String sql="select * from user where username = ? and password = ?";
                preparedStatement=connection.prepareStatement(sql);
                preparedStatement.setString(1,userName);
                preparedStatement.setString(2,DigestUtils.md5Hex(password));
                resultSet=preparedStatement.executeQuery();
                if(resultSet.next()){
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setBrief(resultSet.getString("brief"));
                    return user;
                }
            }catch (SQLException e){
                System.err.println("用户登陆失败");
                e.printStackTrace();
            }finally {
                closeResources(connection,preparedStatement,resultSet);
            }
            return null;
    }
}
