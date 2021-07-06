package com.fasterxml.jackson.databind.util;

import java.lang.annotation.Annotation;

public abstract interface Annotations
{
  public abstract <A extends Annotation> A get(Class<A> paramClass);
  
  public abstract boolean has(Class<?> paramClass);
  
  public abstract boolean hasOneOf(Class<? extends Annotation>[] paramArrayOfClass);
  
  public abstract int size();
}
