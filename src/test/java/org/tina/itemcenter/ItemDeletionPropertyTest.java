package org.tina.itemcenter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tina.itemcenter.application.service.ItemService;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.common.exception.EntityNotFoundException;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.repository.ItemRepository;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 物品删除属性测试
 * 
 * 验证属性 22：物理删除完整性
 */
@SpringBootTest
@Tag("Feature: item-management-system, Property 22: 物理删除完整性")
public class ItemDeletionPropertyTest {
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private ItemRepository itemRepository;
    
    private Random random = new Random();
    
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
     * 属性 22：物理删除完整性
     * 
     * 对于任意物品删除操作，删除后该实体不能再被查询到（物理删除）。
     * 
     * 验证需求：6.4
     */
    @Test
    void itemDeletion_MustBePhysicalDeletion() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 生成随机测试数据
            String itemName = generateRandomString(5, 20);
            int quantity = 1 + random.nextInt(10);
            
            // 创建物品
            Item createdItem = itemService.createItem(
                    itemName, 
                    "Test item for deletion", 
                    null, 
                    null, 
                    quantity
            );
            Long itemId = createdItem.getId();
            
            // 验证：物品创建成功，可以查询到
            Optional<Item> foundBeforeDeletion = itemRepository.findById(itemId);
            assertTrue(foundBeforeDeletion.isPresent(), "物品创建后应该可以查询到");
            
            // 删除物品
            itemService.deleteItem(itemId);
            
            // 验证：删除后不能通过 Repository 查询到
            Optional<Item> foundAfterDeletion = itemRepository.findById(itemId);
            assertFalse(foundAfterDeletion.isPresent(), 
                    "物品删除后不应该能通过 Repository 查询到");
            
            // 验证：删除后不能通过 Service 查询到（应该抛出 EntityNotFoundException）
            assertThrows(EntityNotFoundException.class, () -> {
                itemService.getItemById(itemId);
            }, "物品删除后通过 Service 查询应该抛出 EntityNotFoundException");
            
            // 验证：数据库中确实没有该记录
            long count = itemRepository.count();
            assertEquals(0, count, "删除后数据库中不应该有任何物品记录");
        }
    }
    
    /**
     * 属性 22：物理删除完整性（多物品场景）
     * 
     * 验证删除一个物品不影响其他物品
     */
    @Test
    void itemDeletion_OnlyDeletesTargetItem() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 创建多个物品
            int itemCount = 3 + random.nextInt(5); // 3-7 个物品
            Long[] itemIds = new Long[itemCount];
            
            for (int j = 0; j < itemCount; j++) {
                String name = "Item " + j;
                Item item = itemService.createItem(name, null, null, null, 1);
                itemIds[j] = item.getId();
            }
            
            // 随机选择一个物品删除
            int deleteIndex = random.nextInt(itemCount);
            Long itemToDelete = itemIds[deleteIndex];
            
            // 删除选中的物品
            itemService.deleteItem(itemToDelete);
            
            // 验证：被删除的物品不能查询到
            Optional<Item> deletedItem = itemRepository.findById(itemToDelete);
            assertFalse(deletedItem.isPresent(), "被删除的物品不应该能查询到");
            
            // 验证：其他物品仍然可以查询到
            for (int j = 0; j < itemCount; j++) {
                if (j != deleteIndex) {
                    Optional<Item> otherItem = itemRepository.findById(itemIds[j]);
                    assertTrue(otherItem.isPresent(), 
                            "其他物品应该仍然可以查询到");
                }
            }
            
            // 验证：数据库中的物品数量正确
            long count = itemRepository.count();
            assertEquals(itemCount - 1, count, 
                    "删除一个物品后，数据库中应该还有 " + (itemCount - 1) + " 个物品");
        }
    }
    
    /**
     * 属性 22：物理删除完整性（场景隔离）
     * 
     * 验证删除操作只影响当前场景的物品
     */
    @Test
    void itemDeletion_RespectSceneIsolation() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 在场景 1 中创建物品
            SceneContext.setSceneId(1L);
            Item item1 = itemService.createItem("Item in Scene 1", null, null, null, 1);
            Long item1Id = item1.getId();
            
            // 在场景 2 中创建物品
            SceneContext.setSceneId(2L);
            Item item2 = itemService.createItem("Item in Scene 2", null, null, null, 1);
            Long item2Id = item2.getId();
            
            // 在场景 1 中删除物品
            SceneContext.setSceneId(1L);
            itemService.deleteItem(item1Id);
            
            // 验证：场景 1 中的物品被删除
            Optional<Item> deletedItem = itemRepository.findById(item1Id);
            assertFalse(deletedItem.isPresent(), "场景 1 中的物品应该被删除");
            
            // 验证：场景 2 中的物品仍然存在
            SceneContext.setSceneId(2L);
            Optional<Item> item2AfterDeletion = itemRepository.findById(item2Id);
            assertTrue(item2AfterDeletion.isPresent(), 
                    "场景 2 中的物品不应该受影响");
            
            // 验证：尝试在场景 2 中删除场景 1 的物品应该失败
            SceneContext.setSceneId(2L);
            assertThrows(EntityNotFoundException.class, () -> {
                itemService.deleteItem(item1Id);
            }, "不能跨场景删除物品");
        }
    }
    
    /**
     * 属性 22：删除不存在的物品应该失败
     * 
     * 验证删除不存在的物品会抛出异常
     */
    @Test
    void itemDeletion_NonExistentItemThrowsException() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 生成一个不存在的物品 ID
            Long nonExistentId = 999999L + random.nextInt(100000);
            
            // 验证：删除不存在的物品应该抛出 EntityNotFoundException
            assertThrows(EntityNotFoundException.class, () -> {
                itemService.deleteItem(nonExistentId);
            }, "删除不存在的物品应该抛出 EntityNotFoundException");
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
