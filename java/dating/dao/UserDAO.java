package dating.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dating.model.User;
import dating.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据访问对象
 * 负责与数据库交互，执行用户的CRUD操作
 */
public class UserDAO {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 保存用户到数据库
     */
    public Long save(User user) throws SQLException {
        String sql = "INSERT INTO dating_users (name, gender, age, height, city, education, occupation, interests, description) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getGender());
            ps.setInt(3, user.getAge());
            ps.setObject(4, user.getHeight(), Types.INTEGER);
            ps.setString(5, user.getCity());
            ps.setString(6, user.getEducation());
            ps.setString(7, user.getOccupation());

            // 将interests列表转换为JSON字符串
            String interestsJson = null;
            if (user.getInterests() != null && !user.getInterests().isEmpty()) {
                interestsJson = objectMapper.writeValueAsString(user.getInterests());
            }
            ps.setString(8, interestsJson);

            ps.setString(9, user.getDescription());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLException("保存用户失败: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * 根据ID查找用户
     */
    public User findById(Long id) throws SQLException {
        String sql = "SELECT * FROM dating_users WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null;
    }

    /**
     * 查找所有用户
     */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();

        // 先检查表是否存在
        if (!tableExists("dating_users")) {
            System.out.println("警告: dating_users表不存在，正在创建...");
            initTable();
        }

        String sql = "SELECT * FROM dating_users ORDER BY id DESC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }

        return users;
    }

    /**
     * 根据性别查找用户
     */
    public List<User> findByGender(String gender) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM dating_users WHERE gender = ? ORDER BY id DESC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gender);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }

        return users;
    }

    /**
     * 更新用户信息
     */
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE dating_users SET name=?, gender=?, age=?, height=?, city=?, " +
                "education=?, occupation=?, interests=?, description=? WHERE id=?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getGender());
            ps.setInt(3, user.getAge());
            ps.setObject(4, user.getHeight(), Types.INTEGER);
            ps.setString(5, user.getCity());
            ps.setString(6, user.getEducation());
            ps.setString(7, user.getOccupation());

            // 将interests列表转换为JSON字符串
            String interestsJson = null;
            if (user.getInterests() != null && !user.getInterests().isEmpty()) {
                interestsJson = objectMapper.writeValueAsString(user.getInterests());
            }
            ps.setString(8, interestsJson);

            ps.setString(9, user.getDescription());
            ps.setLong(10, user.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new SQLException("更新用户失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除用户
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM dating_users WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 将ResultSet映射为User对象
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setGender(rs.getString("gender"));
        user.setAge(rs.getInt("age"));

        int height = rs.getInt("height");
        if (!rs.wasNull()) {
            user.setHeight(height);
        }

        user.setCity(rs.getString("city"));
        user.setEducation(rs.getString("education"));
        user.setOccupation(rs.getString("occupation"));

        // 将JSON字符串转换为interests列表
        String interestsJson = rs.getString("interests");
        if (interestsJson != null && !interestsJson.trim().isEmpty()) {
            try {
                List<String> interests = objectMapper.readValue(interestsJson,
                        new TypeReference<List<String>>() {
                        });
                user.setInterests(interests);
            } catch (Exception e) {
                // 如果JSON解析失败，设置为空列表
                user.setInterests(new ArrayList<>());
            }
        } else {
            user.setInterests(new ArrayList<>());
        }

        user.setDescription(rs.getString("description"));

        return user;
    }

    /**
     * 检查表是否存在
     */
    private boolean tableExists(String tableName) throws SQLException {
        String sql = "SHOW TABLES LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * 检查表是否存在，如果不存在则创建
     */
    public static void initTable() throws SQLException {
        String createTableSql = "CREATE TABLE IF NOT EXISTS dating_users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID', " +
                "name VARCHAR(50) NOT NULL COMMENT '姓名', " +
                "gender CHAR(1) NOT NULL COMMENT '性别：M-男，F-女', " +
                "age INT NOT NULL COMMENT '年龄', " +
                "height INT COMMENT '身高（cm）', " +
                "city VARCHAR(50) COMMENT '城市', " +
                "education VARCHAR(20) COMMENT '学历', " +
                "occupation VARCHAR(50) COMMENT '职业', " +
                "interests TEXT COMMENT '兴趣爱好（JSON格式）', " +
                "description TEXT COMMENT '个人简介', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
                "INDEX idx_gender (gender), " +
                "INDEX idx_city (city), " +
                "INDEX idx_age (age)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表'";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
            System.out.println("用户表初始化成功！");
        }
    }

    /**
     * 获取数据库统计信息
     */
    public Map<String, Object> getDatabaseStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // 获取所有表名
            List<String> tables = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
            }
            stats.put("tables", tables);

            // 获取dating_users表的记录数
            int userCount = 0;
            if (tableExists("dating_users")) {
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM dating_users")) {
                    if (rs.next()) {
                        userCount = rs.getInt(1);
                    }
                }
            }
            stats.put("userCount", userCount);

            // 获取数据库名
            stats.put("databaseName", conn.getCatalog());
        }

        return stats;
    }

    /**
     * 获取bookdb数据库中所有表的数据
     */
    public Map<String, Object> getAllTablesData() throws SQLException {
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // 获取所有表名
            List<String> tables = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
            }

            // 遍历每个表，获取数据
            for (String tableName : tables) {
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM `" + tableName + "` LIMIT 100")) {

                    List<Map<String, Object>> tableData = new ArrayList<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            Object value = rs.getObject(i);
                            row.put(columnName, value);
                        }
                        tableData.add(row);
                    }

                    result.put(tableName, tableData);
                } catch (SQLException e) {
                    // 如果某个表查询失败，记录错误但继续处理其他表
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", e.getMessage());
                    result.put(tableName + "_error", error);
                }
            }
        }

        return result;
    }
}
