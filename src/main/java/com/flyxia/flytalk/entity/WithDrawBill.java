package com.flyxia.flytalk.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @author automannn@163.com
 * @time 2019/5/3 9:43
 * 提现记录
 */
@Entity
@Table(name = "tb_withdraw_bill")
public class WithDrawBill {

    @Id
    @GenericGenerator(name = "uuid",strategy = "uuid")
    @GeneratedValue( generator = "uuid")
    private String id;

    @Column(name = "amount",columnDefinition = "double(10,2) default '0.00'")
    private double amount;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createTime;

    @OneToOne
    private FinancialInstitutionType target;

    private String state;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;


    @ManyToOne
    private  User user;

    public WithDrawBill() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public FinancialInstitutionType getTarget() {
        return target;
    }

    public void setTarget(FinancialInstitutionType target) {
        this.target = target;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
