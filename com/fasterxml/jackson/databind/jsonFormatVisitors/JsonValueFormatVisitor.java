package com.fasterxml.jackson.databind.jsonFormatVisitors;

import java.util.Set;

public abstract interface JsonValueFormatVisitor
{
  public abstract void format(JsonValueFormat paramJsonValueFormat);
  
  public abstract void enumTypes(Set<String> paramSet);
  
  public static class Base
    implements JsonValueFormatVisitor
  {
    public Base() {}
    
    public void format(JsonValueFormat format) {}
    
    public void enumTypes(Set<String> enums) {}
  }
}
