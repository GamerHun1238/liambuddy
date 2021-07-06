package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.type.WritableTypeId.Inclusion;
import com.fasterxml.jackson.core.util.VersionUtil;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
















public abstract class JsonGenerator
  implements Closeable, Flushable, Versioned
{
  protected PrettyPrinter _cfgPrettyPrinter;
  protected JsonGenerator() {}
  
  public abstract JsonGenerator setCodec(ObjectCodec paramObjectCodec);
  
  public abstract ObjectCodec getCodec();
  
  public abstract Version version();
  
  public abstract JsonGenerator enable(Feature paramFeature);
  
  public abstract JsonGenerator disable(Feature paramFeature);
  
  public static enum Feature
  {
    AUTO_CLOSE_TARGET(true), 
    










    AUTO_CLOSE_JSON_CONTENT(true), 
    











    FLUSH_PASSED_TO_STREAM(true), 
    













    QUOTE_FIELD_NAMES(true), 
    















    QUOTE_NON_NUMERIC_NUMBERS(true), 
    




















    ESCAPE_NON_ASCII(false), 
    























    WRITE_NUMBERS_AS_STRINGS(false), 
    















    WRITE_BIGDECIMAL_AS_PLAIN(false), 
    

















    STRICT_DUPLICATE_DETECTION(false), 
    




















    IGNORE_UNKNOWN(false);
    


    private final boolean _defaultState;
    

    private final int _mask;
    

    public static int collectDefaults()
    {
      int flags = 0;
      for (Feature f : values()) {
        if (f.enabledByDefault()) {
          flags |= f.getMask();
        }
      }
      return flags;
    }
    
    private Feature(boolean defaultState) {
      _defaultState = defaultState;
      _mask = (1 << ordinal());
    }
    
    public boolean enabledByDefault() { return _defaultState; }
    



    public boolean enabledIn(int flags) { return (flags & _mask) != 0; }
    
    public int getMask() { return _mask; }
  }
  






































































  public final JsonGenerator configure(Feature f, boolean state)
  {
    if (state) enable(f); else disable(f);
    return this;
  }
  



  public abstract boolean isEnabled(Feature paramFeature);
  



  public boolean isEnabled(StreamWriteFeature f)
  {
    return isEnabled(f.mappedFeature());
  }
  












  public abstract int getFeatureMask();
  











  @Deprecated
  public abstract JsonGenerator setFeatureMask(int paramInt);
  











  public JsonGenerator overrideStdFeatures(int values, int mask)
  {
    int oldState = getFeatureMask();
    int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
    return setFeatureMask(newState);
  }
  







  public int getFormatFeatures()
  {
    return 0;
  }
  















  public JsonGenerator overrideFormatFeatures(int values, int mask)
  {
    return this;
  }
  





















  public void setSchema(FormatSchema schema)
  {
    throw new UnsupportedOperationException("Generator of type " + getClass().getName() + " does not support schema of type '" + schema.getSchemaType() + "'");
  }
  




  public FormatSchema getSchema()
  {
    return null;
  }
  















  public JsonGenerator setPrettyPrinter(PrettyPrinter pp)
  {
    _cfgPrettyPrinter = pp;
    return this;
  }
  





  public PrettyPrinter getPrettyPrinter()
  {
    return _cfgPrettyPrinter;
  }
  













  public abstract JsonGenerator useDefaultPrettyPrinter();
  













  public JsonGenerator setHighestNonEscapedChar(int charCode)
  {
    return this;
  }
  









  public int getHighestEscapedChar()
  {
    return 0;
  }
  

  public CharacterEscapes getCharacterEscapes()
  {
    return null;
  }
  



  public JsonGenerator setCharacterEscapes(CharacterEscapes esc)
  {
    return this;
  }
  









  public JsonGenerator setRootValueSeparator(SerializableString sep)
  {
    throw new UnsupportedOperationException();
  }
  




















  public Object getOutputTarget()
  {
    return null;
  }
  

















  public int getOutputBuffered()
  {
    return -1;
  }
  












  public Object getCurrentValue()
  {
    JsonStreamContext ctxt = getOutputContext();
    return ctxt == null ? null : ctxt.getCurrentValue();
  }
  







  public void setCurrentValue(Object v)
  {
    JsonStreamContext ctxt = getOutputContext();
    if (ctxt != null) {
      ctxt.setCurrentValue(v);
    }
  }
  












  public boolean canUseSchema(FormatSchema schema)
  {
    return false;
  }
  











  public boolean canWriteObjectId()
  {
    return false;
  }
  











  public boolean canWriteTypeId()
  {
    return false;
  }
  







  public boolean canWriteBinaryNatively()
  {
    return false;
  }
  





  public boolean canOmitFields()
  {
    return true;
  }
  









  public boolean canWriteFormattedNumbers()
  {
    return false;
  }
  














  public abstract void writeStartArray()
    throws IOException;
  













  public void writeStartArray(int size)
    throws IOException
  {
    writeStartArray();
  }
  

  public void writeStartArray(Object forValue)
    throws IOException
  {
    writeStartArray();
    setCurrentValue(forValue);
  }
  

  public void writeStartArray(Object forValue, int size)
    throws IOException
  {
    writeStartArray(size);
    setCurrentValue(forValue);
  }
  









  public abstract void writeEndArray()
    throws IOException;
  









  public abstract void writeStartObject()
    throws IOException;
  









  public void writeStartObject(Object forValue)
    throws IOException
  {
    writeStartObject();
    setCurrentValue(forValue);
  }
  















  public void writeStartObject(Object forValue, int size)
    throws IOException
  {
    writeStartObject();
    setCurrentValue(forValue);
  }
  









  public abstract void writeEndObject()
    throws IOException;
  









  public abstract void writeFieldName(String paramString)
    throws IOException;
  









  public abstract void writeFieldName(SerializableString paramSerializableString)
    throws IOException;
  








  public void writeFieldId(long id)
    throws IOException
  {
    writeFieldName(Long.toString(id));
  }
  
















  public void writeArray(int[] array, int offset, int length)
    throws IOException
  {
    if (array == null) {
      throw new IllegalArgumentException("null array");
    }
    _verifyOffsets(array.length, offset, length);
    writeStartArray(array, length);
    int i = offset; for (int end = offset + length; i < end; i++) {
      writeNumber(array[i]);
    }
    writeEndArray();
  }
  










  public void writeArray(long[] array, int offset, int length)
    throws IOException
  {
    if (array == null) {
      throw new IllegalArgumentException("null array");
    }
    _verifyOffsets(array.length, offset, length);
    writeStartArray(array, length);
    int i = offset; for (int end = offset + length; i < end; i++) {
      writeNumber(array[i]);
    }
    writeEndArray();
  }
  










  public void writeArray(double[] array, int offset, int length)
    throws IOException
  {
    if (array == null) {
      throw new IllegalArgumentException("null array");
    }
    _verifyOffsets(array.length, offset, length);
    writeStartArray(array, length);
    int i = offset; for (int end = offset + length; i < end; i++) {
      writeNumber(array[i]);
    }
    writeEndArray();
  }
  












  public abstract void writeString(String paramString)
    throws IOException;
  











  public void writeString(Reader reader, int len)
    throws IOException
  {
    _reportUnsupportedOperation();
  }
  













  public abstract void writeString(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException;
  













  public abstract void writeString(SerializableString paramSerializableString)
    throws IOException;
  













  public abstract void writeRawUTF8String(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  













  public abstract void writeUTF8String(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  













  public abstract void writeRaw(String paramString)
    throws IOException;
  













  public abstract void writeRaw(String paramString, int paramInt1, int paramInt2)
    throws IOException;
  













  public abstract void writeRaw(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException;
  












  public abstract void writeRaw(char paramChar)
    throws IOException;
  












  public void writeRaw(SerializableString raw)
    throws IOException
  {
    writeRaw(raw.getValue());
  }
  



  public abstract void writeRawValue(String paramString)
    throws IOException;
  



  public abstract void writeRawValue(String paramString, int paramInt1, int paramInt2)
    throws IOException;
  


  public abstract void writeRawValue(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException;
  


  public void writeRawValue(SerializableString raw)
    throws IOException
  {
    writeRawValue(raw.getValue());
  }
  












  public abstract void writeBinary(Base64Variant paramBase64Variant, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  












  public void writeBinary(byte[] data, int offset, int len)
    throws IOException
  {
    writeBinary(Base64Variants.getDefaultVariant(), data, offset, len);
  }
  




  public void writeBinary(byte[] data)
    throws IOException
  {
    writeBinary(Base64Variants.getDefaultVariant(), data, 0, data.length);
  }
  












  public int writeBinary(InputStream data, int dataLength)
    throws IOException
  {
    return writeBinary(Base64Variants.getDefaultVariant(), data, dataLength);
  }
  


















  public abstract int writeBinary(Base64Variant paramBase64Variant, InputStream paramInputStream, int paramInt)
    throws IOException;
  

















  public void writeNumber(short v)
    throws IOException
  {
    writeNumber(v);
  }
  










  public abstract void writeNumber(int paramInt)
    throws IOException;
  









  public abstract void writeNumber(long paramLong)
    throws IOException;
  









  public abstract void writeNumber(BigInteger paramBigInteger)
    throws IOException;
  









  public abstract void writeNumber(double paramDouble)
    throws IOException;
  









  public abstract void writeNumber(float paramFloat)
    throws IOException;
  









  public abstract void writeNumber(BigDecimal paramBigDecimal)
    throws IOException;
  









  public abstract void writeNumber(String paramString)
    throws IOException;
  









  public abstract void writeBoolean(boolean paramBoolean)
    throws IOException;
  









  public abstract void writeNull()
    throws IOException;
  









  public void writeEmbeddedObject(Object object)
    throws IOException
  {
    if (object == null) {
      writeNull();
      return;
    }
    if ((object instanceof byte[])) {
      writeBinary((byte[])object);
      return;
    }
    
    throw new JsonGenerationException("No native support for writing embedded objects of type " + object.getClass().getName(), this);
  }
  
















  public void writeObjectId(Object id)
    throws IOException
  {
    throw new JsonGenerationException("No native support for writing Object Ids", this);
  }
  







  public void writeObjectRef(Object id)
    throws IOException
  {
    throw new JsonGenerationException("No native support for writing Object Ids", this);
  }
  









  public void writeTypeId(Object id)
    throws IOException
  {
    throw new JsonGenerationException("No native support for writing Type Ids", this);
  }
  













  public WritableTypeId writeTypePrefix(WritableTypeId typeIdDef)
    throws IOException
  {
    Object id = id;
    
    JsonToken valueShape = valueShape;
    if (canWriteTypeId()) {
      wrapperWritten = false;
      
      writeTypeId(id);
    }
    else
    {
      String idStr = (id instanceof String) ? (String)id : String.valueOf(id);
      wrapperWritten = true;
      
      WritableTypeId.Inclusion incl = include;
      
      if ((valueShape != JsonToken.START_OBJECT) && 
        (incl.requiresObjectContext())) {
        include = (incl = WritableTypeId.Inclusion.WRAPPER_ARRAY);
      }
      
      switch (1.$SwitchMap$com$fasterxml$jackson$core$type$WritableTypeId$Inclusion[incl.ordinal()])
      {
      case 1: 
        break;
      

      case 2: 
        break;
      


      case 3: 
        writeStartObject(forValue);
        writeStringField(asProperty, idStr);
        return typeIdDef;
      

      case 4: 
        writeStartObject();
        writeFieldName(idStr);
        break;
      case 5: 
      default: 
        writeStartArray();
        writeString(idStr);
      }
      
    }
    if (valueShape == JsonToken.START_OBJECT) {
      writeStartObject(forValue);
    } else if (valueShape == JsonToken.START_ARRAY)
    {
      writeStartArray();
    }
    return typeIdDef;
  }
  


  public WritableTypeId writeTypeSuffix(WritableTypeId typeIdDef)
    throws IOException
  {
    JsonToken valueShape = valueShape;
    
    if (valueShape == JsonToken.START_OBJECT) {
      writeEndObject();
    } else if (valueShape == JsonToken.START_ARRAY) {
      writeEndArray();
    }
    
    if (wrapperWritten) {
      switch (1.$SwitchMap$com$fasterxml$jackson$core$type$WritableTypeId$Inclusion[include.ordinal()]) {
      case 5: 
        writeEndArray();
        break;
      

      case 1: 
        Object id = id;
        String idStr = (id instanceof String) ? (String)id : String.valueOf(id);
        writeStringField(asProperty, idStr);
        
        break;
      case 2: 
      case 3: 
        break;
      
      case 4: 
      default: 
        writeEndObject();
      }
      
    }
    return typeIdDef;
  }
  













  public abstract void writeObject(Object paramObject)
    throws IOException;
  












  public abstract void writeTree(TreeNode paramTreeNode)
    throws IOException;
  












  public void writeStringField(String fieldName, String value)
    throws IOException
  {
    writeFieldName(fieldName);
    writeString(value);
  }
  






  public final void writeBooleanField(String fieldName, boolean value)
    throws IOException
  {
    writeFieldName(fieldName);
    writeBoolean(value);
  }
  






  public final void writeNullField(String fieldName)
    throws IOException
  {
    writeFieldName(fieldName);
    writeNull();
  }
  






  public final void writeNumberField(String fieldName, int value)
    throws IOException
  {
    writeFieldName(fieldName);
    writeNumber(value);
  }
  






  public final void writeNumberField(String fieldName, long value)
    throws IOException
  {
    writeFieldName(fieldName);
    writeNumber(value);
  }
  






  public final void writeNumberField(String fieldName, double value)
    throws IOException
  {
    writeFieldName(fieldName);
    writeNumber(value);
  }
  






  public final void writeNumberField(String fieldName, float value)
    throws IOException
  {
    writeFieldName(fieldName);
    writeNumber(value);
  }
  







  public final void writeNumberField(String fieldName, BigDecimal value)
    throws IOException
  {
    writeFieldName(fieldName);
    writeNumber(value);
  }
  







  public final void writeBinaryField(String fieldName, byte[] data)
    throws IOException
  {
    writeFieldName(fieldName);
    writeBinary(data);
  }
  











  public final void writeArrayFieldStart(String fieldName)
    throws IOException
  {
    writeFieldName(fieldName);
    writeStartArray();
  }
  











  public final void writeObjectFieldStart(String fieldName)
    throws IOException
  {
    writeFieldName(fieldName);
    writeStartObject();
  }
  







  public final void writeObjectField(String fieldName, Object pojo)
    throws IOException
  {
    writeFieldName(fieldName);
    writeObject(pojo);
  }
  











  public void writeOmittedField(String fieldName)
    throws IOException
  {}
  











  public void copyCurrentEvent(JsonParser p)
    throws IOException
  {
    JsonToken t = p.currentToken();
    int token = t == null ? -1 : t.id();
    switch (token) {
    case -1: 
      _reportError("No current event to copy");
      break;
    case 1: 
      writeStartObject();
      break;
    case 2: 
      writeEndObject();
      break;
    case 3: 
      writeStartArray();
      break;
    case 4: 
      writeEndArray();
      break;
    case 5: 
      writeFieldName(p.getCurrentName());
      break;
    case 6: 
      if (p.hasTextCharacters()) {
        writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
      } else {
        writeString(p.getText());
      }
      break;
    
    case 7: 
      JsonParser.NumberType n = p.getNumberType();
      if (n == JsonParser.NumberType.INT) {
        writeNumber(p.getIntValue());
      } else if (n == JsonParser.NumberType.BIG_INTEGER) {
        writeNumber(p.getBigIntegerValue());
      } else {
        writeNumber(p.getLongValue());
      }
      break;
    

    case 8: 
      JsonParser.NumberType n = p.getNumberType();
      if (n == JsonParser.NumberType.BIG_DECIMAL) {
        writeNumber(p.getDecimalValue());
      } else if (n == JsonParser.NumberType.FLOAT) {
        writeNumber(p.getFloatValue());
      } else {
        writeNumber(p.getDoubleValue());
      }
      break;
    
    case 9: 
      writeBoolean(true);
      break;
    case 10: 
      writeBoolean(false);
      break;
    case 11: 
      writeNull();
      break;
    case 12: 
      writeObject(p.getEmbeddedObject());
      break;
    case 0: default: 
      throw new IllegalStateException("Internal error: unknown current token, " + t);
    }
    
  }
  




























  public void copyCurrentStructure(JsonParser p)
    throws IOException
  {
    JsonToken t = p.currentToken();
    
    int id = t == null ? -1 : t.id();
    if (id == 5) {
      writeFieldName(p.getCurrentName());
      t = p.nextToken();
      id = t == null ? -1 : t.id();
    }
    
    switch (id) {
    case 1: 
      writeStartObject();
      _copyCurrentContents(p);
      return;
    case 3: 
      writeStartArray();
      _copyCurrentContents(p);
      return;
    }
    
    copyCurrentEvent(p);
  }
  



  protected void _copyCurrentContents(JsonParser p)
    throws IOException
  {
    int depth = 1;
    
    JsonToken t;
    
    while ((t = p.nextToken()) != null) {
      switch (t.id()) {
      case 5: 
        writeFieldName(p.getCurrentName());
        break;
      
      case 3: 
        writeStartArray();
        depth++;
        break;
      
      case 1: 
        writeStartObject();
        depth++;
        break;
      
      case 4: 
        writeEndArray();
        depth--; if (depth == 0) {}
        

        break;
      case 2: 
        writeEndObject();
        depth--; if (depth == 0) {}
        


        break;
      case 6: 
        if (p.hasTextCharacters()) {
          writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
        } else {
          writeString(p.getText());
        }
        break;
      
      case 7: 
        JsonParser.NumberType n = p.getNumberType();
        if (n == JsonParser.NumberType.INT) {
          writeNumber(p.getIntValue());
        } else if (n == JsonParser.NumberType.BIG_INTEGER) {
          writeNumber(p.getBigIntegerValue());
        } else {
          writeNumber(p.getLongValue());
        }
        break;
      

      case 8: 
        JsonParser.NumberType n = p.getNumberType();
        if (n == JsonParser.NumberType.BIG_DECIMAL) {
          writeNumber(p.getDecimalValue());
        } else if (n == JsonParser.NumberType.FLOAT) {
          writeNumber(p.getFloatValue());
        } else {
          writeNumber(p.getDoubleValue());
        }
        break;
      
      case 9: 
        writeBoolean(true);
        break;
      case 10: 
        writeBoolean(false);
        break;
      case 11: 
        writeNull();
        break;
      case 12: 
        writeObject(p.getEmbeddedObject());
        break;
      default: 
        throw new IllegalStateException("Internal error: unknown current token, " + t);
      }
      
    }
  }
  











  public abstract JsonStreamContext getOutputContext();
  











  public abstract void flush()
    throws IOException;
  











  public abstract boolean isClosed();
  










  public abstract void close()
    throws IOException;
  










  protected void _reportError(String msg)
    throws JsonGenerationException
  {
    throw new JsonGenerationException(msg, this);
  }
  
  protected final void _throwInternal() {}
  
  protected void _reportUnsupportedOperation() {
    throw new UnsupportedOperationException("Operation not supported by generator of type " + getClass().getName());
  }
  



  protected final void _verifyOffsets(int arrayLength, int offset, int length)
  {
    if ((offset < 0) || (offset + length > arrayLength)) {
      throw new IllegalArgumentException(String.format("invalid argument(s) (offset=%d, length=%d) for input array of %d element", new Object[] {
      
        Integer.valueOf(offset), Integer.valueOf(length), Integer.valueOf(arrayLength) }));
    }
  }
  










  protected void _writeSimpleObject(Object value)
    throws IOException
  {
    if (value == null) {
      writeNull();
      return;
    }
    if ((value instanceof String)) {
      writeString((String)value);
      return;
    }
    if ((value instanceof Number)) {
      Number n = (Number)value;
      if ((n instanceof Integer)) {
        writeNumber(n.intValue());
        return; }
      if ((n instanceof Long)) {
        writeNumber(n.longValue());
        return; }
      if ((n instanceof Double)) {
        writeNumber(n.doubleValue());
        return; }
      if ((n instanceof Float)) {
        writeNumber(n.floatValue());
        return; }
      if ((n instanceof Short)) {
        writeNumber(n.shortValue());
        return; }
      if ((n instanceof Byte)) {
        writeNumber((short)n.byteValue());
        return; }
      if ((n instanceof BigInteger)) {
        writeNumber((BigInteger)n);
        return; }
      if ((n instanceof BigDecimal)) {
        writeNumber((BigDecimal)n);
        return;
      }
      
      if ((n instanceof AtomicInteger)) {
        writeNumber(((AtomicInteger)n).get());
        return; }
      if ((n instanceof AtomicLong)) {
        writeNumber(((AtomicLong)n).get());
        return;
      }
    } else { if ((value instanceof byte[])) {
        writeBinary((byte[])value);
        return; }
      if ((value instanceof Boolean)) {
        writeBoolean(((Boolean)value).booleanValue());
        return; }
      if ((value instanceof AtomicBoolean)) {
        writeBoolean(((AtomicBoolean)value).get());
        return;
      }
    }
    throw new IllegalStateException("No ObjectCodec defined for the generator, can only serialize simple wrapper types (type passed " + value.getClass().getName() + ")");
  }
}
