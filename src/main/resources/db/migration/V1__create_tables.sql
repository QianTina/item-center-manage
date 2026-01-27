-- 物品管理系统数据库迁移脚本
-- 版本：V1
-- 描述：创建核心表结构（scene、item、location、audit_log）

-- ============================================
-- 1. 创建 scene 表（场景表）
-- ============================================
CREATE TABLE scene (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

COMMENT ON TABLE scene IS '场景表，用于数据隔离';
COMMENT ON COLUMN scene.id IS '场景 ID';
COMMENT ON COLUMN scene.name IS '场景名称';
COMMENT ON COLUMN scene.owner IS '场景所有者';
COMMENT ON COLUMN scene.created_at IS '创建时间';
COMMENT ON COLUMN scene.updated_at IS '更新时间';

-- ============================================
-- 2. 创建 item 表（物品表）
-- ============================================
CREATE TABLE item (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    tags JSONB,
    location_id BIGINT,
    quantity INT NOT NULL DEFAULT 1,
    status VARCHAR(50) NOT NULL,
    borrower VARCHAR(255),
    borrowed_at TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

COMMENT ON TABLE item IS '物品表';
COMMENT ON COLUMN item.id IS '物品 ID';
COMMENT ON COLUMN item.scene_id IS '场景 ID，用于数据隔离';
COMMENT ON COLUMN item.name IS '物品名称';
COMMENT ON COLUMN item.description IS '物品描述';
COMMENT ON COLUMN item.tags IS '标签列表（JSON 数组）';
COMMENT ON COLUMN item.location_id IS '位置 ID，可为空';
COMMENT ON COLUMN item.quantity IS '物品数量';
COMMENT ON COLUMN item.status IS '物品状态：AVAILABLE、BORROWED、DAMAGED、MAINTENANCE';
COMMENT ON COLUMN item.borrower IS '借用人';
COMMENT ON COLUMN item.borrowed_at IS '借出时间';
COMMENT ON COLUMN item.metadata IS '扩展元数据（JSON 对象）';
COMMENT ON COLUMN item.created_at IS '创建时间';
COMMENT ON COLUMN item.updated_at IS '更新时间';

-- 创建索引
CREATE INDEX idx_item_scene_id ON item(scene_id);
CREATE INDEX idx_item_location_id ON item(location_id);
CREATE INDEX idx_item_name ON item(name);
CREATE INDEX idx_item_status ON item(status);
CREATE INDEX idx_item_tags ON item USING GIN(tags);

-- ============================================
-- 3. 创建 location 表（位置表）
-- ============================================
CREATE TABLE location (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    path VARCHAR(1000),
    level INT NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

COMMENT ON TABLE location IS '位置表，支持树形层级结构';
COMMENT ON COLUMN location.id IS '位置 ID';
COMMENT ON COLUMN location.scene_id IS '场景 ID，用于数据隔离';
COMMENT ON COLUMN location.name IS '位置名称';
COMMENT ON COLUMN location.parent_id IS '父位置 ID，为空表示根位置';
COMMENT ON COLUMN location.path IS '完整路径，格式：/parent1/parent2/current';
COMMENT ON COLUMN location.level IS '层级深度，根节点为 0';
COMMENT ON COLUMN location.metadata IS '扩展元数据（JSON 对象）';
COMMENT ON COLUMN location.created_at IS '创建时间';
COMMENT ON COLUMN location.updated_at IS '更新时间';

-- 创建索引
CREATE INDEX idx_location_scene_id ON location(scene_id);
CREATE INDEX idx_location_parent_id ON location(parent_id);
CREATE INDEX idx_location_path ON location(path);

-- ============================================
-- 4. 创建 audit_log 表（审计日志表）
-- ============================================
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    before_state JSONB,
    after_state JSONB,
    operator VARCHAR(255),
    operated_at TIMESTAMP NOT NULL
);

COMMENT ON TABLE audit_log IS '审计日志表，记录所有关键操作';
COMMENT ON COLUMN audit_log.id IS '日志 ID';
COMMENT ON COLUMN audit_log.scene_id IS '场景 ID';
COMMENT ON COLUMN audit_log.entity_type IS '实体类型：ITEM、LOCATION';
COMMENT ON COLUMN audit_log.entity_id IS '实体 ID';
COMMENT ON COLUMN audit_log.operation_type IS '操作类型：CREATE、UPDATE、DELETE、MOVE、BORROW、RETURN';
COMMENT ON COLUMN audit_log.before_state IS '操作前状态（JSON 对象）';
COMMENT ON COLUMN audit_log.after_state IS '操作后状态（JSON 对象）';
COMMENT ON COLUMN audit_log.operator IS '操作人';
COMMENT ON COLUMN audit_log.operated_at IS '操作时间';

-- 创建索引
CREATE INDEX idx_audit_log_scene_id ON audit_log(scene_id);
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_operated_at ON audit_log(operated_at);

-- ============================================
-- 5. 插入测试数据（可选）
-- ============================================

-- 插入测试场景
INSERT INTO scene (name, owner, created_at, updated_at) 
VALUES ('测试场景', 'admin', NOW(), NOW());

-- 获取刚插入的场景 ID（用于后续测试数据）
-- 注意：在实际使用中，应该通过应用程序获取场景 ID
