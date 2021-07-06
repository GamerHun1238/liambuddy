package club.minnced.opus.util;

import com.sun.jna.Platform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;















public final class OpusLibrary
{
  private static boolean initialized = false;
  



  private static final Map<String, String> platforms = new HashMap(10);
  static { platforms.put("darwin", "dylib");
    platforms.put("linux-arm", "so");
    platforms.put("linux-aarch64", "so");
    platforms.put("linux-x86", "so");
    platforms.put("linux-x86-64", "so");
    platforms.put("win32-x86", "dll");
    platforms.put("win32-x86-64", "dll"); }
  private static final String SUPPORTED_SYSTEMS = "Supported Systems: " + platforms.keySet() + "\nCurrent Operating system: " + Platform.RESOURCE_PREFIX;
  








  public static List<String> getSupportedPlatforms()
  {
    return Collections.unmodifiableList(new ArrayList(platforms.keySet()));
  }
  




  public static boolean isSupportedPlatform()
  {
    return platforms.containsKey(Platform.RESOURCE_PREFIX);
  }
  





  public static synchronized boolean isInitialized()
  {
    return initialized;
  }
  








  public static synchronized boolean loadFrom(String absolutePath)
  {
    if (initialized)
      return false;
    System.load(absolutePath);
    System.setProperty("opus.lib", absolutePath);
    return OpusLibrary.initialized = 1;
  }
  









  public static synchronized boolean loadFromJar()
    throws IOException
  {
    if (initialized)
      return false;
    String nativesRoot = "";
    
    try
    {
      String platform = Platform.RESOURCE_PREFIX;
      String ext = (String)platforms.get(platform);
      if (ext == null) {
        throw new UnsupportedOperationException(SUPPORTED_SYSTEMS);
      }
      String tmpRoot = String.format("/natives/%s/libopus.%s", new Object[] { platform, ext });
      NativeUtil.loadLibraryFromJar(tmpRoot);
      nativesRoot = tmpRoot;
      initialized = true;
    } finally {
      System.setProperty("opus.lib", nativesRoot);
    }
    return true;
  }
  
  private OpusLibrary() {}
}
