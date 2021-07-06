package com.fasterxml.jackson.core.io;

import java.util.Arrays;

public final class CharTypes
{
  private static final char[] HC = "0123456789ABCDEF".toCharArray();
  private static final byte[] HB;
  
  static { int len = HC.length;
    HB = new byte[len];
    for (int i = 0; i < len; i++) {
      HB[i] = ((byte)HC[i]);
    }
    












    int[] table = new int['Ā'];
    
    for (int i = 0; i < 32; i++) {
      table[i] = -1;
    }
    
    table[34] = 1;
    table[92] = 1;
    sInputCodes = table;
    







    int[] table = new int[sInputCodes.length];
    System.arraycopy(sInputCodes, 0, table, 0, table.length);
    for (int c = 128; c < 256; c++)
    {
      int code;
      int code;
      if ((c & 0xE0) == 192) {
        code = 2; } else { int code;
        if ((c & 0xF0) == 224) {
          code = 3; } else { int code;
          if ((c & 0xF8) == 240)
          {
            code = 4;
          }
          else
            code = -1;
        } }
      table[c] = code;
    }
    sInputCodesUTF8 = table;
    









    int[] table = new int['Ā'];
    
    Arrays.fill(table, -1);
    
    for (int i = 33; i < 256; i++) {
      if (Character.isJavaIdentifierPart((char)i)) {
        table[i] = 0;
      }
    }
    


    table[64] = 0;
    table[35] = 0;
    table[42] = 0;
    table[45] = 0;
    table[43] = 0;
    sInputCodesJsNames = table;
    








    int[] table = new int['Ā'];
    
    System.arraycopy(sInputCodesJsNames, 0, table, 0, table.length);
    Arrays.fill(table, 128, 128, 0);
    sInputCodesUtf8JsNames = table;
    







    int[] buf = new int['Ā'];
    
    System.arraycopy(sInputCodesUTF8, 128, buf, 128, 128);
    

    Arrays.fill(buf, 0, 32, -1);
    buf[9] = 0;
    buf[10] = 10;
    buf[13] = 13;
    buf[42] = 42;
    sInputCodesComment = buf;
    









    int[] buf = new int['Ā'];
    System.arraycopy(sInputCodesUTF8, 128, buf, 128, 128);
    



    Arrays.fill(buf, 0, 32, -1);
    buf[32] = 1;
    buf[9] = 1;
    buf[10] = 10;
    buf[13] = 13;
    buf[47] = 47;
    buf[35] = 35;
    sInputCodesWS = buf;
    







    int[] table = new int[''];
    
    for (int i = 0; i < 32; i++)
    {
      table[i] = -1;
    }
    
    table[34] = 34;
    table[92] = 92;
    
    table[8] = 98;
    table[9] = 116;
    table[12] = 102;
    table[10] = 110;
    table[13] = 114;
    sOutputEscapes128 = table;
    








    sHexValues = new int['Ā'];
    
    Arrays.fill(sHexValues, -1);
    for (int i = 0; i < 10; i++) {
      sHexValues[(48 + i)] = i;
    }
    for (int i = 0; i < 6; i++) {
      sHexValues[(97 + i)] = (10 + i);
      sHexValues[(65 + i)] = (10 + i); } }
  
  private static final int[] sInputCodes;
  
  public static int[] getInputCodeLatin1() { return sInputCodes; }
  public static int[] getInputCodeUtf8() { return sInputCodesUTF8; }
  
  public static int[] getInputCodeLatin1JsNames() { return sInputCodesJsNames; }
  public static int[] getInputCodeUtf8JsNames() { return sInputCodesUtf8JsNames; }
  
  public static int[] getInputCodeComment() { return sInputCodesComment; }
  public static int[] getInputCodeWS() { return sInputCodesWS; }
  
  private static final int[] sInputCodesUTF8;
  private static final int[] sInputCodesJsNames;
  private static final int[] sInputCodesUtf8JsNames;
  private static final int[] sInputCodesComment;
  private static final int[] sInputCodesWS;
  private static final int[] sOutputEscapes128;
  private static final int[] sHexValues;
  public static int[] get7BitOutputEscapes() { return sOutputEscapes128; }
  





  public static int[] get7BitOutputEscapes(int quoteChar)
  {
    if (quoteChar == 34) {
      return sOutputEscapes128;
    }
    return AltEscapes.instance.escapesFor(quoteChar);
  }
  


  public static int charToHex(int ch)
  {
    return sHexValues[(ch & 0xFF)];
  }
  
  public static void appendQuoted(StringBuilder sb, String content)
  {
    int[] escCodes = sOutputEscapes128;
    int escLen = escCodes.length;
    int i = 0; for (int len = content.length(); i < len; i++) {
      char c = content.charAt(i);
      if ((c >= escLen) || (escCodes[c] == 0)) {
        sb.append(c);
      }
      else {
        sb.append('\\');
        int escCode = escCodes[c];
        if (escCode < 0)
        {







          sb.append('u');
          sb.append('0');
          sb.append('0');
          int value = c;
          sb.append(HC[(value >> 4)]);
          sb.append(HC[(value & 0xF)]);
        } else {
          sb.append((char)escCode);
        }
      }
    }
  }
  
  public static char[] copyHexChars() { return (char[])HC.clone(); }
  


  public static byte[] copyHexBytes() { return (byte[])HB.clone(); }
  
  public CharTypes() {}
  
  private static class AltEscapes { private AltEscapes() {}
    public static final AltEscapes instance = new AltEscapes();
    
    private int[][] _altEscapes = new int[''][];
    
    public int[] escapesFor(int quoteChar) {
      int[] esc = _altEscapes[quoteChar];
      if (esc == null) {
        esc = Arrays.copyOf(CharTypes.sOutputEscapes128, 128);
        
        if (esc[quoteChar] == 0) {
          esc[quoteChar] = -1;
        }
        _altEscapes[quoteChar] = esc;
      }
      return esc;
    }
  }
}
