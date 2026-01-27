package org.tina.itemcenter.common.context;

import org.tina.itemcenter.common.exception.SceneContextException;

/**
 * 场景上下文管理类
 * 使用 ThreadLocal 管理当前请求的场景 ID
 * 确保场景 ID 在整个请求链路中可用，无需层层传递参数
 */
public class SceneContext {
    
    private static final ThreadLocal<Long> SCENE_ID = new ThreadLocal<>();
    
    /**
     * 设置当前场景 ID
     * @param sceneId 场景 ID
     */
    public static void setSceneId(Long sceneId) {
        SCENE_ID.set(sceneId);
    }
    
    /**
     * 获取当前场景 ID
     * @return 场景 ID
     * @throws SceneContextException 如果场景 ID 未设置
     */
    public static Long getSceneId() {
        Long sceneId = SCENE_ID.get();
        if (sceneId == null) {
            throw new SceneContextException("Scene ID not found in context");
        }
        return sceneId;
    }
    
    /**
     * 清理当前场景 ID
     * 必须在请求处理结束后调用，避免内存泄漏和线程污染
     */
    public static void clear() {
        SCENE_ID.remove();
    }
    
    /**
     * 私有构造函数，禁止实例化
     */
    private SceneContext() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
