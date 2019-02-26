package ch.uzh.marugoto.core.data.validation;

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
@Constraint(validatedBy = UserNotExistValidator.class)

public @interface UserNotExist {
    String message() default "There is already a user registered with the email provided.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}