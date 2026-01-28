#!/bin/bash

echo "=== 数据库连接测试 ==="
echo ""

# 加载环境变量
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
    echo "✅ 环境变量已加载"
else
    echo "❌ .env 文件不存在"
    exit 1
fi

echo ""
echo "当前配置："
echo "  DATABASE_URL: $DATABASE_URL"
echo "  DATABASE_USERNAME: $DATABASE_USERNAME"
echo "  DATABASE_PASSWORD: ${DATABASE_PASSWORD:0:5}***"
echo ""

# 尝试使用 psql 测试连接（如果可用）
if command -v psql &> /dev/null; then
    echo "使用 psql 测试连接..."
    
    # 从 JDBC URL 提取 PostgreSQL URL
    PG_URL=$(echo $DATABASE_URL | sed 's/jdbc:postgresql:\/\///')
    
    # 提取主机、端口、数据库
    HOST=$(echo $PG_URL | cut -d'/' -f1 | cut -d':' -f1)
    PORT=$(echo $PG_URL | cut -d'/' -f1 | cut -d':' -f2 | cut -d'?' -f1)
    DB=$(echo $PG_URL | cut -d'/' -f2 | cut -d'?' -f1)
    
    echo "  Host: $HOST"
    echo "  Port: $PORT"
    echo "  Database: $DB"
    echo ""
    
    PGPASSWORD=$DATABASE_PASSWORD psql -h $HOST -p $PORT -U $DATABASE_USERNAME -d $DB -c "SELECT version();" 2>&1
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ 数据库连接成功！"
    else
        echo ""
        echo "❌ 数据库连接失败"
    fi
else
    echo "⚠️  psql 未安装，跳过直接连接测试"
    echo "   建议安装 PostgreSQL 客户端：brew install postgresql"
fi

echo ""
echo "=== 运行 Spring Boot 测试 ==="
echo ""

# 运行 Maven 测试
bash mvnw test -Dtest=DatabaseConnectionTest

echo ""
echo "=== 测试完成 ==="
