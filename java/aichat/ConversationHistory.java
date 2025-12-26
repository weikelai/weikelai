package aichat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 对话历史管理类
 * 负责保存、加载对话历史记录
 */
public class ConversationHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Message> messages;
    
    /**
     * 构造方法
     */
    public ConversationHistory() {
        this.messages = new ArrayList<>();
    }
    
    /**
     * 添加消息
     * @param message 消息对象
     */
    public void addMessage(Message message) {
        messages.add(message);
    }
    
    /**
     * 获取所有消息
     * @return 消息列表
     */
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }
    
    /**
     * 清空对话历史
     */
    public void clear() {
        messages.clear();
    }
    
    /**
     * 保存对话历史到文件（序列化）
     * @param filename 文件名
     * @throws IOException IO异常
     */
    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }
    
    /**
     * 从文件加载对话历史（反序列化）
     * @param filename 文件名
     * @return ConversationHistory对象
     * @throws IOException IO异常
     * @throws ClassNotFoundException 类未找到异常
     */
    public static ConversationHistory loadFromFile(String filename) 
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            return (ConversationHistory) ois.readObject();
        }
    }
    
    /**
     * 导出对话历史为文本文件
     * @param filename 文件名
     * @throws IOException IO异常
     */
    public void exportToText(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filename))) {
            writer.write("=== 对话记录 ===\n");
            writer.write("共 " + messages.size() + " 条消息\n\n");
            
            for (Message msg : messages) {
                writer.write(msg.toString() + "\n\n");
            }
        }
    }
    
    /**
     * 获取消息数量
     * @return 消息数量
     */
    public int size() {
        return messages.size();
    }
}


