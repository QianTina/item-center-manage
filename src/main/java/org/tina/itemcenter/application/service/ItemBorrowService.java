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
 * 物品借出归还应用服务
 * 
 * 负责物品的借出和归还操作，所有操作都会自动记录审计日志。
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemBorrowService {
    
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final AuditService auditService;
    
    /**
     * 借出物品
     * 
     * @param itemId 物品 ID
     * @param borrower 借用人
     * @return 借出后的物品
     */
    public Item borrowItem(Long itemId, String borrower) {
        log.debug("借出物品: itemId={}, borrower={}", itemId, borrower);
        
        // 查询物品
        Item item = itemService.getItemById(itemId);
        
        // 记录借出前状态
        Map<String, Object> beforeState = buildItemState(item);
        
        // 执行借出操作（通过领域模型）
        item.markBorrowed(borrower);
        
        // 保存更新
        Item borrowedItem = itemRepository.save(item);
        
        // 记录审计日志
        Map<String, Object> afterState = buildItemState(borrowedItem);
        auditService.log("ITEM", borrowedItem.getId(), "BORROW", beforeState, afterState);
        
        log.info("物品借出成功: itemId={}, borrower={}", itemId, borrower);
        return borrowedItem;
    }
    
    /**
     * 归还物品
     * 
     * @param itemId 物品 ID
     * @return 归还后的物品
     */
    public Item returnItem(Long itemId) {
        log.debug("归还物品: itemId={}", itemId);
        
        // 查询物品
        Item item = itemService.getItemById(itemId);
        
        // 记录归还前状态
        Map<String, Object> beforeState = buildItemState(item);
        
        // 执行归还操作（通过领域模型）
        item.returnItem();
        
        // 保存更新
        Item returnedItem = itemRepository.save(item);
        
        // 记录审计日志
        Map<String, Object> afterState = buildItemState(returnedItem);
        auditService.log("ITEM", returnedItem.getId(), "RETURN", beforeState, afterState);
        
        log.info("物品归还成功: itemId={}", itemId);
        return returnedItem;
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
        state.put("borrower", item.getBorrower());
        state.put("borrowedAt", item.getBorrowedAt() != null ? item.getBorrowedAt().toString() : null);
        return state;
    }
}
