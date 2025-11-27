package com.basssoft.arms.invoice.repository;

import com.basssoft.arms.invoice.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for Invoice entity
 *
 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    List<Invoice> findByCustomer_AccountId(Integer accountId);
}
