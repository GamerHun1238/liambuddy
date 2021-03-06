package com.neovisionaries.ws.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
























class Misc
{
  private static final SecureRandom sRandom = new SecureRandom();
  




  private Misc() {}
  



  public static byte[] getBytesUTF8(String string)
  {
    if (string == null)
    {
      return null;
    }
    
    try
    {
      return string.getBytes("UTF-8");
    }
    catch (UnsupportedEncodingException e) {}
    

    return null;
  }
  





  public static String toStringUTF8(byte[] bytes)
  {
    if (bytes == null)
    {
      return null;
    }
    
    return toStringUTF8(bytes, 0, bytes.length);
  }
  




  public static String toStringUTF8(byte[] bytes, int offset, int length)
  {
    if (bytes == null)
    {
      return null;
    }
    
    try
    {
      return new String(bytes, offset, length, "UTF-8");

    }
    catch (UnsupportedEncodingException e)
    {
      return null;
    }
    catch (IndexOutOfBoundsException e) {}
    
    return null;
  }
  





  public static byte[] nextBytes(byte[] buffer)
  {
    sRandom.nextBytes(buffer);
    
    return buffer;
  }
  




  public static byte[] nextBytes(int nBytes)
  {
    byte[] buffer = new byte[nBytes];
    
    return nextBytes(buffer);
  }
  




  public static String toOpcodeName(int opcode)
  {
    switch (opcode)
    {
    case 0: 
      return "CONTINUATION";
    
    case 1: 
      return "TEXT";
    
    case 2: 
      return "BINARY";
    
    case 8: 
      return "CLOSE";
    
    case 9: 
      return "PING";
    
    case 10: 
      return "PONG";
    }
    
    


    if ((1 <= opcode) && (opcode <= 7))
    {
      return String.format("DATA(0x%X)", new Object[] { Integer.valueOf(opcode) });
    }
    
    if ((8 <= opcode) && (opcode <= 15))
    {
      return String.format("CONTROL(0x%X)", new Object[] { Integer.valueOf(opcode) });
    }
    
    return String.format("0x%X", new Object[] { Integer.valueOf(opcode) });
  }
  



  public static String readLine(InputStream in, String charset)
    throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    

    for (;;)
    {
      int b = in.read();
      

      if (b == -1)
      {
        if (baos.size() != 0) {
          break;
        }
        return null;
      }
      






      if (b == 10) {
        break;
      }
      


      if (b != 13)
      {

        baos.write(b);

      }
      else
      {
        int b2 = in.read();
        

        if (b2 == -1)
        {

          baos.write(b);
          

          break;
        }
        

        if (b2 == 10) {
          break;
        }
        



        baos.write(b);
        

        baos.write(b2);
      }
    }
    
    return baos.toString(charset);
  }
  




  public static int min(int[] values)
  {
    int min = Integer.MAX_VALUE;
    
    for (int i = 0; i < values.length; i++)
    {
      if (values[i] < min)
      {
        min = values[i];
      }
    }
    
    return min;
  }
  




  public static int max(int[] values)
  {
    int max = Integer.MIN_VALUE;
    
    for (int i = 0; i < values.length; i++)
    {
      if (max < values[i])
      {
        max = values[i];
      }
    }
    
    return max;
  }
  

  public static String join(Collection<?> values, String delimiter)
  {
    StringBuilder builder = new StringBuilder();
    
    join(builder, values, delimiter);
    
    return builder.toString();
  }
  

  private static void join(StringBuilder builder, Collection<?> values, String delimiter)
  {
    boolean first = true;
    
    for (Object value : values)
    {
      if (first)
      {
        first = false;
      }
      else
      {
        builder.append(delimiter);
      }
      
      builder.append(value.toString());
    }
  }
  


  public static String extractHost(URI uri)
  {
    String host = uri.getHost();
    
    if (host != null)
    {
      return host;
    }
    










    host = extractHostFromAuthorityPart(uri.getRawAuthority());
    
    if (host != null)
    {
      return host;
    }
    

    return extractHostFromEntireUri(uri.toString());
  }
  


  static String extractHostFromAuthorityPart(String authority)
  {
    if (authority == null)
    {

      return null;
    }
    

    Matcher matcher = Pattern.compile("^(.*@)?([^:]+)(:\\d+)?$").matcher(authority);
    

    if ((matcher == null) || (!matcher.matches()))
    {

      return null;
    }
    

    return matcher.group(2);
  }
  

  static String extractHostFromEntireUri(String uri)
  {
    if (uri == null)
    {

      return null;
    }
    

    Matcher matcher = Pattern.compile("^\\w+://([^@/]*@)?([^:/]+)(:\\d+)?(/.*)?$").matcher(uri);
    

    if ((matcher == null) || (!matcher.matches()))
    {

      return null;
    }
    

    return matcher.group(2);
  }
  

  public static Constructor<?> getConstructor(String className, Class<?>[] parameterTypes)
  {
    try
    {
      return Class.forName(className).getConstructor(parameterTypes);
    }
    catch (Exception e) {}
    
    return null;
  }
  


  public static Object newInstance(Constructor<?> constructor, Object... parameters)
  {
    if (constructor == null)
    {
      return null;
    }
    
    try
    {
      return constructor.newInstance(parameters);
    }
    catch (Exception e) {}
    
    return null;
  }
  


  public static Method getMethod(String className, String methodName, Class<?>[] parameterTypes)
  {
    try
    {
      return Class.forName(className).getMethod(methodName, parameterTypes);
    }
    catch (Exception e) {}
    
    return null;
  }
  


  public static Object invoke(Method method, Object object, Object... parameters)
  {
    if (method == null)
    {
      return null;
    }
    
    try
    {
      return method.invoke(object, parameters);
    }
    catch (Exception e) {}
    
    return null;
  }
}
