package byx.orm;

import byx.orm.annotation.Query;
import byx.orm.annotation.Update;

import java.util.List;

public interface UserDao {
    @Query("SELECT * FROM t_user WHERE u_id = #{id}")
    User getById(Integer id);

    @Query("SELECT * FROM t_user WHERE u_username = #{username} AND u_password = #{password}")
    User getByUsernameAndPassword(String username, String password);

    @Query("SELECT * FROM t_user")
    List<User> listAll();

    @Query("SELECT * FROM t_user WHERE level >= #{level}")
    List<User> listByLevel(Integer level);

    @Query("SELECT COUNT(*) FROM t_user")
    Integer count();

    @Query("SELECT COUNT(*) FROM t_user WHERE level = #{level}")
    Integer countByLevel(Integer level);

    @Update("INSERT INTO t_user(u_username, u_password, level) VALUES(#{username}, #{password}, #{level})")
    int insert(String username, String password, Integer level);

    @Update("DELETE FROM t_user WHERE u_username = #{username}")
    void deleteByUsername(String username);
}
