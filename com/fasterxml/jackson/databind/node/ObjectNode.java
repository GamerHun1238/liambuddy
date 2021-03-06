package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ObjectNode extends ContainerNode<ObjectNode> implements java.io.Serializable
{
  private static final long serialVersionUID = 1L;
  protected final Map<String, JsonNode> _children;
  
  public ObjectNode(JsonNodeFactory nc)
  {
    super(nc);
    _children = new java.util.LinkedHashMap();
  }
  


  public ObjectNode(JsonNodeFactory nc, Map<String, JsonNode> kids)
  {
    super(nc);
    _children = kids;
  }
  
  protected JsonNode _at(JsonPointer ptr)
  {
    return get(ptr.getMatchingProperty());
  }
  






  public ObjectNode deepCopy()
  {
    ObjectNode ret = new ObjectNode(_nodeFactory);
    
    for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
      _children.put(entry.getKey(), ((JsonNode)entry.getValue()).deepCopy());
    }
    return ret;
  }
  






  public boolean isEmpty(SerializerProvider serializers)
  {
    return _children.isEmpty();
  }
  






  public JsonNodeType getNodeType()
  {
    return JsonNodeType.OBJECT;
  }
  
  public final boolean isObject()
  {
    return true;
  }
  
  public JsonToken asToken() { return JsonToken.START_OBJECT; }
  
  public int size()
  {
    return _children.size();
  }
  
  public boolean isEmpty() {
    return _children.isEmpty();
  }
  
  public Iterator<JsonNode> elements() {
    return _children.values().iterator();
  }
  
  public JsonNode get(int index) {
    return null;
  }
  
  public JsonNode get(String fieldName) {
    return (JsonNode)_children.get(fieldName);
  }
  
  public Iterator<String> fieldNames()
  {
    return _children.keySet().iterator();
  }
  
  public JsonNode path(int index)
  {
    return MissingNode.getInstance();
  }
  

  public JsonNode path(String fieldName)
  {
    JsonNode n = (JsonNode)_children.get(fieldName);
    if (n != null) {
      return n;
    }
    return MissingNode.getInstance();
  }
  
  public JsonNode required(String fieldName)
  {
    JsonNode n = (JsonNode)_children.get(fieldName);
    if (n != null) {
      return n;
    }
    return (JsonNode)_reportRequiredViolation("No value for property '%s' of `ObjectNode`", new Object[] { fieldName });
  }
  




  public Iterator<Map.Entry<String, JsonNode>> fields()
  {
    return _children.entrySet().iterator();
  }
  

  public ObjectNode with(String propertyName)
  {
    JsonNode n = (JsonNode)_children.get(propertyName);
    if (n != null) {
      if ((n instanceof ObjectNode)) {
        return (ObjectNode)n;
      }
      

      throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ObjectNode (but " + n.getClass().getName() + ")");
    }
    ObjectNode result = objectNode();
    _children.put(propertyName, result);
    return result;
  }
  


  public ArrayNode withArray(String propertyName)
  {
    JsonNode n = (JsonNode)_children.get(propertyName);
    if (n != null) {
      if ((n instanceof ArrayNode)) {
        return (ArrayNode)n;
      }
      

      throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ArrayNode (but " + n.getClass().getName() + ")");
    }
    ArrayNode result = arrayNode();
    _children.put(propertyName, result);
    return result;
  }
  

  public boolean equals(Comparator<JsonNode> comparator, JsonNode o)
  {
    if (!(o instanceof ObjectNode)) {
      return false;
    }
    ObjectNode other = (ObjectNode)o;
    Map<String, JsonNode> m1 = _children;
    Map<String, JsonNode> m2 = _children;
    
    int len = m1.size();
    if (m2.size() != len) {
      return false;
    }
    
    for (Map.Entry<String, JsonNode> entry : m1.entrySet()) {
      JsonNode v2 = (JsonNode)m2.get(entry.getKey());
      if ((v2 == null) || (!((JsonNode)entry.getValue()).equals(comparator, v2))) {
        return false;
      }
    }
    return true;
  }
  







  public JsonNode findValue(String fieldName)
  {
    for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
      if (fieldName.equals(entry.getKey())) {
        return (JsonNode)entry.getValue();
      }
      JsonNode value = ((JsonNode)entry.getValue()).findValue(fieldName);
      if (value != null) {
        return value;
      }
    }
    return null;
  }
  

  public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar)
  {
    for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
      if (fieldName.equals(entry.getKey())) {
        if (foundSoFar == null) {
          foundSoFar = new ArrayList();
        }
        foundSoFar.add(entry.getValue());
      } else {
        foundSoFar = ((JsonNode)entry.getValue()).findValues(fieldName, foundSoFar);
      }
    }
    return foundSoFar;
  }
  

  public List<String> findValuesAsText(String fieldName, List<String> foundSoFar)
  {
    for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
      if (fieldName.equals(entry.getKey())) {
        if (foundSoFar == null) {
          foundSoFar = new ArrayList();
        }
        foundSoFar.add(((JsonNode)entry.getValue()).asText());
      } else {
        foundSoFar = ((JsonNode)entry.getValue()).findValuesAsText(fieldName, foundSoFar);
      }
    }
    
    return foundSoFar;
  }
  

  public ObjectNode findParent(String fieldName)
  {
    for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
      if (fieldName.equals(entry.getKey())) {
        return this;
      }
      JsonNode value = ((JsonNode)entry.getValue()).findParent(fieldName);
      if (value != null) {
        return (ObjectNode)value;
      }
    }
    return null;
  }
  

  public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar)
  {
    for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
      if (fieldName.equals(entry.getKey())) {
        if (foundSoFar == null) {
          foundSoFar = new ArrayList();
        }
        foundSoFar.add(this);
      }
      else {
        foundSoFar = ((JsonNode)entry.getValue()).findParents(fieldName, foundSoFar);
      }
    }
    return foundSoFar;
  }
  













  public void serialize(JsonGenerator g, SerializerProvider provider)
    throws IOException
  {
    boolean trimEmptyArray = (provider != null) && (!provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS));
    g.writeStartObject(this);
    for (Map.Entry<String, JsonNode> en : _children.entrySet())
    {




      BaseJsonNode value = (BaseJsonNode)en.getValue();
      



      if ((!trimEmptyArray) || (!value.isArray()) || (!value.isEmpty(provider)))
      {

        g.writeFieldName((String)en.getKey());
        value.serialize(g, provider);
      } }
    g.writeEndObject();
  }
  




  public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    boolean trimEmptyArray = (provider != null) && (!provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS));
    
    com.fasterxml.jackson.core.type.WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
      .typeId(this, JsonToken.START_OBJECT));
    for (Map.Entry<String, JsonNode> en : _children.entrySet()) {
      BaseJsonNode value = (BaseJsonNode)en.getValue();
      



      if ((!trimEmptyArray) || (!value.isArray()) || (!value.isEmpty(provider)))
      {


        g.writeFieldName((String)en.getKey());
        value.serialize(g, provider);
      } }
    typeSer.writeTypeSuffix(g, typeIdDef);
  }
  

























  public <T extends JsonNode> T set(String fieldName, JsonNode value)
  {
    if (value == null) {
      value = nullNode();
    }
    _children.put(fieldName, value);
    return this;
  }
  













  public <T extends JsonNode> T setAll(Map<String, ? extends JsonNode> properties)
  {
    for (Map.Entry<String, ? extends JsonNode> en : properties.entrySet()) {
      JsonNode n = (JsonNode)en.getValue();
      if (n == null) {
        n = nullNode();
      }
      _children.put(en.getKey(), n);
    }
    return this;
  }
  













  public <T extends JsonNode> T setAll(ObjectNode other)
  {
    _children.putAll(_children);
    return this;
  }
  












  public JsonNode replace(String fieldName, JsonNode value)
  {
    if (value == null) {
      value = nullNode();
    }
    return (JsonNode)_children.put(fieldName, value);
  }
  











  public <T extends JsonNode> T without(String fieldName)
  {
    _children.remove(fieldName);
    return this;
  }
  













  public <T extends JsonNode> T without(Collection<String> fieldNames)
  {
    _children.keySet().removeAll(fieldNames);
    return this;
  }
  


















  @Deprecated
  public JsonNode put(String fieldName, JsonNode value)
  {
    if (value == null) {
      value = nullNode();
    }
    return (JsonNode)_children.put(fieldName, value);
  }
  






  public JsonNode remove(String fieldName)
  {
    return (JsonNode)_children.remove(fieldName);
  }
  








  public ObjectNode remove(Collection<String> fieldNames)
  {
    _children.keySet().removeAll(fieldNames);
    return this;
  }
  







  public ObjectNode removeAll()
  {
    _children.clear();
    return this;
  }
  









  @Deprecated
  public JsonNode putAll(Map<String, ? extends JsonNode> properties)
  {
    return setAll(properties);
  }
  









  @Deprecated
  public JsonNode putAll(ObjectNode other)
  {
    return setAll(other);
  }
  








  public ObjectNode retain(Collection<String> fieldNames)
  {
    _children.keySet().retainAll(fieldNames);
    return this;
  }
  







  public ObjectNode retain(String... fieldNames)
  {
    return retain(java.util.Arrays.asList(fieldNames));
  }
  

















  public ArrayNode putArray(String fieldName)
  {
    ArrayNode n = arrayNode();
    _put(fieldName, n);
    return n;
  }
  











  public ObjectNode putObject(String fieldName)
  {
    ObjectNode n = objectNode();
    _put(fieldName, n);
    return n;
  }
  


  public ObjectNode putPOJO(String fieldName, Object pojo)
  {
    return _put(fieldName, pojoNode(pojo));
  }
  


  public ObjectNode putRawValue(String fieldName, RawValue raw)
  {
    return _put(fieldName, rawValueNode(raw));
  }
  



  public ObjectNode putNull(String fieldName)
  {
    _children.put(fieldName, nullNode());
    return this;
  }
  




  public ObjectNode put(String fieldName, short v)
  {
    return _put(fieldName, numberNode(v));
  }
  





  public ObjectNode put(String fieldName, Short v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      numberNode(v.shortValue()));
  }
  








  public ObjectNode put(String fieldName, int v)
  {
    return _put(fieldName, numberNode(v));
  }
  





  public ObjectNode put(String fieldName, Integer v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      numberNode(v.intValue()));
  }
  








  public ObjectNode put(String fieldName, long v)
  {
    return _put(fieldName, numberNode(v));
  }
  











  public ObjectNode put(String fieldName, Long v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      numberNode(v.longValue()));
  }
  




  public ObjectNode put(String fieldName, float v)
  {
    return _put(fieldName, numberNode(v));
  }
  





  public ObjectNode put(String fieldName, Float v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      numberNode(v.floatValue()));
  }
  




  public ObjectNode put(String fieldName, double v)
  {
    return _put(fieldName, numberNode(v));
  }
  





  public ObjectNode put(String fieldName, Double v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      numberNode(v.doubleValue()));
  }
  




  public ObjectNode put(String fieldName, java.math.BigDecimal v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      numberNode(v));
  }
  






  public ObjectNode put(String fieldName, BigInteger v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      numberNode(v));
  }
  




  public ObjectNode put(String fieldName, String v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      textNode(v));
  }
  




  public ObjectNode put(String fieldName, boolean v)
  {
    return _put(fieldName, booleanNode(v));
  }
  





  public ObjectNode put(String fieldName, Boolean v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      booleanNode(v.booleanValue()));
  }
  




  public ObjectNode put(String fieldName, byte[] v)
  {
    return _put(fieldName, v == null ? nullNode() : 
      binaryNode(v));
  }
  







  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if ((o instanceof ObjectNode)) {
      return _childrenEqual((ObjectNode)o);
    }
    return false;
  }
  



  protected boolean _childrenEqual(ObjectNode other)
  {
    return _children.equals(_children);
  }
  

  public int hashCode()
  {
    return _children.hashCode();
  }
  






  protected ObjectNode _put(String fieldName, JsonNode value)
  {
    _children.put(fieldName, value);
    return this;
  }
}
