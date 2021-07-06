package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
















@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAutoDetect
{
  Visibility getterVisibility() default Visibility.DEFAULT;
  
  Visibility isGetterVisibility() default Visibility.DEFAULT;
  
  Visibility setterVisibility() default Visibility.DEFAULT;
  
  Visibility creatorVisibility() default Visibility.DEFAULT;
  
  Visibility fieldVisibility() default Visibility.DEFAULT;
  
  public static enum Visibility
  {
    ANY, 
    



    NON_PRIVATE, 
    




    PROTECTED_AND_PUBLIC, 
    



    PUBLIC_ONLY, 
    




    NONE, 
    





    DEFAULT;
    
    private Visibility() {}
    public boolean isVisible(Member m) { switch (JsonAutoDetect.1.$SwitchMap$com$fasterxml$jackson$annotation$JsonAutoDetect$Visibility[ordinal()]) {
      case 1: 
        return true;
      case 2: 
        return false;
      case 3: 
        return !Modifier.isPrivate(m.getModifiers());
      case 4: 
        if (Modifier.isProtected(m.getModifiers())) {
          return true;
        }
      
      case 5: 
        return Modifier.isPublic(m.getModifiers());
      }
      return false;
    }
  }
  






















  public static class Value
    implements JacksonAnnotationValue<JsonAutoDetect>, Serializable
  {
    private static final long serialVersionUID = 1L;
    




















    private static final JsonAutoDetect.Visibility DEFAULT_FIELD_VISIBILITY = JsonAutoDetect.Visibility.PUBLIC_ONLY;
    









    protected static final Value DEFAULT = new Value(DEFAULT_FIELD_VISIBILITY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
    






    protected static final Value NO_OVERRIDES = new Value(JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT);
    
    protected final JsonAutoDetect.Visibility _fieldVisibility;
    
    protected final JsonAutoDetect.Visibility _getterVisibility;
    
    protected final JsonAutoDetect.Visibility _isGetterVisibility;
    
    protected final JsonAutoDetect.Visibility _setterVisibility;
    protected final JsonAutoDetect.Visibility _creatorVisibility;
    
    private Value(JsonAutoDetect.Visibility fields, JsonAutoDetect.Visibility getters, JsonAutoDetect.Visibility isGetters, JsonAutoDetect.Visibility setters, JsonAutoDetect.Visibility creators)
    {
      _fieldVisibility = fields;
      _getterVisibility = getters;
      _isGetterVisibility = isGetters;
      _setterVisibility = setters;
      _creatorVisibility = creators;
    }
    
    public static Value defaultVisibility() {
      return DEFAULT;
    }
    
    public static Value noOverrides() {
      return NO_OVERRIDES;
    }
    
    public static Value from(JsonAutoDetect src) {
      return construct(src.fieldVisibility(), src
        .getterVisibility(), src.isGetterVisibility(), src.setterVisibility(), src
        .creatorVisibility());
    }
    





    public static Value construct(PropertyAccessor acc, JsonAutoDetect.Visibility visibility)
    {
      JsonAutoDetect.Visibility fields = JsonAutoDetect.Visibility.DEFAULT;
      JsonAutoDetect.Visibility getters = JsonAutoDetect.Visibility.DEFAULT;
      JsonAutoDetect.Visibility isGetters = JsonAutoDetect.Visibility.DEFAULT;
      JsonAutoDetect.Visibility setters = JsonAutoDetect.Visibility.DEFAULT;
      JsonAutoDetect.Visibility creators = JsonAutoDetect.Visibility.DEFAULT;
      switch (JsonAutoDetect.1.$SwitchMap$com$fasterxml$jackson$annotation$PropertyAccessor[acc.ordinal()]) {
      case 1: 
        creators = visibility;
        break;
      case 2: 
        fields = visibility;
        break;
      case 3: 
        getters = visibility;
        break;
      case 4: 
        isGetters = visibility;
        break;
      case 5: 
        break;
      case 6: 
        setters = visibility;
        break;
      case 7: 
        fields = getters = isGetters = setters = creators = visibility;
      }
      
      return construct(fields, getters, isGetters, setters, creators);
    }
    


    public static Value construct(JsonAutoDetect.Visibility fields, JsonAutoDetect.Visibility getters, JsonAutoDetect.Visibility isGetters, JsonAutoDetect.Visibility setters, JsonAutoDetect.Visibility creators)
    {
      Value v = _predefined(fields, getters, isGetters, setters, creators);
      if (v == null) {
        v = new Value(fields, getters, isGetters, setters, creators);
      }
      return v;
    }
    
    public Value withFieldVisibility(JsonAutoDetect.Visibility v) {
      return construct(v, _getterVisibility, _isGetterVisibility, _setterVisibility, _creatorVisibility);
    }
    
    public Value withGetterVisibility(JsonAutoDetect.Visibility v)
    {
      return construct(_fieldVisibility, v, _isGetterVisibility, _setterVisibility, _creatorVisibility);
    }
    
    public Value withIsGetterVisibility(JsonAutoDetect.Visibility v)
    {
      return construct(_fieldVisibility, _getterVisibility, v, _setterVisibility, _creatorVisibility);
    }
    
    public Value withSetterVisibility(JsonAutoDetect.Visibility v)
    {
      return construct(_fieldVisibility, _getterVisibility, _isGetterVisibility, v, _creatorVisibility);
    }
    
    public Value withCreatorVisibility(JsonAutoDetect.Visibility v)
    {
      return construct(_fieldVisibility, _getterVisibility, _isGetterVisibility, _setterVisibility, v);
    }
    

    public static Value merge(Value base, Value overrides)
    {
      return base == null ? overrides : base
        .withOverrides(overrides);
    }
    
    public Value withOverrides(Value overrides) {
      if ((overrides == null) || (overrides == NO_OVERRIDES) || (overrides == this)) {
        return this;
      }
      if (_equals(this, overrides)) {
        return this;
      }
      JsonAutoDetect.Visibility fields = _fieldVisibility;
      if (fields == JsonAutoDetect.Visibility.DEFAULT) {
        fields = _fieldVisibility;
      }
      JsonAutoDetect.Visibility getters = _getterVisibility;
      if (getters == JsonAutoDetect.Visibility.DEFAULT) {
        getters = _getterVisibility;
      }
      JsonAutoDetect.Visibility isGetters = _isGetterVisibility;
      if (isGetters == JsonAutoDetect.Visibility.DEFAULT) {
        isGetters = _isGetterVisibility;
      }
      JsonAutoDetect.Visibility setters = _setterVisibility;
      if (setters == JsonAutoDetect.Visibility.DEFAULT) {
        setters = _setterVisibility;
      }
      JsonAutoDetect.Visibility creators = _creatorVisibility;
      if (creators == JsonAutoDetect.Visibility.DEFAULT) {
        creators = _creatorVisibility;
      }
      return construct(fields, getters, isGetters, setters, creators);
    }
    
    public Class<JsonAutoDetect> valueFor()
    {
      return JsonAutoDetect.class;
    }
    
    public JsonAutoDetect.Visibility getFieldVisibility() { return _fieldVisibility; }
    public JsonAutoDetect.Visibility getGetterVisibility() { return _getterVisibility; }
    public JsonAutoDetect.Visibility getIsGetterVisibility() { return _isGetterVisibility; }
    public JsonAutoDetect.Visibility getSetterVisibility() { return _setterVisibility; }
    public JsonAutoDetect.Visibility getCreatorVisibility() { return _creatorVisibility; }
    
    protected Object readResolve()
    {
      Value v = _predefined(_fieldVisibility, _getterVisibility, _isGetterVisibility, _setterVisibility, _creatorVisibility);
      
      return v == null ? this : v;
    }
    
    public String toString()
    {
      return String.format("JsonAutoDetect.Value(fields=%s,getters=%s,isGetters=%s,setters=%s,creators=%s)", new Object[] { _fieldVisibility, _getterVisibility, _isGetterVisibility, _setterVisibility, _creatorVisibility });
    }
    



    public int hashCode()
    {
      return 
      


        1 + _fieldVisibility.ordinal() ^ 3 * _getterVisibility.ordinal() - 7 * _isGetterVisibility.ordinal() + 11 * _setterVisibility.ordinal() ^ 13 * _creatorVisibility.ordinal();
    }
    

    public boolean equals(Object o)
    {
      if (o == this) return true;
      if (o == null) return false;
      return (o.getClass() == getClass()) && (_equals(this, (Value)o));
    }
    


    private static Value _predefined(JsonAutoDetect.Visibility fields, JsonAutoDetect.Visibility getters, JsonAutoDetect.Visibility isGetters, JsonAutoDetect.Visibility setters, JsonAutoDetect.Visibility creators)
    {
      if (fields == DEFAULT_FIELD_VISIBILITY) {
        if ((getters == DEFAULT_getterVisibility) && (isGetters == DEFAULT_isGetterVisibility) && (setters == DEFAULT_setterVisibility) && (creators == DEFAULT_creatorVisibility))
        {


          return DEFAULT;
        }
      } else if ((fields == JsonAutoDetect.Visibility.DEFAULT) && 
        (getters == JsonAutoDetect.Visibility.DEFAULT) && (isGetters == JsonAutoDetect.Visibility.DEFAULT) && (setters == JsonAutoDetect.Visibility.DEFAULT) && (creators == JsonAutoDetect.Visibility.DEFAULT))
      {


        return NO_OVERRIDES;
      }
      
      return null;
    }
    
    private static boolean _equals(Value a, Value b)
    {
      return (_fieldVisibility == _fieldVisibility) && (_getterVisibility == _getterVisibility) && (_isGetterVisibility == _isGetterVisibility) && (_setterVisibility == _setterVisibility) && (_creatorVisibility == _creatorVisibility);
    }
  }
}
