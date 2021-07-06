package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Value;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;













































































































public abstract interface VisibilityChecker<T extends VisibilityChecker<T>>
{
  public abstract T with(JsonAutoDetect paramJsonAutoDetect);
  
  public abstract T withOverrides(JsonAutoDetect.Value paramValue);
  
  public abstract T with(JsonAutoDetect.Visibility paramVisibility);
  
  public abstract T withVisibility(PropertyAccessor paramPropertyAccessor, JsonAutoDetect.Visibility paramVisibility);
  
  public abstract T withGetterVisibility(JsonAutoDetect.Visibility paramVisibility);
  
  public abstract T withIsGetterVisibility(JsonAutoDetect.Visibility paramVisibility);
  
  public abstract T withSetterVisibility(JsonAutoDetect.Visibility paramVisibility);
  
  public abstract T withCreatorVisibility(JsonAutoDetect.Visibility paramVisibility);
  
  public abstract T withFieldVisibility(JsonAutoDetect.Visibility paramVisibility);
  
  public abstract boolean isGetterVisible(Method paramMethod);
  
  public abstract boolean isGetterVisible(AnnotatedMethod paramAnnotatedMethod);
  
  public abstract boolean isIsGetterVisible(Method paramMethod);
  
  public abstract boolean isIsGetterVisible(AnnotatedMethod paramAnnotatedMethod);
  
  public abstract boolean isSetterVisible(Method paramMethod);
  
  public abstract boolean isSetterVisible(AnnotatedMethod paramAnnotatedMethod);
  
  public abstract boolean isCreatorVisible(Member paramMember);
  
  public abstract boolean isCreatorVisible(AnnotatedMember paramAnnotatedMember);
  
  public abstract boolean isFieldVisible(Field paramField);
  
  public abstract boolean isFieldVisible(AnnotatedField paramAnnotatedField);
  
  public static class Std
    implements VisibilityChecker<Std>, Serializable
  {
    private static final long serialVersionUID = 1L;
    protected static final Std DEFAULT = new Std(JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
    
    protected final JsonAutoDetect.Visibility _getterMinLevel;
    
    protected final JsonAutoDetect.Visibility _isGetterMinLevel;
    
    protected final JsonAutoDetect.Visibility _setterMinLevel;
    
    protected final JsonAutoDetect.Visibility _creatorMinLevel;
    
    protected final JsonAutoDetect.Visibility _fieldMinLevel;
    
    public static Std defaultInstance()
    {
      return DEFAULT;
    }
    






    public Std(JsonAutoDetect ann)
    {
      _getterMinLevel = ann.getterVisibility();
      _isGetterMinLevel = ann.isGetterVisibility();
      _setterMinLevel = ann.setterVisibility();
      _creatorMinLevel = ann.creatorVisibility();
      _fieldMinLevel = ann.fieldVisibility();
    }
    




    public Std(JsonAutoDetect.Visibility getter, JsonAutoDetect.Visibility isGetter, JsonAutoDetect.Visibility setter, JsonAutoDetect.Visibility creator, JsonAutoDetect.Visibility field)
    {
      _getterMinLevel = getter;
      _isGetterMinLevel = isGetter;
      _setterMinLevel = setter;
      _creatorMinLevel = creator;
      _fieldMinLevel = field;
    }
    







    public Std(JsonAutoDetect.Visibility v)
    {
      if (v == JsonAutoDetect.Visibility.DEFAULT) {
        _getterMinLevel = DEFAULT_getterMinLevel;
        _isGetterMinLevel = DEFAULT_isGetterMinLevel;
        _setterMinLevel = DEFAULT_setterMinLevel;
        _creatorMinLevel = DEFAULT_creatorMinLevel;
        _fieldMinLevel = DEFAULT_fieldMinLevel;
      } else {
        _getterMinLevel = v;
        _isGetterMinLevel = v;
        _setterMinLevel = v;
        _creatorMinLevel = v;
        _fieldMinLevel = v;
      }
    }
    


    public static Std construct(JsonAutoDetect.Value vis)
    {
      return DEFAULT.withOverrides(vis);
    }
    







    protected Std _with(JsonAutoDetect.Visibility g, JsonAutoDetect.Visibility isG, JsonAutoDetect.Visibility s, JsonAutoDetect.Visibility cr, JsonAutoDetect.Visibility f)
    {
      if ((g == _getterMinLevel) && (isG == _isGetterMinLevel) && (s == _setterMinLevel) && (cr == _creatorMinLevel) && (f == _fieldMinLevel))
      {




        return this;
      }
      return new Std(g, isG, s, cr, f);
    }
    

    public Std with(JsonAutoDetect ann)
    {
      Std curr = this;
      if (ann != null) {
        return _with(
          _defaultOrOverride(_getterMinLevel, ann.getterVisibility()), 
          _defaultOrOverride(_isGetterMinLevel, ann.isGetterVisibility()), 
          _defaultOrOverride(_setterMinLevel, ann.setterVisibility()), 
          _defaultOrOverride(_creatorMinLevel, ann.creatorVisibility()), 
          _defaultOrOverride(_fieldMinLevel, ann.fieldVisibility()));
      }
      
      return curr;
    }
    

    public Std withOverrides(JsonAutoDetect.Value vis)
    {
      Std curr = this;
      if (vis != null) {
        return _with(
          _defaultOrOverride(_getterMinLevel, vis.getGetterVisibility()), 
          _defaultOrOverride(_isGetterMinLevel, vis.getIsGetterVisibility()), 
          _defaultOrOverride(_setterMinLevel, vis.getSetterVisibility()), 
          _defaultOrOverride(_creatorMinLevel, vis.getCreatorVisibility()), 
          _defaultOrOverride(_fieldMinLevel, vis.getFieldVisibility()));
      }
      
      return curr;
    }
    
    private JsonAutoDetect.Visibility _defaultOrOverride(JsonAutoDetect.Visibility defaults, JsonAutoDetect.Visibility override) {
      if (override == JsonAutoDetect.Visibility.DEFAULT) {
        return defaults;
      }
      return override;
    }
    

    public Std with(JsonAutoDetect.Visibility v)
    {
      if (v == JsonAutoDetect.Visibility.DEFAULT) {
        return DEFAULT;
      }
      return new Std(v);
    }
    

    public Std withVisibility(PropertyAccessor method, JsonAutoDetect.Visibility v)
    {
      switch (VisibilityChecker.1.$SwitchMap$com$fasterxml$jackson$annotation$PropertyAccessor[method.ordinal()]) {
      case 1: 
        return withGetterVisibility(v);
      case 2: 
        return withSetterVisibility(v);
      case 3: 
        return withCreatorVisibility(v);
      case 4: 
        return withFieldVisibility(v);
      case 5: 
        return withIsGetterVisibility(v);
      case 6: 
        return with(v);
      }
      
      
      return this;
    }
    

    public Std withGetterVisibility(JsonAutoDetect.Visibility v)
    {
      if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT_getterMinLevel;
      if (_getterMinLevel == v) return this;
      return new Std(v, _isGetterMinLevel, _setterMinLevel, _creatorMinLevel, _fieldMinLevel);
    }
    
    public Std withIsGetterVisibility(JsonAutoDetect.Visibility v)
    {
      if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT_isGetterMinLevel;
      if (_isGetterMinLevel == v) return this;
      return new Std(_getterMinLevel, v, _setterMinLevel, _creatorMinLevel, _fieldMinLevel);
    }
    
    public Std withSetterVisibility(JsonAutoDetect.Visibility v)
    {
      if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT_setterMinLevel;
      if (_setterMinLevel == v) return this;
      return new Std(_getterMinLevel, _isGetterMinLevel, v, _creatorMinLevel, _fieldMinLevel);
    }
    
    public Std withCreatorVisibility(JsonAutoDetect.Visibility v)
    {
      if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT_creatorMinLevel;
      if (_creatorMinLevel == v) return this;
      return new Std(_getterMinLevel, _isGetterMinLevel, _setterMinLevel, v, _fieldMinLevel);
    }
    
    public Std withFieldVisibility(JsonAutoDetect.Visibility v)
    {
      if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT_fieldMinLevel;
      if (_fieldMinLevel == v) return this;
      return new Std(_getterMinLevel, _isGetterMinLevel, _setterMinLevel, _creatorMinLevel, v);
    }
    






    public boolean isCreatorVisible(Member m)
    {
      return _creatorMinLevel.isVisible(m);
    }
    
    public boolean isCreatorVisible(AnnotatedMember m)
    {
      return isCreatorVisible(m.getMember());
    }
    
    public boolean isFieldVisible(Field f)
    {
      return _fieldMinLevel.isVisible(f);
    }
    
    public boolean isFieldVisible(AnnotatedField f)
    {
      return isFieldVisible(f.getAnnotated());
    }
    
    public boolean isGetterVisible(Method m)
    {
      return _getterMinLevel.isVisible(m);
    }
    
    public boolean isGetterVisible(AnnotatedMethod m)
    {
      return isGetterVisible(m.getAnnotated());
    }
    
    public boolean isIsGetterVisible(Method m)
    {
      return _isGetterMinLevel.isVisible(m);
    }
    
    public boolean isIsGetterVisible(AnnotatedMethod m)
    {
      return isIsGetterVisible(m.getAnnotated());
    }
    
    public boolean isSetterVisible(Method m)
    {
      return _setterMinLevel.isVisible(m);
    }
    
    public boolean isSetterVisible(AnnotatedMethod m)
    {
      return isSetterVisible(m.getAnnotated());
    }
    






    public String toString()
    {
      return String.format("[Visibility: getter=%s,isGetter=%s,setter=%s,creator=%s,field=%s]", new Object[] { _getterMinLevel, _isGetterMinLevel, _setterMinLevel, _creatorMinLevel, _fieldMinLevel });
    }
  }
}
