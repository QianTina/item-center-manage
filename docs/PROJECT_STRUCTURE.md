# 项目结构说明

## 完整目录结构

```
item-center/
├── docs/                                  # 📚 文档目录
│   ├── README.md                          # 文档索引
│   ├── architecture.md                    # 架构设计文档
│   ├── CODE_CONSTRAINTS_ZH.md             # 代码约束规范
│   ├── PROJECT_STRUCTURE.md               # 本文件 - 项目结构说明
│   ├── deployment/                        # 🚀 部署文档
│   │   ├── SUPABASE-CONNECTION-GUIDE.md   # Supabase 数据库连接指南
│   │   └── README-DEPLOY.md               # Render 部署指南
│   ├── development/                       # 💻 开发文档
│   │   └── API_TEST.md                    # API 测试示例
│   ├── testing/                           # 🧪 测试文档
│   │   └── PROPERTY_TEST_GUIDE.md         # 属性测试指南
│   └── security/                          # 🔒 安全文档
│       ├── SECURITY_CONFIG.md             # 安全配置指南
│       ├── cleanup-sensitive-data.sh      # Git 历史清理脚本
│       └── reset-repo.sh                  # 仓库重置脚本
│
├── src/                                   # 源代码目录
│   ├── main/
│   │   ├── java/org/tina/itemcenter/
│   │   │   ├── ItemCenterApplication.java # 应用入口
│   │   │   ├── application/               # 应用服务层
│   │   │   │   └── service/
│   │   │   │       └── SceneService.java  # 场景服务
│   │   │   ├── common/                    # 通用组件
│   │   │   │   ├── context/
│   │   │   │   │   └── SceneContext.java  # 场景上下文（ThreadLocal）
│   │   │   │   ├── exception/             # 异常类
│   │   │   │   │   ├── BusinessException.java
│   │   │   │   │   ├── EntityNotFoundException.java
│   │   │   │   │   ├── InvalidOperationException.java
│   │   │   │   │   ├── InvalidSceneIdException.java
│   │   │   │   │   ├── MissingSceneIdException.java
│   │   │   │   │   └── SceneContextException.java
│   │   │   │   └── response/
│   │   │   │       └── ApiResponse.java   # 统一响应格式
│   │   │   ├── domain/                    # 领域模型层
│   │   │   │   ├── model/
│   │   │   │   │   └── Scene.java         # 场景实体
│   │   │   │   └── repository/
│   │   │   │       └── SceneRepository.java # 场景仓储
│   │   │   ├── infrastructure/            # 基础设施层（待实现）
│   │   │   └── presentation/              # 表现层
│   │   │       ├── config/
│   │   │       │   └── WebMvcConfig.java  # Web MVC 配置
│   │   │       ├── controller/
│   │   │       │   └── SceneController.java # 场景控制器
│   │   │       ├── dto/
│   │   │       │   ├── CreateSceneRequest.java # 创建场景请求
│   │   │       │   └── SceneDTO.java      # 场景 DTO
│   │   │       ├── exception/
│   │   │       │   └── GlobalExceptionHandler.java # 全局异常处理
│   │   │       └── interceptor/
│   │   │           └── SceneInterceptor.java # 场景拦截器
│   │   └── resources/
│   │       ├── application.yml            # 通用配置
│   │       ├── application-local.yml.example # 本地配置模板
│   │       ├── application-prod.yml       # 生产配置（不提交）
│   │       └── db/
│   │           └── migration/
│   │               ├── README.md          # 数据库迁移说明
│   │               └── V1__create_tables.sql # 初始表结构
│   └── test/                              # 测试代码
│       └── java/org/tina/itemcenter/
│           ├── DatabaseConnectionTest.java # 数据库连接测试
│           ├── ItemCenterApplicationTests.java # 应用启动测试
│           ├── SceneManagementTest.java   # 场景管理单元测试
│           └── ScenePropertyTest.java     # 场景属性测试
│
├── .env                                   # 环境变量（不提交到 Git）
├── .env.example                           # 环境变量模板
├── .gitattributes                         # Git 属性配置
├── .gitignore                             # Git 忽略配置
├── CHANGELOG.md                           # 更新日志
├── HELP.md                                # Spring Boot 帮助文档
├── mvnw                                   # Maven Wrapper（Unix）
├── mvnw.cmd                               # Maven Wrapper（Windows）
├── pom.xml                                # Maven 项目配置
├── QUICK_REFERENCE.md                     # 快速参考
├── README.md                              # 项目主文档
├── render.yaml                            # Render 部署配置
└── run-property-test.sh                   # 属性测试运行脚本
```

## 目录说明

### 📚 docs/ - 文档目录

所有项目文档的集中存放位置，按功能分类：

- **deployment/**: 部署相关文档（Supabase、Render）
- **development/**: 开发相关文档（API 测试）
- **testing/**: 测试相关文档（属性测试）
- **security/**: 安全相关文档（密码管理、清理脚本）

### 💻 src/main/java/ - 源代码

采用严格的分层架构：

#### application/ - 应用服务层
- 编排业务流程
- 事务边界控制
- 调用领域模型执行业务规则

#### common/ - 通用组件
- **context/**: 场景上下文管理（ThreadLocal）
- **exception/**: 业务异常类
- **response/**: 统一响应格式

#### domain/ - 领域模型层
- **model/**: 实体类（包含业务规则）
- **repository/**: 仓储接口

#### infrastructure/ - 基础设施层
- 数据持久化实现（待实现）

#### presentation/ - 表现层
- **config/**: Web 配置
- **controller/**: REST 控制器
- **dto/**: 数据传输对象
- **exception/**: 全局异常处理
- **interceptor/**: HTTP 拦截器

### 🧪 src/test/java/ - 测试代码

- **单元测试**: 测试特定功能和边缘情况
- **属性测试**: 使用 jqwik 进行基于属性的测试
- **集成测试**: 测试完整的业务流程

### 📝 配置文件

#### 环境变量
- `.env`: 本地环境变量（不提交）
- `.env.example`: 环境变量模板

#### 应用配置
- `application.yml`: 通用配置
- `application-local.yml`: 本地配置（不提交）
- `application-local.yml.example`: 本地配置模板
- `application-prod.yml`: 生产配置（不提交）

#### 构建配置
- `pom.xml`: Maven 项目配置
- `mvnw`, `mvnw.cmd`: Maven Wrapper

#### 部署配置
- `render.yaml`: Render 部署配置

## 分层架构依赖关系

```
┌─────────────────────────────────────┐
│     Presentation Layer              │
│  (Controller, DTO, Interceptor)     │
└──────────────┬──────────────────────┘
               │ 依赖
               ↓
┌─────────────────────────────────────┐
│    Application Service Layer        │
│      (Service, Transaction)         │
└──────────────┬──────────────────────┘
               │ 依赖
               ↓
┌─────────────────────────────────────┐
│      Domain Model Layer             │
│    (Entity, Repository Interface)   │
└──────────────┬──────────────────────┘
               │ 依赖
               ↓
┌─────────────────────────────────────┐
│    Infrastructure Layer             │
│  (Repository Implementation, DB)    │
└─────────────────────────────────────┘
```

## 代码组织原则

### 1. 单向依赖
- 上层可以依赖下层
- 下层不能依赖上层
- 同层之间不能相互依赖

### 2. 职责分离
- **Controller**: 只做参数校验和服务调用
- **Service**: 只做业务流程编排
- **Entity**: 包含业务规则和状态变更
- **Repository**: 只做数据访问

### 3. 命名规范
- **Entity**: 名词，如 `Scene`, `Item`, `Location`
- **Service**: 名词 + Service，如 `SceneService`
- **Controller**: 名词 + Controller，如 `SceneController`
- **DTO**: 名词 + DTO/Request，如 `SceneDTO`, `CreateSceneRequest`
- **Repository**: 名词 + Repository，如 `SceneRepository`

### 4. 包结构规范
- 按层级组织，不按功能模块
- 每层内部可以按功能模块细分
- 避免循环依赖

## 文件命名规范

### Java 类文件
- 使用 PascalCase（大驼峰）
- 类名应该清晰表达其职责
- 测试类以 `Test` 结尾

### 配置文件
- 使用 kebab-case（短横线分隔）
- 如：`application-local.yml`

### 文档文件
- 使用 UPPERCASE 或 PascalCase
- 如：`README.md`, `CHANGELOG.md`

### 脚本文件
- 使用 kebab-case
- 如：`cleanup-sensitive-data.sh`

## 扩展指南

### 添加新功能

1. **创建领域模型**
   - 在 `domain/model/` 创建实体类
   - 在 `domain/repository/` 创建仓储接口

2. **实现应用服务**
   - 在 `application/service/` 创建服务类
   - 编排业务流程，调用领域模型

3. **创建 API 端点**
   - 在 `presentation/dto/` 创建 DTO
   - 在 `presentation/controller/` 创建控制器

4. **编写测试**
   - 在 `test/` 创建单元测试
   - 在 `test/` 创建属性测试

### 添加新文档

1. 确定文档类别（deployment/development/testing/security）
2. 在对应目录创建文档
3. 更新 `docs/README.md` 索引
4. 在主 `README.md` 添加链接（如果是重要文档）

## 参考资源

- [架构设计文档](architecture.md)
- [代码约束规范](CODE_CONSTRAINTS_ZH.md)
- [文档索引](README.md)
