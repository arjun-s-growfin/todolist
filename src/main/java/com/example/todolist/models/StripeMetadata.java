package com.example.todolist.models;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class StripeMetadata extends MetaData {

    public StripeMetadata(String entityType) {
        super(entityType);
    }

    @Getter
    public enum StripeEntityType {
        CUSTOMER("stripe_customer");

        public final String entityType;

        StripeEntityType(String entityType) {
            this.entityType = entityType;
        }
    }
}
