package com.example.dibage.accountb.entitys;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dibage on 2018/3/27.
 * 账户信息实体类
 */

//注解实体类Account
@Entity
public class Account {
    //ID主键自增
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String description;//名称
    @NotNull
    private String  username;//用户名
    @NotNull
    private String password;//密码
    private String remark;//描述

    private String firstchar;//账号名称的首字母

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    @Generated(hash = 1413604925)
    public Account(Long id, @NotNull String description, @NotNull String username,
            @NotNull String password, String remark, String firstchar) {
        this.id = id;
        this.description = description;
        this.username = username;
        this.password = password;
        this.remark = remark;
        this.firstchar = firstchar;
    }

    public Account( @NotNull String description, @NotNull String username,
                   @NotNull String password, String remark) {
        this.description = description;
        this.username = username;
        this.password = password;
        this.remark = remark;
    }

    public Account( @NotNull String description, @NotNull String username,
                    @NotNull String password, String remark,@NotNull String firstchar) {
        this.description = description;
        this.username = username;
        this.password = password;
        this.remark = remark;
        this.firstchar = firstchar;
    }

    @Generated(hash = 882125521)
    public Account() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFirstchar() {
        return this.firstchar;
    }

    public void setFirstchar(String firstchar) {
        this.firstchar = firstchar;
    }

}
