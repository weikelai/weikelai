package dating.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接工具类
 * 用于管理MySQL数据库连接
 * 
 * @author Dating Match System
 * @version 1.0
 */
public class DatabaseUtil {

    // 数据库连接配置
    private static final String URL = "jdbc:mysql://localhost:3306/bookdb?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "122452";

    // MySQL驱动类名
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // 静态块：加载数据库驱动
    static {
        try {
            Class.forName(DRIVER_CLASS);
            System.out.println("MySQL驱动加载成功！");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL驱动加载失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     * 
     * @return Connection 数据库连接对象
     * @throws SQLException 数据库连接异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 获取数据库连接（使用自定义配置）
     * 
     * @param url      数据库URL
     * @param user     用户名
     * @param password 密码
     * @return Connection 数据库连接对象
     * @throws SQLException 数据库连接异常
     */
    public static Connection getConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * 关闭数据库连接
     * 
     * @param conn 数据库连接对象
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("数据库连接已关闭");
            } catch (SQLException e) {
                System.err.println("关闭数据库连接时发生错误：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试数据库连接
     * 
     * @return boolean 连接是否成功
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("数据库连接测试成功！");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("数据库连接测试失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeConnection(conn);
        }
        return false;
    }

    /**
     * 获取数据库URL
     * 
     * @return String 数据库URL
     */
    public static String getUrl() {
        return URL;
    }

    /**
     * 获取数据库用户名
     * 
     * @return String 用户名
     */
    public static String getUser() {
        return USER;
    }
}
