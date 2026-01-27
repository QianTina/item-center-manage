# 属性测试指南

## 概述

本项目使用 jqwik 进行基于属性的测试（Property-Based Testing, PBT）。属性测试通过生成大量随机输入来验证系统的正确性属性。

## 已实现的属性测试

### Task 5.4: 场景管理的属性测试

**文件**: `src/test/java/org/tina/itemcenter/ScenePropertyTest.java`

**测试的属性**:
- **属性 2：实体 ID 唯一性**（场景部分）
  - 验证需求：1.3
  - 测试策略：创建多个场景（2-20个），验证所有场景的 ID 都是唯一的且不为 null
  - 迭代次数：100 次

## 如何运行属性测试

### 方法 1：使用 Maven 命令行

```bash
# 加载环境变量
export $(cat .env | xargs)

# 运行所有测试
bash mvnw test

# 运行特定的属性测试
bash mvnw test -Dtest=ScenePropertyTest
```

### 方法 2：使用 IntelliJ IDEA

1. 打开 `ScenePropertyTest.java` 文件
2. 右键点击类名或测试方法
3. 选择 "Run 'ScenePropertyTest'" 或 "Debug 'ScenePropertyTest'"
4. 确保在 Run Configuration 中设置了环境变量：
   - DATABASE_URL
   - DATABASE_USERNAME
   - DATABASE_PASSWORD

### 方法 3：使用测试脚本

```bash
./run-property-test.sh
```

## 属性测试配置

### jqwik 配置

- **依赖版本**: jqwik 1.9.2
- **默认迭代次数**: 100 次（可通过 `@Property(tries = 100)` 配置）
- **随机种子**: 自动生成（失败时会记录种子以便重现）

### 测试环境

- **Spring Profile**: local
- **数据库**: PostgreSQL (Supabase)
- **事务管理**: 每个测试方法使用 `@Transactional` 自动回滚

## 属性测试最佳实践

1. **每个属性至少 100 次迭代**：确保覆盖足够多的输入组合
2. **使用 @Tag 标注属性**：格式为 `@Tag("Feature: item-management-system, Property {number}: {property_text}")`
3. **清理测试数据**：在测试开始时清理数据库，避免数据污染
4. **验证不变性**：属性测试应该验证系统的不变性，而不是特定的输出值
5. **使用自定义生成器**：通过 `@Provide` 方法提供符合业务规则的测试数据生成器

## 故障排查

### 测试失败时

1. **查看失败的随机种子**：jqwik 会在失败时输出种子值
2. **使用种子重现失败**：在 `@Property` 注解中添加 `seed = "失败的种子值"`
3. **检查数据库状态**：确保数据库连接正常，环境变量配置正确
4. **查看详细日志**：设置 `logging.level.org.tina.itemcenter=DEBUG`

### Maven 命令失败

如果 Maven wrapper 命令失败，尝试：

1. 检查环境变量是否正确加载：`echo $DATABASE_URL`
2. 使用 bash 明确运行：`bash mvnw test`
3. 检查 Maven 进程是否冲突：`ps aux | grep maven`
4. 使用 IDE 运行测试（推荐）

## 下一步

继续实现其他属性测试：
- Task 5.5: 场景管理的单元测试
- Task 6.1: 运行所有测试，确保基础设施正常工作
