package org.tina.itemcenter.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 借出物品请求 DTO
 */
@Data
public class BorrowItemRequest {
    
    /**
     * 借用人
     * 
     * 必填字段
     */
    @NotBlank(message = "借用人不能为空")
    private String borrower;
}
