package com.fasterxml.jackson.core;





public class JsonGenerationException
  extends JsonProcessingException
{
  private static final long serialVersionUID = 123L;
  



  protected transient JsonGenerator _processor;
  



  @Deprecated
  public JsonGenerationException(Throwable rootCause)
  {
    super(rootCause);
  }
  
  @Deprecated
  public JsonGenerationException(String msg) {
    super(msg, (JsonLocation)null);
  }
  
  @Deprecated
  public JsonGenerationException(String msg, Throwable rootCause) {
    super(msg, null, rootCause);
  }
  


  public JsonGenerationException(Throwable rootCause, JsonGenerator g)
  {
    super(rootCause);
    _processor = g;
  }
  


  public JsonGenerationException(String msg, JsonGenerator g)
  {
    super(msg, (JsonLocation)null);
    _processor = g;
  }
  


  public JsonGenerationException(String msg, Throwable rootCause, JsonGenerator g)
  {
    super(msg, null, rootCause);
    _processor = g;
  }
  





  public JsonGenerationException withGenerator(JsonGenerator g)
  {
    _processor = g;
    return this;
  }
  
  public JsonGenerator getProcessor() {
    return _processor;
  }
}
