package byx.orm;

import byx.orm.core.SqlBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SqlBuilderTest {
    @Test
    public void test1() {
        String sql = new SqlBuilder()
                .select("*")
                .from("t_user")
                .build();
        assertEquals("SELECT * FROM t_user", sql);
    }

    @Test
    public void test2() {
        String sql = new SqlBuilder()
                .select("id")
                .select("username")
                .select("password")
                .from("users")
                .build();
        assertEquals("SELECT id, username, password FROM users", sql);
    }

    @Test
    public void test3() {
        String sql = new SqlBuilder()
                .select("id")
                .from("users")
                .from("books")
                .where("id = 1001")
                .build();
        assertEquals("SELECT id FROM users, books WHERE id = 1001", sql);
    }

    @Test
    public void test4() {
        String sql = new SqlBuilder()
                .select("id")
                .select("name")
                .from("users")
                .where("id = 1001")
                .where("level > 30")
                .build();
        assertEquals("SELECT id, name FROM users WHERE id = 1001 AND level > 30", sql);
    }

    @Test
    public void test5() {
        String sql = new SqlBuilder()
                .select("id")
                .select("name")
                .from("users")
                .from("books")
                .where("id = 1001")
                .where("level > 30")
                .build();
        assertEquals("SELECT id, name FROM users, books WHERE id = 1001 AND level > 30", sql);
    }

    @Test
    public void test6() {
        String sql = new SqlBuilder()
                .select("name")
                .from("users")
                .append("LIMIT 100")
                .build();
        assertEquals("SELECT name FROM users LIMIT 100", sql);
    }

    @Test
    public void test7() {
        String sql = new SqlBuilder()
                .select("name")
                .from("users")
                .append("LIMIT 100")
                .append("OFFSET 50")
                .build();
        assertEquals("SELECT name FROM users LIMIT 100 OFFSET 50", sql);
    }

    @Test
    public void test8() {
        String sql = new SqlBuilder()
                .select("id")
                .from("users")
                .select("username")
                .from("books")
                .build();
        assertEquals("SELECT id, username FROM users, books", sql);
    }

    @Test
    public void test9() {
        String sql = new SqlBuilder()
                .update("users")
                .set("username = 'aaa'")
                .set("password = '123'")
                .where("id = 1001")
                .build();
        assertEquals("UPDATE users SET username = 'aaa', password = '123' WHERE id = 1001", sql);
    }
}
