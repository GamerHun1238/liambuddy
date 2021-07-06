package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyMetadata.MergeInfo;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class POJOPropertyBuilder
  extends BeanPropertyDefinition
  implements Comparable<POJOPropertyBuilder>
{
  private static final AnnotationIntrospector.ReferenceProperty NOT_REFEFERENCE_PROP = AnnotationIntrospector.ReferenceProperty.managed("");
  


  protected final boolean _forSerialization;
  


  protected final MapperConfig<?> _config;
  


  protected final AnnotationIntrospector _annotationIntrospector;
  


  protected final PropertyName _name;
  


  protected final PropertyName _internalName;
  


  protected Linked<AnnotatedField> _fields;
  

  protected Linked<AnnotatedParameter> _ctorParameters;
  

  protected Linked<AnnotatedMethod> _getters;
  

  protected Linked<AnnotatedMethod> _setters;
  

  protected transient PropertyMetadata _metadata;
  

  protected transient AnnotationIntrospector.ReferenceProperty _referenceInfo;
  


  public POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName)
  {
    this(config, ai, forSerialization, internalName, internalName);
  }
  

  protected POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName, PropertyName name)
  {
    _config = config;
    _annotationIntrospector = ai;
    _internalName = internalName;
    _name = name;
    _forSerialization = forSerialization;
  }
  

  protected POJOPropertyBuilder(POJOPropertyBuilder src, PropertyName newName)
  {
    _config = _config;
    _annotationIntrospector = _annotationIntrospector;
    _internalName = _internalName;
    _name = newName;
    _fields = _fields;
    _ctorParameters = _ctorParameters;
    _getters = _getters;
    _setters = _setters;
    _forSerialization = _forSerialization;
  }
  






  public POJOPropertyBuilder withName(PropertyName newName)
  {
    return new POJOPropertyBuilder(this, newName);
  }
  

  public POJOPropertyBuilder withSimpleName(String newSimpleName)
  {
    PropertyName newName = _name.withSimpleName(newSimpleName);
    return newName == _name ? this : new POJOPropertyBuilder(this, newName);
  }
  










  public int compareTo(POJOPropertyBuilder other)
  {
    if (_ctorParameters != null) {
      if (_ctorParameters == null) {
        return -1;
      }
    } else if (_ctorParameters != null) {
      return 1;
    }
    


    return getName().compareTo(other.getName());
  }
  






  public String getName()
  {
    return _name == null ? null : _name.getSimpleName();
  }
  
  public PropertyName getFullName()
  {
    return _name;
  }
  
  public boolean hasName(PropertyName name)
  {
    return _name.equals(name);
  }
  
  public String getInternalName() {
    return _internalName.getSimpleName();
  }
  




  public PropertyName getWrapperName()
  {
    AnnotatedMember member = getPrimaryMember();
    return (member == null) || (_annotationIntrospector == null) ? null : _annotationIntrospector
      .findWrapperName(member);
  }
  








  public boolean isExplicitlyIncluded()
  {
    return (_anyExplicits(_fields)) || 
      (_anyExplicits(_getters)) || 
      (_anyExplicits(_setters)) || 
      



      (_anyExplicitNames(_ctorParameters));
  }
  

  public boolean isExplicitlyNamed()
  {
    return (_anyExplicitNames(_fields)) || 
      (_anyExplicitNames(_getters)) || 
      (_anyExplicitNames(_setters)) || 
      (_anyExplicitNames(_ctorParameters));
  }
  







  public PropertyMetadata getMetadata()
  {
    if (_metadata == null) {
      Boolean b = _findRequired();
      String desc = _findDescription();
      Integer idx = _findIndex();
      String def = _findDefaultValue();
      if ((b == null) && (idx == null) && (def == null))
      {
        _metadata = (desc == null ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : PropertyMetadata.STD_REQUIRED_OR_OPTIONAL.withDescription(desc));
      } else {
        _metadata = PropertyMetadata.construct(b, desc, idx, def);
      }
      if (!_forSerialization) {
        _metadata = _getSetterInfo(_metadata);
      }
    }
    return _metadata;
  }
  





  protected PropertyMetadata _getSetterInfo(PropertyMetadata metadata)
  {
    boolean needMerge = true;
    Nulls valueNulls = null;
    Nulls contentNulls = null;
    


    AnnotatedMember prim = getPrimaryMember();
    AnnotatedMember acc = getAccessor();
    
    if (prim != null)
    {
      if (_annotationIntrospector != null) {
        if (acc != null) {
          Boolean b = _annotationIntrospector.findMergeInfo(prim);
          if (b != null) {
            needMerge = false;
            if (b.booleanValue()) {
              metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForPropertyOverride(acc));
            }
          }
        }
        JsonSetter.Value setterInfo = _annotationIntrospector.findSetterInfo(prim);
        if (setterInfo != null) {
          valueNulls = setterInfo.nonDefaultValueNulls();
          contentNulls = setterInfo.nonDefaultContentNulls();
        }
      }
      

      if ((needMerge) || (valueNulls == null) || (contentNulls == null)) {
        Class<?> rawType = getRawPrimaryType();
        ConfigOverride co = _config.getConfigOverride(rawType);
        JsonSetter.Value setterInfo = co.getSetterInfo();
        if (setterInfo != null) {
          if (valueNulls == null) {
            valueNulls = setterInfo.nonDefaultValueNulls();
          }
          if (contentNulls == null) {
            contentNulls = setterInfo.nonDefaultContentNulls();
          }
        }
        if ((needMerge) && (acc != null)) {
          Boolean b = co.getMergeable();
          if (b != null) {
            needMerge = false;
            if (b.booleanValue()) {
              metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForTypeOverride(acc));
            }
          }
        }
      }
    }
    if ((needMerge) || (valueNulls == null) || (contentNulls == null)) {
      JsonSetter.Value setterInfo = _config.getDefaultSetterInfo();
      if (valueNulls == null) {
        valueNulls = setterInfo.nonDefaultValueNulls();
      }
      if (contentNulls == null) {
        contentNulls = setterInfo.nonDefaultContentNulls();
      }
      if (needMerge) {
        Boolean b = _config.getDefaultMergeable();
        if ((Boolean.TRUE.equals(b)) && (acc != null)) {
          metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForDefaults(acc));
        }
      }
    }
    if ((valueNulls != null) || (contentNulls != null)) {
      metadata = metadata.withNulls(valueNulls, contentNulls);
    }
    return metadata;
  }
  





  public JavaType getPrimaryType()
  {
    if (_forSerialization) {
      AnnotatedMember m = getGetter();
      if (m == null) {
        m = getField();
        if (m == null)
        {
          return TypeFactory.unknownType();
        }
        return m.getType();
      }
      return m.getType();
    }
    AnnotatedMember m = getConstructorParameter();
    if (m == null) {
      m = getSetter();
      

      if (m != null) {
        return ((AnnotatedMethod)m).getParameterType(0);
      }
      m = getField();
    }
    
    if (m == null) {
      m = getGetter();
      if (m == null) {
        return TypeFactory.unknownType();
      }
    }
    return m.getType();
  }
  
  public Class<?> getRawPrimaryType()
  {
    return getPrimaryType().getRawClass();
  }
  





  public boolean hasGetter()
  {
    return _getters != null;
  }
  
  public boolean hasSetter() { return _setters != null; }
  
  public boolean hasField() {
    return _fields != null;
  }
  
  public boolean hasConstructorParameter() { return _ctorParameters != null; }
  
  public boolean couldDeserialize()
  {
    return (_ctorParameters != null) || (_setters != null) || (_fields != null);
  }
  
  public boolean couldSerialize()
  {
    return (_getters != null) || (_fields != null);
  }
  
  public AnnotatedMethod getGetter()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 10	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_getters	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   4: astore_1
    //   5: aload_1
    //   6: ifnonnull +5 -> 11
    //   9: aconst_null
    //   10: areturn
    //   11: aload_1
    //   12: getfield 61	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:next	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   15: astore_2
    //   16: aload_2
    //   17: ifnonnull +11 -> 28
    //   20: aload_1
    //   21: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   24: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   27: areturn
    //   28: aload_2
    //   29: ifnull +177 -> 206
    //   32: aload_1
    //   33: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   36: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   39: invokevirtual 63	com/fasterxml/jackson/databind/introspect/AnnotatedMethod:getDeclaringClass	()Ljava/lang/Class;
    //   42: astore_3
    //   43: aload_2
    //   44: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   47: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   50: invokevirtual 63	com/fasterxml/jackson/databind/introspect/AnnotatedMethod:getDeclaringClass	()Ljava/lang/Class;
    //   53: astore 4
    //   55: aload_3
    //   56: aload 4
    //   58: if_acmpeq +29 -> 87
    //   61: aload_3
    //   62: aload 4
    //   64: invokevirtual 64	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   67: ifeq +8 -> 75
    //   70: aload_2
    //   71: astore_1
    //   72: goto +126 -> 198
    //   75: aload 4
    //   77: aload_3
    //   78: invokevirtual 64	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   81: ifeq +6 -> 87
    //   84: goto +114 -> 198
    //   87: aload_0
    //   88: aload_2
    //   89: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   92: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   95: invokevirtual 65	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_getterPriority	(Lcom/fasterxml/jackson/databind/introspect/AnnotatedMethod;)I
    //   98: istore 5
    //   100: aload_0
    //   101: aload_1
    //   102: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   105: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   108: invokevirtual 65	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_getterPriority	(Lcom/fasterxml/jackson/databind/introspect/AnnotatedMethod;)I
    //   111: istore 6
    //   113: iload 5
    //   115: iload 6
    //   117: if_icmpeq +15 -> 132
    //   120: iload 5
    //   122: iload 6
    //   124: if_icmpge +74 -> 198
    //   127: aload_2
    //   128: astore_1
    //   129: goto +69 -> 198
    //   132: new 66	java/lang/IllegalArgumentException
    //   135: dup
    //   136: new 67	java/lang/StringBuilder
    //   139: dup
    //   140: invokespecial 68	java/lang/StringBuilder:<init>	()V
    //   143: ldc 69
    //   145: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: aload_0
    //   149: invokevirtual 15	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:getName	()Ljava/lang/String;
    //   152: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   155: ldc 71
    //   157: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: aload_1
    //   161: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   164: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   167: invokevirtual 72	com/fasterxml/jackson/databind/introspect/AnnotatedMethod:getFullName	()Ljava/lang/String;
    //   170: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: ldc 73
    //   175: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: aload_2
    //   179: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   182: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   185: invokevirtual 72	com/fasterxml/jackson/databind/introspect/AnnotatedMethod:getFullName	()Ljava/lang/String;
    //   188: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   191: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   194: invokespecial 75	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   197: athrow
    //   198: aload_2
    //   199: getfield 61	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:next	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   202: astore_2
    //   203: goto -175 -> 28
    //   206: aload_0
    //   207: aload_1
    //   208: invokevirtual 76	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:withoutNext	()Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   211: putfield 10	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_getters	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   214: aload_1
    //   215: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   218: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   221: areturn
    // Line number table:
    //   Java source line #393	-> byte code offset #0
    //   Java source line #394	-> byte code offset #5
    //   Java source line #395	-> byte code offset #9
    //   Java source line #397	-> byte code offset #11
    //   Java source line #398	-> byte code offset #16
    //   Java source line #399	-> byte code offset #20
    //   Java source line #402	-> byte code offset #28
    //   Java source line #406	-> byte code offset #32
    //   Java source line #407	-> byte code offset #43
    //   Java source line #408	-> byte code offset #55
    //   Java source line #409	-> byte code offset #61
    //   Java source line #410	-> byte code offset #70
    //   Java source line #411	-> byte code offset #72
    //   Java source line #413	-> byte code offset #75
    //   Java source line #414	-> byte code offset #84
    //   Java source line #423	-> byte code offset #87
    //   Java source line #424	-> byte code offset #100
    //   Java source line #426	-> byte code offset #113
    //   Java source line #427	-> byte code offset #120
    //   Java source line #428	-> byte code offset #127
    //   Java source line #432	-> byte code offset #132
    //   Java source line #433	-> byte code offset #167
    //   Java source line #402	-> byte code offset #198
    //   Java source line #436	-> byte code offset #206
    //   Java source line #437	-> byte code offset #214
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	222	0	this	POJOPropertyBuilder
    //   4	211	1	curr	Linked<AnnotatedMethod>
    //   15	188	2	next	Linked<AnnotatedMethod>
    //   42	36	3	currClass	Class<?>
    //   53	23	4	nextClass	Class<?>
    //   98	23	5	priNext	int
    //   111	12	6	priCurr	int
  }
  
  public AnnotatedMethod getSetter()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 11	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_setters	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   4: astore_1
    //   5: aload_1
    //   6: ifnonnull +5 -> 11
    //   9: aconst_null
    //   10: areturn
    //   11: aload_1
    //   12: getfield 61	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:next	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   15: astore_2
    //   16: aload_2
    //   17: ifnonnull +11 -> 28
    //   20: aload_1
    //   21: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   24: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   27: areturn
    //   28: aload_2
    //   29: ifnull +215 -> 244
    //   32: aload_1
    //   33: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   36: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   39: invokevirtual 63	com/fasterxml/jackson/databind/introspect/AnnotatedMethod:getDeclaringClass	()Ljava/lang/Class;
    //   42: astore_3
    //   43: aload_2
    //   44: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   47: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   50: invokevirtual 63	com/fasterxml/jackson/databind/introspect/AnnotatedMethod:getDeclaringClass	()Ljava/lang/Class;
    //   53: astore 4
    //   55: aload_3
    //   56: aload 4
    //   58: if_acmpeq +29 -> 87
    //   61: aload_3
    //   62: aload 4
    //   64: invokevirtual 64	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   67: ifeq +8 -> 75
    //   70: aload_2
    //   71: astore_1
    //   72: goto +164 -> 236
    //   75: aload 4
    //   77: aload_3
    //   78: invokevirtual 64	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   81: ifeq +6 -> 87
    //   84: goto +152 -> 236
    //   87: aload_2
    //   88: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   91: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   94: astore 5
    //   96: aload_1
    //   97: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   100: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   103: astore 6
    //   105: aload_0
    //   106: aload 5
    //   108: invokevirtual 77	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_setterPriority	(Lcom/fasterxml/jackson/databind/introspect/AnnotatedMethod;)I
    //   111: istore 7
    //   113: aload_0
    //   114: aload 6
    //   116: invokevirtual 77	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_setterPriority	(Lcom/fasterxml/jackson/databind/introspect/AnnotatedMethod;)I
    //   119: istore 8
    //   121: iload 7
    //   123: iload 8
    //   125: if_icmpeq +15 -> 140
    //   128: iload 7
    //   130: iload 8
    //   132: if_icmpge +104 -> 236
    //   135: aload_2
    //   136: astore_1
    //   137: goto +99 -> 236
    //   140: aload_0
    //   141: getfield 4	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_annotationIntrospector	Lcom/fasterxml/jackson/databind/AnnotationIntrospector;
    //   144: ifnull +42 -> 186
    //   147: aload_0
    //   148: getfield 4	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_annotationIntrospector	Lcom/fasterxml/jackson/databind/AnnotationIntrospector;
    //   151: aload_0
    //   152: getfield 3	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_config	Lcom/fasterxml/jackson/databind/cfg/MapperConfig;
    //   155: aload 6
    //   157: aload 5
    //   159: invokevirtual 78	com/fasterxml/jackson/databind/AnnotationIntrospector:resolveSetterConflict	(Lcom/fasterxml/jackson/databind/cfg/MapperConfig;Lcom/fasterxml/jackson/databind/introspect/AnnotatedMethod;Lcom/fasterxml/jackson/databind/introspect/AnnotatedMethod;)Lcom/fasterxml/jackson/databind/introspect/AnnotatedMethod;
    //   162: astore 9
    //   164: aload 9
    //   166: aload 6
    //   168: if_acmpne +6 -> 174
    //   171: goto +65 -> 236
    //   174: aload 9
    //   176: aload 5
    //   178: if_acmpne +8 -> 186
    //   181: aload_2
    //   182: astore_1
    //   183: goto +53 -> 236
    //   186: new 66	java/lang/IllegalArgumentException
    //   189: dup
    //   190: ldc 79
    //   192: iconst_3
    //   193: anewarray 80	java/lang/Object
    //   196: dup
    //   197: iconst_0
    //   198: aload_0
    //   199: invokevirtual 15	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:getName	()Ljava/lang/String;
    //   202: aastore
    //   203: dup
    //   204: iconst_1
    //   205: aload_1
    //   206: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   209: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   212: invokevirtual 72	com/fasterxml/jackson/databind/introspect/AnnotatedMethod:getFullName	()Ljava/lang/String;
    //   215: aastore
    //   216: dup
    //   217: iconst_2
    //   218: aload_2
    //   219: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   222: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   225: invokevirtual 72	com/fasterxml/jackson/databind/introspect/AnnotatedMethod:getFullName	()Ljava/lang/String;
    //   228: aastore
    //   229: invokestatic 81	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   232: invokespecial 75	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   235: athrow
    //   236: aload_2
    //   237: getfield 61	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:next	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   240: astore_2
    //   241: goto -213 -> 28
    //   244: aload_0
    //   245: aload_1
    //   246: invokevirtual 76	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:withoutNext	()Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   249: putfield 11	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_setters	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   252: aload_1
    //   253: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   256: checkcast 57	com/fasterxml/jackson/databind/introspect/AnnotatedMethod
    //   259: areturn
    // Line number table:
    //   Java source line #444	-> byte code offset #0
    //   Java source line #445	-> byte code offset #5
    //   Java source line #446	-> byte code offset #9
    //   Java source line #448	-> byte code offset #11
    //   Java source line #449	-> byte code offset #16
    //   Java source line #450	-> byte code offset #20
    //   Java source line #453	-> byte code offset #28
    //   Java source line #455	-> byte code offset #32
    //   Java source line #456	-> byte code offset #43
    //   Java source line #457	-> byte code offset #55
    //   Java source line #458	-> byte code offset #61
    //   Java source line #459	-> byte code offset #70
    //   Java source line #460	-> byte code offset #72
    //   Java source line #462	-> byte code offset #75
    //   Java source line #463	-> byte code offset #84
    //   Java source line #466	-> byte code offset #87
    //   Java source line #467	-> byte code offset #96
    //   Java source line #474	-> byte code offset #105
    //   Java source line #475	-> byte code offset #113
    //   Java source line #477	-> byte code offset #121
    //   Java source line #478	-> byte code offset #128
    //   Java source line #479	-> byte code offset #135
    //   Java source line #484	-> byte code offset #140
    //   Java source line #485	-> byte code offset #147
    //   Java source line #489	-> byte code offset #164
    //   Java source line #490	-> byte code offset #171
    //   Java source line #492	-> byte code offset #174
    //   Java source line #493	-> byte code offset #181
    //   Java source line #494	-> byte code offset #183
    //   Java source line #497	-> byte code offset #186
    //   Java source line #499	-> byte code offset #199
    //   Java source line #497	-> byte code offset #229
    //   Java source line #453	-> byte code offset #236
    //   Java source line #502	-> byte code offset #244
    //   Java source line #503	-> byte code offset #252
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	260	0	this	POJOPropertyBuilder
    //   4	249	1	curr	Linked<AnnotatedMethod>
    //   15	226	2	next	Linked<AnnotatedMethod>
    //   42	36	3	currClass	Class<?>
    //   53	23	4	nextClass	Class<?>
    //   94	83	5	nextM	AnnotatedMethod
    //   103	64	6	currM	AnnotatedMethod
    //   111	18	7	priNext	int
    //   119	12	8	priCurr	int
    //   162	13	9	pref	AnnotatedMethod
  }
  
  public AnnotatedField getField()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 8	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_fields	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   4: ifnonnull +5 -> 9
    //   7: aconst_null
    //   8: areturn
    //   9: aload_0
    //   10: getfield 8	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_fields	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   13: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   16: checkcast 82	com/fasterxml/jackson/databind/introspect/AnnotatedField
    //   19: astore_1
    //   20: aload_0
    //   21: getfield 8	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:_fields	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   24: getfield 61	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:next	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   27: astore_2
    //   28: aload_2
    //   29: ifnull +120 -> 149
    //   32: aload_2
    //   33: getfield 62	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:value	Ljava/lang/Object;
    //   36: checkcast 82	com/fasterxml/jackson/databind/introspect/AnnotatedField
    //   39: astore_3
    //   40: aload_1
    //   41: invokevirtual 83	com/fasterxml/jackson/databind/introspect/AnnotatedField:getDeclaringClass	()Ljava/lang/Class;
    //   44: astore 4
    //   46: aload_3
    //   47: invokevirtual 83	com/fasterxml/jackson/databind/introspect/AnnotatedField:getDeclaringClass	()Ljava/lang/Class;
    //   50: astore 5
    //   52: aload 4
    //   54: aload 5
    //   56: if_acmpeq +31 -> 87
    //   59: aload 4
    //   61: aload 5
    //   63: invokevirtual 64	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   66: ifeq +8 -> 74
    //   69: aload_3
    //   70: astore_1
    //   71: goto +70 -> 141
    //   74: aload 5
    //   76: aload 4
    //   78: invokevirtual 64	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   81: ifeq +6 -> 87
    //   84: goto +57 -> 141
    //   87: new 66	java/lang/IllegalArgumentException
    //   90: dup
    //   91: new 67	java/lang/StringBuilder
    //   94: dup
    //   95: invokespecial 68	java/lang/StringBuilder:<init>	()V
    //   98: ldc 84
    //   100: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: aload_0
    //   104: invokevirtual 15	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder:getName	()Ljava/lang/String;
    //   107: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: ldc 71
    //   112: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: aload_1
    //   116: invokevirtual 85	com/fasterxml/jackson/databind/introspect/AnnotatedField:getFullName	()Ljava/lang/String;
    //   119: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   122: ldc 73
    //   124: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: aload_3
    //   128: invokevirtual 85	com/fasterxml/jackson/databind/introspect/AnnotatedField:getFullName	()Ljava/lang/String;
    //   131: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   137: invokespecial 75	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   140: athrow
    //   141: aload_2
    //   142: getfield 61	com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked:next	Lcom/fasterxml/jackson/databind/introspect/POJOPropertyBuilder$Linked;
    //   145: astore_2
    //   146: goto -118 -> 28
    //   149: aload_1
    //   150: areturn
    // Line number table:
    //   Java source line #509	-> byte code offset #0
    //   Java source line #510	-> byte code offset #7
    //   Java source line #513	-> byte code offset #9
    //   Java source line #514	-> byte code offset #20
    //   Java source line #515	-> byte code offset #28
    //   Java source line #516	-> byte code offset #32
    //   Java source line #517	-> byte code offset #40
    //   Java source line #518	-> byte code offset #46
    //   Java source line #519	-> byte code offset #52
    //   Java source line #520	-> byte code offset #59
    //   Java source line #521	-> byte code offset #69
    //   Java source line #522	-> byte code offset #71
    //   Java source line #524	-> byte code offset #74
    //   Java source line #525	-> byte code offset #84
    //   Java source line #528	-> byte code offset #87
    //   Java source line #529	-> byte code offset #116
    //   Java source line #515	-> byte code offset #141
    //   Java source line #531	-> byte code offset #149
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	151	0	this	POJOPropertyBuilder
    //   19	131	1	field	AnnotatedField
    //   27	119	2	next	Linked<AnnotatedField>
    //   39	89	3	nextField	AnnotatedField
    //   44	33	4	fieldClass	Class<?>
    //   50	25	5	nextClass	Class<?>
  }
  
  public AnnotatedParameter getConstructorParameter()
  {
    if (_ctorParameters == null) {
      return null;
    }
    







    Linked<AnnotatedParameter> curr = _ctorParameters;
    do {
      if ((((AnnotatedParameter)value).getOwner() instanceof AnnotatedConstructor)) {
        return (AnnotatedParameter)value;
      }
      curr = next;
    } while (curr != null);
    return (AnnotatedParameter)_ctorParameters.value;
  }
  
  public Iterator<AnnotatedParameter> getConstructorParameters()
  {
    if (_ctorParameters == null) {
      return ClassUtil.emptyIterator();
    }
    return new MemberIterator(_ctorParameters);
  }
  
  public AnnotatedMember getPrimaryMember()
  {
    if (_forSerialization) {
      return getAccessor();
    }
    AnnotatedMember m = getMutator();
    
    if (m == null) {
      m = getAccessor();
    }
    return m;
  }
  
  protected int _getterPriority(AnnotatedMethod m)
  {
    String name = m.getName();
    
    if ((name.startsWith("get")) && (name.length() > 3))
    {
      return 1;
    }
    if ((name.startsWith("is")) && (name.length() > 2)) {
      return 2;
    }
    return 3;
  }
  
  protected int _setterPriority(AnnotatedMethod m)
  {
    String name = m.getName();
    if ((name.startsWith("set")) && (name.length() > 3))
    {
      return 1;
    }
    return 2;
  }
  






  public Class<?>[] findViews()
  {
    (Class[])fromMemberAnnotations(new WithMember()
    {
      public Class<?>[] withMember(AnnotatedMember member) {
        return _annotationIntrospector.findViews(member);
      }
    });
  }
  


  public AnnotationIntrospector.ReferenceProperty findReferenceType()
  {
    AnnotationIntrospector.ReferenceProperty result = _referenceInfo;
    if (result != null) {
      if (result == NOT_REFEFERENCE_PROP) {
        return null;
      }
      return result;
    }
    result = (AnnotationIntrospector.ReferenceProperty)fromMemberAnnotations(new WithMember()
    {
      public AnnotationIntrospector.ReferenceProperty withMember(AnnotatedMember member) {
        return _annotationIntrospector.findReferenceType(member);
      }
    });
    _referenceInfo = (result == null ? NOT_REFEFERENCE_PROP : result);
    return result;
  }
  
  public boolean isTypeId()
  {
    Boolean b = (Boolean)fromMemberAnnotations(new WithMember()
    {
      public Boolean withMember(AnnotatedMember member) {
        return _annotationIntrospector.isTypeId(member);
      }
    });
    return (b != null) && (b.booleanValue());
  }
  
  protected Boolean _findRequired() {
    (Boolean)fromMemberAnnotations(new WithMember()
    {
      public Boolean withMember(AnnotatedMember member) {
        return _annotationIntrospector.hasRequiredMarker(member);
      }
    });
  }
  
  protected String _findDescription() {
    (String)fromMemberAnnotations(new WithMember()
    {
      public String withMember(AnnotatedMember member) {
        return _annotationIntrospector.findPropertyDescription(member);
      }
    });
  }
  
  protected Integer _findIndex() {
    (Integer)fromMemberAnnotations(new WithMember()
    {
      public Integer withMember(AnnotatedMember member) {
        return _annotationIntrospector.findPropertyIndex(member);
      }
    });
  }
  
  protected String _findDefaultValue() {
    (String)fromMemberAnnotations(new WithMember()
    {
      public String withMember(AnnotatedMember member) {
        return _annotationIntrospector.findPropertyDefaultValue(member);
      }
    });
  }
  
  public ObjectIdInfo findObjectIdInfo()
  {
    (ObjectIdInfo)fromMemberAnnotations(new WithMember()
    {
      public ObjectIdInfo withMember(AnnotatedMember member) {
        ObjectIdInfo info = _annotationIntrospector.findObjectIdInfo(member);
        if (info != null) {
          info = _annotationIntrospector.findObjectReferenceInfo(member, info);
        }
        return info;
      }
    });
  }
  
  public JsonInclude.Value findInclusion()
  {
    AnnotatedMember a = getAccessor();
    




    JsonInclude.Value v = _annotationIntrospector == null ? null : _annotationIntrospector.findPropertyInclusion(a);
    return v == null ? JsonInclude.Value.empty() : v;
  }
  
  public JsonProperty.Access findAccess() {
    (JsonProperty.Access)fromMemberAnnotationsExcept(new WithMember()
    {

      public JsonProperty.Access withMember(AnnotatedMember member) { return _annotationIntrospector.findPropertyAccess(member); } }, JsonProperty.Access.AUTO);
  }
  







  public void addField(AnnotatedField a, PropertyName name, boolean explName, boolean visible, boolean ignored)
  {
    _fields = new Linked(a, _fields, name, explName, visible, ignored);
  }
  
  public void addCtor(AnnotatedParameter a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
    _ctorParameters = new Linked(a, _ctorParameters, name, explName, visible, ignored);
  }
  
  public void addGetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
    _getters = new Linked(a, _getters, name, explName, visible, ignored);
  }
  
  public void addSetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
    _setters = new Linked(a, _setters, name, explName, visible, ignored);
  }
  




  public void addAll(POJOPropertyBuilder src)
  {
    _fields = merge(_fields, _fields);
    _ctorParameters = merge(_ctorParameters, _ctorParameters);
    _getters = merge(_getters, _getters);
    _setters = merge(_setters, _setters);
  }
  
  private static <T> Linked<T> merge(Linked<T> chain1, Linked<T> chain2)
  {
    if (chain1 == null) {
      return chain2;
    }
    if (chain2 == null) {
      return chain1;
    }
    return chain1.append(chain2);
  }
  










  public void removeIgnored()
  {
    _fields = _removeIgnored(_fields);
    _getters = _removeIgnored(_getters);
    _setters = _removeIgnored(_setters);
    _ctorParameters = _removeIgnored(_ctorParameters);
  }
  








  public JsonProperty.Access removeNonVisible(boolean inferMutators)
  {
    JsonProperty.Access acc = findAccess();
    if (acc == null) {
      acc = JsonProperty.Access.AUTO;
    }
    switch (10.$SwitchMap$com$fasterxml$jackson$annotation$JsonProperty$Access[acc.ordinal()])
    {
    case 1: 
      _setters = null;
      _ctorParameters = null;
      if (!_forSerialization) {
        _fields = null;
      }
      

      break;
    case 2: 
      break;
    case 3: 
      _getters = null;
      if (_forSerialization) {
        _fields = null;
      }
      break;
    case 4: 
    default: 
      _getters = _removeNonVisible(_getters);
      _ctorParameters = _removeNonVisible(_ctorParameters);
      
      if ((!inferMutators) || (_getters == null)) {
        _fields = _removeNonVisible(_fields);
        _setters = _removeNonVisible(_setters);
      }
      break; }
    return acc;
  }
  




  public void removeConstructors()
  {
    _ctorParameters = null;
  }
  





  public void trimByVisibility()
  {
    _fields = _trimByVisibility(_fields);
    _getters = _trimByVisibility(_getters);
    _setters = _trimByVisibility(_setters);
    _ctorParameters = _trimByVisibility(_ctorParameters);
  }
  

  public void mergeAnnotations(boolean forSerialization)
  {
    if (forSerialization) {
      if (_getters != null) {
        AnnotationMap ann = _mergeAnnotations(0, new Linked[] { _getters, _fields, _ctorParameters, _setters });
        _getters = _applyAnnotations(_getters, ann);
      } else if (_fields != null) {
        AnnotationMap ann = _mergeAnnotations(0, new Linked[] { _fields, _ctorParameters, _setters });
        _fields = _applyAnnotations(_fields, ann);
      }
    }
    else if (_ctorParameters != null) {
      AnnotationMap ann = _mergeAnnotations(0, new Linked[] { _ctorParameters, _setters, _fields, _getters });
      _ctorParameters = _applyAnnotations(_ctorParameters, ann);
    } else if (_setters != null) {
      AnnotationMap ann = _mergeAnnotations(0, new Linked[] { _setters, _fields, _getters });
      _setters = _applyAnnotations(_setters, ann);
    } else if (_fields != null) {
      AnnotationMap ann = _mergeAnnotations(0, new Linked[] { _fields, _getters });
      _fields = _applyAnnotations(_fields, ann);
    }
  }
  


  private AnnotationMap _mergeAnnotations(int index, Linked<? extends AnnotatedMember>... nodes)
  {
    AnnotationMap ann = _getAllAnnotations(nodes[index]);
    do { index++; if (index >= nodes.length) break;
    } while (nodes[index] == null);
    return AnnotationMap.merge(ann, _mergeAnnotations(index, nodes));
    

    return ann;
  }
  








  private <T extends AnnotatedMember> AnnotationMap _getAllAnnotations(Linked<T> node)
  {
    AnnotationMap ann = ((AnnotatedMember)value).getAllAnnotations();
    if (next != null) {
      ann = AnnotationMap.merge(ann, _getAllAnnotations(next));
    }
    return ann;
  }
  









  private <T extends AnnotatedMember> Linked<T> _applyAnnotations(Linked<T> node, AnnotationMap ann)
  {
    T value = (AnnotatedMember)((AnnotatedMember)value).withAnnotations(ann);
    if (next != null) {
      node = node.withNext(_applyAnnotations(next, ann));
    }
    return node.withValue(value);
  }
  
  private <T> Linked<T> _removeIgnored(Linked<T> node)
  {
    if (node == null) {
      return node;
    }
    return node.withoutIgnored();
  }
  
  private <T> Linked<T> _removeNonVisible(Linked<T> node)
  {
    if (node == null) {
      return node;
    }
    return node.withoutNonVisible();
  }
  
  private <T> Linked<T> _trimByVisibility(Linked<T> node)
  {
    if (node == null) {
      return node;
    }
    return node.trimByVisibility();
  }
  
  private <T> boolean _anyExplicits(Linked<T> n)
  {
    for (; 
        




        n != null; n = next) {
      if ((name != null) && (name.hasSimpleName())) {
        return true;
      }
    }
    return false;
  }
  
  private <T> boolean _anyExplicitNames(Linked<T> n)
  {
    for (; n != null; n = next) {
      if ((name != null) && (isNameExplicit)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean anyVisible() {
    return (_anyVisible(_fields)) || 
      (_anyVisible(_getters)) || 
      (_anyVisible(_setters)) || 
      (_anyVisible(_ctorParameters));
  }
  
  private <T> boolean _anyVisible(Linked<T> n)
  {
    for (; 
        n != null; n = next) {
      if (isVisible) {
        return true;
      }
    }
    return false;
  }
  
  public boolean anyIgnorals() {
    return (_anyIgnorals(_fields)) || 
      (_anyIgnorals(_getters)) || 
      (_anyIgnorals(_setters)) || 
      (_anyIgnorals(_ctorParameters));
  }
  
  private <T> boolean _anyIgnorals(Linked<T> n)
  {
    for (; 
        n != null; n = next) {
      if (isMarkedIgnored) {
        return true;
      }
    }
    return false;
  }
  






  public Set<PropertyName> findExplicitNames()
  {
    Set<PropertyName> renamed = null;
    renamed = _findExplicitNames(_fields, renamed);
    renamed = _findExplicitNames(_getters, renamed);
    renamed = _findExplicitNames(_setters, renamed);
    renamed = _findExplicitNames(_ctorParameters, renamed);
    if (renamed == null) {
      return Collections.emptySet();
    }
    return renamed;
  }
  








  public Collection<POJOPropertyBuilder> explode(Collection<PropertyName> newNames)
  {
    HashMap<PropertyName, POJOPropertyBuilder> props = new HashMap();
    _explode(newNames, props, _fields);
    _explode(newNames, props, _getters);
    _explode(newNames, props, _setters);
    _explode(newNames, props, _ctorParameters);
    return props.values();
  }
  



  private void _explode(Collection<PropertyName> newNames, Map<PropertyName, POJOPropertyBuilder> props, Linked<?> accessors)
  {
    Linked<?> firstAcc = accessors;
    for (Linked<?> node = accessors; node != null; node = next) {
      PropertyName name = name;
      if ((!isNameExplicit) || (name == null))
      {
        if (isVisible)
        {


          throw new IllegalStateException("Conflicting/ambiguous property name definitions (implicit name '" + _name + "'): found multiple explicit names: " + newNames + ", but also implicit accessor: " + node);
        }
      }
      else {
        POJOPropertyBuilder prop = (POJOPropertyBuilder)props.get(name);
        if (prop == null) {
          prop = new POJOPropertyBuilder(_config, _annotationIntrospector, _forSerialization, _internalName, name);
          
          props.put(name, prop);
        }
        
        if (firstAcc == _fields) {
          Linked<AnnotatedField> n2 = node;
          _fields = n2.withNext(_fields);
        } else if (firstAcc == _getters) {
          Linked<AnnotatedMethod> n2 = node;
          _getters = n2.withNext(_getters);
        } else if (firstAcc == _setters) {
          Linked<AnnotatedMethod> n2 = node;
          _setters = n2.withNext(_setters);
        } else if (firstAcc == _ctorParameters) {
          Linked<AnnotatedParameter> n2 = node;
          _ctorParameters = n2.withNext(_ctorParameters);
        } else {
          throw new IllegalStateException("Internal error: mismatched accessors, property: " + this);
        }
      }
    }
  }
  
  private Set<PropertyName> _findExplicitNames(Linked<? extends AnnotatedMember> node, Set<PropertyName> renamed)
  {
    for (; node != null; node = next)
    {





      if ((isNameExplicit) && (name != null))
      {

        if (renamed == null) {
          renamed = new HashSet();
        }
        renamed.add(name);
      } }
    return renamed;
  }
  


  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("[Property '").append(_name)
      .append("'; ctors: ").append(_ctorParameters)
      .append(", field(s): ").append(_fields)
      .append(", getter(s): ").append(_getters)
      .append(", setter(s): ").append(_setters);
    
    sb.append("]");
    return sb.toString();
  }
  










  protected <T> T fromMemberAnnotations(WithMember<T> func)
  {
    T result = null;
    if (_annotationIntrospector != null) {
      if (_forSerialization) {
        if (_getters != null) {
          result = func.withMember((AnnotatedMember)_getters.value);
        }
      } else {
        if (_ctorParameters != null) {
          result = func.withMember((AnnotatedMember)_ctorParameters.value);
        }
        if ((result == null) && (_setters != null)) {
          result = func.withMember((AnnotatedMember)_setters.value);
        }
      }
      if ((result == null) && (_fields != null)) {
        result = func.withMember((AnnotatedMember)_fields.value);
      }
    }
    return result;
  }
  
  protected <T> T fromMemberAnnotationsExcept(WithMember<T> func, T defaultValue)
  {
    if (_annotationIntrospector == null) {
      return null;
    }
    


    if (_forSerialization) {
      if (_getters != null) {
        T result = func.withMember((AnnotatedMember)_getters.value);
        if ((result != null) && (result != defaultValue)) {
          return result;
        }
      }
      if (_fields != null) {
        T result = func.withMember((AnnotatedMember)_fields.value);
        if ((result != null) && (result != defaultValue)) {
          return result;
        }
      }
      if (_ctorParameters != null) {
        T result = func.withMember((AnnotatedMember)_ctorParameters.value);
        if ((result != null) && (result != defaultValue)) {
          return result;
        }
      }
      if (_setters != null) {
        T result = func.withMember((AnnotatedMember)_setters.value);
        if ((result != null) && (result != defaultValue)) {
          return result;
        }
      }
      return null;
    }
    if (_ctorParameters != null) {
      T result = func.withMember((AnnotatedMember)_ctorParameters.value);
      if ((result != null) && (result != defaultValue)) {
        return result;
      }
    }
    if (_setters != null) {
      T result = func.withMember((AnnotatedMember)_setters.value);
      if ((result != null) && (result != defaultValue)) {
        return result;
      }
    }
    if (_fields != null) {
      T result = func.withMember((AnnotatedMember)_fields.value);
      if ((result != null) && (result != defaultValue)) {
        return result;
      }
    }
    if (_getters != null) {
      T result = func.withMember((AnnotatedMember)_getters.value);
      if ((result != null) && (result != defaultValue)) {
        return result;
      }
    }
    return null;
  }
  



  private static abstract interface WithMember<T>
  {
    public abstract T withMember(AnnotatedMember paramAnnotatedMember);
  }
  


  protected static class MemberIterator<T extends AnnotatedMember>
    implements Iterator<T>
  {
    private POJOPropertyBuilder.Linked<T> next;
    


    public MemberIterator(POJOPropertyBuilder.Linked<T> first)
    {
      next = first;
    }
    
    public boolean hasNext()
    {
      return next != null;
    }
    
    public T next()
    {
      if (next == null) throw new NoSuchElementException();
      T result = (AnnotatedMember)next.value;
      next = next.next;
      return result;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  

  protected static final class Linked<T>
  {
    public final T value;
    
    public final Linked<T> next;
    
    public final PropertyName name;
    
    public final boolean isNameExplicit;
    
    public final boolean isVisible;
    
    public final boolean isMarkedIgnored;
    

    public Linked(T v, Linked<T> n, PropertyName name, boolean explName, boolean visible, boolean ignored)
    {
      value = v;
      next = n;
      
      this.name = ((name == null) || (name.isEmpty()) ? null : name);
      
      if (explName) {
        if (this.name == null) {
          throw new IllegalArgumentException("Cannot pass true for 'explName' if name is null/empty");
        }
        

        if (!name.hasSimpleName()) {
          explName = false;
        }
      }
      
      isNameExplicit = explName;
      isVisible = visible;
      isMarkedIgnored = ignored;
    }
    
    public Linked<T> withoutNext() {
      if (next == null) {
        return this;
      }
      return new Linked(value, null, name, isNameExplicit, isVisible, isMarkedIgnored);
    }
    
    public Linked<T> withValue(T newValue) {
      if (newValue == value) {
        return this;
      }
      return new Linked(newValue, next, name, isNameExplicit, isVisible, isMarkedIgnored);
    }
    
    public Linked<T> withNext(Linked<T> newNext) {
      if (newNext == next) {
        return this;
      }
      return new Linked(value, newNext, name, isNameExplicit, isVisible, isMarkedIgnored);
    }
    
    public Linked<T> withoutIgnored() {
      if (isMarkedIgnored) {
        return next == null ? null : next.withoutIgnored();
      }
      if (next != null) {
        Linked<T> newNext = next.withoutIgnored();
        if (newNext != next) {
          return withNext(newNext);
        }
      }
      return this;
    }
    
    public Linked<T> withoutNonVisible() {
      Linked<T> newNext = next == null ? null : next.withoutNonVisible();
      return isVisible ? withNext(newNext) : newNext;
    }
    



    protected Linked<T> append(Linked<T> appendable)
    {
      if (next == null) {
        return withNext(appendable);
      }
      return withNext(next.append(appendable));
    }
    
    public Linked<T> trimByVisibility() {
      if (next == null) {
        return this;
      }
      Linked<T> newNext = next.trimByVisibility();
      if (name != null) {
        if (name == null) {
          return withNext(null);
        }
        
        return withNext(newNext);
      }
      if (name != null) {
        return newNext;
      }
      
      if (isVisible == isVisible) {
        return withNext(newNext);
      }
      return isVisible ? withNext(null) : newNext;
    }
    
    public String toString()
    {
      String msg = String.format("%s[visible=%b,ignore=%b,explicitName=%b]", new Object[] {value
        .toString(), Boolean.valueOf(isVisible), Boolean.valueOf(isMarkedIgnored), Boolean.valueOf(isNameExplicit) });
      if (next != null) {
        msg = msg + ", " + next.toString();
      }
      return msg;
    }
  }
}
