package okhttp3.internal.http2;

import java.util.Arrays;

































public final class Settings
{
  static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
  static final int HEADER_TABLE_SIZE = 1;
  static final int ENABLE_PUSH = 2;
  static final int MAX_CONCURRENT_STREAMS = 4;
  static final int MAX_FRAME_SIZE = 5;
  static final int MAX_HEADER_LIST_SIZE = 6;
  static final int INITIAL_WINDOW_SIZE = 7;
  static final int COUNT = 10;
  private int set;
  
  public Settings() {}
  
  private final int[] values = new int[10];
  
  void clear() {
    set = 0;
    Arrays.fill(values, 0);
  }
  
  Settings set(int id, int value) {
    if ((id < 0) || (id >= values.length)) {
      return this;
    }
    
    int bit = 1 << id;
    set |= bit;
    values[id] = value;
    return this;
  }
  
  boolean isSet(int id)
  {
    int bit = 1 << id;
    return (set & bit) != 0;
  }
  
  int get(int id)
  {
    return values[id];
  }
  
  int size()
  {
    return Integer.bitCount(set);
  }
  
  int getHeaderTableSize()
  {
    int bit = 2;
    return (bit & set) != 0 ? values[1] : -1;
  }
  
  boolean getEnablePush(boolean defaultValue)
  {
    int bit = 4;
    return (defaultValue ? 1 : (bit & set) != 0 ? values[2] : 0) == 1;
  }
  
  int getMaxConcurrentStreams(int defaultValue) {
    int bit = 16;
    return (bit & set) != 0 ? values[4] : defaultValue;
  }
  
  int getMaxFrameSize(int defaultValue) {
    int bit = 32;
    return (bit & set) != 0 ? values[5] : defaultValue;
  }
  
  int getMaxHeaderListSize(int defaultValue) {
    int bit = 64;
    return (bit & set) != 0 ? values[6] : defaultValue;
  }
  
  int getInitialWindowSize() {
    int bit = 128;
    return (bit & set) != 0 ? values[7] : 65535;
  }
  



  void merge(Settings other)
  {
    for (int i = 0; i < 10; i++) {
      if (other.isSet(i)) {
        set(i, other.get(i));
      }
    }
  }
}
