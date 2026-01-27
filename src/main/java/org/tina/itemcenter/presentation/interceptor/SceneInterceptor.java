package org.tina.itemcenter.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.common.exception.InvalidSceneIdException;
import org.tina.itemcenter.common.exception.MissingSceneIdException;

/**
 * 场景拦截器
 * 拦截所有 HTTP 请求，提取并设置场景 ID
 * 确保所有业务操作都在正确的场景上下文中执行
 */
@Component
public class SceneInterceptor implements HandlerInterceptor {
    
    private static final String SCENE_ID_HEADER = "X-Scene-Id";
    
    /**
     * 请求处理前执行
     * 从 Header 提取场景 ID 并设置到 SceneContext
     */
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        String sceneIdHeader = request.getHeader(SCENE_ID_HEADER);
        
        if (sceneIdHeader == null || sceneIdHeader.isEmpty()) {
            throw new MissingSceneIdException("X-Scene-Id header is required");
        }
        
        try {
            Long sceneId = Long.parseLong(sceneIdHeader);
            SceneContext.setSceneId(sceneId);
            return true;
        } catch (NumberFormatException e) {
            throw new InvalidSceneIdException("Invalid X-Scene-Id format: must be a valid number");
        }
    }
    
    /**
     * 请求处理完成后执行
     * 清理 SceneContext，避免内存泄漏和线程污染
     */
    @Override
    public void afterCompletion(HttpServletRequest request, 
                               HttpServletResponse response, 
                               Object handler, 
                               Exception ex) {
        SceneContext.clear();
    }
}
