package com.todev.pdv.common.constraints.impl;

import com.todev.pdv.common.constraints.contracts.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleValidator implements ConstraintValidator<Role, String> {

    @Override
    public void initialize(Role constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String role, ConstraintValidatorContext context) {
        if (role == null) return false;

        return switch (role) {
            case "ADMIN", "MANAGER", "SELLER" -> true;
            default -> false;
        };
    }
}
