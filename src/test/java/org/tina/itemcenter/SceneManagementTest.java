package org.tina.itemcenter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.application.service.SceneService;
import org.tina.itemcenter.presentation.dto.CreateSceneRequest;
import org.tina.itemcenter.presentation.dto.SceneDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 场景管理功能测试
 */
@SpringBootTest
@Transactional
class SceneManagementTest {
    
    @Autowired
    private SceneService sceneService;
    
    @Test
    void testCreateScene_Success() {
        // 创建场景
        CreateSceneRequest request = new CreateSceneRequest("测试场景", "admin");
        SceneDTO scene = sceneService.createScene(request);
        
        // 验证
        assertNotNull(scene.getId());
        assertEquals("测试场景", scene.getName());
        assertEquals("admin", scene.getOwner());
        assertNotNull(scene.getCreatedAt());
        assertNotNull(scene.getUpdatedAt());
        
        System.out.println("✅ 场景创建成功：ID=" + scene.getId() + ", Name=" + scene.getName());
    }
    
    @Test
    void testGetAllScenes() {
        // 创建多个场景
        sceneService.createScene(new CreateSceneRequest("场景1", "user1"));
        sceneService.createScene(new CreateSceneRequest("场景2", "user2"));
        
        // 查询所有场景
        List<SceneDTO> scenes = sceneService.getAllScenes();
        
        // 验证
        assertTrue(scenes.size() >= 2);
        System.out.println("✅ 查询到 " + scenes.size() + " 个场景");
    }
    
    @Test
    void testGetSceneById() {
        // 创建场景
        CreateSceneRequest request = new CreateSceneRequest("测试场景", "admin");
        SceneDTO createdScene = sceneService.createScene(request);
        
        // 根据 ID 查询
        SceneDTO scene = sceneService.getSceneById(createdScene.getId());
        
        // 验证
        assertEquals(createdScene.getId(), scene.getId());
        assertEquals(createdScene.getName(), scene.getName());
        assertEquals(createdScene.getOwner(), scene.getOwner());
        
        System.out.println("✅ 根据 ID 查询场景成功");
    }
}
