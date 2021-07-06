package com.fasterxml.jackson.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

























































@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonCreator
{
  Mode mode() default Mode.DEFAULT;
  
  public static enum Mode
  {
    DEFAULT, 
    





    DELEGATING, 
    








    PROPERTIES, 
    





    DISABLED;
    
    private Mode() {}
  }
}
