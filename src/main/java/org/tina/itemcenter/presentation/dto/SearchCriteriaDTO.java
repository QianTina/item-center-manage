package org.tina.itemcenter.presentation.dto;

import lombok.Data;
import org.tina.itemcenter.domain.model.ItemStatus;

import java.util.List;

/**
 * 搜索条件 DTO
 * 
 * 用于接收物品搜索请求的参数
 */
@Data
public class SearchCriteriaDTO {
    
    /**
     * 搜索关键词
     * 
     * 在物品名称、标签、描述中进行模糊匹配
     */
    private String keyword;
    
    /**
     * 位置 ID
     * 
     * 按位置过滤搜索结果
     */
    private Long locationId;
    
    /**
     * 是否包含子位置
     * 
     * 当 locationId 不为 null 时有效
     * 如果为 true，则搜索该位置及其所有子位置下的物品
     */
    private Boolean includeSubLocations = false;
    
    /**
     * 物品状态
     * 
     * 按状态过滤搜索结果
     */
    private ItemStatus status;
    
    /**
     * 标签列表
     * 
     * 按标签过滤搜索结果
     * 物品必须包含所有指定的标签
     */
    private List<String> tags;
}
