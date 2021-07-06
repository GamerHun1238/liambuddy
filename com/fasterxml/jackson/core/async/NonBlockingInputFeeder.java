package com.fasterxml.jackson.core.async;

public abstract interface NonBlockingInputFeeder
{
  public abstract boolean needMoreInput();
  
  public abstract void endOfInput();
}
