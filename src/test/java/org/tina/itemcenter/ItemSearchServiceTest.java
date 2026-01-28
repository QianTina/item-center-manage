package org.tina.itemcenter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tina.itemcenter.application.service.ItemSearchService;
import org.tina.itemcenter.application.service.ItemService;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.model.ItemStatus;
import org.tina.itemcenter.domain.repository.ItemRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ItemSearchService 单元测试
 * 
 * 测试物品搜索功能的各种场景
 */
@SpringBootTest
public class ItemSearchServiceTest {
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private ItemSearchService itemSearchService;
    
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
     * 测试按关键词搜索 - 匹配名称
     * 
     * 验证需求：7.2
     */
    @Test
    void testSearchByKeyword_MatchName() {
        // 创建测试数据
        itemService.createItem("MacBook Pro", "Apple laptop", null, null, 1);
        itemService.createItem("MacBook Air", "Lightweight laptop", null, null, 1);
        itemService.createItem("iPad Pro", "Apple tablet", null, null, 1);
        
        // 搜索包含 "MacBook" 的物品
        List<Item> results = itemSearchService.searchItems("MacBook", null, null, null, null);
        
        // 验证
        assertEquals(2, results.size(), "应该找到 2 个包含 MacBook 的物品");
        assertTrue(results.stream().allMatch(item -> item.getName().contains("MacBook")),
                "所有结果的名称都应该包含 MacBook");
    }
    
    /**
     * 测试按关键词搜索 - 匹配标签
     * 
     * 验证需求：7.2
     */
    @Test
    void testSearchByKeyword_MatchTags() {
        // 创建测试数据
        itemService.createItem("Item 1", "Description 1", new String[]{"electronics", "laptop"}, null, 1);
        itemService.createItem("Item 2", "Description 2", new String[]{"electronics", "phone"}, null, 1);
        itemService.createItem("Item 3", "Description 3", new String[]{"furniture", "desk"}, null, 1);
        
        // 搜索包含 "electronics" 标签的物品
        List<Item> results = itemSearchService.searchItems("electronics", null, null, null, null);
        
        // 验证
        assertEquals(2, results.size(), "应该找到 2 个包含 electronics 标签的物品");
        assertTrue(results.stream().allMatch(item -> 
                item.getTags() != null && item.getTags().contains("electronics")),
                "所有结果都应该包含 electronics 标签");
    }
    
    /**
     * 测试按关键词搜索 - 匹配描述
     * 
     * 验证需求：7.2
     */
    @Test
    void testSearchByKeyword_MatchDescription() {
        // 创建测试数据
        itemService.createItem("Item A", "High performance computer", null, null, 1);
        itemService.createItem("Item B", "Gaming computer with RGB", null, null, 1);
        itemService.createItem("Item C", "Office desk", null, null, 1);
        
        // 搜索描述中包含 "computer" 的物品
        List<Item> results = itemSearchService.searchItems("computer", null, null, null, null);
        
        // 验证
        assertEquals(2, results.size(), "应该找到 2 个描述中包含 computer 的物品");
        assertTrue(results.stream().allMatch(item -> 
                item.getDescription() != null && item.getDescription().toLowerCase().contains("computer")),
                "所有结果的描述都应该包含 computer");
    }
    
    /**
     * 测试按位置过滤
     * 
     * 验证需求：7.3, 7.4
     */
    @Test
    void testSearchByLocation() {
        // 创建测试数据
        Long location1 = 100L;
        Long location2 = 200L;
        
        itemService.createItem("Item at location 1", null, null, location1, 1);
        itemService.createItem("Another item at location 1", null, null, location1, 1);
        itemService.createItem("Item at location 2", null, null, location2, 1);
        itemService.createItem("Item without location", null, null, null, 1);
        
        // 按位置 1 过滤
        List<Item> results = itemSearchService.searchItems(null, location1, false, null, null);
        
        // 验证
        assertEquals(2, results.size(), "应该找到 2 个位于 location 1 的物品");
        assertTrue(results.stream().allMatch(item -> location1.equals(item.getLocationId())),
                "所有结果都应该位于 location 1");
    }
    
    /**
     * 测试按状态过滤
     * 
     * 验证需求：7.5
     */
    @Test
    void testSearchByStatus() {
        // 创建测试数据
        itemService.createItem("Available item 1", null, null, null, 1);
        itemService.createItem("Available item 2", null, null, null, 1);
        
        Item borrowedItem = itemService.createItem("Borrowed item", null, null, null, 1);
        borrowedItem.markBorrowed("Test User");
        itemRepository.save(borrowedItem);
        
        // 按 AVAILABLE 状态过滤
        List<Item> availableResults = itemSearchService.searchItems(
                null, null, null, ItemStatus.AVAILABLE, null);
        
        // 验证
        assertEquals(2, availableResults.size(), "应该找到 2 个可用的物品");
        assertTrue(availableResults.stream().allMatch(item -> 
                ItemStatus.AVAILABLE.equals(item.getStatus())),
                "所有结果的状态都应该是 AVAILABLE");
        
        // 按 BORROWED 状态过滤
        List<Item> borrowedResults = itemSearchService.searchItems(
                null, null, null, ItemStatus.BORROWED, null);
        
        // 验证
        assertEquals(1, borrowedResults.size(), "应该找到 1 个借出的物品");
        assertEquals(ItemStatus.BORROWED, borrowedResults.get(0).getStatus());
    }
    
    /**
     * 测试按标签过滤
     * 
     * 验证需求：7.6
     */
    @Test
    void testSearchByTags() {
        // 创建测试数据
        itemService.createItem("Item 1", null, 
                new String[]{"electronics", "laptop", "portable"}, null, 1);
        itemService.createItem("Item 2", null, 
                new String[]{"electronics", "phone"}, null, 1);
        itemService.createItem("Item 3", null, 
                new String[]{"furniture"}, null, 1);
        
        // 按标签过滤（物品必须包含所有指定的标签）
        List<String> tags = Arrays.asList("electronics", "laptop");
        List<Item> results = itemSearchService.searchItems(null, null, null, null, tags);
        
        // 验证
        assertEquals(1, results.size(), "应该找到 1 个同时包含 electronics 和 laptop 标签的物品");
        assertTrue(results.get(0).getTags().containsAll(tags),
                "结果应该包含所有指定的标签");
    }
    
    /**
     * 测试搜索结果为空
     * 
     * 验证需求：7.9
     */
    @Test
    void testSearch_EmptyResult() {
        // 创建测试数据
        itemService.createItem("MacBook Pro", "Apple laptop", null, null, 1);
        itemService.createItem("iPad Pro", "Apple tablet", null, null, 1);
        
        // 搜索不存在的关键词
        List<Item> results = itemSearchService.searchItems("Windows", null, null, null, null);
        
        // 验证
        assertNotNull(results, "结果不应该为 null");
        assertTrue(results.isEmpty(), "结果应该为空列表");
        assertEquals(0, results.size(), "结果数量应该为 0");
    }
    
    /**
     * 测试组合搜索条件
     * 
     * 验证多个搜索条件可以组合使用
     */
    @Test
    void testSearch_CombinedCriteria() {
        // 创建测试数据
        Long location1 = 100L;
        
        itemService.createItem("Laptop A", "Gaming laptop", 
                new String[]{"electronics", "gaming"}, location1, 1);
        itemService.createItem("Laptop B", "Office laptop", 
                new String[]{"electronics", "office"}, location1, 1);
        itemService.createItem("Phone", "Gaming phone", 
                new String[]{"electronics", "gaming"}, location1, 1);
        
        // 组合搜索：关键词 + 位置 + 标签
        List<String> tags = Arrays.asList("gaming");
        List<Item> results = itemSearchService.searchItems(
                "laptop", location1, false, null, tags);
        
        // 验证
        assertEquals(1, results.size(), "应该找到 1 个同时满足所有条件的物品");
        Item result = results.get(0);
        assertTrue(result.getName().toLowerCase().contains("laptop"));
        assertEquals(location1, result.getLocationId());
        assertTrue(result.getTags().contains("gaming"));
    }
    
    /**
     * 测试搜索所有物品（无搜索条件）
     * 
     * 验证不提供任何搜索条件时返回所有物品
     */
    @Test
    void testSearch_NoCriteria_ReturnsAll() {
        // 创建测试数据
        itemService.createItem("Item 1", null, null, null, 1);
        itemService.createItem("Item 2", null, null, null, 1);
        itemService.createItem("Item 3", null, null, null, 1);
        
        // 不提供任何搜索条件
        List<Item> results = itemSearchService.searchItems(null, null, null, null, null);
        
        // 验证
        assertEquals(3, results.size(), "应该返回所有 3 个物品");
    }
}
