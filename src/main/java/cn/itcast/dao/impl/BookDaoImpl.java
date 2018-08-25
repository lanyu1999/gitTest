package cn.itcast.dao.impl;

import cn.itcast.dao.BookDao;
import cn.itcast.pojo.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {
    public List<Book> queryBookList() {
        List<Book> bookList = new ArrayList<Book>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            //创建连接
            connection =
                    DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/luceneFirst", "root", "123456");
            //创建执行对象
            preparedStatement = connection.prepareStatement("SELECT *from book");
            //执行查询
            resultSet = preparedStatement.executeQuery();
            Book book = null;
            //遍历查询
            while (resultSet.next()) {
                book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setBookName(resultSet.getString("bookName"));
                book.setPrice(resultSet.getFloat("price"));
                book.setPic(resultSet.getString("pic"));
                book.setBookDesc(resultSet.getString("bookDesc"));
                bookList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return bookList;
    }

    public static void main(String[] args) {
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.queryBookList();
        for (Book book : bookList) {
            System.out.println(book);
        }
    }
}
