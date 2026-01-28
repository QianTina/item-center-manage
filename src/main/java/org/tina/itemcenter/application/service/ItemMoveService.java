package org.tina.itemcenter.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.repository.ItemRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * 物品移动应用服务
 * 
 * 负责物品的移动操作，所有操作都会自动记录审计日志。
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemMoveService {
    
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final AuditService auditService;
    
    /**
     * 移动物品到指定位置
     * 
     * @param itemId 物品 ID
     * @param targetLocationId 目标位置 ID（可为 null，表示移动到无位置状态）
     * @return 移动后的物品
     */
    public Item moveItem(Long itemId, Long targetLocationId) {
        log.debug("移动物品: itemId={}, targetLocationId={}", itemId, targetLocationId);
        
        // 查询物品
        Item item = itemService.getItemById(itemId);
        
        // 记录移动前状态
        Map<String, Object> beforeState = buildItemState(item);
        
        // TODO: 验证目标位置存在且属于当前场景（需要位置管理功能）
        // 目前允许任意位置 ID 或 null
        
        // 执行移动操作（通过领域模型）
        item.moveTo(targetLocationId);
        
        // 保存更新
        Item movedItem = itemRepository.save(item);
        
        // 记录审计日志
        Map<String, Object> afterState = buildItemState(movedItem);
        auditService.log("ITEM", movedItem.getId(), "MOVE", beforeState, afterState);
        
        log.info("物品移动成功: itemId={}, targetLocationId={}", itemId, targetLocationId);
        return movedItem;
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
        state.put("locationId", item.getLocationId());
        state.put("quantity", item.getQuantity());
        state.put("status", item.getStatus() != null ? item.getStatus().name() : null);
        return state;
    }
}
