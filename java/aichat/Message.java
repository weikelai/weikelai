package aichat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 消息实体类
 * 表示对话中的一条消息，包含角色、内容和时间戳
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String role;        // 角色: "user" 或 "assistant"
    private String content;     // 消息内容
    private LocalDateTime timestamp;  // 时间戳
    
    /**
     * 构造方法
     * @param role 消息角色
     * @param content 消息内容
     */
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * 获取格式化的时间字符串
     * @return 格式化的时间字符串
     */
    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    /**
     * 覆盖toString方法，用于显示消息
     */
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", 
            getFormattedTime(), role, content);
    }
}


