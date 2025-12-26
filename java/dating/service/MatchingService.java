package dating.service;

import dating.algorithm.HungarianAlgorithm;
import dating.model.MatchResult;
import dating.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 匹配服务类 - 使用匈牙利算法进行婚介匹配
 */
@Service
public class MatchingService {

    /**
     * 计算两个用户的兼容性分数
     * 
     * @param user1 用户1
     * @param user2 用户2
     * @return 兼容性分数（0-100）
     */
    public double calculateCompatibility(User user1, User user2) {
        double score = 0.0;
        int factors = 0;

        // 年龄匹配（年龄差越小越好，最大差10岁）
        if (user1.getAge() != null && user2.getAge() != null) {
            int ageDiff = Math.abs(user1.getAge() - user2.getAge());
            double ageScore = Math.max(0, 100 - ageDiff * 10);
            score += ageScore;
            factors++;
        }

        // 城市匹配（同城加分）
        if (user1.getCity() != null && user2.getCity() != null) {
            if (user1.getCity().equals(user2.getCity())) {
                score += 30;
            }
            factors++;
        }

        // 学历匹配
        if (user1.getEducation() != null && user2.getEducation() != null) {
            if (user1.getEducation().equals(user2.getEducation())) {
                score += 20;
            }
            factors++;
        }

        // 身高匹配（身高差在合理范围内）
        if (user1.getHeight() != null && user2.getHeight() != null) {
            int heightDiff = Math.abs(user1.getHeight() - user2.getHeight());
            if (heightDiff <= 15) {
                score += 20;
            } else if (heightDiff <= 25) {
                score += 10;
            }
            factors++;
        }

        // 兴趣爱好匹配
        if (user1.getInterests() != null && user2.getInterests() != null) {
            int commonInterests = 0;
            for (String interest : user1.getInterests()) {
                if (user2.getInterests().contains(interest)) {
                    commonInterests++;
                }
            }
            if (user1.getInterests().size() > 0) {
                double interestScore = (commonInterests * 1.0 / user1.getInterests().size()) * 30;
                score += interestScore;
            }
            factors++;
        }

        // 计算平均分
        if (factors > 0) {
            score = score / factors;
        }

        return Math.min(100, Math.max(0, score));
    }

    /**
     * 判断两个用户是否兼容（用于构建二分图）
     * 
     * @param user1    用户1
     * @param user2    用户2
     * @param minScore 最低兼容性分数阈值
     * @return 是否兼容
     */
    public boolean isCompatible(User user1, User user2, double minScore) {
        // 性别必须不同
        if (user1.getGender() == null || user2.getGender() == null ||
                user1.getGender().equals(user2.getGender())) {
            return false;
        }

        // 兼容性分数必须达到阈值
        double score = calculateCompatibility(user1, user2);
        return score >= minScore;
    }

    /**
     * 使用匈牙利算法进行匹配
     * 
     * @param males    男性用户列表
     * @param females  女性用户列表
     * @param minScore 最低兼容性分数阈值
     * @return 匹配结果列表
     */
    public List<MatchResult> performMatching(List<User> males, List<User> females, double minScore) {
        List<MatchResult> results = new ArrayList<>();

        if (males == null || females == null || males.isEmpty() || females.isEmpty()) {
            return results;
        }

        int maleCount = males.size();
        int femaleCount = females.size();

        // 构建二分图的邻接矩阵
        int[][] graph = new int[maleCount][femaleCount];

        for (int i = 0; i < maleCount; i++) {
            for (int j = 0; j < femaleCount; j++) {
                if (isCompatible(males.get(i), females.get(j), minScore)) {
                    graph[i][j] = 1; // 有边
                } else {
                    graph[i][j] = 0; // 无边
                }
            }
        }

        // 使用匈牙利算法进行匹配
        HungarianAlgorithm algorithm = new HungarianAlgorithm(graph, maleCount, femaleCount);
        int maxMatching = algorithm.findMaxMatching();
        int[][] matchPairs = algorithm.getMatchPairs();

        // 构建匹配结果
        for (int[] pair : matchPairs) {
            int maleIndex = pair[0];
            int femaleIndex = pair[1];

            User male = males.get(maleIndex);
            User female = females.get(femaleIndex);

            double score = calculateCompatibility(male, female);
            String reason = generateMatchReason(male, female, score);

            MatchResult match = new MatchResult(
                    male.getId(),
                    female.getId(),
                    male.getName(),
                    female.getName(),
                    score,
                    reason);

            results.add(match);
        }

        return results;
    }

    /**
     * 生成匹配原因描述
     */
    private String generateMatchReason(User user1, User user2, double score) {
        List<String> reasons = new ArrayList<>();

        if (user1.getCity() != null && user2.getCity() != null &&
                user1.getCity().equals(user2.getCity())) {
            reasons.add("同城");
        }

        if (user1.getAge() != null && user2.getAge() != null) {
            int ageDiff = Math.abs(user1.getAge() - user2.getAge());
            if (ageDiff <= 3) {
                reasons.add("年龄相近");
            }
        }

        if (user1.getInterests() != null && user2.getInterests() != null) {
            int commonCount = 0;
            for (String interest : user1.getInterests()) {
                if (user2.getInterests().contains(interest)) {
                    commonCount++;
                }
            }
            if (commonCount > 0) {
                reasons.add(commonCount + "个共同兴趣");
            }
        }

        if (reasons.isEmpty()) {
            return "综合匹配度较高";
        }

        return String.join("、", reasons);
    }
}
