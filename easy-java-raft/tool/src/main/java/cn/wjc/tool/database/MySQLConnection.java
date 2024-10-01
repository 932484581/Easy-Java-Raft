package cn.wjc.tool.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection {
    public static void main(String[] args) {
        // 数据库 URL、用户名和密码
        String url = "jdbc:mysql://localhost:3306/easy-java-raft"; // 替换为您的数据库 URL
        String user = "your_username"; // 替换为您的数据库用户名
        String password = "your_password"; // 替换为您的数据库密码

        Connection connection = null;
        Statement statement = null;

        try {
            // 注册 JDBC 驱动程序（对于较新的 JDBC 版本，这一步通常是自动的）
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 打开连接
            connection = DriverManager.getConnection(url, user, password);

            // 执行查询
            statement = connection.createStatement();
            String sql = "SELECT id, name FROM your_table_name"; // 替换为您的 SQL 查询
            ResultSet resultSet = statement.executeQuery(sql);

            // 处理结果集
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("ID: " + id + ", Name: " + name);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
