package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;











class FactoryBasedEnumDeserializer
  extends StdDeserializer<Object>
  implements ContextualDeserializer
{
  private static final long serialVersionUID = 1L;
  protected final JavaType _inputType;
  protected final boolean _hasArgs;
  protected final AnnotatedMethod _factory;
  protected final JsonDeserializer<?> _deser;
  protected final ValueInstantiator _valueInstantiator;
  protected final SettableBeanProperty[] _creatorProps;
  private transient PropertyBasedCreator _propCreator;
  
  public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f, JavaType paramType, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps)
  {
    super(cls);
    _factory = f;
    _hasArgs = true;
    
    _inputType = (paramType.hasRawClass(String.class) ? null : paramType);
    _deser = null;
    _valueInstantiator = valueInstantiator;
    _creatorProps = creatorProps;
  }
  



  public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f)
  {
    super(cls);
    _factory = f;
    _hasArgs = false;
    _inputType = null;
    _deser = null;
    _valueInstantiator = null;
    _creatorProps = null;
  }
  
  protected FactoryBasedEnumDeserializer(FactoryBasedEnumDeserializer base, JsonDeserializer<?> deser)
  {
    super(_valueClass);
    _inputType = _inputType;
    _factory = _factory;
    _hasArgs = _hasArgs;
    _valueInstantiator = _valueInstantiator;
    _creatorProps = _creatorProps;
    
    _deser = deser;
  }
  


  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    if ((_deser == null) && (_inputType != null) && (_creatorProps == null)) {
      return new FactoryBasedEnumDeserializer(this, ctxt
        .findContextualValueDeserializer(_inputType, property));
    }
    return this;
  }
  
  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return Boolean.FALSE;
  }
  
  public boolean isCachable()
  {
    return true;
  }
  
  public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
  {
    Object value = null;
    if (_deser != null) {
      value = _deser.deserialize(p, ctxt);
    } else if (_hasArgs) {
      JsonToken curr = p.currentToken();
      

      if ((curr == JsonToken.VALUE_STRING) || (curr == JsonToken.FIELD_NAME)) {
        value = p.getText();
      } else { if ((_creatorProps != null) && (p.isExpectedStartObjectToken())) {
          if (_propCreator == null) {
            _propCreator = PropertyBasedCreator.construct(ctxt, _valueInstantiator, _creatorProps, ctxt
              .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
          }
          p.nextToken();
          return deserializeEnumUsingPropertyBased(p, ctxt, _propCreator);
        }
        value = p.getValueAsString();
      }
    } else {
      p.skipChildren();
      try {
        return _factory.call();
      } catch (Exception e) {
        Throwable t = ClassUtil.throwRootCauseIfIOE(e);
        return ctxt.handleInstantiationProblem(_valueClass, null, t);
      }
    }
    try {
      return _factory.callOnWith(_valueClass, new Object[] { value });
    } catch (Exception e) {
      Throwable t = ClassUtil.throwRootCauseIfIOE(e);
      
      if ((ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) && ((t instanceof IllegalArgumentException)))
      {
        return null;
      }
      return ctxt.handleInstantiationProblem(_valueClass, value, t);
    }
  }
  
  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException
  {
    if (_deser == null) {
      return deserialize(p, ctxt);
    }
    return typeDeserializer.deserializeTypedFromAny(p, ctxt);
  }
  
  protected Object deserializeEnumUsingPropertyBased(JsonParser p, DeserializationContext ctxt, PropertyBasedCreator creator)
    throws IOException
  {
    // Byte code:
    //   0: aload_3
    //   1: aload_1
    //   2: aload_2
    //   3: aconst_null
    //   4: invokevirtual 40	com/fasterxml/jackson/databind/deser/impl/PropertyBasedCreator:startBuilding	(Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;Lcom/fasterxml/jackson/databind/deser/impl/ObjectIdReader;)Lcom/fasterxml/jackson/databind/deser/impl/PropertyValueBuffer;
    //   7: astore 4
    //   9: aload_1
    //   10: invokevirtual 16	com/fasterxml/jackson/core/JsonParser:currentToken	()Lcom/fasterxml/jackson/core/JsonToken;
    //   13: astore 5
    //   15: aload 5
    //   17: getstatic 18	com/fasterxml/jackson/core/JsonToken:FIELD_NAME	Lcom/fasterxml/jackson/core/JsonToken;
    //   20: if_acmpne +68 -> 88
    //   23: aload_1
    //   24: invokevirtual 41	com/fasterxml/jackson/core/JsonParser:getCurrentName	()Ljava/lang/String;
    //   27: astore 6
    //   29: aload_1
    //   30: invokevirtual 25	com/fasterxml/jackson/core/JsonParser:nextToken	()Lcom/fasterxml/jackson/core/JsonToken;
    //   33: pop
    //   34: aload_3
    //   35: aload 6
    //   37: invokevirtual 42	com/fasterxml/jackson/databind/deser/impl/PropertyBasedCreator:findCreatorProperty	(Ljava/lang/String;)Lcom/fasterxml/jackson/databind/deser/SettableBeanProperty;
    //   40: astore 7
    //   42: aload 7
    //   44: ifnull +22 -> 66
    //   47: aload 4
    //   49: aload 7
    //   51: aload_0
    //   52: aload_1
    //   53: aload_2
    //   54: aload 7
    //   56: invokevirtual 43	com/fasterxml/jackson/databind/deser/std/FactoryBasedEnumDeserializer:_deserializeWithErrorWrapping	(Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;Lcom/fasterxml/jackson/databind/deser/SettableBeanProperty;)Ljava/lang/Object;
    //   59: invokevirtual 44	com/fasterxml/jackson/databind/deser/impl/PropertyValueBuffer:assignParameter	(Lcom/fasterxml/jackson/databind/deser/SettableBeanProperty;Ljava/lang/Object;)Z
    //   62: pop
    //   63: goto +16 -> 79
    //   66: aload 4
    //   68: aload 6
    //   70: invokevirtual 45	com/fasterxml/jackson/databind/deser/impl/PropertyValueBuffer:readIdProperty	(Ljava/lang/String;)Z
    //   73: ifeq +6 -> 79
    //   76: goto +3 -> 79
    //   79: aload_1
    //   80: invokevirtual 25	com/fasterxml/jackson/core/JsonParser:nextToken	()Lcom/fasterxml/jackson/core/JsonToken;
    //   83: astore 5
    //   85: goto -70 -> 15
    //   88: aload_3
    //   89: aload_2
    //   90: aload 4
    //   92: invokevirtual 46	com/fasterxml/jackson/databind/deser/impl/PropertyBasedCreator:build	(Lcom/fasterxml/jackson/databind/DeserializationContext;Lcom/fasterxml/jackson/databind/deser/impl/PropertyValueBuffer;)Ljava/lang/Object;
    //   95: areturn
    // Line number table:
    //   Java source line #162	-> byte code offset #0
    //   Java source line #164	-> byte code offset #9
    //   Java source line #165	-> byte code offset #15
    //   Java source line #166	-> byte code offset #23
    //   Java source line #167	-> byte code offset #29
    //   Java source line #169	-> byte code offset #34
    //   Java source line #170	-> byte code offset #42
    //   Java source line #171	-> byte code offset #47
    //   Java source line #172	-> byte code offset #63
    //   Java source line #174	-> byte code offset #66
    //   Java source line #175	-> byte code offset #76
    //   Java source line #165	-> byte code offset #79
    //   Java source line #178	-> byte code offset #88
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	96	0	this	FactoryBasedEnumDeserializer
    //   0	96	1	p	JsonParser
    //   0	96	2	ctxt	DeserializationContext
    //   0	96	3	creator	PropertyBasedCreator
    //   7	84	4	buffer	PropertyValueBuffer
    //   13	71	5	t	JsonToken
    //   27	42	6	propName	String
    //   40	15	7	creatorProp	SettableBeanProperty
  }
  
  protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop)
    throws IOException
  {
    try
    {
      return prop.deserialize(p, ctxt);
    } catch (Exception e) {
      return wrapAndThrow(e, handledType(), prop.getName(), ctxt);
    }
  }
  
  protected Object wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt)
    throws IOException
  {
    throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, fieldName);
  }
  
  private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException
  {
    t = ClassUtil.getRootCause(t);
    
    ClassUtil.throwIfError(t);
    boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
    
    if ((t instanceof IOException)) {
      if ((!wrap) || (!(t instanceof JsonProcessingException))) {
        throw ((IOException)t);
      }
    } else if (!wrap) {
      ClassUtil.throwIfRTE(t);
    }
    return t;
  }
}
