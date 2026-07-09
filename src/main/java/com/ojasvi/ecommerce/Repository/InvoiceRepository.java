package com.ojasvi.ecommerce.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.Invoice;
import com.ojasvi.ecommerce.Entity.Order;

@Repository
public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByOrder(Order order);

    Optional<Invoice> findByInvoiceNumber(
            String invoiceNumber);
}
