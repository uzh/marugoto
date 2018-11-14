package ch.uzh.marugoto.backend.validation;

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
@Constraint(validatedBy = EmailValidator.class)
public @interface EmailNotValid {
	
	String message() default "Email address is not in a valid format.";
	Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
