package org.tina.itemcenter.common.exception;

/**
 * 场景上下文异常
 * 当场景 ID 未设置或访问场景上下文失败时抛出
 */
public class SceneContextException extends BusinessException {
    
    public SceneContextException(String message) {
        super("SCENE_CONTEXT_ERROR", message);
    }
    
    public SceneContextException(String message, Throwable cause) {
        super("SCENE_CONTEXT_ERROR", message, cause);
    }
}
