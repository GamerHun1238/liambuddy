package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.Serializable;









public class PropertyMetadata
  implements Serializable
{
  private static final long serialVersionUID = -1L;
  public static final PropertyMetadata STD_REQUIRED = new PropertyMetadata(Boolean.TRUE, null, null, null, null, null, null);
  

  public static final PropertyMetadata STD_OPTIONAL = new PropertyMetadata(Boolean.FALSE, null, null, null, null, null, null);
  

  public static final PropertyMetadata STD_REQUIRED_OR_OPTIONAL = new PropertyMetadata(null, null, null, null, null, null, null);
  
  protected final Boolean _required;
  
  protected final String _description;
  
  protected final Integer _index;
  
  protected final String _defaultValue;
  
  protected final transient MergeInfo _mergeInfo;
  
  protected Nulls _valueNulls;
  protected Nulls _contentNulls;
  
  public static final class MergeInfo
  {
    public final AnnotatedMember getter;
    public final boolean fromDefaults;
    
    protected MergeInfo(AnnotatedMember getter, boolean fromDefaults)
    {
      this.getter = getter;
      this.fromDefaults = fromDefaults;
    }
    
    public static MergeInfo createForDefaults(AnnotatedMember getter) {
      return new MergeInfo(getter, true);
    }
    
    public static MergeInfo createForTypeOverride(AnnotatedMember getter) {
      return new MergeInfo(getter, false);
    }
    
    public static MergeInfo createForPropertyOverride(AnnotatedMember getter) {
      return new MergeInfo(getter, false);
    }
  }
  

























































  protected PropertyMetadata(Boolean req, String desc, Integer index, String def, MergeInfo mergeInfo, Nulls valueNulls, Nulls contentNulls)
  {
    _required = req;
    _description = desc;
    _index = index;
    _defaultValue = ((def == null) || (def.isEmpty()) ? null : def);
    _mergeInfo = mergeInfo;
    _valueNulls = valueNulls;
    _contentNulls = contentNulls;
  }
  



  public static PropertyMetadata construct(Boolean req, String desc, Integer index, String defaultValue)
  {
    if ((desc != null) || (index != null) || (defaultValue != null)) {
      return new PropertyMetadata(req, desc, index, defaultValue, null, null, null);
    }
    
    if (req == null) {
      return STD_REQUIRED_OR_OPTIONAL;
    }
    return req.booleanValue() ? STD_REQUIRED : STD_OPTIONAL;
  }
  
  @Deprecated
  public static PropertyMetadata construct(boolean req, String desc, Integer index, String defaultValue)
  {
    if ((desc != null) || (index != null) || (defaultValue != null)) {
      return new PropertyMetadata(Boolean.valueOf(req), desc, index, defaultValue, null, null, null);
    }
    
    return req ? STD_REQUIRED : STD_OPTIONAL;
  }
  




  protected Object readResolve()
  {
    if ((_description == null) && (_index == null) && (_defaultValue == null) && (_mergeInfo == null) && (_valueNulls == null) && (_contentNulls == null))
    {

      if (_required == null) {
        return STD_REQUIRED_OR_OPTIONAL;
      }
      return _required.booleanValue() ? STD_REQUIRED : STD_OPTIONAL;
    }
    return this;
  }
  
  public PropertyMetadata withDescription(String desc) {
    return new PropertyMetadata(_required, desc, _index, _defaultValue, _mergeInfo, _valueNulls, _contentNulls);
  }
  



  public PropertyMetadata withMergeInfo(MergeInfo mergeInfo)
  {
    return new PropertyMetadata(_required, _description, _index, _defaultValue, mergeInfo, _valueNulls, _contentNulls);
  }
  




  public PropertyMetadata withNulls(Nulls valueNulls, Nulls contentNulls)
  {
    return new PropertyMetadata(_required, _description, _index, _defaultValue, _mergeInfo, valueNulls, contentNulls);
  }
  
  public PropertyMetadata withDefaultValue(String def)
  {
    if ((def == null) || (def.isEmpty())) {
      if (_defaultValue == null) {
        return this;
      }
      def = null;
    } else if (def.equals(_defaultValue)) {
      return this;
    }
    return new PropertyMetadata(_required, _description, _index, def, _mergeInfo, _valueNulls, _contentNulls);
  }
  
  public PropertyMetadata withIndex(Integer index)
  {
    return new PropertyMetadata(_required, _description, index, _defaultValue, _mergeInfo, _valueNulls, _contentNulls);
  }
  
  public PropertyMetadata withRequired(Boolean b)
  {
    if (b == null) {
      if (_required == null) {
        return this;
      }
    } else if (b.equals(_required)) {
      return this;
    }
    return new PropertyMetadata(b, _description, _index, _defaultValue, _mergeInfo, _valueNulls, _contentNulls);
  }
  





  public String getDescription()
  {
    return _description;
  }
  
  public String getDefaultValue()
  {
    return _defaultValue;
  }
  





  public boolean hasDefaultValue() { return _defaultValue != null; }
  
  public boolean isRequired() { return (_required != null) && (_required.booleanValue()); }
  
  public Boolean getRequired() { return _required; }
  

  public Integer getIndex()
  {
    return _index;
  }
  
  public boolean hasIndex()
  {
    return _index != null;
  }
  
  public MergeInfo getMergeInfo()
  {
    return _mergeInfo;
  }
  
  public Nulls getValueNulls()
  {
    return _valueNulls;
  }
  
  public Nulls getContentNulls()
  {
    return _contentNulls;
  }
}
