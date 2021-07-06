package com.neovisionaries.ws.client;






class CounterPayloadGenerator
  implements PayloadGenerator
{
  private long mCount;
  





  CounterPayloadGenerator() {}
  





  public byte[] generate()
  {
    return Misc.getBytesUTF8(String.valueOf(increment()));
  }
  


  private long increment()
  {
    mCount = Math.max(mCount + 1L, 1L);
    
    return mCount;
  }
}
