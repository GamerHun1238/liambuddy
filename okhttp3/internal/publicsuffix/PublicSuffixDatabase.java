package okhttp3.internal.publicsuffix;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.IDN;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.internal.platform.Platform;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;





















public final class PublicSuffixDatabase
{
  public static final String PUBLIC_SUFFIX_RESOURCE = "publicsuffixes.gz";
  private static final byte[] WILDCARD_LABEL = { 42 };
  private static final String[] EMPTY_RULE = new String[0];
  private static final String[] PREVAILING_RULE = { "*" };
  
  private static final byte EXCEPTION_MARKER = 33;
  
  private static final PublicSuffixDatabase instance = new PublicSuffixDatabase();
  

  private final AtomicBoolean listRead = new AtomicBoolean(false);
  

  private final CountDownLatch readCompleteLatch = new CountDownLatch(1);
  
  private byte[] publicSuffixListBytes;
  
  private byte[] publicSuffixExceptionListBytes;
  
  public PublicSuffixDatabase() {}
  
  public static PublicSuffixDatabase get()
  {
    return instance;
  }
  














  public String getEffectiveTldPlusOne(String domain)
  {
    if (domain == null) { throw new NullPointerException("domain == null");
    }
    
    String unicodeDomain = IDN.toUnicode(domain);
    String[] domainLabels = unicodeDomain.split("\\.");
    String[] rule = findMatchingRule(domainLabels);
    if ((domainLabels.length == rule.length) && (rule[0].charAt(0) != '!'))
    {
      return null;
    }
    int firstLabelOffset;
    int firstLabelOffset;
    if (rule[0].charAt(0) == '!')
    {
      firstLabelOffset = domainLabels.length - rule.length;
    }
    else {
      firstLabelOffset = domainLabels.length - (rule.length + 1);
    }
    
    StringBuilder effectiveTldPlusOne = new StringBuilder();
    String[] punycodeLabels = domain.split("\\.");
    for (int i = firstLabelOffset; i < punycodeLabels.length; i++) {
      effectiveTldPlusOne.append(punycodeLabels[i]).append('.');
    }
    effectiveTldPlusOne.deleteCharAt(effectiveTldPlusOne.length() - 1);
    
    return effectiveTldPlusOne.toString();
  }
  
  private String[] findMatchingRule(String[] domainLabels) {
    if ((!listRead.get()) && (listRead.compareAndSet(false, true))) {
      readTheListUninterruptibly();
    } else {
      try {
        readCompleteLatch.await();
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
      }
    }
    
    synchronized (this) {
      if (publicSuffixListBytes == null) {
        throw new IllegalStateException("Unable to load publicsuffixes.gz resource from the classpath.");
      }
    }
    


    byte[][] domainLabelsUtf8Bytes = new byte[domainLabels.length][];
    for (int i = 0; i < domainLabels.length; i++) {
      domainLabelsUtf8Bytes[i] = domainLabels[i].getBytes(StandardCharsets.UTF_8);
    }
    


    String exactMatch = null;
    for (int i = 0; i < domainLabelsUtf8Bytes.length; i++) {
      String rule = binarySearchBytes(publicSuffixListBytes, domainLabelsUtf8Bytes, i);
      if (rule != null) {
        exactMatch = rule;
        break;
      }
    }
    





    String wildcardMatch = null;
    if (domainLabelsUtf8Bytes.length > 1) {
      byte[][] labelsWithWildcard = (byte[][])domainLabelsUtf8Bytes.clone();
      for (int labelIndex = 0; labelIndex < labelsWithWildcard.length - 1; labelIndex++) {
        labelsWithWildcard[labelIndex] = WILDCARD_LABEL;
        String rule = binarySearchBytes(publicSuffixListBytes, labelsWithWildcard, labelIndex);
        if (rule != null) {
          wildcardMatch = rule;
          break;
        }
      }
    }
    

    String exception = null;
    if (wildcardMatch != null) {
      for (int labelIndex = 0; labelIndex < domainLabelsUtf8Bytes.length - 1; labelIndex++) {
        String rule = binarySearchBytes(publicSuffixExceptionListBytes, domainLabelsUtf8Bytes, labelIndex);
        
        if (rule != null) {
          exception = rule;
          break;
        }
      }
    }
    
    if (exception != null)
    {
      exception = "!" + exception;
      return exception.split("\\."); }
    if ((exactMatch == null) && (wildcardMatch == null)) {
      return PREVAILING_RULE;
    }
    


    String[] exactRuleLabels = exactMatch != null ? exactMatch.split("\\.") : EMPTY_RULE;
    


    String[] wildcardRuleLabels = wildcardMatch != null ? wildcardMatch.split("\\.") : EMPTY_RULE;
    
    return exactRuleLabels.length > wildcardRuleLabels.length ? 
      exactRuleLabels : 
      wildcardRuleLabels;
  }
  
  private static String binarySearchBytes(byte[] bytesToSearch, byte[][] labels, int labelIndex) {
    int low = 0;
    int high = bytesToSearch.length;
    String match = null;
    while (low < high) {
      int mid = (low + high) / 2;
      

      while ((mid > -1) && (bytesToSearch[mid] != 10)) {
        mid--;
      }
      mid++;
      

      int end = 1;
      while (bytesToSearch[(mid + end)] != 10) {
        end++;
      }
      int publicSuffixLength = mid + end - mid;
      



      int currentLabelIndex = labelIndex;
      int currentLabelByteIndex = 0;
      int publicSuffixByteIndex = 0;
      
      boolean expectDot = false;
      int compareResult;
      for (;;) { int byte0;
        if (expectDot) {
          int byte0 = 46;
          expectDot = false;
        } else {
          byte0 = labels[currentLabelIndex][currentLabelByteIndex] & 0xFF;
        }
        
        int byte1 = bytesToSearch[(mid + publicSuffixByteIndex)] & 0xFF;
        
        compareResult = byte0 - byte1;
        if (compareResult != 0)
          break;
        publicSuffixByteIndex++;
        currentLabelByteIndex++;
        if (publicSuffixByteIndex == publicSuffixLength)
          break;
        if (labels[currentLabelIndex].length == currentLabelByteIndex)
        {

          if (currentLabelIndex == labels.length - 1) {
            break;
          }
          currentLabelIndex++;
          currentLabelByteIndex = -1;
          expectDot = true;
        }
      }
      

      if (compareResult < 0) {
        high = mid - 1;
      } else if (compareResult > 0) {
        low = mid + end + 1;
      }
      else {
        int publicSuffixBytesLeft = publicSuffixLength - publicSuffixByteIndex;
        int labelBytesLeft = labels[currentLabelIndex].length - currentLabelByteIndex;
        for (int i = currentLabelIndex + 1; i < labels.length; i++) {
          labelBytesLeft += labels[i].length;
        }
        
        if (labelBytesLeft < publicSuffixBytesLeft) {
          high = mid - 1;
        } else if (labelBytesLeft > publicSuffixBytesLeft) {
          low = mid + end + 1;
        }
        else {
          match = new String(bytesToSearch, mid, publicSuffixLength, StandardCharsets.UTF_8);
          break;
        }
      }
    }
    return match;
  }
  




  private void readTheListUninterruptibly()
  {
    boolean interrupted = false;
    
    try
    {
      readTheList(); return;
    } catch (InterruptedIOException e) {
      for (;;) {
        Thread.interrupted();
        interrupted = true;
      }
    } catch (IOException e) { Platform.get().log(5, "Failed to read public suffix list", e);
      return;
    }
    finally
    {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }
  

  private void readTheList()
    throws IOException
  {
    InputStream resource = PublicSuffixDatabase.class.getResourceAsStream("publicsuffixes.gz");
    if (resource == null) { return;
    }
    BufferedSource bufferedSource = Okio.buffer(new GzipSource(Okio.source(resource)));Throwable localThrowable3 = null;
    try { int totalBytes = bufferedSource.readInt();
      byte[] publicSuffixListBytes = new byte[totalBytes];
      bufferedSource.readFully(publicSuffixListBytes);
      
      int totalExceptionBytes = bufferedSource.readInt();
      byte[] publicSuffixExceptionListBytes = new byte[totalExceptionBytes];
      bufferedSource.readFully(publicSuffixExceptionListBytes);
    }
    catch (Throwable localThrowable1)
    {
      localThrowable3 = localThrowable1;throw localThrowable1;


    }
    finally
    {


      if (bufferedSource != null) if (localThrowable3 != null) try { bufferedSource.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else bufferedSource.close();
    }
    synchronized (this) { byte[] publicSuffixExceptionListBytes;
      byte[] publicSuffixListBytes; this.publicSuffixListBytes = publicSuffixListBytes;
      this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
    }
    
    readCompleteLatch.countDown();
  }
  
  void setListBytes(byte[] publicSuffixListBytes, byte[] publicSuffixExceptionListBytes)
  {
    this.publicSuffixListBytes = publicSuffixListBytes;
    this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
    listRead.set(true);
    readCompleteLatch.countDown();
  }
}
