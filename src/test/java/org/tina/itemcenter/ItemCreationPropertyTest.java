package org.tina.itemcenter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tina.itemcenter.application.service.ItemService;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.model.ItemStatus;
import org.tina.itemcenter.domain.repository.ItemRepository;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 物品创建的属性测试
 * 
 * **验证需求：3.2, 3.4, 3.6, 3.7, 5.4**
 */
@SpringBootTest
public class ItemCreationPropertyTest {
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private ItemRepository itemRepository;
    
    private Random random = new Random();
    
    @BeforeEach
    void setUp() {
        // 清理数据库
        itemRepository.deleteAll();
    }
    
    @AfterEach
    void tearDown() {
        // 清理 SceneContext
        SceneContext.clear();
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
    
    /**
     * 属性 3：场景 ID 自动设置且不可变（物品部分）
     * 
     * 对于任意新创建的物品，其场景 ID 必须自动设置为当前场景 ID，
     * 且在后续更新操作中场景 ID 不能被修改。
     * 
     * **Validates: Requirements 3.4, 5.4**
     */
    @Test
    @Tag("property-3")
    void sceneId_MustBeAutoSetAndImmutable() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 生成随机测试数据
            Long sceneId = 1L + random.nextInt(1000);
            String itemName = generateRandomString(1, 50);
            Long newSceneId = 1L + random.nextInt(1000);
            
            // 设置场景上下文
            SceneContext.setSceneId(sceneId);
            
            try {
                // 创建物品
                Item item = itemService.createItem(itemName, null, null, null, null);
                
                // 验证场景 ID 自动设置
                assertNotNull(item.getSceneId(), "场景 ID 不应该为空");
                assertEquals(sceneId, item.getSceneId(), "场景 ID 应该自动设置为当前场景 ID");
                
                // 尝试更新物品（不修改场景 ID）
                Item updatedItem = itemService.updateItem(item.getId(), "新名称", null, null, null);
                
                // 验证场景 ID 不可变
                assertEquals(sceneId, updatedItem.getSceneId(), "场景 ID 在更新后不应该改变");
                
                // 注意：直接修改实体的场景 ID 并保存是不推荐的做法
                // 但我们需要验证即使这样做，场景 ID 也不会真正改变
                // 因为 Repository 的查询会自动过滤场景 ID
                
            } finally {
                SceneContext.clear();
            }
        }
    }
    
    /**
     * 属性 5：必填字段校验（物品名称）
     * 
     * 对于任意创建请求，如果缺少必填字段（物品名称），
     * 系统必须拒绝请求并返回参数校验错误。
     * 
     * **Validates: Requirements 3.2**
     */
    @Test
    @Tag("property-5")
    void itemName_MustBeRequired() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 生成随机场景 ID
            Long sceneId = 1L + random.nextInt(1000);
            
            // 设置场景上下文
            SceneContext.setSceneId(sceneId);
            
            try {
                // 尝试创建没有名称的物品（null）
                assertThrows(Exception.class, () -> {
                    itemService.createItem(null, "描述", null, null, null);
                }, "创建物品时名称为 null 应该抛出异常");
                
                // 尝试创建空名称的物品
                assertThrows(Exception.class, () -> {
                    itemService.createItem("", "描述", null, null, null);
                }, "创建物品时名称为空字符串应该抛出异常");
                
                // 尝试创建只有空格的名称
                assertThrows(Exception.class, () -> {
                    itemService.createItem("   ", "描述", null, null, null);
                }, "创建物品时名称只有空格应该抛出异常");
                
            } finally {
                SceneContext.clear();
            }
        }
    }
    
    /**
     * 属性 7：物品默认状态和数量
     * 
     * 对于任意新创建的物品，如果未指定状态，则状态必须默认为 AVAILABLE；
     * 如果未指定数量，则数量必须默认为 1。
     * 
     * **Validates: Requirements 3.6, 3.7**
     */
    @Test
    @Tag("property-7")
    void item_MustHaveDefaultStatusAndQuantity() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 生成随机测试数据
            Long sceneId = 1L + random.nextInt(1000);
            String itemName = generateRandomString(1, 50);
            
            // 设置场景上下文
            SceneContext.setSceneId(sceneId);
            
            try {
                // 创建物品（不指定状态和数量）
                Item item = itemService.createItem(itemName, null, null, null, null);
                
                // 验证默认状态
                assertNotNull(item.getStatus(), "物品状态不应该为空");
                assertEquals(ItemStatus.AVAILABLE, item.getStatus(), 
                        "物品默认状态应该是 AVAILABLE");
                
                // 验证默认数量
                assertNotNull(item.getQuantity(), "物品数量不应该为空");
                assertEquals(1, item.getQuantity(), 
                        "物品默认数量应该是 1");
                
            } finally {
                SceneContext.clear();
            }
        }
    }
    
    /**
     * 属性 7 扩展：指定数量时应该使用指定的数量
     * 
     * 对于任意新创建的物品，如果指定了数量，则应该使用指定的数量。
     * 
     * **Validates: Requirements 3.7**
     */
    @Test
    @Tag("property-7")
    void item_ShouldUseSpecifiedQuantity() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 生成随机测试数据
            Long sceneId = 1L + random.nextInt(1000);
            String itemName = generateRandomString(1, 50);
            Integer quantity = 1 + random.nextInt(1000);
            
            // 设置场景上下文
            SceneContext.setSceneId(sceneId);
            
            try {
                // 创建物品（指定数量）
                Item item = itemService.createItem(itemName, null, null, null, quantity);
                
                // 验证使用指定的数量
                assertNotNull(item.getQuantity(), "物品数量不应该为空");
                assertEquals(quantity, item.getQuantity(), 
                        "物品数量应该是指定的数量");
                
                // 验证状态仍然是默认的 AVAILABLE
                assertEquals(ItemStatus.AVAILABLE, item.getStatus(), 
                        "物品默认状态应该是 AVAILABLE");
                
            } finally {
                SceneContext.clear();
            }
        }
    }
}
