package org.tina.itemcenter.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 审计日志 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    
    private Long id;
    
    private String entityType;
    
    private Long entityId;
    
    private String operationType;
    
    private Map<String, Object> beforeState;
    
    private Map<String, Object> afterState;
    
    private String operator;
    
    private LocalDateTime operatedAt;
}
