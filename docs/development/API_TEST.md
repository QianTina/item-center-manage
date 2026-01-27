# API 测试指南

## 启动应用

```bash
./mvnw spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## 场景管理 API 测试

### 1. 创建场景

```bash
curl -X POST http://localhost:8080/api/scenes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "我的家",
    "owner": "张三"
  }'
```

**预期响应**：
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "我的家",
    "owner": "张三",
    "createdAt": "2026-01-27T17:30:00",
    "updatedAt": "2026-01-27T17:30:00"
  },
  "message": "场景创建成功",
  "timestamp": "2026-01-27T17:30:00"
}
```

### 2. 查询所有场景

```bash
curl -X GET http://localhost:8080/api/scenes
```

**预期响应**：
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "我的家",
      "owner": "张三",
      "createdAt": "2026-01-27T17:30:00",
      "updatedAt": "2026-01-27T17:30:00"
    }
  ],
  "message": "查询成功",
  "timestamp": "2026-01-27T17:30:00"
}
```

### 3. 根据 ID 查询场景

```bash
curl -X GET http://localhost:8080/api/scenes/1
```

**预期响应**：
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "我的家",
    "owner": "张三",
    "createdAt": "2026-01-27T17:30:00",
    "updatedAt": "2026-01-27T17:30:00"
  },
  "message": "查询成功",
  "timestamp": "2026-01-27T17:30:00"
}
```

### 4. 测试参数校验（缺少必填字段）

```bash
curl -X POST http://localhost:8080/api/scenes \
  -H "Content-Type: application/json" \
  -d '{
    "name": ""
  }'
```

**预期响应**：
```json
{
  "success": false,
  "data": {
    "name": "场景名称不能为空",
    "owner": "所有者不能为空"
  },
  "message": "参数校验失败",
  "timestamp": "2026-01-27T17:30:00"
}
```

### 5. 测试查询不存在的场景

```bash
curl -X GET http://localhost:8080/api/scenes/999
```

**预期响应**：
```json
{
  "success": false,
  "data": null,
  "message": "Scene with id 999 not found",
  "timestamp": "2026-01-27T17:30:00"
}
```

## 使用 Postman 测试

1. 导入以下 Postman Collection：

```json
{
  "info": {
    "name": "物品管理系统 API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "创建场景",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"我的家\",\n  \"owner\": \"张三\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/scenes",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "scenes"]
        }
      }
    },
    {
      "name": "查询所有场景",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:8080/api/scenes",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "scenes"]
        }
      }
    },
    {
      "name": "根据 ID 查询场景",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:8080/api/scenes/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "scenes", "1"]
        }
      }
    }
  ]
}
```

## 注意事项

1. **场景管理接口不需要 X-Scene-Id Header**，因为场景本身就是数据隔离的基础
2. 其他业务接口（物品、位置等）都需要携带 `X-Scene-Id` Header
3. 所有响应都遵循统一的响应格式：`{ success, data, message, timestamp }`
4. 参数校验失败会返回详细的字段错误信息

## 下一步

创建场景后，记住场景 ID，后续的物品和位置管理接口都需要在 Header 中携带这个场景 ID：

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -H "X-Scene-Id: 1" \
  -d '{...}'
```
