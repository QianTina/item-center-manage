# Supabase 数据库连接配置指南

## ❌ 当前问题

数据库连接失败，错误信息：`java.io.EOFException` - SSL 握手失败

## 🔍 原因分析

Supabase 提供两种连接方式：
1. **直连模式** (端口 5432) - 需要完整的 SSL 证书验证
2. **连接池模式** (端口 6543) - 推荐用于应用程序

当前配置可能使用了错误的连接方式或缺少 SSL 配置。

## ✅ 解决方案

### 步骤 1：获取正确的连接字符串

1. 登录 Supabase Dashboard：https://supabase.com/dashboard
2. 选择你的项目：`jchsrvxxcbcilqgqtpkh`
3. 点击左侧 **"Project Settings"** (齿轮图标)
4. 选择 **"Database"** 标签
5. 找到 **"Connection string"** 部分
6. 选择 **"URI"** 模式
7. 在下拉菜单中选择 **"Connection pooling"** (连接池模式)

### 步骤 2：复制连接字符串

连接字符串应该类似这样：

```
postgresql://postgres.jchsrvxxcbcilqgqtpkh:[YOUR-PASSWORD]@aws-0-[region].pooler.supabase.com:6543/postgres
```

注意关键点：
- 用户名格式：`postgres.jchsrvxxcbcilqgqtpkh` (带项目 ID)
- 主机地址：`aws-0-[region].pooler.supabase.com` (包含 pooler)
- 端口：`6543` (连接池端口，不是 5432)

### 步骤 3：更新配置文件

将获取到的连接信息填入 `src/main/resources/application-local.yml`：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://[HOST]:[PORT]/postgres?sslmode=require
    username: [USERNAME]
    password: [PASSWORD]
```

### 示例配置

假设你的连接字符串是：
```
postgresql://postgres.jchsrvxxcbcilqgqtpkh:item-manage-center202601@aws-0-us-west-1.pooler.supabase.com:6543/postgres
```

则配置应该是：
```yaml
spring:
  datasource:
    url: jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres?sslmode=require
    username: postgres.jchsrvxxcbcilqgqtpkh
    password: item-manage-center202601
```

## 🧪 测试连接

更新配置后，运行测试：

```bash
./mvnw test -Dtest=DatabaseConnectionTest
```

如果看到 `✅ 数据库连接成功！`，说明配置正确。

## 🆘 如果还是失败

### 方法 1：检查防火墙

确保你的网络允许访问 Supabase 的端口 6543。

### 方法 2：使用直连模式（需要额外配置）

如果连接池模式不可用，可以尝试直连模式，但需要下载 SSL 证书：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db.jchsrvxxcbcilqgqtpkh.supabase.co:5432/postgres?sslmode=verify-full&sslrootcert=/path/to/ca-certificate.crt
    username: postgres
    password: item-manage-center202601
```

### 方法 3：联系我

如果以上方法都不行，请提供：
1. Supabase Dashboard 中显示的完整连接字符串（隐藏密码）
2. 你的 Supabase 项目所在区域（如 us-west-1）
3. 错误日志的完整输出

## 📚 参考文档

- [Supabase 数据库连接文档](https://supabase.com/docs/guides/database/connecting-to-postgres)
- [PostgreSQL JDBC 驱动文档](https://jdbc.postgresql.org/documentation/head/connect.html)
