package com.classpath.repository;

import com.classpath.model.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem, Long> {
    List<LineItem> findByOrderId(Long orderId);
    List<LineItem> findByProductId(Long productId);

    @Query("""
        SELECT p.name, o.customerName, COUNT(li)
        FROM LineItem li
        JOIN Product p ON li.productId = p.id
        JOIN Order o ON li.orderId = o.id
        GROUP BY p.name, o.customerName
        """)
    List<Object[]> getProductCustomerStats();
}
