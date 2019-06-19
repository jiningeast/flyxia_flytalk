package com.flyxia.flytalk.dao;

import com.flyxia.flytalk.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author automannn@163.com
 * @time 2019/5/19 13:45
 */
public interface ProductDao  extends JpaRepository<Product,String>{
}
