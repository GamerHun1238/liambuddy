package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


































































public class BasicPolymorphicTypeValidator
  extends PolymorphicTypeValidator.Base
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final Set<Class<?>> _invalidBaseTypes;
  protected final TypeMatcher[] _baseTypeMatchers;
  protected final NameMatcher[] _subTypeNameMatchers;
  protected final TypeMatcher[] _subClassMatchers;
  
  public static class Builder
  {
    protected Set<Class<?>> _invalidBaseTypes;
    protected List<BasicPolymorphicTypeValidator.TypeMatcher> _baseTypeMatchers;
    protected List<BasicPolymorphicTypeValidator.NameMatcher> _subTypeNameMatchers;
    protected List<BasicPolymorphicTypeValidator.TypeMatcher> _subTypeClassMatchers;
    
    protected Builder() {}
    
    public Builder allowIfBaseType(final Class<?> baseOfBase)
    {
      _appendBaseMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
      {
        public boolean match(Class<?> clazz) {
          return baseOfBase.isAssignableFrom(clazz);
        }
      });
    }
    
















    public Builder allowIfBaseType(final Pattern patternForBase)
    {
      _appendBaseMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
      {
        public boolean match(Class<?> clazz) {
          return patternForBase.matcher(clazz.getName()).matches();
        }
      });
    }
    










    public Builder allowIfBaseType(final String prefixForBase)
    {
      _appendBaseMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
      {
        public boolean match(Class<?> clazz) {
          return clazz.getName().startsWith(prefixForBase);
        }
      });
    }
    











    public Builder denyForExactBaseType(Class<?> baseTypeToDeny)
    {
      if (_invalidBaseTypes == null) {
        _invalidBaseTypes = new HashSet();
      }
      _invalidBaseTypes.add(baseTypeToDeny);
      return this;
    }
    












    public Builder allowIfSubType(final Class<?> subTypeBase)
    {
      _appendSubClassMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
      {
        public boolean match(Class<?> clazz) {
          return subTypeBase.isAssignableFrom(clazz);
        }
      });
    }
    















    public Builder allowIfSubType(final Pattern patternForSubType)
    {
      _appendSubNameMatcher(new BasicPolymorphicTypeValidator.NameMatcher()
      {
        public boolean match(String clazzName) {
          return patternForSubType.matcher(clazzName).matches();
        }
      });
    }
    










    public Builder allowIfSubType(final String prefixForSubType)
    {
      _appendSubNameMatcher(new BasicPolymorphicTypeValidator.NameMatcher()
      {
        public boolean match(String clazzName) {
          return clazzName.startsWith(prefixForSubType);
        }
      });
    }
    













    public Builder allowIfSubTypeIsArray()
    {
      _appendSubClassMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
      {
        public boolean match(Class<?> clazz) {
          return clazz.isArray();
        }
      });
    }
    
    public BasicPolymorphicTypeValidator build() {
      return new BasicPolymorphicTypeValidator(_invalidBaseTypes, _baseTypeMatchers == null ? null : 
        (BasicPolymorphicTypeValidator.TypeMatcher[])_baseTypeMatchers.toArray(new BasicPolymorphicTypeValidator.TypeMatcher[0]), _subTypeNameMatchers == null ? null : 
        (BasicPolymorphicTypeValidator.NameMatcher[])_subTypeNameMatchers.toArray(new BasicPolymorphicTypeValidator.NameMatcher[0]), _subTypeClassMatchers == null ? null : 
        (BasicPolymorphicTypeValidator.TypeMatcher[])_subTypeClassMatchers.toArray(new BasicPolymorphicTypeValidator.TypeMatcher[0]));
    }
    
    protected Builder _appendBaseMatcher(BasicPolymorphicTypeValidator.TypeMatcher matcher)
    {
      if (_baseTypeMatchers == null) {
        _baseTypeMatchers = new ArrayList();
      }
      _baseTypeMatchers.add(matcher);
      return this;
    }
    
    protected Builder _appendSubNameMatcher(BasicPolymorphicTypeValidator.NameMatcher matcher) {
      if (_subTypeNameMatchers == null) {
        _subTypeNameMatchers = new ArrayList();
      }
      _subTypeNameMatchers.add(matcher);
      return this;
    }
    
    protected Builder _appendSubClassMatcher(BasicPolymorphicTypeValidator.TypeMatcher matcher) {
      if (_subTypeClassMatchers == null) {
        _subTypeClassMatchers = new ArrayList();
      }
      _subTypeClassMatchers.add(matcher);
      return this;
    }
  }
  
































  protected BasicPolymorphicTypeValidator(Set<Class<?>> invalidBaseTypes, TypeMatcher[] baseTypeMatchers, NameMatcher[] subTypeNameMatchers, TypeMatcher[] subClassMatchers)
  {
    _invalidBaseTypes = invalidBaseTypes;
    _baseTypeMatchers = baseTypeMatchers;
    _subTypeNameMatchers = subTypeNameMatchers;
    _subClassMatchers = subClassMatchers;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  

  public PolymorphicTypeValidator.Validity validateBaseType(MapperConfig<?> ctxt, JavaType baseType)
  {
    Class<?> rawBase = baseType.getRawClass();
    if ((_invalidBaseTypes != null) && 
      (_invalidBaseTypes.contains(rawBase))) {
      return PolymorphicTypeValidator.Validity.DENIED;
    }
    
    if (_baseTypeMatchers != null) {
      for (TypeMatcher m : _baseTypeMatchers) {
        if (m.match(rawBase)) {
          return PolymorphicTypeValidator.Validity.ALLOWED;
        }
      }
    }
    return PolymorphicTypeValidator.Validity.INDETERMINATE;
  }
  



  public PolymorphicTypeValidator.Validity validateSubClassName(MapperConfig<?> ctxt, JavaType baseType, String subClassName)
    throws JsonMappingException
  {
    if (_subTypeNameMatchers != null) {
      for (NameMatcher m : _subTypeNameMatchers) {
        if (m.match(subClassName)) {
          return PolymorphicTypeValidator.Validity.ALLOWED;
        }
      }
    }
    
    return PolymorphicTypeValidator.Validity.INDETERMINATE;
  }
  


  public PolymorphicTypeValidator.Validity validateSubType(MapperConfig<?> ctxt, JavaType baseType, JavaType subType)
    throws JsonMappingException
  {
    if (_subClassMatchers != null) {
      Class<?> subClass = subType.getRawClass();
      for (TypeMatcher m : _subClassMatchers) {
        if (m.match(subClass)) {
          return PolymorphicTypeValidator.Validity.ALLOWED;
        }
      }
    }
    
    return PolymorphicTypeValidator.Validity.INDETERMINATE;
  }
  
  protected static abstract class NameMatcher
  {
    protected NameMatcher() {}
    
    public abstract boolean match(String paramString);
  }
  
  protected static abstract class TypeMatcher
  {
    protected TypeMatcher() {}
    
    public abstract boolean match(Class<?> paramClass);
  }
}
