package com.flyxia.flytalk.entity;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

/**
 * @author automannn@163.com
 * @time 2019/5/2 17:45
 * 金融机构类型
 */
@Entity
@Table(name = "tb_financial_institution_type")
public class FinancialInstitutionType {
    @Id
    private String code;

    @NotBlank
    private String name;

    public FinancialInstitutionType() {
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
