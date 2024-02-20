package com.todev.pdv.common.constraints.impl;

import com.todev.pdv.common.constraints.contracts.ProductAmount;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductAmountValidator implements ConstraintValidator<ProductAmount, Integer> {

    @Override
    public void initialize(ProductAmount constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer productAmount, ConstraintValidatorContext context) {
        return productAmount != null && productAmount >= 0;
    }
}
