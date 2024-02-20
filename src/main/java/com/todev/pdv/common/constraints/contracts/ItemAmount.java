package com.todev.pdv.common.constraints.contracts;

import com.todev.pdv.common.constraints.impl.ItemAmountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = ItemAmountValidator.class)
public @interface ItemAmount {

    String message() default "A quantidade do item deve ser maior ou igual a um!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
