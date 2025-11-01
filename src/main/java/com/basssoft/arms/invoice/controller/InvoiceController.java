package com.basssoft.arms.invoice.controller;

import com.basssoft.arms.invoice.domain.InvoiceDTO;
import com.basssoft.arms.invoice.service.IinvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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


    /**
     * Create new Invoice
     *
     * @param invoiceDTO InvoiceDTO
     * @return ResponseEntity with created InvoiceDTO or error message
     */
    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {

        try {
            // Call service to create invoice
            InvoiceDTO createdInvoice = invoiceService.createInvoice(invoiceDTO);

            // return status: 201 and DTO of created invoice
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);

        } catch (IllegalArgumentException iae) {
            // Handle bad input: 400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // Handle unexpected errors: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }




    /**
     * Update existing Invoice
     *
     * @param id         Invoice ID
     * @param invoiceDTO InvoiceDTO with updated data
     * @return ResponseEntity with updated InvoiceDTO or error message
     */
    @PostMapping("/{id}")
    public ResponseEntity<?> updateInvoice(@PathVariable int id, @RequestBody InvoiceDTO invoiceDTO) {

        try {
            // id from path
            invoiceDTO.setInvoiceId(id);

            // call service
            InvoiceDTO updatedInvoice = invoiceService.updateInvoice(invoiceDTO);

            if (updatedInvoice == null) {
                // Invoice not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found.");
            }
            // return updated invoice / 200
            return ResponseEntity.ok(updatedInvoice);

        } catch (IllegalArgumentException iae) {
            // handle bad request / 400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // handle other errors / 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }







}
