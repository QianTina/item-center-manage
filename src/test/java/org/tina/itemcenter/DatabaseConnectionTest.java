package org.tina.itemcenter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnection() {
        // 测试数据库连接是否正常
        String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
        assertEquals("1", result);
        System.out.println("✅ 数据库连接成功！");
    }

    @Test
    void testDatabaseVersion() {
        // 测试 PostgreSQL 版本
        String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
        assertNotNull(version);
        assertTrue(version.contains("PostgreSQL"));
        System.out.println("✅ PostgreSQL 版本: " + version);
    }

    @Test
    void testDatabaseName() {
        // 测试当前数据库名称
        String dbName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        assertEquals("postgres", dbName);
        System.out.println("✅ 当前数据库: " + dbName);
    }
}
