package com.flyxia.flytalk.dao;

import com.flyxia.flytalk.entity.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author automannn@163.com
 * @time 2019/5/3 15:33
 */
public interface UserSecurityDao extends JpaRepository<UserSecurity,String> {
}
