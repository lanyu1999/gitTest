package cn.itcast.dao.impl;

import cn.itcast.pojo.users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestConn {

    public List<users> findAllUsers() {
        List<users> usersList = new ArrayList<users>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            //创建连接
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "123456");
            //创建执行对象
            statement = connection.prepareStatement("select * from users");
            //执行查询
            resultSet = statement.executeQuery();
            //遍历对象
            users users = null;
            while (resultSet.next()) {
                users = new users();
                users.setId(resultSet.getInt("id"));
                users.setUserName(resultSet.getString("userName"));
                users.setPassWord(resultSet.getString("passWord"));
                usersList.add(users);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null){
                    statement.close();
                }
                if(resultSet!=null){
                    resultSet.close();
                }

            } catch (Exception e) {

            }
        }
        //关闭资源
        return usersList;
    }

    public static void main(String[] args) {
        TestConn  testConn = new TestConn();
        List<users> allUsers = testConn.findAllUsers();
        for (users allUser : allUsers) {
            System.out.println(allUser);
        }

    }

}