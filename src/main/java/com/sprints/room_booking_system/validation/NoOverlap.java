package com.sprints.room_booking_system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoOverlapValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoOverlap {
    
    String message() default "The requested time slot overlaps with an existing booking";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
