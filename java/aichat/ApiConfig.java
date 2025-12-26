package aichat;

import java.io.*;
import java.util.Properties;

/**
 * API配置管理类
 * 负责管理API密钥、URL等配置信息，支持保存和加载配置
 */
public class ApiConfig {
    private static final String CONFIG_FILE = "api_config.properties";
    // 通义千问API地址（兼容OpenAI格式）
    private static final String DEFAULT_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    // 默认使用通义千问模型（可在界面修改）
    private static final String DEFAULT_MODEL = "qwen-turbo";
    // 默认API密钥（通义千问）
    private static final String DEFAULT_API_KEY = "sk-2c82affd324b40b19e2ce8ed5ca953e8";
    
    private String apiKey;
    private String apiUrl;
    private String model;
    private int maxTokens;
    private int fontSize;
    
    /**
     * 构造方法，使用默认配置
     */
    public ApiConfig() {
        this.apiKey = DEFAULT_API_KEY;
        this.apiUrl = DEFAULT_API_URL;
        this.model = DEFAULT_MODEL;
        this.maxTokens = 1024;
        this.fontSize = 14; // 默认字体大小14px
    }
    
    /**
     * 从文件加载配置
     * @return ApiConfig对象
     */
    public static ApiConfig loadFromFile() {
        ApiConfig config = new ApiConfig();
        File configFile = new File(CONFIG_FILE);
        
        if (!configFile.exists()) {
            return config;
        }
        
        try (FileInputStream fis = new FileInputStream(configFile);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8")) {
            
            Properties props = new Properties();
            props.load(isr);
            
            config.apiKey = props.getProperty("api.key", DEFAULT_API_KEY);
            config.apiUrl = props.getProperty("api.url", DEFAULT_API_URL);
            config.model = props.getProperty("api.model", DEFAULT_MODEL);
            config.maxTokens = Integer.parseInt(props.getProperty("api.maxTokens", "1024"));
            config.fontSize = Integer.parseInt(props.getProperty("ui.fontSize", "14"));
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("加载配置失败: " + e.getMessage());
        }
        
        return config;
    }
    
    /**
     * 保存配置到文件
     * @throws IOException IO异常
     */
    public void saveToFile() throws IOException {
        Properties props = new Properties();
        props.setProperty("api.key", apiKey);
        props.setProperty("api.url", apiUrl);
        props.setProperty("api.model", model);
        props.setProperty("api.maxTokens", String.valueOf(maxTokens));
        props.setProperty("ui.fontSize", String.valueOf(fontSize));
        
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
             OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")) {
            props.store(osw, "AI Chat API Configuration");
        }
    }
    
    // Getter和Setter方法
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public int getFontSize() {
        return fontSize;
    }
    
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}


