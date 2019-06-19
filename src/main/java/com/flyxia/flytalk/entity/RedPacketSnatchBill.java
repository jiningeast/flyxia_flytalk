package com.flyxia.flytalk.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;

/**
 * @author automannn@163.com
 * @time 2019/5/2 17:31
 * 抢红包账单记录
 */
@Entity
@Table(name = "tb_redpacket_snatch_bill")
public class RedPacketSnatchBill {
    @Id
    @GenericGenerator(name = "uuid",strategy = "uuid")
    @GeneratedValue( generator = "uuid")
    private String id;

    @NotBlank
    private String token;

    @Column(name = "balance",columnDefinition = "double(10,2) default '0.00'")
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createTime;

    public RedPacketSnatchBill() {
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


}
