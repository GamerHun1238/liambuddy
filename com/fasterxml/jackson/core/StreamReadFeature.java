package com.fasterxml.jackson.core;
























public enum StreamReadFeature
{
  AUTO_CLOSE_SOURCE(JsonParser.Feature.AUTO_CLOSE_SOURCE), 
  
















  STRICT_DUPLICATE_DETECTION(JsonParser.Feature.STRICT_DUPLICATE_DETECTION), 
  




















  IGNORE_UNDEFINED(JsonParser.Feature.IGNORE_UNDEFINED), 
  



















  INCLUDE_SOURCE_IN_LOCATION(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
  



  private final boolean _defaultState;
  


  private final int _mask;
  


  private final JsonParser.Feature _mappedFeature;
  


  private StreamReadFeature(JsonParser.Feature mapTo)
  {
    _mappedFeature = mapTo;
    _mask = mapTo.getMask();
    _defaultState = mapTo.enabledByDefault();
  }
  




  public static int collectDefaults()
  {
    int flags = 0;
    for (StreamReadFeature f : values()) {
      if (f.enabledByDefault()) {
        flags |= f.getMask();
      }
    }
    return flags;
  }
  
  public boolean enabledByDefault() { return _defaultState; }
  public boolean enabledIn(int flags) { return (flags & _mask) != 0; }
  public int getMask() { return _mask; }
  
  public JsonParser.Feature mappedFeature() { return _mappedFeature; }
}
