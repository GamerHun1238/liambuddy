package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ArrayBuilders.BooleanBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.ByteBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.DoubleBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.FloatBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.IntBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.LongBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.ShortBuilder;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;









public abstract class PrimitiveArrayDeserializers<T>
  extends StdDeserializer<T>
  implements ContextualDeserializer
{
  protected final Boolean _unwrapSingle;
  private transient Object _emptyValue;
  protected final NullValueProvider _nuller;
  
  protected PrimitiveArrayDeserializers(Class<T> cls)
  {
    super(cls);
    _unwrapSingle = null;
    _nuller = null;
  }
  



  protected PrimitiveArrayDeserializers(PrimitiveArrayDeserializers<?> base, NullValueProvider nuller, Boolean unwrapSingle)
  {
    super(_valueClass);
    _unwrapSingle = unwrapSingle;
    _nuller = nuller;
  }
  

  public static JsonDeserializer<?> forType(Class<?> rawType)
  {
    if (rawType == Integer.TYPE) {
      return IntDeser.instance;
    }
    if (rawType == Long.TYPE) {
      return LongDeser.instance;
    }
    
    if (rawType == Byte.TYPE) {
      return new ByteDeser();
    }
    if (rawType == Short.TYPE) {
      return new ShortDeser();
    }
    if (rawType == Float.TYPE) {
      return new FloatDeser();
    }
    if (rawType == Double.TYPE) {
      return new DoubleDeser();
    }
    if (rawType == Boolean.TYPE) {
      return new BooleanDeser();
    }
    if (rawType == Character.TYPE) {
      return new CharDeser();
    }
    throw new IllegalStateException();
  }
  

  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    Boolean unwrapSingle = findFormatFeature(ctxt, property, _valueClass, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    
    NullValueProvider nuller = null;
    
    Nulls nullStyle = findContentNullStyle(ctxt, property);
    if (nullStyle == Nulls.SKIP) {
      nuller = NullsConstantProvider.skipper();
    } else if (nullStyle == Nulls.FAIL) {
      if (property == null) {
        nuller = NullsFailProvider.constructForRootValue(ctxt.constructType(_valueClass));
      } else {
        nuller = NullsFailProvider.constructForProperty(property);
      }
    }
    if ((unwrapSingle == _unwrapSingle) && (nuller == _nuller)) {
      return this;
    }
    return withResolved(nuller, unwrapSingle);
  }
  




  protected abstract T _concat(T paramT1, T paramT2);
  




  protected abstract T handleSingleElementUnwrapped(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
    throws IOException;
  




  protected abstract PrimitiveArrayDeserializers<?> withResolved(NullValueProvider paramNullValueProvider, Boolean paramBoolean);
  




  protected abstract T _constructEmpty();
  




  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return Boolean.TRUE;
  }
  

  public AccessPattern getEmptyAccessPattern()
  {
    return AccessPattern.CONSTANT;
  }
  
  public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException
  {
    Object empty = _emptyValue;
    if (empty == null) {
      _emptyValue = (empty = _constructEmpty());
    }
    return empty;
  }
  



  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return typeDeserializer.deserializeTypedFromArray(p, ctxt);
  }
  
  public T deserialize(JsonParser p, DeserializationContext ctxt, T existing)
    throws IOException
  {
    T newValue = deserialize(p, ctxt);
    if (existing == null) {
      return newValue;
    }
    int len = Array.getLength(existing);
    if (len == 0) {
      return newValue;
    }
    return _concat(existing, newValue);
  }
  





























  protected T handleNonArray(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if ((p.hasToken(JsonToken.VALUE_STRING)) && 
      (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) && 
      (p.getText().length() == 0)) {
      return null;
    }
    
    if (_unwrapSingle != Boolean.TRUE) if (_unwrapSingle != null) break label63;
    label63:
    boolean canWrap = ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    if (canWrap) {
      return handleSingleElementUnwrapped(p, ctxt);
    }
    return ctxt.handleUnexpectedToken(_valueClass, p);
  }
  
  protected void _failOnNull(DeserializationContext ctxt) throws IOException
  {
    throw InvalidNullException.from(ctxt, null, ctxt.constructType(_valueClass));
  }
  



  @JacksonStdImpl
  static final class CharDeser
    extends PrimitiveArrayDeserializers<char[]>
  {
    private static final long serialVersionUID = 1L;
    



    public CharDeser() { super(); }
    
    protected CharDeser(CharDeser base, NullValueProvider nuller, Boolean unwrapSingle) { super(nuller, unwrapSingle); }
    



    protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle)
    {
      return this;
    }
    
    protected char[] _constructEmpty()
    {
      return new char[0];
    }
    




    public char[] deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (p.hasToken(JsonToken.VALUE_STRING))
      {
        char[] buffer = p.getTextCharacters();
        int offset = p.getTextOffset();
        int len = p.getTextLength();
        
        char[] result = new char[len];
        System.arraycopy(buffer, offset, result, 0, len);
        return result;
      }
      if (p.isExpectedStartArrayToken())
      {
        StringBuilder sb = new StringBuilder(64);
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) { String str;
          String str;
          if (t == JsonToken.VALUE_STRING) {
            str = p.getText();
          } else if (t == JsonToken.VALUE_NULL) {
            if (_nuller != null) {
              _nuller.getNullValue(ctxt);
              continue;
            }
            _verifyNullForPrimitive(ctxt);
            String str = "\000";
          } else {
            CharSequence cs = (CharSequence)ctxt.handleUnexpectedToken(Character.TYPE, p);
            str = cs.toString();
          }
          if (str.length() != 1) {
            ctxt.reportInputMismatch(this, "Cannot convert a JSON String of length %d into a char element of char array", new Object[] {
              Integer.valueOf(str.length()) });
          }
          sb.append(str.charAt(0));
        }
        return sb.toString().toCharArray();
      }
      
      if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
        Object ob = p.getEmbeddedObject();
        if (ob == null) return null;
        if ((ob instanceof char[])) {
          return (char[])ob;
        }
        if ((ob instanceof String)) {
          return ((String)ob).toCharArray();
        }
        
        if ((ob instanceof byte[])) {
          return Base64Variants.getDefaultVariant().encode((byte[])ob, false).toCharArray();
        }
      }
      
      return (char[])ctxt.handleUnexpectedToken(_valueClass, p);
    }
    

    protected char[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      return (char[])ctxt.handleUnexpectedToken(_valueClass, p);
    }
    
    protected char[] _concat(char[] oldValue, char[] newValue)
    {
      int len1 = oldValue.length;
      int len2 = newValue.length;
      char[] result = Arrays.copyOf(oldValue, len1 + len2);
      System.arraycopy(newValue, 0, result, len1, len2);
      return result;
    }
  }
  



  @JacksonStdImpl
  static final class BooleanDeser
    extends PrimitiveArrayDeserializers<boolean[]>
  {
    private static final long serialVersionUID = 1L;
    



    public BooleanDeser() { super(); }
    
    protected BooleanDeser(BooleanDeser base, NullValueProvider nuller, Boolean unwrapSingle) { super(nuller, unwrapSingle); }
    


    protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle)
    {
      return new BooleanDeser(this, nuller, unwrapSingle);
    }
    
    protected boolean[] _constructEmpty()
    {
      return new boolean[0];
    }
    

    public boolean[] deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (!p.isExpectedStartArrayToken()) {
        return (boolean[])handleNonArray(p, ctxt);
      }
      ArrayBuilders.BooleanBuilder builder = ctxt.getArrayBuilders().getBooleanBuilder();
      boolean[] chunk = (boolean[])builder.resetAndStart();
      int ix = 0;
      try
      {
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) { boolean value;
          boolean value;
          if (t == JsonToken.VALUE_TRUE) {
            value = true; } else { boolean value;
            if (t == JsonToken.VALUE_FALSE) {
              value = false;
            } else if (t == JsonToken.VALUE_NULL) {
              if (_nuller != null) {
                _nuller.getNullValue(ctxt);
                continue;
              }
              _verifyNullForPrimitive(ctxt);
              boolean value = false;
            } else {
              value = _parseBooleanPrimitive(p, ctxt);
            } }
          if (ix >= chunk.length) {
            chunk = (boolean[])builder.appendCompletedChunk(chunk, ix);
            ix = 0;
          }
          chunk[(ix++)] = value;
        }
      } catch (Exception e) {
        throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
      }
      return (boolean[])builder.completeAndClearBuffer(chunk, ix);
    }
    
    protected boolean[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      return new boolean[] { _parseBooleanPrimitive(p, ctxt) };
    }
    
    protected boolean[] _concat(boolean[] oldValue, boolean[] newValue)
    {
      int len1 = oldValue.length;
      int len2 = newValue.length;
      boolean[] result = Arrays.copyOf(oldValue, len1 + len2);
      System.arraycopy(newValue, 0, result, len1, len2);
      return result;
    }
  }
  


  @JacksonStdImpl
  static final class ByteDeser
    extends PrimitiveArrayDeserializers<byte[]>
  {
    private static final long serialVersionUID = 1L;
    


    public ByteDeser() { super(); }
    
    protected ByteDeser(ByteDeser base, NullValueProvider nuller, Boolean unwrapSingle) { super(nuller, unwrapSingle); }
    


    protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle)
    {
      return new ByteDeser(this, nuller, unwrapSingle);
    }
    
    protected byte[] _constructEmpty()
    {
      return new byte[0];
    }
    
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      JsonToken t = p.currentToken();
      

      if (t == JsonToken.VALUE_STRING) {
        try {
          return p.getBinaryValue(ctxt.getBase64Variant());

        }
        catch (JsonParseException e)
        {

          String msg = e.getOriginalMessage();
          if (msg.contains("base64")) {
            return (byte[])ctxt.handleWeirdStringValue([B.class, p
              .getText(), msg, new Object[0]);
          }
        }
      }
      
      if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
        Object ob = p.getEmbeddedObject();
        if (ob == null) return null;
        if ((ob instanceof byte[])) {
          return (byte[])ob;
        }
      }
      if (!p.isExpectedStartArrayToken()) {
        return (byte[])handleNonArray(p, ctxt);
      }
      ArrayBuilders.ByteBuilder builder = ctxt.getArrayBuilders().getByteBuilder();
      byte[] chunk = (byte[])builder.resetAndStart();
      int ix = 0;
      try
      {
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
          byte value;
          byte value;
          if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT))
          {
            value = p.getByteValue();

          }
          else if (t == JsonToken.VALUE_NULL) {
            if (_nuller != null) {
              _nuller.getNullValue(ctxt);
              continue;
            }
            _verifyNullForPrimitive(ctxt);
            byte value = 0;
          } else {
            value = _parseBytePrimitive(p, ctxt);
          }
          
          if (ix >= chunk.length) {
            chunk = (byte[])builder.appendCompletedChunk(chunk, ix);
            ix = 0;
          }
          chunk[(ix++)] = value;
        }
      } catch (Exception e) {
        throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
      }
      return (byte[])builder.completeAndClearBuffer(chunk, ix);
    }
    


    protected byte[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      JsonToken t = p.currentToken();
      byte value; byte value; if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT))
      {
        value = p.getByteValue();
      }
      else {
        if (t == JsonToken.VALUE_NULL) {
          if (_nuller != null) {
            _nuller.getNullValue(ctxt);
            return (byte[])getEmptyValue(ctxt);
          }
          _verifyNullForPrimitive(ctxt);
          return null;
        }
        Number n = (Number)ctxt.handleUnexpectedToken(_valueClass.getComponentType(), p);
        value = n.byteValue();
      }
      return new byte[] { value };
    }
    
    protected byte[] _concat(byte[] oldValue, byte[] newValue)
    {
      int len1 = oldValue.length;
      int len2 = newValue.length;
      byte[] result = Arrays.copyOf(oldValue, len1 + len2);
      System.arraycopy(newValue, 0, result, len1, len2);
      return result;
    }
  }
  
  @JacksonStdImpl
  static final class ShortDeser
    extends PrimitiveArrayDeserializers<short[]>
  {
    private static final long serialVersionUID = 1L;
    
    public ShortDeser() { super(); }
    
    protected ShortDeser(ShortDeser base, NullValueProvider nuller, Boolean unwrapSingle) { super(nuller, unwrapSingle); }
    


    protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle)
    {
      return new ShortDeser(this, nuller, unwrapSingle);
    }
    
    protected short[] _constructEmpty()
    {
      return new short[0];
    }
    
    public short[] deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (!p.isExpectedStartArrayToken()) {
        return (short[])handleNonArray(p, ctxt);
      }
      ArrayBuilders.ShortBuilder builder = ctxt.getArrayBuilders().getShortBuilder();
      short[] chunk = (short[])builder.resetAndStart();
      int ix = 0;
      try
      {
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
          short value;
          if (t == JsonToken.VALUE_NULL) {
            if (_nuller != null) {
              _nuller.getNullValue(ctxt);
              continue;
            }
            _verifyNullForPrimitive(ctxt);
            short value = 0;
          } else {
            value = _parseShortPrimitive(p, ctxt);
          }
          if (ix >= chunk.length) {
            chunk = (short[])builder.appendCompletedChunk(chunk, ix);
            ix = 0;
          }
          chunk[(ix++)] = value;
        }
      } catch (Exception e) {
        throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
      }
      return (short[])builder.completeAndClearBuffer(chunk, ix);
    }
    
    protected short[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      return new short[] { _parseShortPrimitive(p, ctxt) };
    }
    
    protected short[] _concat(short[] oldValue, short[] newValue)
    {
      int len1 = oldValue.length;
      int len2 = newValue.length;
      short[] result = Arrays.copyOf(oldValue, len1 + len2);
      System.arraycopy(newValue, 0, result, len1, len2);
      return result;
    }
  }
  

  @JacksonStdImpl
  static final class IntDeser
    extends PrimitiveArrayDeserializers<int[]>
  {
    private static final long serialVersionUID = 1L;
    public static final IntDeser instance = new IntDeser();
    
    public IntDeser() { super(); }
    
    protected IntDeser(IntDeser base, NullValueProvider nuller, Boolean unwrapSingle) { super(nuller, unwrapSingle); }
    


    protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle)
    {
      return new IntDeser(this, nuller, unwrapSingle);
    }
    
    protected int[] _constructEmpty()
    {
      return new int[0];
    }
    
    public int[] deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (!p.isExpectedStartArrayToken()) {
        return (int[])handleNonArray(p, ctxt);
      }
      ArrayBuilders.IntBuilder builder = ctxt.getArrayBuilders().getIntBuilder();
      int[] chunk = (int[])builder.resetAndStart();
      int ix = 0;
      try
      {
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) { int value;
          int value;
          if (t == JsonToken.VALUE_NUMBER_INT) {
            value = p.getIntValue();
          } else if (t == JsonToken.VALUE_NULL) {
            if (_nuller != null) {
              _nuller.getNullValue(ctxt);
              continue;
            }
            _verifyNullForPrimitive(ctxt);
            int value = 0;
          } else {
            value = _parseIntPrimitive(p, ctxt);
          }
          if (ix >= chunk.length) {
            chunk = (int[])builder.appendCompletedChunk(chunk, ix);
            ix = 0;
          }
          chunk[(ix++)] = value;
        }
      } catch (Exception e) {
        throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
      }
      return (int[])builder.completeAndClearBuffer(chunk, ix);
    }
    
    protected int[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      return new int[] { _parseIntPrimitive(p, ctxt) };
    }
    
    protected int[] _concat(int[] oldValue, int[] newValue)
    {
      int len1 = oldValue.length;
      int len2 = newValue.length;
      int[] result = Arrays.copyOf(oldValue, len1 + len2);
      System.arraycopy(newValue, 0, result, len1, len2);
      return result;
    }
  }
  

  @JacksonStdImpl
  static final class LongDeser
    extends PrimitiveArrayDeserializers<long[]>
  {
    private static final long serialVersionUID = 1L;
    public static final LongDeser instance = new LongDeser();
    
    public LongDeser() { super(); }
    
    protected LongDeser(LongDeser base, NullValueProvider nuller, Boolean unwrapSingle) { super(nuller, unwrapSingle); }
    


    protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle)
    {
      return new LongDeser(this, nuller, unwrapSingle);
    }
    
    protected long[] _constructEmpty()
    {
      return new long[0];
    }
    
    public long[] deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (!p.isExpectedStartArrayToken()) {
        return (long[])handleNonArray(p, ctxt);
      }
      ArrayBuilders.LongBuilder builder = ctxt.getArrayBuilders().getLongBuilder();
      long[] chunk = (long[])builder.resetAndStart();
      int ix = 0;
      try
      {
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) { long value;
          long value;
          if (t == JsonToken.VALUE_NUMBER_INT) {
            value = p.getLongValue();
          } else if (t == JsonToken.VALUE_NULL) {
            if (_nuller != null) {
              _nuller.getNullValue(ctxt);
              continue;
            }
            _verifyNullForPrimitive(ctxt);
            long value = 0L;
          } else {
            value = _parseLongPrimitive(p, ctxt);
          }
          if (ix >= chunk.length) {
            chunk = (long[])builder.appendCompletedChunk(chunk, ix);
            ix = 0;
          }
          chunk[(ix++)] = value;
        }
      } catch (Exception e) {
        throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
      }
      return (long[])builder.completeAndClearBuffer(chunk, ix);
    }
    
    protected long[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      return new long[] { _parseLongPrimitive(p, ctxt) };
    }
    
    protected long[] _concat(long[] oldValue, long[] newValue)
    {
      int len1 = oldValue.length;
      int len2 = newValue.length;
      long[] result = Arrays.copyOf(oldValue, len1 + len2);
      System.arraycopy(newValue, 0, result, len1, len2);
      return result;
    }
  }
  
  @JacksonStdImpl
  static final class FloatDeser
    extends PrimitiveArrayDeserializers<float[]>
  {
    private static final long serialVersionUID = 1L;
    
    public FloatDeser() { super(); }
    
    protected FloatDeser(FloatDeser base, NullValueProvider nuller, Boolean unwrapSingle) { super(nuller, unwrapSingle); }
    


    protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle)
    {
      return new FloatDeser(this, nuller, unwrapSingle);
    }
    
    protected float[] _constructEmpty()
    {
      return new float[0];
    }
    
    public float[] deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (!p.isExpectedStartArrayToken()) {
        return (float[])handleNonArray(p, ctxt);
      }
      ArrayBuilders.FloatBuilder builder = ctxt.getArrayBuilders().getFloatBuilder();
      float[] chunk = (float[])builder.resetAndStart();
      int ix = 0;
      try
      {
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY)
        {
          if ((t == JsonToken.VALUE_NULL) && 
            (_nuller != null)) {
            _nuller.getNullValue(ctxt);
          }
          else
          {
            float value = _parseFloatPrimitive(p, ctxt);
            if (ix >= chunk.length) {
              chunk = (float[])builder.appendCompletedChunk(chunk, ix);
              ix = 0;
            }
            chunk[(ix++)] = value;
          } }
      } catch (Exception e) {
        throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
      }
      return (float[])builder.completeAndClearBuffer(chunk, ix);
    }
    
    protected float[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      return new float[] { _parseFloatPrimitive(p, ctxt) };
    }
    
    protected float[] _concat(float[] oldValue, float[] newValue)
    {
      int len1 = oldValue.length;
      int len2 = newValue.length;
      float[] result = Arrays.copyOf(oldValue, len1 + len2);
      System.arraycopy(newValue, 0, result, len1, len2);
      return result;
    }
  }
  
  @JacksonStdImpl
  static final class DoubleDeser
    extends PrimitiveArrayDeserializers<double[]>
  {
    private static final long serialVersionUID = 1L;
    
    public DoubleDeser() { super(); }
    
    protected DoubleDeser(DoubleDeser base, NullValueProvider nuller, Boolean unwrapSingle) { super(nuller, unwrapSingle); }
    


    protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle)
    {
      return new DoubleDeser(this, nuller, unwrapSingle);
    }
    
    protected double[] _constructEmpty()
    {
      return new double[0];
    }
    
    public double[] deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (!p.isExpectedStartArrayToken()) {
        return (double[])handleNonArray(p, ctxt);
      }
      ArrayBuilders.DoubleBuilder builder = ctxt.getArrayBuilders().getDoubleBuilder();
      double[] chunk = (double[])builder.resetAndStart();
      int ix = 0;
      try
      {
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY)
          if ((t == JsonToken.VALUE_NULL) && 
            (_nuller != null)) {
            _nuller.getNullValue(ctxt);
          }
          else
          {
            double value = _parseDoublePrimitive(p, ctxt);
            if (ix >= chunk.length) {
              chunk = (double[])builder.appendCompletedChunk(chunk, ix);
              ix = 0;
            }
            chunk[(ix++)] = value;
          }
      } catch (Exception e) {
        throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
      }
      return (double[])builder.completeAndClearBuffer(chunk, ix);
    }
    
    protected double[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      return new double[] { _parseDoublePrimitive(p, ctxt) };
    }
    
    protected double[] _concat(double[] oldValue, double[] newValue)
    {
      int len1 = oldValue.length;
      int len2 = newValue.length;
      double[] result = Arrays.copyOf(oldValue, len1 + len2);
      System.arraycopy(newValue, 0, result, len1, len2);
      return result;
    }
  }
}
