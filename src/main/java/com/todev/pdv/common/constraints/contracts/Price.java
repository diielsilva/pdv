package com.todev.pdv.common.constraints.contracts;

import com.todev.pdv.common.constraints.impl.PriceValidator;
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
@Constraint(validatedBy = PriceValidator.class)
public @interface Price {

    String message() default "O preço deve ser maior ou igual a zero!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
