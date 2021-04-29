package byx.orm;

import byx.orm.annotation.Column;
import byx.orm.annotation.Table;

@Table("t_user")
public class User {
    @Column("u_id")
    private Integer id;

    @Column("u_username")
    private String username;

    @Column("u_password")
    private String password;

    private Integer level;

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

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + ", password='" + password + '\'' + ", level=" + level + '}';
    }
}
