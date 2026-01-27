package org.tina.itemcenter.presentation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.tina.itemcenter.presentation.interceptor.SceneInterceptor;

/**
 * Web MVC 配置类
 * 注册拦截器和其他 Web 相关配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final SceneInterceptor sceneInterceptor;
    
    public WebMvcConfig(SceneInterceptor sceneInterceptor) {
        this.sceneInterceptor = sceneInterceptor;
    }
    
    /**
     * 注册拦截器
     * SceneInterceptor 拦截所有请求，提取场景 ID
     * 排除场景管理接口，因为场景本身就是数据隔离的基础
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sceneInterceptor)
                .addPathPatterns("/api/**") // 拦截所有 API 请求
                .excludePathPatterns("/api/scenes/**"); // 排除场景管理接口
    }
}
