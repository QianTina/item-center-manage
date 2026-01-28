package org.tina.itemcenter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tina.itemcenter.application.service.ItemSearchService;
import org.tina.itemcenter.application.service.ItemService;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.model.ItemStatus;
import org.tina.itemcenter.domain.repository.ItemRepository;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 物品搜索属性测试
 * 
 * 验证属性 8、10、13
 */
@SpringBootTest
@Tag("Feature: item-management-system, Property 8, 10, 13: 物品搜索")
public class ItemSearchPropertyTest {
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private ItemSearchService itemSearchService;
    
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
     * 属性 8：物品搜索多字段模糊匹配
     * 
     * 对于任意搜索关键词，搜索结果中的所有物品必须在名称、标签或描述字段中
     * 至少有一个字段包含该关键词（不区分大小写）。
     * 
     * 验证需求：7.2
     */
    @Test
    void itemSearch_MustMatchNameTagsOrDescription() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 生成随机关键词
            String keyword = generateRandomString(3, 6);
            
            // 创建包含关键词的物品（在名称中）
            String nameWithKeyword = "Item " + keyword + " test";
            itemService.createItem(nameWithKeyword, null, null, null, 1);
            
            // 创建包含关键词的物品（在描述中）
            String descWithKeyword = "Description with " + keyword;
            itemService.createItem("Another item", descWithKeyword, null, null, 1);
            
            // 创建包含关键词的物品（在标签中）
            String[] tagsWithKeyword = {keyword, "other-tag"};
            itemService.createItem("Third item", null, tagsWithKeyword, null, 1);
            
            // 创建不包含关键词的物品
            itemService.createItem("Unrelated item", "No match here", new String[]{"unrelated"}, null, 1);
            
            // 执行搜索
            List<Item> results = itemSearchService.searchItems(keyword, null, null, null, null);
            
            // 验证：搜索结果应该包含 3 个物品（不包含不相关的物品）
            assertEquals(3, results.size(), "搜索结果应该包含 3 个匹配的物品");
            
            // 验证：所有结果都包含关键词（在名称、标签或描述中）
            for (Item item : results) {
                boolean matchesName = item.getName() != null && 
                        item.getName().toLowerCase().contains(keyword.toLowerCase());
                boolean matchesDescription = item.getDescription() != null && 
                        item.getDescription().toLowerCase().contains(keyword.toLowerCase());
                boolean matchesTags = item.getTags() != null && 
                        item.getTags().stream().anyMatch(tag -> 
                                tag.toLowerCase().contains(keyword.toLowerCase()));
                
                assertTrue(matchesName || matchesDescription || matchesTags,
                        "搜索结果中的物品必须在名称、标签或描述中包含关键词");
            }
        }
    }
    
    /**
     * 属性 8：物品搜索多字段模糊匹配（大小写不敏感）
     * 
     * 验证搜索不区分大小写
     */
    @Test
    void itemSearch_MustBeCaseInsensitive() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 创建物品（使用大写关键词）
            itemService.createItem("LAPTOP Computer", null, null, null, 1);
            itemService.createItem("laptop device", null, null, null, 1);
            itemService.createItem("LaPtOp machine", null, null, null, 1);
            
            // 使用小写关键词搜索
            List<Item> results = itemSearchService.searchItems("laptop", null, null, null, null);
            
            // 验证：应该找到所有 3 个物品（不区分大小写）
            assertEquals(3, results.size(), "搜索应该不区分大小写");
        }
    }
    
    /**
     * 属性 10：位置过滤包含子位置
     * 
     * 对于任意按位置过滤的搜索或查询，如果用户选择包含子位置，
     * 则结果必须包含该位置及其所有子位置（任意深度）下的物品。
     * 
     * 验证需求：7.4, 18.4
     * 
     * 注意：此属性测试目前只验证直接位置过滤，因为子位置功能尚未实现
     */
    @Test
    void itemSearch_LocationFilter_DirectLocationOnly() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 生成随机位置 ID
            Long locationId = 100L + random.nextInt(100);
            Long otherLocationId = 200L + random.nextInt(100);
            
            // 在指定位置创建物品
            itemService.createItem("Item at location", null, null, locationId, 1);
            itemService.createItem("Another item at location", null, null, locationId, 1);
            
            // 在其他位置创建物品
            itemService.createItem("Item at other location", null, null, otherLocationId, 1);
            
            // 创建没有位置的物品
            itemService.createItem("Item without location", null, null, null, 1);
            
            // 按位置过滤搜索
            List<Item> results = itemSearchService.searchItems(null, locationId, false, null, null);
            
            // 验证：应该只返回指定位置的物品
            assertEquals(2, results.size(), "应该只返回指定位置的物品");
            
            // 验证：所有结果都属于指定位置
            for (Item item : results) {
                assertEquals(locationId, item.getLocationId(), 
                        "搜索结果中的物品必须属于指定位置");
            }
        }
    }
    
    /**
     * 属性 13：借出物品仍可查询
     * 
     * 对于任意处于 BORROWED 状态的物品，仍然可以通过查询和搜索功能找到。
     * 
     * 验证需求：9.7
     */
    @Test
    void itemSearch_BorrowedItems_MustBeSearchable() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 创建物品
            String keyword = generateRandomString(4, 8);
            Item item = itemService.createItem("Item " + keyword, null, null, null, 1);
            
            // 借出物品
            item.markBorrowed("Test User");
            itemRepository.save(item);
            
            // 验证物品状态为 BORROWED
            assertEquals(ItemStatus.BORROWED, item.getStatus());
            
            // 搜索物品（使用关键词）
            List<Item> results = itemSearchService.searchItems(keyword, null, null, null, null);
            
            // 验证：借出的物品仍然可以被搜索到
            assertEquals(1, results.size(), "借出的物品应该可以被搜索到");
            assertEquals(item.getId(), results.get(0).getId());
            assertEquals(ItemStatus.BORROWED, results.get(0).getStatus());
            
            // 搜索所有物品（不使用关键词）
            List<Item> allResults = itemSearchService.searchItems(null, null, null, null, null);
            
            // 验证：借出的物品在查询所有物品时也能被找到
            assertEquals(1, allResults.size(), "借出的物品应该可以被查询到");
            assertEquals(ItemStatus.BORROWED, allResults.get(0).getStatus());
        }
    }
    
    /**
     * 属性 13：借出物品仍可查询（按状态过滤）
     * 
     * 验证可以通过状态过滤找到借出的物品
     */
    @Test
    void itemSearch_BorrowedItems_CanFilterByStatus() {
        // 运行 100 次迭代
        for (int i = 0; i < 100; i++) {
            // 清理数据
            itemRepository.deleteAll();
            
            // 创建可用物品
            itemService.createItem("Available item", null, null, null, 1);
            
            // 创建借出物品
            Item borrowedItem = itemService.createItem("Borrowed item", null, null, null, 1);
            borrowedItem.markBorrowed("Test User");
            itemRepository.save(borrowedItem);
            
            // 按 BORROWED 状态过滤搜索
            List<Item> borrowedResults = itemSearchService.searchItems(
                    null, null, null, ItemStatus.BORROWED, null);
            
            // 验证：应该只返回借出的物品
            assertEquals(1, borrowedResults.size(), "应该找到 1 个借出的物品");
            assertEquals(ItemStatus.BORROWED, borrowedResults.get(0).getStatus());
            
            // 按 AVAILABLE 状态过滤搜索
            List<Item> availableResults = itemSearchService.searchItems(
                    null, null, null, ItemStatus.AVAILABLE, null);
            
            // 验证：应该只返回可用的物品
            assertEquals(1, availableResults.size(), "应该找到 1 个可用的物品");
            assertEquals(ItemStatus.AVAILABLE, availableResults.get(0).getStatus());
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
