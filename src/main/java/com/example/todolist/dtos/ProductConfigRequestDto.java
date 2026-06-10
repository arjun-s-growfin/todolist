package com.example.todolist.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class ProductConfigRequestDto {

    // @NotBlank = not null AND not empty AND not just whitespace
    // @Size     = length bounds (works on String, Collection, array)
    // @NotBlank(message = "productName must not be blank")
    @Size(min = 3, max = 50, message = "productName must be between 3 and 50 characters")
    private String productName;

    // @Size with only max — lower bound defaults to 0
    @Size(max = 200, message = "description must not exceed 200 characters")
    private String description;

    // @Min / @Max — for integer types (int, long, Integer, Long, etc.)
    @Min(value = 1, message = "price must be at least 1 cent")
    @Max(value = 1_000_000, message = "price must not exceed 1,000,000 cents")
    private int priceInCents;

    // @PositiveOrZero is a shorthand for @Min(0) on numeric types
    @PositiveOrZero(message = "discountPercent must be 0 or greater")
    @Max(value = 100, message = "discountPercent must not exceed 100")
    private int discountPercent;

    // @DecimalMin / @DecimalMax — for BigDecimal / BigInteger / String
    // inclusive = false means the boundary value itself is NOT allowed
    @DecimalMin(value = "0.0", inclusive = false, message = "rating must be greater than 0.0")
    @DecimalMax(value = "5.0", message = "rating must not exceed 5.0")
    private BigDecimal rating;

    // @Email validates the format, but NOT whether the domain actually exists
    @NotBlank
    @Email(message = "supportEmail must be a valid email address")
    private String supportEmail;

    // @Pattern accepts a Java regex
    @Pattern(regexp = "^[A-Z]{2,5}$", message = "currencyCode must be 2-5 uppercase letters (e.g. USD, EUR)")
    private String currencyCode;

    // @Positive = strictly > 0 (no need for a separate @Min(1))
    @Positive(message = "warrantyDays must be a positive number")
    @Max(value = 365, message = "warrantyDays must not exceed 365")
    private int warrantyDays;

    // @NotNull on a nested object, PLUS @Valid to trigger cascaded validation
    // Without @Valid the fields inside AddressDto are NOT checked
    // @NotNull(message = "shippingAddress must not be null")
    @Valid
    private AddressDto shippingAddress;

    @Getter
    @Setter
    public static class AddressDto {

        @NotBlank(message = "street must not be blank")
        @Size(min = 5, max = 100, message = "street must be between 5 and 100 characters")
        private String street;

        @NotBlank(message = "city must not be blank")
        @Size(min = 2, max = 50)
        private String city;

        // @Size with min == max enforces an exact length
        @NotBlank
        @Size(min = 2, max = 2, message = "countryCode must be exactly 2 characters (ISO 3166-1 alpha-2)")
        private String countryCode;

        // @Pattern to enforce numeric-only postal codes of 5 or 6 digits
        @NotBlank
        @Pattern(regexp = "^[0-9]{5,6}$", message = "pincode must be 5 or 6 digits")
        private String pincode;
    }
}
