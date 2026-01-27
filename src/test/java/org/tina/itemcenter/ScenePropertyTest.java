package org.tina.itemcenter;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.application.service.SceneService;
import org.tina.itemcenter.domain.repository.SceneRepository;
import org.tina.itemcenter.presentation.dto.CreateSceneRequest;
import org.tina.itemcenter.presentation.dto.SceneDTO;

import java.util.HashSet;
import java.util.Set;

/**
 * 场景管理的属性测试
 * 
 * 使用 jqwik 进行基于属性的测试，验证场景管理的正确性属性
 */
@SpringBootTest
@ActiveProfiles("local")
public class ScenePropertyTest {

    @Autowired
    private SceneService sceneService;

    @Autowired
    private SceneRepository sceneRepository;

    /**
     * 属性 2：实体 ID 唯一性（场景部分）
     * 
     * 验证需求：1.3
     * 
     * 对于任意实体类型（场景、物品、位置），系统分配的 ID 在该实体类型内必须唯一。
     * 
     * 测试策略：
     * 1. 创建多个场景（随机数量）
     * 2. 验证所有场景的 ID 都是唯一的
     * 3. 验证 ID 都不为 null
     */
    @Property(tries = 100)
    @Tag("Feature: item-management-system, Property 2: 实体 ID 唯一性")
    @Transactional
    void sceneIdUniqueness_AllSceneIdsMustBeUnique(
            @ForAll @IntRange(min = 2, max = 20) int sceneCount,
            @ForAll("sceneNames") String[] sceneNames,
            @ForAll("ownerNames") String[] ownerNames
    ) {
        // 清理数据库
        sceneRepository.deleteAll();
        
        Set<Long> sceneIds = new HashSet<>();
        
        // 创建多个场景
        for (int i = 0; i < sceneCount; i++) {
            CreateSceneRequest request = new CreateSceneRequest();
            request.setName(sceneNames[i % sceneNames.length] + "_" + i);
            request.setOwner(ownerNames[i % ownerNames.length]);
            
            SceneDTO scene = sceneService.createScene(request);
            
            // 验证 ID 不为 null
            assert scene.getId() != null : "Scene ID must not be null";
            
            // 验证 ID 唯一性
            boolean isUnique = sceneIds.add(scene.getId());
            assert isUnique : "Scene ID " + scene.getId() + " is not unique";
        }
        
        // 验证创建的场景数量正确
        assert sceneIds.size() == sceneCount : 
            "Expected " + sceneCount + " unique scene IDs, but got " + sceneIds.size();
    }

    /**
     * 提供场景名称的生成器
     */
    @Provide
    Arbitrary<String[]> sceneNames() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(3)
                .ofMaxLength(20)
                .array(String[].class)
                .ofMinSize(5)
                .ofMaxSize(10);
    }

    /**
     * 提供所有者名称的生成器
     */
    @Provide
    Arbitrary<String[]> ownerNames() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(2)
                .ofMaxLength(15)
                .array(String[].class)
                .ofMinSize(3)
                .ofMaxSize(8);
    }
}
