# 快速参考

## 🚀 常用命令

### 启动应用
```bash
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

### 运行测试
```bash
# 所有测试
export $(cat .env | xargs) && ./mvnw test

# 数据库连接测试
export $(cat .env | xargs) && ./mvnw test -Dtest=DatabaseConnectionTest

# 场景管理测试
export $(cat .env | xargs) && ./mvnw test -Dtest=SceneManagementTest

# 属性测试
export $(cat .env | xargs) && ./mvnw test -Dtest=ScenePropertyTest
```

### 构建项目
```bash
./mvnw clean package
```

## 📚 文档快速链接

| 需求 | 文档 |
|------|------|
| 我要开始开发 | [README.md](README.md) |
| 我要了解架构 | [docs/architecture.md](docs/architecture.md) |
| 我要查看代码规范 | [docs/CODE_CONSTRAINTS_ZH.md](docs/CODE_CONSTRAINTS_ZH.md) |
| 我要测试 API | [docs/development/API_TEST.md](docs/development/API_TEST.md) |
| 我要运行测试 | [docs/testing/PROPERTY_TEST_GUIDE.md](docs/testing/PROPERTY_TEST_GUIDE.md) |
| 我要配置数据库 | [docs/deployment/SUPABASE-CONNECTION-GUIDE.md](docs/deployment/SUPABASE-CONNECTION-GUIDE.md) |
| 我要部署应用 | [docs/deployment/README-DEPLOY.md](docs/deployment/README-DEPLOY.md) |
| 我要配置安全 | [docs/security/SECURITY_CONFIG.md](docs/security/SECURITY_CONFIG.md) |
| 密码泄露了怎么办 | [docs/security/SECURITY_CONFIG.md](docs/security/SECURITY_CONFIG.md#已泄露密码的处理) |

## 🔧 配置文件位置

| 配置项 | 文件路径 |
|--------|----------|
| 环境变量 | `.env` (不提交到 Git) |
| 环境变量模板 | `.env.example` |
| 本地配置 | `src/main/resources/application-local.yml` (不提交到 Git) |
| 本地配置模板 | `src/main/resources/application-local.yml.example` |
| 生产配置 | `src/main/resources/application-prod.yml` (不提交到 Git) |
| 通用配置 | `src/main/resources/application.yml` |

## 📖 API 端点

### 场景管理

```bash
# 创建场景
POST /api/scenes
Content-Type: application/json
{
  "name": "我的家",
  "owner": "张三"
}

# 查询所有场景
GET /api/scenes

# 查询场景详情
GET /api/scenes/{id}
```

### 请求头

所有业务 API 需要携带：
```
X-Scene-Id: {sceneId}
```

场景管理 API 不需要此 Header。

## 🐛 常见问题

### 数据库连接失败
1. 检查 `.env` 文件是否存在
2. 检查环境变量是否正确加载：`echo $DATABASE_URL`
3. 检查数据库密码是否正确

### 测试失败
1. 确保环境变量已设置
2. 确保数据库连接正常
3. 查看测试日志获取详细错误信息

### Maven 命令失败
1. 尝试使用 `bash mvnw` 而不是 `./mvnw`
2. 检查是否有 Maven 进程冲突：`ps aux | grep maven`
3. 使用 IDE 运行（推荐）

### 密码泄露到 Git
1. 立即更改数据库密码
2. 参考 [安全配置指南](docs/security/SECURITY_CONFIG.md)
3. 运行清理脚本：`docs/security/reset-repo.sh`

## 🎯 开发流程

1. **创建分支**
```bash
git checkout -b feature/your-feature-name
```

2. **开发功能**
   - 遵循 [代码约束](docs/CODE_CONSTRAINTS_ZH.md)
   - 编写单元测试
   - 编写属性测试（如适用）

3. **运行测试**
```bash
export $(cat .env | xargs) && ./mvnw test
```

4. **提交代码**
```bash
git add .
git commit -m "feat: your feature description"
git push origin feature/your-feature-name
```

5. **创建 Pull Request**

## 📞 获取帮助

- 查看 [文档索引](docs/README.md)
- 提交 [Issue](https://github.com/QianTina/item-center-manage/issues)
- 查看 [更新日志](CHANGELOG.md)

## 🔗 相关链接

- [GitHub 仓库](https://github.com/QianTina/item-center-manage)
- [Supabase Dashboard](https://supabase.com/dashboard)
- [Render Dashboard](https://dashboard.render.com)
- [Spring Boot 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [jqwik 文档](https://jqwik.net/docs/current/user-guide.html)
