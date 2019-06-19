package com.flyxia.flytalk.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;

/**
 * @author automannn@163.com
 * @time 2019/5/2 17:52
 * 充值记录
 */
@Entity
@Table(name = "tb_recharge_bill")
public class RechargeBill {
    @Id
    @GenericGenerator(name = "uuid",strategy = "uuid")
    @GeneratedValue( generator = "uuid")
    private String id;

    @Column(name = "amount",columnDefinition = "double(10,2) default '0.00'")
    private Double amount;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createTime;

    @OneToOne
    private FinancialInstitutionType target;

    private String state;


    private String outTradeNumber;


    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ManyToOne
    private User user;

    private String productCode;

    public RechargeBill() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOutTradeNumber() {
        return outTradeNumber;
    }

    public void setOutTradeNumber(String outTradeNumber) {
        this.outTradeNumber = outTradeNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
