package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;






































public abstract class PolymorphicTypeValidator
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  public PolymorphicTypeValidator() {}
  
  public abstract Validity validateBaseType(MapperConfig<?> paramMapperConfig, JavaType paramJavaType);
  
  public abstract Validity validateSubClassName(MapperConfig<?> paramMapperConfig, JavaType paramJavaType, String paramString)
    throws JsonMappingException;
  
  public abstract Validity validateSubType(MapperConfig<?> paramMapperConfig, JavaType paramJavaType1, JavaType paramJavaType2)
    throws JsonMappingException;
  
  public static enum Validity
  {
    ALLOWED, 
    



    DENIED, 
    







    INDETERMINATE;
    

















    private Validity() {}
  }
  

















  public static abstract class Base
    extends PolymorphicTypeValidator
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    
















    public Base() {}
    
















    public PolymorphicTypeValidator.Validity validateBaseType(MapperConfig<?> config, JavaType baseType)
    {
      return PolymorphicTypeValidator.Validity.INDETERMINATE;
    }
    
    public PolymorphicTypeValidator.Validity validateSubClassName(MapperConfig<?> config, JavaType baseType, String subClassName)
      throws JsonMappingException
    {
      return PolymorphicTypeValidator.Validity.INDETERMINATE;
    }
    
    public PolymorphicTypeValidator.Validity validateSubType(MapperConfig<?> config, JavaType baseType, JavaType subType)
      throws JsonMappingException
    {
      return PolymorphicTypeValidator.Validity.INDETERMINATE;
    }
  }
}
