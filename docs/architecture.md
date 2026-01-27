# 物品管理系统 · 整体架构设计

## 1. 系统概述

### 1.1 系统定位
物品管理系统（Item Center）是一个面向真实生活场景的物品追踪与查找系统，核心目标是**让用户快速找到物品**。

### 1.2 核心设计原则
- **Findability First**：搜索优先，快速定位
- **场景隔离**：多场景数据完全隔离
- **弱耦合关系**：物品与位置松耦合
- **容错性**：允许不完整数据，不强迫用户"正确使用"
- **可审计**：所有关键操作可追溯

### 1.3 技术栈
- **框架**：Spring Boot 4.0.1 + Spring MVC
- **持久化**：Spring Data JPA + MySQL
- **语言**：Java 17
- **工具**：Lombok、Validation

---

## 2. 架构分层

### 2.1 整体分层结构

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│    (Controller / API / DTO)             │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│       Application Service Layer         │
│   (Use Case / Orchestration)            │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│          Domain Model Layer             │
│   (Entity / Value Object / Rule)        │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│       Infrastructure Layer              │
│  (Repository / DB / Cache / MQ)         │
└─────────────────────────────────────────┘
```


### 2.2 各层职责

#### Presentation Layer（表现层）
- 接收 HTTP 请求，提取 `X-Scene-Id` Header
- 参数校验（JSR-303 Validation）
- 调用 Application Service
- 返回统一响应格式

**约束**：
- Controller 必须是"瘦"的，不包含业务逻辑
- 不直接访问 Repository

#### Application Service Layer（应用服务层）
- 编排业务流程
- 事务边界控制
- 调用领域模型执行业务规则
- 协调多个聚合根

**约束**：
- 不包含业务规则判断
- 业务规则必须在 Domain Model 中

#### Domain Model Layer（领域模型层）
- 封装业务规则和状态变更
- 提供语义明确的行为方法
- 保证领域不变性

**约束**：
- 必须是"有行为的"，禁止贫血模型
- 不依赖外部基础设施（DB/Cache/HTTP）

#### Infrastructure Layer（基础设施层）
- 数据持久化
- 外部服务调用
- 缓存、消息队列等

**约束**：
- Repository 自动注入 `scene_id`，不暴露给调用方
- 禁止动态拼接 SQL

---

## 3. 核心领域模型

### 3.1 聚合根设计

#### Item（物品）聚合根
```
Item
├── id: Long
├── sceneId: Long              【强制】场景 ID
├── name: String               物品名称
├── description: String        描述
├── tags: List<String>         标签
├── locationId: Long           【可空】位置 ID
├── quantity: Integer          数量
├── status: ItemStatus         状态（AVAILABLE/BORROWED/DAMAGED）
├── borrower: String           【可空】借用人
├── borrowedAt: LocalDateTime  【可空】借出时间
└── metadata: Map              扩展字段

行为方法：
- moveTo(locationId)           移动到位置
- markBorrowed(borrower)       标记借出
- returnItem()                 归还
- updateQuantity(delta)        更新数量
- addTag(tag)                  添加标签
```

#### Location（位置）聚合根
```
Location
├── id: Long
├── sceneId: Long              【强制】场景 ID
├── name: String               位置名称
├── parentId: Long             【可空】父位置 ID
├── path: String               完整路径（如：/home/bedroom/desk）
├── level: Integer             层级深度
└── metadata: Map              扩展字段

行为方法：
- moveTo(newParentId)          移动到新父节点
- getFullPath()                获取完整路径
- isDescendantOf(locationId)   判断是否为子节点
```


#### AuditLog（审计日志）
```
AuditLog
├── id: Long
├── sceneId: Long              【强制】场景 ID
├── entityType: String         实体类型（ITEM/LOCATION）
├── entityId: Long             实体 ID
├── operationType: String      操作类型（CREATE/UPDATE/DELETE/MOVE）
├── beforeState: JSON          操作前状态
├── afterState: JSON           操作后状态
├── operator: String           操作人
└── operatedAt: LocalDateTime  操作时间
```

### 3.2 值对象设计

#### ItemStatus（物品状态）
```java
enum ItemStatus {
    AVAILABLE,   // 可用
    BORROWED,    // 借出
    DAMAGED,     // 损坏
    MAINTENANCE  // 维护中
}
```

#### SearchCriteria（搜索条件）
```
SearchCriteria
├── keyword: String            关键词（匹配名称/标签/描述）
├── locationId: Long           位置过滤
├── includeSubLocations: Boolean  是否包含子位置
├── status: ItemStatus         状态过滤
└── tags: List<String>         标签过滤
```

---

## 4. 场景隔离机制

### 4.1 SceneContext 设计
```java
public class SceneContext {
    private static final ThreadLocal<Long> SCENE_ID = new ThreadLocal<>();
    
    public static void setSceneId(Long sceneId) { ... }
    public static Long getSceneId() { ... }
    public static void clear() { ... }
}
```

### 4.2 场景拦截器
```
SceneInterceptor
├── 从 Header 提取 X-Scene-Id
├── 校验 scene_id 合法性
├── 写入 SceneContext
└── 请求结束后清理 ThreadLocal
```

### 4.3 Repository 自动注入
所有 Repository 查询自动添加 `scene_id` 过滤条件：
```java
@Query("SELECT i FROM Item i WHERE i.sceneId = :#{T(SceneContext).getSceneId()}")
List<Item> findAll();
```

---

## 5. 核心业务流程

### 5.1 物品搜索流程
```
用户输入关键词
    ↓
Controller 接收请求 + 提取 scene_id
    ↓
ItemSearchService.search(criteria)
    ↓
构建查询条件（名称 OR 标签 OR 描述）
    ↓
ItemRepository.search(criteria)
    ↓
加载位置信息（批量查询）
    ↓
组装 ItemDTO（包含完整位置路径）
    ↓
返回结果
```


### 5.2 物品移动流程
```
用户请求移动物品
    ↓
Controller 接收 (itemId, targetLocationId)
    ↓
ItemMoveService.moveItem(itemId, targetLocationId)
    ↓
加载 Item 聚合根
    ↓
校验目标位置存在且属于同一场景
    ↓
item.moveTo(targetLocationId)  【领域行为】
    ↓
保存 Item
    ↓
记录审计日志
    ↓
返回成功
```

### 5.3 物品借出流程
```
用户请求借出物品
    ↓
Controller 接收 (itemId, borrower)
    ↓
ItemBorrowService.borrowItem(itemId, borrower)
    ↓
加载 Item 聚合根
    ↓
item.markBorrowed(borrower)  【领域行为】
    ↓
保存 Item
    ↓
记录审计日志
    ↓
返回成功
```

### 5.4 位置树查询流程
```
用户请求位置树
    ↓
Controller 接收请求
    ↓
LocationTreeService.getTree(rootId)
    ↓
递归查询子位置（使用 parent_id）
    ↓
构建树形结构
    ↓
返回 LocationTreeDTO
```

---

## 6. 数据库设计

### 6.1 核心表结构

#### item（物品表）
```sql
CREATE TABLE item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    scene_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    tags JSON,
    location_id BIGINT,
    quantity INT DEFAULT 1,
    status VARCHAR(50) NOT NULL,
    borrower VARCHAR(255),
    borrowed_at DATETIME,
    metadata JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_scene_id (scene_id),
    INDEX idx_location_id (location_id),
    INDEX idx_name (name),
    INDEX idx_status (status)
);
```

#### location（位置表）
```sql
CREATE TABLE location (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    scene_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    path VARCHAR(1000),
    level INT NOT NULL,
    metadata JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_scene_id (scene_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_path (path)
);
```


#### audit_log（审计日志表）
```sql
CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    scene_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    before_state JSON,
    after_state JSON,
    operator VARCHAR(255),
    operated_at DATETIME NOT NULL,
    INDEX idx_scene_id (scene_id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_operated_at (operated_at)
);
```

#### scene（场景表）
```sql
CREATE TABLE scene (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

### 6.2 关键约束
- **禁止外键级联删除**：删除位置不删除物品
- **scene_id 必须有索引**：所有查询都带 scene_id
- **location_id 可为空**：物品允许没有位置

---

## 7. API 设计

### 7.1 统一请求格式
所有请求必须携带 Header：
```
X-Scene-Id: {sceneId}
```

### 7.2 统一响应格式
```json
{
  "success": true,
  "data": { ... },
  "message": "操作成功",
  "timestamp": "2026-01-27T10:00:00"
}
```

### 7.3 核心 API 列表

#### 物品管理
```
POST   /api/items                创建物品
GET    /api/items/{id}           获取物品详情
PUT    /api/items/{id}           更新物品
DELETE /api/items/{id}           删除物品
POST   /api/items/search         搜索物品
POST   /api/items/{id}/move      移动物品
POST   /api/items/{id}/borrow    借出物品
POST   /api/items/{id}/return    归还物品
```

#### 位置管理
```
POST   /api/locations            创建位置
GET    /api/locations/{id}       获取位置详情
PUT    /api/locations/{id}       更新位置
DELETE /api/locations/{id}       删除位置
GET    /api/locations/tree       获取位置树
POST   /api/locations/{id}/move  移动位置节点
```

#### 审计日志
```
GET    /api/audit-logs           查询审计日志
GET    /api/audit-logs/entity/{type}/{id}  查询实体历史
```

---

## 8. 目录结构设计

```
item-center/
├── src/main/java/org/tina/itemcenter/
│   ├── ItemCenterApplication.java
│   ├── common/                      # 公共组件
│   │   ├── context/
│   │   │   └── SceneContext.java    # 场景上下文
│   │   ├── interceptor/
│   │   │   └── SceneInterceptor.java # 场景拦截器
│   │   ├── exception/               # 异常定义
│   │   └── response/                # 统一响应
│   ├── domain/                      # 领域层
│   │   ├── item/
│   │   │   ├── Item.java            # 物品聚合根
│   │   │   ├── ItemStatus.java      # 物品状态
│   │   │   └── ItemRepository.java  # 物品仓储接口
│   │   ├── location/
│   │   │   ├── Location.java        # 位置聚合根
│   │   │   └── LocationRepository.java
│   │   └── audit/
│   │       ├── AuditLog.java        # 审计日志
│   │       └── AuditLogRepository.java
│   ├── application/                 # 应用服务层
│   │   ├── item/
│   │   │   ├── ItemService.java
│   │   │   ├── ItemSearchService.java
│   │   │   └── ItemMoveService.java
│   │   ├── location/
│   │   │   ├── LocationService.java
│   │   │   └── LocationTreeService.java
│   │   └── audit/
│   │       └── AuditService.java
│   ├── infrastructure/              # 基础设施层
│   │   ├── persistence/
│   │   │   ├── ItemRepositoryImpl.java
│   │   │   └── LocationRepositoryImpl.java
│   │   └── config/
│   │       └── JpaConfig.java
│   └── presentation/                # 表现层
│       ├── controller/
│       │   ├── ItemController.java
│       │   ├── LocationController.java
│       │   └── AuditLogController.java
│       └── dto/
│           ├── ItemDTO.java
│           ├── LocationDTO.java
│           └── SearchCriteriaDTO.java
└── src/main/resources/
    ├── application.yml
    └── db/migration/                # 数据库迁移脚本
        ├── V1__create_tables.sql
        └── V2__add_indexes.sql
```


---

## 9. 关键技术决策

### 9.1 为什么使用 ThreadLocal 存储 scene_id？
- 避免方法参数层层传递
- 保证场景上下文在整个请求链路可用
- 通过拦截器统一管理生命周期

### 9.2 为什么物品与位置是弱耦合？
- 真实场景中物品可能"不知道在哪"
- 删除位置不应该删除物品（物品仍然存在）
- 允许数据不完整，符合真实使用场景

### 9.3 为什么位置树不限制深度？
- 不同用户的组织方式差异巨大
- 硬编码层级会限制系统灵活性
- 使用递归查询支持任意深度

### 9.4 为什么搜索要同时匹配名称/标签/描述？
- 用户记忆方式多样（可能记得名字、可能记得标签）
- "找得到"比"精确匹配"更重要
- 模糊搜索降低使用门槛

### 9.5 为什么必须记录审计日志？
- 关键操作可追溯
- 支持数据恢复
- 满足合规要求

---

## 10. 扩展性设计

### 10.1 未来可扩展功能

#### 图片上传
- 物品支持上传图片
- 使用对象存储（OSS）
- metadata 字段存储图片 URL

#### 批量操作
- 批量移动物品
- 批量修改标签
- 批量导入/导出

#### 提醒功能
- 借出到期提醒
- 维护到期提醒
- 使用消息队列异步处理

#### 统计分析
- 物品数量统计
- 位置利用率分析
- 借出频率分析

#### 权限管理
- 场景内角色划分（管理员/成员/访客）
- 操作权限控制
- 数据可见性控制

### 10.2 性能优化方向

#### 缓存策略
- 位置树缓存（Redis）
- 热点物品缓存
- 搜索结果缓存

#### 查询优化
- 搜索使用全文索引（Elasticsearch）
- 位置路径使用物化路径
- 分页查询避免深度分页

#### 异步处理
- 审计日志异步写入
- 批量操作异步执行
- 统计任务定时计算

---

## 11. 部署架构

### 11.1 单体部署（初期）
```
┌─────────────────┐
│   Nginx/LB      │
└────────┬────────┘
         ↓
┌─────────────────┐
│  Spring Boot    │
│  Application    │
└────────┬────────┘
         ↓
┌─────────────────┐
│     MySQL       │
└─────────────────┘
```

### 11.2 微服务拆分（后期）
```
┌─────────────────┐
│   API Gateway   │
└────────┬────────┘
         ↓
    ┌────┴────┬────────┬────────┐
    ↓         ↓        ↓        ↓
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│ Item   │ │Location│ │ Audit  │ │ Search │
│Service │ │Service │ │Service │ │Service │
└────────┘ └────────┘ └────────┘ └────────┘
```

---

## 12. 开发规范

### 12.1 代码规范
- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 减少样板代码
- 所有 public 方法必须有 Javadoc
- 单元测试覆盖率 > 80%

### 12.2 Git 规范
- 分支策略：main / develop / feature/*
- Commit 格式：`[类型] 描述`
  - feat: 新功能
  - fix: 修复
  - refactor: 重构
  - docs: 文档

### 12.3 Code Review 检查点
- 是否违反代码约束（CODE_CONSTRAINTS_ZH.md）
- 是否正确使用 SceneContext
- 是否记录审计日志
- 是否有单元测试

---

## 13. 总结

本架构设计基于以下核心理念：

1. **以用户为中心**：系统服务真实生活，不强迫用户"正确使用"
2. **场景隔离**：多场景数据完全隔离，保证数据安全
3. **领域驱动**：业务规则封装在领域模型中，保证代码可维护性
4. **可扩展性**：分层清晰，易于扩展新功能
5. **可追溯性**：关键操作全部审计，数据变更可追溯

通过严格遵循代码约束和架构设计，可以构建一个**简单、可靠、易用**的物品管理系统。

---

**文档版本**：v1.0  
**最后更新**：2026-01-27  
**维护者**：开发团队
