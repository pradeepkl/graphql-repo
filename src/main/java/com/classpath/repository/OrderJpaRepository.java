package com.classpath.repository;

import com.classpath.dto.CustomerOrderSummary;
import com.classpath.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerName(String name);

    List<Order> findByCreatedAtBetween(LocalDate start, LocalDate end);

    List<Order> findByCustomerNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    @Query("""
        SELECT new com.classpath.dto.CustomerOrderSummary(o.customerName, COUNT(o))
        FROM Order o
        GROUP BY o.customerName
        ORDER BY COUNT(o) DESC
        """)
    List<CustomerOrderSummary> findTopCustomers();

    List<Order> findByIdIn(List<Long> ids);

    @Query("""
        SELECT o
        FROM Order o
        WHERE LOWER(o.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(o.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<Order> searchOrders(@Param("keyword") String keyword);

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        WHERE o.id IN (
            SELECT li.orderId
            FROM LineItem li
            JOIN Product p ON li.productId = p.id
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
        )
    """)
    List<Order> findOrdersByProductName(@Param("name") String name);
}

