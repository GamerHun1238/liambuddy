package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;








public abstract class DOMDeserializer<T>
  extends FromStringDeserializer<T>
{
  private static final long serialVersionUID = 1L;
  private static final DocumentBuilderFactory DEFAULT_PARSER_FACTORY;
  
  static
  {
    DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
    
    parserFactory.setNamespaceAware(true);
    
    parserFactory.setExpandEntityReferences(false);
    try
    {
      parserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
    }
    catch (ParserConfigurationException localParserConfigurationException) {}catch (Error localError) {}
    



    DEFAULT_PARSER_FACTORY = parserFactory;
  }
  
  protected DOMDeserializer(Class<T> cls) { super(cls); }
  
  protected final Document parse(String value)
    throws IllegalArgumentException
  {
    try
    {
      return documentBuilder().parse(new InputSource(new StringReader(value)));
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to parse JSON String as XML: " + e.getMessage(), e);
    }
  }
  




  protected DocumentBuilder documentBuilder()
    throws ParserConfigurationException
  {
    return DEFAULT_PARSER_FACTORY.newDocumentBuilder();
  }
  
  public abstract T _deserialize(String paramString, DeserializationContext paramDeserializationContext);
  
  public static class NodeDeserializer extends DOMDeserializer<Node>
  {
    private static final long serialVersionUID = 1L;
    
    public NodeDeserializer()
    {
      super();
    }
    
    public Node _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException { return parse(value); }
  }
  
  public static class DocumentDeserializer extends DOMDeserializer<Document> {
    private static final long serialVersionUID = 1L;
    
    public DocumentDeserializer() { super(); }
    
    public Document _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
      return parse(value);
    }
  }
}
