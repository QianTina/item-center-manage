package org.tina.itemcenter;

import net.jqwik.api.*;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.StringLength;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.model.ItemStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Item 领域模型的属性测试
 * 
 * 验证 Item 实体的核心业务规则
 */
public class ItemDomainModelPropertyTest {
    
    /**
     * 属性 12：物品借出状态转换
     * 
     * 对于任意物品借出操作，物品状态必须从非 BORROWED 状态转换为 BORROWED 状态，
     * 并记录借用人和借出时间。
     * 
     * 验证需求：9.4, 9.5
     * 
     * **Validates: Requirements 9.4, 9.5**
     */
    @Property(tries = 100)
    void itemBorrow_MustTransitionFromNonBorrowedToBorrowed(
            @ForAll("nonBorrowedStatuses") ItemStatus initialStatus,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String borrower
    ) {
        // 创建物品并设置初始状态
        Item item = new Item();
        item.setName("测试物品");
        item.setStatus(initialStatus);
        item.setQuantity(1);
        item.setSceneId(1L);
        
        // 执行借出操作
        item.markBorrowed(borrower);
        
        // 验证状态转换
        assertEquals(ItemStatus.BORROWED, item.getStatus(), 
                "物品状态应该转换为 BORROWED");
        assertEquals(borrower, item.getBorrower(), 
                "应该记录借用人");
        assertNotNull(item.getBorrowedAt(), 
                "应该记录借出时间");
    }
    
    /**
     * 属性 12 的反例：重复借出应该失败
     * 
     * 如果物品已经处于 BORROWED 状态，再次借出应该抛出异常
     * 
     * **Validates: Requirements 9.4, 9.5**
     */
    @Property(tries = 100)
    void itemBorrow_AlreadyBorrowed_ShouldFail(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String borrower1,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String borrower2
    ) {
        // 创建已借出的物品
        Item item = new Item();
        item.setName("测试物品");
        item.setStatus(ItemStatus.BORROWED);
        item.setBorrower(borrower1);
        item.setQuantity(1);
        item.setSceneId(1L);
        
        // 尝试再次借出应该失败
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            item.markBorrowed(borrower2);
        });
        
        assertEquals("物品已被借出", exception.getMessage());
        // 验证状态没有改变
        assertEquals(ItemStatus.BORROWED, item.getStatus());
        assertEquals(borrower1, item.getBorrower(), 
                "借用人不应该被修改");
    }
    
    /**
     * 属性 14：物品归还状态转换
     * 
     * 对于任意物品归还操作，物品状态必须从 BORROWED 状态转换为 AVAILABLE 状态，
     * 并清空借用人和借出时间。
     * 
     * 验证需求：10.3, 10.4
     * 
     * **Validates: Requirements 10.3, 10.4**
     */
    @Property(tries = 100)
    void itemReturn_MustTransitionFromBorrowedToAvailable(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String borrower
    ) {
        // 创建已借出的物品
        Item item = new Item();
        item.setName("测试物品");
        item.setStatus(ItemStatus.BORROWED);
        item.setBorrower(borrower);
        item.setQuantity(1);
        item.setSceneId(1L);
        
        // 执行归还操作
        item.returnItem();
        
        // 验证状态转换
        assertEquals(ItemStatus.AVAILABLE, item.getStatus(), 
                "物品状态应该转换为 AVAILABLE");
        assertNull(item.getBorrower(), 
                "借用人应该被清空");
        assertNull(item.getBorrowedAt(), 
                "借出时间应该被清空");
    }
    
    /**
     * 属性 14 的反例：未借出状态归还应该失败
     * 
     * 如果物品不处于 BORROWED 状态，归还操作应该抛出异常
     * 
     * **Validates: Requirements 10.3, 10.4**
     */
    @Property(tries = 100)
    void itemReturn_NotBorrowed_ShouldFail(
            @ForAll("nonBorrowedStatuses") ItemStatus initialStatus
    ) {
        // 创建未借出的物品
        Item item = new Item();
        item.setName("测试物品");
        item.setStatus(initialStatus);
        item.setQuantity(1);
        item.setSceneId(1L);
        
        // 尝试归还应该失败
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            item.returnItem();
        });
        
        assertEquals("物品未处于借出状态", exception.getMessage());
        // 验证状态没有改变
        assertEquals(initialStatus, item.getStatus());
    }
    
    /**
     * 属性 15：物品数量非负约束
     * 
     * 对于任意物品数量更新操作，更新后的数量必须大于或等于 0。
     * 
     * 验证需求：11.4
     * 
     * **Validates: Requirements 11.4**
     */
    @Property(tries = 100)
    void updateQuantity_ResultMustBeNonNegative(
            @ForAll @IntRange(min = 0, max = 100) int initialQuantity,
            @ForAll @IntRange(min = -50, max = 50) int delta
    ) {
        // 创建物品并设置初始数量
        Item item = new Item();
        item.setName("测试物品");
        item.setQuantity(initialQuantity);
        item.setStatus(ItemStatus.AVAILABLE);
        item.setSceneId(1L);
        
        int expectedQuantity = initialQuantity + delta;
        
        if (expectedQuantity < 0) {
            // 如果结果为负，应该抛出异常
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                item.updateQuantity(delta);
            });
            
            assertEquals("物品数量不能为负数", exception.getMessage());
            // 验证数量没有改变
            assertEquals(initialQuantity, item.getQuantity(), 
                    "数量不应该被修改");
        } else {
            // 如果结果非负，应该成功更新
            item.updateQuantity(delta);
            
            assertTrue(item.getQuantity() >= 0, 
                    "更新后的数量必须非负");
            assertEquals(expectedQuantity, item.getQuantity(), 
                    "数量应该正确更新");
        }
    }
    
    /**
     * 属性 15 的边界测试：数量为 0 是合法的
     * 
     * **Validates: Requirements 11.4**
     */
    @Property(tries = 100)
    void updateQuantity_ZeroIsValid(
            @ForAll @IntRange(min = 1, max = 100) int initialQuantity
    ) {
        // 创建物品
        Item item = new Item();
        item.setName("测试物品");
        item.setQuantity(initialQuantity);
        item.setStatus(ItemStatus.AVAILABLE);
        item.setSceneId(1L);
        
        // 将数量减少到 0
        item.updateQuantity(-initialQuantity);
        
        // 验证数量为 0 是合法的
        assertEquals(0, item.getQuantity(), 
                "数量可以为 0");
    }
    
    // ============================================
    // 数据生成器
    // ============================================
    
    /**
     * 生成非 BORROWED 状态
     */
    @Provide
    Arbitrary<ItemStatus> nonBorrowedStatuses() {
        return Arbitraries.of(
                ItemStatus.AVAILABLE,
                ItemStatus.DAMAGED,
                ItemStatus.MAINTENANCE
        );
    }
}
