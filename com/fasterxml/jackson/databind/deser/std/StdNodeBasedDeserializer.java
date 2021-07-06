package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;













public abstract class StdNodeBasedDeserializer<T>
  extends StdDeserializer<T>
  implements ResolvableDeserializer
{
  private static final long serialVersionUID = 1L;
  protected JsonDeserializer<Object> _treeDeserializer;
  
  protected StdNodeBasedDeserializer(JavaType targetType)
  {
    super(targetType);
  }
  
  protected StdNodeBasedDeserializer(Class<T> targetType) {
    super(targetType);
  }
  



  protected StdNodeBasedDeserializer(StdNodeBasedDeserializer<?> src)
  {
    super(src);
    _treeDeserializer = _treeDeserializer;
  }
  
  public void resolve(DeserializationContext ctxt) throws JsonMappingException
  {
    _treeDeserializer = ctxt.findRootValueDeserializer(ctxt.constructType(JsonNode.class));
  }
  





  public abstract T convert(JsonNode paramJsonNode, DeserializationContext paramDeserializationContext)
    throws IOException;
  





  public T deserialize(JsonParser jp, DeserializationContext ctxt)
    throws IOException
  {
    JsonNode n = (JsonNode)_treeDeserializer.deserialize(jp, ctxt);
    return convert(n, ctxt);
  }
  





  public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer td)
    throws IOException, JsonProcessingException
  {
    JsonNode n = (JsonNode)_treeDeserializer.deserializeWithType(jp, ctxt, td);
    return convert(n, ctxt);
  }
}
