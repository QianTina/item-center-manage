# 更新日志

## [2026-01-27] 文档结构重组

### 变更内容

#### 文档分类整理

将所有文档按功能分类到 `docs/` 目录下的子目录：

**移动的文件**：
- `SUPABASE-CONNECTION-GUIDE.md` → `docs/deployment/SUPABASE-CONNECTION-GUIDE.md`
- `README-DEPLOY.md` → `docs/deployment/README-DEPLOY.md`
- `SECURITY_CONFIG.md` → `docs/security/SECURITY_CONFIG.md`
- `API_TEST.md` → `docs/development/API_TEST.md`
- `PROPERTY_TEST_GUIDE.md` → `docs/testing/PROPERTY_TEST_GUIDE.md`
- `cleanup-sensitive-data.sh` → `docs/security/cleanup-sensitive-data.sh`
- `reset-repo.sh` → `docs/security/reset-repo.sh`

**新增的文件**：
- `docs/README.md` - 文档索引和导航
- `README.md` - 更新主 README，添加清晰的文档导航
- `CHANGELOG.md` - 本文件

#### 文档目录结构

```
docs/
├── README.md                      # 文档索引
├── architecture.md                # 架构设计
├── CODE_CONSTRAINTS_ZH.md         # 代码约束
├── deployment/                    # 部署文档
│   ├── SUPABASE-CONNECTION-GUIDE.md
│   └── README-DEPLOY.md
├── development/                   # 开发文档
│   └── API_TEST.md
├── testing/                       # 测试文档
│   └── PROPERTY_TEST_GUIDE.md
└── security/                      # 安全文档
    ├── SECURITY_CONFIG.md
    ├── cleanup-sensitive-data.sh
    └── reset-repo.sh
```

### 优势

1. **更清晰的组织结构**：文档按功能分类，易于查找
2. **更好的可维护性**：新文档有明确的归属位置
3. **更专业的项目结构**：符合开源项目的最佳实践
4. **更好的导航**：通过 README 和文档索引快速找到所需文档

### 迁移指南

如果你有旧的书签或链接，请更新为新路径：

| 旧路径 | 新路径 |
|--------|--------|
| `/SUPABASE-CONNECTION-GUIDE.md` | `/docs/deployment/SUPABASE-CONNECTION-GUIDE.md` |
| `/README-DEPLOY.md` | `/docs/deployment/README-DEPLOY.md` |
| `/SECURITY_CONFIG.md` | `/docs/security/SECURITY_CONFIG.md` |
| `/API_TEST.md` | `/docs/development/API_TEST.md` |
| `/PROPERTY_TEST_GUIDE.md` | `/docs/testing/PROPERTY_TEST_GUIDE.md` |
| `/cleanup-sensitive-data.sh` | `/docs/security/cleanup-sensitive-data.sh` |
| `/reset-repo.sh` | `/docs/security/reset-repo.sh` |

---

## [2026-01-27] 属性测试实现

### 新增功能

- 添加 jqwik 依赖用于属性测试
- 实现场景管理的属性测试（Task 5.4）
- 创建属性测试指南文档

### 测试内容

- **属性 2：实体 ID 唯一性**（场景部分）
  - 验证需求：1.3
  - 测试策略：创建多个场景，验证 ID 唯一性
  - 迭代次数：100 次

---

## [2026-01-27] 安全配置更新

### 安全改进

- 更新数据库密码（旧密码已泄露）
- 验证新密码连接成功
- 创建安全配置文档和清理脚本

### 密码管理

- 使用环境变量管理敏感配置
- `.env` 文件已添加到 .gitignore
- 提供密码泄露后的处理方案

---

## [2026-01-27] 场景管理功能实现

### 已完成任务

- Task 1.1, 1.2: 项目初始化和基础设施
- Task 2.1: SceneContext 和 SceneInterceptor
- Task 3.1: 统一响应格式和全局异常处理
- Task 4.1: 数据库迁移脚本
- Task 5.1, 5.2, 5.3: 场景管理功能
- Task 5.4: 场景管理的属性测试

### 功能特性

- 场景创建、查询、详情查看
- 场景上下文管理（ThreadLocal）
- 统一响应格式
- 全局异常处理
- 数据库自动创建表结构
