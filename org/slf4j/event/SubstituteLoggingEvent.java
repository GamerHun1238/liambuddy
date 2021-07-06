package org.slf4j.event;

import org.slf4j.Marker;

public class SubstituteLoggingEvent implements LoggingEvent {
  Level level;
  Marker marker;
  String loggerName;
  org.slf4j.helpers.SubstituteLogger logger;
  String threadName;
  String message;
  Object[] argArray;
  long timeStamp;
  Throwable throwable;
  
  public SubstituteLoggingEvent() {}
  
  public Level getLevel() {
    return level;
  }
  
  public void setLevel(Level level) {
    this.level = level;
  }
  
  public Marker getMarker() {
    return marker;
  }
  
  public void setMarker(Marker marker) {
    this.marker = marker;
  }
  
  public String getLoggerName() {
    return loggerName;
  }
  
  public void setLoggerName(String loggerName) {
    this.loggerName = loggerName;
  }
  
  public org.slf4j.helpers.SubstituteLogger getLogger() {
    return logger;
  }
  
  public void setLogger(org.slf4j.helpers.SubstituteLogger logger) {
    this.logger = logger;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public Object[] getArgumentArray() {
    return argArray;
  }
  
  public void setArgumentArray(Object[] argArray) {
    this.argArray = argArray;
  }
  
  public long getTimeStamp() {
    return timeStamp;
  }
  
  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }
  
  public String getThreadName() {
    return threadName;
  }
  
  public void setThreadName(String threadName) {
    this.threadName = threadName;
  }
  
  public Throwable getThrowable() {
    return throwable;
  }
  
  public void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }
}
