package org.tina.itemcenter;

import net.jqwik.api.*;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.tina.itemcenter.common.exception.EntityNotFoundException;
import org.tina.itemcenter.common.exception.InvalidOperationException;
import org.tina.itemcenter.common.exception.InvalidSceneIdException;
import org.tina.itemcenter.common.exception.MissingSceneIdException;
import org.tina.itemcenter.common.response.ApiResponse;
import org.tina.itemcenter.presentation.exception.GlobalExceptionHandler;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * GlobalExceptionHandler 的属性测试
 * 
 * 验证属性 24：统一错误处理
 * 验证需求：23.1-23.6
 * 
 * 对于任意业务异常或参数校验失败，系统必须返回统一的错误响应格式，
 * 包含 success（false）、message 和 timestamp 字段，并返回适当的 HTTP 状态码。
 * 
 * Feature: item-management-system, Property 24: 统一错误处理
 */
public class GlobalExceptionHandlerPropertyTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    /**
     * 属性 24：MissingSceneIdException 必须返回统一错误格式
     * 
     * 验证需求：23.1-23.6
     * 
     * 对于任意 MissingSceneIdException，必须返回：
     * - HTTP 状态码 400 (BAD_REQUEST)
     * - success 字段为 false
     * - message 字段包含错误信息
     * - timestamp 字段不为 null
     */
    @Property(tries = 100)
    void missingSceneIdException_MustReturnUnifiedErrorFormat(
            @ForAll @StringLength(min = 1, max = 100) String errorMessage
    ) {
        // 创建异常
        MissingSceneIdException exception = new MissingSceneIdException(errorMessage);
        
        // 调用异常处理器
        ResponseEntity<ApiResponse<Void>> response = handler.handleMissingSceneId(exception);
        
        // 验证 HTTP 状态码
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), 
                "HTTP status must be 400 BAD_REQUEST");
        
        // 验证响应体
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body, "Response body must not be null");
        
        // 验证统一错误格式
        assertFalse(body.isSuccess(), "Success field must be false for error response");
        assertNull(body.getData(), "Data field must be null for error response");
        assertNotNull(body.getMessage(), "Message field must not be null");
        assertEquals(errorMessage, body.getMessage(), "Message must match exception message");
        assertNotNull(body.getTimestamp(), "Timestamp field must not be null");
    }

    /**
     * 属性 24：InvalidSceneIdException 必须返回统一错误格式
     * 
     * 验证需求：23.1-23.6
     */
    @Property(tries = 100)
    void invalidSceneIdException_MustReturnUnifiedErrorFormat(
            @ForAll @StringLength(min = 1, max = 100) String errorMessage
    ) {
        InvalidSceneIdException exception = new InvalidSceneIdException(errorMessage);
        ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidSceneId(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertNull(body.getData());
        assertEquals(errorMessage, body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    /**
     * 属性 24：EntityNotFoundException 必须返回统一错误格式
     * 
     * 验证需求：23.1-23.6
     * 
     * 对于任意 EntityNotFoundException，必须返回：
     * - HTTP 状态码 404 (NOT_FOUND)
     * - 统一错误响应格式
     */
    @Property(tries = 100)
    void entityNotFoundException_MustReturnUnifiedErrorFormat(
            @ForAll @StringLength(min = 1, max = 20) String entityType,
            @ForAll @LongRange(min = 1, max = 10000) Long entityId
    ) {
        EntityNotFoundException exception = new EntityNotFoundException(entityType, entityId);
        ResponseEntity<ApiResponse<Void>> response = handler.handleEntityNotFound(exception);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), 
                "HTTP status must be 404 NOT_FOUND");
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertNull(body.getData());
        assertNotNull(body.getMessage());
        assertTrue(body.getMessage().contains(entityType), 
                "Message must contain entity type");
        assertTrue(body.getMessage().contains(entityId.toString()), 
                "Message must contain entity ID");
        assertNotNull(body.getTimestamp());
    }

    /**
     * 属性 24：InvalidOperationException 必须返回统一错误格式
     * 
     * 验证需求：23.1-23.6
     */
    @Property(tries = 100)
    void invalidOperationException_MustReturnUnifiedErrorFormat(
            @ForAll @StringLength(min = 1, max = 100) String errorMessage
    ) {
        InvalidOperationException exception = new InvalidOperationException(errorMessage);
        ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidOperation(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertNull(body.getData());
        assertEquals(errorMessage, body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    /**
     * 属性 24：IllegalArgumentException 必须返回统一错误格式
     * 
     * 验证需求：23.1-23.6
     */
    @Property(tries = 100)
    void illegalArgumentException_MustReturnUnifiedErrorFormat(
            @ForAll @StringLength(min = 1, max = 100) String errorMessage
    ) {
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
        ResponseEntity<ApiResponse<Void>> response = handler.handleIllegalArgument(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertNull(body.getData());
        assertEquals(errorMessage, body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    /**
     * 属性 24：IllegalStateException 必须返回统一错误格式
     * 
     * 验证需求：23.1-23.6
     */
    @Property(tries = 100)
    void illegalStateException_MustReturnUnifiedErrorFormat(
            @ForAll @StringLength(min = 1, max = 100) String errorMessage
    ) {
        IllegalStateException exception = new IllegalStateException(errorMessage);
        ResponseEntity<ApiResponse<Void>> response = handler.handleIllegalState(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertNull(body.getData());
        assertEquals(errorMessage, body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    /**
     * 属性 24：通用 Exception 必须返回统一错误格式
     * 
     * 验证需求：23.1-23.6
     * 
     * 对于任意未预期的异常，必须返回：
     * - HTTP 状态码 500 (INTERNAL_SERVER_ERROR)
     * - 统一错误响应格式
     * - 通用错误消息（不暴露内部细节）
     */
    @Property(tries = 100)
    void genericException_MustReturnUnifiedErrorFormat(
            @ForAll @StringLength(min = 1, max = 100) String errorMessage
    ) {
        Exception exception = new Exception(errorMessage);
        ResponseEntity<ApiResponse<Void>> response = handler.handleGenericException(exception);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), 
                "HTTP status must be 500 INTERNAL_SERVER_ERROR");
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertNull(body.getData());
        assertNotNull(body.getMessage());
        assertEquals("系统内部错误", body.getMessage(), 
                "Generic exception must return generic error message");
        assertNotNull(body.getTimestamp());
    }

    /**
     * 属性 24：MethodArgumentNotValidException 必须返回统一错误格式
     * 
     * 验证需求：23.1-23.6
     * 
     * 对于任意参数校验失败，必须返回：
     * - HTTP 状态码 400 (BAD_REQUEST)
     * - success 字段为 false
     * - data 字段包含字段错误详情（Map<String, String>）
     * - message 字段包含通用错误消息
     * - timestamp 字段不为 null
     */
    @Property(tries = 100)
    void validationException_MustReturnUnifiedErrorFormat(
            @ForAll @StringLength(min = 1, max = 20) String fieldName,
            @ForAll @StringLength(min = 1, max = 50) String errorMessage
    ) {
        // 创建 mock MethodArgumentNotValidException
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", fieldName, errorMessage);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        // 调用异常处理器
        ResponseEntity<ApiResponse<Map<String, String>>> response = 
                handler.handleValidationException(exception);
        
        // 验证 HTTP 状态码
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        // 验证响应体
        ApiResponse<Map<String, String>> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertNotNull(body.getData(), "Data field must contain validation errors");
        assertTrue(body.getData().containsKey(fieldName), 
                "Data must contain field name");
        assertEquals(errorMessage, body.getData().get(fieldName), 
                "Data must contain field error message");
        assertEquals("参数校验失败", body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    /**
     * 属性 24：所有异常处理必须返回一致的响应结构
     * 
     * 验证需求：23.1-23.6
     * 
     * 验证所有异常处理方法都返回相同的响应结构。
     */
    @Property(tries = 100)
    void allExceptions_MustReturnConsistentStructure(
            @ForAll @StringLength(min = 1, max = 100) String errorMessage
    ) {
        // 测试多种异常类型
        ResponseEntity<ApiResponse<Void>> response1 = 
                handler.handleMissingSceneId(new MissingSceneIdException(errorMessage));
        ResponseEntity<ApiResponse<Void>> response2 = 
                handler.handleInvalidSceneId(new InvalidSceneIdException(errorMessage));
        ResponseEntity<ApiResponse<Void>> response3 = 
                handler.handleInvalidOperation(new InvalidOperationException(errorMessage));
        ResponseEntity<ApiResponse<Void>> response4 = 
                handler.handleIllegalArgument(new IllegalArgumentException(errorMessage));
        ResponseEntity<ApiResponse<Void>> response5 = 
                handler.handleGenericException(new Exception(errorMessage));
        
        // 验证所有响应都有相同的结构
        assertNotNull(response1.getBody());
        assertNotNull(response2.getBody());
        assertNotNull(response3.getBody());
        assertNotNull(response4.getBody());
        assertNotNull(response5.getBody());
        
        // 验证所有响应的 success 字段都为 false
        assertFalse(response1.getBody().isSuccess());
        assertFalse(response2.getBody().isSuccess());
        assertFalse(response3.getBody().isSuccess());
        assertFalse(response4.getBody().isSuccess());
        assertFalse(response5.getBody().isSuccess());
        
        // 验证所有响应都有 timestamp
        assertNotNull(response1.getBody().getTimestamp());
        assertNotNull(response2.getBody().getTimestamp());
        assertNotNull(response3.getBody().getTimestamp());
        assertNotNull(response4.getBody().getTimestamp());
        assertNotNull(response5.getBody().getTimestamp());
    }
}
