package com.example.demo.matcher.repo;

import com.example.demo.matcher.models.OrderObj;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<OrderObj, Long> {

}
