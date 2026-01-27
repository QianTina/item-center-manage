# 数据库迁移脚本

## 说明

本目录包含数据库迁移脚本，用于创建和维护数据库表结构。

## 使用方式

### 方式一：使用 JPA 自动创建（推荐用于开发环境）

项目配置了 `spring.jpa.hibernate.ddl-auto=update`，Hibernate 会根据实体类自动创建和更新表结构。

**优点**：
- 无需手动执行 SQL 脚本
- 实体类和数据库结构自动同步
- 适合快速开发和测试

**缺点**：
- 不适合生产环境
- 无法精确控制表结构变更

### 方式二：手动执行 SQL 脚本

如果需要精确控制数据库结构，可以手动执行 SQL 脚本：

1. 连接到 Supabase 数据库：
```bash
psql "postgresql://postgres.jchsrvxxcbcilqgqtpkh:item-manage-center202601@aws-1-ap-southeast-1.pooler.supabase.com:6543/postgres?sslmode=require"
```

2. 执行迁移脚本：
```sql
\i src/main/resources/db/migration/V1__create_tables.sql
```

或者通过 Supabase Dashboard 的 SQL Editor 执行。

### 方式三：使用 Flyway（推荐用于生产环境）

如果需要使用 Flyway 进行数据库迁移管理：

1. 在 `pom.xml` 中添加 Flyway 依赖：
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

2. 在 `application.yml` 中配置 Flyway：
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

3. 修改 JPA 配置：
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # 改为 validate，不让 Hibernate 自动创建表
```

4. 重启应用，Flyway 会自动执行迁移脚本。

## 迁移脚本说明

### V1__create_tables.sql

创建核心表结构：
- `scene` - 场景表，用于数据隔离
- `item` - 物品表，存储物品信息
- `location` - 位置表，支持树形层级结构
- `audit_log` - 审计日志表，记录所有关键操作

## 注意事项

1. **开发环境**：使用 JPA 自动创建表即可，无需手动执行脚本
2. **生产环境**：建议使用 Flyway 进行版本化的数据库迁移管理
3. **数据备份**：在执行任何数据库变更前，请先备份数据
4. **索引优化**：脚本中已包含必要的索引，确保查询性能

## 表结构说明

详细的表结构说明请参考设计文档：`.kiro/specs/item-management-system/design.md`
