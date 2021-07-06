package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;





public class NumberDeserializers
{
  private static final HashSet<String> _classNames = new HashSet();
  
  static {
    Class<?>[] numberTypes = { Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class };
    










    for (Class<?> cls : numberTypes) {
      _classNames.add(cls.getName());
    }
  }
  
  public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
    if (rawType.isPrimitive()) {
      if (rawType == Integer.TYPE) {
        return IntegerDeserializer.primitiveInstance;
      }
      if (rawType == Boolean.TYPE) {
        return BooleanDeserializer.primitiveInstance;
      }
      if (rawType == Long.TYPE) {
        return LongDeserializer.primitiveInstance;
      }
      if (rawType == Double.TYPE) {
        return DoubleDeserializer.primitiveInstance;
      }
      if (rawType == Character.TYPE) {
        return CharacterDeserializer.primitiveInstance;
      }
      if (rawType == Byte.TYPE) {
        return ByteDeserializer.primitiveInstance;
      }
      if (rawType == Short.TYPE) {
        return ShortDeserializer.primitiveInstance;
      }
      if (rawType == Float.TYPE) {
        return FloatDeserializer.primitiveInstance;
      }
    } else if (_classNames.contains(clsName))
    {
      if (rawType == Integer.class) {
        return IntegerDeserializer.wrapperInstance;
      }
      if (rawType == Boolean.class) {
        return BooleanDeserializer.wrapperInstance;
      }
      if (rawType == Long.class) {
        return LongDeserializer.wrapperInstance;
      }
      if (rawType == Double.class) {
        return DoubleDeserializer.wrapperInstance;
      }
      if (rawType == Character.class) {
        return CharacterDeserializer.wrapperInstance;
      }
      if (rawType == Byte.class) {
        return ByteDeserializer.wrapperInstance;
      }
      if (rawType == Short.class) {
        return ShortDeserializer.wrapperInstance;
      }
      if (rawType == Float.class) {
        return FloatDeserializer.wrapperInstance;
      }
      if (rawType == Number.class) {
        return NumberDeserializer.instance;
      }
      if (rawType == BigDecimal.class) {
        return BigDecimalDeserializer.instance;
      }
      if (rawType == BigInteger.class) {
        return BigIntegerDeserializer.instance;
      }
    } else {
      return null;
    }
    
    throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
  }
  


  public NumberDeserializers() {}
  


  protected static abstract class PrimitiveOrWrapperDeserializer<T>
    extends StdScalarDeserializer<T>
  {
    private static final long serialVersionUID = 1L;
    
    protected final T _nullValue;
    
    protected final T _emptyValue;
    
    protected final boolean _primitive;
    

    protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl, T empty)
    {
      super();
      _nullValue = nvl;
      _emptyValue = empty;
      _primitive = vc.isPrimitive();
    }
    


    public AccessPattern getNullAccessPattern()
    {
      if (_primitive) {
        return AccessPattern.DYNAMIC;
      }
      if (_nullValue == null) {
        return AccessPattern.ALWAYS_NULL;
      }
      return AccessPattern.CONSTANT;
    }
    

    public final T getNullValue(DeserializationContext ctxt)
      throws JsonMappingException
    {
      if ((_primitive) && (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))) {
        ctxt.reportInputMismatch(this, "Cannot map `null` into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", new Object[] {
        
          handledType().toString() });
      }
      return _nullValue;
    }
    
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException
    {
      return _emptyValue;
    }
  }
  




  @JacksonStdImpl
  public static final class BooleanDeserializer
    extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Boolean>
  {
    private static final long serialVersionUID = 1L;
    


    static final BooleanDeserializer primitiveInstance = new BooleanDeserializer(Boolean.TYPE, Boolean.FALSE);
    static final BooleanDeserializer wrapperInstance = new BooleanDeserializer(Boolean.class, null);
    
    public BooleanDeserializer(Class<Boolean> cls, Boolean nvl)
    {
      super(nvl, Boolean.FALSE);
    }
    
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_TRUE) {
        return Boolean.TRUE;
      }
      if (t == JsonToken.VALUE_FALSE) {
        return Boolean.FALSE;
      }
      return _parseBoolean(p, ctxt);
    }
    




    public Boolean deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
      throws IOException
    {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_TRUE) {
        return Boolean.TRUE;
      }
      if (t == JsonToken.VALUE_FALSE) {
        return Boolean.FALSE;
      }
      return _parseBoolean(p, ctxt);
    }
    
    protected final Boolean _parseBoolean(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_NULL) {
        return (Boolean)_coerceNullToken(ctxt, _primitive);
      }
      if (t == JsonToken.START_ARRAY) {
        return (Boolean)_deserializeFromArray(p, ctxt);
      }
      
      if (t == JsonToken.VALUE_NUMBER_INT) {
        return Boolean.valueOf(_parseBooleanFromInt(p, ctxt));
      }
      
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        
        if (("true".equals(text)) || ("True".equals(text))) {
          _verifyStringForScalarCoercion(ctxt, text);
          return Boolean.TRUE;
        }
        if (("false".equals(text)) || ("False".equals(text))) {
          _verifyStringForScalarCoercion(ctxt, text);
          return Boolean.FALSE;
        }
        if (text.length() == 0) {
          return (Boolean)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Boolean)_coerceTextualNull(ctxt, _primitive);
        }
        return (Boolean)ctxt.handleWeirdStringValue(_valueClass, text, "only \"true\" or \"false\" recognized", new Object[0]);
      }
      

      if (t == JsonToken.VALUE_TRUE) {
        return Boolean.TRUE;
      }
      if (t == JsonToken.VALUE_FALSE) {
        return Boolean.FALSE;
      }
      
      return (Boolean)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  

  @JacksonStdImpl
  public static class ByteDeserializer
    extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Byte>
  {
    private static final long serialVersionUID = 1L;
    static final ByteDeserializer primitiveInstance = new ByteDeserializer(Byte.TYPE, Byte.valueOf((byte)0));
    static final ByteDeserializer wrapperInstance = new ByteDeserializer(Byte.class, null);
    
    public ByteDeserializer(Class<Byte> cls, Byte nvl)
    {
      super(nvl, Byte.valueOf((byte)0));
    }
    
    public Byte deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
        return Byte.valueOf(p.getByteValue());
      }
      return _parseByte(p, ctxt);
    }
    
    protected Byte _parseByte(JsonParser p, DeserializationContext ctxt) throws IOException
    {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        if (_hasTextualNull(text)) {
          return (Byte)_coerceTextualNull(ctxt, _primitive);
        }
        int len = text.length();
        if (len == 0) {
          return (Byte)_coerceEmptyString(ctxt, _primitive);
        }
        _verifyStringForScalarCoercion(ctxt, text);
        try
        {
          value = NumberInput.parseInt(text);
        } catch (IllegalArgumentException iae) { int value;
          return (Byte)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Byte value", new Object[0]);
        }
        
        int value;
        
        if (_byteOverflow(value)) {
          return (Byte)ctxt.handleWeirdStringValue(_valueClass, text, "overflow, value cannot be represented as 8-bit value", new Object[0]);
        }
        

        return Byte.valueOf((byte)value);
      }
      if (t == JsonToken.VALUE_NUMBER_FLOAT) {
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "Byte");
        }
        return Byte.valueOf(p.getByteValue());
      }
      if (t == JsonToken.VALUE_NULL) {
        return (Byte)_coerceNullToken(ctxt, _primitive);
      }
      
      if (t == JsonToken.START_ARRAY) {
        return (Byte)_deserializeFromArray(p, ctxt);
      }
      if (t == JsonToken.VALUE_NUMBER_INT) {
        return Byte.valueOf(p.getByteValue());
      }
      return (Byte)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  

  @JacksonStdImpl
  public static class ShortDeserializer
    extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Short>
  {
    private static final long serialVersionUID = 1L;
    static final ShortDeserializer primitiveInstance = new ShortDeserializer(Short.TYPE, Short.valueOf((short)0));
    static final ShortDeserializer wrapperInstance = new ShortDeserializer(Short.class, null);
    
    public ShortDeserializer(Class<Short> cls, Short nvl)
    {
      super(nvl, Short.valueOf((short)0));
    }
    

    public Short deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      return _parseShort(p, ctxt);
    }
    
    protected Short _parseShort(JsonParser p, DeserializationContext ctxt) throws IOException
    {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_NUMBER_INT) {
        return Short.valueOf(p.getShortValue());
      }
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        int len = text.length();
        if (len == 0) {
          return (Short)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Short)_coerceTextualNull(ctxt, _primitive);
        }
        _verifyStringForScalarCoercion(ctxt, text);
        try
        {
          value = NumberInput.parseInt(text);
        } catch (IllegalArgumentException iae) { int value;
          return (Short)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Short value", new Object[0]);
        }
        
        int value;
        if (_shortOverflow(value)) {
          return (Short)ctxt.handleWeirdStringValue(_valueClass, text, "overflow, value cannot be represented as 16-bit value", new Object[0]);
        }
        
        return Short.valueOf((short)value);
      }
      if (t == JsonToken.VALUE_NUMBER_FLOAT) {
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "Short");
        }
        return Short.valueOf(p.getShortValue());
      }
      if (t == JsonToken.VALUE_NULL) {
        return (Short)_coerceNullToken(ctxt, _primitive);
      }
      if (t == JsonToken.START_ARRAY) {
        return (Short)_deserializeFromArray(p, ctxt);
      }
      return (Short)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  

  @JacksonStdImpl
  public static class CharacterDeserializer
    extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Character>
  {
    private static final long serialVersionUID = 1L;
    static final CharacterDeserializer primitiveInstance = new CharacterDeserializer(Character.TYPE, Character.valueOf('\000'));
    static final CharacterDeserializer wrapperInstance = new CharacterDeserializer(Character.class, null);
    
    public CharacterDeserializer(Class<Character> cls, Character nvl)
    {
      super(nvl, Character.valueOf('\000'));
    }
    

    public Character deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      switch (p.getCurrentTokenId()) {
      case 7: 
        _verifyNumberForScalarCoercion(ctxt, p);
        int value = p.getIntValue();
        if ((value >= 0) && (value <= 65535)) {
          return Character.valueOf((char)value);
        }
        
        break;
      case 6: 
        String text = p.getText();
        if (text.length() == 1) {
          return Character.valueOf(text.charAt(0));
        }
        
        if (text.length() == 0) {
          return (Character)_coerceEmptyString(ctxt, _primitive);
        }
        break;
      case 11: 
        return (Character)_coerceNullToken(ctxt, _primitive);
      case 3: 
        return (Character)_deserializeFromArray(p, ctxt);
      }
      
      return (Character)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  

  @JacksonStdImpl
  public static final class IntegerDeserializer
    extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Integer>
  {
    private static final long serialVersionUID = 1L;
    static final IntegerDeserializer primitiveInstance = new IntegerDeserializer(Integer.TYPE, Integer.valueOf(0));
    static final IntegerDeserializer wrapperInstance = new IntegerDeserializer(Integer.class, null);
    
    public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
      super(nvl, Integer.valueOf(0));
    }
    
    public boolean isCachable()
    {
      return true;
    }
    
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
        return Integer.valueOf(p.getIntValue());
      }
      return _parseInteger(p, ctxt);
    }
    



    public Integer deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
      throws IOException
    {
      if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
        return Integer.valueOf(p.getIntValue());
      }
      return _parseInteger(p, ctxt);
    }
    
    protected final Integer _parseInteger(JsonParser p, DeserializationContext ctxt) throws IOException
    {
      switch (p.getCurrentTokenId())
      {
      case 7: 
        return Integer.valueOf(p.getIntValue());
      case 8: 
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "Integer");
        }
        return Integer.valueOf(p.getValueAsInt());
      case 6: 
        String text = p.getText().trim();
        int len = text.length();
        if (len == 0) {
          return (Integer)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Integer)_coerceTextualNull(ctxt, _primitive);
        }
        _verifyStringForScalarCoercion(ctxt, text);
        try {
          if (len > 9) {
            long l = Long.parseLong(text);
            if (_intOverflow(l)) {
              return (Integer)ctxt.handleWeirdStringValue(_valueClass, text, String.format("Overflow: numeric value (%s) out of range of Integer (%d - %d)", new Object[] { text, 
              
                Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE) }), new Object[0]);
            }
            return Integer.valueOf((int)l);
          }
          return Integer.valueOf(NumberInput.parseInt(text));
        } catch (IllegalArgumentException iae) {
          return (Integer)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Integer value", new Object[0]);
        }
      
      case 11: 
        return (Integer)_coerceNullToken(ctxt, _primitive);
      case 3: 
        return (Integer)_deserializeFromArray(p, ctxt);
      }
      
      return (Integer)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  

  @JacksonStdImpl
  public static final class LongDeserializer
    extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Long>
  {
    private static final long serialVersionUID = 1L;
    static final LongDeserializer primitiveInstance = new LongDeserializer(Long.TYPE, Long.valueOf(0L));
    static final LongDeserializer wrapperInstance = new LongDeserializer(Long.class, null);
    
    public LongDeserializer(Class<Long> cls, Long nvl) {
      super(nvl, Long.valueOf(0L));
    }
    
    public boolean isCachable()
    {
      return true;
    }
    
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
        return Long.valueOf(p.getLongValue());
      }
      return _parseLong(p, ctxt);
    }
    
    protected final Long _parseLong(JsonParser p, DeserializationContext ctxt) throws IOException
    {
      switch (p.getCurrentTokenId())
      {
      case 7: 
        return Long.valueOf(p.getLongValue());
      case 8: 
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "Long");
        }
        return Long.valueOf(p.getValueAsLong());
      case 6: 
        String text = p.getText().trim();
        if (text.length() == 0) {
          return (Long)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Long)_coerceTextualNull(ctxt, _primitive);
        }
        _verifyStringForScalarCoercion(ctxt, text);
        try
        {
          return Long.valueOf(NumberInput.parseLong(text));
        } catch (IllegalArgumentException localIllegalArgumentException) {
          return (Long)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Long value", new Object[0]);
        }
      
      case 11: 
        return (Long)_coerceNullToken(ctxt, _primitive);
      case 3: 
        return (Long)_deserializeFromArray(p, ctxt);
      }
      
      return (Long)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  

  @JacksonStdImpl
  public static class FloatDeserializer
    extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Float>
  {
    private static final long serialVersionUID = 1L;
    static final FloatDeserializer primitiveInstance = new FloatDeserializer(Float.TYPE, Float.valueOf(0.0F));
    static final FloatDeserializer wrapperInstance = new FloatDeserializer(Float.class, null);
    
    public FloatDeserializer(Class<Float> cls, Float nvl) {
      super(nvl, Float.valueOf(0.0F));
    }
    
    public Float deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      return _parseFloat(p, ctxt);
    }
    

    protected final Float _parseFloat(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      JsonToken t = p.getCurrentToken();
      
      if ((t == JsonToken.VALUE_NUMBER_FLOAT) || (t == JsonToken.VALUE_NUMBER_INT)) {
        return Float.valueOf(p.getFloatValue());
      }
      
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        if (text.length() == 0) {
          return (Float)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Float)_coerceTextualNull(ctxt, _primitive);
        }
        switch (text.charAt(0)) {
        case 'I': 
          if (_isPosInf(text)) {
            return Float.valueOf(Float.POSITIVE_INFINITY);
          }
          break;
        case 'N': 
          if (_isNaN(text)) {
            return Float.valueOf(NaN.0F);
          }
          break;
        case '-': 
          if (_isNegInf(text)) {
            return Float.valueOf(Float.NEGATIVE_INFINITY);
          }
          break;
        }
        _verifyStringForScalarCoercion(ctxt, text);
        try {
          return Float.valueOf(Float.parseFloat(text));
        } catch (IllegalArgumentException localIllegalArgumentException) {
          return (Float)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Float value", new Object[0]);
        }
      }
      if (t == JsonToken.VALUE_NULL) {
        return (Float)_coerceNullToken(ctxt, _primitive);
      }
      if (t == JsonToken.START_ARRAY) {
        return (Float)_deserializeFromArray(p, ctxt);
      }
      
      return (Float)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  

  @JacksonStdImpl
  public static class DoubleDeserializer
    extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Double>
  {
    private static final long serialVersionUID = 1L;
    static final DoubleDeserializer primitiveInstance = new DoubleDeserializer(Double.TYPE, Double.valueOf(0.0D));
    static final DoubleDeserializer wrapperInstance = new DoubleDeserializer(Double.class, null);
    
    public DoubleDeserializer(Class<Double> cls, Double nvl) {
      super(nvl, Double.valueOf(0.0D));
    }
    
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
      return _parseDouble(p, ctxt);
    }
    



    public Double deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
      throws IOException
    {
      return _parseDouble(p, ctxt);
    }
    
    protected final Double _parseDouble(JsonParser p, DeserializationContext ctxt) throws IOException
    {
      JsonToken t = p.getCurrentToken();
      if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT)) {
        return Double.valueOf(p.getDoubleValue());
      }
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        if (text.length() == 0) {
          return (Double)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Double)_coerceTextualNull(ctxt, _primitive);
        }
        switch (text.charAt(0)) {
        case 'I': 
          if (_isPosInf(text)) {
            return Double.valueOf(Double.POSITIVE_INFINITY);
          }
          break;
        case 'N': 
          if (_isNaN(text)) {
            return Double.valueOf(NaN.0D);
          }
          break;
        case '-': 
          if (_isNegInf(text)) {
            return Double.valueOf(Double.NEGATIVE_INFINITY);
          }
          break;
        }
        _verifyStringForScalarCoercion(ctxt, text);
        try {
          return Double.valueOf(parseDouble(text));
        } catch (IllegalArgumentException localIllegalArgumentException) {
          return (Double)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Double value", new Object[0]);
        }
      }
      if (t == JsonToken.VALUE_NULL) {
        return (Double)_coerceNullToken(ctxt, _primitive);
      }
      if (t == JsonToken.START_ARRAY) {
        return (Double)_deserializeFromArray(p, ctxt);
      }
      
      return (Double)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  











  @JacksonStdImpl
  public static class NumberDeserializer
    extends StdScalarDeserializer<Object>
  {
    public static final NumberDeserializer instance = new NumberDeserializer();
    
    public NumberDeserializer() {
      super();
    }
    
    public Object deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      switch (p.getCurrentTokenId()) {
      case 7: 
        if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
          return _coerceIntegral(p, ctxt);
        }
        return p.getNumberValue();
      
      case 8: 
        if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS))
        {
          if (!p.isNaN()) {
            return p.getDecimalValue();
          }
        }
        return p.getNumberValue();
      



      case 6: 
        String text = p.getText().trim();
        if (text.length() == 0)
        {
          return getNullValue(ctxt);
        }
        if (_hasTextualNull(text))
        {
          return getNullValue(ctxt);
        }
        if (_isPosInf(text)) {
          return Double.valueOf(Double.POSITIVE_INFINITY);
        }
        if (_isNegInf(text)) {
          return Double.valueOf(Double.NEGATIVE_INFINITY);
        }
        if (_isNaN(text)) {
          return Double.valueOf(NaN.0D);
        }
        _verifyStringForScalarCoercion(ctxt, text);
        try {
          if (!_isIntNumber(text)) {
            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
              return new BigDecimal(text);
            }
            return Double.valueOf(text);
          }
          if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
            return new BigInteger(text);
          }
          long value = Long.parseLong(text);
          if ((!ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS)) && 
            (value <= 2147483647L) && (value >= -2147483648L)) {
            return Integer.valueOf((int)value);
          }
          
          return Long.valueOf(value);
        } catch (IllegalArgumentException iae) {
          return ctxt.handleWeirdStringValue(_valueClass, text, "not a valid number", new Object[0]);
        }
      
      case 3: 
        return _deserializeFromArray(p, ctxt);
      }
      
      return ctxt.handleUnexpectedToken(_valueClass, p);
    }
    








    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
      throws IOException
    {
      switch (p.getCurrentTokenId())
      {
      case 6: 
      case 7: 
      case 8: 
        return deserialize(p, ctxt);
      }
      return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
    }
  }
  












  @JacksonStdImpl
  public static class BigIntegerDeserializer
    extends StdScalarDeserializer<BigInteger>
  {
    public static final BigIntegerDeserializer instance = new BigIntegerDeserializer();
    
    public BigIntegerDeserializer() { super(); }
    
    public Object getEmptyValue(DeserializationContext ctxt)
    {
      return BigInteger.ZERO;
    }
    

    public BigInteger deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      switch (p.getCurrentTokenId()) {
      case 7: 
        switch (NumberDeserializers.1.$SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType[p.getNumberType().ordinal()]) {
        case 1: 
        case 2: 
        case 3: 
          return p.getBigIntegerValue();
        }
        break;
      case 8: 
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "java.math.BigInteger");
        }
        return p.getDecimalValue().toBigInteger();
      case 3: 
        return (BigInteger)_deserializeFromArray(p, ctxt);
      case 6: 
        String text = p.getText().trim();
        
        if (_isEmptyOrTextualNull(text)) {
          _verifyNullForScalarCoercion(ctxt, text);
          return (BigInteger)getNullValue(ctxt);
        }
        _verifyStringForScalarCoercion(ctxt, text);
        try {
          return new BigInteger(text);
        } catch (IllegalArgumentException localIllegalArgumentException) {
          return (BigInteger)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid representation", new Object[0]);
        }
      }
      
      return (BigInteger)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  

  @JacksonStdImpl
  public static class BigDecimalDeserializer
    extends StdScalarDeserializer<BigDecimal>
  {
    public static final BigDecimalDeserializer instance = new BigDecimalDeserializer();
    
    public BigDecimalDeserializer() { super(); }
    
    public Object getEmptyValue(DeserializationContext ctxt)
    {
      return BigDecimal.ZERO;
    }
    

    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      switch (p.getCurrentTokenId()) {
      case 7: 
      case 8: 
        return p.getDecimalValue();
      case 6: 
        String text = p.getText().trim();
        
        if (_isEmptyOrTextualNull(text)) {
          _verifyNullForScalarCoercion(ctxt, text);
          return (BigDecimal)getNullValue(ctxt);
        }
        _verifyStringForScalarCoercion(ctxt, text);
        try {
          return new BigDecimal(text);
        } catch (IllegalArgumentException localIllegalArgumentException) {
          return (BigDecimal)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid representation", new Object[0]);
        }
      case 3: 
        return (BigDecimal)_deserializeFromArray(p, ctxt);
      }
      
      return (BigDecimal)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
}
