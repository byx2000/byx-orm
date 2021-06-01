package byx.orm;

import byx.orm.core.DaoGenerator;
import byx.orm.core.SqlGenerator;
import byx.orm.core.SqlType;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CustomSqlGeneratorTest extends BaseTest {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private @interface SelectAll {
        String value();
    }

    private interface UserDao {
        @SelectAll("t_user")
        List<User> listAll();
    }

    private static class MySqlGenerator implements SqlGenerator {
        @Override
        public boolean support(Method method, Object[] params) {
            return method.isAnnotationPresent(SelectAll.class);
        }

        @Override
        public String getSql(Method method, Object[] params) {
            String tableName = method.getAnnotation(SelectAll.class).value();
            return "SELECT * FROM " + tableName;
        }

        @Override
        public SqlType getType() {
            return SqlType.QUERY;
        }
    }

    @Test
    public void test() {
        DaoGenerator daoGenerator = new DaoGenerator(dataSource());
        daoGenerator.addSqlGenerator(new MySqlGenerator());
        UserDao userDao = daoGenerator.generate(UserDao.class);

        List<User> users = userDao.listAll();
        System.out.println(users);
        assertEquals(3, users.size());
        assertEquals(List.of(1, 2, 3), users.stream().map(User::getId).collect(Collectors.toList()));
        assertEquals(List.of("aaa", "bbb", "ccc"), users.stream().map(User::getUsername).collect(Collectors.toList()));
        assertEquals(List.of("123", "456", "789"), users.stream().map(User::getPassword).collect(Collectors.toList()));
        assertEquals(List.of(3, 1, 2), users.stream().map(User::getLevel).collect(Collectors.toList()));
    }
}
