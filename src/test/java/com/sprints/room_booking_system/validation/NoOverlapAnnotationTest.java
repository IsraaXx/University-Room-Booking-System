package com.sprints.room_booking_system.validation;

import com.sprints.room_booking_system.dto.BookingDto;
import jakarta.validation.Constraint;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

class NoOverlapAnnotationTest {
    
    @Test
    void testNoOverlapAnnotationExists() {
        // Given
        Class<BookingDto> dtoClass = BookingDto.class;
        
        // When
        Annotation[] annotations = dtoClass.getAnnotations();
        
        // Then
        boolean hasNoOverlapAnnotation = false;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == NoOverlap.class) {
                hasNoOverlapAnnotation = true;
                break;
            }
        }
        assertThat(hasNoOverlapAnnotation).isTrue();
    }
    
    @Test
    void testNoOverlapAnnotationMessage() {
        // Given
        Class<BookingDto> dtoClass = BookingDto.class;
        
        // When
        NoOverlap noOverlapAnnotation = dtoClass.getAnnotation(NoOverlap.class);
        
        // Then
        assertThat(noOverlapAnnotation).isNotNull();
        assertThat(noOverlapAnnotation.message()).isEqualTo("The requested time slot overlaps with an existing booking");
    }
    
    @Test
    void testNoOverlapAnnotationTarget() {
        // Given
        Class<NoOverlap> annotationClass = NoOverlap.class;
        
        // When
        Target target = annotationClass.getAnnotation(Target.class);
        
        // Then
        assertThat(target).isNotNull();
        assertThat(target.value()).contains(ElementType.TYPE);
    }
    
    @Test
    void testNoOverlapAnnotationRetention() {
        // Given
        Class<NoOverlap> annotationClass = NoOverlap.class;
        
        // When
        Retention retention = annotationClass.getAnnotation(Retention.class);
        
        // Then
        assertThat(retention).isNotNull();
        assertThat(retention.value()).isEqualTo(RetentionPolicy.RUNTIME);
    }
    
    @Test
    void testNoOverlapAnnotationConstraint() {
        // Given
        Class<NoOverlap> annotationClass = NoOverlap.class;
        
        // When
        Constraint constraint = annotationClass.getAnnotation(Constraint.class);
        
        // Then
        assertThat(constraint).isNotNull();
        assertThat(constraint.validatedBy()).contains(NoOverlapValidator.class);
    }
    
    @Test
    void testNoOverlapAnnotationDocumented() {
        // Given
        Class<NoOverlap> annotationClass = NoOverlap.class;
        
        // When
        Documented documented = annotationClass.getAnnotation(Documented.class);
        
        // Then
        assertThat(documented).isNotNull();
    }
}
