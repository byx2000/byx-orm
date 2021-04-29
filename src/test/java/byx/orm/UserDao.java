package byx.orm;

import byx.orm.annotation.Param;
import byx.orm.annotation.Query;
import byx.orm.annotation.Update;

import java.util.List;

public interface UserDao {
    @Query("SELECT * FROM t_user WHERE u_id = #{id}")
    User getById(@Param("id") Integer id);

    @Query("SELECT * FROM t_user WHERE u_username = #{username} AND u_password = #{password}")
    User getByUsernameAndPassword(@Param("username") String username,
                                  @Param("password") String password);

    @Query("SELECT * FROM t_user")
    List<User> list();

    @Query("SELECT * FROM t_user WHERE level >= #{level}")
    List<User> listByLevel(@Param("level") Integer level);

    @Query("SELECT COUNT(*) FROM t_user")
    Integer count();

    @Query("SELECT COUNT(*) FROM t_user WHERE level = #{level}")
    Integer countByLevel(@Param("level") Integer level);

    @Update("INSERT INTO t_user(u_username, u_password, level) VALUES(#{username}, #{password}, #{level})")
    int insert(@Param("username") String username,
               @Param("password") String password,
               @Param("level") Integer level);

    @Update("DELETE FROM t_user WHERE u_username = #{username}")
    void deleteByUsername(@Param("username") String username);
}
