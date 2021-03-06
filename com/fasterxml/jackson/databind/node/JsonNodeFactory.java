package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;












public class JsonNodeFactory
  implements Serializable, JsonNodeCreator
{
  private static final long serialVersionUID = 1L;
  private final boolean _cfgBigDecimalExact;
  private static final JsonNodeFactory decimalsNormalized = new JsonNodeFactory(false);
  
  private static final JsonNodeFactory decimalsAsIs = new JsonNodeFactory(true);
  






  public static final JsonNodeFactory instance = decimalsNormalized;
  

























  public JsonNodeFactory(boolean bigDecimalExact)
  {
    _cfgBigDecimalExact = bigDecimalExact;
  }
  






  protected JsonNodeFactory()
  {
    this(false);
  }
  







  public static JsonNodeFactory withExactBigDecimals(boolean bigDecimalExact)
  {
    return bigDecimalExact ? decimalsAsIs : decimalsNormalized;
  }
  










  public BooleanNode booleanNode(boolean v)
  {
    return v ? BooleanNode.getTrue() : BooleanNode.getFalse();
  }
  



  public NullNode nullNode()
  {
    return NullNode.getInstance();
  }
  
  public JsonNode missingNode() { return MissingNode.getInstance(); }
  










  public NumericNode numberNode(byte v)
  {
    return IntNode.valueOf(v);
  }
  





  public ValueNode numberNode(Byte value)
  {
    return value == null ? nullNode() : IntNode.valueOf(value.intValue());
  }
  



  public NumericNode numberNode(short v)
  {
    return ShortNode.valueOf(v);
  }
  





  public ValueNode numberNode(Short value)
  {
    return value == null ? nullNode() : ShortNode.valueOf(value.shortValue());
  }
  



  public NumericNode numberNode(int v)
  {
    return IntNode.valueOf(v);
  }
  





  public ValueNode numberNode(Integer value)
  {
    return value == null ? nullNode() : IntNode.valueOf(value.intValue());
  }
  




  public NumericNode numberNode(long v)
  {
    return LongNode.valueOf(v);
  }
  





  public ValueNode numberNode(Long v)
  {
    if (v == null) {
      return nullNode();
    }
    return LongNode.valueOf(v.longValue());
  }
  




  public ValueNode numberNode(BigInteger v)
  {
    if (v == null) {
      return nullNode();
    }
    return BigIntegerNode.valueOf(v);
  }
  



  public NumericNode numberNode(float v)
  {
    return FloatNode.valueOf(v);
  }
  





  public ValueNode numberNode(Float value)
  {
    return value == null ? nullNode() : FloatNode.valueOf(value.floatValue());
  }
  



  public NumericNode numberNode(double v)
  {
    return DoubleNode.valueOf(v);
  }
  





  public ValueNode numberNode(Double value)
  {
    return value == null ? nullNode() : DoubleNode.valueOf(value.doubleValue());
  }
  











  public ValueNode numberNode(BigDecimal v)
  {
    if (v == null) {
      return nullNode();
    }
    




    if (_cfgBigDecimalExact) {
      return DecimalNode.valueOf(v);
    }
    








    return v.compareTo(BigDecimal.ZERO) == 0 ? DecimalNode.ZERO : 
      DecimalNode.valueOf(v.stripTrailingZeros());
  }
  









  public TextNode textNode(String text)
  {
    return TextNode.valueOf(text);
  }
  



  public BinaryNode binaryNode(byte[] data)
  {
    return BinaryNode.valueOf(data);
  }
  




  public BinaryNode binaryNode(byte[] data, int offset, int length)
  {
    return BinaryNode.valueOf(data, offset, length);
  }
  








  public ArrayNode arrayNode()
  {
    return new ArrayNode(this);
  }
  



  public ArrayNode arrayNode(int capacity)
  {
    return new ArrayNode(this, capacity);
  }
  

  public ObjectNode objectNode()
  {
    return new ObjectNode(this);
  }
  




  public ValueNode pojoNode(Object pojo)
  {
    return new POJONode(pojo);
  }
  
  public ValueNode rawValueNode(RawValue value) {
    return new POJONode(value);
  }
  






  protected boolean _inIntRange(long l)
  {
    int i = (int)l;
    long l2 = i;
    return l2 == l;
  }
}
