package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;















































@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JacksonInject
{
  String value() default "";
  
  OptBoolean useInput() default OptBoolean.DEFAULT;
  
  public static class Value
    implements JacksonAnnotationValue<JacksonInject>, Serializable
  {
    private static final long serialVersionUID = 1L;
    protected static final Value EMPTY = new Value(null, null);
    

    protected final Object _id;
    

    protected final Boolean _useInput;
    

    protected Value(Object id, Boolean useInput)
    {
      _id = id;
      _useInput = useInput;
    }
    
    public Class<JacksonInject> valueFor()
    {
      return JacksonInject.class;
    }
    





    public static Value empty()
    {
      return EMPTY;
    }
    
    public static Value construct(Object id, Boolean useInput) {
      if ("".equals(id)) {
        id = null;
      }
      if (_empty(id, useInput)) {
        return EMPTY;
      }
      return new Value(id, useInput);
    }
    
    public static Value from(JacksonInject src) {
      if (src == null) {
        return EMPTY;
      }
      return construct(src.value(), src.useInput().asBoolean());
    }
    
    public static Value forId(Object id) {
      return construct(id, null);
    }
    





    public Value withId(Object id)
    {
      if (id == null) {
        if (_id == null) {
          return this;
        }
      } else if (id.equals(_id)) {
        return this;
      }
      return new Value(id, _useInput);
    }
    
    public Value withUseInput(Boolean useInput) {
      if (useInput == null) {
        if (_useInput == null) {
          return this;
        }
      } else if (useInput.equals(_useInput)) {
        return this;
      }
      return new Value(_id, useInput);
    }
    






    public Object getId() { return _id; }
    public Boolean getUseInput() { return _useInput; }
    
    public boolean hasId() {
      return _id != null;
    }
    
    public boolean willUseInput(boolean defaultSetting) {
      return _useInput == null ? defaultSetting : _useInput.booleanValue();
    }
    






    public String toString()
    {
      return String.format("JacksonInject.Value(id=%s,useInput=%s)", new Object[] { _id, _useInput });
    }
    

    public int hashCode()
    {
      int h = 1;
      if (_id != null) {
        h += _id.hashCode();
      }
      if (_useInput != null) {
        h += _useInput.hashCode();
      }
      return h;
    }
    
    public boolean equals(Object o)
    {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass() == getClass()) {
        Value other = (Value)o;
        if (OptBoolean.equals(_useInput, _useInput)) {
          if (_id == null) {
            return _id == null;
          }
          return _id.equals(_id);
        }
      }
      return false;
    }
    





    private static boolean _empty(Object id, Boolean useInput)
    {
      return (id == null) && (useInput == null);
    }
  }
}
