package com.todev.pdv.common.constraints.contracts;


import com.todev.pdv.common.constraints.impl.PaymentMethodValidator;
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
@Constraint(validatedBy = PaymentMethodValidator.class)
public @interface PaymentMethod {

    String message() default "O método de pagamento é inválido!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
