package com.sun.jna;










public class LastErrorException
  extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  








  private int errorCode;
  








  private static String formatMessage(int code)
  {
    return "errno was " + code;
  }
  
  private static String parseMessage(String m)
  {
    try
    {
      return formatMessage(Integer.parseInt(m));
    } catch (NumberFormatException e) {}
    return m;
  }
  



  public int getErrorCode()
  {
    return errorCode;
  }
  
  public LastErrorException(String msg) {
    super(parseMessage(msg.trim()));
    try {
      if (msg.startsWith("[")) {
        msg = msg.substring(1, msg.indexOf("]"));
      }
      errorCode = Integer.parseInt(msg);
    } catch (NumberFormatException e) {
      errorCode = -1;
    }
  }
  
  public LastErrorException(int code) {
    this(code, formatMessage(code));
  }
  
  protected LastErrorException(int code, String msg) {
    super(msg);
    errorCode = code;
  }
}
