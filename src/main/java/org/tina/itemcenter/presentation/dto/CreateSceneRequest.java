package org.tina.itemcenter.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建场景请求
 */
public class CreateSceneRequest {
    
    @NotBlank(message = "场景名称不能为空")
    @Size(max = 255, message = "场景名称长度不能超过255")
    private String name;
    
    @NotBlank(message = "所有者不能为空")
    @Size(max = 255, message = "所有者长度不能超过255")
    private String owner;
    
    public CreateSceneRequest() {
    }
    
    public CreateSceneRequest(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }
    
    // Getters and Setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
}
