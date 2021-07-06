package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

public class PropertyBuilder
{
  private static final Object NO_DEFAULT_MARKER = Boolean.FALSE;
  



  protected final SerializationConfig _config;
  



  protected final BeanDescription _beanDesc;
  



  protected final AnnotationIntrospector _annotationIntrospector;
  



  protected Object _defaultBean;
  



  protected final JsonInclude.Value _defaultInclusion;
  


  protected final boolean _useRealPropertyDefaults;
  



  public PropertyBuilder(SerializationConfig config, BeanDescription beanDesc)
  {
    _config = config;
    _beanDesc = beanDesc;
    









    JsonInclude.Value inclPerType = JsonInclude.Value.merge(beanDesc
      .findPropertyInclusion(JsonInclude.Value.empty()), config
      .getDefaultPropertyInclusion(beanDesc.getBeanClass(), 
      JsonInclude.Value.empty()));
    _defaultInclusion = JsonInclude.Value.merge(config.getDefaultPropertyInclusion(), inclPerType);
    
    _useRealPropertyDefaults = (inclPerType.getValueInclusion() == JsonInclude.Include.NON_DEFAULT);
    _annotationIntrospector = _config.getAnnotationIntrospector();
  }
  





  public com.fasterxml.jackson.databind.util.Annotations getClassAnnotations()
  {
    return _beanDesc.getClassAnnotations();
  }
  









  protected BeanPropertyWriter buildWriter(SerializerProvider prov, BeanPropertyDefinition propDef, JavaType declaredType, com.fasterxml.jackson.databind.JsonSerializer<?> ser, TypeSerializer typeSer, TypeSerializer contentTypeSer, AnnotatedMember am, boolean defaultUseStaticTyping)
    throws JsonMappingException
  {
    try
    {
      serializationType = findSerializationType(am, defaultUseStaticTyping, declaredType);
    } catch (JsonMappingException e) { JavaType serializationType;
      if (propDef == null) {
        return (BeanPropertyWriter)prov.reportBadDefinition(declaredType, ClassUtil.exceptionMessage(e));
      }
      return (BeanPropertyWriter)prov.reportBadPropertyDefinition(_beanDesc, propDef, ClassUtil.exceptionMessage(e), new Object[0]);
    }
    
    JavaType serializationType;
    if (contentTypeSer != null)
    {


      if (serializationType == null)
      {
        serializationType = declaredType;
      }
      JavaType ct = serializationType.getContentType();
      
      if (ct == null) {
        prov.reportBadPropertyDefinition(_beanDesc, propDef, "serialization type " + serializationType + " has no content", new Object[0]);
      }
      
      serializationType = serializationType.withContentTypeHandler(contentTypeSer);
      ct = serializationType.getContentType();
    }
    
    Object valueToSuppress = null;
    boolean suppressNulls = false;
    

    JavaType actualType = serializationType == null ? declaredType : serializationType;
    

    AnnotatedMember accessor = propDef.getAccessor();
    if (accessor == null)
    {
      return (BeanPropertyWriter)prov.reportBadPropertyDefinition(_beanDesc, propDef, "could not determine property type", new Object[0]);
    }
    
    Class<?> rawPropertyType = accessor.getRawType();
    



    JsonInclude.Value inclV = _config.getDefaultInclusion(actualType.getRawClass(), rawPropertyType, _defaultInclusion);
    



    inclV = inclV.withOverrides(propDef.findInclusion());
    
    JsonInclude.Include inclusion = inclV.getValueInclusion();
    if (inclusion == JsonInclude.Include.USE_DEFAULTS) {
      inclusion = JsonInclude.Include.ALWAYS;
    }
    switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonInclude$Include[inclusion.ordinal()])
    {
    case 1: 
      Object defaultBean;
      








      if ((_useRealPropertyDefaults) && ((defaultBean = getDefaultBean()) != null))
      {
        if (prov.isEnabled(com.fasterxml.jackson.databind.MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
          am.fixAccess(_config.isEnabled(com.fasterxml.jackson.databind.MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        try {
          valueToSuppress = am.getValue(defaultBean);
        } catch (Exception e) {
          _throwWrapped(e, propDef.getName(), defaultBean);
        }
      } else {
        valueToSuppress = com.fasterxml.jackson.databind.util.BeanUtil.getDefaultValue(actualType);
        suppressNulls = true;
      }
      if (valueToSuppress == null) {
        suppressNulls = true;
      }
      else if (valueToSuppress.getClass().isArray()) {
        valueToSuppress = com.fasterxml.jackson.databind.util.ArrayBuilders.getArrayComparator(valueToSuppress);
      }
      

      break;
    case 2: 
      suppressNulls = true;
      
      if (actualType.isReferenceType()) {
        valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
      }
      
      break;
    case 3: 
      suppressNulls = true;
      
      valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
      break;
    case 4: 
      valueToSuppress = prov.includeFilterInstance(propDef, inclV.getValueFilter());
      if (valueToSuppress == null) {
        suppressNulls = true;
      } else {
        suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress);
      }
      break;
    case 5: 
      suppressNulls = true;
    

    case 6: 
    default: 
      if ((actualType.isContainerType()) && 
        (!_config.isEnabled(com.fasterxml.jackson.databind.SerializationFeature.WRITE_EMPTY_JSON_ARRAYS))) {
        valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
      }
      break;
    }
    Class<?>[] views = propDef.findViews();
    if (views == null) {
      views = _beanDesc.findDefaultViews();
    }
    
    BeanPropertyWriter bpw = new BeanPropertyWriter(propDef, am, _beanDesc.getClassAnnotations(), declaredType, ser, typeSer, serializationType, suppressNulls, valueToSuppress, views);
    


    Object serDef = _annotationIntrospector.findNullSerializer(am);
    if (serDef != null) {
      bpw.assignNullSerializer(prov.serializerInstance(am, serDef));
    }
    
    com.fasterxml.jackson.databind.util.NameTransformer unwrapper = _annotationIntrospector.findUnwrappingNameTransformer(am);
    if (unwrapper != null) {
      bpw = bpw.unwrappingWriter(unwrapper);
    }
    return bpw;
  }
  












  protected JavaType findSerializationType(com.fasterxml.jackson.databind.introspect.Annotated a, boolean useStaticTyping, JavaType declaredType)
    throws JsonMappingException
  {
    JavaType secondary = _annotationIntrospector.refineSerializationType(_config, a, declaredType);
    


    if (secondary != declaredType) {
      Class<?> serClass = secondary.getRawClass();
      
      Class<?> rawDeclared = declaredType.getRawClass();
      if (!serClass.isAssignableFrom(rawDeclared))
      {







        if (!rawDeclared.isAssignableFrom(serClass)) {
          throw new IllegalArgumentException("Illegal concrete-type annotation for method '" + a.getName() + "': class " + serClass.getName() + " not a super-type of (declared) class " + rawDeclared.getName());
        }
      }
      



      useStaticTyping = true;
      declaredType = secondary;
    }
    
    com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing typing = _annotationIntrospector.findSerializationTyping(a);
    if ((typing != null) && (typing != com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing.DEFAULT_TYPING)) {
      useStaticTyping = typing == com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing.STATIC;
    }
    if (useStaticTyping)
    {
      return declaredType.withStaticTyping();
    }
    
    return null;
  }
  






  protected Object getDefaultBean()
  {
    Object def = _defaultBean;
    if (def == null)
    {


      def = _beanDesc.instantiateBean(_config.canOverrideAccessModifiers());
      if (def == null)
      {






        def = NO_DEFAULT_MARKER;
      }
      _defaultBean = def;
    }
    return def == NO_DEFAULT_MARKER ? null : _defaultBean;
  }
  
















  @Deprecated
  protected Object getPropertyDefaultValue(String name, AnnotatedMember member, JavaType type)
  {
    Object defaultBean = getDefaultBean();
    if (defaultBean == null) {
      return getDefaultValue(type);
    }
    try {
      return member.getValue(defaultBean);
    } catch (Exception e) {
      return _throwWrapped(e, name, defaultBean);
    }
  }
  


  @Deprecated
  protected Object getDefaultValue(JavaType type)
  {
    return com.fasterxml.jackson.databind.util.BeanUtil.getDefaultValue(type);
  }
  






  protected Object _throwWrapped(Exception e, String propName, Object defaultBean)
  {
    Throwable t = e;
    while (t.getCause() != null) {
      t = t.getCause();
    }
    ClassUtil.throwIfError(t);
    ClassUtil.throwIfRTE(t);
    throw new IllegalArgumentException("Failed to get property '" + propName + "' of default " + defaultBean.getClass().getName() + " instance");
  }
}
