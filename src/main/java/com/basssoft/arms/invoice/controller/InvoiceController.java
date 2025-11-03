package com.basssoft.arms.invoice.controller;

import com.basssoft.arms.invoice.domain.InvoiceDTO;
import com.basssoft.arms.invoice.service.IinvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Invoice Controller
 * for handling invoice-related http requests
 *
 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private IinvoiceService invoiceService;

    @Autowired
    private InvoiceModelAssembler assembler;


    /**
     * Create new Invoice
     *
     * @param invoiceDTO InvoiceDTO
     * @return ResponseEntity with created InvoiceDTO and or status
     */
    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {

        try {
            // call service to create invoice
            InvoiceDTO createdInvoice = invoiceService.createInvoice(invoiceDTO);

            // return status: 201 and DTO of created invoice
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(assembler.toModel(createdInvoice));

        } catch (IllegalArgumentException iae) {
            // handle bad input: 400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // handle unexpected errors: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    /**
     * Get Invoice by ID
     *
     * @param id Invoice ID
     * @return ResponseEntity with InvoiceDTO and or status
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoice(@PathVariable int id) {

        try {
            // call service / get invoice
            InvoiceDTO invoice = invoiceService.getInvoice(id);

            if (invoice == null) {
                // when not found status: 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found.");
            }

            // return invoice, wrapped in HATEOAS links / success: 200
            return ResponseEntity.ok(assembler.toModel(invoice));

        } catch (IllegalArgumentException iae) {
            // handle bad request: 400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // handle other errors: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred." + ex.getMessage());
        }
    }


    /**
     * Get all Invoices
     *
     * @return ResponseEntity with list of InvoiceDTOs and or status
     */
    @GetMapping
    public ResponseEntity<?> getInvoices() {

        try {
            // call service / get invoices
            List<InvoiceDTO> invoices = invoiceService.getAllInvoices();

            // wrap each invoice in HATEOAS links
            List<EntityModel<InvoiceDTO>> wrappedInvoices= invoices.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());

            // return list of wrapped invoices / success: 200
            return ResponseEntity.ok(CollectionModel.of(wrappedInvoices));

        } catch (IllegalArgumentException iae) {
            // handle bad request status: 400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // handle other errors status: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Invoice retrieval failed.");
        }
    }


    /**
     * Update existing Invoice
     *
     * @param id         int
     * @param invoiceDTO InvoiceDTO with updated data
     * @return ResponseEntity with updated InvoiceDTO and or status
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInvoice(@PathVariable int id, @RequestBody InvoiceDTO invoiceDTO) {

        try {
            // id from path
            invoiceDTO.setInvoiceId(id);

            // call service
            InvoiceDTO updatedInvoice = invoiceService.updateInvoice(invoiceDTO);

            if (updatedInvoice == null) {
                // Invoice not found / status: 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found.");
            }
            // return updated invoice wrapped in HATEOAS links / status: 200
            return ResponseEntity.ok(assembler.toModel(updatedInvoice));

        } catch (IllegalArgumentException iae) {
            // handle bad request / 400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // handle other errors / 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }



    /**
     * Delete Invoice by ID
     *
     * @param id int
     * @return ResponseEntity with success message and or status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable int id) {

        try {
            // call service / delete invoice
            int deletedId = invoiceService.deleteInvoice(id);

            if (deletedId == id) {
                // success status:200
                return ResponseEntity.ok("Invoice deleted successfully.");

            } else {
                // not found status:404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found.");
            }
        } catch (IllegalArgumentException iae) {
            // handle bad request status:400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // handle other errors status:500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Invoice deletion failed.");
        }
    }


}
