# Supabase 数据库连接问题排查

## 当前状态

✅ **本地开发环境**：已配置使用 H2 内存数据库  
❌ **Supabase 连接**：持续失败，即使网络恢复后仍然无法连接

已尝试的连接配置：
1. ✗ Connection Pooling (port 6543) + SSL
2. ✗ Connection Pooling (port 5432) + SSL  
3. ✗ Connection Pooling (port 5432) + sslmode=disable
4. ✗ Direct Connection (db.jchsrvxxcbcilqgqtpkh.supabase.co:5432) - 网络恢复后仍失败

所有尝试都失败，错误：`java.io.EOFException` 在 SSL 握手阶段（`enableSSL`）

## 根本原因分析

`EOFException` 在 `enableSSL` 阶段表示：
1. **IP 白名单限制**：Supabase 可能限制了访问 IP
2. **防火墙/网络限制**：本地网络环境可能阻止了 PostgreSQL SSL 连接
3. **SSL 证书问题**：Supabase 的 SSL 证书可能需要特殊配置

## 当前配置

**本地开发**：
- 数据库：H2 内存数据库
- 配置文件：`application-local.yml`
- 状态：✅ 正常工作

**Supabase 信息**：
- 密码：`item-manage-center2026010126`  
- 项目 ID：`jchsrvxxcbcilqgqtpkh`  
- 区域：ap-southeast-1 (Singapore)  
- Direct Connection：`db.jchsrvxxcbcilqgqtpkh.supabase.co:5432`

## 解决方案

### 方案 1: 检查 Supabase IP 白名单（推荐）

1. 登录 Supabase Dashboard
2. 进入 Project Settings → Database
3. 查看 "Connection Pooling" 部分
4. 检查是否启用了 IP 限制
5. 如果启用，添加你的 IP 地址或禁用 IP 限制

### 方案 2: 使用 Supabase API 而不是直连

如果直连持续失败，可以考虑使用 Supabase 的 REST API 或 GraphQL API

### 方案 3: 继续使用 H2 进行本地开发（当前方案）

- 本地开发使用 H2 内存数据库
- 生产环境部署到 Render 时使用 Supabase
- 这是最稳定的方案，不受网络限制影响

## 测试 Supabase 连接

如果想测试 Supabase 连接，可以使用 `psql` 命令：

```bash
psql "postgresql://postgres:item-manage-center2026010126@db.jchsrvxxcbcilqgqtpkh.supabase.co:5432/postgres"
```

如果 `psql` 也无法连接，说明是网络/防火墙问题，而不是 Java 配置问题。

## 建议

**当前最佳实践**：
1. 本地开发使用 H2 数据库（已配置）
2. 部署到 Render 时使用 Supabase PostgreSQL
3. 等网络环境稳定后再尝试本地连接 Supabase

这样可以确保开发不受网络问题影响。
