package org.tina.itemcenter.presentation.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.tina.itemcenter.common.exception.EntityNotFoundException;
import org.tina.itemcenter.common.exception.InvalidOperationException;
import org.tina.itemcenter.common.exception.InvalidSceneIdException;
import org.tina.itemcenter.common.exception.MissingSceneIdException;
import org.tina.itemcenter.common.response.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理所有异常，返回统一的错误响应格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理缺少场景 ID 异常
     */
    @ExceptionHandler(MissingSceneIdException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingSceneId(MissingSceneIdException e) {
        log.warn("Missing scene ID: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 处理非法场景 ID 异常
     */
    @ExceptionHandler(InvalidSceneIdException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSceneId(InvalidSceneIdException e) {
        log.warn("Invalid scene ID: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 处理实体不存在异常
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException e) {
        log.warn("Entity not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 处理非法操作异常
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidOperation(InvalidOperationException e) {
        log.warn("Invalid operation: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errors, "参数校验失败"));
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 处理非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException e) {
        log.warn("Illegal state: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("系统内部错误"));
    }
}
