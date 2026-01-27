package org.tina.itemcenter.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.common.exception.EntityNotFoundException;
import org.tina.itemcenter.domain.model.Scene;
import org.tina.itemcenter.domain.repository.SceneRepository;
import org.tina.itemcenter.presentation.dto.CreateSceneRequest;
import org.tina.itemcenter.presentation.dto.SceneDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 场景管理应用服务
 * 负责场景的创建、查询等业务流程编排
 */
@Service
public class SceneService {
    
    private final SceneRepository sceneRepository;
    
    public SceneService(SceneRepository sceneRepository) {
        this.sceneRepository = sceneRepository;
    }
    
    /**
     * 创建场景
     * @param request 创建场景请求
     * @return 场景 DTO
     */
    @Transactional
    public SceneDTO createScene(CreateSceneRequest request) {
        // 创建场景实体
        Scene scene = new Scene(request.getName(), request.getOwner());
        
        // 保存到数据库
        Scene savedScene = sceneRepository.save(scene);
        
        // 转换为 DTO 返回
        return toDTO(savedScene);
    }
    
    /**
     * 查询所有场景
     * @return 场景列表
     */
    @Transactional(readOnly = true)
    public List<SceneDTO> getAllScenes() {
        List<Scene> scenes = sceneRepository.findAll();
        return scenes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据 ID 查询场景
     * @param id 场景 ID
     * @return 场景 DTO
     * @throws EntityNotFoundException 如果场景不存在
     */
    @Transactional(readOnly = true)
    public SceneDTO getSceneById(Long id) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scene", id));
        return toDTO(scene);
    }
    
    /**
     * 将 Scene 实体转换为 DTO
     * @param scene 场景实体
     * @return 场景 DTO
     */
    private SceneDTO toDTO(Scene scene) {
        return new SceneDTO(
                scene.getId(),
                scene.getName(),
                scene.getOwner(),
                scene.getCreatedAt(),
                scene.getUpdatedAt()
        );
    }
}
