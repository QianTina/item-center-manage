package org.tina.itemcenter.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tina.itemcenter.domain.model.Item;
import org.tina.itemcenter.domain.model.ItemStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物品 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    
    private Long id;
    private Long sceneId;
    private String name;
    private String description;
    private List<String> tags;
    private Long locationId;
    private String locationPath;  // 位置路径（完整路径，如 "/家/书房/书桌"）
    private Integer quantity;
    private ItemStatus status;
    private String borrower;
    private LocalDateTime borrowedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 从领域模型转换为 DTO
     */
    public static ItemDTO fromDomain(Item item) {
        return fromDomain(item, null);
    }
    
    /**
     * 从领域模型转换为 DTO（包含位置路径）
     * 
     * @param item 物品实体
     * @param locationPath 位置路径（可选）
     */
    public static ItemDTO fromDomain(Item item, String locationPath) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setSceneId(item.getSceneId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setTags(item.getTags());
        dto.setLocationId(item.getLocationId());
        dto.setLocationPath(locationPath);  // 设置位置路径
        dto.setQuantity(item.getQuantity());
        dto.setStatus(item.getStatus());
        dto.setBorrower(item.getBorrower());
        dto.setBorrowedAt(item.getBorrowedAt());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }
}
