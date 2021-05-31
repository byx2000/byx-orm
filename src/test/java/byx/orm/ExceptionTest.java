package byx.orm;

import byx.orm.annotation.DynamicQuery;
import byx.orm.annotation.DynamicUpdate;
import byx.orm.annotation.Query;
import byx.orm.annotation.Update;
import byx.orm.core.DaoGenerator;
import byx.orm.exception.ByxOrmException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest extends BaseTest {
    private interface UserDao {
        @Query("SELECT * FROM t_user")
        User list1();

        @Query("SELECT * FROM t_user")
        int list2();

        @Query("SELECT * FROM t_user")
        List<?> list3();

        @DynamicQuery(type = SqlProvider.class, method = "fdvbdf")
        List<User> list4();

        @Query("SELECT * FROM t_user")
        void list5();

        @Query("sdjgfsjkdbvkjsbvkjsd")
        List<User> list6();

        @DynamicQuery(type = SqlProvider.class, method = "list7")
        List<User> list7();

        @Update("sdjvkgkasjdbasjdk")
        void update1();

        @DynamicUpdate(type = SqlProvider.class, method = "update")
        void update2();

        @DynamicUpdate(type = SqlProvider.class, method = "update3")
        void update3();

        @Query("SELECT u_id, u_username FROM t_user WHERE u_id = 1")
        String get1();

        @Query("SELECT * FROM t_user WHERE username = #{username}")
        String get2();

        @Query("SELECT u_username FROM t_user WHERE id = #{username.id}")
        String get3(String username);

        class SqlProvider {
            public String list7() {
                return "sdjsvgfshdjhdgkhabgfjd";
            }

            public String update3() {
                return "sdfjjvsavfkguibrfjkvbasjkdbv";
            }
        }
    }

    @Test
    public void test1() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::list1);
    }

    @Test
    public void test2() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::list2);
    }

    @Test
    public void test3() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::list3);
    }

    @Test
    public void test4() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::list4);
    }
    @Test
    public void test5() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::list5);
    }

    @Test
    public void test6() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::list6);
    }

    @Test
    public void test7() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::list7);
    }

    @Test
    public void test8() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::update1);
    }

    @Test
    public void test9() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::update2);
    }

    @Test
    public void test10() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::update3);
    }

    @Test
    public void test11() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        System.out.println(userDao.toString());
        System.out.println(userDao.hashCode());
    }

    @Test
    public void test12() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::get1);
    }

    @Test
    public void test13() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::get2);
    }

    @Test
    public void test14() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, () -> userDao.get3("aaa"));
    }
}
