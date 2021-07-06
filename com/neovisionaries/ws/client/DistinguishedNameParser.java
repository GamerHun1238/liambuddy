package com.neovisionaries.ws.client;

import javax.security.auth.x500.X500Principal;



























final class DistinguishedNameParser
{
  private final String dn;
  private final int length;
  private int pos;
  private int beg;
  private int end;
  private int cur;
  private char[] chars;
  
  public DistinguishedNameParser(X500Principal principal)
  {
    dn = principal.getName("RFC2253");
    length = dn.length();
  }
  


  private String nextAT()
  {
    while ((pos < length) && (chars[pos] == ' ')) { pos += 1;
    }
    if (pos == length) {
      return null;
    }
    

    beg = pos;
    

    pos += 1;
    while ((pos < length) && (chars[pos] != '=') && (chars[pos] != ' ')) { pos += 1;
    }
    

    if (pos >= length) {
      throw new IllegalStateException("Unexpected end of DN: " + dn);
    }
    

    end = pos;
    


    if (chars[pos] == ' ') {
      while ((pos < length) && (chars[pos] != '=') && (chars[pos] == ' ')) { pos += 1;
      }
      
      if ((chars[pos] != '=') || (pos == length)) {
        throw new IllegalStateException("Unexpected end of DN: " + dn);
      }
    }
    
    pos += 1;
    


    while ((pos < length) && (chars[pos] == ' ')) { pos += 1;
    }
    


    if ((end - beg > 4) && (chars[(beg + 3)] == '.') && ((chars[beg] == 'O') || (chars[beg] == 'o')) && ((chars[(beg + 1)] == 'I') || (chars[(beg + 1)] == 'i')) && ((chars[(beg + 2)] == 'D') || (chars[(beg + 2)] == 'd')))
    {


      beg += 4;
    }
    
    return new String(chars, beg, end - beg);
  }
  
  private String quotedAV()
  {
    pos += 1;
    beg = pos;
    end = beg;
    for (;;)
    {
      if (pos == length) {
        throw new IllegalStateException("Unexpected end of DN: " + dn);
      }
      
      if (chars[pos] == '"')
      {
        pos += 1;
        break; }
      if (chars[pos] == '\\') {
        chars[end] = getEscaped();
      }
      else {
        chars[end] = chars[pos];
      }
      pos += 1;
      end += 1;
    }
    


    while ((pos < length) && (chars[pos] == ' ')) { pos += 1;
    }
    
    return new String(chars, beg, end - beg);
  }
  
  private String hexAV()
  {
    if (pos + 4 >= length)
    {
      throw new IllegalStateException("Unexpected end of DN: " + dn);
    }
    
    beg = pos;
    pos += 1;
    

    for (;;)
    {
      if ((pos == length) || (chars[pos] == '+') || (chars[pos] == ',') || (chars[pos] == ';'))
      {
        end = pos;
        break;
      }
      
      if (chars[pos] == ' ') {
        end = pos;
        pos += 1;
        

        while ((pos < length) && (chars[pos] == ' ')) { pos += 1;
        }
      }
      if ((chars[pos] >= 'A') && (chars[pos] <= 'F')) {
        int tmp231_228 = pos; char[] tmp231_224 = chars;tmp231_224[tmp231_228] = ((char)(tmp231_224[tmp231_228] + ' '));
      }
      
      pos += 1;
    }
    


    int hexLen = end - beg;
    if ((hexLen < 5) || ((hexLen & 0x1) == 0)) {
      throw new IllegalStateException("Unexpected end of DN: " + dn);
    }
    

    byte[] encoded = new byte[hexLen / 2];
    int i = 0; for (int p = beg + 1; i < encoded.length; i++) {
      encoded[i] = ((byte)getByte(p));p += 2;
    }
    
    return new String(chars, beg, hexLen);
  }
  
  private String escapedAV()
  {
    beg = pos;
    end = pos;
    for (;;) {
      if (pos >= length)
      {
        return new String(chars, beg, end - beg);
      }
      
      switch (chars[pos])
      {
      case '+': 
      case ',': 
      case ';': 
        return new String(chars, beg, end - beg);
      
      case '\\': 
        chars[(end++)] = getEscaped();
        pos += 1;
        break;
      

      case ' ': 
        cur = end;
        
        pos += 1;
        chars[(end++)] = ' ';
        for (; 
            (pos < length) && (chars[pos] == ' '); pos += 1) {
          chars[(end++)] = ' ';
        }
        if ((pos == length) || (chars[pos] == ',') || (chars[pos] == '+') || (chars[pos] == ';'))
        {

          return new String(chars, beg, cur - beg);
        }
        break;
      default: 
        chars[(end++)] = chars[pos];
        pos += 1;
      }
    }
  }
  
  private char getEscaped()
  {
    pos += 1;
    if (pos == length) {
      throw new IllegalStateException("Unexpected end of DN: " + dn);
    }
    
    switch (chars[pos])
    {
    case ' ': 
    case '"': 
    case '#': 
    case '%': 
    case '*': 
    case '+': 
    case ',': 
    case ';': 
    case '<': 
    case '=': 
    case '>': 
    case '\\': 
    case '_': 
      return chars[pos];
    }
    
    
    return getUTF8();
  }
  


  private char getUTF8()
  {
    int res = getByte(pos);
    pos += 1;
    
    if (res < 128)
      return (char)res;
    if ((res >= 192) && (res <= 247))
    {
      int count;
      if (res <= 223) {
        int count = 1;
        res &= 0x1F;
      } else if (res <= 239) {
        int count = 2;
        res &= 0xF;
      } else {
        count = 3;
        res &= 0x7;
      }
      

      for (int i = 0; i < count; i++) {
        pos += 1;
        if ((pos == length) || (chars[pos] != '\\')) {
          return '?';
        }
        pos += 1;
        
        int b = getByte(pos);
        pos += 1;
        if ((b & 0xC0) != 128) {
          return '?';
        }
        
        res = (res << 6) + (b & 0x3F);
      }
      return (char)res;
    }
    return '?';
  }
  






  private int getByte(int position)
  {
    if (position + 1 >= length) {
      throw new IllegalStateException("Malformed DN: " + dn);
    }
    


    int b1 = chars[position];
    if ((b1 >= 48) && (b1 <= 57)) {
      b1 -= 48;
    } else if ((b1 >= 97) && (b1 <= 102)) {
      b1 -= 87;
    } else if ((b1 >= 65) && (b1 <= 70)) {
      b1 -= 55;
    } else {
      throw new IllegalStateException("Malformed DN: " + dn);
    }
    
    int b2 = chars[(position + 1)];
    if ((b2 >= 48) && (b2 <= 57)) {
      b2 -= 48;
    } else if ((b2 >= 97) && (b2 <= 102)) {
      b2 -= 87;
    } else if ((b2 >= 65) && (b2 <= 70)) {
      b2 -= 55;
    } else {
      throw new IllegalStateException("Malformed DN: " + dn);
    }
    
    return (b1 << 4) + b2;
  }
  






  public String findMostSpecific(String attributeType)
  {
    pos = 0;
    beg = 0;
    end = 0;
    cur = 0;
    chars = dn.toCharArray();
    
    String attType = nextAT();
    if (attType == null) {
      return null;
    }
    for (;;) {
      String attValue = "";
      
      if (pos == length) {
        return null;
      }
      
      switch (chars[pos]) {
      case '"': 
        attValue = quotedAV();
        break;
      case '#': 
        attValue = hexAV();
        break;
      case '+': 
      case ',': 
      case ';': 
        break;
      
      default: 
        attValue = escapedAV();
      }
      
      


      if (attributeType.equalsIgnoreCase(attType)) {
        return attValue;
      }
      
      if (pos >= length) {
        return null;
      }
      
      if ((chars[pos] != ',') && (chars[pos] != ';') && 
        (chars[pos] != '+')) {
        throw new IllegalStateException("Malformed DN: " + dn);
      }
      
      pos += 1;
      attType = nextAT();
      if (attType == null) {
        throw new IllegalStateException("Malformed DN: " + dn);
      }
    }
  }
}
