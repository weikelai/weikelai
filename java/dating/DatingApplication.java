package dating;

import dating.dao.UserDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

/**
 * 婚介匹配系统主应用类
 */
@SpringBootApplication
public class DatingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatingApplication.class, args);
        System.out.println("=================================");
        System.out.println("婚介匹配系统启动成功！");
        System.out.println("访问地址: http://localhost:8884");
        System.out.println("API文档: http://localhost:8884/api/dating");
        System.out.println("=================================");
    }

    /**
     * 应用启动后自动初始化数据库表
     */
    @PostConstruct
    public void initDatabase() {
        try {
            System.out.println("正在初始化数据库表...");
            UserDAO.initTable();
            System.out.println("数据库表初始化完成！");
        } catch (SQLException e) {
            System.err.println("数据库表初始化失败: " + e.getMessage());
            System.err.println("请确保MySQL服务已启动，并且数据库连接配置正确！");
            e.printStackTrace();
        }
    }
}
