package org.tina.itemcenter.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.model.ItemStatus;
import org.tina.itemcenter.domain.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物品搜索应用服务
 * 
 * 负责物品搜索功能，支持多字段模糊匹配、位置过滤、状态过滤和标签过滤。
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemSearchService {
    
    private final ItemRepository itemRepository;
    
    /**
     * 搜索物品
     * 
     * 支持以下搜索条件：
     * - 关键词：在名称、标签、描述中进行模糊匹配
     * - 位置过滤：按位置 ID 过滤（可选择是否包含子位置）
     * - 状态过滤：按物品状态过滤
     * - 标签过滤：按标签过滤
     * 
     * @param keyword 搜索关键词（可选）
     * @param locationId 位置 ID（可选）
     * @param includeSubLocations 是否包含子位置（仅当 locationId 不为 null 时有效）
     * @param status 物品状态（可选）
     * @param tags 标签列表（可选）
     * @return 匹配的物品列表
     */
    public List<Item> searchItems(String keyword, 
                                  Long locationId, 
                                  Boolean includeSubLocations, 
                                  ItemStatus status, 
                                  List<String> tags) {
        log.debug("搜索物品: keyword={}, locationId={}, includeSubLocations={}, status={}, tags={}", 
                keyword, locationId, includeSubLocations, status, tags);
        
        // 获取基础搜索结果
        List<Item> items;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 如果提供了关键词，使用关键词搜索
            items = itemRepository.searchByKeyword(keyword.trim());
        } else {
            // 如果没有关键词，查询所有物品
            items = itemRepository.findAll();
        }
        
        // 应用位置过滤
        if (locationId != null) {
            items = filterByLocation(items, locationId, includeSubLocations);
        }
        
        // 应用状态过滤
        if (status != null) {
            items = filterByStatus(items, status);
        }
        
        // 应用标签过滤
        if (tags != null && !tags.isEmpty()) {
            items = filterByTags(items, tags);
        }
        
        log.info("搜索完成，找到 {} 个物品", items.size());
        return items;
    }
    
    /**
     * 按位置过滤物品
     * 
     * @param items 物品列表
     * @param locationId 位置 ID
     * @param includeSubLocations 是否包含子位置
     * @return 过滤后的物品列表
     */
    private List<Item> filterByLocation(List<Item> items, Long locationId, Boolean includeSubLocations) {
        if (includeSubLocations != null && includeSubLocations) {
            // TODO: 实现包含子位置的过滤逻辑
            // 这需要查询位置树结构，暂时只过滤直接位置
            log.warn("包含子位置的过滤功能尚未实现，暂时只过滤直接位置");
            return items.stream()
                    .filter(item -> locationId.equals(item.getLocationId()))
                    .collect(Collectors.toList());
        } else {
            // 只过滤直接位置
            return items.stream()
                    .filter(item -> locationId.equals(item.getLocationId()))
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * 按状态过滤物品
     * 
     * @param items 物品列表
     * @param status 物品状态
     * @return 过滤后的物品列表
     */
    private List<Item> filterByStatus(List<Item> items, ItemStatus status) {
        return items.stream()
                .filter(item -> status.equals(item.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * 按标签过滤物品
     * 
     * 物品必须包含所有指定的标签才会被保留
     * 
     * @param items 物品列表
     * @param tags 标签列表
     * @return 过滤后的物品列表
     */
    private List<Item> filterByTags(List<Item> items, List<String> tags) {
        return items.stream()
                .filter(item -> {
                    if (item.getTags() == null || item.getTags().isEmpty()) {
                        return false;
                    }
                    // 物品必须包含所有指定的标签
                    return item.getTags().containsAll(tags);
                })
                .collect(Collectors.toList());
    }
}
