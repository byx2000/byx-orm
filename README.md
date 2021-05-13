# ByxOrm——简易ORM框架

ByxOrm是一个模仿MyBatis设计的轻量级ORM框架，支持以下特性：

* 使用动态代理生成Dao接口的实现类
* 使用注解配置Dao方法对应的SQL语句
* 使用注解配置实体类字段与数据库列名的对应关系
* 动态查询和动态更新

## Maven引入

```xml
<repositories>
    <repository>
        <id>byx-maven-repo</id>
        <name>byx-maven-repo</name>
        <url>https://gitee.com/byx2000/maven-repo/raw/master/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>byx.orm</groupId>
        <artifactId>byx-orm</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## 开启`-parameters`编译选项

由于ByxOrm运行过程中需要读取方法参数名，所以需要在`pom.xml`中启用`-parameters`编译选项：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <compilerArgs>
                    <arg>-parameters</arg>
                </compilerArgs>
                <source>${java.version}</source>
                <target>${java.version}</target>
                <compilerVersion>${java.version}</compilerVersion>
                <encoding>${project.build.sourceEncoding}</encoding>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 快速入门

通过一个简单的例子来快速了解ByxOrm的特性。

首先在数据库中创建一个`user`表，并插入如下数据：

|u_id|u_username|u_password|
|---|---|---|
|1|aaa|123|
|2|bbb|456|
|3|ccc|789|

导入数据库驱动类和连接池依赖，在启动类中写一个方法用来返回`DataSource`：

```java
public class Main {
    private static DataSource dataSource() {
        // 返回一个DataSource ...
    }

    public static void main(String[] args) {
        // ...
    }
}
```

编写用户实体类`User`，并配置字段名与列名的映射：

```java
public class User {
    @Column("u_id")
    private Integer id;

    @Column("u_username")
    private String username;

    @Column("u_password")
    private String password;

    // 省略getters、setters和toString ...
}
```

编写数据访问接口`UserDao`，并配置每个方法的SQL语句：

```java
public interface UserDao {
    /**
     * 查询所有用户，返回列表
     */
    @Query("SELECT * FROM user")
    List<User> listAll();

    /**
     * 查询指定id的用户，返回单个对象
     */
    @Query("SELECT * FROM user WHERE u_id = #{id}")
    User getById(Integer id);

    /**
     * 多条件查询用户，动态构造SQL语句
     */
    @DynamicQuery(type = SqlProvider.class, method = "query")
    List<User> query(String username, String password);

    /**
     * 查询用户总数
     */
    @Query("SELECT COUNT(0) FROM user")
    int count();

    /**
     * 插入用户，无返回值
     */
    @Update("INSERT into user(u_username, u_password) " +
            "VALUES(#{user.username}, #{user.password})")
    void insert(User user);

    /**
     * 删除指定id的用户，返回影响行数
     */
    @Update("DELETE FROM user WHERE u_id = #{id}")
    int delete(Integer id);

    class SqlProvider {
        /**
         * 提供动态查询SQL
         */
        public String query(String username, String password) {
            return new SqlBuilder(){
                {
                    select("*");
                    from("user");
                    if (username != null) {
                        where("u_username = #{username}");
                    }
                    if (password != null) {
                        where("u_password = #{password}");
                    }
                }
            }.build();
        }
    }
}
```

在`main`函数中依次测试`UserDao`中的各个方法：

```java
public static void main(String[] args) {
    // 生成UserDao的实现类
    UserDao userDao = new DaoGenerator(dataSource()).generate(UserDao.class);

    System.out.println("查询所有用户列表：");
    List<User> users = userDao.listAll();
    for (User u : users) {
        System.out.println(u);
    }

    System.out.println("查询id为2的用户：");
    User user = userDao.getById(2);
    System.out.println(user);

    System.out.println("查询用户名为ccc的用户：");
    users = userDao.query("ccc", null);
    for (User u : users) {
        System.out.println(u);
    }

    System.out.println("插入用户：");
    user.setUsername("byx");
    user.setPassword("666");
    userDao.insert(user);

    System.out.println("查询用户总数：");
    System.out.println(userDao.count());

    System.out.println("删除id为1的用户：");
    int row = userDao.delete(1);
    System.out.println("影响行数：" + row);

    System.out.println("查询所有用户列表：");
    users = userDao.listAll();
    for (User u : users) {
        System.out.println(u);
    }
}
```

控制台输出如下：

```
查询所有用户列表：
sql: SELECT * FROM user
User{id=1, username='aaa', password='123'}
User{id=2, username='bbb', password='456'}
User{id=3, username='ccc', password='789'}
查询id为2的用户：
sql: SELECT * FROM user WHERE u_id = 2
User{id=2, username='bbb', password='456'}
查询用户名为ccc的用户：
sql: SELECT * FROM user WHERE u_username = 'ccc'
User{id=3, username='ccc', password='789'}
插入用户：
sql: INSERT into user(u_username, u_password) VALUES('byx', '666')
查询用户总数：
sql: SELECT COUNT(0) FROM user
4
删除id为1的用户：
sql: DELETE FROM user WHERE u_id = 1
影响行数：1
查询所有用户列表：
sql: SELECT * FROM user
User{id=2, username='bbb', password='456'}
User{id=3, username='ccc', password='789'}
User{id=4, username='byx', password='666'}
```

## @Query注解

该注解用于指定查询操作的SQL，与MyBatis的`@Select`类似，支持`#{...}`占位符。

使用`@Query`标注的方法的返回值可以为以下形式：

|返回值类型|说明|
|---|---|
|基本类型或`String`|查询单个值或结果总数|
|JavaBean|查询单行，并把该行数据转换成JavaBean|
|`List`|查询多行，每行封装成一个JavaBean|

例子：

```java
public interface UserDao {
    // 查询所有用户
    @Query("SELECT * FROM user")
    List<User> listAll();

    // 根据id查询用户
    @Query("SELECT * FROM user WHERE id = #{id}")
    User getById(Integer id);

    // 查询用户总数
    @Query("SELECT COUNT(0) FROM user")
    int count();    
}
```

## @Update注解

该注解用于指定更新操作的SQL，更新操作包括`insert`、`update`、`delete`，与MyBatis的`Update`、`Insert`、`Delete`类似，支持`#{...}`占位符。

使用`@Update`标注的方法的返回值可以为以下形式：

|返回值类型|说明|
|---|---|
|`int`|返回受影响行数|
|`void`|直接执行操作，什么也不返回|

例子：

```java
public interface UserDao {
    // 插入用户
    @Update("INSERT INTO user(username, password) VALUES(#{user.username}, #{user.password})")
    int insert(User user);

    // 删除用户
    @Update("DELETE FROM user WHERE id = #{id}")
    void delete(Integer id);

    // 更新用户名
    @Update("UPDATE user SET username = #{username} WHERE id = #{id}")
    void update(Integer id, String username);
}
```

## @DynamicQuery注解

该注解用于动态生成查询SQL字符串，与MyBatis中的`SelectProvider`类似，需要指定以下两个属性：

* `type`: 生成SQL字符串的类
* `method`: 生成SQL字符串的方法名

`type`中的`method`方法需要接收与对应的Dao方法相同的参数列表，并返回`String`类型。

例子：

```java
public interface UserDao {
    // 根据用户名或密码查询用户
    @DynamicQuery(type = SqlProvider.class, method = "query")
    List<User> query(String username, String password);

    class SqlProvider {
        public String query(String username, String password) {
            return new SqlBuilder(){
                {
                    select("*");
                    from("user");
                    if (username != null) {
                        where("u_username = #{username}");
                    }
                    if (password != null) {
                        where("u_password = #{password}");
                    }
                }
            }.build();
        }
    }
}
```

注：

1. `DynamicQuery`的`type`指定的类必须要有默认构造函数
2. 如果`DynamicQuery`不指定`method`，则默认使用被标注方法的方法名
3. 可以使用`SqlBuilder`来拼接SQL字符串，用法与MyBatis的`SQL`类似
