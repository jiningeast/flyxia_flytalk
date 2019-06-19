package com.flyxia.flytalk.dao;

import com.flyxia.flytalk.entity.WithDrawBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author automannn@163.com
 * @time 2019/5/3 10:23
 */
public interface WithdrawBillDao extends JpaRepository<WithDrawBill,String> {

    @Query("select sum(w.amount) as sumpsjg from WithDrawBill w where w.user.id = ?1 and w.state='completed'")
    Double getTotalFee(String userID);
}
