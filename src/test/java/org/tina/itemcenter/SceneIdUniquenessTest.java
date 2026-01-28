package org.tina.itemcenter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.application.service.SceneService;
import org.tina.itemcenter.presentation.dto.CreateSceneRequest;
import org.tina.itemcenter.presentation.dto.SceneDTO;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 场景 ID 唯一性测试
 * 
 * 验证属性 2：实体 ID 唯一性（场景部分）
 * 验证需求：1.3
 * 
 * 对于任意实体类型（场景、物品、位置），系统分配的 ID 在该实体类型内必须唯一。
 * 
 * Feature: item-management-system, Property 2: 实体 ID 唯一性
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class SceneIdUniquenessTest {

    @Autowired
    private SceneService sceneService;

    /**
     * 测试场景 ID 唯一性 - 多次运行以模拟属性测试
     * 
     * 这个测试会运行 100 次，每次创建随机数量的场景，验证所有 ID 都是唯一的。
     */
    @Test
    void testSceneIdUniqueness_MultipleIterations() {
        Random random = new Random();
        
        // 运行 100 次迭代，模拟属性测试
        for (int iteration = 0; iteration < 100; iteration++) {
            // 随机生成场景数量（2-20个）
            int sceneCount = 2 + random.nextInt(19);
            
            Set<Long> sceneIds = new HashSet<>();
            
            // 创建多个场景
            for (int i = 0; i < sceneCount; i++) {
                String name = "Scene_" + iteration + "_" + i;
                String owner = "Owner_" + (i % 5);
                
                CreateSceneRequest request = new CreateSceneRequest(name, owner);
                SceneDTO scene = sceneService.createScene(request);
                
                // 验证 ID 不为 null
                assertNotNull(scene.getId(), "Scene ID must not be null");
                
                // 验证 ID 唯一性
                boolean isUnique = sceneIds.add(scene.getId());
                assertTrue(isUnique, "Scene ID " + scene.getId() + " is not unique in iteration " + iteration);
            }
            
            // 验证创建的场景数量正确
            assertEquals(sceneCount, sceneIds.size(), 
                    "Expected " + sceneCount + " unique scene IDs in iteration " + iteration);
        }
        
        System.out.println("✅ 场景 ID 唯一性测试通过（100 次迭代）");
    }
    
    /**
     * 测试大量场景创建时的 ID 唯一性
     */
    @Test
    void testSceneIdUniqueness_LargeScale() {
        Set<Long> sceneIds = new HashSet<>();
        int sceneCount = 100;
        
        // 创建 100 个场景
        for (int i = 0; i < sceneCount; i++) {
            CreateSceneRequest request = new CreateSceneRequest("Scene_" + i, "Owner");
            SceneDTO scene = sceneService.createScene(request);
            
            assertNotNull(scene.getId());
            assertTrue(sceneIds.add(scene.getId()), 
                    "Scene ID " + scene.getId() + " is not unique");
        }
        
        assertEquals(sceneCount, sceneIds.size());
        System.out.println("✅ 大规模场景 ID 唯一性测试通过（100 个场景）");
    }
}
