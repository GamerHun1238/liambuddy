package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class NumberSerializers
{
  protected NumberSerializers() {}
  
  public static void addAll(Map<String, JsonSerializer<?>> allDeserializers)
  {
    allDeserializers.put(Integer.class.getName(), new IntegerSerializer(Integer.class));
    allDeserializers.put(Integer.TYPE.getName(), new IntegerSerializer(Integer.TYPE));
    allDeserializers.put(Long.class.getName(), new LongSerializer(Long.class));
    allDeserializers.put(Long.TYPE.getName(), new LongSerializer(Long.TYPE));
    
    allDeserializers.put(Byte.class.getName(), IntLikeSerializer.instance);
    allDeserializers.put(Byte.TYPE.getName(), IntLikeSerializer.instance);
    allDeserializers.put(Short.class.getName(), ShortSerializer.instance);
    allDeserializers.put(Short.TYPE.getName(), ShortSerializer.instance);
    

    allDeserializers.put(Double.class.getName(), new DoubleSerializer(Double.class));
    allDeserializers.put(Double.TYPE.getName(), new DoubleSerializer(Double.TYPE));
    allDeserializers.put(Float.class.getName(), FloatSerializer.instance);
    allDeserializers.put(Float.TYPE.getName(), FloatSerializer.instance);
  }
  




  public static abstract class Base<T>
    extends StdScalarSerializer<T>
    implements com.fasterxml.jackson.databind.ser.ContextualSerializer
  {
    protected final JsonParser.NumberType _numberType;
    



    protected final String _schemaType;
    



    protected final boolean _isInt;
    



    protected Base(Class<?> cls, JsonParser.NumberType numberType, String schemaType)
    {
      super(false);
      _numberType = numberType;
      _schemaType = schemaType;
      _isInt = ((numberType == JsonParser.NumberType.INT) || (numberType == JsonParser.NumberType.LONG) || (numberType == JsonParser.NumberType.BIG_INTEGER));
    }
    


    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
    {
      return createSchemaNode(_schemaType, true);
    }
    

    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
      throws JsonMappingException
    {
      if (_isInt) {
        visitIntFormat(visitor, typeHint, _numberType);
      } else {
        visitFloatFormat(visitor, typeHint, _numberType);
      }
    }
    

    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
      throws JsonMappingException
    {
      JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
      if (format != null) {
        switch (NumberSerializers.1.$SwitchMap$com$fasterxml$jackson$annotation$JsonFormat$Shape[format.getShape().ordinal()]) {
        case 1: 
          if (handledType() == java.math.BigDecimal.class) {
            return NumberSerializer.bigDecimalAsStringSerializer();
          }
          return ToStringSerializer.instance;
        }
        
      }
      return this;
    }
  }
  




  @JacksonStdImpl
  public static class ShortSerializer
    extends NumberSerializers.Base<Object>
  {
    static final ShortSerializer instance = new ShortSerializer();
    
    public ShortSerializer() {
      super(JsonParser.NumberType.INT, "number");
    }
    
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
      throws IOException
    {
      gen.writeNumber(((Short)value).shortValue());
    }
  }
  







  @JacksonStdImpl
  public static class IntegerSerializer
    extends NumberSerializers.Base<Object>
  {
    public IntegerSerializer(Class<?> type)
    {
      super(JsonParser.NumberType.INT, "integer");
    }
    
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
      throws IOException
    {
      gen.writeNumber(((Integer)value).intValue());
    }
    



    public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
      throws IOException
    {
      serialize(value, gen, provider);
    }
  }
  



  @JacksonStdImpl
  public static class IntLikeSerializer
    extends NumberSerializers.Base<Object>
  {
    static final IntLikeSerializer instance = new IntLikeSerializer();
    
    public IntLikeSerializer() {
      super(JsonParser.NumberType.INT, "integer");
    }
    
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
      throws IOException
    {
      gen.writeNumber(((Number)value).intValue());
    }
  }
  
  @JacksonStdImpl
  public static class LongSerializer extends NumberSerializers.Base<Object> {
    public LongSerializer(Class<?> cls) {
      super(JsonParser.NumberType.LONG, "number");
    }
    
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
      throws IOException
    {
      gen.writeNumber(((Long)value).longValue());
    }
  }
  
  @JacksonStdImpl
  public static class FloatSerializer extends NumberSerializers.Base<Object> {
    static final FloatSerializer instance = new FloatSerializer();
    
    public FloatSerializer() {
      super(JsonParser.NumberType.FLOAT, "number");
    }
    
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
      throws IOException
    {
      gen.writeNumber(((Float)value).floatValue());
    }
  }
  




  @JacksonStdImpl
  public static class DoubleSerializer
    extends NumberSerializers.Base<Object>
  {
    public DoubleSerializer(Class<?> cls)
    {
      super(JsonParser.NumberType.DOUBLE, "number");
    }
    
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
      throws IOException
    {
      gen.writeNumber(((Double)value).doubleValue());
    }
    




    public void serializeWithType(Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
      throws IOException
    {
      Double d = (Double)value;
      if (notFinite(d.doubleValue())) {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
        
          .typeId(value, JsonToken.VALUE_NUMBER_FLOAT));
        g.writeNumber(d.doubleValue());
        typeSer.writeTypeSuffix(g, typeIdDef);
      } else {
        g.writeNumber(d.doubleValue());
      }
    }
    
    public static boolean notFinite(double value)
    {
      return (Double.isNaN(value)) || (Double.isInfinite(value));
    }
  }
}
