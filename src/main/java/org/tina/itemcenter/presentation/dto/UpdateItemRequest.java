package org.tina.itemcenter.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新物品请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemRequest {
    
    @Size(max = 255, message = "物品名称长度不能超过 255 个字符")
    private String name;
    
    @Size(max = 5000, message = "物品描述长度不能超过 5000 个字符")
    private String description;
    
    private String[] tags;
    
    @Min(value = 1, message = "物品数量必须大于 0")
    private Integer quantity;
}
