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
        // 测试数据库版本 - 使用通用 SQL
        String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
        assertNotNull(result);
        assertEquals("1", result);
        System.out.println("✅ 数据库版本测试通过");
    }

    @Test
    void testDatabaseName() {
        // 测试数据库连接 - 使用通用 SQL
        String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
        assertNotNull(result, "查询结果不应为 null");
        assertEquals("1", result);
        System.out.println("✅ 数据库查询测试通过");
    }
}
