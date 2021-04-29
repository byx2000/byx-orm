package byx.orm;

import byx.orm.annotation.Equal;
import byx.orm.annotation.GreaterThan;
import byx.orm.annotation.Query;

public class UserQueryObject {
    @Equal("u_id")
    private Integer id;

    @Equal("u_username")
    private String username;

    @Equal("u_password")
    private String password;

    @GreaterThan
    private Integer level;

    @Query("username LIKE ? OR password LIKE ?")
    private String keyword;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
