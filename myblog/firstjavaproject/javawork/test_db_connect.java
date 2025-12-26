package com.myblog.firstjavaproject.javawork;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-20
 * @Description: 测试数据库连接
 * @Version: 1.0
 */

public class test_db_connect {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/bookdb?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "122452";

        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println("数据库连接成功！");
        conn.close();
    }
}
