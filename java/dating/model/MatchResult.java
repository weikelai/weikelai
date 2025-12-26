package dating.model;

/**
 * 匹配结果模型类
 */
public class MatchResult {
    private Long userId1; // 用户1的ID
    private Long userId2; // 用户2的ID
    private String userName1; // 用户1的姓名
    private String userName2; // 用户2的姓名
    private Double compatibilityScore; // 兼容性分数（0-100）
    private String matchReason; // 匹配原因

    public MatchResult() {
    }

    public MatchResult(Long userId1, Long userId2, String userName1, String userName2,
            Double compatibilityScore, String matchReason) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.userName1 = userName1;
        this.userName2 = userName2;
        this.compatibilityScore = compatibilityScore;
        this.matchReason = matchReason;
    }

    // Getter和Setter方法
    public Long getUserId1() {
        return userId1;
    }

    public void setUserId1(Long userId1) {
        this.userId1 = userId1;
    }

    public Long getUserId2() {
        return userId2;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }

    public String getUserName1() {
        return userName1;
    }

    public void setUserName1(String userName1) {
        this.userName1 = userName1;
    }

    public String getUserName2() {
        return userName2;
    }

    public void setUserName2(String userName2) {
        this.userName2 = userName2;
    }

    public Double getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setCompatibilityScore(Double compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "userId1=" + userId1 +
                ", userId2=" + userId2 +
                ", userName1='" + userName1 + '\'' +
                ", userName2='" + userName2 + '\'' +
                ", compatibilityScore=" + compatibilityScore +
                ", matchReason='" + matchReason + '\'' +
                '}';
    }
}
