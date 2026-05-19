package com.condolives.api.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {

    String message() default "A senha deve ter no mínimo 8 caracteres e conter letras maiúsculas, minúsculas, dígitos e caracteres especiais";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
