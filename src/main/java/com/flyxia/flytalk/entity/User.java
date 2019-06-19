package com.flyxia.flytalk.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

/**
 * @author automannn@163.com
 * @time 2019/4/25 11:22
 */
@Entity
@Table(name = "tb_user")
public class User {
    @Id
    @GenericGenerator(name = "uuid",strategy = "uuid")
    @GeneratedValue( generator = "uuid")

    private String id;


    @Column(nullable = false,unique = true)
    private String phoneNumber;

    @Column(nullable = false,unique = true)

    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String avatar;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
