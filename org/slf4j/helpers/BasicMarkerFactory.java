package org.slf4j.helpers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;


































public class BasicMarkerFactory
  implements IMarkerFactory
{
  private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap();
  







  public BasicMarkerFactory() {}
  






  public Marker getMarker(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException("Marker name cannot be null");
    }
    
    Marker marker = (Marker)markerMap.get(name);
    if (marker == null) {
      marker = new BasicMarker(name);
      Marker oldMarker = (Marker)markerMap.putIfAbsent(name, marker);
      if (oldMarker != null) {
        marker = oldMarker;
      }
    }
    return marker;
  }
  


  public boolean exists(String name)
  {
    if (name == null) {
      return false;
    }
    return markerMap.containsKey(name);
  }
  
  public boolean detachMarker(String name) {
    if (name == null) {
      return false;
    }
    return markerMap.remove(name) != null;
  }
  
  public Marker getDetachedMarker(String name) {
    return new BasicMarker(name);
  }
}