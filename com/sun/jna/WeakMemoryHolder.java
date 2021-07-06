package com.sun.jna;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;





























public class WeakMemoryHolder
{
  public WeakMemoryHolder() {}
  
  ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
  IdentityHashMap<Reference<Object>, Memory> backingMap = new IdentityHashMap();
  
  public synchronized void put(Object o, Memory m) {
    clean();
    Reference<Object> reference = new WeakReference(o, referenceQueue);
    backingMap.put(reference, m);
  }
  
  public synchronized void clean() {
    for (Reference ref = referenceQueue.poll(); ref != null; ref = referenceQueue.poll()) {
      backingMap.remove(ref);
    }
  }
}
