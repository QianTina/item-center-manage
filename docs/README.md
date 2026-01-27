# 文档索引

本目录包含物品管理系统的所有文档，按类别组织。

## 📂 文档结构

```
docs/
├── README.md                      # 本文件 - 文档索引
├── architecture.md                # 系统架构设计
├── CODE_CONSTRAINTS_ZH.md         # 代码约束和规范
├── deployment/                    # 部署相关文档
│   ├── SUPABASE-CONNECTION-GUIDE.md
│   └── README-DEPLOY.md
├── development/                   # 开发相关文档
│   └── API_TEST.md
├── testing/                       # 测试相关文档
│   └── PROPERTY_TEST_GUIDE.md
└── security/                      # 安全相关文档
    ├── SECURITY_CONFIG.md
    ├── cleanup-sensitive-data.sh
    └── reset-repo.sh
```

## 📖 核心文档

### [项目结构](PROJECT_STRUCTURE.md)
完整的项目目录结构说明，包括：
- 目录组织原则
- 分层架构说明
- 文件命名规范
- 扩展指南

### [架构设计](architecture.md)
系统的整体架构设计，包括：
- 分层架构说明
- 技术选型
- 设计原则
- 组件关系

### [代码约束](CODE_CONSTRAINTS_ZH.md)
开发必须遵循的代码规范，包括：
- 领域驱动设计（DDD）原则
- 分层架构约束
- 命名规范
- 禁止事项

## 🚀 部署文档

### [Supabase 连接指南](deployment/SUPABASE-CONNECTION-GUIDE.md)
如何配置和连接 Supabase PostgreSQL 数据库：
- 获取连接信息
- 配置连接字符串
- 连接池设置
- 常见问题

### [部署指南](deployment/README-DEPLOY.md)
如何将应用部署到 Render：
- Render 配置
- 环境变量设置
- 部署流程
- 免费层限制

## 💻 开发文档

### [API 测试指南](development/API_TEST.md)
API 端点的测试示例：
- 场景管理 API
- 请求示例
- 响应格式
- 错误处理

## 🧪 测试文档

### [属性测试指南](testing/PROPERTY_TEST_GUIDE.md)
基于属性的测试（Property-Based Testing）说明：
- jqwik 使用指南
- 属性测试示例
- 如何运行测试
- 故障排查

## 🔒 安全文档

### [安全配置指南](security/SECURITY_CONFIG.md)
如何安全地管理敏感配置：
- 环境变量配置
- 密码管理最佳实践
- 已泄露密码的处理
- 安全检查清单

### [敏感数据清理脚本](security/cleanup-sensitive-data.sh)
从 Git 历史中删除敏感数据的脚本：
- 使用 git filter-branch
- 清理 Git 引用
- 强制推送到远程

### [仓库重置脚本](security/reset-repo.sh)
简单的仓库重置工具（适合新项目）：
- 删除所有历史
- 重新初始化
- 创建干净的初始提交

## 📝 文档编写规范

### 文档分类原则

- **deployment/**: 与部署、运维相关的文档
- **development/**: 与开发、调试相关的文档
- **testing/**: 与测试相关的文档
- **security/**: 与安全、密码管理相关的文档

### 文档命名规范

- 使用英文命名，单词之间用 `-` 连接
- 使用大写字母开头（如 `README-DEPLOY.md`）
- 脚本文件使用小写字母和 `-` 连接（如 `cleanup-sensitive-data.sh`）

### 文档内容要求

- 使用 Markdown 格式
- 包含清晰的标题层级
- 提供代码示例
- 包含常见问题解答
- 保持更新

## 🔄 文档更新

当添加新文档时：

1. 将文档放入合适的分类目录
2. 更新本索引文件
3. 在主 README.md 中添加链接（如果是重要文档）
4. 确保文档格式符合规范

## 📞 反馈

如果发现文档有误或需要补充，请：
- 提交 Issue
- 提交 Pull Request
- 联系项目维护者
