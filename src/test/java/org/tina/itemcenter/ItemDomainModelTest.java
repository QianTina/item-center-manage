package org.tina.itemcenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.model.ItemStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Item 领域模型的单元测试
 * 
 * 测试 Item 实体的领域行为方法
 */
class ItemDomainModelTest {
    
    private Item item;
    
    @BeforeEach
    void setUp() {
        item = new Item();
        item.setName("测试物品");
        item.setSceneId(1L);
        item.setQuantity(1);
        item.setStatus(ItemStatus.AVAILABLE);
    }
    
    // ============================================
    // moveTo 方法测试
    // ============================================
    
    /**
     * 测试移动物品到有效位置
     * 
     * 验证需求：8.4
     */
    @Test
    void moveTo_ValidLocation_ShouldUpdateLocationId() {
        // Given
        Long targetLocationId = 100L;
        
        // When
        item.moveTo(targetLocationId);
        
        // Then
        assertEquals(targetLocationId, item.getLocationId(), 
                "物品的位置 ID 应该更新为目标位置 ID");
        assertNotNull(item.getUpdatedAt(), 
                "更新时间应该被设置");
    }
    
    /**
     * 测试移动物品到 null 位置（无位置状态）
     * 
     * 验证需求：8.4
     */
    @Test
    void moveTo_NullLocation_ShouldSetLocationIdToNull() {
        // Given
        item.setLocationId(100L);
        
        // When
        item.moveTo(null);
        
        // Then
        assertNull(item.getLocationId(), 
                "物品的位置 ID 应该被设置为 null");
    }
    
    // ============================================
    // markBorrowed 方法测试
    // ============================================
    
    /**
     * 测试借出物品成功
     * 
     * 验证需求：9.3
     */
    @Test
    void markBorrowed_AvailableItem_ShouldSucceed() {
        // Given
        String borrower = "张三";
        item.setStatus(ItemStatus.AVAILABLE);
        
        // When
        item.markBorrowed(borrower);
        
        // Then
        assertEquals(ItemStatus.BORROWED, item.getStatus(), 
                "物品状态应该变为 BORROWED");
        assertEquals(borrower, item.getBorrower(), 
                "应该记录借用人");
        assertNotNull(item.getBorrowedAt(), 
                "应该记录借出时间");
        assertNotNull(item.getUpdatedAt(), 
                "更新时间应该被设置");
    }
    
    /**
     * 测试重复借出应该失败
     * 
     * 验证需求：9.3
     */
    @Test
    void markBorrowed_AlreadyBorrowedItem_ShouldThrowException() {
        // Given
        item.setStatus(ItemStatus.BORROWED);
        item.setBorrower("张三");
        
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            item.markBorrowed("李四");
        });
        
        assertEquals("物品已被借出", exception.getMessage());
        assertEquals("张三", item.getBorrower(), 
                "借用人不应该被修改");
    }
    
    /**
     * 测试从 DAMAGED 状态借出物品
     * 
     * 验证需求：9.3
     */
    @Test
    void markBorrowed_DamagedItem_ShouldSucceed() {
        // Given
        String borrower = "王五";
        item.setStatus(ItemStatus.DAMAGED);
        
        // When
        item.markBorrowed(borrower);
        
        // Then
        assertEquals(ItemStatus.BORROWED, item.getStatus(), 
                "物品状态应该变为 BORROWED");
        assertEquals(borrower, item.getBorrower(), 
                "应该记录借用人");
    }
    
    // ============================================
    // returnItem 方法测试
    // ============================================
    
    /**
     * 测试归还物品成功
     * 
     * 验证需求：10.2
     */
    @Test
    void returnItem_BorrowedItem_ShouldSucceed() {
        // Given
        item.setStatus(ItemStatus.BORROWED);
        item.setBorrower("张三");
        item.setBorrowedAt(LocalDateTime.now());
        
        // When
        item.returnItem();
        
        // Then
        assertEquals(ItemStatus.AVAILABLE, item.getStatus(), 
                "物品状态应该变为 AVAILABLE");
        assertNull(item.getBorrower(), 
                "借用人应该被清空");
        assertNull(item.getBorrowedAt(), 
                "借出时间应该被清空");
        assertNotNull(item.getUpdatedAt(), 
                "更新时间应该被设置");
    }
    
    /**
     * 测试未借出状态归还应该失败
     * 
     * 验证需求：10.2
     */
    @Test
    void returnItem_AvailableItem_ShouldThrowException() {
        // Given
        item.setStatus(ItemStatus.AVAILABLE);
        
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            item.returnItem();
        });
        
        assertEquals("物品未处于借出状态", exception.getMessage());
    }
    
    /**
     * 测试 DAMAGED 状态归还应该失败
     * 
     * 验证需求：10.2
     */
    @Test
    void returnItem_DamagedItem_ShouldThrowException() {
        // Given
        item.setStatus(ItemStatus.DAMAGED);
        
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            item.returnItem();
        });
        
        assertEquals("物品未处于借出状态", exception.getMessage());
    }
    
    // ============================================
    // updateQuantity 方法测试
    // ============================================
    
    /**
     * 测试增加物品数量
     * 
     * 验证需求：11.2
     */
    @Test
    void updateQuantity_PositiveDelta_ShouldIncreaseQuantity() {
        // Given
        item.setQuantity(10);
        
        // When
        item.updateQuantity(5);
        
        // Then
        assertEquals(15, item.getQuantity(), 
                "数量应该增加 5");
        assertNotNull(item.getUpdatedAt(), 
                "更新时间应该被设置");
    }
    
    /**
     * 测试减少物品数量
     * 
     * 验证需求：11.2
     */
    @Test
    void updateQuantity_NegativeDelta_ShouldDecreaseQuantity() {
        // Given
        item.setQuantity(10);
        
        // When
        item.updateQuantity(-3);
        
        // Then
        assertEquals(7, item.getQuantity(), 
                "数量应该减少 3");
    }
    
    /**
     * 测试数量减少到 0
     * 
     * 验证需求：11.2
     */
    @Test
    void updateQuantity_ToZero_ShouldSucceed() {
        // Given
        item.setQuantity(5);
        
        // When
        item.updateQuantity(-5);
        
        // Then
        assertEquals(0, item.getQuantity(), 
                "数量应该为 0");
    }
    
    /**
     * 测试负数应该失败
     * 
     * 验证需求：11.2
     */
    @Test
    void updateQuantity_ResultingInNegative_ShouldThrowException() {
        // Given
        item.setQuantity(5);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            item.updateQuantity(-10);
        });
        
        assertEquals("物品数量不能为负数", exception.getMessage());
        assertEquals(5, item.getQuantity(), 
                "数量不应该被修改");
    }
    
    // ============================================
    // addTag 和 removeTag 方法测试
    // ============================================
    
    /**
     * 测试添加标签
     * 
     * 验证需求：11.2
     */
    @Test
    void addTag_NewTag_ShouldAddToList() {
        // Given
        String tag = "电子产品";
        
        // When
        item.addTag(tag);
        
        // Then
        assertTrue(item.getTags().contains(tag), 
                "标签列表应该包含新标签");
        assertNotNull(item.getUpdatedAt(), 
                "更新时间应该被设置");
    }
    
    /**
     * 测试添加重复标签
     * 
     * 验证需求：11.2
     */
    @Test
    void addTag_DuplicateTag_ShouldNotAddAgain() {
        // Given
        String tag = "电子产品";
        item.addTag(tag);
        int initialSize = item.getTags().size();
        
        // When
        item.addTag(tag);
        
        // Then
        assertEquals(initialSize, item.getTags().size(), 
                "标签列表大小不应该改变");
    }
    
    /**
     * 测试删除标签
     * 
     * 验证需求：11.2
     */
    @Test
    void removeTag_ExistingTag_ShouldRemoveFromList() {
        // Given
        String tag = "电子产品";
        item.addTag(tag);
        
        // When
        item.removeTag(tag);
        
        // Then
        assertFalse(item.getTags().contains(tag), 
                "标签列表不应该包含已删除的标签");
        assertNotNull(item.getUpdatedAt(), 
                "更新时间应该被设置");
    }
    
    /**
     * 测试删除不存在的标签
     * 
     * 验证需求：11.2
     */
    @Test
    void removeTag_NonExistingTag_ShouldNotThrowException() {
        // Given
        String tag = "不存在的标签";
        
        // When & Then
        assertDoesNotThrow(() -> {
            item.removeTag(tag);
        });
    }
    
    /**
     * 测试添加多个标签
     * 
     * 验证需求：11.2
     */
    @Test
    void addTag_MultipleTags_ShouldAddAll() {
        // Given
        String tag1 = "电子产品";
        String tag2 = "笔记本";
        String tag3 = "办公用品";
        
        // When
        item.addTag(tag1);
        item.addTag(tag2);
        item.addTag(tag3);
        
        // Then
        assertEquals(3, item.getTags().size(), 
                "标签列表应该包含 3 个标签");
        assertTrue(item.getTags().contains(tag1));
        assertTrue(item.getTags().contains(tag2));
        assertTrue(item.getTags().contains(tag3));
    }
}
