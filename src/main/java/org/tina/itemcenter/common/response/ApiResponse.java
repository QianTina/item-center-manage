package org.tina.itemcenter.common.response;

import java.time.LocalDateTime;

/**
 * 统一响应格式
 * 所有 API 返回统一的响应结构
 * 
 * @param <T> 响应数据类型
 */
public class ApiResponse<T> {
    
    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;
    
    public ApiResponse(boolean success, T data, String message, LocalDateTime timestamp) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    /**
     * 创建成功响应
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "操作成功", LocalDateTime.now());
    }
    
    /**
     * 创建成功响应（带自定义消息）
     * @param data 响应数据
     * @param message 提示消息
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, LocalDateTime.now());
    }
    
    /**
     * 创建错误响应
     * @param message 错误消息
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, LocalDateTime.now());
    }
    
    /**
     * 创建错误响应（带数据）
     * @param data 错误数据（如参数校验错误详情）
     * @param message 错误消息
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(T data, String message) {
        return new ApiResponse<>(false, data, message, LocalDateTime.now());
    }
    
    // Getters and Setters
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
