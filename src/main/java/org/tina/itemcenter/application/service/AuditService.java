package org.tina.itemcenter.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.domain.model.AuditLog;
import org.tina.itemcenter.domain.repository.AuditLogRepository;

import java.util.List;
import java.util.Map;

/**
 * 审计日志服务
 * 
 * 负责记录和查询审计日志，支持以下功能：
 * - 记录关键操作的审计日志
 * - 分页查询审计日志
 * - 查询特定实体的历史记录
 * 
 * 应用服务层特点：
 * - 只做流程编排，不包含业务规则判断
 * - 使用 @Transactional 管理事务
 * - 自动从 SceneContext 获取场景 ID
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    
    /**
     * 记录审计日志
     * 
     * @param entityType 实体类型（ITEM 或 LOCATION）
     * @param entityId 实体 ID
     * @param operationType 操作类型（CREATE、UPDATE、DELETE、MOVE、BORROW、RETURN）
     * @param beforeState 操作前状态（JSON 格式）
     * @param afterState 操作后状态（JSON 格式）
     * @param operator 操作人（可选）
     */
    @Transactional
    public void log(String entityType, Long entityId, String operationType, 
                    Map<String, Object> beforeState, Map<String, Object> afterState, 
                    String operator) {
        AuditLog auditLog = new AuditLog();
        auditLog.setSceneId(SceneContext.getSceneId());
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOperationType(operationType);
        auditLog.setBeforeState(beforeState);
        auditLog.setAfterState(afterState);
        auditLog.setOperator(operator);
        
        auditLogRepository.save(auditLog);
        
        log.debug("审计日志已记录: entityType={}, entityId={}, operationType={}", 
                entityType, entityId, operationType);
    }
    
    /**
     * 记录审计日志（无操作人）
     * 
     * @param entityType 实体类型
     * @param entityId 实体 ID
     * @param operationType 操作类型
     * @param beforeState 操作前状态
     * @param afterState 操作后状态
     */
    @Transactional
    public void log(String entityType, Long entityId, String operationType, 
                    Map<String, Object> beforeState, Map<String, Object> afterState) {
        log(entityType, entityId, operationType, beforeState, afterState, null);
    }
    
    /**
     * 分页查询审计日志
     * 
     * 自动添加场景 ID 过滤条件，按操作时间倒序返回
     * 
     * @param pageable 分页参数
     * @return 审计日志分页结果
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
    
    /**
     * 查询实体的历史记录
     * 
     * 返回指定实体的所有审计日志，按操作时间倒序
     * 
     * @param entityType 实体类型（ITEM 或 LOCATION）
     * @param entityId 实体 ID
     * @return 实体的审计日志列表
     */
    @Transactional(readOnly = true)
    public List<AuditLog> findEntityHistory(String entityType, Long entityId) {
        return auditLogRepository.findByEntity(entityType, entityId);
    }
}
