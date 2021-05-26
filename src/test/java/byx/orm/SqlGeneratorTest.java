package byx.orm;

import byx.orm.annotation.*;
import byx.orm.core.SqlGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SqlGeneratorTest {
    @Prefix("SELECT * FROM users WHERE ")
    @Delimiter(" AND ")
    @Suffix(" ORDER BY ${orderBy} ${orderType}")
    private static class QueryObject1 {
        @Sql("username = #{username}")
        private String username;

        @Sql("password = #{password}")
        private String password;

        @Sql("level >= #{minVal}")
        private Integer minVal;

        @Sql("level <= #{maxVal}")
        private Integer maxVal;

        @Sql("(desc LIKE '%${keyword}%' OR name LIKE '%${keyword}%')")
        private String keyword;

        private String orderBy;

        private Boolean isDesc;

        public String getOrderType() {
            return (isDesc == null || !isDesc) ? "ASC" : "DESC";
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

        public Integer getMinVal() {
            return minVal;
        }

        public void setMinVal(Integer minVal) {
            this.minVal = minVal;
        }

        public Integer getMaxVal() {
            return maxVal;
        }

        public void setMaxVal(Integer maxVal) {
            this.maxVal = maxVal;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }

        public Boolean getDesc() {
            return isDesc;
        }

        public void setDesc(Boolean desc) {
            isDesc = desc;
        }
    }

    @Sql("SELECT * FROM users " +
            "WHERE username = #{username} " +
            "AND password = #{password} " +
            "AND keyword = #{keyword}")
    private static class QueryObject2 {
        private String username;
        private String password;

        public String getKeyword() {
            return username + " " + password;
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
    }

    @Test
    public void test1() {
        QueryObject1 qo1 = new QueryObject1();
        qo1.setUsername("aaa");
        qo1.setPassword("123");
        qo1.setMinVal(20);
        qo1.setMaxVal(50);
        qo1.setKeyword("byx");
        qo1.setOrderBy("time");
        qo1.setDesc(true);

        String sql = SqlGenerator.generate(qo1);
        System.out.println(sql);
        assertEquals("SELECT * FROM users WHERE username = 'aaa' AND password = '123' AND level >= 20 AND level <= 50 AND (desc LIKE '%byx%' OR name LIKE '%byx%') ORDER BY time DESC",
                sql);
    }

    @Test
    public void test2() {
        QueryObject1 qo1 = new QueryObject1();
        qo1.setPassword("123456");
        qo1.setMaxVal(100);
        qo1.setOrderBy("level");

        String sql = SqlGenerator.generate(qo1);
        System.out.println(sql);
        assertEquals("SELECT * FROM users WHERE password = '123456' AND level <= 100 ORDER BY level ASC", sql);
    }

    @Test
    public void test3() {
        QueryObject2 qo2 = new QueryObject2();
        qo2.setUsername("bbb");
        qo2.setPassword("456");

        String sql = SqlGenerator.generate(qo2);
        System.out.println(sql);
        assertEquals("SELECT * FROM users WHERE username = 'bbb' AND password = '456' AND keyword = 'bbb 456'", sql);
    }
}
