package aichat;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * API客户端类
 * 使用OkHttp调用AI API，发送消息并获取响应
 */
public class ApiClient {
    private ApiConfig config;
    private OkHttpClient client;
    
    /**
     * 构造方法
     * @param config API配置
     */
    public ApiClient(ApiConfig config) {
        this.config = config;
        // 配置超时时间，避免长时间无响应导致超时
        this.client = new OkHttpClient.Builder()
                .connectTimeout(java.time.Duration.ofSeconds(15))
                .readTimeout(java.time.Duration.ofSeconds(30))
                .writeTimeout(java.time.Duration.ofSeconds(30))
                .callTimeout(java.time.Duration.ofSeconds(45))
                .retryOnConnectionFailure(true)
                .build();
    }
    
    /**
     * 发送消息到API
     * @param history 对话历史
     * @param userMessage 用户消息
     * @return AI回复内容
     * @throws ApiException API异常
     */
    public String sendMessage(List<Message> history, String userMessage) throws ApiException {
        if (config.getApiKey() == null || config.getApiKey().trim().isEmpty()) {
            throw new ApiException("API密钥未配置，请在设置中配置API密钥");
        }
        
        // 验证用户消息不为空
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new ApiException("用户消息不能为空");
        }
        
        try {
            // 构建JSON请求体
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", config.getModel());
            jsonBody.put("max_tokens", config.getMaxTokens());
            
            // 构建消息数组（使用messages格式，符合chat/completions接口规范）
            JSONArray messages = new JSONArray();
            
            // ⚠️ 关键修复：正确添加历史消息
            // 历史消息应该已经是完整的对话记录（包含role和content）
            for (Message msg : history) {
                if (msg == null || msg.getRole() == null || msg.getContent() == null) {
                    continue; // 跳过无效消息
                }
                JSONObject msgObj = new JSONObject();
                msgObj.put("role", msg.getRole().trim());
                msgObj.put("content", msg.getContent().trim());
                messages.put(msgObj);
            }
            
            // ⚠️ 关键修复：检查历史消息中是否已包含当前用户消息
            // 如果已包含，则不再重复添加（避免重复）
            boolean userMessageAlreadyInHistory = false;
            if (!history.isEmpty()) {
                Message lastMsg = history.get(history.size() - 1);
                if ("user".equals(lastMsg.getRole()) && userMessage.equals(lastMsg.getContent())) {
                    userMessageAlreadyInHistory = true;
                }
            }
            
            // 只有当历史消息中不包含当前用户消息时，才添加
            if (!userMessageAlreadyInHistory) {
                JSONObject userMsgObj = new JSONObject();
                userMsgObj.put("role", "user");
                userMsgObj.put("content", userMessage.trim());
                messages.put(userMsgObj);
            }
            
            jsonBody.put("messages", messages);
            
            // 创建HTTP请求
            RequestBody requestBody = RequestBody.create(
                jsonBody.toString(), 
                MediaType.parse("application/json; charset=utf-8")
            );
            
            Request request = new Request.Builder()
                .url(config.getApiUrl())
                .addHeader("Authorization", "Bearer " + config.getApiKey())
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();
            
            // 执行请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = "未知错误";
                    ResponseBody errorResponseBody = response.body();
                    if (errorResponseBody != null) {
                        try {
                            errorBody = errorResponseBody.string();
                            // 尝试解析错误响应中的中文错误信息
                            try {
                                JSONObject errorJson = new JSONObject(errorBody);
                                if (errorJson.has("error")) {
                                    JSONObject error = errorJson.getJSONObject("error");
                                    String errorZh = error.optString("message_zh", "");
                                    if (!errorZh.isEmpty()) {
                                        errorBody = errorZh;
                                    } else {
                                        errorBody = error.optString("message", errorBody);
                                    }
                                }
                            } catch (Exception ignored) {
                                // 如果解析失败，使用原始错误信息
                            }
                        } catch (IOException e) {
                            errorBody = "无法读取错误响应: " + e.getMessage();
                        }
                    }
                    throw new ApiException("API请求失败 (HTTP " + response.code() + "): " + errorBody);
                }
                
                // 解析响应
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new ApiException("API返回空响应体");
                }
                
                String responseBodyString = responseBody.string();
                if (responseBodyString == null || responseBodyString.trim().isEmpty()) {
                    throw new ApiException("API返回空响应体");
                }
                
                return parseResponse(responseBodyString);
            }
            
        } catch (IOException e) {
            throw new ApiException("网络请求失败: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ApiException("处理响应失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析API响应
     * @param responseBody 响应体JSON字符串
     * @return AI回复内容
     * @throws ApiException API异常
     */
    private String parseResponse(String responseBody) throws ApiException {
        // 如果响应体为空，直接抛出异常
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new ApiException("API返回空响应体");
        }
        
        try {
            JSONObject json = new JSONObject(responseBody);
            
            // 检查是否有错误
            if (json.has("error")) {
                JSONObject error = json.getJSONObject("error");
                String errorMessage = error.optString("message", "未知错误");
                String errorType = error.optString("type", "");
                String errorZh = error.optString("message_zh", "");
                
                // 优先使用中文错误信息
                String finalErrorMessage = errorZh.isEmpty() ? errorMessage : errorZh;
                if (!errorType.isEmpty()) {
                    finalErrorMessage = "[" + errorType + "] " + finalErrorMessage;
                }
                throw new ApiException("API错误: " + finalErrorMessage);
            }
            
            // 提取回复内容
            if (!json.has("choices")) {
                // 如果没有choices字段，尝试其他可能的响应格式
                if (json.has("content")) {
                    return json.getString("content");
                }
                if (json.has("text")) {
                    return json.getString("text");
                }
                throw new ApiException("API响应格式异常，缺少choices字段。响应内容: " + responseBody.substring(0, Math.min(200, responseBody.length())));
            }
            
            JSONArray choices = json.getJSONArray("choices");
            if (choices.length() == 0) {
                throw new ApiException("API返回空响应：choices数组为空。响应内容: " + responseBody.substring(0, Math.min(200, responseBody.length())));
            }
            
            JSONObject firstChoice = choices.getJSONObject(0);
            
            // 检查finish_reason，如果是length或content_filter，说明响应被截断或过滤
            if (firstChoice.has("finish_reason")) {
                String finishReason = firstChoice.getString("finish_reason");
                if ("length".equals(finishReason)) {
                    // 响应被截断，但仍然返回内容
                    System.out.println("警告: 响应因达到最大token限制被截断");
                } else if ("content_filter".equals(finishReason)) {
                    throw new ApiException("API响应被内容过滤器拦截");
                }
            }
            
            // 提取message对象
            if (!firstChoice.has("message")) {
                // 某些API可能直接返回text字段
                if (firstChoice.has("text")) {
                    String text = firstChoice.getString("text");
                    return text != null && !text.trim().isEmpty() ? text.trim() : "";
                }
                throw new ApiException("API响应格式异常，缺少message字段");
            }
            
            JSONObject message = firstChoice.getJSONObject("message");
            
            // 提取content，处理可能为空的情况
            String content = null;
            if (message.has("content")) {
                Object contentObj = message.get("content");
                if (contentObj != null) {
                    content = contentObj.toString().trim();
                }
            }
            
            // 如果content为空，尝试其他字段
            if (content == null || content.isEmpty()) {
                // 优先尝试reasoning_content字段（某些模型如Gemini会使用此字段）
                if (message.has("reasoning_content")) {
                    Object reasoningObj = message.get("reasoning_content");
                    if (reasoningObj != null) {
                        content = reasoningObj.toString().trim();
                    }
                }
                // 如果reasoning_content也为空，尝试text字段
                if ((content == null || content.isEmpty()) && message.has("text")) {
                    content = message.getString("text").trim();
                }
                // 如果所有字段都为空，抛出异常
                if (content == null || content.isEmpty()) {
                    // 返回详细的错误信息，包含原始响应
                    String debugInfo = "响应结构: " + json.toString().substring(0, Math.min(500, json.toString().length()));
                    throw new ApiException("API返回空内容。message对象: " + message.toString() + "\n" + debugInfo);
                }
            }
            
            return content;
            
        } catch (org.json.JSONException e) {
            // JSON解析失败，返回更详细的错误信息
            String debugInfo = responseBody.length() > 500 
                ? responseBody.substring(0, 500) + "..." 
                : responseBody;
            throw new ApiException("解析响应失败: " + e.getMessage() + "\n响应内容: " + debugInfo, e);
        }
    }
    
    /**
     * 更新配置
     * @param config 新的配置
     */
    public void updateConfig(ApiConfig config) {
        this.config = config;
    }
}


