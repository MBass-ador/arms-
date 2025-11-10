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
     * Create new Invoice

     * @param invoice InvoiceDTO
     * @return InvoiceDTO
     */
    @Override
    public InvoiceDTO createInvoice(InvoiceDTO invoice) {

        // null check
        if (invoice == null) return null;

        // convert dto to entity
        Invoice entity = _dtoToEntity(invoice);

        // persist
        Invoice saved = invoiceRepo.save(entity);

        // convert back to dto and return
        return _entityToDto(saved);
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
    public List<InvoiceDTO> getAllInvoices() {

        return invoiceRepo.findAll().stream()
                .map(this::_entityToDto)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }



    /**
     * Update existing Invoice

     * @param account InvoiceDTO
     * @return InvoiceDTO
     */
    @Override
    public InvoiceDTO updateInvoice(InvoiceDTO account) {

        // null check
        if (account == null || account.getInvoiceId() == null) return null;

        // find existing by id
        Optional<Invoice> opt = invoiceRepo.findById(account.getInvoiceId());
        if (!opt.isPresent()) return null;

        // get existing invoice
        Invoice existing = opt.get();

        // copy simple properties but skip id and account references
        BeanUtils.copyProperties(account, existing, "invoiceId", "provider", "customer");

        // update provider/customer refs (set null when no id)
        if (account.getProviderId() != null) {
            existing.setProvider(accountRepo.getReferenceById(account.getProviderId()));
        } else {
            existing.setProvider(null);
        }
        if (account.getCustomerId() != null) {
            existing.setCustomer(accountRepo.getReferenceById(account.getCustomerId()));
        } else {
            existing.setCustomer(null);
        }
        // persist updates
        Invoice saved = invoiceRepo.save(existing);

        // convert back to dto and return
        return _entityToDto(saved);
    }


    /**
     * Delete Invoice by ID

     * @param invoiceId int
     * @return int deletedId
     */
    @Override
    public int deleteInvoice(int invoiceId) {

        // validate id
        if (invoiceId <= 0) return -1;
        if (!invoiceRepo.existsById(invoiceId)) return -1; // not found

        // delete from repo
        invoiceRepo.deleteById(invoiceId);

        // return deleted id
        return invoiceId;
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
        BeanUtils.copyProperties(entity, dto, "provider", "customer");

        // expose provider/customer ids on DTO if present
        if (entity.getProvider() != null) {
            dto.setProviderId(entity.getProvider().getAccountId());
        }
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getAccountId());
        }
        return dto;
    }







}
