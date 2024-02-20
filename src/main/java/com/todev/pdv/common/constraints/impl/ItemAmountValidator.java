package com.todev.pdv.common.constraints.impl;

import com.todev.pdv.common.constraints.contracts.ItemAmount;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ItemAmountValidator implements ConstraintValidator<ItemAmount, Integer> {

    @Override
    public void initialize(ItemAmount constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer amount, ConstraintValidatorContext context) {
        return amount != null && amount >= 1;
    }
}
