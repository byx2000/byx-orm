package byx.orm;

import byx.orm.annotation.Query;
import byx.util.jdbc.JdbcUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryTest extends BaseTest {
    private interface UserDao {
        @Query("SELECT * FROM t_user WHERE u_id = #{id}")
        User getById(int id);

        @Query("SELECT * FROM t_user WHERE u_username = #{username} AND u_password = #{password}")
        User getByUsernameAndPassword(String username, String password);

        @Query("SELECT * FROM t_user")
        List<User> listAll();

        @Query("SELECT * FROM t_user WHERE level >= #{level}")
        List<User> listByLevel(Integer level);

        @Query("SELECT COUNT(*) FROM t_user")
        Integer count1();

        @Query("SELECT COUNT(*) FROM t_user")
        int count2();

        @Query("SELECT COUNT(*) FROM t_user WHERE level = #{level}")
        Integer countByLevel1(Integer level);

        @Query("SELECT COUNT(*) FROM t_user WHERE level = #{level}")
        int countByLevel2(Integer level);

        @Query("SELECT * FROM t_user WHERE level >= #{range.low} AND level <= #{range.high}")
        List<User> listOfLevelRange(Range range);

        @Query("SELECT u_username FROM t_user")
        List<String> listAllUsername();

        @Query("SELECT u_id, u_username FROM t_user WHERE u_id = #{id}")
        User getById2(Integer id);

        @Query("SELECT u_id, 123 AS value FROM t_user WHERE u_id = #{id}")
        User getById3(Integer id);

        @Query("SELECT u_username FROM t_user WHERE u_id = #{id}")
        String getUsername(Integer id);

        @Query("SELECT * FROM t_user ORDER BY ${orderBy}")
        List<User> listWithOrder(String orderBy);
    }

    @Test
    public void test1() {
        UserDao userDao = new DaoGenerator(new JdbcUtils(dataSource())).generate(UserDao.class);

        List<User> users = userDao.listAll();
        assertEquals(3, users.size());
        assertEquals(List.of(1, 2, 3), users.stream().map(User::getId).collect(Collectors.toList()));
        assertEquals(List.of("aaa", "bbb", "ccc"), users.stream().map(User::getUsername).collect(Collectors.toList()));
        assertEquals(List.of("123", "456", "789"), users.stream().map(User::getPassword).collect(Collectors.toList()));
        assertEquals(List.of(3, 1, 2), users.stream().map(User::getLevel).collect(Collectors.toList()));
    }

    @Test
    public void test2() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

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
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        User user = userDao.getById(2);
        assertEquals(2, user.getId());
        assertEquals("bbb", user.getUsername());
        assertEquals("456", user.getPassword());
        assertEquals(1, user.getLevel());

        assertNull(userDao.getById(100));
    }

    @Test
    public void test4() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        User user = userDao.getByUsernameAndPassword("ccc", "789");
        assertEquals(3, user.getId());
        assertEquals("ccc", user.getUsername());
        assertEquals("789", user.getPassword());
        assertEquals(2, user.getLevel());

        assertNull(userDao.getByUsernameAndPassword("byx", "666"));
    }

    @Test
    public void test5() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

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
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        List<User> users = userDao.listOfLevelRange(new Range(2, 4));
        assertEquals(2, users.size());
        assertEquals(List.of(3, 2), users.stream().map(User::getLevel).collect(Collectors.toList()));
    }

    @Test
    public void test7() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        List<String> usernames = userDao.listAllUsername();
        assertEquals(List.of("aaa", "bbb", "ccc"), usernames);
    }

    @Test
    public void test8() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        User user = userDao.getById2(1);
        assertEquals(1, user.getId());
        assertEquals("aaa", user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getLevel());
    }

    @Test
    public void test9() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        User user = userDao.getById3(2);
        assertEquals(2, user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getLevel());
    }

    @Test
    public void test10() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        String username = userDao.getUsername(1);
        assertEquals("aaa", username);

        username = userDao.getUsername(2);
        assertEquals("bbb", username);

        username = userDao.getUsername(3);
        assertEquals("ccc", username);
    }

    @Test
    public void test11() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        List<User> users = userDao.listWithOrder("level");
        System.out.println(users);
        assertEquals(3, users.size());
        assertEquals(2, users.get(0).getId());
        assertEquals(3, users.get(1).getId());
        assertEquals(1, users.get(2).getId());
    }
}
