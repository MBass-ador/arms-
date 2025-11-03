package com.basssoft.arms.account.controller;

import com.basssoft.arms.account.domain.AccountDTO;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles Account models (links)
 * for HATEOAS responses

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Component
public class AccountModelAssembler implements RepresentationModelAssembler<AccountDTO, EntityModel<AccountDTO>> {

    /**
     * Convert AccountDTO to EntityModel<AccountDTO> with links
     *
     * @param account AccountDTO
     * @return EntityModel<AccountDTO>
     */
    @Override
    public EntityModel<AccountDTO> toModel(AccountDTO account) {

        return EntityModel.of(account,
                linkTo(methodOn(AccountController.class).getAccount(account.getAccountId())).withSelfRel(),
                linkTo(methodOn(AccountController.class).getAccounts()).withRel("accounts")
        );
    }

}
