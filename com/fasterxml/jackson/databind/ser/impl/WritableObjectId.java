package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;













public final class WritableObjectId
{
  public final ObjectIdGenerator<?> generator;
  public Object id;
  protected boolean idWritten = false;
  
  public WritableObjectId(ObjectIdGenerator<?> generator) {
    this.generator = generator;
  }
  
  public boolean writeAsId(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w) throws IOException
  {
    if ((id != null) && ((idWritten) || (alwaysAsId)))
    {
      if (gen.canWriteObjectId()) {
        gen.writeObjectRef(String.valueOf(id));
      } else {
        serializer.serialize(id, gen, provider);
      }
      return true;
    }
    return false;
  }
  


  public Object generateId(Object forPojo)
  {
    if (id == null) {
      id = generator.generateId(forPojo);
    }
    return id;
  }
  



  public void writeAsField(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w)
    throws IOException
  {
    idWritten = true;
    

    if (gen.canWriteObjectId())
    {



      String idStr = id == null ? null : String.valueOf(id);
      gen.writeObjectId(idStr);
      return;
    }
    
    SerializableString name = propertyName;
    if (name != null)
    {
      gen.writeFieldName(name);
      serializer.serialize(id, gen, provider);
    }
  }
}
