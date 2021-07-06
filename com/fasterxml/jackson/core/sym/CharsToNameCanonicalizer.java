package com.fasterxml.jackson.core.sym;

import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.fasterxml.jackson.core.util.InternCache;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReference;








































































































































































































public final class CharsToNameCanonicalizer
{
  public static final int HASH_MULT = 33;
  private static final int DEFAULT_T_SIZE = 64;
  private static final int MAX_T_SIZE = 65536;
  static final int MAX_ENTRIES_FOR_REUSE = 12000;
  static final int MAX_COLL_CHAIN_LENGTH = 100;
  private final CharsToNameCanonicalizer _parent;
  private final AtomicReference<TableInfo> _tableInfo;
  private final int _seed;
  private final int _flags;
  private boolean _canonicalize;
  private String[] _symbols;
  private Bucket[] _buckets;
  private int _size;
  private int _sizeThreshold;
  private int _indexMask;
  private int _longestCollisionList;
  private boolean _hashShared;
  private BitSet _overflows;
  
  private CharsToNameCanonicalizer(int seed)
  {
    _parent = null;
    _seed = seed;
    

    _canonicalize = true;
    _flags = -1;
    
    _hashShared = false;
    _longestCollisionList = 0;
    
    _tableInfo = new AtomicReference(TableInfo.createInitial(64));
  }
  






  private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, int flags, int seed, TableInfo parentState)
  {
    _parent = parent;
    _seed = seed;
    _tableInfo = null;
    _flags = flags;
    _canonicalize = JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags);
    

    _symbols = symbols;
    _buckets = buckets;
    
    _size = size;
    _longestCollisionList = longestCollisionList;
    

    int arrayLen = _symbols.length;
    _sizeThreshold = _thresholdSize(arrayLen);
    _indexMask = (arrayLen - 1);
    

    _hashShared = true;
  }
  
  private static int _thresholdSize(int hashAreaSize) { return hashAreaSize - (hashAreaSize >> 2); }
  













  public static CharsToNameCanonicalizer createRoot()
  {
    long now = System.currentTimeMillis();
    
    int seed = (int)now + (int)(now >>> 32) | 0x1;
    return createRoot(seed);
  }
  
  protected static CharsToNameCanonicalizer createRoot(int seed) {
    return new CharsToNameCanonicalizer(seed);
  }
  










  public CharsToNameCanonicalizer makeChild(int flags)
  {
    return new CharsToNameCanonicalizer(this, flags, _seed, (TableInfo)_tableInfo.get());
  }
  






  public void release()
  {
    if (!maybeDirty()) { return;
    }
    
    if ((_parent != null) && (_canonicalize)) {
      _parent.mergeChild(new TableInfo(this));
      

      _hashShared = true;
    }
  }
  







  private void mergeChild(TableInfo childState)
  {
    int childCount = size;
    TableInfo currState = (TableInfo)_tableInfo.get();
    


    if (childCount == size) {
      return;
    }
    



    if (childCount > 12000)
    {
      childState = TableInfo.createInitial(64);
    }
    _tableInfo.compareAndSet(currState, childState);
  }
  





  public int size()
  {
    if (_tableInfo != null) {
      return _tableInfo.get()).size;
    }
    
    return _size;
  }
  






  public int bucketCount() { return _symbols.length; }
  public boolean maybeDirty() { return !_hashShared; }
  public int hashSeed() { return _seed; }
  






  public int collisionCount()
  {
    int count = 0;
    
    for (Bucket bucket : _buckets) {
      if (bucket != null) {
        count += length;
      }
    }
    return count;
  }
  





  public int maxCollisionLength()
  {
    return _longestCollisionList;
  }
  





  public String findSymbol(char[] buffer, int start, int len, int h)
  {
    if (len < 1) {
      return "";
    }
    if (!_canonicalize) {
      return new String(buffer, start, len);
    }
    





    int index = _hashToIndex(h);
    String sym = _symbols[index];
    

    if (sym != null)
    {
      if (sym.length() == len) {
        int i = 0;
        while (sym.charAt(i) == buffer[(start + i)])
        {
          i++; if (i == len) {
            return sym;
          }
        }
      }
      Bucket b = _buckets[(index >> 1)];
      if (b != null) {
        sym = b.has(buffer, start, len);
        if (sym != null) {
          return sym;
        }
        sym = _findSymbol2(buffer, start, len, next);
        if (sym != null) {
          return sym;
        }
      }
    }
    return _addSymbol(buffer, start, len, h, index);
  }
  
  private String _findSymbol2(char[] buffer, int start, int len, Bucket b) {
    while (b != null) {
      String sym = b.has(buffer, start, len);
      if (sym != null) {
        return sym;
      }
      b = next;
    }
    return null;
  }
  
  private String _addSymbol(char[] buffer, int start, int len, int h, int index)
  {
    if (_hashShared) {
      copyArrays();
      _hashShared = false;
    } else if (_size >= _sizeThreshold) {
      rehash();
      

      index = _hashToIndex(calcHash(buffer, start, len));
    }
    
    String newSymbol = new String(buffer, start, len);
    if (JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(_flags)) {
      newSymbol = InternCache.instance.intern(newSymbol);
    }
    _size += 1;
    
    if (_symbols[index] == null) {
      _symbols[index] = newSymbol;
    } else {
      int bix = index >> 1;
      Bucket newB = new Bucket(newSymbol, _buckets[bix]);
      int collLen = length;
      if (collLen > 100)
      {

        _handleSpillOverflow(bix, newB, index);
      } else {
        _buckets[bix] = newB;
        _longestCollisionList = Math.max(collLen, _longestCollisionList);
      }
    }
    return newSymbol;
  }
  







  private void _handleSpillOverflow(int bucketIndex, Bucket newBucket, int mainIndex)
  {
    if (_overflows == null) {
      _overflows = new BitSet();
      _overflows.set(bucketIndex);
    }
    else if (_overflows.get(bucketIndex))
    {
      if (JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(_flags)) {
        reportTooManyCollisions(100);
      }
      

      _canonicalize = false;
    } else {
      _overflows.set(bucketIndex);
    }
    


    _symbols[mainIndex] = symbol;
    _buckets[bucketIndex] = null;
    
    _size -= length;
    
    _longestCollisionList = -1;
  }
  




  public int _hashToIndex(int rawHash)
  {
    rawHash += (rawHash >>> 15);
    rawHash ^= rawHash << 7;
    rawHash += (rawHash >>> 3);
    return rawHash & _indexMask;
  }
  








  public int calcHash(char[] buffer, int start, int len)
  {
    int hash = _seed;
    int i = start; for (int end = start + len; i < end; i++) {
      hash = hash * 33 + buffer[i];
    }
    
    return hash == 0 ? 1 : hash;
  }
  
  public int calcHash(String key)
  {
    int len = key.length();
    
    int hash = _seed;
    for (int i = 0; i < len; i++) {
      hash = hash * 33 + key.charAt(i);
    }
    
    return hash == 0 ? 1 : hash;
  }
  









  private void copyArrays()
  {
    String[] oldSyms = _symbols;
    _symbols = ((String[])Arrays.copyOf(oldSyms, oldSyms.length));
    Bucket[] oldBuckets = _buckets;
    _buckets = ((Bucket[])Arrays.copyOf(oldBuckets, oldBuckets.length));
  }
  






  private void rehash()
  {
    int size = _symbols.length;
    int newSize = size + size;
    




    if (newSize > 65536)
    {

      _size = 0;
      _canonicalize = false;
      
      _symbols = new String[64];
      _buckets = new Bucket[32];
      _indexMask = 63;
      _hashShared = false;
      return;
    }
    
    String[] oldSyms = _symbols;
    Bucket[] oldBuckets = _buckets;
    _symbols = new String[newSize];
    _buckets = new Bucket[newSize >> 1];
    
    _indexMask = (newSize - 1);
    _sizeThreshold = _thresholdSize(newSize);
    
    int count = 0;
    


    int maxColl = 0;
    for (int i = 0; i < size; i++) {
      String symbol = oldSyms[i];
      if (symbol != null) {
        count++;
        int index = _hashToIndex(calcHash(symbol));
        if (_symbols[index] == null) {
          _symbols[index] = symbol;
        } else {
          int bix = index >> 1;
          Bucket newB = new Bucket(symbol, _buckets[bix]);
          _buckets[bix] = newB;
          maxColl = Math.max(maxColl, length);
        }
      }
    }
    
    int bucketSize = size >> 1;
    for (int i = 0; i < bucketSize; i++) {
      Bucket b = oldBuckets[i];
      while (b != null) {
        count++;
        String symbol = symbol;
        int index = _hashToIndex(calcHash(symbol));
        if (_symbols[index] == null) {
          _symbols[index] = symbol;
        } else {
          int bix = index >> 1;
          Bucket newB = new Bucket(symbol, _buckets[bix]);
          _buckets[bix] = newB;
          maxColl = Math.max(maxColl, length);
        }
        b = next;
      }
    }
    _longestCollisionList = maxColl;
    _overflows = null;
    
    if (count != _size) {
      throw new IllegalStateException(String.format("Internal error on SymbolTable.rehash(): had %d entries; now have %d", new Object[] {
      
        Integer.valueOf(_size), Integer.valueOf(count) }));
    }
  }
  


  protected void reportTooManyCollisions(int maxLen)
  {
    throw new IllegalStateException("Longest collision chain in symbol table (of size " + _size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
  }
  







  protected void verifyInternalConsistency()
  {
    int count = 0;
    int size = _symbols.length;
    
    for (int i = 0; i < size; i++) {
      String symbol = _symbols[i];
      if (symbol != null) {
        count++;
      }
    }
    
    int bucketSize = size >> 1;
    for (int i = 0; i < bucketSize; i++) {
      for (Bucket b = _buckets[i]; b != null; b = next) {
        count++;
      }
    }
    if (count != _size) {
      throw new IllegalStateException(String.format("Internal error: expected internal size %d vs calculated count %d", new Object[] {
        Integer.valueOf(_size), Integer.valueOf(count) }));
    }
  }
  















  static final class Bucket
  {
    public final String symbol;
    














    public final Bucket next;
    













    public final int length;
    














    public Bucket(String s, Bucket n)
    {
      symbol = s;
      next = n;
      length = (n == null ? 1 : length + 1);
    }
    
    public String has(char[] buf, int start, int len) {
      if (symbol.length() != len) {
        return null;
      }
      int i = 0;
      do {
        if (symbol.charAt(i) != buf[(start + i)]) {
          return null;
        }
        i++; } while (i < len);
      return symbol;
    }
  }
  


  private static final class TableInfo
  {
    final int size;
    

    final int longestCollisionList;
    

    final String[] symbols;
    
    final CharsToNameCanonicalizer.Bucket[] buckets;
    

    public TableInfo(int size, int longestCollisionList, String[] symbols, CharsToNameCanonicalizer.Bucket[] buckets)
    {
      this.size = size;
      this.longestCollisionList = longestCollisionList;
      this.symbols = symbols;
      this.buckets = buckets;
    }
    
    public TableInfo(CharsToNameCanonicalizer src)
    {
      size = _size;
      longestCollisionList = _longestCollisionList;
      symbols = _symbols;
      buckets = _buckets;
    }
    
    public static TableInfo createInitial(int sz) {
      return new TableInfo(0, 0, new String[sz], new CharsToNameCanonicalizer.Bucket[sz >> 1]);
    }
  }
}
