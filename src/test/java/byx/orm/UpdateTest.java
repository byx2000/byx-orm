package byx.orm;

import byx.orm.annotation.Query;
import byx.orm.annotation.Update;
import byx.orm.core.DaoGenerator;
import byx.util.jdbc.JdbcUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateTest extends BaseTest {
    private interface UserDao {
        @Update("INSERT INTO t_user(u_username, u_password, level) VALUES(#{username}, #{password}, #{level})")
        int insert(String username, String password, int level);

        @Update("DELETE FROM t_user WHERE u_username = #{username}")
        void deleteByUsername(String username);

        @Query("SELECT COUNT(*) FROM t_user")
        int count();

        @Query("SELECT * FROM t_user WHERE level >= #{level}")
        List<User> listByLevel(Integer level);
    }

    @Test
    public void test1() {
        UserDao userDao = new DaoGenerator(new JdbcUtils(dataSource())).generate(UserDao.class);

        int row = userDao.insert("byx", "666", 100);
        assertEquals(1, row);
        assertEquals(4, userDao.count());

        List<User> users = userDao.listByLevel(100);
        assertEquals(1, users.size());
        assertEquals("byx", users.get(0).getUsername());
        assertEquals("666", users.get(0).getPassword());
        assertEquals(100, users.get(0).getLevel());

        userDao.deleteByUsername("byx");
        assertEquals(3, userDao.count());

        userDao.deleteByUsername("John");
    }
}
