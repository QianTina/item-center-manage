# 安全配置指南

## ⚠️ 重要安全说明

**数据库密码等敏感信息绝不应该直接提交到 Git 仓库！**

本项目已经配置了安全的环境变量方式来管理敏感配置。

## 配置方式

### 方式一：使用环境变量（推荐）

#### 1. 本地开发环境

创建 `.env` 文件（已在 .gitignore 中，不会被提交）：

```bash
cp .env.example .env
```

编辑 `.env` 文件，填入真实的配置：

```properties
DATABASE_URL=jdbc:postgresql://your-supabase-host:6543/postgres?sslmode=require
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
```

#### 2. 启动应用时加载环境变量

**macOS/Linux**:
```bash
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

或者使用 `source` 命令：
```bash
source .env
./mvnw spring-boot:run
```

**Windows (PowerShell)**:
```powershell
Get-Content .env | ForEach-Object {
    $name, $value = $_.split('=')
    Set-Item -Path "env:$name" -Value $value
}
./mvnw spring-boot:run
```

**Windows (CMD)**:
```cmd
for /f "tokens=*" %i in (.env) do set %i
mvnw spring-boot:run
```

#### 3. IDE 配置（IntelliJ IDEA）

1. 打开 Run/Debug Configurations
2. 选择 Spring Boot 配置
3. 在 Environment variables 中添加：
   ```
   DATABASE_URL=jdbc:postgresql://...
   DATABASE_USERNAME=...
   DATABASE_PASSWORD=...
   ```

### 方式二：使用配置文件（仅本地开发）

如果你更喜欢使用配置文件：

1. 复制模板文件：
```bash
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

2. 编辑 `application-local.yml`，填入真实配置

3. **注意**：`application-local.yml` 已在 .gitignore 中，不会被提交

## 生产环境配置

### Render 部署

在 Render Dashboard 中配置环境变量：

1. 进入你的 Web Service
2. 点击 "Environment" 标签
3. 添加环境变量：
   - `DATABASE_URL`: 完整的数据库连接字符串
   - `DATABASE_USERNAME`: 数据库用户名
   - `DATABASE_PASSWORD`: 数据库密码

Render 会自动注入这些环境变量到应用中。

### Supabase 连接信息获取

1. 登录 Supabase Dashboard
2. 选择你的项目
3. 进入 Settings → Database
4. 在 Connection string 部分选择 "Connection pooling"
5. 复制连接字符串（格式：`postgresql://user:password@host:port/database`）
6. 转换为 JDBC 格式：`jdbc:postgresql://host:port/database?sslmode=require`

## 已泄露密码的处理

如果你已经将包含密码的配置文件提交到了 GitHub：

### 1. 立即更改数据库密码

在 Supabase Dashboard 中重置数据库密码：
1. 进入 Settings → Database
2. 点击 "Reset database password"
3. 生成新密码并保存

### 2. 从 Git 历史中删除敏感信息

**警告**：这会重写 Git 历史，如果有其他人克隆了仓库，需要通知他们重新克隆。

```bash
# 安装 git-filter-repo（如果还没安装）
# macOS: brew install git-filter-repo
# Linux: pip install git-filter-repo

# 从历史中删除敏感文件
git filter-repo --path src/main/resources/application-local.yml --invert-paths
git filter-repo --path src/main/resources/application-prod.yml --invert-paths

# 强制推送到远程仓库
git push origin --force --all
```

**更简单的方法**（如果仓库是新建的）：
```bash
# 删除 .git 目录，重新初始化
rm -rf .git
git init
git add .
git commit -m "Initial commit with secure configuration"
git remote add origin https://github.com/your-username/your-repo.git
git push -u origin main --force
```

### 3. 更新 GitHub 仓库

确保新的提交不包含敏感信息：
```bash
git status
# 确认 application-local.yml 和 .env 不在待提交列表中
git add .
git commit -m "feat: use environment variables for database configuration"
git push
```

## 检查清单

在提交代码前，请确认：

- [ ] `.env` 文件已添加到 .gitignore
- [ ] `application-local.yml` 已添加到 .gitignore
- [ ] 配置文件中使用了环境变量（`${DATABASE_URL}` 等）
- [ ] 创建了 `.env.example` 和 `application-local.yml.example` 模板文件
- [ ] 真实的密码只存在于本地的 `.env` 或 `application-local.yml` 文件中
- [ ] 运行 `git status` 确认敏感文件不在待提交列表中

## 团队协作

当其他开发者克隆项目时：

1. 复制环境变量模板：
```bash
cp .env.example .env
```

2. 联系项目管理员获取真实的数据库配置

3. 编辑 `.env` 文件，填入配置

4. 启动应用

## 参考资源

- [Spring Boot 外部化配置](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Render 环境变量](https://render.com/docs/environment-variables)
- [Supabase 数据库连接](https://supabase.com/docs/guides/database/connecting-to-postgres)
