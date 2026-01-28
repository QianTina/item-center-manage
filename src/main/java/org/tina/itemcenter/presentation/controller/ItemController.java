package org.tina.itemcenter.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tina.itemcenter.application.service.ItemService;
import org.tina.itemcenter.application.service.ItemSearchService;
import org.tina.itemcenter.application.service.ItemMoveService;
import org.tina.itemcenter.application.service.ItemBorrowService;
import org.tina.itemcenter.common.response.ApiResponse;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.presentation.dto.CreateItemRequest;
import org.tina.itemcenter.presentation.dto.ItemDTO;
import org.tina.itemcenter.presentation.dto.SearchCriteriaDTO;
import org.tina.itemcenter.presentation.dto.UpdateItemRequest;
import org.tina.itemcenter.presentation.dto.MoveItemRequest;
import org.tina.itemcenter.presentation.dto.BorrowItemRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物品管理 Controller
 * 
 * 提供物品的 CRUD 操作接口
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    
    private final ItemService itemService;
    private final ItemSearchService itemSearchService;
    private final ItemMoveService itemMoveService;
    private final ItemBorrowService itemBorrowService;
    
    /**
     * 创建物品
     * 
     * @param request 创建物品请求
     * @return 创建的物品信息
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ItemDTO> createItem(@Valid @RequestBody CreateItemRequest request) {
        log.info("创建物品请求: name={}", request.getName());
        
        Item item = itemService.createItem(
                request.getName(),
                request.getDescription(),
                request.getTags(),
                request.getLocationId(),
                request.getQuantity()
        );
        
        return ApiResponse.success(ItemDTO.fromDomain(item));
    }
    
    /**
     * 根据 ID 查询物品
     * 
     * @param id 物品 ID
     * @return 物品详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ItemDTO> getItemById(@PathVariable Long id) {
        log.info("查询物品: id={}", id);
        
        Item item = itemService.getItemById(id);
        return ApiResponse.success(ItemDTO.fromDomain(item));
    }
    
    /**
     * 更新物品信息
     * 
     * @param id 物品 ID
     * @param request 更新物品请求
     * @return 更新后的物品信息
     */
    @PutMapping("/{id}")
    public ApiResponse<ItemDTO> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateItemRequest request) {
        log.info("更新物品: id={}", id);
        
        Item item = itemService.updateItem(
                id,
                request.getName(),
                request.getDescription(),
                request.getTags(),
                request.getQuantity()
        );
        
        return ApiResponse.success(ItemDTO.fromDomain(item));
    }
    
    /**
     * 删除物品
     * 
     * @param id 物品 ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteItem(@PathVariable Long id) {
        log.info("删除物品: id={}", id);
        
        itemService.deleteItem(id);
        return ApiResponse.success(null);
    }
    
    /**
     * 搜索物品
     * 
     * 支持多种搜索条件：
     * - 关键词：在名称、标签、描述中进行模糊匹配
     * - 位置过滤：按位置 ID 过滤（可选择是否包含子位置）
     * - 状态过滤：按物品状态过滤
     * - 标签过滤：按标签过滤
     * 
     * @param criteria 搜索条件
     * @return 匹配的物品列表
     */
    @PostMapping("/search")
    public ApiResponse<List<ItemDTO>> searchItems(@RequestBody SearchCriteriaDTO criteria) {
        log.info("搜索物品: keyword={}, locationId={}, status={}", 
                criteria.getKeyword(), criteria.getLocationId(), criteria.getStatus());
        
        List<Item> items = itemSearchService.searchItems(
                criteria.getKeyword(),
                criteria.getLocationId(),
                criteria.getIncludeSubLocations(),
                criteria.getStatus(),
                criteria.getTags()
        );
        
        List<ItemDTO> itemDTOs = items.stream()
                .map(ItemDTO::fromDomain)
                .collect(Collectors.toList());
        
        return ApiResponse.success(itemDTOs);
    }
    
    /**
     * 移动物品
     * 
     * @param id 物品 ID
     * @param request 移动物品请求
     * @return 移动后的物品信息
     */
    @PostMapping("/{id}/move")
    public ApiResponse<ItemDTO> moveItem(
            @PathVariable Long id,
            @RequestBody MoveItemRequest request) {
        log.info("移动物品: id={}, targetLocationId={}", id, request.getTargetLocationId());
        
        Item item = itemMoveService.moveItem(id, request.getTargetLocationId());
        return ApiResponse.success(ItemDTO.fromDomain(item));
    }
    
    /**
     * 借出物品
     * 
     * @param id 物品 ID
     * @param request 借出物品请求
     * @return 借出后的物品信息
     */
    @PostMapping("/{id}/borrow")
    public ApiResponse<ItemDTO> borrowItem(
            @PathVariable Long id,
            @Valid @RequestBody BorrowItemRequest request) {
        log.info("借出物品: id={}, borrower={}", id, request.getBorrower());
        
        Item item = itemBorrowService.borrowItem(id, request.getBorrower());
        return ApiResponse.success(ItemDTO.fromDomain(item));
    }
    
    /**
     * 归还物品
     * 
     * @param id 物品 ID
     * @return 归还后的物品信息
     */
    @PostMapping("/{id}/return")
    public ApiResponse<ItemDTO> returnItem(@PathVariable Long id) {
        log.info("归还物品: id={}", id);
        
        Item item = itemBorrowService.returnItem(id);
        return ApiResponse.success(ItemDTO.fromDomain(item));
    }
}
