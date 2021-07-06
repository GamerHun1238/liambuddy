package com.fasterxml.jackson.databind.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;




























































@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonPOJOBuilder
{
  public static final String DEFAULT_BUILD_METHOD = "build";
  public static final String DEFAULT_WITH_PREFIX = "with";
  
  String buildMethodName() default "build";
  
  String withPrefix() default "with";
  
  public static class Value
  {
    public final String buildMethodName;
    public final String withPrefix;
    
    public Value(JsonPOJOBuilder ann)
    {
      this(ann.buildMethodName(), ann.withPrefix());
    }
    
    public Value(String buildMethodName, String withPrefix)
    {
      this.buildMethodName = buildMethodName;
      this.withPrefix = withPrefix;
    }
  }
}
