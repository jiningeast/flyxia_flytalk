package com.flyxia.flytalk.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

/**
 * @author automannn@163.com
 * @time 2019/5/23 17:33
 */
@Entity
@Table
public class CardBinding {

    @Id
    @GenericGenerator(name = "uuid",strategy = "uuid")
    @GeneratedValue( generator = "uuid")
    private String id;

    @ManyToOne
    private User user;

    @NotBlank
    private String userName;

    //卡号
    @NotBlank
    private String IdCard;

    //银行名称
    private String bankName;

    @Column(unique = true)
    private String bankCardNumber;





    private String state;

    public CardBinding() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public void setIdCard(String idCard) {
        IdCard = idCard;
    }


    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }


    public void setState(String state) {
        this.state = state;
    }
}
