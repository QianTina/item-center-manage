package org.tina.itemcenter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.model.ItemStatus;
import org.tina.itemcenter.domain.repository.ItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ItemRepository 场景隔离的属性测试
 * 
 * 验证属性 1：场景完全隔离
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class ItemRepositorySceneIsolationTest {
    
    @Autowired
    private ItemRepository itemRepository;
    
    private final Random random = new Random();
    
    /**
     * 属性 1：场景完全隔离（物品部分 - findAll）
     * 
     * 对于任意场景和任意物品，查询操作只能返回属于当前场景的数据，不能跨场景访问。
     * 
     * 验证需求：4.2, 22.1-22.4
     * 
     * **Validates: Requirements 4.2, 22.1-22.4**
     */
    @Test
    void sceneIsolation_FindAll_MustReturnOnlyCurrentSceneItems() {
        // 运行 100 次迭代，模拟属性测试
        for (int iteration = 0; iteration < 100; iteration++) {
            try {
                // 生成随机场景 ID
                Long sceneId1 = 1L + random.nextInt(1000);
                Long sceneId2 = 1001L + random.nextInt(1000);
                String itemName1 = "Item" + random.nextInt(10000);
                String itemName2 = "Item" + random.nextInt(10000);
                
                // 清理数据库
                itemRepository.deleteAll();
                
                // 在场景 1 中创建物品
                SceneContext.setSceneId(sceneId1);
                Item item1 = new Item();
                item1.setName(itemName1);
                item1.setSceneId(sceneId1);
                item1.setQuantity(1);
                item1.setStatus(ItemStatus.AVAILABLE);
                itemRepository.save(item1);
                
                // 在场景 2 中创建物品
                SceneContext.setSceneId(sceneId2);
                Item item2 = new Item();
                item2.setName(itemName2);
                item2.setSceneId(sceneId2);
                item2.setQuantity(1);
                item2.setStatus(ItemStatus.AVAILABLE);
                itemRepository.save(item2);
                
                // 切换到场景 1，查询所有物品
                SceneContext.setSceneId(sceneId1);
                List<Item> scene1Items = itemRepository.findAll();
                
                // 验证只返回场景 1 的物品
                assertEquals(1, scene1Items.size(), 
                        "场景 1 应该只返回 1 个物品");
                assertEquals(sceneId1, scene1Items.get(0).getSceneId(), 
                        "返回的物品应该属于场景 1");
                assertEquals(itemName1, scene1Items.get(0).getName(), 
                        "返回的物品名称应该是场景 1 的物品名称");
                
                // 切换到场景 2，查询所有物品
                SceneContext.setSceneId(sceneId2);
                List<Item> scene2Items = itemRepository.findAll();
                
                // 验证只返回场景 2 的物品
                assertEquals(1, scene2Items.size(), 
                        "场景 2 应该只返回 1 个物品");
                assertEquals(sceneId2, scene2Items.get(0).getSceneId(), 
                        "返回的物品应该属于场景 2");
                assertEquals(itemName2, scene2Items.get(0).getName(), 
                        "返回的物品名称应该是场景 2 的物品名称");
                
            } finally {
                SceneContext.clear();
            }
        }
    }
    
    /**
     * 属性 1：场景完全隔离（根据 ID 查询）
     * 
     * 对于任意场景和任意物品 ID，只能查询到属于当前场景的物品。
     * 
     * 验证需求：4.2, 22.1-22.4
     * 
     * **Validates: Requirements 4.2, 22.1-22.4**
     */
    @Test
    void sceneIsolation_FindById_MustReturnOnlyCurrentSceneItem() {
        // 运行 100 次迭代，模拟属性测试
        for (int iteration = 0; iteration < 100; iteration++) {
            try {
                // 生成随机场景 ID
                Long sceneId1 = 1L + random.nextInt(1000);
                Long sceneId2 = 1001L + random.nextInt(1000);
                String itemName = "Item" + random.nextInt(10000);
                
                // 清理数据库
                itemRepository.deleteAll();
                
                // 在场景 1 中创建物品
                SceneContext.setSceneId(sceneId1);
                Item item = new Item();
                item.setName(itemName);
                item.setSceneId(sceneId1);
                item.setQuantity(1);
                item.setStatus(ItemStatus.AVAILABLE);
                Item savedItem = itemRepository.save(item);
                Long itemId = savedItem.getId();
                
                // 在场景 1 中查询该物品，应该能找到
                SceneContext.setSceneId(sceneId1);
                Optional<Item> foundInScene1 = itemRepository.findById(itemId);
                assertTrue(foundInScene1.isPresent(), 
                        "在场景 1 中应该能找到该物品");
                assertEquals(sceneId1, foundInScene1.get().getSceneId(), 
                        "找到的物品应该属于场景 1");
                
                // 在场景 2 中查询该物品，应该找不到
                SceneContext.setSceneId(sceneId2);
                Optional<Item> foundInScene2 = itemRepository.findById(itemId);
                assertFalse(foundInScene2.isPresent(), 
                        "在场景 2 中不应该能找到场景 1 的物品");
                
            } finally {
                SceneContext.clear();
            }
        }
    }
    
    /**
     * 属性 1：场景完全隔离（根据位置 ID 查询）
     * 
     * 对于任意场景和任意位置 ID，只能查询到属于当前场景的物品。
     * 
     * 验证需求：4.2, 22.1-22.4
     * 
     * **Validates: Requirements 4.2, 22.1-22.4**
     */
    @Test
    void sceneIsolation_FindByLocationId_MustReturnOnlyCurrentSceneItems() {
        // 运行 100 次迭代，模拟属性测试
        for (int iteration = 0; iteration < 100; iteration++) {
            try {
                // 生成随机场景 ID 和位置 ID
                Long sceneId1 = 1L + random.nextInt(1000);
                Long sceneId2 = 1001L + random.nextInt(1000);
                Long locationId = 1L + random.nextInt(100);
                String itemName1 = "Item" + random.nextInt(10000);
                String itemName2 = "Item" + random.nextInt(10000);
                
                // 清理数据库
                itemRepository.deleteAll();
                
                // 在场景 1 中创建物品，位于指定位置
                SceneContext.setSceneId(sceneId1);
                Item item1 = new Item();
                item1.setName(itemName1);
                item1.setSceneId(sceneId1);
                item1.setLocationId(locationId);
                item1.setQuantity(1);
                item1.setStatus(ItemStatus.AVAILABLE);
                itemRepository.save(item1);
                
                // 在场景 2 中创建物品，位于相同位置
                SceneContext.setSceneId(sceneId2);
                Item item2 = new Item();
                item2.setName(itemName2);
                item2.setSceneId(sceneId2);
                item2.setLocationId(locationId);
                item2.setQuantity(1);
                item2.setStatus(ItemStatus.AVAILABLE);
                itemRepository.save(item2);
                
                // 在场景 1 中查询该位置的物品
                SceneContext.setSceneId(sceneId1);
                List<Item> scene1Items = itemRepository.findByLocationId(locationId);
                
                // 验证只返回场景 1 的物品
                assertEquals(1, scene1Items.size(), 
                        "场景 1 应该只返回 1 个物品");
                assertEquals(sceneId1, scene1Items.get(0).getSceneId(), 
                        "返回的物品应该属于场景 1");
                
                // 在场景 2 中查询该位置的物品
                SceneContext.setSceneId(sceneId2);
                List<Item> scene2Items = itemRepository.findByLocationId(locationId);
                
                // 验证只返回场景 2 的物品
                assertEquals(1, scene2Items.size(), 
                        "场景 2 应该只返回 1 个物品");
                assertEquals(sceneId2, scene2Items.get(0).getSceneId(), 
                        "返回的物品应该属于场景 2");
                
            } finally {
                SceneContext.clear();
            }
        }
    }
    
    /**
     * 属性 1：场景完全隔离（关键词搜索）
     * 
     * 对于任意场景和任意搜索关键词，只能搜索到属于当前场景的物品。
     * 
     * 验证需求：4.2, 22.1-22.4
     * 
     * **Validates: Requirements 4.2, 22.1-22.4**
     */
    @Test
    void sceneIsolation_SearchByKeyword_MustReturnOnlyCurrentSceneItems() {
        // 运行 100 次迭代，模拟属性测试
        for (int iteration = 0; iteration < 100; iteration++) {
            try {
                // 生成随机场景 ID 和关键词
                Long sceneId1 = 1L + random.nextInt(1000);
                Long sceneId2 = 1001L + random.nextInt(1000);
                String keyword = "Keyword" + random.nextInt(1000);
                
                // 清理数据库
                itemRepository.deleteAll();
                
                // 在场景 1 中创建包含关键词的物品
                SceneContext.setSceneId(sceneId1);
                Item item1 = new Item();
                item1.setName("Item with " + keyword + " in scene 1");
                item1.setSceneId(sceneId1);
                item1.setQuantity(1);
                item1.setStatus(ItemStatus.AVAILABLE);
                itemRepository.save(item1);
                
                // 在场景 2 中创建包含相同关键词的物品
                SceneContext.setSceneId(sceneId2);
                Item item2 = new Item();
                item2.setName("Item with " + keyword + " in scene 2");
                item2.setSceneId(sceneId2);
                item2.setQuantity(1);
                item2.setStatus(ItemStatus.AVAILABLE);
                itemRepository.save(item2);
                
                // 在场景 1 中搜索关键词
                SceneContext.setSceneId(sceneId1);
                List<Item> scene1Items = itemRepository.searchByKeyword(keyword);
                
                // 验证只返回场景 1 的物品
                assertEquals(1, scene1Items.size(), 
                        "场景 1 应该只返回 1 个物品");
                assertEquals(sceneId1, scene1Items.get(0).getSceneId(), 
                        "返回的物品应该属于场景 1");
                assertTrue(scene1Items.get(0).getName().contains(keyword), 
                        "返回的物品名称应该包含关键词");
                
                // 在场景 2 中搜索关键词
                SceneContext.setSceneId(sceneId2);
                List<Item> scene2Items = itemRepository.searchByKeyword(keyword);
                
                // 验证只返回场景 2 的物品
                assertEquals(1, scene2Items.size(), 
                        "场景 2 应该只返回 1 个物品");
                assertEquals(sceneId2, scene2Items.get(0).getSceneId(), 
                        "返回的物品应该属于场景 2");
                assertTrue(scene2Items.get(0).getName().contains(keyword), 
                        "返回的物品名称应该包含关键词");
                
            } finally {
                SceneContext.clear();
            }
        }
    }
}
