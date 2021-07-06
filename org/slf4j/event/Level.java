package org.slf4j.event;












public enum Level
{
  ERROR(40, "ERROR"),  WARN(30, "WARN"),  INFO(20, "INFO"),  DEBUG(10, "DEBUG"),  TRACE(0, "TRACE");
  
  private int levelInt;
  private String levelStr;
  
  private Level(int i, String s) {
    levelInt = i;
    levelStr = s;
  }
  
  public int toInt() {
    return levelInt;
  }
  


  public String toString()
  {
    return levelStr;
  }
}
