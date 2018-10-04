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
@Constraint(validatedBy = UserExistValidator.class)

public @interface UserExist {
    String message() default "There is already a user registered with the email provided";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}