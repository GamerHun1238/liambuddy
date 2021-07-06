package org.slf4j.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.SubstituteLoggingEvent;





























public class SubstituteLoggerFactory
  implements ILoggerFactory
{
  public SubstituteLoggerFactory() {}
  
  boolean postInitialization = false;
  
  final Map<String, SubstituteLogger> loggers = new HashMap();
  
  final LinkedBlockingQueue<SubstituteLoggingEvent> eventQueue = new LinkedBlockingQueue();
  
  public synchronized Logger getLogger(String name) {
    SubstituteLogger logger = (SubstituteLogger)loggers.get(name);
    if (logger == null) {
      logger = new SubstituteLogger(name, eventQueue, postInitialization);
      loggers.put(name, logger);
    }
    return logger;
  }
  
  public List<String> getLoggerNames() {
    return new ArrayList(loggers.keySet());
  }
  
  public List<SubstituteLogger> getLoggers() {
    return new ArrayList(loggers.values());
  }
  
  public LinkedBlockingQueue<SubstituteLoggingEvent> getEventQueue() {
    return eventQueue;
  }
  
  public void postInitialization() {
    postInitialization = true;
  }
  
  public void clear() {
    loggers.clear();
    eventQueue.clear();
  }
}
