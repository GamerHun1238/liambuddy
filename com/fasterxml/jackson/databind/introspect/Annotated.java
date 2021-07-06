package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;













public abstract class Annotated
{
  protected Annotated() {}
  
  public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
  
  public abstract boolean hasAnnotation(Class<?> paramClass);
  
  public abstract boolean hasOneOf(Class<? extends Annotation>[] paramArrayOfClass);
  
  public abstract AnnotatedElement getAnnotated();
  
  protected abstract int getModifiers();
  
  public boolean isPublic()
  {
    return Modifier.isPublic(getModifiers());
  }
  



  public abstract String getName();
  



  public abstract JavaType getType();
  


  @Deprecated
  public final JavaType getType(TypeBindings bogus)
  {
    return getType();
  }
  








  @Deprecated
  public Type getGenericType()
  {
    return getRawType();
  }
  
  public abstract Class<?> getRawType();
  
  @Deprecated
  public abstract Iterable<Annotation> annotations();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}
