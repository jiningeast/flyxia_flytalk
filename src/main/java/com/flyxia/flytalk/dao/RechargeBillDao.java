package com.flyxia.flytalk.dao;

import com.flyxia.flytalk.entity.RechargeBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author automannn@163.com
 * @time 2019/5/3 10:21
 */
public interface RechargeBillDao extends JpaRepository<RechargeBill,String> {

    @Query("select sum(r.amount) as sumpsjg from RechargeBill r where r.user.id = ?1 and r.state='completed'")
    Double getTotalFee(String userID);
}
