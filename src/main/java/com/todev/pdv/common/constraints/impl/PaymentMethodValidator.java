package com.todev.pdv.common.constraints.impl;

import com.todev.pdv.common.constraints.contracts.PaymentMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentMethodValidator implements ConstraintValidator<PaymentMethod, String> {

    @Override
    public void initialize(PaymentMethod constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String paymentMethod, ConstraintValidatorContext context) {
        if (paymentMethod == null) return false;

        return switch (paymentMethod) {
            case "CARD", "CASH", "PIX" -> true;
            default -> false;
        };
    }
}
