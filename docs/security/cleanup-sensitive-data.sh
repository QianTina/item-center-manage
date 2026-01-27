#!/bin/bash

# 清理 Git 历史中的敏感数据脚本
# 警告：这会重写 Git 历史！

echo "⚠️  警告：此操作将重写 Git 历史！"
echo "请确保："
echo "1. 已经备份了重要数据"
echo "2. 已经更改了 Supabase 数据库密码"
echo "3. 如果有其他人克隆了仓库，需要通知他们重新克隆"
echo ""
read -p "是否继续？(yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "操作已取消"
    exit 0
fi

echo ""
echo "开始清理敏感数据..."

# 方法 1: 使用 git filter-branch（内置命令）
echo "使用 git filter-branch 删除敏感文件..."

# 从历史中删除 application-local.yml
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch src/main/resources/application-local.yml" \
  --prune-empty --tag-name-filter cat -- --all

# 从历史中删除 application-prod.yml（如果有的话）
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch src/main/resources/application-prod.yml" \
  --prune-empty --tag-name-filter cat -- --all

echo ""
echo "清理 Git 引用..."
rm -rf .git/refs/original/
git reflog expire --expire=now --all
git gc --prune=now --aggressive

echo ""
echo "✅ 清理完成！"
echo ""
echo "下一步："
echo "1. 检查当前状态: git status"
echo "2. 添加新文件: git add ."
echo "3. 提交更改: git commit -m 'security: use environment variables for database configuration'"
echo "4. 强制推送到远程: git push origin main --force"
echo ""
echo "⚠️  注意：强制推送会覆盖远程仓库的历史！"
