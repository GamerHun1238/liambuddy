package com.sun.jna;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;








































public final class Platform
{
  public static final int UNSPECIFIED = -1;
  public static final int MAC = 0;
  public static final int LINUX = 1;
  public static final int WINDOWS = 2;
  public static final int SOLARIS = 3;
  public static final int FREEBSD = 4;
  public static final int OPENBSD = 5;
  public static final int WINDOWSCE = 6;
  public static final int AIX = 7;
  public static final int ANDROID = 8;
  public static final int GNU = 9;
  public static final int KFREEBSD = 10;
  public static final int NETBSD = 11;
  public static final boolean RO_FIELDS;
  public static final boolean HAS_BUFFERS;
  public static final boolean HAS_AWT;
  public static final boolean HAS_JAWT;
  public static final String MATH_LIBRARY_NAME;
  public static final String C_LIBRARY_NAME;
  public static final boolean HAS_DLL_CALLBACKS;
  
  static
  {
    String osName = System.getProperty("os.name");
    if (osName.startsWith("Linux")) {
      if ("dalvik".equals(System.getProperty("java.vm.name").toLowerCase())) {
        osType = 8;
        
        System.setProperty("jna.nounpack", "true");
      }
      else {
        osType = 1;
      }
    }
    else if (osName.startsWith("AIX")) {
      osType = 7;
    }
    else if ((osName.startsWith("Mac")) || (osName.startsWith("Darwin"))) {
      osType = 0;
    }
    else if (osName.startsWith("Windows CE")) {
      osType = 6;
    }
    else if (osName.startsWith("Windows")) {
      osType = 2;
    }
    else if ((osName.startsWith("Solaris")) || (osName.startsWith("SunOS"))) {
      osType = 3;
    }
    else if (osName.startsWith("FreeBSD")) {
      osType = 4;
    }
    else if (osName.startsWith("OpenBSD")) {
      osType = 5;
    }
    else if (osName.equalsIgnoreCase("gnu")) {
      osType = 9;
    }
    else if (osName.equalsIgnoreCase("gnu/kfreebsd")) {
      osType = 10;
    }
    else if (osName.equalsIgnoreCase("netbsd")) {
      osType = 11;
    }
    else {
      osType = -1;
    }
    boolean hasBuffers = false;
    try {
      Class.forName("java.nio.Buffer");
      hasBuffers = true;
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    



    HAS_AWT = (osType != 6) && (osType != 8) && (osType != 7);
    HAS_JAWT = (HAS_AWT) && (osType != 0);
    HAS_BUFFERS = hasBuffers;
    RO_FIELDS = osType != 6;
    C_LIBRARY_NAME = osType == 6 ? "coredll" : osType == 2 ? "msvcrt" : "c";
    MATH_LIBRARY_NAME = osType == 6 ? "coredll" : osType == 2 ? "msvcrt" : "m";
    HAS_DLL_CALLBACKS = osType == 2;
    ARCH = getCanonicalArchitecture(System.getProperty("os.arch"), isSoftFloat()); }
  public static final String RESOURCE_PREFIX = getNativeLibraryResourcePrefix();
  private static final int osType;
  public static final String ARCH;
  
  public static final int getOSType() { return osType; }
  
  public static final boolean isMac() {
    return osType == 0;
  }
  
  public static final boolean isAndroid() { return osType == 8; }
  
  public static final boolean isLinux() {
    return osType == 1;
  }
  
  public static final boolean isAIX() { return osType == 7; }
  /**
   * @deprecated
   */
  public static final boolean isAix() { return isAIX(); }
  
  public static final boolean isWindowsCE() {
    return osType == 6;
  }
  
  public static final boolean isWindows() {
    return (osType == 2) || (osType == 6);
  }
  
  public static final boolean isSolaris() { return osType == 3; }
  
  public static final boolean isFreeBSD() {
    return osType == 4;
  }
  
  public static final boolean isOpenBSD() { return osType == 5; }
  
  public static final boolean isNetBSD() {
    return osType == 11;
  }
  
  public static final boolean isGNU() { return osType == 9; }
  
  public static final boolean iskFreeBSD() {
    return osType == 10;
  }
  

  public static final boolean isX11() { return (!isWindows()) && (!isMac()); }
  
  public static final boolean hasRuntimeExec() {
    if ((isWindowsCE()) && ("J9".equals(System.getProperty("java.vm.name"))))
      return false;
    return true;
  }
  
  public static final boolean is64Bit() { String model = System.getProperty("sun.arch.data.model", 
      System.getProperty("com.ibm.vm.bitmode"));
    if (model != null) {
      return "64".equals(model);
    }
    if (("x86-64".equals(ARCH)) || 
      ("ia64".equals(ARCH)) || 
      ("ppc64".equals(ARCH)) || ("ppc64le".equals(ARCH)) || 
      ("sparcv9".equals(ARCH)) || 
      ("amd64".equals(ARCH))) {
      return true;
    }
    return Native.POINTER_SIZE == 8;
  }
  
  public static final boolean isIntel() {
    if (ARCH.startsWith("x86")) {
      return true;
    }
    return false;
  }
  
  public static final boolean isPPC() {
    if (ARCH.startsWith("ppc")) {
      return true;
    }
    return false;
  }
  
  public static final boolean isARM() {
    return ARCH.startsWith("arm");
  }
  
  public static final boolean isSPARC() {
    return ARCH.startsWith("sparc");
  }
  
  static String getCanonicalArchitecture(String arch, boolean softfloat) {
    arch = arch.toLowerCase().trim();
    if ("powerpc".equals(arch)) {
      arch = "ppc";
    }
    else if ("powerpc64".equals(arch)) {
      arch = "ppc64";
    }
    else if (("i386".equals(arch)) || ("i686".equals(arch))) {
      arch = "x86";
    }
    else if (("x86_64".equals(arch)) || ("amd64".equals(arch))) {
      arch = "x86-64";
    }
    

    if (("ppc64".equals(arch)) && ("little".equals(System.getProperty("sun.cpu.endian")))) {
      arch = "ppc64le";
    }
    
    if (("arm".equals(arch)) && (softfloat)) {
      arch = "armel";
    }
    

    return arch;
  }
  
  private static boolean isSoftFloat() {
    try {
      File self = new File("/proc/self/exe");
      ELFAnalyser ahfd = ELFAnalyser.analyse(self.getCanonicalPath());
      return ahfd.isArmSoftFloat();
    }
    catch (IOException ex) {
      Logger.getLogger(Platform.class.getName()).log(Level.FINE, null, ex);
    }
    return false;
  }
  


  static String getNativeLibraryResourcePrefix()
  {
    String prefix = System.getProperty("jna.prefix");
    if (prefix != null) {
      return prefix;
    }
    return getNativeLibraryResourcePrefix(getOSType(), System.getProperty("os.arch"), System.getProperty("os.name"));
  }
  






  static String getNativeLibraryResourcePrefix(int osType, String arch, String name)
  {
    return getNativeLibraryResourcePrefix(osType, arch, name, isSoftFloat());
  }
  
  static String getNativeLibraryResourcePrefix(int osType, String arch, String name, boolean isSoftfloat)
  {
    arch = getCanonicalArchitecture(arch, isSoftfloat);
    String osPrefix; String osPrefix; String osPrefix; String osPrefix; String osPrefix; String osPrefix; String osPrefix; String osPrefix; String osPrefix; String osPrefix; String osPrefix; switch (osType) {
    case 8: 
      if (arch.startsWith("arm")) {
        arch = "arm";
      }
      osPrefix = "android-" + arch;
      break;
    case 2: 
      osPrefix = "win32-" + arch;
      break;
    case 6: 
      osPrefix = "w32ce-" + arch;
      break;
    case 0: 
      osPrefix = "darwin";
      break;
    case 1: 
      osPrefix = "linux-" + arch;
      break;
    case 3: 
      osPrefix = "sunos-" + arch;
      break;
    case 4: 
      osPrefix = "freebsd-" + arch;
      break;
    case 5: 
      osPrefix = "openbsd-" + arch;
      break;
    case 11: 
      osPrefix = "netbsd-" + arch;
      break;
    case 10: 
      osPrefix = "kfreebsd-" + arch;
      break;
    case 7: case 9: default: 
      osPrefix = name.toLowerCase();
      int space = osPrefix.indexOf(" ");
      if (space != -1) {
        osPrefix = osPrefix.substring(0, space);
      }
      osPrefix = osPrefix + "-" + arch;
    }
    
    return osPrefix;
  }
  
  private Platform() {}
}
