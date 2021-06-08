package byx.orm;

import byx.orm.core.DaoGenerator;
import byx.orm.core.MethodContext;
import byx.orm.core.SqlGenerator;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        public boolean support(MethodContext ctx) {
            return ctx.getMethod().isAnnotationPresent(SelectAll.class);
        }

        @Override
        public String getSql(MethodContext ctx) {
            String tableName = ctx.getMethod().getAnnotation(SelectAll.class).value();
            return "SELECT * FROM " + tableName;
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
