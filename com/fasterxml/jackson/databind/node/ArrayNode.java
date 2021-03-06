package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;




public class ArrayNode
  extends ContainerNode<ArrayNode>
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final List<JsonNode> _children;
  
  public ArrayNode(JsonNodeFactory nf)
  {
    super(nf);
    _children = new ArrayList();
  }
  


  public ArrayNode(JsonNodeFactory nf, int capacity)
  {
    super(nf);
    _children = new ArrayList(capacity);
  }
  


  public ArrayNode(JsonNodeFactory nf, List<JsonNode> children)
  {
    super(nf);
    _children = children;
  }
  
  protected JsonNode _at(JsonPointer ptr)
  {
    return get(ptr.getMatchingIndex());
  }
  



  public ArrayNode deepCopy()
  {
    ArrayNode ret = new ArrayNode(_nodeFactory);
    
    for (JsonNode element : _children) {
      _children.add(element.deepCopy());
    }
    return ret;
  }
  






  public boolean isEmpty(SerializerProvider serializers)
  {
    return _children.isEmpty();
  }
  






  public JsonNodeType getNodeType()
  {
    return JsonNodeType.ARRAY;
  }
  
  public boolean isArray()
  {
    return true;
  }
  
  public JsonToken asToken() { return JsonToken.START_ARRAY; }
  
  public int size()
  {
    return _children.size();
  }
  
  public boolean isEmpty() {
    return _children.isEmpty();
  }
  
  public Iterator<JsonNode> elements() {
    return _children.iterator();
  }
  
  public JsonNode get(int index)
  {
    if ((index >= 0) && (index < _children.size())) {
      return (JsonNode)_children.get(index);
    }
    return null;
  }
  
  public JsonNode get(String fieldName) {
    return null;
  }
  
  public JsonNode path(String fieldName) { return MissingNode.getInstance(); }
  
  public JsonNode path(int index)
  {
    if ((index >= 0) && (index < _children.size())) {
      return (JsonNode)_children.get(index);
    }
    return MissingNode.getInstance();
  }
  
  public JsonNode required(int index)
  {
    if ((index >= 0) && (index < _children.size())) {
      return (JsonNode)_children.get(index);
    }
    return (JsonNode)_reportRequiredViolation("No value at index #%d [0, %d) of `ArrayNode`", new Object[] {
      Integer.valueOf(index), Integer.valueOf(_children.size()) });
  }
  

  public boolean equals(Comparator<JsonNode> comparator, JsonNode o)
  {
    if (!(o instanceof ArrayNode)) {
      return false;
    }
    ArrayNode other = (ArrayNode)o;
    int len = _children.size();
    if (other.size() != len) {
      return false;
    }
    List<JsonNode> l1 = _children;
    List<JsonNode> l2 = _children;
    for (int i = 0; i < len; i++) {
      if (!((JsonNode)l1.get(i)).equals(comparator, (JsonNode)l2.get(i))) {
        return false;
      }
    }
    return true;
  }
  






  public void serialize(JsonGenerator f, SerializerProvider provider)
    throws IOException
  {
    List<JsonNode> c = _children;
    int size = c.size();
    f.writeStartArray(this, size);
    for (int i = 0; i < size; i++)
    {
      JsonNode n = (JsonNode)c.get(i);
      ((BaseJsonNode)n).serialize(f, provider);
    }
    f.writeEndArray();
  }
  

  public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
      .typeId(this, JsonToken.START_ARRAY));
    for (JsonNode n : _children) {
      ((BaseJsonNode)n).serialize(g, provider);
    }
    typeSer.writeTypeSuffix(g, typeIdDef);
  }
  







  public JsonNode findValue(String fieldName)
  {
    for (JsonNode node : _children) {
      JsonNode value = node.findValue(fieldName);
      if (value != null) {
        return value;
      }
    }
    return null;
  }
  

  public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar)
  {
    for (JsonNode node : _children) {
      foundSoFar = node.findValues(fieldName, foundSoFar);
    }
    return foundSoFar;
  }
  

  public List<String> findValuesAsText(String fieldName, List<String> foundSoFar)
  {
    for (JsonNode node : _children) {
      foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
    }
    return foundSoFar;
  }
  

  public ObjectNode findParent(String fieldName)
  {
    for (JsonNode node : _children) {
      JsonNode parent = node.findParent(fieldName);
      if (parent != null) {
        return (ObjectNode)parent;
      }
    }
    return null;
  }
  

  public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar)
  {
    for (JsonNode node : _children) {
      foundSoFar = node.findParents(fieldName, foundSoFar);
    }
    return foundSoFar;
  }
  

















  public JsonNode set(int index, JsonNode value)
  {
    if (value == null) {
      value = nullNode();
    }
    if ((index < 0) || (index >= _children.size())) {
      throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + size());
    }
    return (JsonNode)_children.set(index, value);
  }
  





  public ArrayNode add(JsonNode value)
  {
    if (value == null) {
      value = nullNode();
    }
    _add(value);
    return this;
  }
  








  public ArrayNode addAll(ArrayNode other)
  {
    _children.addAll(_children);
    return this;
  }
  







  public ArrayNode addAll(Collection<? extends JsonNode> nodes)
  {
    for (JsonNode node : nodes) {
      add(node);
    }
    return this;
  }
  









  public ArrayNode insert(int index, JsonNode value)
  {
    if (value == null) {
      value = nullNode();
    }
    _insert(index, value);
    return this;
  }
  







  public JsonNode remove(int index)
  {
    if ((index >= 0) && (index < _children.size())) {
      return (JsonNode)_children.remove(index);
    }
    return null;
  }
  







  public ArrayNode removeAll()
  {
    _children.clear();
    return this;
  }
  












  public ArrayNode addArray()
  {
    ArrayNode n = arrayNode();
    _add(n);
    return n;
  }
  






  public ObjectNode addObject()
  {
    ObjectNode n = objectNode();
    _add(n);
    return n;
  }
  






  public ArrayNode addPOJO(Object value)
  {
    if (value == null) {
      addNull();
    } else {
      _add(pojoNode(value));
    }
    return this;
  }
  




  public ArrayNode addRawValue(RawValue raw)
  {
    if (raw == null) {
      addNull();
    } else {
      _add(rawValueNode(raw));
    }
    return this;
  }
  





  public ArrayNode addNull()
  {
    _add(nullNode());
    return this;
  }
  




  public ArrayNode add(int v)
  {
    _add(numberNode(v));
    return this;
  }
  





  public ArrayNode add(Integer value)
  {
    if (value == null) {
      return addNull();
    }
    return _add(numberNode(value.intValue()));
  }
  



  public ArrayNode add(long v)
  {
    return _add(numberNode(v));
  }
  




  public ArrayNode add(Long value)
  {
    if (value == null) {
      return addNull();
    }
    return _add(numberNode(value.longValue()));
  }
  




  public ArrayNode add(float v)
  {
    return _add(numberNode(v));
  }
  





  public ArrayNode add(Float value)
  {
    if (value == null) {
      return addNull();
    }
    return _add(numberNode(value.floatValue()));
  }
  




  public ArrayNode add(double v)
  {
    return _add(numberNode(v));
  }
  





  public ArrayNode add(Double value)
  {
    if (value == null) {
      return addNull();
    }
    return _add(numberNode(value.doubleValue()));
  }
  




  public ArrayNode add(BigDecimal v)
  {
    if (v == null) {
      return addNull();
    }
    return _add(numberNode(v));
  }
  






  public ArrayNode add(BigInteger v)
  {
    if (v == null) {
      return addNull();
    }
    return _add(numberNode(v));
  }
  




  public ArrayNode add(String v)
  {
    if (v == null) {
      return addNull();
    }
    return _add(textNode(v));
  }
  




  public ArrayNode add(boolean v)
  {
    return _add(booleanNode(v));
  }
  





  public ArrayNode add(Boolean value)
  {
    if (value == null) {
      return addNull();
    }
    return _add(booleanNode(value.booleanValue()));
  }
  





  public ArrayNode add(byte[] v)
  {
    if (v == null) {
      return addNull();
    }
    return _add(binaryNode(v));
  }
  






  public ArrayNode insertArray(int index)
  {
    ArrayNode n = arrayNode();
    _insert(index, n);
    return n;
  }
  







  public ObjectNode insertObject(int index)
  {
    ObjectNode n = objectNode();
    _insert(index, n);
    return n;
  }
  






  public ArrayNode insertPOJO(int index, Object value)
  {
    if (value == null) {
      return insertNull(index);
    }
    return _insert(index, pojoNode(value));
  }
  






  public ArrayNode insertNull(int index)
  {
    _insert(index, nullNode());
    return this;
  }
  





  public ArrayNode insert(int index, int v)
  {
    _insert(index, numberNode(v));
    return this;
  }
  





  public ArrayNode insert(int index, Integer value)
  {
    if (value == null) {
      insertNull(index);
    } else {
      _insert(index, numberNode(value.intValue()));
    }
    return this;
  }
  





  public ArrayNode insert(int index, long v)
  {
    return _insert(index, numberNode(v));
  }
  





  public ArrayNode insert(int index, Long value)
  {
    if (value == null) {
      return insertNull(index);
    }
    return _insert(index, numberNode(value.longValue()));
  }
  





  public ArrayNode insert(int index, float v)
  {
    return _insert(index, numberNode(v));
  }
  





  public ArrayNode insert(int index, Float value)
  {
    if (value == null) {
      return insertNull(index);
    }
    return _insert(index, numberNode(value.floatValue()));
  }
  





  public ArrayNode insert(int index, double v)
  {
    return _insert(index, numberNode(v));
  }
  





  public ArrayNode insert(int index, Double value)
  {
    if (value == null) {
      return insertNull(index);
    }
    return _insert(index, numberNode(value.doubleValue()));
  }
  





  public ArrayNode insert(int index, BigDecimal v)
  {
    if (v == null) {
      return insertNull(index);
    }
    return _insert(index, numberNode(v));
  }
  







  public ArrayNode insert(int index, BigInteger v)
  {
    if (v == null) {
      return insertNull(index);
    }
    return _insert(index, numberNode(v));
  }
  





  public ArrayNode insert(int index, String v)
  {
    if (v == null) {
      return insertNull(index);
    }
    return _insert(index, textNode(v));
  }
  





  public ArrayNode insert(int index, boolean v)
  {
    return _insert(index, booleanNode(v));
  }
  





  public ArrayNode insert(int index, Boolean value)
  {
    if (value == null) {
      return insertNull(index);
    }
    return _insert(index, booleanNode(value.booleanValue()));
  }
  






  public ArrayNode insert(int index, byte[] v)
  {
    if (v == null) {
      return insertNull(index);
    }
    return _insert(index, binaryNode(v));
  }
  







  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if ((o instanceof ArrayNode)) {
      return _children.equals(_children);
    }
    return false;
  }
  


  protected boolean _childrenEqual(ArrayNode other)
  {
    return _children.equals(_children);
  }
  
  public int hashCode()
  {
    return _children.hashCode();
  }
  





  protected ArrayNode _add(JsonNode node)
  {
    _children.add(node);
    return this;
  }
  
  protected ArrayNode _insert(int index, JsonNode node)
  {
    if (index < 0) {
      _children.add(0, node);
    } else if (index >= _children.size()) {
      _children.add(node);
    } else {
      _children.add(index, node);
    }
    return this;
  }
}
