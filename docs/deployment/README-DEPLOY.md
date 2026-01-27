# 物品管理系统 - 部署指南

## 📋 技术栈

- **后端**: Spring Boot 4.0.1 + JPA
- **数据库**: PostgreSQL (Supabase 免费层)
- **部署**: Render 免费层
- **语言**: Java 17

## 🚀 本地开发

### 1. 前置要求

- Java 17+
- Maven 3.6+
- 已配置好的 Supabase 数据库

### 2. 配置数据库

数据库连接已配置在 `src/main/resources/application-local.yml`：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db.jchsrvxxcbcilqgqtpkh.supabase.co:5432/postgres?sslmode=require
    username: postgres
    password: item-manage-center202601
```

### 3. 启动应用

```bash
# 进入项目目录
cd item-center

# 编译项目
./mvnw clean package

# 运行应用
./mvnw spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

### 4. 验证启动

```bash
# 检查应用健康状态
curl http://localhost:8080/actuator/health
```

## 🌐 部署到 Render

### 1. 准备工作

- 注册 Render 账号：https://render.com
- 将代码推送到 GitHub 仓库

### 2. 创建 Web Service

1. 登录 Render Dashboard
2. 点击 "New +" → "Web Service"
3. 连接你的 GitHub 仓库
4. 配置如下：

**基本设置**：
- Name: `item-management-system`
- Region: `Oregon (US West)`
- Branch: `main`
- Root Directory: `item-center`

**构建设置**：
- Build Command: `./mvnw clean package -DskipTests`
- Start Command: `java -Xmx512m -Xms256m -Dspring.profiles.active=prod -jar target/item-center-0.0.1-SNAPSHOT.jar`

**环境变量**：
- `SPRING_PROFILES_ACTIVE`: `prod`
- `DATABASE_URL`: `jdbc:postgresql://db.jchsrvxxcbcilqgqtpkh.supabase.co:5432/postgres?sslmode=require&user=postgres&password=item-manage-center202601`

### 3. 使用 render.yaml 自动部署

项目根目录已包含 `render.yaml` 配置文件，可以直接使用：

1. 在 Render Dashboard 点击 "New +" → "Blueprint"
2. 连接 GitHub 仓库
3. Render 会自动读取 `render.yaml` 并创建服务

### 4. 部署后验证

```bash
# 替换为你的 Render 应用 URL
curl https://item-management-system.onrender.com/actuator/health
```

## 📊 数据库迁移

### 初始化数据库表

在 Supabase SQL Editor 中执行以下脚本：

```sql
-- 创建 scene 表
CREATE TABLE scene (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 创建 item 表
CREATE TABLE item (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    tags JSONB,
    location_id BIGINT,
    quantity INT NOT NULL DEFAULT 1,
    status VARCHAR(50) NOT NULL,
    borrower VARCHAR(255),
    borrowed_at TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_item_scene_id ON item(scene_id);
CREATE INDEX idx_item_location_id ON item(location_id);
CREATE INDEX idx_item_name ON item(name);
CREATE INDEX idx_item_status ON item(status);
CREATE INDEX idx_item_tags ON item USING GIN(tags);

-- 创建 location 表
CREATE TABLE location (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    path VARCHAR(1000),
    level INT NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_location_scene_id ON location(scene_id);
CREATE INDEX idx_location_parent_id ON location(parent_id);
CREATE INDEX idx_location_path ON location(path);

-- 创建 audit_log 表
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    before_state JSONB,
    after_state JSONB,
    operator VARCHAR(255),
    operated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_log_scene_id ON audit_log(scene_id);
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_operated_at ON audit_log(operated_at);
```

## ⚠️ 免费层限制

### Render 免费层
- ✅ 512 MB RAM
- ✅ 0.1 CPU
- ⚠️ 15分钟无流量自动休眠
- ⚠️ 冷启动需要 30-60 秒
- ✅ 100 GB 带宽/月

### Supabase 免费层
- ✅ 500 MB 数据库存储
- ✅ 5 GB 出站流量/月
- ✅ 最多 60 个并发连接

## 🔧 故障排查

### 应用无法启动

1. 检查数据库连接：
```bash
# 测试数据库连接
psql "postgresql://postgres:item-manage-center202601@db.jchsrvxxcbcilqgqtpkh.supabase.co:5432/postgres?sslmode=require"
```

2. 检查 Render 日志：
   - 进入 Render Dashboard
   - 选择你的服务
   - 查看 "Logs" 标签

### 冷启动时间过长

这是 Render 免费层的正常现象。优化建议：
- 使用轻量级的 JVM 参数（已配置）
- 考虑升级到付费层（$7/月）

### 数据库连接超时

检查 Supabase 连接池配置：
```yaml
hikari:
  maximum-pool-size: 3
  connection-timeout: 30000
```

## 📚 相关文档

- [Render 文档](https://render.com/docs)
- [Supabase 文档](https://supabase.com/docs)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)

## 🎯 下一步

1. 完成所有功能开发
2. 运行测试套件
3. 部署到 Render
4. 配置自定义域名（可选）
