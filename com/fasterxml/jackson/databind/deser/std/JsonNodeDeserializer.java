package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;








public class JsonNodeDeserializer
  extends BaseNodeDeserializer<JsonNode>
{
  private static final JsonNodeDeserializer instance = new JsonNodeDeserializer();
  


  protected JsonNodeDeserializer()
  {
    super(JsonNode.class, null);
  }
  



  public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass)
  {
    if (nodeClass == ObjectNode.class) {
      return ObjectDeserializer.getInstance();
    }
    if (nodeClass == ArrayNode.class) {
      return ArrayDeserializer.getInstance();
    }
    
    return instance;
  }
  






  public JsonNode getNullValue(DeserializationContext ctxt)
  {
    return ctxt.getNodeFactory().nullNode();
  }
  





  public JsonNode deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    switch (p.currentTokenId()) {
    case 1: 
      return deserializeObject(p, ctxt, ctxt.getNodeFactory());
    case 3: 
      return deserializeArray(p, ctxt, ctxt.getNodeFactory());
    }
    
    return deserializeAny(p, ctxt, ctxt.getNodeFactory());
  }
  




  static final class ObjectDeserializer
    extends BaseNodeDeserializer<ObjectNode>
  {
    private static final long serialVersionUID = 1L;
    


    protected static final ObjectDeserializer _instance = new ObjectDeserializer();
    
    protected ObjectDeserializer() { super(Boolean.valueOf(true)); }
    
    public static ObjectDeserializer getInstance() { return _instance; }
    
    public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (p.isExpectedStartObjectToken()) {
        return deserializeObject(p, ctxt, ctxt.getNodeFactory());
      }
      if (p.hasToken(JsonToken.FIELD_NAME)) {
        return deserializeObjectAtName(p, ctxt, ctxt.getNodeFactory());
      }
      

      if (p.hasToken(JsonToken.END_OBJECT)) {
        return ctxt.getNodeFactory().objectNode();
      }
      return (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p);
    }
    






    public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt, ObjectNode node)
      throws IOException
    {
      if ((p.isExpectedStartObjectToken()) || (p.hasToken(JsonToken.FIELD_NAME))) {
        return (ObjectNode)updateObject(p, ctxt, node);
      }
      return (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p);
    }
  }
  

  static final class ArrayDeserializer
    extends BaseNodeDeserializer<ArrayNode>
  {
    private static final long serialVersionUID = 1L;
    protected static final ArrayDeserializer _instance = new ArrayDeserializer();
    
    protected ArrayDeserializer() { super(Boolean.valueOf(true)); }
    
    public static ArrayDeserializer getInstance() { return _instance; }
    
    public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if (p.isExpectedStartArrayToken()) {
        return deserializeArray(p, ctxt, ctxt.getNodeFactory());
      }
      return (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
    }
    






    public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt, ArrayNode node)
      throws IOException
    {
      if (p.isExpectedStartArrayToken()) {
        return (ArrayNode)updateArray(p, ctxt, node);
      }
      return (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
    }
  }
}
