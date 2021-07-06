package gnu.trove;

import gnu.trove.list.TByteList;
import gnu.trove.list.TCharList;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.TShortList;
import gnu.trove.map.TByteByteMap;
import gnu.trove.map.TByteCharMap;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.map.TByteIntMap;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.TByteShortMap;
import gnu.trove.map.TCharCharMap;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.map.TCharLongMap;
import gnu.trove.map.TCharShortMap;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongByteMap;
import gnu.trove.map.TLongCharMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TLongShortMap;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortCharMap;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.map.TShortLongMap;
import gnu.trove.map.TShortShortMap;
import gnu.trove.set.TByteSet;
import gnu.trove.set.TCharSet;
import gnu.trove.set.TDoubleSet;
import gnu.trove.set.TFloatSet;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.TShortSet;
import java.util.RandomAccess;

public class TCollections
{
  private TCollections() {}
  
  public static TDoubleCollection unmodifiableCollection(TDoubleCollection c)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCollection(c);
  }
  





















  public static TFloatCollection unmodifiableCollection(TFloatCollection c)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatCollection(c);
  }
  





















  public static TIntCollection unmodifiableCollection(TIntCollection c)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntCollection(c);
  }
  





















  public static TLongCollection unmodifiableCollection(TLongCollection c)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongCollection(c);
  }
  





















  public static TByteCollection unmodifiableCollection(TByteCollection c)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteCollection(c);
  }
  





















  public static TShortCollection unmodifiableCollection(TShortCollection c)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortCollection(c);
  }
  





















  public static TCharCollection unmodifiableCollection(TCharCollection c)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharCollection(c);
  }
  














  public static TDoubleSet unmodifiableSet(TDoubleSet s)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleSet(s);
  }
  












  public static TFloatSet unmodifiableSet(TFloatSet s)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatSet(s);
  }
  












  public static TIntSet unmodifiableSet(TIntSet s)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntSet(s);
  }
  












  public static TLongSet unmodifiableSet(TLongSet s)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongSet(s);
  }
  












  public static TByteSet unmodifiableSet(TByteSet s)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteSet(s);
  }
  












  public static TShortSet unmodifiableSet(TShortSet s)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortSet(s);
  }
  












  public static TCharSet unmodifiableSet(TCharSet s)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharSet(s);
  }
  















  public static TDoubleList unmodifiableList(TDoubleList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessDoubleList(list) : new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleList(list);
  }
  
















  public static TFloatList unmodifiableList(TFloatList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessFloatList(list) : new gnu.trove.impl.unmodifiable.TUnmodifiableFloatList(list);
  }
  
















  public static TIntList unmodifiableList(TIntList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessIntList(list) : new gnu.trove.impl.unmodifiable.TUnmodifiableIntList(list);
  }
  
















  public static TLongList unmodifiableList(TLongList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessLongList(list) : new gnu.trove.impl.unmodifiable.TUnmodifiableLongList(list);
  }
  
















  public static TByteList unmodifiableList(TByteList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessByteList(list) : new gnu.trove.impl.unmodifiable.TUnmodifiableByteList(list);
  }
  
















  public static TShortList unmodifiableList(TShortList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessShortList(list) : new gnu.trove.impl.unmodifiable.TUnmodifiableShortList(list);
  }
  
















  public static TCharList unmodifiableList(TCharList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessCharList(list) : new gnu.trove.impl.unmodifiable.TUnmodifiableCharList(list);
  }
  
















  public static TDoubleDoubleMap unmodifiableMap(TDoubleDoubleMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleDoubleMap(m);
  }
  













  public static TDoubleFloatMap unmodifiableMap(TDoubleFloatMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleFloatMap(m);
  }
  













  public static TDoubleIntMap unmodifiableMap(TDoubleIntMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleIntMap(m);
  }
  













  public static TDoubleLongMap unmodifiableMap(TDoubleLongMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleLongMap(m);
  }
  













  public static TDoubleByteMap unmodifiableMap(TDoubleByteMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleByteMap(m);
  }
  













  public static TDoubleShortMap unmodifiableMap(TDoubleShortMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleShortMap(m);
  }
  













  public static TDoubleCharMap unmodifiableMap(TDoubleCharMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCharMap(m);
  }
  













  public static TFloatDoubleMap unmodifiableMap(TFloatDoubleMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatDoubleMap(m);
  }
  













  public static TFloatFloatMap unmodifiableMap(TFloatFloatMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatFloatMap(m);
  }
  













  public static TFloatIntMap unmodifiableMap(TFloatIntMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatIntMap(m);
  }
  













  public static TFloatLongMap unmodifiableMap(TFloatLongMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatLongMap(m);
  }
  













  public static TFloatByteMap unmodifiableMap(TFloatByteMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatByteMap(m);
  }
  













  public static TFloatShortMap unmodifiableMap(TFloatShortMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatShortMap(m);
  }
  













  public static TFloatCharMap unmodifiableMap(TFloatCharMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatCharMap(m);
  }
  













  public static gnu.trove.map.TIntDoubleMap unmodifiableMap(gnu.trove.map.TIntDoubleMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntDoubleMap(m);
  }
  













  public static TIntFloatMap unmodifiableMap(TIntFloatMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntFloatMap(m);
  }
  













  public static TIntIntMap unmodifiableMap(TIntIntMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntIntMap(m);
  }
  













  public static gnu.trove.map.TIntLongMap unmodifiableMap(gnu.trove.map.TIntLongMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntLongMap(m);
  }
  













  public static TIntByteMap unmodifiableMap(TIntByteMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntByteMap(m);
  }
  













  public static gnu.trove.map.TIntShortMap unmodifiableMap(gnu.trove.map.TIntShortMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntShortMap(m);
  }
  













  public static TIntCharMap unmodifiableMap(TIntCharMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntCharMap(m);
  }
  













  public static gnu.trove.map.TLongDoubleMap unmodifiableMap(gnu.trove.map.TLongDoubleMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongDoubleMap(m);
  }
  













  public static TLongFloatMap unmodifiableMap(TLongFloatMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongFloatMap(m);
  }
  













  public static TLongIntMap unmodifiableMap(TLongIntMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongIntMap(m);
  }
  













  public static TLongLongMap unmodifiableMap(TLongLongMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongLongMap(m);
  }
  













  public static TLongByteMap unmodifiableMap(TLongByteMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongByteMap(m);
  }
  













  public static TLongShortMap unmodifiableMap(TLongShortMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongShortMap(m);
  }
  













  public static TLongCharMap unmodifiableMap(TLongCharMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongCharMap(m);
  }
  













  public static TByteDoubleMap unmodifiableMap(TByteDoubleMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteDoubleMap(m);
  }
  













  public static TByteFloatMap unmodifiableMap(TByteFloatMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteFloatMap(m);
  }
  













  public static TByteIntMap unmodifiableMap(TByteIntMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteIntMap(m);
  }
  













  public static gnu.trove.map.TByteLongMap unmodifiableMap(gnu.trove.map.TByteLongMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteLongMap(m);
  }
  













  public static TByteByteMap unmodifiableMap(TByteByteMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteByteMap(m);
  }
  













  public static TByteShortMap unmodifiableMap(TByteShortMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteShortMap(m);
  }
  













  public static TByteCharMap unmodifiableMap(TByteCharMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteCharMap(m);
  }
  













  public static TShortDoubleMap unmodifiableMap(TShortDoubleMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortDoubleMap(m);
  }
  













  public static gnu.trove.map.TShortFloatMap unmodifiableMap(gnu.trove.map.TShortFloatMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortFloatMap(m);
  }
  













  public static gnu.trove.map.TShortIntMap unmodifiableMap(gnu.trove.map.TShortIntMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortIntMap(m);
  }
  













  public static TShortLongMap unmodifiableMap(TShortLongMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortLongMap(m);
  }
  













  public static gnu.trove.map.TShortByteMap unmodifiableMap(gnu.trove.map.TShortByteMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortByteMap(m);
  }
  













  public static TShortShortMap unmodifiableMap(TShortShortMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortShortMap(m);
  }
  













  public static TShortCharMap unmodifiableMap(TShortCharMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortCharMap(m);
  }
  













  public static TCharDoubleMap unmodifiableMap(TCharDoubleMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharDoubleMap(m);
  }
  













  public static gnu.trove.map.TCharFloatMap unmodifiableMap(gnu.trove.map.TCharFloatMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharFloatMap(m);
  }
  













  public static gnu.trove.map.TCharIntMap unmodifiableMap(gnu.trove.map.TCharIntMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharIntMap(m);
  }
  













  public static TCharLongMap unmodifiableMap(TCharLongMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharLongMap(m);
  }
  













  public static gnu.trove.map.TCharByteMap unmodifiableMap(gnu.trove.map.TCharByteMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharByteMap(m);
  }
  













  public static TCharShortMap unmodifiableMap(TCharShortMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharShortMap(m);
  }
  













  public static TCharCharMap unmodifiableMap(TCharCharMap m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharCharMap(m);
  }
  














  public static <V> gnu.trove.map.TDoubleObjectMap<V> unmodifiableMap(gnu.trove.map.TDoubleObjectMap<V> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableDoubleObjectMap(m);
  }
  













  public static <V> TFloatObjectMap<V> unmodifiableMap(TFloatObjectMap<V> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableFloatObjectMap(m);
  }
  













  public static <V> TIntObjectMap<V> unmodifiableMap(TIntObjectMap<V> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableIntObjectMap(m);
  }
  













  public static <V> TLongObjectMap<V> unmodifiableMap(TLongObjectMap<V> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableLongObjectMap(m);
  }
  













  public static <V> TByteObjectMap<V> unmodifiableMap(TByteObjectMap<V> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableByteObjectMap(m);
  }
  













  public static <V> gnu.trove.map.TShortObjectMap<V> unmodifiableMap(gnu.trove.map.TShortObjectMap<V> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableShortObjectMap(m);
  }
  













  public static <V> gnu.trove.map.TCharObjectMap<V> unmodifiableMap(gnu.trove.map.TCharObjectMap<V> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableCharObjectMap(m);
  }
  














  public static <K> TObjectDoubleMap<K> unmodifiableMap(TObjectDoubleMap<K> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableObjectDoubleMap(m);
  }
  













  public static <K> gnu.trove.map.TObjectFloatMap<K> unmodifiableMap(gnu.trove.map.TObjectFloatMap<K> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableObjectFloatMap(m);
  }
  













  public static <K> gnu.trove.map.TObjectIntMap<K> unmodifiableMap(gnu.trove.map.TObjectIntMap<K> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableObjectIntMap(m);
  }
  













  public static <K> TObjectLongMap<K> unmodifiableMap(TObjectLongMap<K> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableObjectLongMap(m);
  }
  













  public static <K> TObjectByteMap<K> unmodifiableMap(TObjectByteMap<K> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableObjectByteMap(m);
  }
  













  public static <K> TObjectShortMap<K> unmodifiableMap(TObjectShortMap<K> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableObjectShortMap(m);
  }
  













  public static <K> TObjectCharMap<K> unmodifiableMap(TObjectCharMap<K> m)
  {
    return new gnu.trove.impl.unmodifiable.TUnmodifiableObjectCharMap(m);
  }
  



































  public static TDoubleCollection synchronizedCollection(TDoubleCollection c)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleCollection(c);
  }
  
  static TDoubleCollection synchronizedCollection(TDoubleCollection c, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedDoubleCollection(c, mutex);
  }
  






























  public static TFloatCollection synchronizedCollection(TFloatCollection c)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatCollection(c);
  }
  
  static TFloatCollection synchronizedCollection(TFloatCollection c, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedFloatCollection(c, mutex);
  }
  






























  public static TIntCollection synchronizedCollection(TIntCollection c)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntCollection(c);
  }
  
  static TIntCollection synchronizedCollection(TIntCollection c, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedIntCollection(c, mutex);
  }
  






























  public static TLongCollection synchronizedCollection(TLongCollection c)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongCollection(c);
  }
  
  static TLongCollection synchronizedCollection(TLongCollection c, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedLongCollection(c, mutex);
  }
  






























  public static TByteCollection synchronizedCollection(TByteCollection c)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteCollection(c);
  }
  
  static TByteCollection synchronizedCollection(TByteCollection c, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedByteCollection(c, mutex);
  }
  






























  public static TShortCollection synchronizedCollection(TShortCollection c)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortCollection(c);
  }
  
  static TShortCollection synchronizedCollection(TShortCollection c, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedShortCollection(c, mutex);
  }
  






























  public static TCharCollection synchronizedCollection(TCharCollection c)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharCollection(c);
  }
  
  static TCharCollection synchronizedCollection(TCharCollection c, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedCharCollection(c, mutex);
  }
  

























  public static TDoubleSet synchronizedSet(TDoubleSet s)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleSet(s);
  }
  
  static TDoubleSet synchronizedSet(TDoubleSet s, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedDoubleSet(s, mutex);
  }
  
























  public static TFloatSet synchronizedSet(TFloatSet s)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatSet(s);
  }
  
  static TFloatSet synchronizedSet(TFloatSet s, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedFloatSet(s, mutex);
  }
  
























  public static TIntSet synchronizedSet(TIntSet s)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntSet(s);
  }
  
  static TIntSet synchronizedSet(TIntSet s, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedIntSet(s, mutex);
  }
  
























  public static TLongSet synchronizedSet(TLongSet s)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongSet(s);
  }
  
  static TLongSet synchronizedSet(TLongSet s, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedLongSet(s, mutex);
  }
  
























  public static TByteSet synchronizedSet(TByteSet s)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteSet(s);
  }
  
  static TByteSet synchronizedSet(TByteSet s, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedByteSet(s, mutex);
  }
  
























  public static TShortSet synchronizedSet(TShortSet s)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortSet(s);
  }
  
  static TShortSet synchronizedSet(TShortSet s, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedShortSet(s, mutex);
  }
  
























  public static TCharSet synchronizedSet(TCharSet s)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharSet(s);
  }
  
  static TCharSet synchronizedSet(TCharSet s, Object mutex) {
    return new gnu.trove.impl.sync.TSynchronizedCharSet(s, mutex);
  }
  

























  public static TDoubleList synchronizedList(TDoubleList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessDoubleList(list) : new gnu.trove.impl.sync.TSynchronizedDoubleList(list);
  }
  

  static TDoubleList synchronizedList(TDoubleList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessDoubleList(list, mutex) : new gnu.trove.impl.sync.TSynchronizedDoubleList(list, mutex);
  }
  


























  public static TFloatList synchronizedList(TFloatList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessFloatList(list) : new gnu.trove.impl.sync.TSynchronizedFloatList(list);
  }
  

  static TFloatList synchronizedList(TFloatList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessFloatList(list, mutex) : new gnu.trove.impl.sync.TSynchronizedFloatList(list, mutex);
  }
  


























  public static TIntList synchronizedList(TIntList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessIntList(list) : new gnu.trove.impl.sync.TSynchronizedIntList(list);
  }
  

  static TIntList synchronizedList(TIntList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessIntList(list, mutex) : new gnu.trove.impl.sync.TSynchronizedIntList(list, mutex);
  }
  


























  public static TLongList synchronizedList(TLongList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessLongList(list) : new gnu.trove.impl.sync.TSynchronizedLongList(list);
  }
  

  static TLongList synchronizedList(TLongList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessLongList(list, mutex) : new gnu.trove.impl.sync.TSynchronizedLongList(list, mutex);
  }
  


























  public static TByteList synchronizedList(TByteList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessByteList(list) : new gnu.trove.impl.sync.TSynchronizedByteList(list);
  }
  

  static TByteList synchronizedList(TByteList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessByteList(list, mutex) : new gnu.trove.impl.sync.TSynchronizedByteList(list, mutex);
  }
  


























  public static TShortList synchronizedList(TShortList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessShortList(list) : new gnu.trove.impl.sync.TSynchronizedShortList(list);
  }
  

  static TShortList synchronizedList(TShortList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessShortList(list, mutex) : new gnu.trove.impl.sync.TSynchronizedShortList(list, mutex);
  }
  


























  public static TCharList synchronizedList(TCharList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessCharList(list) : new gnu.trove.impl.sync.TSynchronizedCharList(list);
  }
  

  static TCharList synchronizedList(TCharList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.sync.TSynchronizedRandomAccessCharList(list, mutex) : new gnu.trove.impl.sync.TSynchronizedCharList(list, mutex);
  }
  





























  public static TDoubleDoubleMap synchronizedMap(TDoubleDoubleMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleDoubleMap(m);
  }
  


























  public static TDoubleFloatMap synchronizedMap(TDoubleFloatMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleFloatMap(m);
  }
  


























  public static TDoubleIntMap synchronizedMap(TDoubleIntMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleIntMap(m);
  }
  


























  public static TDoubleLongMap synchronizedMap(TDoubleLongMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleLongMap(m);
  }
  


























  public static TDoubleByteMap synchronizedMap(TDoubleByteMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleByteMap(m);
  }
  


























  public static TDoubleShortMap synchronizedMap(TDoubleShortMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleShortMap(m);
  }
  


























  public static TDoubleCharMap synchronizedMap(TDoubleCharMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleCharMap(m);
  }
  


























  public static TFloatDoubleMap synchronizedMap(TFloatDoubleMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatDoubleMap(m);
  }
  


























  public static TFloatFloatMap synchronizedMap(TFloatFloatMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatFloatMap(m);
  }
  


























  public static TFloatIntMap synchronizedMap(TFloatIntMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatIntMap(m);
  }
  


























  public static TFloatLongMap synchronizedMap(TFloatLongMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatLongMap(m);
  }
  


























  public static TFloatByteMap synchronizedMap(TFloatByteMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatByteMap(m);
  }
  


























  public static TFloatShortMap synchronizedMap(TFloatShortMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatShortMap(m);
  }
  


























  public static TFloatCharMap synchronizedMap(TFloatCharMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatCharMap(m);
  }
  


























  public static gnu.trove.map.TIntDoubleMap synchronizedMap(gnu.trove.map.TIntDoubleMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntDoubleMap(m);
  }
  


























  public static TIntFloatMap synchronizedMap(TIntFloatMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntFloatMap(m);
  }
  


























  public static TIntIntMap synchronizedMap(TIntIntMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntIntMap(m);
  }
  


























  public static gnu.trove.map.TIntLongMap synchronizedMap(gnu.trove.map.TIntLongMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntLongMap(m);
  }
  


























  public static TIntByteMap synchronizedMap(TIntByteMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntByteMap(m);
  }
  


























  public static gnu.trove.map.TIntShortMap synchronizedMap(gnu.trove.map.TIntShortMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntShortMap(m);
  }
  


























  public static TIntCharMap synchronizedMap(TIntCharMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntCharMap(m);
  }
  


























  public static gnu.trove.map.TLongDoubleMap synchronizedMap(gnu.trove.map.TLongDoubleMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongDoubleMap(m);
  }
  


























  public static TLongFloatMap synchronizedMap(TLongFloatMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongFloatMap(m);
  }
  


























  public static TLongIntMap synchronizedMap(TLongIntMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongIntMap(m);
  }
  


























  public static TLongLongMap synchronizedMap(TLongLongMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongLongMap(m);
  }
  


























  public static TLongByteMap synchronizedMap(TLongByteMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongByteMap(m);
  }
  


























  public static TLongShortMap synchronizedMap(TLongShortMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongShortMap(m);
  }
  


























  public static TLongCharMap synchronizedMap(TLongCharMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongCharMap(m);
  }
  


























  public static TByteDoubleMap synchronizedMap(TByteDoubleMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteDoubleMap(m);
  }
  


























  public static TByteFloatMap synchronizedMap(TByteFloatMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteFloatMap(m);
  }
  


























  public static TByteIntMap synchronizedMap(TByteIntMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteIntMap(m);
  }
  


























  public static gnu.trove.map.TByteLongMap synchronizedMap(gnu.trove.map.TByteLongMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteLongMap(m);
  }
  


























  public static TByteByteMap synchronizedMap(TByteByteMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteByteMap(m);
  }
  


























  public static TByteShortMap synchronizedMap(TByteShortMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteShortMap(m);
  }
  


























  public static TByteCharMap synchronizedMap(TByteCharMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteCharMap(m);
  }
  


























  public static TShortDoubleMap synchronizedMap(TShortDoubleMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortDoubleMap(m);
  }
  


























  public static gnu.trove.map.TShortFloatMap synchronizedMap(gnu.trove.map.TShortFloatMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortFloatMap(m);
  }
  


























  public static gnu.trove.map.TShortIntMap synchronizedMap(gnu.trove.map.TShortIntMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortIntMap(m);
  }
  


























  public static TShortLongMap synchronizedMap(TShortLongMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortLongMap(m);
  }
  


























  public static gnu.trove.map.TShortByteMap synchronizedMap(gnu.trove.map.TShortByteMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortByteMap(m);
  }
  


























  public static TShortShortMap synchronizedMap(TShortShortMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortShortMap(m);
  }
  


























  public static TShortCharMap synchronizedMap(TShortCharMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortCharMap(m);
  }
  


























  public static TCharDoubleMap synchronizedMap(TCharDoubleMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharDoubleMap(m);
  }
  


























  public static gnu.trove.map.TCharFloatMap synchronizedMap(gnu.trove.map.TCharFloatMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharFloatMap(m);
  }
  


























  public static gnu.trove.map.TCharIntMap synchronizedMap(gnu.trove.map.TCharIntMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharIntMap(m);
  }
  


























  public static TCharLongMap synchronizedMap(TCharLongMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharLongMap(m);
  }
  


























  public static gnu.trove.map.TCharByteMap synchronizedMap(gnu.trove.map.TCharByteMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharByteMap(m);
  }
  


























  public static TCharShortMap synchronizedMap(TCharShortMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharShortMap(m);
  }
  


























  public static TCharCharMap synchronizedMap(TCharCharMap m)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharCharMap(m);
  }
  



























  public static <V> gnu.trove.map.TDoubleObjectMap<V> synchronizedMap(gnu.trove.map.TDoubleObjectMap<V> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedDoubleObjectMap(m);
  }
  


























  public static <V> TFloatObjectMap<V> synchronizedMap(TFloatObjectMap<V> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedFloatObjectMap(m);
  }
  


























  public static <V> TIntObjectMap<V> synchronizedMap(TIntObjectMap<V> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedIntObjectMap(m);
  }
  


























  public static <V> TLongObjectMap<V> synchronizedMap(TLongObjectMap<V> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedLongObjectMap(m);
  }
  


























  public static <V> TByteObjectMap<V> synchronizedMap(TByteObjectMap<V> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedByteObjectMap(m);
  }
  


























  public static <V> gnu.trove.map.TShortObjectMap<V> synchronizedMap(gnu.trove.map.TShortObjectMap<V> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedShortObjectMap(m);
  }
  


























  public static <V> gnu.trove.map.TCharObjectMap<V> synchronizedMap(gnu.trove.map.TCharObjectMap<V> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedCharObjectMap(m);
  }
  



























  public static <K> TObjectDoubleMap<K> synchronizedMap(TObjectDoubleMap<K> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedObjectDoubleMap(m);
  }
  


























  public static <K> gnu.trove.map.TObjectFloatMap<K> synchronizedMap(gnu.trove.map.TObjectFloatMap<K> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedObjectFloatMap(m);
  }
  


























  public static <K> gnu.trove.map.TObjectIntMap<K> synchronizedMap(gnu.trove.map.TObjectIntMap<K> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedObjectIntMap(m);
  }
  


























  public static <K> TObjectLongMap<K> synchronizedMap(TObjectLongMap<K> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedObjectLongMap(m);
  }
  


























  public static <K> TObjectByteMap<K> synchronizedMap(TObjectByteMap<K> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedObjectByteMap(m);
  }
  


























  public static <K> TObjectShortMap<K> synchronizedMap(TObjectShortMap<K> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedObjectShortMap(m);
  }
  


























  public static <K> TObjectCharMap<K> synchronizedMap(TObjectCharMap<K> m)
  {
    return new gnu.trove.impl.sync.TSynchronizedObjectCharMap(m);
  }
}
