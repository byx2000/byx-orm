package byx.orm;

import byx.orm.annotation.DynamicQuery;
import byx.orm.util.SqlBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DaoGeneratorTest2 extends BaseTest {
    private interface UserDao {
        @DynamicQuery(type = SqlProvider.class, method = "listByLevelRange1")
        List<User> listByLevelRange1(Integer low, Integer high);

        @DynamicQuery(type = SqlProvider.class)
        List<User> listByLevelRange2(Range range);

        @DynamicQuery(type = SqlProvider.class, method = "listByLevelRange3")
        List<User> listByLevelRange3(Integer low, Integer high);

        @DynamicQuery(type = SqlProvider.class)
        List<User> listByLevelRange4(Integer low, Integer high);

        @DynamicQuery(type = SqlProvider.class)
        List<User> listByLevelRange5(Integer low, Integer high);

        class SqlProvider {
            public String listByLevelRange1(Integer low, Integer high) {
                String sql = "SELECT * FROM t_user ";
                if (low != null && high != null) {
                    sql += String.format("WHERE level >= %d AND level <= %d", low, high);
                } else if (low != null) {
                    sql += String.format("WHERE level >= %d ", low);
                } else if (high != null) {
                    sql += String.format("WHERE level <= %d", high);
                }
                return sql;
            }

            public String listByLevelRange2(Range range) {
                Integer low = range.getLow();
                Integer high = range.getHigh();
                String sql = "SELECT * FROM t_user ";
                if (low != null && high != null) {
                    sql += String.format("WHERE level >= %d AND level <= %d", low, high);
                } else if (low != null) {
                    sql += String.format("WHERE level >= %d ", low);
                } else if (high != null) {
                    sql += String.format("WHERE level <= %d", high);
                }
                return sql;
            }

            public String listByLevelRange3(Integer low, Integer high) {
                String sql = "SELECT * FROM t_user ";
                if (low != null && high != null) {
                    sql += "WHERE level >= #{low} AND level <= #{high}";
                } else if (low != null) {
                    sql += "WHERE level >= #{low} ";
                } else if (high != null) {
                    sql += "WHERE level <= #{high}";
                }
                return sql;
            }

            public String listByLevelRange4(Integer low, Integer high) {
                SqlBuilder builder = new SqlBuilder();
                builder.select("*").from("t_user");
                if (low != null) {
                    builder.where("level >= #{low}");
                }
                if (high != null) {
                    builder.where("level <= #{high}");
                }
                return builder.build();
            }

            public String listByLevelRange5(Integer low, Integer high) {
                return new SqlBuilder(){
                    {
                        select("*");
                        from("t_user");
                        if (low != null) {
                            where("level >= #{low}");
                        }
                        if (high != null) {
                            where("level <= #{high}");
                        }
                    }
                }.build();
            }
        }
    }

    @Test
    public void test1() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        List<User> users = userDao.listByLevelRange1(2, 3);
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2 && u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange1(null, 3);
        assertEquals(3, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange1(2, null);
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2);
        }

        users = userDao.listByLevelRange1(null, null);
        assertEquals(3, users.size());

        users = userDao.listByLevelRange1(3, 2);
        assertTrue(users.isEmpty());
    }

    @Test
    public void test2() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        List<User> users = userDao.listByLevelRange2(new Range(2, 3));
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2 && u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange2(new Range(null, 3));
        assertEquals(3, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange2(new Range(2, null));
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2);
        }

        users = userDao.listByLevelRange2(new Range(null, null));
        assertEquals(3, users.size());

        users = userDao.listByLevelRange2(new Range(3, 2));
        assertTrue(users.isEmpty());
    }

    @Test
    public void test3() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        List<User> users = userDao.listByLevelRange3(2, 3);
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2 && u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange3(null, 3);
        assertEquals(3, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange3(2, null);
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2);
        }

        users = userDao.listByLevelRange3(null, null);
        assertEquals(3, users.size());

        users = userDao.listByLevelRange3(3, 2);
        assertTrue(users.isEmpty());
    }

    @Test
    public void test4() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        List<User> users = userDao.listByLevelRange4(2, 3);
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2 && u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange4(null, 3);
        assertEquals(3, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange4(2, null);
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2);
        }

        users = userDao.listByLevelRange4(null, null);
        assertEquals(3, users.size());

        users = userDao.listByLevelRange4(3, 2);
        assertTrue(users.isEmpty());
    }

    @Test
    public void test5() {
        UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

        List<User> users = userDao.listByLevelRange5(2, 3);
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2 && u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange5(null, 3);
        assertEquals(3, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() <= 3);
        }

        users = userDao.listByLevelRange5(2, null);
        assertEquals(2, users.size());
        for (User u : users) {
            assertTrue(u.getLevel() >= 2);
        }

        users = userDao.listByLevelRange5(null, null);
        assertEquals(3, users.size());

        users = userDao.listByLevelRange5(3, 2);
        assertTrue(users.isEmpty());
    }
}
