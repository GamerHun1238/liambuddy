package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonGenerator.Feature;


















public enum JsonWriteFeature
  implements FormatFeature
{
  QUOTE_FIELD_NAMES(true, JsonGenerator.Feature.QUOTE_FIELD_NAMES), 
  













  WRITE_NAN_AS_STRINGS(true, JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS), 
  

















  WRITE_NUMBERS_AS_STRINGS(false, JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS), 
  










  ESCAPE_NON_ASCII(false, JsonGenerator.Feature.ESCAPE_NON_ASCII);
  









  private final boolean _defaultState;
  








  private final int _mask;
  







  private final JsonGenerator.Feature _mappedFeature;
  








  public static int collectDefaults()
  {
    int flags = 0;
    for (JsonWriteFeature f : values()) {
      if (f.enabledByDefault()) {
        flags |= f.getMask();
      }
    }
    return flags;
  }
  
  private JsonWriteFeature(boolean defaultState, JsonGenerator.Feature mapTo)
  {
    _defaultState = defaultState;
    _mask = (1 << ordinal());
    _mappedFeature = mapTo;
  }
  

  public boolean enabledByDefault() { return _defaultState; }
  
  public int getMask() { return _mask; }
  
  public boolean enabledIn(int flags) { return (flags & _mask) != 0; }
  
  public JsonGenerator.Feature mappedFeature() { return _mappedFeature; }
}
