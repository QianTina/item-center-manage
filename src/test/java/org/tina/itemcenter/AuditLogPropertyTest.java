package org.tina.itemcenter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.tina.itemcenter.application.service.AuditService;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.domain.model.AuditLog;
import org.tina.itemcenter.domain.repository.AuditLogRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 审计日志属性测试
 * 
 * 验证属性 4：关键操作审计日志完整性
 */
@SpringBootTest
@Tag("Feature: item-management-system, Property 4: 关键操作审计日志完整性")
public class AuditLogPropertyTest {
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    private Random random = new Random();
    
    @BeforeEach
    void setUp() {
        // 清理数据库
        auditLogRepository.deleteAll();
    }
    
    @AfterEach
    void tearDown() {
        // 清理 SceneContext
        SceneContext.clear();
    }
    
    /**
     * 属性 4：关键操作审计日志完整性
     * 
     * 对于任意关键操作，系统必须记录审计日志，包含场景 ID、实体类型、实体 ID、
     * 操作类型、操作前状态、操作后状态和操作时间。
     * 
     * 验证需求：19.1-19.9
     */
    @Test
    void auditLogCompleteness_AllRequiredFieldsMustBePresent() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            auditLogRepository.deleteAll();
            
            // 生成随机测试数据
            Long sceneId = 1L + random.nextInt(1000);
            String entityType = generateRandomString(4, 10);
            Long entityId = 1L + random.nextInt(10000);
            String operationType = generateRandomString(4, 10);
            
            // 设置场景上下文
            SceneContext.setSceneId(sceneId);
            
            // 创建操作前后状态
            Map<String, Object> beforeState = new HashMap<>();
            beforeState.put("field1", "value1");
            beforeState.put("field2", 123);
            
            Map<String, Object> afterState = new HashMap<>();
            afterState.put("field1", "value2");
            afterState.put("field2", 456);
            
            // 记录审计日志（不带操作人）
            LocalDateTime beforeLog = LocalDateTime.now().minusSeconds(1);
            auditService.log(entityType, entityId, operationType, beforeState, afterState);
            LocalDateTime afterLog = LocalDateTime.now().plusSeconds(1);
            
            // 查询审计日志
            Pageable pageable = PageRequest.of(0, 10);
            Page<AuditLog> logs = auditService.findAll(pageable);
            
            // 验证：必须有一条审计日志
            assertEquals(1, logs.getTotalElements(), "应该记录一条审计日志");
            
            AuditLog log = logs.getContent().get(0);
            
            // 验证：场景 ID 必须正确（需求 19.3）
            assertEquals(sceneId, log.getSceneId(), "场景 ID 必须正确");
            
            // 验证：实体类型必须正确（需求 19.4）
            assertEquals(entityType, log.getEntityType(), "实体类型必须正确");
            
            // 验证：实体 ID 必须正确（需求 19.5）
            assertEquals(entityId, log.getEntityId(), "实体 ID 必须正确");
            
            // 验证：操作类型必须正确（需求 19.6）
            assertEquals(operationType, log.getOperationType(), "操作类型必须正确");
            
            // 验证：操作前状态必须正确（需求 19.7）
            assertNotNull(log.getBeforeState(), "操作前状态不能为空");
            assertEquals("value1", log.getBeforeState().get("field1"));
            assertEquals(123, log.getBeforeState().get("field2"));
            
            // 验证：操作后状态必须正确（需求 19.8）
            assertNotNull(log.getAfterState(), "操作后状态不能为空");
            assertEquals("value2", log.getAfterState().get("field1"));
            assertEquals(456, log.getAfterState().get("field2"));
            
            // 验证：操作时间必须在合理范围内（需求 19.9）
            assertNotNull(log.getOperatedAt(), "操作时间不能为空");
            assertTrue(log.getOperatedAt().isAfter(beforeLog) || log.getOperatedAt().isEqual(beforeLog),
                    "操作时间应该在记录之后");
            assertTrue(log.getOperatedAt().isBefore(afterLog) || log.getOperatedAt().isEqual(afterLog),
                    "操作时间应该在记录之前");
            
            // 验证：操作人字段可以为空（需求 19.10）
            // 这里我们没有提供操作人，所以应该为 null
            assertNull(log.getOperator(), "操作人字段应该为空");
        }
    }
    
    /**
     * 属性 4：关键操作审计日志完整性（带操作人）
     * 
     * 验证带操作人的审计日志记录
     */
    @Test
    void auditLogCompleteness_WithOperator_AllFieldsMustBePresent() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            auditLogRepository.deleteAll();
            
            // 生成随机测试数据
            Long sceneId = 1L + random.nextInt(1000);
            String entityType = generateRandomString(4, 10);
            Long entityId = 1L + random.nextInt(10000);
            String operationType = generateRandomString(4, 10);
            String operator = generateRandomString(2, 20);
            
            // 设置场景上下文
            SceneContext.setSceneId(sceneId);
            
            // 创建操作前后状态
            Map<String, Object> beforeState = new HashMap<>();
            beforeState.put("status", "AVAILABLE");
            
            Map<String, Object> afterState = new HashMap<>();
            afterState.put("status", "BORROWED");
            
            // 记录审计日志（带操作人）
            auditService.log(entityType, entityId, operationType, beforeState, afterState, operator);
            
            // 查询审计日志
            Pageable pageable = PageRequest.of(0, 10);
            Page<AuditLog> logs = auditService.findAll(pageable);
            
            // 验证：必须有一条审计日志
            assertEquals(1, logs.getTotalElements());
            
            AuditLog log = logs.getContent().get(0);
            
            // 验证：所有必填字段都存在
            assertNotNull(log.getSceneId());
            assertNotNull(log.getEntityType());
            assertNotNull(log.getEntityId());
            assertNotNull(log.getOperationType());
            assertNotNull(log.getBeforeState());
            assertNotNull(log.getAfterState());
            assertNotNull(log.getOperatedAt());
            
            // 验证：操作人字段必须正确
            assertEquals(operator, log.getOperator(), "操作人必须正确");
        }
    }
    
    /**
     * 属性 4：实体历史记录查询
     * 
     * 验证可以查询特定实体的所有历史记录
     * 
     * 验证需求：21.1-21.5
     */
    @Test
    void entityHistory_MustReturnAllLogsForEntity() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            auditLogRepository.deleteAll();
            
            // 生成随机测试数据
            Long sceneId = 1L + random.nextInt(100);
            String entityType = generateRandomString(4, 10);
            Long entityId = 1L + random.nextInt(1000);
            int operationCount = 2 + random.nextInt(4); // 2-5 次操作
            
            // 设置场景上下文
            SceneContext.setSceneId(sceneId);
            
            // 为同一个实体记录多次操作
            for (int j = 0; j < operationCount; j++) {
                Map<String, Object> beforeState = new HashMap<>();
                beforeState.put("version", j);
                
                Map<String, Object> afterState = new HashMap<>();
                afterState.put("version", j + 1);
                
                auditService.log(entityType, entityId, "UPDATE", beforeState, afterState);
                
                // 稍微延迟，确保时间戳不同
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // 查询实体历史记录
            List<AuditLog> history = auditService.findEntityHistory(entityType, entityId);
            
            // 验证：历史记录数量必须正确
            assertEquals(operationCount, history.size(), "历史记录数量必须等于操作次数");
            
            // 验证：所有记录都属于同一个实体
            for (AuditLog log : history) {
                assertEquals(entityType, log.getEntityType(), "实体类型必须一致");
                assertEquals(entityId, log.getEntityId(), "实体 ID 必须一致");
                assertEquals(sceneId, log.getSceneId(), "场景 ID 必须一致");
            }
            
            // 验证：记录按时间倒序排列（最新的在前）
            for (int j = 0; j < history.size() - 1; j++) {
                LocalDateTime current = history.get(j).getOperatedAt();
                LocalDateTime next = history.get(j + 1).getOperatedAt();
                assertTrue(current.isAfter(next) || current.isEqual(next),
                        "历史记录应该按时间倒序排列");
            }
        }
    }
    
    /**
     * 属性 4：场景隔离
     * 
     * 验证不同场景的审计日志完全隔离
     */
    @Test
    void auditLogSceneIsolation_MustOnlyReturnCurrentSceneLogs() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            auditLogRepository.deleteAll();
            
            // 生成随机测试数据
            Long sceneId1 = 1L + random.nextInt(100);
            Long sceneId2 = 101L + random.nextInt(100);
            String entityType = generateRandomString(4, 10);
            Long entityId = 1L + random.nextInt(1000);
            
            // 在场景1中记录审计日志
            SceneContext.setSceneId(sceneId1);
            Map<String, Object> state1 = new HashMap<>();
            state1.put("scene", "scene1");
            auditService.log(entityType, entityId, "CREATE", null, state1);
            
            // 在场景2中记录审计日志
            SceneContext.setSceneId(sceneId2);
            Map<String, Object> state2 = new HashMap<>();
            state2.put("scene", "scene2");
            auditService.log(entityType, entityId, "CREATE", null, state2);
            
            // 在场景1中查询，应该只返回场景1的日志
            SceneContext.setSceneId(sceneId1);
            Pageable pageable = PageRequest.of(0, 10);
            Page<AuditLog> logs1 = auditService.findAll(pageable);
            
            assertEquals(1, logs1.getTotalElements(), "场景1应该只有1条日志");
            assertEquals(sceneId1, logs1.getContent().get(0).getSceneId());
            
            // 在场景2中查询，应该只返回场景2的日志
            SceneContext.setSceneId(sceneId2);
            Page<AuditLog> logs2 = auditService.findAll(pageable);
            
            assertEquals(1, logs2.getTotalElements(), "场景2应该只有1条日志");
            assertEquals(sceneId2, logs2.getContent().get(0).getSceneId());
        }
    }
    
    /**
     * 生成随机字符串
     */
    private String generateRandomString(int minLength, int maxLength) {
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char) ('a' + random.nextInt(26));
            sb.append(c);
        }
        return sb.toString();
    }
}
