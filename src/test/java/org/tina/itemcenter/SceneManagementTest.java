package org.tina.itemcenter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.application.service.SceneService;
import org.tina.itemcenter.common.exception.EntityNotFoundException;
import org.tina.itemcenter.presentation.dto.CreateSceneRequest;
import org.tina.itemcenter.presentation.dto.SceneDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 场景管理功能单元测试
 * 
 * 验证需求：1.1, 1.2, 1.4
 * 
 * 测试内容：
 * 1. 创建场景成功
 * 2. 查询场景列表
 * 3. 根据 ID 查询场景
 * 4. 缺少必填字段时创建失败
 * 5. 查询不存在的场景
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class SceneManagementTest {
    
    @Autowired
    private SceneService sceneService;
    
    /**
     * 测试创建场景成功
     * 
     * 验证需求：1.1, 1.2
     */
    @Test
    void testCreateScene_Success() {
        // 创建场景
        CreateSceneRequest request = new CreateSceneRequest("测试场景", "admin");
        SceneDTO scene = sceneService.createScene(request);
        
        // 验证
        assertNotNull(scene.getId(), "场景 ID 不应为 null");
        assertEquals("测试场景", scene.getName(), "场景名称应该匹配");
        assertEquals("admin", scene.getOwner(), "场景所有者应该匹配");
        assertNotNull(scene.getCreatedAt(), "创建时间不应为 null");
        assertNotNull(scene.getUpdatedAt(), "更新时间不应为 null");
        
        System.out.println("✅ 场景创建成功：ID=" + scene.getId() + ", Name=" + scene.getName());
    }
    
    /**
     * 测试查询场景列表
     * 
     * 验证需求：1.4
     */
    @Test
    void testGetAllScenes() {
        // 创建多个场景
        sceneService.createScene(new CreateSceneRequest("场景1", "user1"));
        sceneService.createScene(new CreateSceneRequest("场景2", "user2"));
        sceneService.createScene(new CreateSceneRequest("场景3", "user3"));
        
        // 查询所有场景
        List<SceneDTO> scenes = sceneService.getAllScenes();
        
        // 验证
        assertNotNull(scenes, "场景列表不应为 null");
        assertTrue(scenes.size() >= 3, "应该至少有 3 个场景");
        
        // 验证场景名称唯一性
        long uniqueNames = scenes.stream()
                .map(SceneDTO::getName)
                .distinct()
                .count();
        assertTrue(uniqueNames >= 3, "场景名称应该是唯一的");
        
        System.out.println("✅ 查询到 " + scenes.size() + " 个场景");
    }
    
    /**
     * 测试根据 ID 查询场景
     * 
     * 验证需求：1.4
     */
    @Test
    void testGetSceneById_Success() {
        // 创建场景
        CreateSceneRequest request = new CreateSceneRequest("测试场景", "admin");
        SceneDTO createdScene = sceneService.createScene(request);
        
        // 根据 ID 查询
        SceneDTO scene = sceneService.getSceneById(createdScene.getId());
        
        // 验证
        assertNotNull(scene, "查询结果不应为 null");
        assertEquals(createdScene.getId(), scene.getId(), "场景 ID 应该匹配");
        assertEquals(createdScene.getName(), scene.getName(), "场景名称应该匹配");
        assertEquals(createdScene.getOwner(), scene.getOwner(), "场景所有者应该匹配");
        
        System.out.println("✅ 根据 ID 查询场景成功");
    }
    
    /**
     * 测试查询不存在的场景
     * 
     * 验证需求：1.4
     */
    @Test
    void testGetSceneById_NotFound() {
        // 查询不存在的场景 ID
        Long nonExistentId = 999999L;
        
        // 验证抛出异常
        assertThrows(EntityNotFoundException.class, () -> {
            sceneService.getSceneById(nonExistentId);
        }, "查询不存在的场景应该抛出 EntityNotFoundException");
        
        System.out.println("✅ 查询不存在的场景正确抛出异常");
    }
    
    /**
     * 测试缺少必填字段（场景名称）时创建失败
     * 
     * 验证需求：1.2
     */
    @Test
    void testCreateScene_MissingName_ShouldFail() {
        // 创建缺少名称的请求
        CreateSceneRequest request = new CreateSceneRequest(null, "admin");
        
        // 验证抛出异常
        assertThrows(Exception.class, () -> {
            sceneService.createScene(request);
        }, "缺少场景名称应该抛出异常");
        
        System.out.println("✅ 缺少场景名称时创建失败");
    }
    
    /**
     * 测试缺少必填字段（所有者）时创建失败
     * 
     * 验证需求：1.2
     */
    @Test
    void testCreateScene_MissingOwner_ShouldFail() {
        // 创建缺少所有者的请求
        CreateSceneRequest request = new CreateSceneRequest("测试场景", null);
        
        // 验证抛出异常
        assertThrows(Exception.class, () -> {
            sceneService.createScene(request);
        }, "缺少场景所有者应该抛出异常");
        
        System.out.println("✅ 缺少场景所有者时创建失败");
    }
    
    /**
     * 测试空字符串名称时创建失败
     * 
     * 验证需求：1.2
     */
    @Test
    void testCreateScene_EmptyName_ShouldFail() {
        // 创建空名称的请求
        CreateSceneRequest request = new CreateSceneRequest("", "admin");
        
        // 验证抛出异常
        assertThrows(Exception.class, () -> {
            sceneService.createScene(request);
        }, "空字符串名称应该抛出异常");
        
        System.out.println("✅ 空字符串名称时创建失败");
    }
    
    /**
     * 测试创建场景后时间戳正确设置
     * 
     * 验证需求：1.1
     */
    @Test
    void testCreateScene_TimestampsSet() {
        // 创建场景
        CreateSceneRequest request = new CreateSceneRequest("测试场景", "admin");
        SceneDTO scene = sceneService.createScene(request);
        
        // 验证时间戳
        assertNotNull(scene.getCreatedAt(), "创建时间应该被设置");
        assertNotNull(scene.getUpdatedAt(), "更新时间应该被设置");
        assertEquals(scene.getCreatedAt(), scene.getUpdatedAt(), 
                "新创建的场景，创建时间和更新时间应该相同");
        
        System.out.println("✅ 场景时间戳正确设置");
    }
}
