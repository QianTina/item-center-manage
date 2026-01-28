package org.tina.itemcenter.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.tina.itemcenter.application.service.AuditService;
import org.tina.itemcenter.common.response.ApiResponse;
import org.tina.itemcenter.domain.model.AuditLog;
import org.tina.itemcenter.presentation.dto.AuditLogDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 审计日志 Controller
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
    
    private final AuditService auditService;
    
    /**
     * 查询审计日志（分页）
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 审计日志分页数据
     */
    @GetMapping
    public ApiResponse<Page<AuditLogDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogs = auditService.findAll(pageable);
        
        Page<AuditLogDTO> dtoPage = auditLogs.map(this::toDTO);
        
        return ApiResponse.success(dtoPage, "查询审计日志成功");
    }
    
    /**
     * 查询实体历史记录
     * 
     * @param type 实体类型（ITEM 或 LOCATION）
     * @param id 实体 ID
     * @return 实体的历史记录列表
     */
    @GetMapping("/entity/{type}/{id}")
    public ApiResponse<List<AuditLogDTO>> findEntityHistory(
            @PathVariable String type,
            @PathVariable Long id) {
        
        List<AuditLog> history = auditService.findEntityHistory(type, id);
        
        List<AuditLogDTO> dtoList = history.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return ApiResponse.success(dtoList, "查询实体历史记录成功");
    }
    
    /**
     * 将 AuditLog 实体转换为 DTO
     */
    private AuditLogDTO toDTO(AuditLog auditLog) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(auditLog.getId());
        dto.setEntityType(auditLog.getEntityType());
        dto.setEntityId(auditLog.getEntityId());
        dto.setOperationType(auditLog.getOperationType());
        dto.setBeforeState(auditLog.getBeforeState());
        dto.setAfterState(auditLog.getAfterState());
        dto.setOperator(auditLog.getOperator());
        dto.setOperatedAt(auditLog.getOperatedAt());
        return dto;
    }
}
