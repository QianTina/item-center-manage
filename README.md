# 物品管理系统

一个基于 Spring Boot 4.0.1 + PostgreSQL (Supabase) 的物品追踪与查找系统。

## 快速开始

### 1. 环境要求

- Java 17+
- Maven 3.6+
- PostgreSQL 数据库（推荐使用 Supabase）

### 2. 配置数据库

#### 方式一：使用环境变量（推荐）

1. 复制环境变量模板：
```bash
cp .env.example .env
```

2. 编辑 `.env` 文件，填入你的数据库配置：
```properties
DATABASE_URL=jdbc:postgresql://your-host:6543/postgres?sslmode=require
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
```

3. 加载环境变量并启动：
```bash
# macOS/Linux
export $(cat .env | xargs) && ./mvnw spring-boot:run

# 或者使用 source
source .env
./mvnw spring-boot:run
```

#### 方式二：使用配置文件

1. 复制配置模板：
```bash
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

2. 编辑 `application-local.yml`，填入你的数据库配置

3. 启动应用：
```bash
./mvnw spring-boot:run
```

### 3. 运行测试

```bash
# 设置环境变量
export DATABASE_URL="jdbc:postgresql://..."
export DATABASE_USERNAME="..."
export DATABASE_PASSWORD="..."

# 运行所有测试
./mvnw test

# 运行特定测试
./mvnw test -Dtest=DatabaseConnectionTest
```

### 4. 访问应用

应用启动后，访问：
- API 地址：http://localhost:8080
- 健康检查：http://localhost:8080/actuator/health（如果启用）

## API 文档

详细的 API 测试说明请参考：[API_TEST.md](API_TEST.md)

### 场景管理 API

```bash
# 创建场景
curl -X POST http://localhost:8080/api/scenes \
  -H "Content-Type: application/json" \
  -d '{"name": "我的家", "owner": "张三"}'

# 查询所有场景
curl -X GET http://localhost:8080/api/scenes

# 根据 ID 查询场景
curl -X GET http://localhost:8080/api/scenes/1
```

## 项目结构

```
item-center/
├── src/
│   ├── main/
│   │   ├── java/org/tina/itemcenter/
│   │   │   ├── application/        # 应用服务层
│   │   │   │   └── service/
│   │   │   ├── common/             # 公共组件
│   │   │   │   ├── context/        # 场景上下文
│   │   │   │   ├── exception/      # 异常类
│   │   │   │   └── response/       # 响应格式
│   │   │   ├── domain/             # 领域模型层
│   │   │   │   ├── model/          # 实体类
│   │   │   │   └── repository/     # 仓储接口
│   │   │   ├── infrastructure/     # 基础设施层
│   │   │   └── presentation/       # 表现层
│   │   │       ├── config/         # 配置类
│   │   │       ├── controller/     # 控制器
│   │   │       ├── dto/            # 数据传输对象
│   │   │       ├── exception/      # 全局异常处理
│   │   │       └── interceptor/    # 拦截器
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml.example
│   │       └── application-prod.yml
│   └── test/                       # 测试代码
├── .env.example                    # 环境变量模板
├── .gitignore
├── API_TEST.md                     # API 测试文档
├── SECURITY_CONFIG.md              # 安全配置指南
└── pom.xml
```

## 安全配置

**⚠️ 重要**：数据库密码等敏感信息不应该提交到 Git！

详细的安全配置说明请参考：[SECURITY_CONFIG.md](SECURITY_CONFIG.md)

## 部署

### Render 部署

1. 在 Render Dashboard 创建 Web Service
2. 连接 GitHub 仓库
3. 配置环境变量：
   - `DATABASE_URL`
   - `DATABASE_USERNAME`
   - `DATABASE_PASSWORD`
4. Render 会自动构建和部署

详细部署说明请参考：[README-DEPLOY.md](README-DEPLOY.md)

## 技术栈

- **框架**：Spring Boot 4.0.1
- **数据库**：PostgreSQL (Supabase)
- **ORM**：Spring Data JPA + Hibernate
- **构建工具**：Maven
- **Java 版本**：17

## 开发指南

### 代码约束

项目严格遵循代码约束规范，详见：`docs/CODE_CONSTRAINTS_ZH.md`

### 分层架构

- **表现层**：Controller、DTO、拦截器
- **应用服务层**：业务流程编排、事务管理
- **领域模型层**：实体类、业务规则
- **基础设施层**：Repository、数据访问

### 场景隔离

系统使用场景（Scene）实现数据隔离：
- 所有业务 API 需要携带 `X-Scene-Id` Header
- 场景管理 API 不需要 Header
- 使用 ThreadLocal 管理场景上下文

## 常见问题

### 1. 数据库连接失败

检查：
- 数据库连接信息是否正确
- 环境变量是否正确设置
- 网络是否可以访问 Supabase

### 2. 环境变量未生效

确保：
- 已正确设置环境变量
- 使用 `export` 命令（macOS/Linux）
- 或在 IDE 中配置环境变量

### 3. 测试失败

确保：
- 数据库连接正常
- 环境变量已设置
- 使用 `@Transactional` 注解的测试会自动回滚

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License
