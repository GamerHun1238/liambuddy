package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.util.ClassUtil;







public abstract class Java7Support
{
  private static final Java7Support IMPL;
  
  static
  {
    Java7Support impl = null;
    try {
      Class<?> cls = Class.forName("com.fasterxml.jackson.databind.ext.Java7SupportImpl");
      impl = (Java7Support)ClassUtil.createInstance(cls, false);
    }
    catch (Throwable localThrowable) {}
    


    IMPL = impl; }
  public abstract PropertyName findConstructorName(AnnotatedParameter paramAnnotatedParameter);
  public abstract Boolean hasCreatorAnnotation(Annotated paramAnnotated);
  public abstract Boolean findTransient(Annotated paramAnnotated);
  public static Java7Support instance() { return IMPL; }
  
  public Java7Support() {}
}
