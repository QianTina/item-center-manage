package org.tina.itemcenter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 审计日志服务单元测试
 * 
 * 测试审计日志的查询功能，包括分页查询和实体历史记录查询
 */
@SpringBootTest
public class AuditLogServiceTest {
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @BeforeEach
    void setUp() {
        // 清理数据库
        auditLogRepository.deleteAll();
        // 设置测试场景
        SceneContext.setSceneId(1L);
    }
    
    @AfterEach
    void tearDown() {
        // 清理 SceneContext
        SceneContext.clear();
    }
    
    /**
     * 测试查询审计日志 - 分页功能
     * 
     * 验证需求：20.1, 20.2
     */
    @Test
    void testFindAll_WithPagination() {
        // 准备测试数据：创建 15 条审计日志
        for (int i = 0; i < 15; i++) {
            Map<String, Object> state = new HashMap<>();
            state.put("index", i);
            auditService.log("ITEM", (long) i, "CREATE", null, state);
        }
        
        // 测试第一页（每页 10 条）
        Pageable pageable1 = PageRequest.of(0, 10);
        Page<AuditLog> page1 = auditService.findAll(pageable1);
        
        assertEquals(15, page1.getTotalElements(), "总记录数应该是 15");
        assertEquals(2, page1.getTotalPages(), "总页数应该是 2");
        assertEquals(10, page1.getContent().size(), "第一页应该有 10 条记录");
        assertTrue(page1.hasNext(), "第一页应该有下一页");
        
        // 测试第二页
        Pageable pageable2 = PageRequest.of(1, 10);
        Page<AuditLog> page2 = auditService.findAll(pageable2);
        
        assertEquals(15, page2.getTotalElements(), "总记录数应该是 15");
        assertEquals(5, page2.getContent().size(), "第二页应该有 5 条记录");
        assertFalse(page2.hasNext(), "第二页不应该有下一页");
    }
    
    /**
     * 测试查询审计日志 - 空结果
     * 
     * 验证需求：20.3
     */
    @Test
    void testFindAll_EmptyResult() {
        // 不创建任何数据
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = auditService.findAll(pageable);
        
        assertEquals(0, page.getTotalElements(), "总记录数应该是 0");
        assertEquals(0, page.getContent().size(), "应该没有记录");
        assertTrue(page.isEmpty(), "页面应该为空");
    }
    
    /**
     * 测试查询审计日志 - 场景过滤
     * 
     * 验证需求：20.4
     */
    @Test
    void testFindAll_SceneFiltering() {
        // 在场景 1 中创建 5 条日志
        SceneContext.setSceneId(1L);
        for (int i = 0; i < 5; i++) {
            Map<String, Object> state = new HashMap<>();
            state.put("scene", 1);
            auditService.log("ITEM", (long) i, "CREATE", null, state);
        }
        
        // 在场景 2 中创建 3 条日志
        SceneContext.setSceneId(2L);
        for (int i = 0; i < 3; i++) {
            Map<String, Object> state = new HashMap<>();
            state.put("scene", 2);
            auditService.log("ITEM", (long) i, "CREATE", null, state);
        }
        
        // 查询场景 1 的日志
        SceneContext.setSceneId(1L);
        Pageable pageable1 = PageRequest.of(0, 10);
        Page<AuditLog> page1 = auditService.findAll(pageable1);
        
        assertEquals(5, page1.getTotalElements(), "场景 1 应该有 5 条日志");
        page1.getContent().forEach(log -> 
            assertEquals(1L, log.getSceneId(), "所有日志都应该属于场景 1")
        );
        
        // 查询场景 2 的日志
        SceneContext.setSceneId(2L);
        Pageable pageable2 = PageRequest.of(0, 10);
        Page<AuditLog> page2 = auditService.findAll(pageable2);
        
        assertEquals(3, page2.getTotalElements(), "场景 2 应该有 3 条日志");
        page2.getContent().forEach(log -> 
            assertEquals(2L, log.getSceneId(), "所有日志都应该属于场景 2")
        );
    }
    
    /**
     * 测试查询实体历史记录 - 基本功能
     * 
     * 验证需求：21.1, 21.2
     */
    @Test
    void testFindEntityHistory_BasicFunctionality() {
        // 为同一个实体创建多条历史记录
        Long entityId = 100L;
        String entityType = "ITEM";
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> beforeState = new HashMap<>();
            beforeState.put("version", i);
            
            Map<String, Object> afterState = new HashMap<>();
            afterState.put("version", i + 1);
            
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
        
        assertEquals(5, history.size(), "应该有 5 条历史记录");
        
        // 验证所有记录都属于同一个实体
        history.forEach(log -> {
            assertEquals(entityType, log.getEntityType(), "实体类型应该一致");
            assertEquals(entityId, log.getEntityId(), "实体 ID 应该一致");
        });
    }
    
    /**
     * 测试查询实体历史记录 - 按时间倒序排列
     * 
     * 验证需求：21.3
     */
    @Test
    void testFindEntityHistory_OrderByTimeDescending() {
        // 创建多条历史记录
        Long entityId = 200L;
        String entityType = "LOCATION";
        
        for (int i = 0; i < 3; i++) {
            Map<String, Object> state = new HashMap<>();
            state.put("step", i);
            auditService.log(entityType, entityId, "UPDATE", null, state);
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 查询历史记录
        List<AuditLog> history = auditService.findEntityHistory(entityType, entityId);
        
        // 验证按时间倒序排列（最新的在前）
        for (int i = 0; i < history.size() - 1; i++) {
            assertTrue(
                history.get(i).getOperatedAt().isAfter(history.get(i + 1).getOperatedAt()) ||
                history.get(i).getOperatedAt().isEqual(history.get(i + 1).getOperatedAt()),
                "历史记录应该按时间倒序排列"
            );
        }
    }
    
    /**
     * 测试查询实体历史记录 - 空结果
     * 
     * 验证需求：21.4
     */
    @Test
    void testFindEntityHistory_EmptyResult() {
        // 查询不存在的实体
        List<AuditLog> history = auditService.findEntityHistory("ITEM", 999L);
        
        assertNotNull(history, "结果不应该为 null");
        assertTrue(history.isEmpty(), "应该返回空列表");
    }
    
    /**
     * 测试查询实体历史记录 - 场景隔离
     * 
     * 验证需求：21.5
     */
    @Test
    void testFindEntityHistory_SceneIsolation() {
        Long entityId = 300L;
        String entityType = "ITEM";
        
        // 在场景 1 中创建历史记录
        SceneContext.setSceneId(1L);
        for (int i = 0; i < 3; i++) {
            Map<String, Object> state = new HashMap<>();
            state.put("scene", 1);
            auditService.log(entityType, entityId, "UPDATE", null, state);
        }
        
        // 在场景 2 中创建历史记录
        SceneContext.setSceneId(2L);
        for (int i = 0; i < 2; i++) {
            Map<String, Object> state = new HashMap<>();
            state.put("scene", 2);
            auditService.log(entityType, entityId, "UPDATE", null, state);
        }
        
        // 在场景 1 中查询，应该只返回场景 1 的记录
        SceneContext.setSceneId(1L);
        List<AuditLog> history1 = auditService.findEntityHistory(entityType, entityId);
        
        assertEquals(3, history1.size(), "场景 1 应该有 3 条历史记录");
        history1.forEach(log -> 
            assertEquals(1L, log.getSceneId(), "所有记录都应该属于场景 1")
        );
        
        // 在场景 2 中查询，应该只返回场景 2 的记录
        SceneContext.setSceneId(2L);
        List<AuditLog> history2 = auditService.findEntityHistory(entityType, entityId);
        
        assertEquals(2, history2.size(), "场景 2 应该有 2 条历史记录");
        history2.forEach(log -> 
            assertEquals(2L, log.getSceneId(), "所有记录都应该属于场景 2")
        );
    }
    
    /**
     * 测试记录审计日志 - 包含操作人
     * 
     * 验证需求：19.10
     */
    @Test
    void testLog_WithOperator() {
        String operator = "test-user";
        Map<String, Object> state = new HashMap<>();
        state.put("field", "value");
        
        auditService.log("ITEM", 1L, "CREATE", null, state, operator);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = auditService.findAll(pageable);
        
        assertEquals(1, page.getTotalElements());
        AuditLog log = page.getContent().get(0);
        
        assertEquals(operator, log.getOperator(), "操作人应该正确记录");
    }
    
    /**
     * 测试记录审计日志 - 不包含操作人
     * 
     * 验证需求：19.10
     */
    @Test
    void testLog_WithoutOperator() {
        Map<String, Object> state = new HashMap<>();
        state.put("field", "value");
        
        auditService.log("ITEM", 1L, "CREATE", null, state);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = auditService.findAll(pageable);
        
        assertEquals(1, page.getTotalElements());
        AuditLog log = page.getContent().get(0);
        
        assertNull(log.getOperator(), "操作人应该为 null");
    }
}
