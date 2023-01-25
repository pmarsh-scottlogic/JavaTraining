package com.example.demo.matcher.repo;

import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderObj;
import com.example.demo.security.userInfo.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepo extends JpaRepository<OrderObj, Long> {

    public final static String GET_ORDERS_BY_USERNAME_AND_ACTION =
            "SELECT Order_ID, ACTION, DATETIME, PRICE, QUANTITY, USER_ID " +
                    "FROM ORDER_OBJ, APP_USER " +
                    "WHERE ORDER_OBJ.USER_ID = APP_USER.ID " +
                    "AND APP_USER.USERNAME = :username " +
                    "AND ACTION + :actionOrdinal";


    @Query(value = GET_ORDERS_BY_USERNAME_AND_ACTION, nativeQuery=true)
    List<OrderObj> findByUsernameAndAction(
            @Param("username") final String username,
            @Param("actionOrdinal") final int actionOrdinal);

    List<OrderObj> findByAction(OrderAction action);
}
