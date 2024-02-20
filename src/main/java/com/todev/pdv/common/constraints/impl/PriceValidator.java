package com.todev.pdv.common.constraints.impl;

import com.todev.pdv.common.constraints.contracts.Price;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceValidator implements ConstraintValidator<Price, Double> {

    @Override
    public void initialize(Price constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Double price, ConstraintValidatorContext context) {
        return price != null && price >= 0.0;
    }
}
