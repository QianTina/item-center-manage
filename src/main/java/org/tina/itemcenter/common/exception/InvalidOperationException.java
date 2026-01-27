package org.tina.itemcenter.common.exception;

/**
 * 非法操作异常
 * 当业务操作违反业务规则时抛出
 */
public class InvalidOperationException extends BusinessException {
    
    public InvalidOperationException(String message) {
        super("INVALID_OPERATION", message);
    }
}
