package byx.orm;

import byx.orm.annotation.DynamicSql;
import byx.orm.annotation.Sql;
import byx.orm.core.DaoGenerator;
import byx.orm.util.SqlBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicUpdateTest extends BaseTest {
    private interface UserDao {
        @Sql("SELECT * FROM t_user WHERE u_id = #{id}")
        User getById(Integer id);

        @DynamicSql(type = SqlProvider.class, method = "update")
        void update(User user);

        @DynamicSql(type = SqlProvider.class)
        void update(int id, String username, String password, Integer level);

        class SqlProvider {
            public String update(User user) {
                SqlBuilder builder = new SqlBuilder().update("t_user");
                if (user.getUsername() != null) {
                    builder.set("u_username = #{user.username}");
                }
                if (user.getPassword() != null) {
                    builder.set("u_password = #{user.password}");
                }
                if (user.getLevel() != null) {
                    builder.set("level = #{user.level}");
                }
                builder.where("u_id = #{user.id}");
                return builder.build();
            }

            public String update(int id, String username, String password, Integer level) {
                SqlBuilder builder = new SqlBuilder().update("t_user");
                if (username != null) {
                    builder.set("u_username = #{username}");
                }
                if (password != null) {
                    builder.set("u_password = #{password}");
                }
                if (level != null) {
                    builder.set("level = #{level}");
                }
                builder.where("u_id = #{id}");
                return builder.build();
            }
        }
    }

    @Test
    public void test1() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        User user = new User();
        user.setId(1);
        user.setUsername("aaax");
        user.setPassword("123x");
        userDao.update(user);

        user = userDao.getById(1);
        assertEquals(1, user.getId());
        assertEquals("aaax", user.getUsername());
        assertEquals("123x", user.getPassword());
        assertEquals(3, user.getLevel());

        user.setId(1);
        user.setUsername("aaa");
        user.setPassword("123");
        userDao.update(user);

        user = userDao.getById(1);
        assertEquals(1, user.getId());
        assertEquals("aaa", user.getUsername());
        assertEquals("123", user.getPassword());
        assertEquals(3, user.getLevel());
    }

    @Test
    public void test2() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        userDao.update(1, null, null, 100);

        User user = userDao.getById(1);
        assertEquals(1, user.getId());
        assertEquals("aaa", user.getUsername());
        assertEquals("123", user.getPassword());
        assertEquals(100, user.getLevel());

        userDao.update(1, "aaa", "123", 3);

        user = userDao.getById(1);
        assertEquals(1, user.getId());
        assertEquals("aaa", user.getUsername());
        assertEquals("123", user.getPassword());
        assertEquals(3, user.getLevel());
    }
}
