package org.tina.itemcenter;

import net.jqwik.api.*;
import net.jqwik.api.constraints.StringLength;
import org.tina.itemcenter.common.response.ApiResponse;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ApiResponse 的属性测试
 * 
 * 验证属性 23：统一响应格式
 * 验证需求：24.1-24.5
 * 
 * 对于任意成功的 API 响应，必须包含 success（true）、data、message 和 timestamp 字段。
 * 
 * Feature: item-management-system, Property 23: 统一响应格式
 */
public class ApiResponsePropertyTest {

    /**
     * 属性 23：成功响应必须包含所有必需字段
     * 
     * 验证需求：24.1-24.5
     * 
     * 对于任意成功响应，必须包含：
     * - success 字段为 true
     * - data 字段包含业务数据
     * - message 字段包含提示信息
     * - timestamp 字段包含响应时间
     */
    @Property(tries = 100)
    void successResponse_MustContainAllRequiredFields(
            @ForAll @StringLength(min = 1, max = 100) String data
    ) {
        // 创建成功响应
        ApiResponse<String> response = ApiResponse.success(data);
        
        // 验证 success 字段为 true
        assertTrue(response.isSuccess(), "Success field must be true for success response");
        
        // 验证 data 字段不为 null
        assertNotNull(response.getData(), "Data field must not be null");
        assertEquals(data, response.getData(), "Data field must match the provided data");
        
        // 验证 message 字段不为 null
        assertNotNull(response.getMessage(), "Message field must not be null");
        assertFalse(response.getMessage().isEmpty(), "Message field must not be empty");
        
        // 验证 timestamp 字段不为 null
        assertNotNull(response.getTimestamp(), "Timestamp field must not be null");
        
        // 验证 timestamp 是最近的时间（在过去 1 秒内）
        LocalDateTime now = LocalDateTime.now();
        long secondsDiff = ChronoUnit.SECONDS.between(response.getTimestamp(), now);
        assertTrue(Math.abs(secondsDiff) <= 1, 
                "Timestamp must be recent (within 1 second), but was " + secondsDiff + " seconds ago");
    }
    
    /**
     * 属性 23：成功响应（带自定义消息）必须包含所有必需字段
     * 
     * 验证需求：24.1-24.5
     */
    @Property(tries = 100)
    void successResponseWithMessage_MustContainAllRequiredFields(
            @ForAll @StringLength(min = 1, max = 100) String data,
            @ForAll @StringLength(min = 1, max = 50) String message
    ) {
        // 创建成功响应（带自定义消息）
        ApiResponse<String> response = ApiResponse.success(data, message);
        
        // 验证所有字段
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(data, response.getData());
        assertNotNull(response.getMessage());
        assertEquals(message, response.getMessage());
        assertNotNull(response.getTimestamp());
    }
    
    /**
     * 属性 23：错误响应必须包含所有必需字段
     * 
     * 验证需求：24.1-24.5
     * 
     * 对于任意错误响应，必须包含：
     * - success 字段为 false
     * - data 字段为 null（或错误详情）
     * - message 字段包含错误信息
     * - timestamp 字段包含响应时间
     */
    @Property(tries = 100)
    void errorResponse_MustContainAllRequiredFields(
            @ForAll @StringLength(min = 1, max = 100) String errorMessage
    ) {
        // 创建错误响应
        ApiResponse<Void> response = ApiResponse.error(errorMessage);
        
        // 验证 success 字段为 false
        assertFalse(response.isSuccess(), "Success field must be false for error response");
        
        // 验证 data 字段为 null
        assertNull(response.getData(), "Data field must be null for error response");
        
        // 验证 message 字段不为 null
        assertNotNull(response.getMessage(), "Message field must not be null");
        assertEquals(errorMessage, response.getMessage(), "Message field must match the error message");
        
        // 验证 timestamp 字段不为 null
        assertNotNull(response.getTimestamp(), "Timestamp field must not be null");
        
        // 验证 timestamp 是最近的时间
        LocalDateTime now = LocalDateTime.now();
        long secondsDiff = ChronoUnit.SECONDS.between(response.getTimestamp(), now);
        assertTrue(Math.abs(secondsDiff) <= 1, "Timestamp must be recent");
    }
    
    /**
     * 属性 23：错误响应（带数据）必须包含所有必需字段
     * 
     * 验证需求：24.1-24.5
     */
    @Property(tries = 100)
    void errorResponseWithData_MustContainAllRequiredFields(
            @ForAll @StringLength(min = 1, max = 100) String errorData,
            @ForAll @StringLength(min = 1, max = 50) String errorMessage
    ) {
        // 创建错误响应（带数据）
        ApiResponse<String> response = ApiResponse.error(errorData, errorMessage);
        
        // 验证所有字段
        assertFalse(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(errorData, response.getData());
        assertNotNull(response.getMessage());
        assertEquals(errorMessage, response.getMessage());
        assertNotNull(response.getTimestamp());
    }
    
    /**
     * 属性 23：响应格式一致性
     * 
     * 验证需求：24.1-24.5
     * 
     * 验证成功响应和错误响应都遵循相同的结构。
     */
    @Property(tries = 100)
    void responseFormat_MustBeConsistent(
            @ForAll @StringLength(min = 1, max = 100) String data,
            @ForAll @StringLength(min = 1, max = 50) String message
    ) {
        // 创建成功响应
        ApiResponse<String> successResponse = ApiResponse.success(data, message);
        
        // 创建错误响应
        ApiResponse<String> errorResponse = ApiResponse.error(data, message);
        
        // 验证两者都有相同的字段结构
        assertNotNull(successResponse.getMessage());
        assertNotNull(successResponse.getTimestamp());
        
        assertNotNull(errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
        
        // 验证 success 字段的值不同
        assertTrue(successResponse.isSuccess());
        assertFalse(errorResponse.isSuccess());
    }
    
    /**
     * 属性 23：null 数据的成功响应
     * 
     * 验证需求：24.1-24.5
     * 
     * 验证即使数据为 null，响应格式仍然正确。
     */
    @Property(tries = 100)
    void successResponseWithNullData_MustBeValid() {
        // 创建 null 数据的成功响应
        ApiResponse<String> response = ApiResponse.success(null);
        
        // 验证响应格式正确
        assertTrue(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getMessage());
        assertNotNull(response.getTimestamp());
    }
    
    /**
     * 属性 23：空字符串消息的响应
     * 
     * 验证需求：24.1-24.5
     * 
     * 验证空字符串消息的响应仍然有效。
     */
    @Property(tries = 100)
    void responseWithEmptyMessage_MustBeValid(
            @ForAll @StringLength(min = 1, max = 100) String data
    ) {
        // 创建空消息的成功响应
        ApiResponse<String> response = ApiResponse.success(data, "");
        
        // 验证响应格式正确
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertNotNull(response.getMessage());
        assertEquals("", response.getMessage());
        assertNotNull(response.getTimestamp());
    }
}
