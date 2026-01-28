package org.tina.itemcenter.domain.model;

/**
 * 物品状态枚举
 * 
 * 定义物品的各种状态
 */
public enum ItemStatus {
    /**
     * 可用状态
     */
    AVAILABLE,
    
    /**
     * 借出状态
     */
    BORROWED,
    
    /**
     * 损坏状态
     */
    DAMAGED,
    
    /**
     * 维护中状态
     */
    MAINTENANCE
}
