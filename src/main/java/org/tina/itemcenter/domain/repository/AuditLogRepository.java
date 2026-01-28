package org.tina.itemcenter.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tina.itemcenter.domain.model.AuditLog;

import java.util.List;

/**
 * AuditLog Repository 接口
 * 
 * 所有查询方法自动注入场景 ID，实现场景隔离
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * 查询当前场景下的所有审计日志（分页）
     * 
     * 自动注入场景 ID，只返回当前场景的审计日志
     * 按操作时间倒序排列
     * 
     * @param pageable 分页参数
     * @return 当前场景下的审计日志分页结果
     */
    @Query("SELECT a FROM AuditLog a WHERE a.sceneId = :#{T(org.tina.itemcenter.common.context.SceneContext).getSceneId()} ORDER BY a.operatedAt DESC")
    Page<AuditLog> findAll(Pageable pageable);
    
    /**
     * 根据实体类型和实体 ID 查询审计日志
     * 
     * 自动注入场景 ID，只返回当前场景的审计日志
     * 按操作时间倒序排列
     * 
     * @param entityType 实体类型（ITEM 或 LOCATION）
     * @param entityId 实体 ID
     * @return 指定实体的审计日志列表
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId " +
           "AND a.sceneId = :#{T(org.tina.itemcenter.common.context.SceneContext).getSceneId()} ORDER BY a.operatedAt DESC")
    List<AuditLog> findByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);
}
