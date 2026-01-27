#!/bin/bash

# 简单重置 Git 仓库脚本
# 适合新项目，会删除所有历史记录并重新开始

echo "⚠️  警告：此操作将删除所有 Git 历史记录！"
echo "这是最简单的方法，适合新项目。"
echo ""
read -p "是否继续？(yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "操作已取消"
    exit 0
fi

echo ""
echo "开始重置仓库..."

# 1. 备份远程仓库地址
REMOTE_URL=$(git remote get-url origin)
echo "远程仓库: $REMOTE_URL"

# 2. 删除 .git 目录
echo "删除 .git 目录..."
rm -rf .git

# 3. 重新初始化 Git
echo "重新初始化 Git..."
git init

# 4. 添加所有文件（.gitignore 会自动排除敏感文件）
echo "添加文件..."
git add .

# 5. 创建初始提交
echo "创建初始提交..."
git commit -m "Initial commit: Item Management System with secure configuration

- Use environment variables for database credentials
- Add security configuration guide
- Implement scene management API
- Add comprehensive documentation"

# 6. 添加远程仓库
echo "添加远程仓库..."
git remote add origin "$REMOTE_URL"

# 7. 创建 main 分支
git branch -M main

echo ""
echo "✅ 重置完成！"
echo ""
echo "下一步："
echo "1. 检查状态: git status"
echo "2. 强制推送到远程: git push -u origin main --force"
echo ""
echo "⚠️  注意：这会完全覆盖远程仓库！"
