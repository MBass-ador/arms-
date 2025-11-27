package com.basssoft.arms.invoice.service;

import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.booking.repository.BookingRepository;
import com.basssoft.arms.invoice.domain.Invoice;
import com.basssoft.arms.invoice.domain.InvoiceDTO;
import com.basssoft.arms.invoice.repository.InvoiceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation
 * for Invoice CRUD operations

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Service
@Transactional
public class InvoiceSvcImpl implements IinvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final AccountRepository accountRepo;
    private final BookingRepository bookingRepo;


    /** Constructor injects dependencies
     * @param invoiceRepo InvoiceRepository
     * @param accountRepo AccountRepository
     * @param bookingRepo BookingRepository
     */
    public InvoiceSvcImpl(InvoiceRepository invoiceRepo,
                          AccountRepository accountRepo,
                          BookingRepository bookingRepo) {
        this.invoiceRepo = invoiceRepo;
        this.accountRepo = accountRepo;
        this.bookingRepo = bookingRepo;
    }


    /**
     * Get Invoice by ID

     * @param invoiceId int
     * @return InvoiceDTO
     */
    @Override
    public InvoiceDTO getInvoice(int invoiceId) {

        // find by id
        Optional<Invoice> opt = invoiceRepo.findById(invoiceId);

        // convert to dto and return or null if not found
        if (opt.isPresent()) {
            return _entityToDto(opt.get());
        } else {
            return null;
        }
    }


    /**
     * Get all Invoices

     * @return List<InvoiceDTO> invoices
     */
    @Override
    public List<InvoiceDTO> getCustomerInvoices(int customerId) {

        return invoiceRepo.findByCustomer_AccountId(customerId).stream()
                .map(this::_entityToDto)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }










    /** helper
     *  converts InvoiceDTO to Invoice entity
     *
     * @param dto InvoiceDTO
     * @return Invoice entity
     */
    Invoice _dtoToEntity(InvoiceDTO dto) {
        // null check
        if (dto == null) return null;

        // new Invoice
        Invoice entity = new Invoice();
        // preserve id when present (for update paths)
        if (dto.getInvoiceId() != null) {
            entity.setInvoiceId(dto.getInvoiceId());
        }
        // copy simple properties but skip account references and id
        BeanUtils.copyProperties(dto, entity,"invoiceId", "provider", "customer");

        // map provider/customer by id (create lightweight reference)
        if (dto.getProviderId() != null) {
            entity.setProvider(accountRepo.getReferenceById(dto.getProviderId()));
        }
        if (dto.getCustomerId() != null) {
            entity.setCustomer(accountRepo.getReferenceById(dto.getCustomerId()));
        }
        return entity;
    }


    /** helper
     *  converts Invoice entity to InvoiceDTO
     *
     * @param entity Invoice entity
     * @return InvoiceDTO
     */
    InvoiceDTO _entityToDto(Invoice entity) {

        // null check
        if (entity == null) return null;

        // new dto
        InvoiceDTO dto = new InvoiceDTO();

        // copy simple properties but skip account objects
        org.springframework.beans.BeanUtils.copyProperties(entity, dto, "provider", "customer", "bookings");

        // expose provider/customer ids on DTO if present
        if (entity.getProvider() != null) {
            dto.setProviderId(entity.getProvider().getAccountId());
        }
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getAccountId());
        }
        // map bookings to bookingIds
        if (entity.getBookings() != null) {
            dto.setBookingIds(
                    entity.getBookings().stream()
                            .map(booking -> booking.getBookingId())
                            .collect(java.util.stream.Collectors.toList())
            );
        }
        return dto;
    }







}