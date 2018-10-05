package ch.uzh.marugoto.backend.request;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
@Constraint(validatedBy = PasswordValidator.class)

public @interface Password {
    String message() default "Please check your password. It must contain at least 8 digits with 1 capital letter,and 1 digit";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}