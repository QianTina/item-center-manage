package org.tina.itemcenter.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tina.itemcenter.domain.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Item Repository 接口
 * 
 * 所有查询方法自动注入场景 ID，实现场景隔离
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    /**
     * 查询当前场景下的所有物品
     * 
     * 自动注入场景 ID，只返回当前场景的数据
     * 
     * @return 当前场景下的所有物品列表
     */
    @Query("SELECT i FROM Item i WHERE i.sceneId = :#{T(org.tina.itemcenter.common.context.SceneContext).getSceneId()}")
    List<Item> findAll();
    
    /**
     * 根据 ID 查询物品
     * 
     * 自动注入场景 ID，确保只能查询当前场景的物品
     * 
     * @param id 物品 ID
     * @return 物品对象（如果存在且属于当前场景）
     */
    @Query("SELECT i FROM Item i WHERE i.id = :id AND i.sceneId = :#{T(org.tina.itemcenter.common.context.SceneContext).getSceneId()}")
    Optional<Item> findById(@Param("id") Long id);
    
    /**
     * 根据位置 ID 查询物品
     * 
     * 自动注入场景 ID，只返回当前场景指定位置的物品
     * 
     * @param locationId 位置 ID
     * @return 指定位置的物品列表
     */
    @Query("SELECT i FROM Item i WHERE i.locationId = :locationId AND i.sceneId = :#{T(org.tina.itemcenter.common.context.SceneContext).getSceneId()}")
    List<Item> findByLocationId(@Param("locationId") Long locationId);
    
    /**
     * 根据关键词搜索物品
     * 
     * 支持多字段模糊匹配：名称、标签、描述
     * 使用 H2 兼容的 JSON 查询语法进行标签搜索
     * 自动注入场景 ID，只搜索当前场景的物品
     * 
     * @param keyword 搜索关键词
     * @return 匹配的物品列表
     */
    @Query(value = "SELECT * FROM item i WHERE i.scene_id = :#{T(org.tina.itemcenter.common.context.SceneContext).getSceneId()} " +
           "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(CAST(i.tags AS VARCHAR)) LIKE LOWER(CONCAT('%', :keyword, '%')))", 
           nativeQuery = true)
    List<Item> searchByKeyword(@Param("keyword") String keyword);
}
