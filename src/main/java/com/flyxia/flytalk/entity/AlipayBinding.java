package com.flyxia.flytalk.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

/**
 * @author automannn@163.com
 * @time 2019/5/27 16:05
 */
@Entity
@Table(name="tb_alipay_binding")
public class AlipayBinding {

    @Id
    @GenericGenerator(name = "uuid",strategy = "uuid")
    @GeneratedValue( generator = "uuid")
    private String id;

    @ManyToOne
    private User user;

    @NotBlank
    private String realName;

    //卡号
    @NotBlank
    private String account;

    public AlipayBinding() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
