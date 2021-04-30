package byx.orm;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

public class BaseTest {
    protected DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite::resource:test.db");
        dataSource.setUsername("");
        dataSource.setPassword("");
        dataSource.setTestWhileIdle(false);
        return dataSource;
    }
}
