package byx.orm;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

public class Test1 {
    private DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite::resource:test.db");
        dataSource.setUsername("");
        dataSource.setPassword("");
        dataSource.setTestWhileIdle(false);
        return dataSource;
    }

    private UserDao getUserDao() {
        DaoGenerator daoGenerator = new DaoGenerator(dataSource());
        return daoGenerator.generate(UserDao.class);
    }

    @Test
    public void test1() {
        UserDao userDao = getUserDao();

        List<User> users = userDao.listAll();
        assertEquals(3, users.size());
        assertEquals(List.of(1, 2, 3), users.stream().map(User::getId).collect(Collectors.toList()));
        assertEquals(List.of("aaa", "bbb", "ccc"), users.stream().map(User::getUsername).collect(Collectors.toList()));
        assertEquals(List.of("123", "456", "789"), users.stream().map(User::getPassword).collect(Collectors.toList()));
        assertEquals(List.of(3, 1, 2), users.stream().map(User::getLevel).collect(Collectors.toList()));
    }

    @Test
    public void test2() {
        UserDao userDao = getUserDao();

        List<User> users = userDao.listByLevel(2);
        assertEquals(2, users.size());
        assertEquals(List.of(1, 3), users.stream().map(User::getId).collect(Collectors.toList()));
        assertEquals(List.of("aaa", "ccc"), users.stream().map(User::getUsername).collect(Collectors.toList()));
        assertEquals(List.of("123", "789"), users.stream().map(User::getPassword).collect(Collectors.toList()));
        assertEquals(List.of(3, 2), users.stream().map(User::getLevel).collect(Collectors.toList()));

        assertTrue(userDao.listByLevel(100).isEmpty());
    }

    @Test
    public void test3() {
        UserDao userDao = getUserDao();

        User user = userDao.getById(2);
        assertEquals(2, user.getId());
        assertEquals("bbb", user.getUsername());
        assertEquals("456", user.getPassword());
        assertEquals(1, user.getLevel());

        assertNull(userDao.getById(100));
    }

    @Test
    public void test4() {
        UserDao userDao = getUserDao();

        User user = userDao.getByUsernameAndPassword("ccc", "789");
        assertEquals(3, user.getId());
        assertEquals("ccc", user.getUsername());
        assertEquals("789", user.getPassword());
        assertEquals(2, user.getLevel());

        assertNull(userDao.getByUsernameAndPassword("byx", "666"));
    }

    @Test
    public void test5() {
        UserDao userDao = getUserDao();

        int cnt = userDao.count1();
        assertEquals(3, cnt);

        cnt = userDao.countByLevel1(3);
        assertEquals(1, cnt);

        cnt = userDao.countByLevel1(100);
        assertEquals(0, cnt);

        cnt = userDao.count2();
        assertEquals(3, cnt);

        cnt = userDao.countByLevel2(3);
        assertEquals(1, cnt);

        cnt = userDao.countByLevel2(100);
        assertEquals(0, cnt);
    }

    @Test
    public void test6() {
        UserDao userDao = getUserDao();

        int row = userDao.insert("byx", "666", 100);
        assertEquals(1, row);
        assertEquals(4, userDao.count1());

        List<User> users = userDao.listByLevel(100);
        assertEquals(1, users.size());
        assertEquals("byx", users.get(0).getUsername());
        assertEquals("666", users.get(0).getPassword());
        assertEquals(100, users.get(0).getLevel());

        userDao.deleteByUsername("byx");
        assertEquals(3, userDao.count1());

        userDao.deleteByUsername("John");
    }
}
