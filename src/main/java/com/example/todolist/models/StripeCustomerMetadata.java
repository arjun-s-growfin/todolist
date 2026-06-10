package com.example.todolist.models;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class StripeCustomerMetadata extends StripeMetadata {

    public StripeCustomerMetadata() {
        super(StripeEntityType.CUSTOMER.getEntityType());
    }
}
