package org.tina.itemcenter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tina.itemcenter.application.service.ItemService;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.common.exception.EntityNotFoundException;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.model.ItemStatus;
import org.tina.itemcenter.domain.repository.ItemRepository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ItemService 单元测试
 */
@SpringBootTest
public class ItemServiceTest {
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @BeforeEach
    void setUp() {
        // 清理数据库
        itemRepository.deleteAll();
        // 设置测试场景
        SceneContext.setSceneId(1L);
    }
    
    @AfterEach
    void tearDown() {
        // 清理 SceneContext
        SceneContext.clear();
    }
    
    /**
     * 测试创建物品 - 包含所有字段
     */
    @Test
    void testCreateItem_WithAllFields() {
        // 准备测试数据
        String name = "测试物品";
        String description = "这是一个测试物品";
        String[] tags = {"标签1", "标签2"};
        Long locationId = 100L;
        Integer quantity = 5;
        
        // 创建物品
        Item item = itemService.createItem(name, description, tags, locationId, quantity);
        
        // 验证
        assertNotNull(item.getId(), "物品 ID 不应该为空");
        assertEquals(name, item.getName(), "物品名称应该正确");
        assertEquals(description, item.getDescription(), "物品描述应该正确");
        assertEquals(locationId, item.getLocationId(), "位置 ID 应该正确");
        assertEquals(quantity, item.getQuantity(), "数量应该正确");
        assertEquals(ItemStatus.AVAILABLE, item.getStatus(), "状态应该是 AVAILABLE");
        assertEquals(1L, item.getSceneId(), "场景 ID 应该自动设置");
        assertNotNull(item.getTags(), "标签列表不应该为空");
        assertEquals(2, item.getTags().size(), "应该有 2 个标签");
    }
    
    /**
     * 测试创建物品 - 仅必填字段
     */
    @Test
    void testCreateItem_WithRequiredFieldsOnly() {
        // 创建物品（仅提供名称）
        Item item = itemService.createItem("简单物品", null, null, null, null);
        
        // 验证
        assertNotNull(item.getId());
        assertEquals("简单物品", item.getName());
        assertNull(item.getDescription());
        assertNull(item.getLocationId());
        assertEquals(1, item.getQuantity(), "数量应该默认为 1");
        assertEquals(ItemStatus.AVAILABLE, item.getStatus());
        assertEquals(1L, item.getSceneId());
    }
    
    /**
     * 测试创建物品失败 - 缺少名称
     * 
     * 验证需求：3.2
     */
    @Test
    void testCreateItem_MissingName_ShouldFail() {
        // 尝试创建物品但不提供名称
        assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem(null, "描述", null, null, 1);
        }, "缺少名称应该抛出 IllegalArgumentException");
        
        // 尝试创建物品但提供空名称
        assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem("", "描述", null, null, 1);
        }, "空名称应该抛出 IllegalArgumentException");
        
        // 尝试创建物品但提供空白名称
        assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem("   ", "描述", null, null, 1);
        }, "空白名称应该抛出 IllegalArgumentException");
    }
    
    /**
     * 测试创建物品成功 - 位置为 null
     * 
     * 验证需求：3.3（物品允许没有位置）
     */
    @Test
    void testCreateItem_WithNullLocation_ShouldSucceed() {
        // 创建物品，位置为 null
        Item item = itemService.createItem("无位置物品", "这个物品没有位置", null, null, 1);
        
        // 验证
        assertNotNull(item.getId(), "物品应该创建成功");
        assertEquals("无位置物品", item.getName());
        assertNull(item.getLocationId(), "位置 ID 应该为 null");
        assertEquals(ItemStatus.AVAILABLE, item.getStatus());
        assertEquals(1L, item.getSceneId());
    }
    
    /**
     * 测试查询物品
     */
    @Test
    void testGetItemById() {
        // 创建物品
        Item createdItem = itemService.createItem("查询测试", null, null, null, null);
        
        // 查询物品
        Item foundItem = itemService.getItemById(createdItem.getId());
        
        // 验证
        assertNotNull(foundItem);
        assertEquals(createdItem.getId(), foundItem.getId());
        assertEquals(createdItem.getName(), foundItem.getName());
    }
    
    /**
     * 测试查询不存在的物品
     */
    @Test
    void testGetItemById_NotFound() {
        // 查询不存在的物品
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.getItemById(999L);
        });
    }
    
    /**
     * 测试更新物品
     */
    @Test
    void testUpdateItem() {
        // 创建物品
        Item item = itemService.createItem("原始名称", "原始描述", null, null, 1);
        
        // 更新物品
        String newName = "新名称";
        String newDescription = "新描述";
        String[] newTags = {"新标签"};
        Integer newQuantity = 10;
        
        Item updatedItem = itemService.updateItem(
                item.getId(), 
                newName, 
                newDescription, 
                newTags, 
                newQuantity
        );
        
        // 验证
        assertEquals(newName, updatedItem.getName());
        assertEquals(newDescription, updatedItem.getDescription());
        assertEquals(newQuantity, updatedItem.getQuantity());
        assertNotNull(updatedItem.getTags());
        assertEquals(1, updatedItem.getTags().size());
        assertTrue(updatedItem.getTags().contains("新标签"));
    }
    
    /**
     * 测试删除物品
     */
    @Test
    void testDeleteItem() {
        // 创建物品
        Item item = itemService.createItem("待删除物品", null, null, null, null);
        Long itemId = item.getId();
        
        // 删除物品
        itemService.deleteItem(itemId);
        
        // 验证物品已被删除
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.getItemById(itemId);
        });
    }
}
