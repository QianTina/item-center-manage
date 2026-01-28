package org.tina.itemcenter;

import net.jqwik.api.*;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.common.exception.InvalidSceneIdException;
import org.tina.itemcenter.common.exception.MissingSceneIdException;
import org.tina.itemcenter.presentation.interceptor.SceneInterceptor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SceneInterceptor 的属性测试
 * 
 * 使用 jqwik 进行基于属性的测试，验证 SceneInterceptor 的正确性属性
 */
public class SceneInterceptorPropertyTest {

    private final SceneInterceptor interceptor = new SceneInterceptor();

    /**
     * 属性 6：场景 Header 强制要求
     * 
     * 验证需求：2.2, 2.3
     * 
     * 对于任意 API 请求，如果缺少 X-Scene-Id Header 或 Header 值非法，
     * 系统必须拒绝请求并返回 400 错误。
     * 
     * 测试策略：
     * 1. 测试缺少 X-Scene-Id Header 时抛出 MissingSceneIdException
     * 2. 测试 X-Scene-Id Header 为空字符串时抛出 MissingSceneIdException
     * 3. 测试 X-Scene-Id Header 为非数字时抛出 InvalidSceneIdException
     * 4. 测试 X-Scene-Id Header 为有效数字时成功设置场景 ID
     * 
     * Feature: item-management-system, Property 6: 场景 Header 强制要求
     */
    @Property(tries = 100)
    void missingSceneIdHeader_MustThrowException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 不设置 X-Scene-Id Header
        
        // 验证抛出 MissingSceneIdException
        assertThrows(MissingSceneIdException.class, () -> {
            interceptor.preHandle(request, response, new Object());
        }, "Missing X-Scene-Id header must throw MissingSceneIdException");
    }
    
    /**
     * 属性 6：空 Header 值必须抛出异常
     * 
     * 验证需求：2.2, 2.3
     */
    @Property(tries = 100)
    void emptySceneIdHeader_MustThrowException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 设置空字符串 Header
        request.addHeader("X-Scene-Id", "");
        
        // 验证抛出 MissingSceneIdException
        assertThrows(MissingSceneIdException.class, () -> {
            interceptor.preHandle(request, response, new Object());
        }, "Empty X-Scene-Id header must throw MissingSceneIdException");
        
        // 清理
        SceneContext.clear();
    }
    
    /**
     * 属性 6：非数字 Header 值必须抛出异常
     * 
     * 验证需求：2.2, 2.3
     * 
     * 测试各种非数字字符串作为 X-Scene-Id Header 值。
     */
    @Property(tries = 100)
    void invalidSceneIdHeader_MustThrowException(
            @ForAll @StringLength(min = 1, max = 20) String invalidSceneId
    ) {
        // 跳过可以解析为数字的字符串
        try {
            Long.parseLong(invalidSceneId);
            return; // 如果可以解析为数字，跳过此测试
        } catch (NumberFormatException e) {
            // 这是我们想要测试的情况
        }
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 设置非数字 Header
        request.addHeader("X-Scene-Id", invalidSceneId);
        
        // 验证抛出 InvalidSceneIdException
        assertThrows(InvalidSceneIdException.class, () -> {
            interceptor.preHandle(request, response, new Object());
        }, "Invalid X-Scene-Id header must throw InvalidSceneIdException");
        
        // 清理
        SceneContext.clear();
    }
    
    /**
     * 属性 6：有效的场景 ID 必须成功设置
     * 
     * 验证需求：2.2, 2.3
     * 
     * 测试各种有效的数字作为 X-Scene-Id Header 值。
     */
    @Property(tries = 100)
    void validSceneIdHeader_MustSetSceneContext(
            @ForAll @LongRange(min = 1, max = 1000000) Long sceneId
    ) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 设置有效的场景 ID
        request.addHeader("X-Scene-Id", sceneId.toString());
        
        try {
            // 调用 preHandle
            boolean result = interceptor.preHandle(request, response, new Object());
            
            // 验证返回 true
            assertTrue(result, "preHandle must return true for valid scene ID");
            
            // 验证场景 ID 被正确设置到 SceneContext
            Long retrievedSceneId = SceneContext.getSceneId();
            assertEquals(sceneId, retrievedSceneId, 
                    "Scene ID in context must match the header value");
        } finally {
            // 清理
            SceneContext.clear();
        }
    }
    
    /**
     * 属性 6：负数场景 ID 也应该被接受
     * 
     * 验证需求：2.2, 2.3
     * 
     * 虽然业务上场景 ID 通常是正数，但拦截器层面应该接受任何有效的 Long 值。
     */
    @Property(tries = 100)
    void negativeSceneId_MustBeAccepted(
            @ForAll @LongRange(min = -1000000, max = -1) Long negativeSceneId
    ) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        request.addHeader("X-Scene-Id", negativeSceneId.toString());
        
        try {
            boolean result = interceptor.preHandle(request, response, new Object());
            assertTrue(result);
            assertEquals(negativeSceneId, SceneContext.getSceneId());
        } finally {
            SceneContext.clear();
        }
    }
    
    /**
     * 属性 6：零作为场景 ID 应该被接受
     * 
     * 验证需求：2.2, 2.3
     */
    @Property(tries = 100)
    void zeroSceneId_MustBeAccepted() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        request.addHeader("X-Scene-Id", "0");
        
        try {
            boolean result = interceptor.preHandle(request, response, new Object());
            assertTrue(result);
            assertEquals(0L, SceneContext.getSceneId());
        } finally {
            SceneContext.clear();
        }
    }
    
    /**
     * 属性 25：afterCompletion 必须清理 SceneContext
     * 
     * 验证需求：2.5
     * 
     * 验证 afterCompletion 方法会清理 SceneContext。
     */
    @Property(tries = 100)
    void afterCompletion_MustClearSceneContext(
            @ForAll @LongRange(min = 1, max = 1000000) Long sceneId
    ) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 设置场景 ID
        request.addHeader("X-Scene-Id", sceneId.toString());
        
        try {
            // 调用 preHandle 设置场景 ID
            interceptor.preHandle(request, response, new Object());
            
            // 验证场景 ID 已设置
            assertEquals(sceneId, SceneContext.getSceneId());
            
            // 调用 afterCompletion 清理
            interceptor.afterCompletion(request, response, new Object(), null);
            
            // 验证场景 ID 已被清理（获取时应抛出异常）
            assertThrows(org.tina.itemcenter.common.exception.SceneContextException.class, () -> {
                SceneContext.getSceneId();
            }, "Scene ID must be cleared after afterCompletion");
        } finally {
            // 确保清理
            SceneContext.clear();
        }
    }
    
    /**
     * 属性 6：包含空格的数字字符串应该抛出异常
     * 
     * 验证需求：2.2, 2.3
     */
    @Property(tries = 100)
    void sceneIdWithWhitespace_MustThrowException(
            @ForAll @LongRange(min = 1, max = 1000) Long sceneId
    ) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 在数字前后添加空格
        request.addHeader("X-Scene-Id", " " + sceneId + " ");
        
        // Long.parseLong() 不会自动 trim，所以应该抛出异常
        assertThrows(InvalidSceneIdException.class, () -> {
            interceptor.preHandle(request, response, new Object());
        }, "Scene ID with whitespace must throw InvalidSceneIdException");
        
        SceneContext.clear();
    }
    
    /**
     * 属性 6：超出 Long 范围的数字应该抛出异常
     * 
     * 验证需求：2.2, 2.3
     */
    @Property(tries = 100)
    void sceneIdOutOfLongRange_MustThrowException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 超出 Long.MAX_VALUE 的数字
        request.addHeader("X-Scene-Id", "9223372036854775808"); // Long.MAX_VALUE + 1
        
        assertThrows(InvalidSceneIdException.class, () -> {
            interceptor.preHandle(request, response, new Object());
        }, "Scene ID out of Long range must throw InvalidSceneIdException");
        
        SceneContext.clear();
    }
    
    /**
     * 属性 6：小数应该抛出异常
     * 
     * 验证需求：2.2, 2.3
     */
    @Property(tries = 100)
    void decimalSceneId_MustThrowException(
            @ForAll @LongRange(min = 1, max = 1000) Long integerPart
    ) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 设置小数
        request.addHeader("X-Scene-Id", integerPart + ".5");
        
        assertThrows(InvalidSceneIdException.class, () -> {
            interceptor.preHandle(request, response, new Object());
        }, "Decimal scene ID must throw InvalidSceneIdException");
        
        SceneContext.clear();
    }
}
