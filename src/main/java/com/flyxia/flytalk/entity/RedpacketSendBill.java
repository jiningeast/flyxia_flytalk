package com.flyxia.flytalk.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;

/**
 * @author automannn@163.com
 * @time 2019/5/2 17:18
 * 发红包账单记录
 */
@Entity
@Table(name = "tb_redpacket_send_bill")
public class RedpacketSendBill {
    @Id
    @GenericGenerator(name = "uuid",strategy = "uuid")
    @GeneratedValue( generator = "uuid")
    private String id;

    @NotBlank
    private String token;

    @Column(name = "balance",columnDefinition = "double(10,2) default '0.00'")
    private Double amount;

    @ManyToOne
    private User user;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createTime;

    private Integer count;

    private Integer personNumber;

    private Long expireTime;

    private String state;

    public RedpacketSendBill() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(Integer personNumber) {
        this.personNumber = personNumber;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
