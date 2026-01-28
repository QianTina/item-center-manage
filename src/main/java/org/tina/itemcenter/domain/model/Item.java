package org.tina.itemcenter.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tina.itemcenter.infrastructure.converter.JsonbConverter;
import org.tina.itemcenter.infrastructure.converter.StringListConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物品聚合根
 * 
 * 封装物品的所有业务规则和状态变更逻辑
 */
@Entity
@Table(name = "item")
@Data
@NoArgsConstructor
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "scene_id", nullable = false)
    private Long sceneId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "tags", columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> tags = new ArrayList<>();
    
    @Column(name = "location_id")
    private Long locationId;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ItemStatus status = ItemStatus.AVAILABLE;
    
    @Column(name = "borrower")
    private String borrower;
    
    @Column(name = "borrowed_at")
    private LocalDateTime borrowedAt;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    @Convert(converter = JsonbConverter.class)
    private Map<String, Object> metadata = new HashMap<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // ============================================
    // 领域行为方法
    // ============================================
    
    /**
     * 移动物品到指定位置
     * 
     * @param targetLocationId 目标位置 ID，可为 null 表示移动到无位置状态
     */
    public void moveTo(Long targetLocationId) {
        this.locationId = targetLocationId;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 标记物品为借出状态
     * 
     * @param borrower 借用人姓名
     * @throws IllegalStateException 如果物品已经处于借出状态
     */
    public void markBorrowed(String borrower) {
        if (this.status == ItemStatus.BORROWED) {
            throw new IllegalStateException("物品已被借出");
        }
        this.status = ItemStatus.BORROWED;
        this.borrower = borrower;
        this.borrowedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 归还物品
     * 
     * @throws IllegalStateException 如果物品未处于借出状态
     */
    public void returnItem() {
        if (this.status != ItemStatus.BORROWED) {
            throw new IllegalStateException("物品未处于借出状态");
        }
        this.status = ItemStatus.AVAILABLE;
        this.borrower = null;
        this.borrowedAt = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新物品数量
     * 
     * @param delta 数量变化值（正数表示增加，负数表示减少）
     * @throws IllegalArgumentException 如果更新后数量为负数
     */
    public void updateQuantity(Integer delta) {
        int newQuantity = this.quantity + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("物品数量不能为负数");
        }
        this.quantity = newQuantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 添加标签
     * 
     * @param tag 标签名称
     */
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 删除标签
     * 
     * @param tag 标签名称
     */
    public void removeTag(String tag) {
        if (this.tags != null) {
            this.tags.remove(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    // ============================================
    // JPA 生命周期回调
    // ============================================
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
