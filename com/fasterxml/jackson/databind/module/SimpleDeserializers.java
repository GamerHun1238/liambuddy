package com.fasterxml.jackson.databind.module;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.ClassKey;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleDeserializers implements com.fasterxml.jackson.databind.deser.Deserializers, java.io.Serializable
{
  private static final long serialVersionUID = 1L;
  protected HashMap<ClassKey, JsonDeserializer<?>> _classMappings = null;
  





  protected boolean _hasEnumDeserializer = false;
  




  public SimpleDeserializers() {}
  




  public SimpleDeserializers(Map<Class<?>, JsonDeserializer<?>> desers)
  {
    addDeserializers(desers);
  }
  
  public <T> void addDeserializer(Class<T> forClass, JsonDeserializer<? extends T> deser)
  {
    ClassKey key = new ClassKey(forClass);
    if (_classMappings == null) {
      _classMappings = new HashMap();
    }
    _classMappings.put(key, deser);
    
    if (forClass == Enum.class) {
      _hasEnumDeserializer = true;
    }
  }
  




  public void addDeserializers(Map<Class<?>, JsonDeserializer<?>> desers)
  {
    for (Map.Entry<Class<?>, JsonDeserializer<?>> entry : desers.entrySet()) {
      Class<?> cls = (Class)entry.getKey();
      
      JsonDeserializer<Object> deser = (JsonDeserializer)entry.getValue();
      addDeserializer(cls, deser);
    }
  }
  









  public JsonDeserializer<?> findArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    return _find(type);
  }
  


  public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
    throws JsonMappingException
  {
    return _find(type);
  }
  




  public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    return _find(type);
  }
  




  public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    return _find(type);
  }
  


  public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc)
    throws JsonMappingException
  {
    if (_classMappings == null) {
      return null;
    }
    JsonDeserializer<?> deser = (JsonDeserializer)_classMappings.get(new ClassKey(type));
    if (deser == null)
    {



      if ((_hasEnumDeserializer) && (type.isEnum())) {
        deser = (JsonDeserializer)_classMappings.get(new ClassKey(Enum.class));
      }
    }
    return deser;
  }
  


  public JsonDeserializer<?> findTreeNodeDeserializer(Class<? extends JsonNode> nodeType, DeserializationConfig config, BeanDescription beanDesc)
    throws JsonMappingException
  {
    if (_classMappings == null) {
      return null;
    }
    return (JsonDeserializer)_classMappings.get(new ClassKey(nodeType));
  }
  




  public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer)
    throws JsonMappingException
  {
    return _find(refType);
  }
  





  public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    return _find(type);
  }
  





  public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    return _find(type);
  }
  
  private final JsonDeserializer<?> _find(JavaType type) {
    if (_classMappings == null) {
      return null;
    }
    return (JsonDeserializer)_classMappings.get(new ClassKey(type.getRawClass()));
  }
}
