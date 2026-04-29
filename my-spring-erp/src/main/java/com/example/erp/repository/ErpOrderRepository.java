package com.example.erp.repository;

import com.example.erp.model.ErpOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErpOrderRepository extends JpaRepository<ErpOrder, Long> {
}
