package byx.orm;

import byx.orm.annotation.*;
import byx.orm.core.DaoGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class SqlObjectTest extends BaseTest {
    @Prefix("SELECT * FROM t_user WHERE ")
    @Delimiter(" AND ")
    @Suffix(" ORDER BY ${orderBy}")
    private static class UserQueryObject {
        @Sql("u_id = #{id}")
        private Integer id;

        @Sql("u_username = #{username}")
        private String username;

        @Sql("u_password = #{password}")
        private String password;

        private String orderBy;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }
    }

    @Prefix("UPDATE t_user SET ")
    @Delimiter(", ")
    @Suffix(" WHERE u_id = #{id}")
    private static class UserUpdateDTO {
        @Sql("u_username = #{username}")
        private String username;

        @Sql("u_password = #{password}")
        private String password;

        private Integer id;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    private interface UserDao {
        @SqlObject
        List<User> query(UserQueryObject qo);

        @SqlObject
        int update(UserUpdateDTO dto);

        @Query("SELECT * FROM t_user WHERE u_id = #{id}")
        User getById(Integer id);
    }

    @Test
    public void test1() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        UserQueryObject qo = new UserQueryObject();
        qo.setUsername("aaa");
        qo.setPassword("123");
        qo.setOrderBy("u_id");

        List<User> users = userDao.query(qo);
        System.out.println(users);
        assertEquals(1, users.size());
        assertEquals(1, users.get(0).getId());
        assertEquals("aaa", users.get(0).getUsername());
        assertEquals("123", users.get(0).getPassword());
        assertEquals(3, users.get(0).getLevel());
    }

    @Test
    public void test2() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        UserQueryObject qo = new UserQueryObject();
        qo.setId(3);
        qo.setOrderBy("u_id");

        List<User> users = userDao.query(qo);
        System.out.println(users);
        assertEquals(1, users.size());
        assertEquals(3, users.get(0).getId());
        assertEquals("ccc", users.get(0).getUsername());
        assertEquals("789", users.get(0).getPassword());
        assertEquals(2, users.get(0).getLevel());
    }

    @Test
    public void test3() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setId(1);
        dto.setUsername("byx");
        dto.setPassword("666");

        int count = userDao.update(dto);
        assertEquals(1, count);

        User user = userDao.getById(1);
        System.out.println(user);
        assertEquals(1, user.getId());
        assertEquals("byx", user.getUsername());
        assertEquals("666", user.getPassword());

        dto.setId(1);
        dto.setUsername("aaa");
        dto.setPassword("123");
        count = userDao.update(dto);
        assertEquals(1, count);

        user = userDao.getById(1);
        System.out.println(user);
        assertEquals(1, user.getId());
        assertEquals("aaa", user.getUsername());
        assertEquals("123", user.getPassword());
    }

    @Test
    public void test4() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setId(3);
        dto.setPassword("666");

        int count = userDao.update(dto);
        assertEquals(1, count);

        User user = userDao.getById(3);
        System.out.println(user);
        assertEquals(3, user.getId());
        assertEquals("ccc", user.getUsername());
        assertEquals("666", user.getPassword());

        dto.setId(3);
        dto.setPassword("789");
        count = userDao.update(dto);
        assertEquals(1, count);

        user = userDao.getById(3);
        System.out.println(user);
        assertEquals(3, user.getId());
        assertEquals("ccc", user.getUsername());
        assertEquals("789", user.getPassword());
    }
}
