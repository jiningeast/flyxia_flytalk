package com.flyxia.flytalk.entity;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

/**
 * @author automannn@163.com
 * @time 2019/5/3 10:09
 */
@Entity
@Table(name = "tb_bill_state_type")
public class BillStateType {

    @Id
    private String code;

    @NotBlank
    private String name;

    public BillStateType() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
