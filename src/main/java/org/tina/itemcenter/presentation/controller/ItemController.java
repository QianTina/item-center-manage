package org.tina.itemcenter.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tina.itemcenter.application.service.ItemService;
import org.tina.itemcenter.common.response.ApiResponse;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.presentation.dto.CreateItemRequest;
import org.tina.itemcenter.presentation.dto.ItemDTO;
import org.tina.itemcenter.presentation.dto.UpdateItemRequest;

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
}
