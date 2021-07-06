package okio;


































public final class Utf8
{
  private Utf8() {}
  
































  public static long size(String string)
  {
    return size(string, 0, string.length());
  }
  



  public static long size(String string, int beginIndex, int endIndex)
  {
    if (string == null) throw new IllegalArgumentException("string == null");
    if (beginIndex < 0) throw new IllegalArgumentException("beginIndex < 0: " + beginIndex);
    if (endIndex < beginIndex) {
      throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
    }
    if (endIndex > string.length())
    {
      throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + string.length());
    }
    
    long result = 0L;
    for (int i = beginIndex; i < endIndex;) {
      int c = string.charAt(i);
      
      if (c < 128)
      {
        result += 1L;
        i++;
      }
      else if (c < 2048)
      {
        result += 2L;
        i++;
      }
      else if ((c < 55296) || (c > 57343))
      {
        result += 3L;
        i++;
      }
      else {
        int low = i + 1 < endIndex ? string.charAt(i + 1) : 0;
        if ((c > 56319) || (low < 56320) || (low > 57343))
        {
          result += 1L;
          i++;
        }
        else
        {
          result += 4L;
          i += 2;
        }
      }
    }
    
    return result;
  }
}
