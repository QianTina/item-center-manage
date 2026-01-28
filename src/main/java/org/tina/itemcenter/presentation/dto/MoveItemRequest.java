package org.tina.itemcenter.presentation.dto;

import lombok.Data;

/**
 * 移动物品请求 DTO
 */
@Data
public class MoveItemRequest {
    
    /**
     * 目标位置 ID
     * 
     * 可为 null，表示移动到无位置状态
     */
    private Long targetLocationId;
}
