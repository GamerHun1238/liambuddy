package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;





































public class JsonMappingException
  extends JsonProcessingException
{
  private static final long serialVersionUID = 1L;
  static final int MAX_REFS_TO_LIST = 1000;
  protected LinkedList<Reference> _path;
  protected transient Closeable _processor;
  
  public static class Reference
    implements Serializable
  {
    private static final long serialVersionUID = 2L;
    protected transient Object _from;
    protected String _fieldName;
    protected int _index = -1;
    




    protected String _desc;
    




    protected Reference() {}
    



    public Reference(Object from) { _from = from; }
    
    public Reference(Object from, String fieldName) {
      _from = from;
      if (fieldName == null) {
        throw new NullPointerException("Cannot pass null fieldName");
      }
      _fieldName = fieldName;
    }
    
    public Reference(Object from, int index) {
      _from = from;
      _index = index;
    }
    

    void setFieldName(String n) { _fieldName = n; }
    void setIndex(int ix) { _index = ix; }
    void setDescription(String d) { _desc = d; }
    










    @JsonIgnore
    public Object getFrom() { return _from; }
    
    public String getFieldName() { return _fieldName; }
    public int getIndex() { return _index; }
    
    public String getDescription() { if (_desc == null) {
        StringBuilder sb = new StringBuilder();
        
        if (_from == null) {
          sb.append("UNKNOWN");
        } else {
          Class<?> cls = (_from instanceof Class) ? (Class)_from : _from.getClass();
          



          int arrays = 0;
          while (cls.isArray()) {
            cls = cls.getComponentType();
            arrays++;
          }
          sb.append(cls.getName());
          for (;;) { arrays--; if (arrays < 0) break;
            sb.append("[]");
          }
        }
        






        sb.append('[');
        if (_fieldName != null) {
          sb.append('"');
          sb.append(_fieldName);
          sb.append('"');
        } else if (_index >= 0) {
          sb.append(_index);
        } else {
          sb.append('?');
        }
        sb.append(']');
        _desc = sb.toString();
      }
      return _desc;
    }
    
    public String toString()
    {
      return getDescription();
    }
    






    Object writeReplace()
    {
      getDescription();
      return this;
    }
  }
  





























  @Deprecated
  public JsonMappingException(String msg)
  {
    super(msg);
  }
  
  @Deprecated
  public JsonMappingException(String msg, Throwable rootCause)
  {
    super(msg, rootCause);
  }
  
  @Deprecated
  public JsonMappingException(String msg, JsonLocation loc)
  {
    super(msg, loc);
  }
  
  @Deprecated
  public JsonMappingException(String msg, JsonLocation loc, Throwable rootCause)
  {
    super(msg, loc, rootCause);
  }
  

  public JsonMappingException(Closeable processor, String msg)
  {
    super(msg);
    _processor = processor;
    if ((processor instanceof JsonParser))
    {


      _location = ((JsonParser)processor).getTokenLocation();
    }
  }
  


  public JsonMappingException(Closeable processor, String msg, Throwable problem)
  {
    super(msg, problem);
    _processor = processor;
    if ((processor instanceof JsonParser)) {
      _location = ((JsonParser)processor).getTokenLocation();
    }
  }
  


  public JsonMappingException(Closeable processor, String msg, JsonLocation loc)
  {
    super(msg, loc);
    _processor = processor;
  }
  


  public static JsonMappingException from(JsonParser p, String msg)
  {
    return new JsonMappingException(p, msg);
  }
  


  public static JsonMappingException from(JsonParser p, String msg, Throwable problem)
  {
    return new JsonMappingException(p, msg, problem);
  }
  


  public static JsonMappingException from(JsonGenerator g, String msg)
  {
    return new JsonMappingException(g, msg, (Throwable)null);
  }
  


  public static JsonMappingException from(JsonGenerator g, String msg, Throwable problem)
  {
    return new JsonMappingException(g, msg, problem);
  }
  


  public static JsonMappingException from(DeserializationContext ctxt, String msg)
  {
    return new JsonMappingException(ctxt.getParser(), msg);
  }
  


  public static JsonMappingException from(DeserializationContext ctxt, String msg, Throwable t)
  {
    return new JsonMappingException(ctxt.getParser(), msg, t);
  }
  


  public static JsonMappingException from(SerializerProvider ctxt, String msg)
  {
    return new JsonMappingException(ctxt.getGenerator(), msg);
  }
  





  public static JsonMappingException from(SerializerProvider ctxt, String msg, Throwable problem)
  {
    return new JsonMappingException(ctxt.getGenerator(), msg, problem);
  }
  









  public static JsonMappingException fromUnexpectedIOE(IOException src)
  {
    return new JsonMappingException(null, 
      String.format("Unexpected IOException (of type %s): %s", new Object[] {src
      .getClass().getName(), 
      ClassUtil.exceptionMessage(src) }));
  }
  








  public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, String refFieldName)
  {
    return wrapWithPath(src, new Reference(refFrom, refFieldName));
  }
  







  public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, int index)
  {
    return wrapWithPath(src, new Reference(refFrom, index));
  }
  


  public static JsonMappingException wrapWithPath(Throwable src, Reference ref)
  {
    JsonMappingException jme;
    

    JsonMappingException jme;
    
    if ((src instanceof JsonMappingException)) {
      jme = (JsonMappingException)src;
    }
    else {
      String msg = ClassUtil.exceptionMessage(src);
      
      if ((msg == null) || (msg.length() == 0)) {
        msg = "(was " + src.getClass().getName() + ")";
      }
      
      Closeable proc = null;
      if ((src instanceof JsonProcessingException)) {
        Object proc0 = ((JsonProcessingException)src).getProcessor();
        if ((proc0 instanceof Closeable)) {
          proc = (Closeable)proc0;
        }
      }
      jme = new JsonMappingException(proc, msg, src);
    }
    jme.prependPath(ref);
    return jme;
  }
  










  public List<Reference> getPath()
  {
    if (_path == null) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(_path);
  }
  




  public String getPathReference()
  {
    return getPathReference(new StringBuilder()).toString();
  }
  
  public StringBuilder getPathReference(StringBuilder sb)
  {
    _appendPathDesc(sb);
    return sb;
  }
  




  public void prependPath(Object referrer, String fieldName)
  {
    Reference ref = new Reference(referrer, fieldName);
    prependPath(ref);
  }
  



  public void prependPath(Object referrer, int index)
  {
    Reference ref = new Reference(referrer, index);
    prependPath(ref);
  }
  
  public void prependPath(Reference r)
  {
    if (_path == null) {
      _path = new LinkedList();
    }
    



    if (_path.size() < 1000) {
      _path.addFirst(r);
    }
  }
  





  @JsonIgnore
  public Object getProcessor()
  {
    return _processor;
  }
  
  public String getLocalizedMessage() {
    return _buildMessage();
  }
  




  public String getMessage()
  {
    return _buildMessage();
  }
  

  protected String _buildMessage()
  {
    String msg = super.getMessage();
    if (_path == null) {
      return msg;
    }
    StringBuilder sb = msg == null ? new StringBuilder() : new StringBuilder(msg);
    




    sb.append(" (through reference chain: ");
    sb = getPathReference(sb);
    sb.append(')');
    return sb.toString();
  }
  

  public String toString()
  {
    return getClass().getName() + ": " + getMessage();
  }
  






  protected void _appendPathDesc(StringBuilder sb)
  {
    if (_path == null) {
      return;
    }
    Iterator<Reference> it = _path.iterator();
    while (it.hasNext()) {
      sb.append(((Reference)it.next()).toString());
      if (it.hasNext()) {
        sb.append("->");
      }
    }
  }
}
