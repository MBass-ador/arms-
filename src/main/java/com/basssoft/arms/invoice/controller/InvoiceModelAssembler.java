package com.basssoft.arms.invoice.controller;

import com.basssoft.arms.invoice.domain.InvoiceDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles Invoice models (links)
 * for HATEOAS responses
 *
 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Component
public class InvoiceModelAssembler
        implements RepresentationModelAssembler<InvoiceDTO, EntityModel<InvoiceDTO>>  {

    @Override
    public EntityModel<InvoiceDTO> toModel(InvoiceDTO invoice) {

        return EntityModel.of(invoice,
                linkTo(methodOn(InvoiceController.class).getInvoice(invoice.getInvoiceId())).withSelfRel(),
                linkTo(methodOn(InvoiceController.class).getInvoices()).withRel("invoices")
        );
    }
}
