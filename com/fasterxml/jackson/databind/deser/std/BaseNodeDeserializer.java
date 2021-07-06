package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;






















































































































































abstract class BaseNodeDeserializer<T extends JsonNode>
  extends StdDeserializer<T>
{
  protected final Boolean _supportsUpdates;
  
  public BaseNodeDeserializer(Class<T> vc, Boolean supportsUpdates)
  {
    super(vc);
    _supportsUpdates = supportsUpdates;
  }
  



  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return typeDeserializer.deserializeTypedFromAny(p, ctxt);
  }
  



  public boolean isCachable()
  {
    return true;
  }
  
  public Boolean supportsUpdate(DeserializationConfig config) {
    return _supportsUpdates;
  }
  























  protected void _handleDuplicateField(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue)
    throws JsonProcessingException
  {
    if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY))
    {


      ctxt.reportInputMismatch(JsonNode.class, "Duplicate field '%s' for `ObjectNode`: not allowed when `DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY` enabled", new Object[] { fieldName });
    }
  }
  












  protected final ObjectNode deserializeObject(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
    throws IOException
  {
    ObjectNode node = nodeFactory.objectNode();
    for (String key = p.nextFieldName(); 
        key != null; key = p.nextFieldName())
    {
      JsonToken t = p.nextToken();
      if (t == null)
        t = JsonToken.NOT_AVAILABLE;
      JsonNode value;
      JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; switch (t.id()) {
      case 1: 
        value = deserializeObject(p, ctxt, nodeFactory);
        break;
      case 3: 
        value = deserializeArray(p, ctxt, nodeFactory);
        break;
      case 12: 
        value = _fromEmbedded(p, ctxt, nodeFactory);
        break;
      case 6: 
        value = nodeFactory.textNode(p.getText());
        break;
      case 7: 
        value = _fromInt(p, ctxt, nodeFactory);
        break;
      case 9: 
        value = nodeFactory.booleanNode(true);
        break;
      case 10: 
        value = nodeFactory.booleanNode(false);
        break;
      case 11: 
        value = nodeFactory.nullNode();
        break;
      case 2: case 4: case 5: case 8: default: 
        value = deserializeAny(p, ctxt, nodeFactory);
      }
      JsonNode old = node.replace(key, value);
      if (old != null) {
        _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
      }
    }
    
    return node;
  }
  






  protected final ObjectNode deserializeObjectAtName(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
    throws IOException
  {
    ObjectNode node = nodeFactory.objectNode();
    for (String key = p.getCurrentName(); 
        key != null; key = p.nextFieldName())
    {
      JsonToken t = p.nextToken();
      if (t == null)
        t = JsonToken.NOT_AVAILABLE;
      JsonNode value;
      JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; switch (t.id()) {
      case 1: 
        value = deserializeObject(p, ctxt, nodeFactory);
        break;
      case 3: 
        value = deserializeArray(p, ctxt, nodeFactory);
        break;
      case 12: 
        value = _fromEmbedded(p, ctxt, nodeFactory);
        break;
      case 6: 
        value = nodeFactory.textNode(p.getText());
        break;
      case 7: 
        value = _fromInt(p, ctxt, nodeFactory);
        break;
      case 9: 
        value = nodeFactory.booleanNode(true);
        break;
      case 10: 
        value = nodeFactory.booleanNode(false);
        break;
      case 11: 
        value = nodeFactory.nullNode();
        break;
      case 2: case 4: case 5: case 8: default: 
        value = deserializeAny(p, ctxt, nodeFactory);
      }
      JsonNode old = node.replace(key, value);
      if (old != null) {
        _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
      }
    }
    
    return node;
  }
  



  protected final JsonNode updateObject(JsonParser p, DeserializationContext ctxt, ObjectNode node)
    throws IOException
  {
    String key;
    


    if (p.isExpectedStartObjectToken()) {
      key = p.nextFieldName();
    }
    else if (!p.hasToken(JsonToken.FIELD_NAME)) {
      return (JsonNode)deserialize(p, ctxt);
    }
    for (String key = p.getCurrentName(); 
        
        key != null; key = p.nextFieldName())
    {
      JsonToken t = p.nextToken();
      

      JsonNode old = node.get(key);
      if (old != null) {
        if ((old instanceof ObjectNode)) {
          JsonNode newValue = updateObject(p, ctxt, (ObjectNode)old);
          if (newValue == old) continue;
          node.set(key, newValue); continue;
        }
        

        if ((old instanceof ArrayNode)) {
          JsonNode newValue = updateArray(p, ctxt, (ArrayNode)old);
          if (newValue == old) continue;
          node.set(key, newValue); continue;
        }
      }
      

      if (t == null) {
        t = JsonToken.NOT_AVAILABLE;
      }
      
      JsonNodeFactory nodeFactory = ctxt.getNodeFactory();
      JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; JsonNode value; switch (t.id()) {
      case 1: 
        value = deserializeObject(p, ctxt, nodeFactory);
        break;
      case 3: 
        value = deserializeArray(p, ctxt, nodeFactory);
        break;
      case 12: 
        value = _fromEmbedded(p, ctxt, nodeFactory);
        break;
      case 6: 
        value = nodeFactory.textNode(p.getText());
        break;
      case 7: 
        value = _fromInt(p, ctxt, nodeFactory);
        break;
      case 9: 
        value = nodeFactory.booleanNode(true);
        break;
      case 10: 
        value = nodeFactory.booleanNode(false);
        break;
      case 11: 
        value = nodeFactory.nullNode();
        break;
      case 2: case 4: case 5: case 8: default: 
        value = deserializeAny(p, ctxt, nodeFactory);
      }
      if (old != null) {
        _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
      }
      
      node.set(key, value);
    }
    return node;
  }
  
  protected final ArrayNode deserializeArray(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
    throws IOException
  {
    ArrayNode node = nodeFactory.arrayNode();
    for (;;) {
      JsonToken t = p.nextToken();
      switch (t.id()) {
      case 1: 
        node.add(deserializeObject(p, ctxt, nodeFactory));
        break;
      case 3: 
        node.add(deserializeArray(p, ctxt, nodeFactory));
        break;
      case 4: 
        return node;
      case 12: 
        node.add(_fromEmbedded(p, ctxt, nodeFactory));
        break;
      case 6: 
        node.add(nodeFactory.textNode(p.getText()));
        break;
      case 7: 
        node.add(_fromInt(p, ctxt, nodeFactory));
        break;
      case 9: 
        node.add(nodeFactory.booleanNode(true));
        break;
      case 10: 
        node.add(nodeFactory.booleanNode(false));
        break;
      case 11: 
        node.add(nodeFactory.nullNode());
        break;
      case 2: case 5: case 8: default: 
        node.add(deserializeAny(p, ctxt, nodeFactory));
      }
      
    }
  }
  






  protected final JsonNode updateArray(JsonParser p, DeserializationContext ctxt, ArrayNode node)
    throws IOException
  {
    JsonNodeFactory nodeFactory = ctxt.getNodeFactory();
    for (;;) {
      JsonToken t = p.nextToken();
      switch (t.id()) {
      case 1: 
        node.add(deserializeObject(p, ctxt, nodeFactory));
        break;
      case 3: 
        node.add(deserializeArray(p, ctxt, nodeFactory));
        break;
      case 4: 
        return node;
      case 12: 
        node.add(_fromEmbedded(p, ctxt, nodeFactory));
        break;
      case 6: 
        node.add(nodeFactory.textNode(p.getText()));
        break;
      case 7: 
        node.add(_fromInt(p, ctxt, nodeFactory));
        break;
      case 9: 
        node.add(nodeFactory.booleanNode(true));
        break;
      case 10: 
        node.add(nodeFactory.booleanNode(false));
        break;
      case 11: 
        node.add(nodeFactory.nullNode());
        break;
      case 2: case 5: case 8: default: 
        node.add(deserializeAny(p, ctxt, nodeFactory));
      }
      
    }
  }
  
  protected final JsonNode deserializeAny(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
    throws IOException
  {
    switch (p.currentTokenId()) {
    case 2: 
      return nodeFactory.objectNode();
    case 5: 
      return deserializeObjectAtName(p, ctxt, nodeFactory);
    case 12: 
      return _fromEmbedded(p, ctxt, nodeFactory);
    case 6: 
      return nodeFactory.textNode(p.getText());
    case 7: 
      return _fromInt(p, ctxt, nodeFactory);
    case 8: 
      return _fromFloat(p, ctxt, nodeFactory);
    case 9: 
      return nodeFactory.booleanNode(true);
    case 10: 
      return nodeFactory.booleanNode(false);
    case 11: 
      return nodeFactory.nullNode();
    }
    
    













    return (JsonNode)ctxt.handleUnexpectedToken(handledType(), p);
  }
  

  protected final JsonNode _fromInt(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
    throws IOException
  {
    int feats = ctxt.getDeserializationFeatures();
    JsonParser.NumberType nt; JsonParser.NumberType nt; if ((feats & F_MASK_INT_COERCIONS) != 0) { JsonParser.NumberType nt;
      if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
        nt = JsonParser.NumberType.BIG_INTEGER; } else { JsonParser.NumberType nt;
        if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
          nt = JsonParser.NumberType.LONG;
        } else
          nt = p.getNumberType();
      }
    } else {
      nt = p.getNumberType();
    }
    if (nt == JsonParser.NumberType.INT) {
      return nodeFactory.numberNode(p.getIntValue());
    }
    if (nt == JsonParser.NumberType.LONG) {
      return nodeFactory.numberNode(p.getLongValue());
    }
    return nodeFactory.numberNode(p.getBigIntegerValue());
  }
  
  protected final JsonNode _fromFloat(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
    throws IOException
  {
    JsonParser.NumberType nt = p.getNumberType();
    if (nt == JsonParser.NumberType.BIG_DECIMAL) {
      return nodeFactory.numberNode(p.getDecimalValue());
    }
    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS))
    {

      if (p.isNaN()) {
        return nodeFactory.numberNode(p.getDoubleValue());
      }
      return nodeFactory.numberNode(p.getDecimalValue());
    }
    if (nt == JsonParser.NumberType.FLOAT) {
      return nodeFactory.numberNode(p.getFloatValue());
    }
    return nodeFactory.numberNode(p.getDoubleValue());
  }
  
  protected final JsonNode _fromEmbedded(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory)
    throws IOException
  {
    Object ob = p.getEmbeddedObject();
    if (ob == null) {
      return nodeFactory.nullNode();
    }
    Class<?> type = ob.getClass();
    if (type == [B.class) {
      return nodeFactory.binaryNode((byte[])ob);
    }
    
    if ((ob instanceof RawValue)) {
      return nodeFactory.rawValueNode((RawValue)ob);
    }
    if ((ob instanceof JsonNode))
    {
      return (JsonNode)ob;
    }
    
    return nodeFactory.pojoNode(ob);
  }
}
