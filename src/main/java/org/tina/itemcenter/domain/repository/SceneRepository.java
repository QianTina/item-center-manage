package org.tina.itemcenter.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tina.itemcenter.domain.model.Scene;

/**
 * 场景仓储接口
 * 
 * 注意：Scene 不受场景隔离约束，因为它本身就是场景的定义
 * 所有场景都可以被查询，不需要自动注入 scene_id 过滤条件
 */
@Repository
public interface SceneRepository extends JpaRepository<Scene, Long> {
    
    // JpaRepository 已经提供了基本的 CRUD 方法：
    // - save(Scene) - 保存或更新场景
    // - findById(Long) - 根据 ID 查询场景
    // - findAll() - 查询所有场景
    // - deleteById(Long) - 删除场景
    // - count() - 统计场景数量
    
    // 如果需要自定义查询方法，可以在这里添加
    // 例如：根据所有者查询场景
    // List<Scene> findByOwner(String owner);
}
