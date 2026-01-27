package org.tina.itemcenter.presentation.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tina.itemcenter.application.service.SceneService;
import org.tina.itemcenter.common.response.ApiResponse;
import org.tina.itemcenter.presentation.dto.CreateSceneRequest;
import org.tina.itemcenter.presentation.dto.SceneDTO;

import java.util.List;

/**
 * 场景管理 Controller
 * 提供场景的创建、查询等 API 接口
 * 
 * 注意：场景管理接口不需要 X-Scene-Id Header，因为场景本身就是数据隔离的基础
 */
@RestController
@RequestMapping("/api/scenes")
public class SceneController {
    
    private final SceneService sceneService;
    
    public SceneController(SceneService sceneService) {
        this.sceneService = sceneService;
    }
    
    /**
     * 创建场景
     * POST /api/scenes
     * 
     * @param request 创建场景请求
     * @return 创建的场景信息
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SceneDTO>> createScene(@Valid @RequestBody CreateSceneRequest request) {
        SceneDTO scene = sceneService.createScene(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(scene, "场景创建成功"));
    }
    
    /**
     * 查询所有场景
     * GET /api/scenes
     * 
     * @return 场景列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SceneDTO>>> getAllScenes() {
        List<SceneDTO> scenes = sceneService.getAllScenes();
        return ResponseEntity.ok(ApiResponse.success(scenes, "查询成功"));
    }
    
    /**
     * 根据 ID 查询场景
     * GET /api/scenes/{id}
     * 
     * @param id 场景 ID
     * @return 场景信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SceneDTO>> getSceneById(@PathVariable Long id) {
        SceneDTO scene = sceneService.getSceneById(id);
        return ResponseEntity.ok(ApiResponse.success(scene, "查询成功"));
    }
}
