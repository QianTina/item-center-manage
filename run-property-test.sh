#!/bin/bash

# 加载环境变量
export $(cat .env | xargs)

# 运行属性测试
bash mvnw test -Dtest=ScenePropertyTest
