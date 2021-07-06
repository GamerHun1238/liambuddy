package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.HashMap;

public class CreatorCollector
{
  protected static final int C_DEFAULT = 0;
  protected static final int C_STRING = 1;
  protected static final int C_INT = 2;
  protected static final int C_LONG = 3;
  protected static final int C_DOUBLE = 4;
  protected static final int C_BOOLEAN = 5;
  protected static final int C_DELEGATE = 6;
  protected static final int C_PROPS = 7;
  protected static final int C_ARRAY_DELEGATE = 8;
  protected static final String[] TYPE_DESCS = { "default", "from-String", "from-int", "from-long", "from-double", "from-boolean", "delegate", "property-based" };
  



  protected final BeanDescription _beanDesc;
  



  protected final boolean _canFixAccess;
  



  protected final boolean _forceAccess;
  


  protected final AnnotatedWithParams[] _creators = new AnnotatedWithParams[9];
  







  protected int _explicitCreators = 0;
  
  protected boolean _hasNonDefaultCreator = false;
  


  protected SettableBeanProperty[] _delegateArgs;
  

  protected SettableBeanProperty[] _arrayDelegateArgs;
  

  protected SettableBeanProperty[] _propertyBasedArgs;
  


  public CreatorCollector(BeanDescription beanDesc, MapperConfig<?> config)
  {
    _beanDesc = beanDesc;
    _canFixAccess = config.canOverrideAccessModifiers();
    
    _forceAccess = config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
  }
  
  public com.fasterxml.jackson.databind.deser.ValueInstantiator constructValueInstantiator(DeserializationContext ctxt)
    throws JsonMappingException
  {
    DeserializationConfig config = ctxt.getConfig();
    JavaType delegateType = _computeDelegateType(ctxt, _creators[6], _delegateArgs);
    
    JavaType arrayDelegateType = _computeDelegateType(ctxt, _creators[8], _arrayDelegateArgs);
    
    JavaType type = _beanDesc.getType();
    
    StdValueInstantiator inst = new StdValueInstantiator(config, type);
    inst.configureFromObjectSettings(_creators[0], _creators[6], delegateType, _delegateArgs, _creators[7], _propertyBasedArgs);
    

    inst.configureFromArraySettings(_creators[8], arrayDelegateType, _arrayDelegateArgs);
    
    inst.configureFromStringCreator(_creators[1]);
    inst.configureFromIntCreator(_creators[2]);
    inst.configureFromLongCreator(_creators[3]);
    inst.configureFromDoubleCreator(_creators[4]);
    inst.configureFromBooleanCreator(_creators[5]);
    return inst;
  }
  















  public void setDefaultCreator(AnnotatedWithParams creator)
  {
    _creators[0] = ((AnnotatedWithParams)_fixAccess(creator));
  }
  
  public void addStringCreator(AnnotatedWithParams creator, boolean explicit) {
    verifyNonDup(creator, 1, explicit);
  }
  
  public void addIntCreator(AnnotatedWithParams creator, boolean explicit) {
    verifyNonDup(creator, 2, explicit);
  }
  
  public void addLongCreator(AnnotatedWithParams creator, boolean explicit) {
    verifyNonDup(creator, 3, explicit);
  }
  
  public void addDoubleCreator(AnnotatedWithParams creator, boolean explicit) {
    verifyNonDup(creator, 4, explicit);
  }
  
  public void addBooleanCreator(AnnotatedWithParams creator, boolean explicit) {
    verifyNonDup(creator, 5, explicit);
  }
  


  public void addDelegatingCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] injectables, int delegateeIndex)
  {
    if (creator.getParameterType(delegateeIndex).isCollectionLikeType()) {
      if (verifyNonDup(creator, 8, explicit)) {
        _arrayDelegateArgs = injectables;
      }
    }
    else if (verifyNonDup(creator, 6, explicit)) {
      _delegateArgs = injectables;
    }
  }
  


  public void addPropertyCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] properties)
  {
    if (verifyNonDup(creator, 7, explicit))
    {
      if (properties.length > 1) {
        HashMap<String, Integer> names = new HashMap();
        int i = 0; for (int len = properties.length; i < len; i++) {
          String name = properties[i].getName();
          

          if ((!name.isEmpty()) || (properties[i].getInjectableValueId() == null))
          {

            Integer old = (Integer)names.put(name, Integer.valueOf(i));
            if (old != null)
              throw new IllegalArgumentException(String.format("Duplicate creator property \"%s\" (index %s vs %d) for type %s ", new Object[] { name, old, 
              
                Integer.valueOf(i), ClassUtil.nameOf(_beanDesc.getBeanClass()) }));
          }
        }
      }
      _propertyBasedArgs = properties;
    }
  }
  








  public boolean hasDefaultCreator()
  {
    return _creators[0] != null;
  }
  


  public boolean hasDelegatingCreator()
  {
    return _creators[6] != null;
  }
  


  public boolean hasPropertyBasedCreator()
  {
    return _creators[7] != null;
  }
  







  private JavaType _computeDelegateType(DeserializationContext ctxt, AnnotatedWithParams creator, SettableBeanProperty[] delegateArgs)
    throws JsonMappingException
  {
    if ((!_hasNonDefaultCreator) || (creator == null)) {
      return null;
    }
    
    int ix = 0;
    if (delegateArgs != null) {
      int i = 0; for (int len = delegateArgs.length; i < len; i++) {
        if (delegateArgs[i] == null) {
          ix = i;
          break;
        }
      }
    }
    DeserializationConfig config = ctxt.getConfig();
    



    JavaType baseType = creator.getParameterType(ix);
    AnnotationIntrospector intr = config.getAnnotationIntrospector();
    if (intr != null) {
      AnnotatedParameter delegate = creator.getParameter(ix);
      

      Object deserDef = intr.findDeserializer(delegate);
      if (deserDef != null) {
        com.fasterxml.jackson.databind.JsonDeserializer<Object> deser = ctxt.deserializerInstance(delegate, deserDef);
        baseType = baseType.withValueHandler(deser);
      }
      else {
        baseType = intr.refineDeserializationType(config, delegate, baseType);
      }
    }
    
    return baseType;
  }
  
  private <T extends AnnotatedMember> T _fixAccess(T member) {
    if ((member != null) && (_canFixAccess)) {
      ClassUtil.checkAndFixAccess((java.lang.reflect.Member)member.getAnnotated(), _forceAccess);
    }
    
    return member;
  }
  



  protected boolean verifyNonDup(AnnotatedWithParams newOne, int typeIndex, boolean explicit)
  {
    int mask = 1 << typeIndex;
    _hasNonDefaultCreator = true;
    AnnotatedWithParams oldOne = _creators[typeIndex];
    
    if (oldOne != null) { boolean verify;
      boolean verify;
      if ((_explicitCreators & mask) != 0)
      {
        if (!explicit) {
          return false;
        }
        
        verify = true;
      }
      else {
        verify = !explicit;
      }
      

      if ((verify) && (oldOne.getClass() == newOne.getClass()))
      {
        Class<?> oldType = oldOne.getRawParameterType(0);
        Class<?> newType = newOne.getRawParameterType(0);
        
        if (oldType == newType)
        {




          if (_isEnumValueOf(newOne)) {
            return false;
          }
          if (!_isEnumValueOf(oldOne))
          {

            throw new IllegalArgumentException(String.format("Conflicting %s creators: already had %s creator %s, encountered another: %s", new Object[] { TYPE_DESCS[typeIndex], explicit ? "explicitly marked" : "implicitly discovered", oldOne, newOne }));


          }
          



        }
        else if (newType.isAssignableFrom(oldType))
        {
          return false;
        }
      }
    }
    
    if (explicit) {
      _explicitCreators |= mask;
    }
    _creators[typeIndex] = ((AnnotatedWithParams)_fixAccess(newOne));
    return true;
  }
  




  protected boolean _isEnumValueOf(AnnotatedWithParams creator)
  {
    return (ClassUtil.isEnumType(creator.getDeclaringClass())) && 
      ("valueOf".equals(creator.getName()));
  }
}
