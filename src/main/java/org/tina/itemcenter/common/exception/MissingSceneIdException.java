package org.tina.itemcenter.common.exception;

/**
 * 缺少场景 ID 异常
 * 当 HTTP 请求缺少 X-Scene-Id Header 时抛出
 */
public class MissingSceneIdException extends BusinessException {
    
    public MissingSceneIdException(String message) {
        super("MISSING_SCENE_ID", message);
    }
}
