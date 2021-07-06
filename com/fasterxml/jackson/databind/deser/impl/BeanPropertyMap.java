package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;












































public class BeanPropertyMap
  implements Iterable<SettableBeanProperty>, Serializable
{
  private static final long serialVersionUID = 2L;
  protected final boolean _caseInsensitive;
  private int _hashMask;
  private int _size;
  private int _spillCount;
  private Object[] _hashArea;
  private final SettableBeanProperty[] _propsInOrder;
  private final Map<String, List<PropertyName>> _aliasDefs;
  private final Map<String, String> _aliasMapping;
  
  public BeanPropertyMap(boolean caseInsensitive, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasDefs)
  {
    _caseInsensitive = caseInsensitive;
    _propsInOrder = ((SettableBeanProperty[])props.toArray(new SettableBeanProperty[props.size()]));
    _aliasDefs = aliasDefs;
    _aliasMapping = _buildAliasMapping(aliasDefs);
    init(props);
  }
  






  private BeanPropertyMap(BeanPropertyMap src, SettableBeanProperty newProp, int hashIndex, int orderedIndex)
  {
    _caseInsensitive = _caseInsensitive;
    _hashMask = _hashMask;
    _size = _size;
    _spillCount = _spillCount;
    _aliasDefs = _aliasDefs;
    _aliasMapping = _aliasMapping;
    

    _hashArea = Arrays.copyOf(_hashArea, _hashArea.length);
    _propsInOrder = ((SettableBeanProperty[])Arrays.copyOf(_propsInOrder, _propsInOrder.length));
    _hashArea[hashIndex] = newProp;
    _propsInOrder[orderedIndex] = newProp;
  }
  






  private BeanPropertyMap(BeanPropertyMap src, SettableBeanProperty newProp, String key, int slot)
  {
    _caseInsensitive = _caseInsensitive;
    _hashMask = _hashMask;
    _size = _size;
    _spillCount = _spillCount;
    _aliasDefs = _aliasDefs;
    _aliasMapping = _aliasMapping;
    

    _hashArea = Arrays.copyOf(_hashArea, _hashArea.length);
    int last = _propsInOrder.length;
    
    _propsInOrder = ((SettableBeanProperty[])Arrays.copyOf(_propsInOrder, last + 1));
    _propsInOrder[last] = newProp;
    
    int hashSize = _hashMask + 1;
    int ix = slot << 1;
    

    if (_hashArea[ix] != null)
    {
      ix = hashSize + (slot >> 1) << 1;
      if (_hashArea[ix] != null)
      {
        ix = (hashSize + (hashSize >> 1) << 1) + _spillCount;
        _spillCount += 2;
        if (ix >= _hashArea.length) {
          _hashArea = Arrays.copyOf(_hashArea, _hashArea.length + 4);
        }
      }
    }
    _hashArea[ix] = key;
    _hashArea[(ix + 1)] = newProp;
  }
  
  @Deprecated
  public BeanPropertyMap(boolean caseInsensitive, Collection<SettableBeanProperty> props)
  {
    this(caseInsensitive, props, Collections.emptyMap());
  }
  



  protected BeanPropertyMap(BeanPropertyMap base, boolean caseInsensitive)
  {
    _caseInsensitive = caseInsensitive;
    _aliasDefs = _aliasDefs;
    _aliasMapping = _aliasMapping;
    

    _propsInOrder = ((SettableBeanProperty[])Arrays.copyOf(_propsInOrder, _propsInOrder.length));
    init(Arrays.asList(_propsInOrder));
  }
  






  public BeanPropertyMap withCaseInsensitivity(boolean state)
  {
    if (_caseInsensitive == state) {
      return this;
    }
    return new BeanPropertyMap(this, state);
  }
  
  protected void init(Collection<SettableBeanProperty> props)
  {
    _size = props.size();
    

    int hashSize = findSize(_size);
    _hashMask = (hashSize - 1);
    

    int alloc = (hashSize + (hashSize >> 1)) * 2;
    Object[] hashed = new Object[alloc];
    int spillCount = 0;
    
    for (SettableBeanProperty prop : props)
    {
      if (prop != null)
      {


        String key = getPropertyName(prop);
        int slot = _hashCode(key);
        int ix = slot << 1;
        

        if (hashed[ix] != null)
        {
          ix = hashSize + (slot >> 1) << 1;
          if (hashed[ix] != null)
          {
            ix = (hashSize + (hashSize >> 1) << 1) + spillCount;
            spillCount += 2;
            if (ix >= hashed.length) {
              hashed = Arrays.copyOf(hashed, hashed.length + 4);
            }
          }
        }
        hashed[ix] = key;
        hashed[(ix + 1)] = prop;
      }
    }
    
    _hashArea = hashed;
    _spillCount = spillCount;
  }
  
  private static final int findSize(int size)
  {
    if (size <= 5) {
      return 8;
    }
    if (size <= 12) {
      return 16;
    }
    int needed = size + (size >> 2);
    int result = 32;
    while (result < needed) {
      result += result;
    }
    return result;
  }
  



  public static BeanPropertyMap construct(Collection<SettableBeanProperty> props, boolean caseInsensitive, Map<String, List<PropertyName>> aliasMapping)
  {
    return new BeanPropertyMap(caseInsensitive, props, aliasMapping);
  }
  
  @Deprecated
  public static BeanPropertyMap construct(Collection<SettableBeanProperty> props, boolean caseInsensitive) {
    return construct(props, caseInsensitive, 
      Collections.emptyMap());
  }
  








  public BeanPropertyMap withProperty(SettableBeanProperty newProp)
  {
    String key = getPropertyName(newProp);
    
    int i = 1; for (int end = _hashArea.length; i < end; i += 2) {
      SettableBeanProperty prop = (SettableBeanProperty)_hashArea[i];
      if ((prop != null) && (prop.getName().equals(key))) {
        return new BeanPropertyMap(this, newProp, i, _findFromOrdered(prop));
      }
    }
    
    int slot = _hashCode(key);
    
    return new BeanPropertyMap(this, newProp, key, slot);
  }
  

  public BeanPropertyMap assignIndexes()
  {
    int index = 0;
    int i = 1; for (int end = _hashArea.length; i < end; i += 2) {
      SettableBeanProperty prop = (SettableBeanProperty)_hashArea[i];
      if (prop != null) {
        prop.assignIndex(index++);
      }
    }
    return this;
  }
  




  public BeanPropertyMap renameAll(NameTransformer transformer)
  {
    if ((transformer == null) || (transformer == NameTransformer.NOP)) {
      return this;
    }
    
    int len = _propsInOrder.length;
    ArrayList<SettableBeanProperty> newProps = new ArrayList(len);
    
    for (int i = 0; i < len; i++) {
      SettableBeanProperty prop = _propsInOrder[i];
      

      if (prop == null) {
        newProps.add(prop);
      }
      else {
        newProps.add(_rename(prop, transformer));
      }
    }
    
    return new BeanPropertyMap(_caseInsensitive, newProps, _aliasDefs);
  }
  













  public BeanPropertyMap withoutProperties(Collection<String> toExclude)
  {
    if (toExclude.isEmpty()) {
      return this;
    }
    int len = _propsInOrder.length;
    ArrayList<SettableBeanProperty> newProps = new ArrayList(len);
    
    for (int i = 0; i < len; i++) {
      SettableBeanProperty prop = _propsInOrder[i];
      


      if ((prop != null) && 
        (!toExclude.contains(prop.getName()))) {
        newProps.add(prop);
      }
    }
    

    return new BeanPropertyMap(_caseInsensitive, newProps, _aliasDefs);
  }
  
  @Deprecated
  public void replace(SettableBeanProperty newProp)
  {
    String key = getPropertyName(newProp);
    int ix = _findIndexInHash(key);
    if (ix < 0) {
      throw new NoSuchElementException("No entry '" + key + "' found, can't replace");
    }
    SettableBeanProperty prop = (SettableBeanProperty)_hashArea[ix];
    _hashArea[ix] = newProp;
    
    _propsInOrder[_findFromOrdered(prop)] = newProp;
  }
  







  public void replace(SettableBeanProperty origProp, SettableBeanProperty newProp)
  {
    int i = 1;
    int end = _hashArea.length;
    for (;; 
        i += 2) {
      if (i > end) {
        throw new NoSuchElementException("No entry '" + origProp.getName() + "' found, can't replace");
      }
      if (_hashArea[i] == origProp) {
        _hashArea[i] = newProp;
        break;
      }
    }
    _propsInOrder[_findFromOrdered(origProp)] = newProp;
  }
  




  public void remove(SettableBeanProperty propToRm)
  {
    ArrayList<SettableBeanProperty> props = new ArrayList(_size);
    String key = getPropertyName(propToRm);
    boolean found = false;
    
    int i = 1; for (int end = _hashArea.length; i < end; i += 2) {
      SettableBeanProperty prop = (SettableBeanProperty)_hashArea[i];
      if (prop != null)
      {

        if (!found)
        {

          found = key.equals(_hashArea[(i - 1)]);
          if (found)
          {
            _propsInOrder[_findFromOrdered(prop)] = null;
            continue;
          }
        }
        props.add(prop);
      } }
    if (!found) {
      throw new NoSuchElementException("No entry '" + propToRm.getName() + "' found, can't remove");
    }
    init(props);
  }
  




  public int size()
  {
    return _size;
  }
  

  public boolean isCaseInsensitive()
  {
    return _caseInsensitive;
  }
  


  public boolean hasAliases()
  {
    return !_aliasDefs.isEmpty();
  }
  



  public Iterator<SettableBeanProperty> iterator()
  {
    return _properties().iterator();
  }
  
  private List<SettableBeanProperty> _properties() {
    ArrayList<SettableBeanProperty> p = new ArrayList(_size);
    int i = 1; for (int end = _hashArea.length; i < end; i += 2) {
      SettableBeanProperty prop = (SettableBeanProperty)_hashArea[i];
      if (prop != null) {
        p.add(prop);
      }
    }
    return p;
  }
  







  public SettableBeanProperty[] getPropertiesInInsertionOrder()
  {
    return _propsInOrder;
  }
  

  protected final String getPropertyName(SettableBeanProperty prop)
  {
    return _caseInsensitive ? prop.getName().toLowerCase() : prop.getName();
  }
  











  public SettableBeanProperty find(int index)
  {
    int i = 1; for (int end = _hashArea.length; i < end; i += 2) {
      SettableBeanProperty prop = (SettableBeanProperty)_hashArea[i];
      if ((prop != null) && (index == prop.getPropertyIndex())) {
        return prop;
      }
    }
    return null;
  }
  
  public SettableBeanProperty find(String key)
  {
    if (key == null) {
      throw new IllegalArgumentException("Cannot pass null property name");
    }
    if (_caseInsensitive) {
      key = key.toLowerCase();
    }
    

    int slot = key.hashCode() & _hashMask;
    


    int ix = slot << 1;
    Object match = _hashArea[ix];
    if ((match == key) || (key.equals(match))) {
      return (SettableBeanProperty)_hashArea[(ix + 1)];
    }
    return _find2(key, slot, match);
  }
  
  private final SettableBeanProperty _find2(String key, int slot, Object match)
  {
    if (match == null)
    {
      return _findWithAlias((String)_aliasMapping.get(key));
    }
    
    int hashSize = _hashMask + 1;
    int ix = hashSize + (slot >> 1) << 1;
    match = _hashArea[ix];
    if (key.equals(match)) {
      return (SettableBeanProperty)_hashArea[(ix + 1)];
    }
    if (match != null) {
      int i = hashSize + (hashSize >> 1) << 1;
      for (int end = i + _spillCount; i < end; i += 2) {
        match = _hashArea[i];
        if ((match == key) || (key.equals(match))) {
          return (SettableBeanProperty)_hashArea[(i + 1)];
        }
      }
    }
    
    return _findWithAlias((String)_aliasMapping.get(key));
  }
  
  private SettableBeanProperty _findWithAlias(String keyFromAlias)
  {
    if (keyFromAlias == null) {
      return null;
    }
    

    int slot = _hashCode(keyFromAlias);
    int ix = slot << 1;
    Object match = _hashArea[ix];
    if (keyFromAlias.equals(match)) {
      return (SettableBeanProperty)_hashArea[(ix + 1)];
    }
    if (match == null) {
      return null;
    }
    return _find2ViaAlias(keyFromAlias, slot, match);
  }
  

  private SettableBeanProperty _find2ViaAlias(String key, int slot, Object match)
  {
    int hashSize = _hashMask + 1;
    int ix = hashSize + (slot >> 1) << 1;
    match = _hashArea[ix];
    if (key.equals(match)) {
      return (SettableBeanProperty)_hashArea[(ix + 1)];
    }
    if (match != null) {
      int i = hashSize + (hashSize >> 1) << 1;
      for (int end = i + _spillCount; i < end; i += 2) {
        match = _hashArea[i];
        if ((match == key) || (key.equals(match))) {
          return (SettableBeanProperty)_hashArea[(i + 1)];
        }
      }
    }
    return null;
  }
  















  public boolean findDeserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, String key)
    throws IOException
  {
    SettableBeanProperty prop = find(key);
    if (prop == null) {
      return false;
    }
    try {
      prop.deserializeAndSet(p, ctxt, bean);
    } catch (Exception e) {
      wrapAndThrow(e, bean, key, ctxt);
    }
    return true;
  }
  







  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Properties=[");
    int count = 0;
    
    Iterator<SettableBeanProperty> it = iterator();
    while (it.hasNext()) {
      SettableBeanProperty prop = (SettableBeanProperty)it.next();
      if (count++ > 0) {
        sb.append(", ");
      }
      sb.append(prop.getName());
      sb.append('(');
      sb.append(prop.getType());
      sb.append(')');
    }
    sb.append(']');
    if (!_aliasDefs.isEmpty()) {
      sb.append("(aliases: ");
      sb.append(_aliasDefs);
      sb.append(")");
    }
    return sb.toString();
  }
  






  protected SettableBeanProperty _rename(SettableBeanProperty prop, NameTransformer xf)
  {
    if (prop == null) {
      return prop;
    }
    String newName = xf.transform(prop.getName());
    prop = prop.withSimpleName(newName);
    JsonDeserializer<?> deser = prop.getValueDeserializer();
    if (deser != null)
    {

      JsonDeserializer<Object> newDeser = deser.unwrappingDeserializer(xf);
      if (newDeser != deser) {
        prop = prop.withValueDeserializer(newDeser);
      }
    }
    return prop;
  }
  

  protected void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt)
    throws IOException
  {
    while (((t instanceof InvocationTargetException)) && (t.getCause() != null)) {
      t = t.getCause();
    }
    
    ClassUtil.throwIfError(t);
    
    boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
    
    if ((t instanceof IOException)) {
      if ((!wrap) || (!(t instanceof JsonProcessingException))) {
        throw ((IOException)t);
      }
    } else if (!wrap) {
      ClassUtil.throwIfRTE(t);
    }
    throw JsonMappingException.wrapWithPath(t, bean, fieldName);
  }
  








  private final int _findIndexInHash(String key)
  {
    int slot = _hashCode(key);
    int ix = slot << 1;
    

    if (key.equals(_hashArea[ix])) {
      return ix + 1;
    }
    
    int hashSize = _hashMask + 1;
    ix = hashSize + (slot >> 1) << 1;
    if (key.equals(_hashArea[ix])) {
      return ix + 1;
    }
    
    int i = hashSize + (hashSize >> 1) << 1;
    for (int end = i + _spillCount; i < end; i += 2) {
      if (key.equals(_hashArea[i])) {
        return i + 1;
      }
    }
    return -1;
  }
  
  private final int _findFromOrdered(SettableBeanProperty prop) {
    int i = 0; for (int end = _propsInOrder.length; i < end; i++) {
      if (_propsInOrder[i] == prop) {
        return i;
      }
    }
    throw new IllegalStateException("Illegal state: property '" + prop.getName() + "' missing from _propsInOrder");
  }
  









  private final int _hashCode(String key)
  {
    return key.hashCode() & _hashMask;
  }
  

  private Map<String, String> _buildAliasMapping(Map<String, List<PropertyName>> defs)
  {
    if ((defs == null) || (defs.isEmpty())) {
      return Collections.emptyMap();
    }
    Map<String, String> aliases = new HashMap();
    for (Map.Entry<String, List<PropertyName>> entry : defs.entrySet()) {
      key = (String)entry.getKey();
      if (_caseInsensitive) {
        key = key.toLowerCase();
      }
      for (PropertyName pn : (List)entry.getValue()) {
        String mapped = pn.getSimpleName();
        if (_caseInsensitive) {
          mapped = mapped.toLowerCase();
        }
        aliases.put(mapped, key);
      } }
    String key;
    return aliases;
  }
}
