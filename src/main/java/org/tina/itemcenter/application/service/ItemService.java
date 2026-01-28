package org.tina.itemcenter.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.common.exception.EntityNotFoundException;
import org.tina.itemcenter.domain.model.Item;

import org.tina.itemcenter.domain.repository.ItemRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * 物品管理应用服务
 * 
 * 负责编排物品的业务流程，包括创建、查询、更新、删除等操作。
 * 所有操作都会自动记录审计日志。
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final AuditService auditService;
    
    /**
     * 创建物品
     * 
     * @param name 物品名称（必填）
     * @param description 物品描述（可选）
     * @param tags 标签列表（可选）
     * @param locationId 位置 ID（可选）
     * @param quantity 数量（可选，默认为 1）
     * @return 创建的物品
     */
    public Item createItem(String name, String description, String[] tags, Long locationId, Integer quantity) {
        log.debug("创建物品: name={}, locationId={}", name, locationId);
        
        // 验证必填字段
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("物品名称不能为空");
        }
        
        // 创建物品实体
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setLocationId(locationId);
        
        // 设置标签
        if (tags != null && tags.length > 0) {
            for (String tag : tags) {
                item.addTag(tag);
            }
        }
        
        // 设置数量（默认为 1）
        if (quantity != null && quantity > 0) {
            item.setQuantity(quantity);
        }
        
        // 自动设置场景 ID
        item.setSceneId(SceneContext.getSceneId());
        
        // 保存物品
        Item savedItem = itemRepository.save(item);
        
        // 记录审计日志
        Map<String, Object> afterState = buildItemState(savedItem);
        auditService.log("ITEM", savedItem.getId(), "CREATE", null, afterState);
        
        log.info("物品创建成功: id={}, name={}", savedItem.getId(), savedItem.getName());
        return savedItem;
    }
    
    /**
     * 根据 ID 查询物品
     * 
     * @param id 物品 ID
     * @return 物品实体
     * @throws EntityNotFoundException 如果物品不存在
     */
    @Transactional(readOnly = true)
    public Item getItemById(Long id) {
        log.debug("查询物品: id={}", id);
        
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("物品不存在: id=" + id));
    }
    
    /**
     * 更新物品信息
     * 
     * @param id 物品 ID
     * @param name 物品名称（可选）
     * @param description 物品描述（可选）
     * @param tags 标签列表（可选）
     * @param quantity 数量（可选）
     * @return 更新后的物品
     * @throws EntityNotFoundException 如果物品不存在
     */
    public Item updateItem(Long id, String name, String description, String[] tags, Integer quantity) {
        log.debug("更新物品: id={}", id);
        
        // 查询物品
        Item item = getItemById(id);
        
        // 记录更新前状态
        Map<String, Object> beforeState = buildItemState(item);
        
        // 更新字段
        if (name != null) {
            item.setName(name);
        }
        
        if (description != null) {
            item.setDescription(description);
        }
        
        // 更新标签（完全替换）
        if (tags != null) {
            // 清空现有标签
            if (item.getTags() != null) {
                item.getTags().clear();
            }
            // 添加新标签
            for (String tag : tags) {
                item.addTag(tag);
            }
        }
        
        // 更新数量
        if (quantity != null) {
            item.setQuantity(quantity);
        }
        
        // 保存更新
        Item updatedItem = itemRepository.save(item);
        
        // 记录审计日志
        Map<String, Object> afterState = buildItemState(updatedItem);
        auditService.log("ITEM", updatedItem.getId(), "UPDATE", beforeState, afterState);
        
        log.info("物品更新成功: id={}", updatedItem.getId());
        return updatedItem;
    }
    
    /**
     * 删除物品
     * 
     * @param id 物品 ID
     * @throws EntityNotFoundException 如果物品不存在
     */
    public void deleteItem(Long id) {
        log.debug("删除物品: id={}", id);
        
        // 查询物品
        Item item = getItemById(id);
        
        // 记录删除前状态
        Map<String, Object> beforeState = buildItemState(item);
        
        // 执行物理删除
        itemRepository.delete(item);
        
        // 记录审计日志
        auditService.log("ITEM", id, "DELETE", beforeState, null);
        
        log.info("物品删除成功: id={}", id);
    }
    
    /**
     * 构建物品状态（用于审计日志）
     * 
     * @param item 物品实体
     * @return 物品状态 Map
     */
    private Map<String, Object> buildItemState(Item item) {
        Map<String, Object> state = new HashMap<>();
        state.put("id", item.getId());
        state.put("sceneId", item.getSceneId());
        state.put("name", item.getName());
        state.put("description", item.getDescription());
        state.put("tags", item.getTags());
        state.put("locationId", item.getLocationId());
        state.put("quantity", item.getQuantity());
        state.put("status", item.getStatus() != null ? item.getStatus().name() : null);
        state.put("borrower", item.getBorrower());
        state.put("borrowedAt", item.getBorrowedAt() != null ? item.getBorrowedAt().toString() : null);
        return state;
    }
}
