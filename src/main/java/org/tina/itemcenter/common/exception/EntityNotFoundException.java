package org.tina.itemcenter.common.exception;

/**
 * 实体不存在异常
 * 当查询的实体不存在或不属于当前场景时抛出
 */
public class EntityNotFoundException extends BusinessException {
    
    public EntityNotFoundException(String entityType, Long entityId) {
        super("ENTITY_NOT_FOUND", 
              String.format("%s with id %d not found", entityType, entityId));
    }
    
    public EntityNotFoundException(String message) {
        super("ENTITY_NOT_FOUND", message);
    }
}
