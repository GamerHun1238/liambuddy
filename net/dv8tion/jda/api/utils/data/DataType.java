package net.dv8tion.jda.api.utils.data;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;




















public enum DataType
{
  INT,  FLOAT,  STRING,  OBJECT,  ARRAY,  BOOLEAN,  NULL,  UNKNOWN;
  



  private DataType() {}
  



  @Nonnull
  public static DataType getType(@Nullable Object value)
  {
    for (DataType type : )
    {
      if (type.isType(value))
        return type;
    }
    return UNKNOWN;
  }
  









  public boolean isType(@Nullable Object value)
  {
    switch (1.$SwitchMap$net$dv8tion$jda$api$utils$data$DataType[ordinal()])
    {
    case 1: 
      return ((value instanceof Integer)) || ((value instanceof Long)) || ((value instanceof Short)) || ((value instanceof Byte));
    case 2: 
      return ((value instanceof Double)) || ((value instanceof Float));
    case 3: 
      return value instanceof String;
    case 4: 
      return value instanceof Boolean;
    case 5: 
      return value instanceof List;
    case 6: 
      return value instanceof Map;
    case 7: 
      return value == null;
    }
    return false;
  }
}
