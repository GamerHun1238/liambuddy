package com.fasterxml.jackson.databind.introspect;

public abstract interface WithMember<T>
{
  public abstract T withMember(AnnotatedMember paramAnnotatedMember);
}
