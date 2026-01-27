# 物品管理系统 (Item Management System)

一个基于 Spring Boot 4.0.1 + JPA + PostgreSQL 的物品追踪与查找系统。

## 🚀 快速开始

### 环境要求

- Java 17+
- PostgreSQL (使用 Supabase 免费层)
- Maven 3.9+

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/QianTina/item-center-manage.git
cd item-center-manage/item-center
```

2. **配置数据库**
```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，填入你的 Supabase 数据库配置
# DATABASE_URL=jdbc:postgresql://your-host:6543/postgres?sslmode=require
# DATABASE_USERNAME=your-username
# DATABASE_PASSWORD=your-password
```

3. **运行应用**
```bash
# 加载环境变量并启动
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

4. **测试 API**
```bash
# 应用运行在 http://localhost:8080
curl http://localhost:8080/api/scenes
```

## 📚 文档导航

### 快速开始

- **[快速参考](QUICK_REFERENCE.md)** - 常用命令和快速链接
- **[更新日志](CHANGELOG.md)** - 项目变更记录

### 核心文档

- **[项目架构](docs/architecture.md)** - 系统架构设计和技术选型
- **[代码约束](docs/CODE_CONSTRAINTS_ZH.md)** - 编码规范和约束

### 开发文档

- **[API 测试指南](docs/development/API_TEST.md)** - API 端点测试示例

### 测试文档

- **[属性测试指南](docs/testing/PROPERTY_TEST_GUIDE.md)** - 基于属性的测试说明

### 部署文档

- **[Supabase 连接指南](docs/deployment/SUPABASE-CONNECTION-GUIDE.md)** - 数据库配置说明
- **[部署指南](docs/deployment/README-DEPLOY.md)** - Render 部署配置

### 安全文档

- **[安全配置指南](docs/security/SECURITY_CONFIG.md)** - 环境变量和密码管理
- **[敏感数据清理脚本](docs/security/cleanup-sensitive-data.sh)** - Git 历史清理工具
- **[仓库重置脚本](docs/security/reset-repo.sh)** - 简单的仓库重置工具

## 🏗️ 项目结构

```
item-center/
├── docs/                          # 文档目录
│   ├── architecture.md            # 架构文档
│   ├── CODE_CONSTRAINTS_ZH.md     # 代码约束
│   ├── deployment/                # 部署相关文档
│   ├── development/               # 开发相关文档
│   ├── testing/                   # 测试相关文档
│   └── security/                  # 安全相关文档
├── src/
│   ├── main/
│   │   ├── java/org/tina/itemcenter/
│   │   │   ├── application/       # 应用服务层
│   │   │   ├── common/            # 通用组件
│   │   │   ├── domain/            # 领域模型层
│   │   │   ├── infrastructure/    # 基础设施层
│   │   │   └── presentation/      # 表现层
│   │   └── resources/
│   │       ├── application.yml    # 应用配置
│   │       └── db/migration/      # 数据库迁移脚本
│   └── test/                      # 测试代码
├── .env.example                   # 环境变量模板
├── pom.xml                        # Maven 配置
└── README.md                      # 本文件
```

## 🎯 核心特性

- **场景隔离**：多场景数据完全隔离，使用 ThreadLocal 管理场景上下文
- **领域驱动设计**：业务规则封装在领域模型中
- **弱耦合关系**：物品与位置松耦合，允许不完整数据
- **审计日志**：所有关键操作自动记录
- **属性测试**：使用 jqwik 进行基于属性的测试

## 🧪 测试

### 运行所有测试
```bash
export $(cat .env | xargs) && ./mvnw test
```

### 运行特定测试
```bash
# 数据库连接测试
export $(cat .env | xargs) && ./mvnw test -Dtest=DatabaseConnectionTest

# 场景管理测试
export $(cat .env | xargs) && ./mvnw test -Dtest=SceneManagementTest

# 属性测试
export $(cat .env | xargs) && ./mvnw test -Dtest=ScenePropertyTest
```

详细测试指南请参考 [属性测试指南](docs/testing/PROPERTY_TEST_GUIDE.md)

## 🔒 安全注意事项

⚠️ **重要**：绝不要将数据库密码提交到 Git！

- 使用 `.env` 文件管理敏感配置（已在 .gitignore 中）
- 参考 [安全配置指南](docs/security/SECURITY_CONFIG.md) 了解最佳实践
- 如果不慎泄露密码，立即参考安全文档中的清理脚本

## 🚢 部署

项目使用 Render 免费层 + Supabase 免费层部署。

详细部署步骤请参考 [部署指南](docs/deployment/README-DEPLOY.md)

## 📖 API 文档

### 场景管理 API

```
POST   /api/scenes              创建场景
GET    /api/scenes              查询场景列表
GET    /api/scenes/{id}         获取场景详情
```

### 统一请求头

所有 API 请求必须携带：
```
X-Scene-Id: {sceneId}
```

更多 API 示例请参考 [API 测试指南](docs/development/API_TEST.md)

## 🛠️ 技术栈

- **框架**：Spring Boot 4.0.1
- **持久化**：Spring Data JPA + Hibernate
- **数据库**：PostgreSQL (Supabase)
- **语言**：Java 17
- **工具**：Lombok、Bean Validation
- **测试**：JUnit 5、jqwik (属性测试)
- **部署**：Render

## 📝 开发规范

请严格遵循 [代码约束](docs/CODE_CONSTRAINTS_ZH.md) 中的规范：

- 使用领域驱动设计（DDD）
- 严格的分层架构
- 禁止贫血模型
- Repository 自动注入 scene_id

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 📧 联系方式

- GitHub: [@QianTina](https://github.com/QianTina)
- 项目地址: https://github.com/QianTina/item-center-manage
