package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class DOMSerializer extends StdSerializer<Node>
{
  protected final DOMImplementationLS _domImpl;
  
  public DOMSerializer()
  {
    super(Node.class);
    try
    {
      registry = DOMImplementationRegistry.newInstance();
    } catch (Exception e) { DOMImplementationRegistry registry;
      throw new IllegalStateException("Could not instantiate DOMImplementationRegistry: " + e.getMessage(), e); }
    DOMImplementationRegistry registry;
    _domImpl = ((DOMImplementationLS)registry.getDOMImplementation("LS"));
  }
  

  public void serialize(Node value, JsonGenerator jgen, SerializerProvider provider)
    throws IOException, JsonGenerationException
  {
    if (_domImpl == null) throw new IllegalStateException("Could not find DOM LS");
    LSSerializer writer = _domImpl.createLSSerializer();
    jgen.writeString(writer.writeToString(value));
  }
  

  public JsonNode getSchema(SerializerProvider provider, Type typeHint)
  {
    return createSchemaNode("string", true);
  }
  
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
  {
    if (visitor != null) visitor.expectAnyFormat(typeHint);
  }
}