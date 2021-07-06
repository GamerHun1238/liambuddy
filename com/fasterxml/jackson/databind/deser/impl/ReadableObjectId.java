package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;










public class ReadableObjectId
{
  protected Object _item;
  protected final ObjectIdGenerator.IdKey _key;
  protected LinkedList<Referring> _referringProperties;
  protected ObjectIdResolver _resolver;
  
  public ReadableObjectId(ObjectIdGenerator.IdKey key)
  {
    _key = key;
  }
  
  public void setResolver(ObjectIdResolver resolver) {
    _resolver = resolver;
  }
  
  public ObjectIdGenerator.IdKey getKey() {
    return _key;
  }
  
  public void appendReferring(Referring currentReferring) {
    if (_referringProperties == null) {
      _referringProperties = new LinkedList();
    }
    _referringProperties.add(currentReferring);
  }
  



  public void bindItem(Object ob)
    throws IOException
  {
    _resolver.bindItem(_key, ob);
    _item = ob;
    Object id = _key.key;
    if (_referringProperties != null) {
      Iterator<Referring> it = _referringProperties.iterator();
      _referringProperties = null;
      while (it.hasNext()) {
        ((Referring)it.next()).handleResolvedForwardReference(id, ob);
      }
    }
  }
  
  public Object resolve() {
    return this._item = _resolver.resolveId(_key);
  }
  
  public boolean hasReferringProperties() {
    return (_referringProperties != null) && (!_referringProperties.isEmpty());
  }
  
  public Iterator<Referring> referringProperties() {
    if (_referringProperties == null) {
      return Collections.emptyList().iterator();
    }
    return _referringProperties.iterator();
  }
  















  public boolean tryToResolveUnresolved(DeserializationContext ctxt)
  {
    return false;
  }
  








  public ObjectIdResolver getResolver()
  {
    return _resolver;
  }
  
  public String toString()
  {
    return String.valueOf(_key);
  }
  


  public static abstract class Referring
  {
    private final UnresolvedForwardReference _reference;
    
    private final Class<?> _beanType;
    

    public Referring(UnresolvedForwardReference ref, Class<?> beanType)
    {
      _reference = ref;
      _beanType = beanType;
    }
    
    public Referring(UnresolvedForwardReference ref, JavaType beanType) {
      _reference = ref;
      _beanType = beanType.getRawClass();
    }
    
    public JsonLocation getLocation() { return _reference.getLocation(); }
    public Class<?> getBeanType() { return _beanType; }
    
    public abstract void handleResolvedForwardReference(Object paramObject1, Object paramObject2) throws IOException;
    
    public boolean hasId(Object id) { return id.equals(_reference.getUnresolvedId()); }
  }
}
