package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.DataOutputAsStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;


























































public abstract class TokenStreamFactory
  implements Versioned, Serializable
{
  private static final long serialVersionUID = 2L;
  
  public TokenStreamFactory() {}
  
  public abstract boolean requiresPropertyOrdering();
  
  public abstract boolean canHandleBinaryNatively();
  
  public abstract boolean canParseAsync();
  
  public abstract Class<? extends FormatFeature> getFormatReadFeatureType();
  
  public abstract Class<? extends FormatFeature> getFormatWriteFeatureType();
  
  public abstract boolean canUseSchema(FormatSchema paramFormatSchema);
  
  public abstract String getFormatName();
  
  public abstract boolean isEnabled(JsonParser.Feature paramFeature);
  
  public abstract boolean isEnabled(JsonGenerator.Feature paramFeature);
  
  public abstract int getParserFeatures();
  
  public abstract int getGeneratorFeatures();
  
  public abstract int getFormatParserFeatures();
  
  public abstract int getFormatGeneratorFeatures();
  
  public abstract JsonParser createParser(byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract JsonParser createParser(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract JsonParser createParser(char[] paramArrayOfChar)
    throws IOException;
  
  public abstract JsonParser createParser(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract JsonParser createParser(DataInput paramDataInput)
    throws IOException;
  
  public abstract JsonParser createParser(File paramFile)
    throws IOException;
  
  public abstract JsonParser createParser(InputStream paramInputStream)
    throws IOException;
  
  public abstract JsonParser createParser(Reader paramReader)
    throws IOException;
  
  public abstract JsonParser createParser(String paramString)
    throws IOException;
  
  public abstract JsonParser createParser(URL paramURL)
    throws IOException;
  
  public abstract JsonParser createNonBlockingByteArrayParser()
    throws IOException;
  
  public abstract JsonGenerator createGenerator(DataOutput paramDataOutput, JsonEncoding paramJsonEncoding)
    throws IOException;
  
  public abstract JsonGenerator createGenerator(DataOutput paramDataOutput)
    throws IOException;
  
  public abstract JsonGenerator createGenerator(File paramFile, JsonEncoding paramJsonEncoding)
    throws IOException;
  
  public abstract JsonGenerator createGenerator(OutputStream paramOutputStream)
    throws IOException;
  
  public abstract JsonGenerator createGenerator(OutputStream paramOutputStream, JsonEncoding paramJsonEncoding)
    throws IOException;
  
  public abstract JsonGenerator createGenerator(Writer paramWriter)
    throws IOException;
  
  protected OutputStream _createDataOutputWrapper(DataOutput out)
  {
    return new DataOutputAsStream(out);
  }
  


  protected InputStream _optimizedStreamFromURL(URL url)
    throws IOException
  {
    if ("file".equals(url.getProtocol()))
    {





      String host = url.getHost();
      if ((host == null) || (host.length() == 0))
      {
        String path = url.getPath();
        if (path.indexOf('%') < 0) {
          return new FileInputStream(url.getPath());
        }
      }
    }
    

    return url.openStream();
  }
}
