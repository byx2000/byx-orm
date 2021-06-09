package byx.orm;

import byx.orm.annotation.Query;
import byx.orm.core.DaoGenerator;
import byx.orm.core.MethodContext;
import byx.orm.core.SqlExecutor;
import byx.orm.exception.ByxOrmException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CustomSqlExecutorTest extends BaseTest {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private @interface MyAnnotation {

    }

    private interface UserDao {
        @Query("SELECT * FROM t_user")
        @MyAnnotation
        int listAll();
    }

    private static class MySqlExecutor implements SqlExecutor {
        @Override
        public boolean support(MethodContext ctx) {
            return ctx.getMethod().isAnnotationPresent(MyAnnotation.class);
        }

        @Override
        public Object execute(MethodContext ctx, String sql) {
            flag = true;
            System.out.println(sql);
            assertEquals("SELECT * FROM t_user", sql);
            return 1001;
        }
    }

    private static boolean flag;

    @Test
    public void test1() {
        DaoGenerator generator = new DaoGenerator(dataSource());
        generator.addSqlExecutor(new MySqlExecutor());
        UserDao userDao = generator.generate(UserDao.class);

        flag = false;
        int result = userDao.listAll();
        assertTrue(flag);
        assertEquals(1001, result);
    }

    @Test
    public void test2() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);
        assertThrows(ByxOrmException.class, userDao::listAll);
    }
}
