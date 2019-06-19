package com.flyxia.flytalk.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * @author automannn@163.com
 * @time 2019/5/3 15:30
 */
@Entity
@Table(name = "tb_user_security")
public class UserSecurity {
    @Id
    private String id;

    @Lob
    private String privacyKey;

    @Lob
    private String publicKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivacyKey() {
        return privacyKey;
    }

    public void setPrivacyKey(String privacyKey) {
        this.privacyKey = privacyKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
