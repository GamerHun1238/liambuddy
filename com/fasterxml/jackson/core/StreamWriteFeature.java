package com.fasterxml.jackson.core;


























public enum StreamWriteFeature
{
  AUTO_CLOSE_TARGET(JsonGenerator.Feature.AUTO_CLOSE_TARGET), 
  











  AUTO_CLOSE_CONTENT(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT), 
  











  FLUSH_PASSED_TO_STREAM(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM), 
  














  WRITE_BIGDECIMAL_AS_PLAIN(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN), 
  















  STRICT_DUPLICATE_DETECTION(JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION), 
  


















  IGNORE_UNKNOWN(JsonGenerator.Feature.IGNORE_UNKNOWN);
  



  private final boolean _defaultState;
  


  private final int _mask;
  

  private final JsonGenerator.Feature _mappedFeature;
  


  private StreamWriteFeature(JsonGenerator.Feature mappedTo)
  {
    _mappedFeature = mappedTo;
    _mask = mappedTo.getMask();
    _defaultState = mappedTo.enabledByDefault();
  }
  




  public static int collectDefaults()
  {
    int flags = 0;
    for (StreamWriteFeature f : values()) {
      if (f.enabledByDefault()) {
        flags |= f.getMask();
      }
    }
    return flags;
  }
  
  public boolean enabledByDefault() { return _defaultState; }
  public boolean enabledIn(int flags) { return (flags & _mask) != 0; }
  public int getMask() { return _mask; }
  
  public JsonGenerator.Feature mappedFeature() { return _mappedFeature; }
}
