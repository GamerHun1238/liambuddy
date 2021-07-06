package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public abstract class StdDeserializer<T> extends JsonDeserializer<T> implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected static final int F_MASK_INT_COERCIONS = DeserializationFeature.USE_BIG_INTEGER_FOR_INTS
    .getMask() | DeserializationFeature.USE_LONG_FOR_INTS
    .getMask();
  

  protected static final int F_MASK_ACCEPT_ARRAYS = DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS
    .getMask() | DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT
    .getMask();
  


  protected final Class<?> _valueClass;
  


  protected final JavaType _valueType;
  


  protected StdDeserializer(Class<?> vc)
  {
    _valueClass = vc;
    _valueType = null;
  }
  
  protected StdDeserializer(JavaType valueType)
  {
    _valueClass = (valueType == null ? Object.class : valueType.getRawClass());
    _valueType = valueType;
  }
  





  protected StdDeserializer(StdDeserializer<?> src)
  {
    _valueClass = _valueClass;
    _valueType = _valueType;
  }
  





  public Class<?> handledType()
  {
    return _valueClass;
  }
  






  @Deprecated
  public final Class<?> getValueClass()
  {
    return _valueClass;
  }
  
  public JavaType getValueType()
  {
    return _valueType;
  }
  











  public JavaType getValueType(DeserializationContext ctxt)
  {
    if (_valueType != null) {
      return _valueType;
    }
    return ctxt.constructType(_valueClass);
  }
  





  protected boolean isDefaultDeserializer(JsonDeserializer<?> deserializer)
  {
    return ClassUtil.isJacksonStdImpl(deserializer);
  }
  
  protected boolean isDefaultKeyDeserializer(KeyDeserializer keyDeser) {
    return ClassUtil.isJacksonStdImpl(keyDeser);
  }
  











  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return typeDeserializer.deserializeTypedFromAny(p, ctxt);
  }
  







  protected final boolean _parseBooleanPrimitive(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonToken t = p.getCurrentToken();
    if (t == JsonToken.VALUE_TRUE) return true;
    if (t == JsonToken.VALUE_FALSE) return false;
    if (t == JsonToken.VALUE_NULL) {
      _verifyNullForPrimitive(ctxt);
      return false;
    }
    

    if (t == JsonToken.VALUE_NUMBER_INT) {
      return _parseBooleanFromInt(p, ctxt);
    }
    
    if (t == JsonToken.VALUE_STRING) {
      String text = p.getText().trim();
      
      if (("true".equals(text)) || ("True".equals(text))) {
        return true;
      }
      if (("false".equals(text)) || ("False".equals(text))) {
        return false;
      }
      if (_isEmptyOrTextualNull(text)) {
        _verifyNullForPrimitiveCoercion(ctxt, text);
        return false;
      }
      Boolean b = (Boolean)ctxt.handleWeirdStringValue(_valueClass, text, "only \"true\" or \"false\" recognized", new Object[0]);
      
      return Boolean.TRUE.equals(b);
    }
    
    if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
      p.nextToken();
      boolean parsed = _parseBooleanPrimitive(p, ctxt);
      _verifyEndArrayForSingle(p, ctxt);
      return parsed;
    }
    
    return ((Boolean)ctxt.handleUnexpectedToken(_valueClass, p)).booleanValue();
  }
  




  protected boolean _parseBooleanFromInt(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    _verifyNumberForScalarCoercion(ctxt, p);
    

    return !"0".equals(p.getText());
  }
  
  protected final byte _parseBytePrimitive(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    int value = _parseIntPrimitive(p, ctxt);
    
    if (_byteOverflow(value)) {
      Number v = (Number)ctxt.handleWeirdStringValue(_valueClass, String.valueOf(value), "overflow, value cannot be represented as 8-bit value", new Object[0]);
      
      return _nonNullNumber(v).byteValue();
    }
    return (byte)value;
  }
  
  protected final short _parseShortPrimitive(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    int value = _parseIntPrimitive(p, ctxt);
    
    if (_shortOverflow(value)) {
      Number v = (Number)ctxt.handleWeirdStringValue(_valueClass, String.valueOf(value), "overflow, value cannot be represented as 16-bit value", new Object[0]);
      
      return _nonNullNumber(v).shortValue();
    }
    return (short)value;
  }
  
  protected final int _parseIntPrimitive(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
      return p.getIntValue();
    }
    switch (p.getCurrentTokenId()) {
    case 6: 
      String text = p.getText().trim();
      if (_isEmptyOrTextualNull(text)) {
        _verifyNullForPrimitiveCoercion(ctxt, text);
        return 0;
      }
      return _parseIntPrimitive(ctxt, text);
    case 8: 
      if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
        _failDoubleToIntCoercion(p, ctxt, "int");
      }
      return p.getValueAsInt();
    case 11: 
      _verifyNullForPrimitive(ctxt);
      return 0;
    case 3: 
      if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        p.nextToken();
        int parsed = _parseIntPrimitive(p, ctxt);
        _verifyEndArrayForSingle(p, ctxt);
        return parsed;
      }
      
      break;
    }
    
    return ((Number)ctxt.handleUnexpectedToken(_valueClass, p)).intValue();
  }
  

  protected final int _parseIntPrimitive(DeserializationContext ctxt, String text)
    throws IOException
  {
    try
    {
      if (text.length() > 9) {
        long l = Long.parseLong(text);
        if (_intOverflow(l)) {
          Number v = (Number)ctxt.handleWeirdStringValue(_valueClass, text, "Overflow: numeric value (%s) out of range of int (%d -%d)", new Object[] { text, 
          
            Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE) });
          return _nonNullNumber(v).intValue();
        }
        return (int)l;
      }
      return NumberInput.parseInt(text);
    } catch (IllegalArgumentException iae) {
      Number v = (Number)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid int value", new Object[0]);
      
      return _nonNullNumber(v).intValue();
    }
  }
  
  protected final long _parseLongPrimitive(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
      return p.getLongValue();
    }
    switch (p.getCurrentTokenId()) {
    case 6: 
      String text = p.getText().trim();
      if (_isEmptyOrTextualNull(text)) {
        _verifyNullForPrimitiveCoercion(ctxt, text);
        return 0L;
      }
      return _parseLongPrimitive(ctxt, text);
    case 8: 
      if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
        _failDoubleToIntCoercion(p, ctxt, "long");
      }
      return p.getValueAsLong();
    case 11: 
      _verifyNullForPrimitive(ctxt);
      return 0L;
    case 3: 
      if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        p.nextToken();
        long parsed = _parseLongPrimitive(p, ctxt);
        _verifyEndArrayForSingle(p, ctxt);
        return parsed;
      }
      break;
    }
    return ((Number)ctxt.handleUnexpectedToken(_valueClass, p)).longValue();
  }
  

  protected final long _parseLongPrimitive(DeserializationContext ctxt, String text)
    throws IOException
  {
    try
    {
      return NumberInput.parseLong(text);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {
      Number v = (Number)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid long value", new Object[0]);
      
      return _nonNullNumber(v).longValue();
    }
  }
  
  protected final float _parseFloatPrimitive(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
      return p.getFloatValue();
    }
    switch (p.getCurrentTokenId()) {
    case 6: 
      String text = p.getText().trim();
      if (_isEmptyOrTextualNull(text)) {
        _verifyNullForPrimitiveCoercion(ctxt, text);
        return 0.0F;
      }
      return _parseFloatPrimitive(ctxt, text);
    case 7: 
      return p.getFloatValue();
    case 11: 
      _verifyNullForPrimitive(ctxt);
      return 0.0F;
    case 3: 
      if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        p.nextToken();
        float parsed = _parseFloatPrimitive(p, ctxt);
        _verifyEndArrayForSingle(p, ctxt);
        return parsed;
      }
      break;
    }
    
    return ((Number)ctxt.handleUnexpectedToken(_valueClass, p)).floatValue();
  }
  



  protected final float _parseFloatPrimitive(DeserializationContext ctxt, String text)
    throws IOException
  {
    switch (text.charAt(0)) {
    case 'I': 
      if (_isPosInf(text)) {
        return Float.POSITIVE_INFINITY;
      }
      break;
    case 'N': 
      if (_isNaN(text)) return NaN.0F;
      break;
    case '-': 
      if (_isNegInf(text)) {
        return Float.NEGATIVE_INFINITY;
      }
      break;
    }
    try {
      return Float.parseFloat(text);
    } catch (IllegalArgumentException localIllegalArgumentException) {
      Number v = (Number)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid float value", new Object[0]);
      
      return _nonNullNumber(v).floatValue();
    }
  }
  
  protected final double _parseDoublePrimitive(JsonParser p, DeserializationContext ctxt) throws IOException
  {
    if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
      return p.getDoubleValue();
    }
    switch (p.getCurrentTokenId()) {
    case 6: 
      String text = p.getText().trim();
      if (_isEmptyOrTextualNull(text)) {
        _verifyNullForPrimitiveCoercion(ctxt, text);
        return 0.0D;
      }
      return _parseDoublePrimitive(ctxt, text);
    case 7: 
      return p.getDoubleValue();
    case 11: 
      _verifyNullForPrimitive(ctxt);
      return 0.0D;
    case 3: 
      if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        p.nextToken();
        double parsed = _parseDoublePrimitive(p, ctxt);
        _verifyEndArrayForSingle(p, ctxt);
        return parsed;
      }
      break;
    }
    
    return ((Number)ctxt.handleUnexpectedToken(_valueClass, p)).doubleValue();
  }
  



  protected final double _parseDoublePrimitive(DeserializationContext ctxt, String text)
    throws IOException
  {
    switch (text.charAt(0)) {
    case 'I': 
      if (_isPosInf(text)) {
        return Double.POSITIVE_INFINITY;
      }
      break;
    case 'N': 
      if (_isNaN(text)) {
        return NaN.0D;
      }
      break;
    case '-': 
      if (_isNegInf(text)) {
        return Double.NEGATIVE_INFINITY;
      }
      break;
    }
    try {
      return parseDouble(text);
    } catch (IllegalArgumentException localIllegalArgumentException) {
      Number v = (Number)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid double value (as String to convert)", new Object[0]);
      
      return _nonNullNumber(v).doubleValue();
    }
  }
  
  protected Date _parseDate(JsonParser p, DeserializationContext ctxt) throws IOException
  {
    switch (p.getCurrentTokenId()) {
    case 6: 
      return _parseDate(p.getText().trim(), ctxt);
    case 7: 
      long ts;
      try
      {
        ts = p.getLongValue();
      }
      catch (JsonParseException|InputCoercionException e) {
        long ts;
        Number v = (Number)ctxt.handleWeirdNumberValue(_valueClass, p.getNumberValue(), "not a valid 64-bit long for creating `java.util.Date`", new Object[0]);
        
        ts = v.longValue();
      }
      return new Date(ts);
    
    case 11: 
      return (Date)getNullValue(ctxt);
    case 3: 
      return _parseDateFromArray(p, ctxt);
    }
    return (Date)ctxt.handleUnexpectedToken(_valueClass, p);
  }
  

  protected Date _parseDateFromArray(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonToken t;
    if (ctxt.hasSomeOfFeatures(F_MASK_ACCEPT_ARRAYS)) {
      JsonToken t = p.nextToken();
      if ((t == JsonToken.END_ARRAY) && 
        (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT))) {
        return (Date)getNullValue(ctxt);
      }
      
      if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        Date parsed = _parseDate(p, ctxt);
        _verifyEndArrayForSingle(p, ctxt);
        return parsed;
      }
    } else {
      t = p.getCurrentToken();
    }
    return (Date)ctxt.handleUnexpectedToken(_valueClass, t, p, null, new Object[0]);
  }
  



  protected Date _parseDate(String value, DeserializationContext ctxt)
    throws IOException
  {
    try
    {
      if (_isEmptyOrTextualNull(value)) {
        return (Date)getNullValue(ctxt);
      }
      return ctxt.parseDate(value);
    } catch (IllegalArgumentException iae) {}
    return (Date)ctxt.handleWeirdStringValue(_valueClass, value, "not a valid representation (error: %s)", tmp36_33);
  }
  







  protected static final double parseDouble(String numStr)
    throws NumberFormatException
  {
    if ("2.2250738585072012e-308".equals(numStr)) {
      return 2.2250738585072014E-308D;
    }
    return Double.parseDouble(numStr);
  }
  





  protected final String _parseString(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonToken t = p.getCurrentToken();
    if (t == JsonToken.VALUE_STRING) {
      return p.getText();
    }
    
    if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
      Object ob = p.getEmbeddedObject();
      if ((ob instanceof byte[])) {
        return ctxt.getBase64Variant().encode((byte[])ob, false);
      }
      if (ob == null) {
        return null;
      }
      
      return ob.toString();
    }
    











    String value = p.getValueAsString();
    if (value != null) {
      return value;
    }
    return (String)ctxt.handleUnexpectedToken(String.class, p);
  }
  







  protected T _deserializeFromEmpty(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonToken t = p.getCurrentToken();
    if (t == JsonToken.START_ARRAY) {
      if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
        t = p.nextToken();
        if (t == JsonToken.END_ARRAY) {
          return null;
        }
        return ctxt.handleUnexpectedToken(handledType(), p);
      }
    } else if ((t == JsonToken.VALUE_STRING) && 
      (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT))) {
      String str = p.getText().trim();
      if (str.isEmpty()) {
        return null;
      }
    }
    
    return ctxt.handleUnexpectedToken(handledType(), p);
  }
  






  protected boolean _hasTextualNull(String value)
  {
    return "null".equals(value);
  }
  


  protected boolean _isEmptyOrTextualNull(String value)
  {
    return (value.isEmpty()) || ("null".equals(value));
  }
  
  protected final boolean _isNegInf(String text) {
    return ("-Infinity".equals(text)) || ("-INF".equals(text));
  }
  
  protected final boolean _isPosInf(String text) {
    return ("Infinity".equals(text)) || ("INF".equals(text));
  }
  
  protected final boolean _isNaN(String text) { return "NaN".equals(text); }
  











  protected T _deserializeFromArray(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonToken t;
    










    if (ctxt.hasSomeOfFeatures(F_MASK_ACCEPT_ARRAYS)) {
      JsonToken t = p.nextToken();
      if ((t == JsonToken.END_ARRAY) && 
        (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT))) {
        return getNullValue(ctxt);
      }
      
      if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        T parsed = deserialize(p, ctxt);
        if (p.nextToken() != JsonToken.END_ARRAY) {
          handleMissingEndArrayForSingle(p, ctxt);
        }
        return parsed;
      }
    } else {
      t = p.getCurrentToken();
    }
    
    T result = ctxt.handleUnexpectedToken(getValueType(ctxt), p.getCurrentToken(), p, null, new Object[0]);
    return result;
  }
  









  protected T _deserializeWrappedValue(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.hasToken(JsonToken.START_ARRAY)) {
      String msg = String.format("Cannot deserialize instance of %s out of %s token: nested Arrays not allowed with %s", new Object[] {
      
        ClassUtil.nameOf(_valueClass), JsonToken.START_ARRAY, "DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS" });
      

      T result = ctxt.handleUnexpectedToken(getValueType(ctxt), p.getCurrentToken(), p, msg, new Object[0]);
      return result;
    }
    return deserialize(p, ctxt);
  }
  






  protected void _failDoubleToIntCoercion(JsonParser p, DeserializationContext ctxt, String type)
    throws IOException
  {
    ctxt.reportInputMismatch(handledType(), "Cannot coerce a floating-point value ('%s') into %s (enable `DeserializationFeature.ACCEPT_FLOAT_AS_INT` to allow)", new Object[] {p
    
      .getValueAsString(), type });
  }
  










  protected Object _coerceIntegral(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    int feats = ctxt.getDeserializationFeatures();
    if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
      return p.getBigIntegerValue();
    }
    if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
      return Long.valueOf(p.getLongValue());
    }
    return p.getBigIntegerValue();
  }
  





  protected Object _coerceNullToken(DeserializationContext ctxt, boolean isPrimitive)
    throws JsonMappingException
  {
    if (isPrimitive) {
      _verifyNullForPrimitive(ctxt);
    }
    return getNullValue(ctxt);
  }
  



  protected Object _coerceTextualNull(DeserializationContext ctxt, boolean isPrimitive)
    throws JsonMappingException
  {
    boolean enable;
    


    if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
      Enum<?> feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
      enable = true; } else { boolean enable;
      if ((isPrimitive) && (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))) {
        Enum<?> feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
        enable = false;
      } else {
        return getNullValue(ctxt); } }
    boolean enable;
    Enum<?> feat; _reportFailedNullCoerce(ctxt, enable, feat, "String \"null\"");
    return null;
  }
  



  protected Object _coerceEmptyString(DeserializationContext ctxt, boolean isPrimitive)
    throws JsonMappingException
  {
    boolean enable;
    


    if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
      Enum<?> feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
      enable = true; } else { boolean enable;
      if ((isPrimitive) && (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))) {
        Enum<?> feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
        enable = false;
      } else {
        return getNullValue(ctxt); } }
    boolean enable;
    Enum<?> feat; _reportFailedNullCoerce(ctxt, enable, feat, "empty String (\"\")");
    return null;
  }
  
  protected final void _verifyNullForPrimitive(DeserializationContext ctxt)
    throws JsonMappingException
  {
    if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
      ctxt.reportInputMismatch(this, "Cannot coerce `null` %s (disable `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to allow)", new Object[] {
      
        _coercedTypeDesc() });
    }
  }
  


  protected final void _verifyNullForPrimitiveCoercion(DeserializationContext ctxt, String str)
    throws JsonMappingException
  {
    boolean enable;
    
    if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
      Enum<?> feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
      enable = true; } else { boolean enable;
      if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
        Enum<?> feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
        enable = false;
      } else { return; } }
    boolean enable;
    Enum<?> feat;
    String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", new Object[] { str });
    _reportFailedNullCoerce(ctxt, enable, feat, strDesc);
  }
  

  protected final void _verifyNullForScalarCoercion(DeserializationContext ctxt, String str)
    throws JsonMappingException
  {
    if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
      String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", new Object[] { str });
      _reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, strDesc);
    }
  }
  
  protected void _verifyStringForScalarCoercion(DeserializationContext ctxt, String str)
    throws JsonMappingException
  {
    MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
    if (!ctxt.isEnabled(feat)) {
      ctxt.reportInputMismatch(this, "Cannot coerce String \"%s\" %s (enable `%s.%s` to allow)", new Object[] { str, 
        _coercedTypeDesc(), feat.getClass().getSimpleName(), feat.name() });
    }
  }
  
  protected void _verifyNumberForScalarCoercion(DeserializationContext ctxt, JsonParser p)
    throws IOException
  {
    MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
    if (!ctxt.isEnabled(feat))
    {

      String valueDesc = p.getText();
      ctxt.reportInputMismatch(this, "Cannot coerce Number (%s) %s (enable `%s.%s` to allow)", new Object[] { valueDesc, 
        _coercedTypeDesc(), feat.getClass().getSimpleName(), feat.name() });
    }
  }
  
  protected void _reportFailedNullCoerce(DeserializationContext ctxt, boolean state, Enum<?> feature, String inputDesc)
    throws JsonMappingException
  {
    String enableDesc = state ? "enable" : "disable";
    ctxt.reportInputMismatch(this, "Cannot coerce %s to Null value %s (%s `%s.%s` to allow)", new Object[] { inputDesc, 
      _coercedTypeDesc(), enableDesc, feature.getClass().getSimpleName(), feature.name() });
  }
  











  protected String _coercedTypeDesc()
  {
    JavaType t = getValueType();
    String typeDesc; boolean structured; String typeDesc; if ((t != null) && (!t.isPrimitive())) {
      boolean structured = (t.isContainerType()) || (t.isReferenceType());
      
      typeDesc = "'" + t.toString() + "'";
    } else {
      Class<?> cls = handledType();
      
      structured = (cls.isArray()) || (Collection.class.isAssignableFrom(cls)) || (Map.class.isAssignableFrom(cls));
      typeDesc = ClassUtil.nameOf(cls);
    }
    if (structured) {
      return "as content of type " + typeDesc;
    }
    return "for type " + typeDesc;
  }
  
















  protected JsonDeserializer<Object> findDeserializer(DeserializationContext ctxt, JavaType type, BeanProperty property)
    throws JsonMappingException
  {
    return ctxt.findContextualValueDeserializer(type, property);
  }
  




  protected final boolean _isIntNumber(String text)
  {
    int len = text.length();
    if (len > 0) {
      char c = text.charAt(0);
      
      for (int i = (c == '-') || (c == '+') ? 1 : 0; 
          i < len; i++) {
        int ch = text.charAt(i);
        if ((ch > 57) || (ch < 48)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  

















  protected JsonDeserializer<?> findConvertingContentDeserializer(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> existingDeserializer)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if (_neitherNull(intr, prop)) {
      AnnotatedMember member = prop.getMember();
      if (member != null) {
        Object convDef = intr.findDeserializationContentConverter(member);
        if (convDef != null) {
          Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
          JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
          if (existingDeserializer == null) {
            existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop);
          }
          return new StdDelegatingDeserializer(conv, delegateType, existingDeserializer);
        }
      }
    }
    return existingDeserializer;
  }
  
















  protected JsonFormat.Value findFormatOverrides(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults)
  {
    if (prop != null) {
      return prop.findPropertyFormat(ctxt.getConfig(), typeForDefaults);
    }
    
    return ctxt.getDefaultPropertyFormat(typeForDefaults);
  }
  











  protected Boolean findFormatFeature(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults, JsonFormat.Feature feat)
  {
    JsonFormat.Value format = findFormatOverrides(ctxt, prop, typeForDefaults);
    if (format != null) {
      return format.getFeature(feat);
    }
    return null;
  }
  








  protected final NullValueProvider findValueNullProvider(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata)
    throws JsonMappingException
  {
    if (prop != null) {
      return _findNullProvider(ctxt, prop, propMetadata.getValueNulls(), prop
        .getValueDeserializer());
    }
    return null;
  }
  









  protected NullValueProvider findContentNullProvider(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> valueDeser)
    throws JsonMappingException
  {
    Nulls nulls = findContentNullStyle(ctxt, prop);
    if (nulls == Nulls.SKIP) {
      return NullsConstantProvider.skipper();
    }
    NullValueProvider prov = _findNullProvider(ctxt, prop, nulls, valueDeser);
    if (prov != null) {
      return prov;
    }
    return valueDeser;
  }
  
  protected Nulls findContentNullStyle(DeserializationContext ctxt, BeanProperty prop)
    throws JsonMappingException
  {
    if (prop != null) {
      return prop.getMetadata().getContentNulls();
    }
    return null;
  }
  


  protected final NullValueProvider _findNullProvider(DeserializationContext ctxt, BeanProperty prop, Nulls nulls, JsonDeserializer<?> valueDeser)
    throws JsonMappingException
  {
    if (nulls == Nulls.FAIL) {
      if (prop == null) {
        return NullsFailProvider.constructForRootValue(ctxt.constructType(valueDeser.handledType()));
      }
      return NullsFailProvider.constructForProperty(prop);
    }
    if (nulls == Nulls.AS_EMPTY)
    {

      if (valueDeser == null) {
        return null;
      }
      



      if ((valueDeser instanceof BeanDeserializerBase)) {
        ValueInstantiator vi = ((BeanDeserializerBase)valueDeser).getValueInstantiator();
        if (!vi.canCreateUsingDefault()) {
          JavaType type = prop.getType();
          ctxt.reportBadDefinition(type, 
            String.format("Cannot create empty instance of %s, no default Creator", new Object[] { type }));
        }
      }
      

      AccessPattern access = valueDeser.getEmptyAccessPattern();
      if (access == AccessPattern.ALWAYS_NULL) {
        return NullsConstantProvider.nuller();
      }
      if (access == AccessPattern.CONSTANT) {
        return NullsConstantProvider.forValue(valueDeser.getEmptyValue(ctxt));
      }
      
      return new NullsAsEmptyProvider(valueDeser);
    }
    if (nulls == Nulls.SKIP) {
      return NullsConstantProvider.skipper();
    }
    return null;
  }
  





















  protected void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object instanceOrClass, String propName)
    throws IOException
  {
    if (instanceOrClass == null) {
      instanceOrClass = handledType();
    }
    
    if (ctxt.handleUnknownProperty(p, this, instanceOrClass, propName)) {
      return;
    }
    


    p.skipChildren();
  }
  
  protected void handleMissingEndArrayForSingle(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Attempted to unwrap '%s' value from an array (with `DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS`) but it contains more than one value", new Object[] {
    
      handledType().getName() });
  }
  

  protected void _verifyEndArrayForSingle(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonToken t = p.nextToken();
    if (t != JsonToken.END_ARRAY) {
      handleMissingEndArrayForSingle(p, ctxt);
    }
  }
  








  protected static final boolean _neitherNull(Object a, Object b)
  {
    return (a != null) && (b != null);
  }
  




  protected final boolean _byteOverflow(int value)
  {
    return (value < -128) || (value > 255);
  }
  


  protected final boolean _shortOverflow(int value)
  {
    return (value < 32768) || (value > 32767);
  }
  


  protected final boolean _intOverflow(long value)
  {
    return (value < -2147483648L) || (value > 2147483647L);
  }
  


  protected Number _nonNullNumber(Number n)
  {
    if (n == null) {
      n = Integer.valueOf(0);
    }
    return n;
  }
}
