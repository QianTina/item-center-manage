package org.tina.itemcenter.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tina.itemcenter.infrastructure.converter.JsonbConverter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AuditLog 审计日志实体
 * 
 * 记录所有关键操作的历史记录，包括物品和位置的创建、更新、删除、移动、借出、归还等操作。
 * 
 * 领域模型特点：
 * - 不可变：审计日志一旦创建就不能修改
 * - 场景隔离：每条审计日志都属于特定场景
 * - 完整记录：记录操作前后的完整状态
 */
@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 场景 ID
     * 
     * 必填字段，标识审计日志所属的场景
     */
    @Column(name = "scene_id", nullable = false)
    private Long sceneId;
    
    /**
     * 实体类型
     * 
     * 可选值：ITEM（物品）、LOCATION（位置）
     */
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;
    
    /**
     * 实体 ID
     * 
     * 被操作实体的 ID
     */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    /**
     * 操作类型
     * 
     * 可选值：CREATE（创建）、UPDATE（更新）、DELETE（删除）、MOVE（移动）、BORROW（借出）、RETURN（归还）
     */
    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;
    
    /**
     * 操作前状态
     * 
     * JSON 格式，记录操作前的实体状态
     * 对于 CREATE 操作，此字段为 null
     */
    @Convert(converter = JsonbConverter.class)
    @Column(name = "before_state", columnDefinition = "TEXT")
    private Map<String, Object> beforeState;
    
    /**
     * 操作后状态
     * 
     * JSON 格式，记录操作后的实体状态
     * 对于 DELETE 操作，此字段为 null
     */
    @Convert(converter = JsonbConverter.class)
    @Column(name = "after_state", columnDefinition = "TEXT")
    private Map<String, Object> afterState;
    
    /**
     * 操作人
     * 
     * 可选字段，记录执行操作的用户
     */
    @Column(name = "operator", length = 100)
    private String operator;
    
    /**
     * 操作时间
     * 
     * 必填字段，记录操作发生的时间
     */
    @Column(name = "operated_at", nullable = false)
    private LocalDateTime operatedAt;
    
    /**
     * 创建时自动设置操作时间
     */
    @PrePersist
    protected void onCreate() {
        if (this.operatedAt == null) {
            this.operatedAt = LocalDateTime.now();
        }
    }
}
