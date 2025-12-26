package aichat;

/**
 * API调用自定义异常类
 * 用于处理API调用过程中的各种错误
 */
public class ApiException extends Exception {
    private static final long serialVersionUID = 1L;
    
    /**
     * 构造方法
     * @param message 错误消息
     */
    public ApiException(String message) {
        super(message);
    }
    
    /**
     * 构造方法
     * @param message 错误消息
     * @param cause 原因异常
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}


