package org.tina.itemcenter.common.exception;

/**
 * 非法场景 ID 异常
 * 当 X-Scene-Id Header 格式非法时抛出
 */
public class InvalidSceneIdException extends BusinessException {
    
    public InvalidSceneIdException(String message) {
        super("INVALID_SCENE_ID", message);
    }
}
