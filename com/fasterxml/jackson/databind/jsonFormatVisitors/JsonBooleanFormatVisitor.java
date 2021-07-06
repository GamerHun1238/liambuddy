package com.fasterxml.jackson.databind.jsonFormatVisitors;

public abstract interface JsonBooleanFormatVisitor
  extends JsonValueFormatVisitor
{
  public static class Base
    extends JsonValueFormatVisitor.Base
    implements JsonBooleanFormatVisitor
  {
    public Base() {}
  }
}
