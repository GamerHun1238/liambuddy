package okhttp3;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpDate;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;































public final class Headers
{
  private final String[] namesAndValues;
  
  Headers(Builder builder)
  {
    namesAndValues = ((String[])namesAndValues.toArray(new String[namesAndValues.size()]));
  }
  
  private Headers(String[] namesAndValues) {
    this.namesAndValues = namesAndValues;
  }
  
  @Nullable
  public String get(String name) {
    return get(namesAndValues, name);
  }
  


  @Nullable
  public Date getDate(String name)
  {
    String value = get(name);
    return value != null ? HttpDate.parse(value) : null;
  }
  


  @Nullable
  @IgnoreJRERequirement
  public Instant getInstant(String name)
  {
    Date value = getDate(name);
    return value != null ? value.toInstant() : null;
  }
  
  public int size()
  {
    return namesAndValues.length / 2;
  }
  
  public String name(int index)
  {
    return namesAndValues[(index * 2)];
  }
  
  public String value(int index)
  {
    return namesAndValues[(index * 2 + 1)];
  }
  
  public Set<String> names()
  {
    TreeSet<String> result = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    int i = 0; for (int size = size(); i < size; i++) {
      result.add(name(i));
    }
    return Collections.unmodifiableSet(result);
  }
  
  public List<String> values(String name)
  {
    List<String> result = null;
    int i = 0; for (int size = size(); i < size; i++) {
      if (name.equalsIgnoreCase(name(i))) {
        if (result == null) result = new ArrayList(2);
        result.add(value(i));
      }
    }
    return result != null ? 
      Collections.unmodifiableList(result) : 
      Collections.emptyList();
  }
  






  public long byteCount()
  {
    long result = namesAndValues.length * 2;
    
    int i = 0; for (int size = namesAndValues.length; i < size; i++) {
      result += namesAndValues[i].length();
    }
    
    return result;
  }
  
  public Builder newBuilder() {
    Builder result = new Builder();
    Collections.addAll(namesAndValues, namesAndValues);
    return result;
  }
  

























  public boolean equals(@Nullable Object other)
  {
    return ((other instanceof Headers)) && 
      (Arrays.equals(namesAndValues, namesAndValues));
  }
  
  public int hashCode() {
    return Arrays.hashCode(namesAndValues);
  }
  
  public String toString() {
    StringBuilder result = new StringBuilder();
    int i = 0; for (int size = size(); i < size; i++) {
      result.append(name(i)).append(": ").append(value(i)).append("\n");
    }
    return result.toString();
  }
  
  public Map<String, List<String>> toMultimap() {
    Map<String, List<String>> result = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    int i = 0; for (int size = size(); i < size; i++) {
      String name = name(i).toLowerCase(Locale.US);
      List<String> values = (List)result.get(name);
      if (values == null) {
        values = new ArrayList(2);
        result.put(name, values);
      }
      values.add(value(i));
    }
    return result;
  }
  
  @Nullable
  private static String get(String[] namesAndValues, String name) { for (int i = namesAndValues.length - 2; i >= 0; i -= 2) {
      if (name.equalsIgnoreCase(namesAndValues[i])) {
        return namesAndValues[(i + 1)];
      }
    }
    return null;
  }
  



  public static Headers of(String... namesAndValues)
  {
    if (namesAndValues == null) throw new NullPointerException("namesAndValues == null");
    if (namesAndValues.length % 2 != 0) {
      throw new IllegalArgumentException("Expected alternating header names and values");
    }
    

    namesAndValues = (String[])namesAndValues.clone();
    for (int i = 0; i < namesAndValues.length; i++) {
      if (namesAndValues[i] == null) throw new IllegalArgumentException("Headers cannot be null");
      namesAndValues[i] = namesAndValues[i].trim();
    }
    

    for (int i = 0; i < namesAndValues.length; i += 2) {
      String name = namesAndValues[i];
      String value = namesAndValues[(i + 1)];
      checkName(name);
      checkValue(value, name);
    }
    
    return new Headers(namesAndValues);
  }
  


  public static Headers of(Map<String, String> headers)
  {
    if (headers == null) { throw new NullPointerException("headers == null");
    }
    
    String[] namesAndValues = new String[headers.size() * 2];
    int i = 0;
    for (Map.Entry<String, String> header : headers.entrySet()) {
      if ((header.getKey() == null) || (header.getValue() == null)) {
        throw new IllegalArgumentException("Headers cannot be null");
      }
      String name = ((String)header.getKey()).trim();
      String value = ((String)header.getValue()).trim();
      checkName(name);
      checkValue(value, name);
      namesAndValues[i] = name;
      namesAndValues[(i + 1)] = value;
      i += 2;
    }
    
    return new Headers(namesAndValues);
  }
  
  static void checkName(String name) {
    if (name == null) throw new NullPointerException("name == null");
    if (name.isEmpty()) throw new IllegalArgumentException("name is empty");
    int i = 0; for (int length = name.length(); i < length; i++) {
      char c = name.charAt(i);
      if ((c <= ' ') || (c >= '')) {
        throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in header name: %s", new Object[] {
          Integer.valueOf(c), Integer.valueOf(i), name }));
      }
    }
  }
  
  static void checkValue(String value, String name) {
    if (value == null) throw new NullPointerException("value for name " + name + " == null");
    int i = 0; for (int length = value.length(); i < length; i++) {
      char c = value.charAt(i);
      if (((c <= '\037') && (c != '\t')) || (c >= '')) {
        throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in %s value: %s", new Object[] {
          Integer.valueOf(c), Integer.valueOf(i), name, value }));
      }
    }
  }
  
  public static final class Builder {
    final List<String> namesAndValues = new ArrayList(20);
    

    public Builder() {}
    
    Builder addLenient(String line)
    {
      int index = line.indexOf(":", 1);
      if (index != -1)
        return addLenient(line.substring(0, index), line.substring(index + 1));
      if (line.startsWith(":"))
      {

        return addLenient("", line.substring(1));
      }
      return addLenient("", line);
    }
    

    public Builder add(String line)
    {
      int index = line.indexOf(":");
      if (index == -1) {
        throw new IllegalArgumentException("Unexpected header: " + line);
      }
      return add(line.substring(0, index).trim(), line.substring(index + 1));
    }
    


    public Builder add(String name, String value)
    {
      Headers.checkName(name);
      Headers.checkValue(value, name);
      return addLenient(name, value);
    }
    



    public Builder addUnsafeNonAscii(String name, String value)
    {
      Headers.checkName(name);
      return addLenient(name, value);
    }
    


    public Builder addAll(Headers headers)
    {
      int i = 0; for (int size = headers.size(); i < size; i++) {
        addLenient(headers.name(i), headers.value(i));
      }
      
      return this;
    }
    



    public Builder add(String name, Date value)
    {
      if (value == null) throw new NullPointerException("value for name " + name + " == null");
      add(name, HttpDate.format(value));
      return this;
    }
    



    @IgnoreJRERequirement
    public Builder add(String name, Instant value)
    {
      if (value == null) throw new NullPointerException("value for name " + name + " == null");
      return add(name, new Date(value.toEpochMilli()));
    }
    



    public Builder set(String name, Date value)
    {
      if (value == null) throw new NullPointerException("value for name " + name + " == null");
      set(name, HttpDate.format(value));
      return this;
    }
    



    @IgnoreJRERequirement
    public Builder set(String name, Instant value)
    {
      if (value == null) throw new NullPointerException("value for name " + name + " == null");
      return set(name, new Date(value.toEpochMilli()));
    }
    



    Builder addLenient(String name, String value)
    {
      namesAndValues.add(name);
      namesAndValues.add(value.trim());
      return this;
    }
    
    public Builder removeAll(String name) {
      for (int i = 0; i < namesAndValues.size(); i += 2) {
        if (name.equalsIgnoreCase((String)namesAndValues.get(i))) {
          namesAndValues.remove(i);
          namesAndValues.remove(i);
          i -= 2;
        }
      }
      return this;
    }
    



    public Builder set(String name, String value)
    {
      Headers.checkName(name);
      Headers.checkValue(value, name);
      removeAll(name);
      addLenient(name, value);
      return this;
    }
    
    @Nullable
    public String get(String name) {
      for (int i = namesAndValues.size() - 2; i >= 0; i -= 2) {
        if (name.equalsIgnoreCase((String)namesAndValues.get(i))) {
          return (String)namesAndValues.get(i + 1);
        }
      }
      return null;
    }
    
    public Headers build() {
      return new Headers(this);
    }
  }
}
