package com.example.todolist.controllers;

import com.example.todolist.dtos.ProductConfigRequestDto;
import com.example.todolist.dtos.ProductConfigResultDto;
import com.example.todolist.dtos.UpdateConfigResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts/{account-id}/products")
public class ProductController {

    // ── Approach 1: manual BindingResult check ───────────────────────────────
    //
    // When you add BindingResult as a parameter RIGHT AFTER @Valid @RequestBody,
    // Spring does NOT throw an exception on validation failure.
    // Instead it populates the BindingResult and lets your code decide what to do.
    //
    // Use this when you want custom error shaping or conditional logic on errors.
    @PostMapping
    public ResponseEntity<?> createProduct(
            @PathVariable("account-id") long accountId,
            @Valid @RequestBody ProductConfigRequestDto dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> bindingErrors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .filter(StringUtils::hasLength)
                    .collect(Collectors.toList());
            UpdateConfigResult result = new UpdateConfigResult(false, bindingErrors, null);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        // Happy path — pretend we saved it and return a result
        long fakeProductId = System.currentTimeMillis();
        return ResponseEntity.ok(new ProductConfigResultDto(accountId, fakeProductId, "Product created"));
    }

    // ── Approach 2: let the exception propagate ───────────────────────────────
    //
    // When BindingResult is absent, Spring throws MethodArgumentNotValidException
    // on the first validation failure.  GlobalControllerAdvice (or Spring's own
    // DefaultHandlerExceptionResolver) catches it.
    //
    // This is the cleaner pattern for most endpoints — less boilerplate here,
    // one central place handles all validation errors.
    @PutMapping("/{product-id}")
    public ResponseEntity<ProductConfigResultDto> updateProduct(
            @PathVariable("account-id") long accountId,
            @PathVariable("product-id") long productId,
            @Valid @RequestBody ProductConfigRequestDto dto) {

        return ResponseEntity.ok(new ProductConfigResultDto(accountId, productId, "Product updated"));
    }
}