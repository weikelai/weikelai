package dating.controller;

import dating.dao.UserDAO;
import dating.model.MatchResult;
import dating.model.User;
import dating.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 婚介匹配系统控制器
 */
@RestController
@RequestMapping("/api/dating")
@CrossOrigin(origins = "*") // 允许跨域访问
public class DatingController {

    @Autowired
    private MatchingService matchingService;

    // 使用数据库存储
    private UserDAO userDAO = new UserDAO();

    /**
     * 注册新用户
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 验证必填字段
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "姓名不能为空");
                return ResponseEntity.ok(response);
            }

            if (user.getGender() == null || user.getGender().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "性别不能为空");
                return ResponseEntity.ok(response);
            }

            if (user.getAge() == null || user.getAge() < 18 || user.getAge() > 100) {
                response.put("success", false);
                response.put("message", "年龄必须在18-100之间");
                return ResponseEntity.ok(response);
            }

            // 保存到数据库
            Long userId = userDAO.save(user);

            if (userId != null) {
                user.setId(userId);
                response.put("success", true);
                response.put("message", "用户注册成功");
                response.put("userId", userId);
                response.put("user", user);
            } else {
                response.put("success", false);
                response.put("message", "用户注册失败");
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "数据库错误: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "注册失败: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 确保表存在
            UserDAO.initTable();

            List<User> users = userDAO.findAll();
            response.put("success", true);
            response.put("users", users);
            response.put("count", users.size());

            // 添加数据库统计信息
            Map<String, Object> dbStats = userDAO.getDatabaseStats();
            response.put("databaseStats", dbStats);

        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "获取用户列表失败: " + e.getMessage());
            response.put("users", new ArrayList<>());
            response.put("count", 0);
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userDAO.findById(id);

            if (user != null) {
                response.put("success", true);
                response.put("user", user);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "获取用户失败: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 执行匹配
     * 
     * @param minScore 最低兼容性分数阈值（可选，默认50）
     */
    @GetMapping("/match")
    public ResponseEntity<Map<String, Object>> performMatch(
            @RequestParam(required = false, defaultValue = "50") double minScore) {

        Map<String, Object> response = new HashMap<>();

        // 从数据库分离男性和女性用户
        List<User> males = new ArrayList<>();
        List<User> females = new ArrayList<>();

        try {
            List<User> allUsers = userDAO.findAll();
            for (User user : allUsers) {
                if ("M".equalsIgnoreCase(user.getGender())) {
                    males.add(user);
                } else if ("F".equalsIgnoreCase(user.getGender())) {
                    females.add(user);
                }
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "获取用户列表失败: " + e.getMessage());
            response.put("matches", new ArrayList<>());
            response.put("matchCount", 0);
            response.put("maleCount", 0);
            response.put("femaleCount", 0);
            response.put("minScore", minScore);
            return ResponseEntity.ok(response);
        }

        // 执行匹配
        List<MatchResult> matches = matchingService.performMatching(males, females, minScore);

        response.put("success", true);
        response.put("matches", matches);
        response.put("matchCount", matches.size());
        response.put("maleCount", males.size());
        response.put("femaleCount", females.size());
        response.put("minScore", minScore);

        return ResponseEntity.ok(response);
    }

    /**
     * 计算两个用户的兼容性分数
     */
    @GetMapping("/compatibility")
    public ResponseEntity<Map<String, Object>> calculateCompatibility(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {

        Map<String, Object> response = new HashMap<>();

        User user1 = null;
        User user2 = null;

        try {
            user1 = userDAO.findById(userId1);
            user2 = userDAO.findById(userId2);
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "获取用户失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }

        if (user1 == null || user2 == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response);
        }

        double score = matchingService.calculateCompatibility(user1, user2);

        response.put("success", true);
        response.put("userId1", userId1);
        response.put("userId2", userId2);
        response.put("userName1", user1.getName());
        response.put("userName2", user2.getName());
        response.put("compatibilityScore", score);

        return ResponseEntity.ok(response);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 验证用户ID
            if (id == null || id <= 0) {
                response.put("success", false);
                response.put("message", "无效的用户ID");
                return ResponseEntity.ok(response);
            }

            User existingUser = userDAO.findById(id);
            if (existingUser == null) {
                response.put("success", false);
                response.put("message", "用户不存在，ID: " + id);
                return ResponseEntity.ok(response);
            }

            // 验证必填字段
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "姓名不能为空");
                return ResponseEntity.ok(response);
            }

            if (user.getGender() == null || user.getGender().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "性别不能为空");
                return ResponseEntity.ok(response);
            }

            // 更新用户信息（保持ID不变）
            user.setId(id);
            boolean updated = userDAO.update(user);

            if (updated) {
                response.put("success", true);
                response.put("message", "用户更新成功");
                response.put("user", user);
            } else {
                response.put("success", false);
                response.put("message", "更新失败");
            }

            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "数据库错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新用户时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = userDAO.delete(id);

            if (deleted) {
                response.put("success", true);
                response.put("message", "用户删除成功");
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "删除用户失败: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取数据库统计信息
     */
    @GetMapping("/database/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStats() {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> stats = userDAO.getDatabaseStats();
            response.put("success", true);
            response.put("stats", stats);
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "获取数据库统计信息失败: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取bookdb数据库中所有表的数据
     */
    @GetMapping("/database/tables")
    public ResponseEntity<Map<String, Object>> getAllTablesData() {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> allData = userDAO.getAllTablesData();
            response.put("success", true);
            response.put("data", allData);
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "获取数据库表数据失败: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取匹配规则说明
     */
    @GetMapping("/match-rules")
    public ResponseEntity<Map<String, Object>> getMatchRules() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        Map<String, Object> rules = new HashMap<>();
        rules.put("title", "匹配规则说明");
        rules.put("description", "系统使用匈牙利算法进行二分图最大匹配，兼容性评分综合考虑以下因素：");

        List<Map<String, Object>> factors = new ArrayList<>();

        Map<String, Object> factor1 = new HashMap<>();
        factor1.put("name", "年龄匹配");
        factor1.put("weight", "30%");
        factor1.put("rule", "年龄差越小分数越高，计算公式：(100 - 年龄差 × 10)，最大差10岁");
        factors.add(factor1);

        Map<String, Object> factor2 = new HashMap<>();
        factor2.put("name", "城市匹配");
        factor2.put("weight", "20%");
        factor2.put("rule", "同城用户额外加30分，不同城市不加分");
        factors.add(factor2);

        Map<String, Object> factor3 = new HashMap<>();
        factor3.put("name", "学历匹配");
        factor3.put("weight", "15%");
        factor3.put("rule", "相同学历加20分，不同学历不加分");
        factors.add(factor3);

        Map<String, Object> factor4 = new HashMap<>();
        factor4.put("name", "身高匹配");
        factor4.put("weight", "15%");
        factor4.put("rule", "身高差≤15cm加20分，15-25cm加10分，>25cm不加分");
        factors.add(factor4);

        Map<String, Object> factor5 = new HashMap<>();
        factor5.put("name", "兴趣爱好匹配");
        factor5.put("weight", "20%");
        factor5.put("rule", "共同兴趣数量占比例×30分，例如：3个共同兴趣/5个总兴趣=60%×30=18分");
        factors.add(factor5);

        rules.put("factors", factors);
        rules.put("algorithm", "匈牙利算法（二分图最大匹配算法）");
        rules.put("algorithmDesc", "通过寻找增广路径，找到最大数量的匹配对，确保每个用户最多匹配一个对象");
        rules.put("minScore", "默认最低兼容性分数为50分，可在匹配页面自定义调整");

        response.put("rules", rules);
        return ResponseEntity.ok(response);
    }
}
