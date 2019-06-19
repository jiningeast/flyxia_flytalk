package com.flyxia.flytalk.dao;

import com.flyxia.flytalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author automannn@163.com
 * @time 2019/4/25 13:31
 */
public interface UserDao extends JpaRepository<User,String>{
}
