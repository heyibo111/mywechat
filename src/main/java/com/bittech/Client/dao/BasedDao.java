package com.bittech.Client.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bittech.util.CommUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

//Dao层基础类，封装数据源，获取连接，关闭资源等共有操作
public class BasedDao {
    private  static   DruidDataSource DATASOURCE;
    //1.加载数据源
    static {
        Properties pro = CommUtil.loadProperties("db.properties");
        try {
            DATASOURCE=(DruidDataSource) DruidDataSourceFactory.createDataSource(pro);
        } catch (Exception e) {
            System.out.println("数据源加载失败");
            e.printStackTrace();
        }
    }
    //2.获取连接
    protected Connection getConnection(){
        try {
            return (Connection) DATASOURCE.getPooledConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //4.关闭资源
    protected void closeResources(Connection connection , Statement statement){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void closeResources(Connection connection , Statement statement, ResultSet resultSet){
        closeResources(connection,statement);
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
