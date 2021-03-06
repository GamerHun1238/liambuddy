package com.fasterxml.jackson.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIdentityInfo
{
  String property() default "@id";
  
  Class<? extends ObjectIdGenerator<?>> generator();
  
  Class<? extends ObjectIdResolver> resolver() default SimpleObjectIdResolver.class;
  
  Class<?> scope() default Object.class;
}
