package com.iwebpp.crypto;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.concurrent.atomic.AtomicLong;








public final class TweetNaclFast
{
  private static final String TAG = "TweetNaclFast";
  
  public static final class Box
  {
    private static final String TAG = "Box";
    private AtomicLong nonce;
    private byte[] theirPublicKey;
    private byte[] mySecretKey;
    private byte[] sharedKey;
    public static final int publicKeyLength = 32;
    public static final int secretKeyLength = 32;
    public static final int sharedKeyLength = 32;
    public static final int nonceLength = 24;
    public static final int zerobytesLength = 32;
    public static final int boxzerobytesLength = 16;
    public static final int overheadLength = 16;
    
    public Box(byte[] theirPublicKey, byte[] mySecretKey)
    {
      this(theirPublicKey, mySecretKey, 68L);
    }
    
    public Box(byte[] theirPublicKey, byte[] mySecretKey, long nonce) {
      this.theirPublicKey = theirPublicKey;
      this.mySecretKey = mySecretKey;
      
      this.nonce = new AtomicLong(nonce);
      

      before();
    }
    
    public void setNonce(long nonce) {
      this.nonce.set(nonce);
    }
    
    public long getNonce() { return nonce.get(); }
    
    public long incrNonce() {
      return nonce.incrementAndGet();
    }
    
    private byte[] generateNonce() {
      long nonce = this.nonce.get();
      
      byte[] n = new byte[24];
      for (int i = 0; i < 24; i += 8) {
        n[(i + 0)] = ((byte)(int)(nonce >>> 0));
        n[(i + 1)] = ((byte)(int)(nonce >>> 8));
        n[(i + 2)] = ((byte)(int)(nonce >>> 16));
        n[(i + 3)] = ((byte)(int)(nonce >>> 24));
        n[(i + 4)] = ((byte)(int)(nonce >>> 32));
        n[(i + 5)] = ((byte)(int)(nonce >>> 40));
        n[(i + 6)] = ((byte)(int)(nonce >>> 48));
        n[(i + 7)] = ((byte)(int)(nonce >>> 56));
      }
      
      return n;
    }
    








    public byte[] box(byte[] message)
    {
      if (message == null) return null;
      return box(message, 0, message.length);
    }
    
    public byte[] box(byte[] message, int moff) {
      if ((message == null) || (message.length <= moff)) return null;
      return box(message, moff, message.length - moff);
    }
    
    public byte[] box(byte[] message, int moff, int mlen) {
      if ((message == null) || (message.length < moff + mlen)) { return null;
      }
      
      if (sharedKey == null) { before();
      }
      return after(message, moff, mlen);
    }
    










    public byte[] box(byte[] message, byte[] theNonce)
    {
      if (message == null) return null;
      return box(message, 0, message.length, theNonce);
    }
    
    public byte[] box(byte[] message, int moff, byte[] theNonce) {
      if ((message == null) || (message.length <= moff)) return null;
      return box(message, moff, message.length - moff, theNonce);
    }
    
    public byte[] box(byte[] message, int moff, int mlen, byte[] theNonce) {
      if ((message == null) || (message.length < moff + mlen) || (theNonce == null) || (theNonce.length != 24))
      {
        return null;
      }
      
      if (sharedKey == null) { before();
      }
      return after(message, moff, mlen, theNonce);
    }
    






    public byte[] open(byte[] box)
    {
      if (box == null) { return null;
      }
      
      if (sharedKey == null) { before();
      }
      return open_after(box, 0, box.length);
    }
    
    public byte[] open(byte[] box, int boxoff) { if ((box == null) || (box.length <= boxoff)) { return null;
      }
      
      if (sharedKey == null) { before();
      }
      return open_after(box, boxoff, box.length - boxoff);
    }
    
    public byte[] open(byte[] box, int boxoff, int boxlen) { if ((box == null) || (box.length < boxoff + boxlen)) { return null;
      }
      
      if (sharedKey == null) { before();
      }
      return open_after(box, boxoff, boxlen);
    }
    







    public byte[] open(byte[] box, byte[] theNonce)
    {
      if ((box == null) || (theNonce == null) || (theNonce.length != 24))
      {
        return null;
      }
      
      if (sharedKey == null) { before();
      }
      return open_after(box, 0, box.length, theNonce);
    }
    
    public byte[] open(byte[] box, int boxoff, byte[] theNonce) { if ((box == null) || (box.length <= boxoff) || (theNonce == null) || (theNonce.length != 24))
      {
        return null;
      }
      
      if (sharedKey == null) { before();
      }
      return open_after(box, boxoff, box.length - boxoff, theNonce);
    }
    
    public byte[] open(byte[] box, int boxoff, int boxlen, byte[] theNonce) { if ((box == null) || (box.length < boxoff + boxlen) || (theNonce == null) || (theNonce.length != 24))
      {
        return null;
      }
      
      if (sharedKey == null) { before();
      }
      return open_after(box, boxoff, boxlen, theNonce);
    }
    





    public byte[] before()
    {
      if (sharedKey == null) {
        sharedKey = new byte[32];
        TweetNaclFast.crypto_box_beforenm(sharedKey, theirPublicKey, mySecretKey);
      }
      
      return sharedKey;
    }
    



    public byte[] after(byte[] message, int moff, int mlen)
    {
      return after(message, moff, mlen, generateNonce());
    }
    





    public byte[] after(byte[] message, int moff, int mlen, byte[] theNonce)
    {
      if ((message == null) || (message.length < moff + mlen) || (theNonce == null) || (theNonce.length != 24))
      {
        return null;
      }
      
      byte[] m = new byte[mlen + 32];
      

      byte[] c = new byte[m.length];
      
      for (int i = 0; i < mlen; i++) {
        m[(i + 32)] = message[(i + moff)];
      }
      if (0 != TweetNaclFast.crypto_box_afternm(c, m, m.length, theNonce, sharedKey)) {
        return null;
      }
      

      byte[] ret = new byte[c.length - 16];
      
      for (int i = 0; i < ret.length; i++) {
        ret[i] = c[(i + 16)];
      }
      return ret;
    }
    




    public byte[] open_after(byte[] box, int boxoff, int boxlen)
    {
      return open_after(box, boxoff, boxlen, generateNonce());
    }
    
    public byte[] open_after(byte[] box, int boxoff, int boxlen, byte[] theNonce)
    {
      if ((box == null) || (box.length < boxoff + boxlen) || (boxlen < 16)) {
        return null;
      }
      
      byte[] c = new byte[boxlen + 16];
      

      byte[] m = new byte[c.length];
      
      for (int i = 0; i < boxlen; i++) {
        c[(i + 16)] = box[(i + boxoff)];
      }
      if (TweetNaclFast.crypto_box_open_afternm(m, c, c.length, theNonce, sharedKey) != 0) {
        return null;
      }
      

      byte[] ret = new byte[m.length - 32];
      
      for (int i = 0; i < ret.length; i++) {
        ret[i] = m[(i + 32)];
      }
      return ret;
    }
    













    public static class KeyPair
    {
      private byte[] publicKey;
      












      private byte[] secretKey;
      













      public KeyPair()
      {
        publicKey = new byte[32];
        secretKey = new byte[32];
      }
      
      public byte[] getPublicKey() {
        return publicKey;
      }
      
      public byte[] getSecretKey() {
        return secretKey;
      }
    }
    




    public static KeyPair keyPair()
    {
      KeyPair kp = new KeyPair();
      
      TweetNaclFast.crypto_box_keypair(kp.getPublicKey(), kp.getSecretKey());
      return kp;
    }
    
    public static KeyPair keyPair_fromSecretKey(byte[] secretKey) {
      KeyPair kp = new KeyPair();
      byte[] sk = kp.getSecretKey();
      byte[] pk = kp.getPublicKey();
      

      for (int i = 0; i < sk.length; i++) {
        sk[i] = secretKey[i];
      }
      TweetNaclFast.crypto_scalarmult_base(pk, sk);
      return kp;
    }
  }
  

  public static final class SecretBox
  {
    private static final String TAG = "SecretBox";
    private AtomicLong nonce;
    private byte[] key;
    public static final int keyLength = 32;
    public static final int nonceLength = 24;
    public static final int overheadLength = 16;
    public static final int zerobytesLength = 32;
    public static final int boxzerobytesLength = 16;
    
    public SecretBox(byte[] key)
    {
      this(key, 68L);
    }
    
    public SecretBox(byte[] key, long nonce) {
      this.key = key;
      
      this.nonce = new AtomicLong(nonce);
    }
    
    public void setNonce(long nonce) {
      this.nonce.set(nonce);
    }
    
    public long getNonce() { return nonce.get(); }
    
    public long incrNonce() {
      return nonce.incrementAndGet();
    }
    
    private byte[] generateNonce() {
      long nonce = this.nonce.get();
      
      byte[] n = new byte[24];
      for (int i = 0; i < 24; i += 8) {
        n[(i + 0)] = ((byte)(int)(nonce >>> 0));
        n[(i + 1)] = ((byte)(int)(nonce >>> 8));
        n[(i + 2)] = ((byte)(int)(nonce >>> 16));
        n[(i + 3)] = ((byte)(int)(nonce >>> 24));
        n[(i + 4)] = ((byte)(int)(nonce >>> 32));
        n[(i + 5)] = ((byte)(int)(nonce >>> 40));
        n[(i + 6)] = ((byte)(int)(nonce >>> 48));
        n[(i + 7)] = ((byte)(int)(nonce >>> 56));
      }
      
      return n;
    }
    







    public byte[] box(byte[] message)
    {
      if (message == null) return null;
      return box(message, 0, message.length);
    }
    
    public byte[] box(byte[] message, int moff) {
      if ((message == null) || (message.length <= moff)) return null;
      return box(message, moff, message.length - moff);
    }
    
    public byte[] box(byte[] message, int moff, int mlen)
    {
      if ((message == null) || (message.length < moff + mlen))
        return null;
      return box(message, moff, message.length - moff, generateNonce());
    }
    
    public byte[] box(byte[] message, byte[] theNonce) {
      if (message == null) return null;
      return box(message, 0, message.length, theNonce);
    }
    
    public byte[] box(byte[] message, int moff, byte[] theNonce) {
      if ((message == null) || (message.length <= moff)) return null;
      return box(message, moff, message.length - moff, theNonce);
    }
    
    public byte[] box(byte[] message, int moff, int mlen, byte[] theNonce)
    {
      if ((message == null) || (message.length < moff + mlen) || (theNonce == null) || (theNonce.length != 24))
      {
        return null;
      }
      
      byte[] m = new byte[mlen + 32];
      

      byte[] c = new byte[m.length];
      
      for (int i = 0; i < mlen; i++) {
        m[(i + 32)] = message[(i + moff)];
      }
      if (0 != TweetNaclFast.crypto_secretbox(c, m, m.length, theNonce, key)) {
        return null;
      }
      


      byte[] ret = new byte[c.length - 16];
      
      for (int i = 0; i < ret.length; i++) {
        ret[i] = c[(i + 16)];
      }
      return ret;
    }
    






    public byte[] open(byte[] box)
    {
      if (box == null) return null;
      return open(box, 0, box.length);
    }
    
    public byte[] open(byte[] box, int boxoff) {
      if ((box == null) || (box.length <= boxoff)) return null;
      return open(box, boxoff, box.length - boxoff);
    }
    
    public byte[] open(byte[] box, int boxoff, int boxlen)
    {
      if ((box == null) || (box.length < boxoff + boxlen) || (boxlen < 16))
        return null;
      return open(box, boxoff, box.length - boxoff, generateNonce());
    }
    
    public byte[] open(byte[] box, byte[] theNonce) {
      if (box == null) return null;
      return open(box, 0, box.length, theNonce);
    }
    
    public byte[] open(byte[] box, int boxoff, byte[] theNonce) {
      if ((box == null) || (box.length <= boxoff)) return null;
      return open(box, boxoff, box.length - boxoff, theNonce);
    }
    
    public byte[] open(byte[] box, int boxoff, int boxlen, byte[] theNonce)
    {
      if ((box == null) || (box.length < boxoff + boxlen) || (boxlen < 16) || (theNonce == null) || (theNonce.length != 24))
      {
        return null;
      }
      
      byte[] c = new byte[boxlen + 16];
      

      byte[] m = new byte[c.length];
      
      for (int i = 0; i < boxlen; i++) {
        c[(i + 16)] = box[(i + boxoff)];
      }
      if (0 != TweetNaclFast.crypto_secretbox_open(m, c, c.length, theNonce, key)) {
        return null;
      }
      

      byte[] ret = new byte[m.length - 32];
      
      for (int i = 0; i < ret.length; i++) {
        ret[i] = m[(i + 32)];
      }
      return ret;
    }
  }
  







  public static final class ScalarMult
  {
    private static final String TAG = "ScalarMult";
    






    public static final int scalarLength = 32;
    






    public static final int groupElementLength = 32;
    







    public ScalarMult() {}
    






    public static byte[] scalseMult(byte[] n, byte[] p)
    {
      if ((n.length != 32) || (p.length != 32)) {
        return null;
      }
      byte[] q = new byte[32];
      
      TweetNaclFast.crypto_scalarmult(q, n, p);
      
      return q;
    }
    




    public static byte[] scalseMult_base(byte[] n)
    {
      if (n.length != 32) {
        return null;
      }
      byte[] q = new byte[32];
      
      TweetNaclFast.crypto_scalarmult_base(q, n);
      
      return q;
    }
  }
  





  public static final class Hash
  {
    private static final String TAG = "Hash";
    




    public static final int hashLength = 64;
    




    public Hash() {}
    




    public static byte[] sha512(byte[] message)
    {
      if ((message == null) || (message.length <= 0)) {
        return null;
      }
      byte[] out = new byte[64];
      
      TweetNaclFast.crypto_hash(out, message);
      
      return out;
    }
    
    public static byte[] sha512(String message) throws UnsupportedEncodingException { return sha512(message.getBytes("utf-8")); }
  }
  


  public static final class Signature
  {
    private static final String TAG = "Signature";
    
    private byte[] theirPublicKey;
    
    private byte[] mySecretKey;
    
    public static final int publicKeyLength = 32;
    
    public static final int secretKeyLength = 64;
    
    public static final int seedLength = 32;
    
    public static final int signatureLength = 64;
    

    public Signature(byte[] theirPublicKey, byte[] mySecretKey)
    {
      this.theirPublicKey = theirPublicKey;
      this.mySecretKey = mySecretKey;
    }
    



    public byte[] sign(byte[] message)
    {
      if (message == null) { return null;
      }
      return sign(message, 0, message.length);
    }
    
    public byte[] sign(byte[] message, int moff) { if ((message == null) || (message.length <= moff)) { return null;
      }
      return sign(message, moff, message.length - moff);
    }
    
    public byte[] sign(byte[] message, int moff, int mlen) {
      if ((message == null) || (message.length < moff + mlen)) {
        return null;
      }
      
      byte[] sm = new byte[mlen + 64];
      
      TweetNaclFast.crypto_sign(sm, -1L, message, moff, mlen, mySecretKey);
      
      return sm;
    }
    




    public byte[] open(byte[] signedMessage)
    {
      if (signedMessage == null) { return null;
      }
      return open(signedMessage, 0, signedMessage.length);
    }
    
    public byte[] open(byte[] signedMessage, int smoff) { if ((signedMessage == null) || (signedMessage.length <= smoff)) { return null;
      }
      return open(signedMessage, smoff, signedMessage.length - smoff);
    }
    
    public byte[] open(byte[] signedMessage, int smoff, int smlen) {
      if ((signedMessage == null) || (signedMessage.length < smoff + smlen) || (smlen < 64)) {
        return null;
      }
      
      byte[] tmp = new byte[smlen];
      
      if (0 != TweetNaclFast.crypto_sign_open(tmp, -1L, signedMessage, smoff, smlen, theirPublicKey)) {
        return null;
      }
      
      byte[] msg = new byte[smlen - 64];
      for (int i = 0; i < msg.length; i++) {
        msg[i] = signedMessage[(smoff + i + 64)];
      }
      return msg;
    }
    



    public byte[] detached(byte[] message)
    {
      byte[] signedMsg = sign(message);
      byte[] sig = new byte[64];
      for (int i = 0; i < sig.length; i++)
        sig[i] = signedMsg[i];
      return sig;
    }
    




    public boolean detached_verify(byte[] message, byte[] signature)
    {
      if (signature.length != 64)
        return false;
      if (theirPublicKey.length != 32)
        return false;
      byte[] sm = new byte[64 + message.length];
      byte[] m = new byte[64 + message.length];
      for (int i = 0; i < 64; i++)
        sm[i] = signature[i];
      for (int i = 0; i < message.length; i++)
        sm[(i + 64)] = message[i];
      return TweetNaclFast.crypto_sign_open(m, -1L, sm, 0, sm.length, theirPublicKey) >= 0;
    }
    

    public static class KeyPair
    {
      private byte[] publicKey;
      
      private byte[] secretKey;
      

      public KeyPair()
      {
        publicKey = new byte[32];
        secretKey = new byte[64];
      }
      
      public byte[] getPublicKey() {
        return publicKey;
      }
      
      public byte[] getSecretKey() {
        return secretKey;
      }
    }
    



    public static KeyPair keyPair()
    {
      KeyPair kp = new KeyPair();
      
      TweetNaclFast.crypto_sign_keypair(kp.getPublicKey(), kp.getSecretKey(), false);
      return kp;
    }
    
    public static KeyPair keyPair_fromSecretKey(byte[] secretKey) { KeyPair kp = new KeyPair();
      byte[] pk = kp.getPublicKey();
      byte[] sk = kp.getSecretKey();
      

      for (int i = 0; i < kp.getSecretKey().length; i++) {
        sk[i] = secretKey[i];
      }
      
      for (int i = 0; i < kp.getPublicKey().length; i++) {
        pk[i] = secretKey[(32 + i)];
      }
      return kp;
    }
    
    public static KeyPair keyPair_fromSeed(byte[] seed) {
      KeyPair kp = new KeyPair();
      byte[] pk = kp.getPublicKey();
      byte[] sk = kp.getSecretKey();
      

      for (int i = 0; i < 32; i++) {
        sk[i] = seed[i];
      }
      
      TweetNaclFast.crypto_sign_keypair(pk, sk, true);
      
      return kp;
    }
  }
  































  private static final byte[] _0 = new byte[16];
  private static final byte[] _9 = new byte[32];
  private static final long[] gf0;
  private static final long[] gf1;
  private static final long[] _121665;
  
  static { _9[0] = 9;
    

    gf0 = new long[16];
    gf1 = new long[16];
    _121665 = new long[16];
    



    gf1[0] = 1L;
    

    _121665[0] = 56129L;_121665[1] = 1L;
  }
  
  private static final long[] D = { 30883L, 4953L, 19914L, 30187L, 55467L, 16705L, 2637L, 112L, 59544L, 30585L, 16505L, 36039L, 65139L, 11119L, 27886L, 20995L };
  




  private static final long[] D2 = { 61785L, 9906L, 39828L, 60374L, 45398L, 33411L, 5274L, 224L, 53552L, 61171L, 33010L, 6542L, 64743L, 22239L, 55772L, 9222L };
  




  private static final long[] X = { 54554L, 36645L, 11616L, 51542L, 42930L, 38181L, 51040L, 26924L, 56412L, 64982L, 57905L, 49316L, 21502L, 52590L, 14035L, 8553L };
  




  private static final long[] Y = { 26200L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L, 26214L };
  




  private static final long[] I = { 41136L, 18958L, 6951L, 50414L, 58488L, 44335L, 6150L, 12099L, 55207L, 15867L, 153L, 11085L, 57099L, 20417L, 9344L, 11139L };
  








  private static void ts64(byte[] x, int xoff, long u)
  {
    x[(7 + xoff)] = ((byte)(int)(u & 0xFF));u >>>= 8;
    x[(6 + xoff)] = ((byte)(int)(u & 0xFF));u >>>= 8;
    x[(5 + xoff)] = ((byte)(int)(u & 0xFF));u >>>= 8;
    x[(4 + xoff)] = ((byte)(int)(u & 0xFF));u >>>= 8;
    x[(3 + xoff)] = ((byte)(int)(u & 0xFF));u >>>= 8;
    x[(2 + xoff)] = ((byte)(int)(u & 0xFF));u >>>= 8;
    x[(1 + xoff)] = ((byte)(int)(u & 0xFF));u >>>= 8;
    x[(0 + xoff)] = ((byte)(int)(u & 0xFF));
  }
  



  private static int vn(byte[] x, int xoff, byte[] y, int yoff, int n)
  {
    int d = 0;
    for (int i = 0; i < n; i++) d |= (x[(i + xoff)] ^ y[(i + yoff)]) & 0xFF;
    return (0x1 & d - 1 >>> 8) - 1;
  }
  


  private static int crypto_verify_16(byte[] x, int xoff, byte[] y, int yoff)
  {
    return vn(x, xoff, y, yoff, 16);
  }
  
  public static int crypto_verify_16(byte[] x, byte[] y) {
    return crypto_verify_16(x, 0, y, 0);
  }
  


  private static int crypto_verify_32(byte[] x, int xoff, byte[] y, int yoff)
  {
    return vn(x, xoff, y, yoff, 32);
  }
  
  public static int crypto_verify_32(byte[] x, byte[] y) {
    return crypto_verify_32(x, 0, y, 0);
  }
  
  private static void core_salsa20(byte[] o, byte[] p, byte[] k, byte[] c) {
    int j0 = c[0] & 0xFF | (c[1] & 0xFF) << 8 | (c[2] & 0xFF) << 16 | (c[3] & 0xFF) << 24;
    int j1 = k[0] & 0xFF | (k[1] & 0xFF) << 8 | (k[2] & 0xFF) << 16 | (k[3] & 0xFF) << 24;
    int j2 = k[4] & 0xFF | (k[5] & 0xFF) << 8 | (k[6] & 0xFF) << 16 | (k[7] & 0xFF) << 24;
    int j3 = k[8] & 0xFF | (k[9] & 0xFF) << 8 | (k[10] & 0xFF) << 16 | (k[11] & 0xFF) << 24;
    int j4 = k[12] & 0xFF | (k[13] & 0xFF) << 8 | (k[14] & 0xFF) << 16 | (k[15] & 0xFF) << 24;
    int j5 = c[4] & 0xFF | (c[5] & 0xFF) << 8 | (c[6] & 0xFF) << 16 | (c[7] & 0xFF) << 24;
    int j6 = p[0] & 0xFF | (p[1] & 0xFF) << 8 | (p[2] & 0xFF) << 16 | (p[3] & 0xFF) << 24;
    int j7 = p[4] & 0xFF | (p[5] & 0xFF) << 8 | (p[6] & 0xFF) << 16 | (p[7] & 0xFF) << 24;
    int j8 = p[8] & 0xFF | (p[9] & 0xFF) << 8 | (p[10] & 0xFF) << 16 | (p[11] & 0xFF) << 24;
    int j9 = p[12] & 0xFF | (p[13] & 0xFF) << 8 | (p[14] & 0xFF) << 16 | (p[15] & 0xFF) << 24;
    int j10 = c[8] & 0xFF | (c[9] & 0xFF) << 8 | (c[10] & 0xFF) << 16 | (c[11] & 0xFF) << 24;
    int j11 = k[16] & 0xFF | (k[17] & 0xFF) << 8 | (k[18] & 0xFF) << 16 | (k[19] & 0xFF) << 24;
    int j12 = k[20] & 0xFF | (k[21] & 0xFF) << 8 | (k[22] & 0xFF) << 16 | (k[23] & 0xFF) << 24;
    int j13 = k[24] & 0xFF | (k[25] & 0xFF) << 8 | (k[26] & 0xFF) << 16 | (k[27] & 0xFF) << 24;
    int j14 = k[28] & 0xFF | (k[29] & 0xFF) << 8 | (k[30] & 0xFF) << 16 | (k[31] & 0xFF) << 24;
    int j15 = c[12] & 0xFF | (c[13] & 0xFF) << 8 | (c[14] & 0xFF) << 16 | (c[15] & 0xFF) << 24;
    
    int x0 = j0;int x1 = j1;int x2 = j2;int x3 = j3;int x4 = j4;int x5 = j5;int x6 = j6;int x7 = j7;
    int x8 = j8;int x9 = j9;int x10 = j10;int x11 = j11;int x12 = j12;int x13 = j13;int x14 = j14;
    int x15 = j15;
    
    for (int i = 0; i < 20; i += 2) {
      int u = x0 + x12 | 0x0;
      x4 ^= (u << 7 | u >>> 25);
      u = x4 + x0 | 0x0;
      x8 ^= (u << 9 | u >>> 23);
      u = x8 + x4 | 0x0;
      x12 ^= (u << 13 | u >>> 19);
      u = x12 + x8 | 0x0;
      x0 ^= (u << 18 | u >>> 14);
      
      u = x5 + x1 | 0x0;
      x9 ^= (u << 7 | u >>> 25);
      u = x9 + x5 | 0x0;
      x13 ^= (u << 9 | u >>> 23);
      u = x13 + x9 | 0x0;
      x1 ^= (u << 13 | u >>> 19);
      u = x1 + x13 | 0x0;
      x5 ^= (u << 18 | u >>> 14);
      
      u = x10 + x6 | 0x0;
      x14 ^= (u << 7 | u >>> 25);
      u = x14 + x10 | 0x0;
      x2 ^= (u << 9 | u >>> 23);
      u = x2 + x14 | 0x0;
      x6 ^= (u << 13 | u >>> 19);
      u = x6 + x2 | 0x0;
      x10 ^= (u << 18 | u >>> 14);
      
      u = x15 + x11 | 0x0;
      x3 ^= (u << 7 | u >>> 25);
      u = x3 + x15 | 0x0;
      x7 ^= (u << 9 | u >>> 23);
      u = x7 + x3 | 0x0;
      x11 ^= (u << 13 | u >>> 19);
      u = x11 + x7 | 0x0;
      x15 ^= (u << 18 | u >>> 14);
      
      u = x0 + x3 | 0x0;
      x1 ^= (u << 7 | u >>> 25);
      u = x1 + x0 | 0x0;
      x2 ^= (u << 9 | u >>> 23);
      u = x2 + x1 | 0x0;
      x3 ^= (u << 13 | u >>> 19);
      u = x3 + x2 | 0x0;
      x0 ^= (u << 18 | u >>> 14);
      
      u = x5 + x4 | 0x0;
      x6 ^= (u << 7 | u >>> 25);
      u = x6 + x5 | 0x0;
      x7 ^= (u << 9 | u >>> 23);
      u = x7 + x6 | 0x0;
      x4 ^= (u << 13 | u >>> 19);
      u = x4 + x7 | 0x0;
      x5 ^= (u << 18 | u >>> 14);
      
      u = x10 + x9 | 0x0;
      x11 ^= (u << 7 | u >>> 25);
      u = x11 + x10 | 0x0;
      x8 ^= (u << 9 | u >>> 23);
      u = x8 + x11 | 0x0;
      x9 ^= (u << 13 | u >>> 19);
      u = x9 + x8 | 0x0;
      x10 ^= (u << 18 | u >>> 14);
      
      u = x15 + x14 | 0x0;
      x12 ^= (u << 7 | u >>> 25);
      u = x12 + x15 | 0x0;
      x13 ^= (u << 9 | u >>> 23);
      u = x13 + x12 | 0x0;
      x14 ^= (u << 13 | u >>> 19);
      u = x14 + x13 | 0x0;
      x15 ^= (u << 18 | u >>> 14);
    }
    x0 = x0 + j0 | 0x0;
    x1 = x1 + j1 | 0x0;
    x2 = x2 + j2 | 0x0;
    x3 = x3 + j3 | 0x0;
    x4 = x4 + j4 | 0x0;
    x5 = x5 + j5 | 0x0;
    x6 = x6 + j6 | 0x0;
    x7 = x7 + j7 | 0x0;
    x8 = x8 + j8 | 0x0;
    x9 = x9 + j9 | 0x0;
    x10 = x10 + j10 | 0x0;
    x11 = x11 + j11 | 0x0;
    x12 = x12 + j12 | 0x0;
    x13 = x13 + j13 | 0x0;
    x14 = x14 + j14 | 0x0;
    x15 = x15 + j15 | 0x0;
    
    o[0] = ((byte)(x0 >>> 0 & 0xFF));
    o[1] = ((byte)(x0 >>> 8 & 0xFF));
    o[2] = ((byte)(x0 >>> 16 & 0xFF));
    o[3] = ((byte)(x0 >>> 24 & 0xFF));
    
    o[4] = ((byte)(x1 >>> 0 & 0xFF));
    o[5] = ((byte)(x1 >>> 8 & 0xFF));
    o[6] = ((byte)(x1 >>> 16 & 0xFF));
    o[7] = ((byte)(x1 >>> 24 & 0xFF));
    
    o[8] = ((byte)(x2 >>> 0 & 0xFF));
    o[9] = ((byte)(x2 >>> 8 & 0xFF));
    o[10] = ((byte)(x2 >>> 16 & 0xFF));
    o[11] = ((byte)(x2 >>> 24 & 0xFF));
    
    o[12] = ((byte)(x3 >>> 0 & 0xFF));
    o[13] = ((byte)(x3 >>> 8 & 0xFF));
    o[14] = ((byte)(x3 >>> 16 & 0xFF));
    o[15] = ((byte)(x3 >>> 24 & 0xFF));
    
    o[16] = ((byte)(x4 >>> 0 & 0xFF));
    o[17] = ((byte)(x4 >>> 8 & 0xFF));
    o[18] = ((byte)(x4 >>> 16 & 0xFF));
    o[19] = ((byte)(x4 >>> 24 & 0xFF));
    
    o[20] = ((byte)(x5 >>> 0 & 0xFF));
    o[21] = ((byte)(x5 >>> 8 & 0xFF));
    o[22] = ((byte)(x5 >>> 16 & 0xFF));
    o[23] = ((byte)(x5 >>> 24 & 0xFF));
    
    o[24] = ((byte)(x6 >>> 0 & 0xFF));
    o[25] = ((byte)(x6 >>> 8 & 0xFF));
    o[26] = ((byte)(x6 >>> 16 & 0xFF));
    o[27] = ((byte)(x6 >>> 24 & 0xFF));
    
    o[28] = ((byte)(x7 >>> 0 & 0xFF));
    o[29] = ((byte)(x7 >>> 8 & 0xFF));
    o[30] = ((byte)(x7 >>> 16 & 0xFF));
    o[31] = ((byte)(x7 >>> 24 & 0xFF));
    
    o[32] = ((byte)(x8 >>> 0 & 0xFF));
    o[33] = ((byte)(x8 >>> 8 & 0xFF));
    o[34] = ((byte)(x8 >>> 16 & 0xFF));
    o[35] = ((byte)(x8 >>> 24 & 0xFF));
    
    o[36] = ((byte)(x9 >>> 0 & 0xFF));
    o[37] = ((byte)(x9 >>> 8 & 0xFF));
    o[38] = ((byte)(x9 >>> 16 & 0xFF));
    o[39] = ((byte)(x9 >>> 24 & 0xFF));
    
    o[40] = ((byte)(x10 >>> 0 & 0xFF));
    o[41] = ((byte)(x10 >>> 8 & 0xFF));
    o[42] = ((byte)(x10 >>> 16 & 0xFF));
    o[43] = ((byte)(x10 >>> 24 & 0xFF));
    
    o[44] = ((byte)(x11 >>> 0 & 0xFF));
    o[45] = ((byte)(x11 >>> 8 & 0xFF));
    o[46] = ((byte)(x11 >>> 16 & 0xFF));
    o[47] = ((byte)(x11 >>> 24 & 0xFF));
    
    o[48] = ((byte)(x12 >>> 0 & 0xFF));
    o[49] = ((byte)(x12 >>> 8 & 0xFF));
    o[50] = ((byte)(x12 >>> 16 & 0xFF));
    o[51] = ((byte)(x12 >>> 24 & 0xFF));
    
    o[52] = ((byte)(x13 >>> 0 & 0xFF));
    o[53] = ((byte)(x13 >>> 8 & 0xFF));
    o[54] = ((byte)(x13 >>> 16 & 0xFF));
    o[55] = ((byte)(x13 >>> 24 & 0xFF));
    
    o[56] = ((byte)(x14 >>> 0 & 0xFF));
    o[57] = ((byte)(x14 >>> 8 & 0xFF));
    o[58] = ((byte)(x14 >>> 16 & 0xFF));
    o[59] = ((byte)(x14 >>> 24 & 0xFF));
    
    o[60] = ((byte)(x15 >>> 0 & 0xFF));
    o[61] = ((byte)(x15 >>> 8 & 0xFF));
    o[62] = ((byte)(x15 >>> 16 & 0xFF));
    o[63] = ((byte)(x15 >>> 24 & 0xFF));
  }
  




  private static void core_hsalsa20(byte[] o, byte[] p, byte[] k, byte[] c)
  {
    int j0 = c[0] & 0xFF | (c[1] & 0xFF) << 8 | (c[2] & 0xFF) << 16 | (c[3] & 0xFF) << 24;
    int j1 = k[0] & 0xFF | (k[1] & 0xFF) << 8 | (k[2] & 0xFF) << 16 | (k[3] & 0xFF) << 24;
    int j2 = k[4] & 0xFF | (k[5] & 0xFF) << 8 | (k[6] & 0xFF) << 16 | (k[7] & 0xFF) << 24;
    int j3 = k[8] & 0xFF | (k[9] & 0xFF) << 8 | (k[10] & 0xFF) << 16 | (k[11] & 0xFF) << 24;
    int j4 = k[12] & 0xFF | (k[13] & 0xFF) << 8 | (k[14] & 0xFF) << 16 | (k[15] & 0xFF) << 24;
    int j5 = c[4] & 0xFF | (c[5] & 0xFF) << 8 | (c[6] & 0xFF) << 16 | (c[7] & 0xFF) << 24;
    int j6 = p[0] & 0xFF | (p[1] & 0xFF) << 8 | (p[2] & 0xFF) << 16 | (p[3] & 0xFF) << 24;
    int j7 = p[4] & 0xFF | (p[5] & 0xFF) << 8 | (p[6] & 0xFF) << 16 | (p[7] & 0xFF) << 24;
    int j8 = p[8] & 0xFF | (p[9] & 0xFF) << 8 | (p[10] & 0xFF) << 16 | (p[11] & 0xFF) << 24;
    int j9 = p[12] & 0xFF | (p[13] & 0xFF) << 8 | (p[14] & 0xFF) << 16 | (p[15] & 0xFF) << 24;
    int j10 = c[8] & 0xFF | (c[9] & 0xFF) << 8 | (c[10] & 0xFF) << 16 | (c[11] & 0xFF) << 24;
    int j11 = k[16] & 0xFF | (k[17] & 0xFF) << 8 | (k[18] & 0xFF) << 16 | (k[19] & 0xFF) << 24;
    int j12 = k[20] & 0xFF | (k[21] & 0xFF) << 8 | (k[22] & 0xFF) << 16 | (k[23] & 0xFF) << 24;
    int j13 = k[24] & 0xFF | (k[25] & 0xFF) << 8 | (k[26] & 0xFF) << 16 | (k[27] & 0xFF) << 24;
    int j14 = k[28] & 0xFF | (k[29] & 0xFF) << 8 | (k[30] & 0xFF) << 16 | (k[31] & 0xFF) << 24;
    int j15 = c[12] & 0xFF | (c[13] & 0xFF) << 8 | (c[14] & 0xFF) << 16 | (c[15] & 0xFF) << 24;
    
    int x0 = j0;int x1 = j1;int x2 = j2;int x3 = j3;int x4 = j4;int x5 = j5;int x6 = j6;int x7 = j7;
    int x8 = j8;int x9 = j9;int x10 = j10;int x11 = j11;int x12 = j12;int x13 = j13;int x14 = j14;
    int x15 = j15;
    
    for (int i = 0; i < 20; i += 2) {
      int u = x0 + x12 | 0x0;
      x4 ^= (u << 7 | u >>> 25);
      u = x4 + x0 | 0x0;
      x8 ^= (u << 9 | u >>> 23);
      u = x8 + x4 | 0x0;
      x12 ^= (u << 13 | u >>> 19);
      u = x12 + x8 | 0x0;
      x0 ^= (u << 18 | u >>> 14);
      
      u = x5 + x1 | 0x0;
      x9 ^= (u << 7 | u >>> 25);
      u = x9 + x5 | 0x0;
      x13 ^= (u << 9 | u >>> 23);
      u = x13 + x9 | 0x0;
      x1 ^= (u << 13 | u >>> 19);
      u = x1 + x13 | 0x0;
      x5 ^= (u << 18 | u >>> 14);
      
      u = x10 + x6 | 0x0;
      x14 ^= (u << 7 | u >>> 25);
      u = x14 + x10 | 0x0;
      x2 ^= (u << 9 | u >>> 23);
      u = x2 + x14 | 0x0;
      x6 ^= (u << 13 | u >>> 19);
      u = x6 + x2 | 0x0;
      x10 ^= (u << 18 | u >>> 14);
      
      u = x15 + x11 | 0x0;
      x3 ^= (u << 7 | u >>> 25);
      u = x3 + x15 | 0x0;
      x7 ^= (u << 9 | u >>> 23);
      u = x7 + x3 | 0x0;
      x11 ^= (u << 13 | u >>> 19);
      u = x11 + x7 | 0x0;
      x15 ^= (u << 18 | u >>> 14);
      
      u = x0 + x3 | 0x0;
      x1 ^= (u << 7 | u >>> 25);
      u = x1 + x0 | 0x0;
      x2 ^= (u << 9 | u >>> 23);
      u = x2 + x1 | 0x0;
      x3 ^= (u << 13 | u >>> 19);
      u = x3 + x2 | 0x0;
      x0 ^= (u << 18 | u >>> 14);
      
      u = x5 + x4 | 0x0;
      x6 ^= (u << 7 | u >>> 25);
      u = x6 + x5 | 0x0;
      x7 ^= (u << 9 | u >>> 23);
      u = x7 + x6 | 0x0;
      x4 ^= (u << 13 | u >>> 19);
      u = x4 + x7 | 0x0;
      x5 ^= (u << 18 | u >>> 14);
      
      u = x10 + x9 | 0x0;
      x11 ^= (u << 7 | u >>> 25);
      u = x11 + x10 | 0x0;
      x8 ^= (u << 9 | u >>> 23);
      u = x8 + x11 | 0x0;
      x9 ^= (u << 13 | u >>> 19);
      u = x9 + x8 | 0x0;
      x10 ^= (u << 18 | u >>> 14);
      
      u = x15 + x14 | 0x0;
      x12 ^= (u << 7 | u >>> 25);
      u = x12 + x15 | 0x0;
      x13 ^= (u << 9 | u >>> 23);
      u = x13 + x12 | 0x0;
      x14 ^= (u << 13 | u >>> 19);
      u = x14 + x13 | 0x0;
      x15 ^= (u << 18 | u >>> 14);
    }
    
    o[0] = ((byte)(x0 >>> 0 & 0xFF));
    o[1] = ((byte)(x0 >>> 8 & 0xFF));
    o[2] = ((byte)(x0 >>> 16 & 0xFF));
    o[3] = ((byte)(x0 >>> 24 & 0xFF));
    
    o[4] = ((byte)(x5 >>> 0 & 0xFF));
    o[5] = ((byte)(x5 >>> 8 & 0xFF));
    o[6] = ((byte)(x5 >>> 16 & 0xFF));
    o[7] = ((byte)(x5 >>> 24 & 0xFF));
    
    o[8] = ((byte)(x10 >>> 0 & 0xFF));
    o[9] = ((byte)(x10 >>> 8 & 0xFF));
    o[10] = ((byte)(x10 >>> 16 & 0xFF));
    o[11] = ((byte)(x10 >>> 24 & 0xFF));
    
    o[12] = ((byte)(x15 >>> 0 & 0xFF));
    o[13] = ((byte)(x15 >>> 8 & 0xFF));
    o[14] = ((byte)(x15 >>> 16 & 0xFF));
    o[15] = ((byte)(x15 >>> 24 & 0xFF));
    
    o[16] = ((byte)(x6 >>> 0 & 0xFF));
    o[17] = ((byte)(x6 >>> 8 & 0xFF));
    o[18] = ((byte)(x6 >>> 16 & 0xFF));
    o[19] = ((byte)(x6 >>> 24 & 0xFF));
    
    o[20] = ((byte)(x7 >>> 0 & 0xFF));
    o[21] = ((byte)(x7 >>> 8 & 0xFF));
    o[22] = ((byte)(x7 >>> 16 & 0xFF));
    o[23] = ((byte)(x7 >>> 24 & 0xFF));
    
    o[24] = ((byte)(x8 >>> 0 & 0xFF));
    o[25] = ((byte)(x8 >>> 8 & 0xFF));
    o[26] = ((byte)(x8 >>> 16 & 0xFF));
    o[27] = ((byte)(x8 >>> 24 & 0xFF));
    
    o[28] = ((byte)(x9 >>> 0 & 0xFF));
    o[29] = ((byte)(x9 >>> 8 & 0xFF));
    o[30] = ((byte)(x9 >>> 16 & 0xFF));
    o[31] = ((byte)(x9 >>> 24 & 0xFF));
  }
  







  public static int crypto_core_salsa20(byte[] out, byte[] in, byte[] k, byte[] c)
  {
    core_salsa20(out, in, k, c);
    




    return 0;
  }
  

  public static int crypto_core_hsalsa20(byte[] out, byte[] in, byte[] k, byte[] c)
  {
    core_hsalsa20(out, in, k, c);
    




    return 0;
  }
  

  private static final byte[] sigma = { 101, 120, 112, 97, 110, 100, 32, 51, 50, 45, 98, 121, 116, 101, 32, 107 };
  








  private static int crypto_stream_salsa20_xor(byte[] c, int cpos, byte[] m, int mpos, long b, byte[] n, byte[] k)
  {
    byte[] z = new byte[16];byte[] x = new byte[64];
    
    for (int i = 0; i < 16; i++) z[i] = 0;
    for (i = 0; i < 8; i++) z[i] = n[i];
    while (b >= 64L) {
      crypto_core_salsa20(x, z, k, sigma);
      for (i = 0; i < 64; i++) c[(cpos + i)] = ((byte)((m[(mpos + i)] ^ x[i]) & 0xFF));
      int u = 1;
      for (i = 8; i < 16; i++) {
        u = u + (z[i] & 0xFF) | 0x0;
        z[i] = ((byte)(u & 0xFF));
        u >>>= 8;
      }
      b -= 64L;
      cpos += 64;
      mpos += 64;
    }
    if (b > 0L) {
      crypto_core_salsa20(x, z, k, sigma);
      for (i = 0; i < b; i++) { c[(cpos + i)] = ((byte)((m[(mpos + i)] ^ x[i]) & 0xFF));
      }
    }
    



    return 0;
  }
  
  public static int crypto_stream_salsa20(byte[] c, int cpos, long b, byte[] n, byte[] k) {
    byte[] z = new byte[16];byte[] x = new byte[64];
    
    for (int i = 0; i < 16; i++) z[i] = 0;
    for (i = 0; i < 8; i++) z[i] = n[i];
    while (b >= 64L) {
      crypto_core_salsa20(x, z, k, sigma);
      for (i = 0; i < 64; i++) c[(cpos + i)] = x[i];
      int u = 1;
      for (i = 8; i < 16; i++) {
        u = u + (z[i] & 0xFF) | 0x0;
        z[i] = ((byte)(u & 0xFF));
        u >>>= 8;
      }
      b -= 64L;
      cpos += 64;
    }
    if (b > 0L) {
      crypto_core_salsa20(x, z, k, sigma);
      for (i = 0; i < b; i++) { c[(cpos + i)] = x[i];
      }
    }
    



    return 0;
  }
  
  public static int crypto_stream(byte[] c, int cpos, long d, byte[] n, byte[] k) {
    byte[] s = new byte[32];
    crypto_core_hsalsa20(s, n, k, sigma);
    byte[] sn = new byte[8];
    for (int i = 0; i < 8; i++) sn[i] = n[(i + 16)];
    return crypto_stream_salsa20(c, cpos, d, sn, s);
  }
  
  public static int crypto_stream_xor(byte[] c, int cpos, byte[] m, int mpos, long d, byte[] n, byte[] k) {
    byte[] s = new byte[32];
    









    crypto_core_hsalsa20(s, n, k, sigma);
    byte[] sn = new byte[8];
    for (int i = 0; i < 8; i++) sn[i] = n[(i + 16)];
    return crypto_stream_salsa20_xor(c, cpos, m, mpos, d, sn, s);
  }
  

  public static final class poly1305
  {
    private byte[] buffer;
    
    private int[] r;
    
    private int[] h;
    private int[] pad;
    private int leftover;
    private int fin;
    
    public poly1305(byte[] key)
    {
      buffer = new byte[16];
      r = new int[10];
      h = new int[10];
      pad = new int[8];
      leftover = 0;
      fin = 0;
      


      int t0 = key[0] & 0xFF | (key[1] & 0xFF) << 8;r[0] = (t0 & 0x1FFF);
      int t1 = key[2] & 0xFF | (key[3] & 0xFF) << 8;r[1] = ((t0 >>> 13 | t1 << 3) & 0x1FFF);
      int t2 = key[4] & 0xFF | (key[5] & 0xFF) << 8;r[2] = ((t1 >>> 10 | t2 << 6) & 0x1F03);
      int t3 = key[6] & 0xFF | (key[7] & 0xFF) << 8;r[3] = ((t2 >>> 7 | t3 << 9) & 0x1FFF);
      int t4 = key[8] & 0xFF | (key[9] & 0xFF) << 8;r[4] = ((t3 >>> 4 | t4 << 12) & 0xFF);
      r[5] = (t4 >>> 1 & 0x1FFE);
      int t5 = key[10] & 0xFF | (key[11] & 0xFF) << 8;r[6] = ((t4 >>> 14 | t5 << 2) & 0x1FFF);
      int t6 = key[12] & 0xFF | (key[13] & 0xFF) << 8;r[7] = ((t5 >>> 11 | t6 << 5) & 0x1F81);
      int t7 = key[14] & 0xFF | (key[15] & 0xFF) << 8;r[8] = ((t6 >>> 8 | t7 << 8) & 0x1FFF);
      r[9] = (t7 >>> 5 & 0x7F);
      
      pad[0] = (key[16] & 0xFF | (key[17] & 0xFF) << 8);
      pad[1] = (key[18] & 0xFF | (key[19] & 0xFF) << 8);
      pad[2] = (key[20] & 0xFF | (key[21] & 0xFF) << 8);
      pad[3] = (key[22] & 0xFF | (key[23] & 0xFF) << 8);
      pad[4] = (key[24] & 0xFF | (key[25] & 0xFF) << 8);
      pad[5] = (key[26] & 0xFF | (key[27] & 0xFF) << 8);
      pad[6] = (key[28] & 0xFF | (key[29] & 0xFF) << 8);
      pad[7] = (key[30] & 0xFF | (key[31] & 0xFF) << 8);
    }
    
    public poly1305 blocks(byte[] m, int mpos, int bytes) {
      int hibit = fin != 0 ? 0 : 2048;
      


      int h0 = h[0];
      int h1 = h[1];
      int h2 = h[2];
      int h3 = h[3];
      int h4 = h[4];
      int h5 = h[5];
      int h6 = h[6];
      int h7 = h[7];
      int h8 = h[8];
      int h9 = h[9];
      
      int r0 = r[0];
      int r1 = r[1];
      int r2 = r[2];
      int r3 = r[3];
      int r4 = r[4];
      int r5 = r[5];
      int r6 = r[6];
      int r7 = r[7];
      int r8 = r[8];
      int r9 = r[9];
      
      while (bytes >= 16) {
        int t0 = m[(mpos + 0)] & 0xFF | (m[(mpos + 1)] & 0xFF) << 8;h0 += (t0 & 0x1FFF);
        int t1 = m[(mpos + 2)] & 0xFF | (m[(mpos + 3)] & 0xFF) << 8;h1 += ((t0 >>> 13 | t1 << 3) & 0x1FFF);
        int t2 = m[(mpos + 4)] & 0xFF | (m[(mpos + 5)] & 0xFF) << 8;h2 += ((t1 >>> 10 | t2 << 6) & 0x1FFF);
        int t3 = m[(mpos + 6)] & 0xFF | (m[(mpos + 7)] & 0xFF) << 8;h3 += ((t2 >>> 7 | t3 << 9) & 0x1FFF);
        int t4 = m[(mpos + 8)] & 0xFF | (m[(mpos + 9)] & 0xFF) << 8;h4 += ((t3 >>> 4 | t4 << 12) & 0x1FFF);
        h5 += (t4 >>> 1 & 0x1FFF);
        int t5 = m[(mpos + 10)] & 0xFF | (m[(mpos + 11)] & 0xFF) << 8;h6 += ((t4 >>> 14 | t5 << 2) & 0x1FFF);
        int t6 = m[(mpos + 12)] & 0xFF | (m[(mpos + 13)] & 0xFF) << 8;h7 += ((t5 >>> 11 | t6 << 5) & 0x1FFF);
        int t7 = m[(mpos + 14)] & 0xFF | (m[(mpos + 15)] & 0xFF) << 8;h8 += ((t6 >>> 8 | t7 << 8) & 0x1FFF);
        h9 += (t7 >>> 5 | hibit);
        
        int c = 0;
        
        int d0 = c;
        d0 += h0 * r0;
        d0 += h1 * (5 * r9);
        d0 += h2 * (5 * r8);
        d0 += h3 * (5 * r7);
        d0 += h4 * (5 * r6);
        c = d0 >>> 13;d0 &= 0x1FFF;
        d0 += h5 * (5 * r5);
        d0 += h6 * (5 * r4);
        d0 += h7 * (5 * r3);
        d0 += h8 * (5 * r2);
        d0 += h9 * (5 * r1);
        c += (d0 >>> 13);d0 &= 0x1FFF;
        
        int d1 = c;
        d1 += h0 * r1;
        d1 += h1 * r0;
        d1 += h2 * (5 * r9);
        d1 += h3 * (5 * r8);
        d1 += h4 * (5 * r7);
        c = d1 >>> 13;d1 &= 0x1FFF;
        d1 += h5 * (5 * r6);
        d1 += h6 * (5 * r5);
        d1 += h7 * (5 * r4);
        d1 += h8 * (5 * r3);
        d1 += h9 * (5 * r2);
        c += (d1 >>> 13);d1 &= 0x1FFF;
        
        int d2 = c;
        d2 += h0 * r2;
        d2 += h1 * r1;
        d2 += h2 * r0;
        d2 += h3 * (5 * r9);
        d2 += h4 * (5 * r8);
        c = d2 >>> 13;d2 &= 0x1FFF;
        d2 += h5 * (5 * r7);
        d2 += h6 * (5 * r6);
        d2 += h7 * (5 * r5);
        d2 += h8 * (5 * r4);
        d2 += h9 * (5 * r3);
        c += (d2 >>> 13);d2 &= 0x1FFF;
        
        int d3 = c;
        d3 += h0 * r3;
        d3 += h1 * r2;
        d3 += h2 * r1;
        d3 += h3 * r0;
        d3 += h4 * (5 * r9);
        c = d3 >>> 13;d3 &= 0x1FFF;
        d3 += h5 * (5 * r8);
        d3 += h6 * (5 * r7);
        d3 += h7 * (5 * r6);
        d3 += h8 * (5 * r5);
        d3 += h9 * (5 * r4);
        c += (d3 >>> 13);d3 &= 0x1FFF;
        
        int d4 = c;
        d4 += h0 * r4;
        d4 += h1 * r3;
        d4 += h2 * r2;
        d4 += h3 * r1;
        d4 += h4 * r0;
        c = d4 >>> 13;d4 &= 0x1FFF;
        d4 += h5 * (5 * r9);
        d4 += h6 * (5 * r8);
        d4 += h7 * (5 * r7);
        d4 += h8 * (5 * r6);
        d4 += h9 * (5 * r5);
        c += (d4 >>> 13);d4 &= 0x1FFF;
        
        int d5 = c;
        d5 += h0 * r5;
        d5 += h1 * r4;
        d5 += h2 * r3;
        d5 += h3 * r2;
        d5 += h4 * r1;
        c = d5 >>> 13;d5 &= 0x1FFF;
        d5 += h5 * r0;
        d5 += h6 * (5 * r9);
        d5 += h7 * (5 * r8);
        d5 += h8 * (5 * r7);
        d5 += h9 * (5 * r6);
        c += (d5 >>> 13);d5 &= 0x1FFF;
        
        int d6 = c;
        d6 += h0 * r6;
        d6 += h1 * r5;
        d6 += h2 * r4;
        d6 += h3 * r3;
        d6 += h4 * r2;
        c = d6 >>> 13;d6 &= 0x1FFF;
        d6 += h5 * r1;
        d6 += h6 * r0;
        d6 += h7 * (5 * r9);
        d6 += h8 * (5 * r8);
        d6 += h9 * (5 * r7);
        c += (d6 >>> 13);d6 &= 0x1FFF;
        
        int d7 = c;
        d7 += h0 * r7;
        d7 += h1 * r6;
        d7 += h2 * r5;
        d7 += h3 * r4;
        d7 += h4 * r3;
        c = d7 >>> 13;d7 &= 0x1FFF;
        d7 += h5 * r2;
        d7 += h6 * r1;
        d7 += h7 * r0;
        d7 += h8 * (5 * r9);
        d7 += h9 * (5 * r8);
        c += (d7 >>> 13);d7 &= 0x1FFF;
        
        int d8 = c;
        d8 += h0 * r8;
        d8 += h1 * r7;
        d8 += h2 * r6;
        d8 += h3 * r5;
        d8 += h4 * r4;
        c = d8 >>> 13;d8 &= 0x1FFF;
        d8 += h5 * r3;
        d8 += h6 * r2;
        d8 += h7 * r1;
        d8 += h8 * r0;
        d8 += h9 * (5 * r9);
        c += (d8 >>> 13);d8 &= 0x1FFF;
        
        int d9 = c;
        d9 += h0 * r9;
        d9 += h1 * r8;
        d9 += h2 * r7;
        d9 += h3 * r6;
        d9 += h4 * r5;
        c = d9 >>> 13;d9 &= 0x1FFF;
        d9 += h5 * r4;
        d9 += h6 * r3;
        d9 += h7 * r2;
        d9 += h8 * r1;
        d9 += h9 * r0;
        c += (d9 >>> 13);d9 &= 0x1FFF;
        
        c = (c << 2) + c | 0x0;
        c = c + d0 | 0x0;
        d0 = c & 0x1FFF;
        c >>>= 13;
        d1 += c;
        
        h0 = d0;
        h1 = d1;
        h2 = d2;
        h3 = d3;
        h4 = d4;
        h5 = d5;
        h6 = d6;
        h7 = d7;
        h8 = d8;
        h9 = d9;
        
        mpos += 16;
        bytes -= 16;
      }
      h[0] = h0;
      h[1] = h1;
      h[2] = h2;
      h[3] = h3;
      h[4] = h4;
      h[5] = h5;
      h[6] = h6;
      h[7] = h7;
      h[8] = h8;
      h[9] = h9;
      
      return this;
    }
    
    public poly1305 finish(byte[] mac, int macpos) {
      int[] g = new int[10];
      

      if (leftover != 0) {
        int i = leftover;
        buffer[(i++)] = 1;
        for (; i < 16; i++) buffer[i] = 0;
        fin = 1;
        blocks(buffer, 0, 16);
      }
      
      int c = h[1] >>> 13;
      h[1] &= 0x1FFF;
      for (int i = 2; i < 10; i++) {
        h[i] += c;
        c = h[i] >>> 13;
        h[i] &= 0x1FFF;
      }
      h[0] += c * 5;
      c = h[0] >>> 13;
      h[0] &= 0x1FFF;
      h[1] += c;
      c = h[1] >>> 13;
      h[1] &= 0x1FFF;
      h[2] += c;
      
      g[0] = (h[0] + 5);
      c = g[0] >>> 13;
      g[0] &= 0x1FFF;
      for (i = 1; i < 10; i++) {
        g[i] = (h[i] + c);
        c = g[i] >>> 13;
        g[i] &= 0x1FFF;
      }
      g[9] -= 8192;g[9] &= 0xFFFF;
      
      int mask = (g[9] >>> 15) - 1;mask &= 0xFFFF;
      for (i = 0; i < 10; i++) g[i] &= mask;
      mask ^= 0xFFFFFFFF;
      for (i = 0; i < 10; i++) { h[i] = (h[i] & mask | g[i]);
      }
      h[0] = ((h[0] | h[1] << 13) & 0xFFFF);
      h[1] = ((h[1] >>> 3 | h[2] << 10) & 0xFFFF);
      h[2] = ((h[2] >>> 6 | h[3] << 7) & 0xFFFF);
      h[3] = ((h[3] >>> 9 | h[4] << 4) & 0xFFFF);
      h[4] = ((h[4] >>> 12 | h[5] << 1 | h[6] << 14) & 0xFFFF);
      h[5] = ((h[6] >>> 2 | h[7] << 11) & 0xFFFF);
      h[6] = ((h[7] >>> 5 | h[8] << 8) & 0xFFFF);
      h[7] = ((h[8] >>> 8 | h[9] << 5) & 0xFFFF);
      
      int f = h[0] + pad[0];
      h[0] = (f & 0xFFFF);
      for (i = 1; i < 8; i++) {
        f = (h[i] + pad[i] | 0x0) + (f >>> 16) | 0x0;
        h[i] = (f & 0xFFFF);
      }
      
      mac[(macpos + 0)] = ((byte)(h[0] >>> 0 & 0xFF));
      mac[(macpos + 1)] = ((byte)(h[0] >>> 8 & 0xFF));
      mac[(macpos + 2)] = ((byte)(h[1] >>> 0 & 0xFF));
      mac[(macpos + 3)] = ((byte)(h[1] >>> 8 & 0xFF));
      mac[(macpos + 4)] = ((byte)(h[2] >>> 0 & 0xFF));
      mac[(macpos + 5)] = ((byte)(h[2] >>> 8 & 0xFF));
      mac[(macpos + 6)] = ((byte)(h[3] >>> 0 & 0xFF));
      mac[(macpos + 7)] = ((byte)(h[3] >>> 8 & 0xFF));
      mac[(macpos + 8)] = ((byte)(h[4] >>> 0 & 0xFF));
      mac[(macpos + 9)] = ((byte)(h[4] >>> 8 & 0xFF));
      mac[(macpos + 10)] = ((byte)(h[5] >>> 0 & 0xFF));
      mac[(macpos + 11)] = ((byte)(h[5] >>> 8 & 0xFF));
      mac[(macpos + 12)] = ((byte)(h[6] >>> 0 & 0xFF));
      mac[(macpos + 13)] = ((byte)(h[6] >>> 8 & 0xFF));
      mac[(macpos + 14)] = ((byte)(h[7] >>> 0 & 0xFF));
      mac[(macpos + 15)] = ((byte)(h[7] >>> 8 & 0xFF));
      
      return this;
    }
    

    public poly1305 update(byte[] m, int mpos, int bytes)
    {
      if (leftover != 0) {
        int want = 16 - leftover;
        if (want > bytes)
          want = bytes;
        for (int i = 0; i < want; i++)
          buffer[(leftover + i)] = m[(mpos + i)];
        bytes -= want;
        mpos += want;
        leftover += want;
        if (leftover < 16)
          return this;
        blocks(buffer, 0, 16);
        leftover = 0;
      }
      
      if (bytes >= 16) {
        int want = bytes - bytes % 16;
        blocks(m, mpos, want);
        mpos += want;
        bytes -= want;
      }
      
      if (bytes != 0) {
        for (int i = 0; i < bytes; i++)
          buffer[(leftover + i)] = m[(mpos + i)];
        leftover += bytes;
      }
      
      return this;
    }
  }
  





  private static int crypto_onetimeauth(byte[] out, int outpos, byte[] m, int mpos, int n, byte[] k)
  {
    poly1305 s = new poly1305(k);
    s.update(m, mpos, n);
    s.finish(out, outpos);
    





    return 0;
  }
  
  public static int crypto_onetimeauth(byte[] out, byte[] m, int n, byte[] k) { return crypto_onetimeauth(out, 0, m, 0, n, k); }
  





  private static int crypto_onetimeauth_verify(byte[] h, int hoff, byte[] m, int moff, int n, byte[] k)
  {
    byte[] x = new byte[16];
    crypto_onetimeauth(x, 0, m, moff, n, k);
    return crypto_verify_16(h, hoff, x, 0);
  }
  
  public static int crypto_onetimeauth_verify(byte[] h, byte[] m, int n, byte[] k) { return crypto_onetimeauth_verify(h, 0, m, 0, n, k); }
  
  public static int crypto_onetimeauth_verify(byte[] h, byte[] m, byte[] k) {
    return crypto_onetimeauth_verify(h, m, m != null ? m.length : 0, k);
  }
  

  public static int crypto_secretbox(byte[] c, byte[] m, int d, byte[] n, byte[] k)
  {
    if (d < 32) return -1;
    crypto_stream_xor(c, 0, m, 0, d, n, k);
    crypto_onetimeauth(c, 16, c, 32, d - 32, c);
    
    return 0;
  }
  

  public static int crypto_secretbox_open(byte[] m, byte[] c, int d, byte[] n, byte[] k)
  {
    byte[] x = new byte[32];
    if (d < 32) return -1;
    crypto_stream(x, 0, 32L, n, k);
    if (crypto_onetimeauth_verify(c, 16, c, 32, d - 32, x) != 0) return -1;
    crypto_stream_xor(m, 0, c, 0, d, n, k);
    
    return 0;
  }
  

  private static void set25519(long[] r, long[] a)
  {
    for (int i = 0; i < 16; i++) { r[i] = a[i];
    }
  }
  
  private static void car25519(long[] o)
  {
    long c = 1L;
    for (int i = 0; i < 16; i++) {
      long v = o[i] + c + 65535L;
      c = v >> 16;
      o[i] = (v - c * 65536L);
    }
    o[0] += c - 1L + 37L * (c - 1L);
  }
  



  private static void sel25519(long[] p, long[] q, int b)
  {
    sel25519(p, 0, q, 0, b);
  }
  


  private static void sel25519(long[] p, int poff, long[] q, int qoff, int b)
  {
    long c = b - 1 ^ 0xFFFFFFFF;
    for (int i = 0; i < 16; i++) {
      long t = c & (p[(i + poff)] ^ q[(i + qoff)]);
      p[(i + poff)] ^= t;
      q[(i + qoff)] ^= t;
    }
  }
  

  private static void pack25519(byte[] o, long[] n, int noff)
  {
    long[] m = new long[16];long[] t = new long[16];
    for (int i = 0; i < 16; i++) t[i] = n[(i + noff)];
    car25519(t);
    car25519(t);
    car25519(t);
    for (int j = 0; j < 2; j++) {
      t[0] -= 65517L;
      for (i = 1; i < 15; i++) {
        m[i] = (t[i] - 65535L - (m[(i - 1)] >> 16 & 1L));
        m[(i - 1)] &= 0xFFFF;
      }
      m[15] = (t[15] - 32767L - (m[14] >> 16 & 1L));
      int b = (int)(m[15] >> 16 & 1L);
      m[14] &= 0xFFFF;
      sel25519(t, 0, m, 0, 1 - b);
    }
    for (i = 0; i < 16; i++) {
      o[(2 * i)] = ((byte)(int)(t[i] & 0xFF));
      o[(2 * i + 1)] = ((byte)(int)(t[i] >> 8));
    }
  }
  
  private static int neq25519(long[] a, long[] b) {
    return neq25519(a, 0, b, 0);
  }
  
  private static int neq25519(long[] a, int aoff, long[] b, int boff) {
    byte[] c = new byte[32];byte[] d = new byte[32];
    pack25519(c, a, aoff);
    pack25519(d, b, boff);
    return crypto_verify_32(c, 0, d, 0);
  }
  
  private static byte par25519(long[] a)
  {
    return par25519(a, 0);
  }
  
  private static byte par25519(long[] a, int aoff) {
    byte[] d = new byte[32];
    pack25519(d, a, aoff);
    return (byte)(d[0] & 0x1);
  }
  

  private static void unpack25519(long[] o, byte[] n)
  {
    for (int i = 0; i < 16; i++) o[i] = ((n[(2 * i)] & 0xFF) + (n[(2 * i + 1)] << 8 & 0xFFFF));
    o[15] &= 0x7FFF;
  }
  



  private static void A(long[] o, long[] a, long[] b)
  {
    A(o, 0, a, 0, b, 0);
  }
  



  private static void A(long[] o, int ooff, long[] a, int aoff, long[] b, int boff)
  {
    for (int i = 0; i < 16; i++) { a[(i + aoff)] += b[(i + boff)];
    }
  }
  


  private static void Z(long[] o, long[] a, long[] b)
  {
    Z(o, 0, a, 0, b, 0);
  }
  



  private static void Z(long[] o, int ooff, long[] a, int aoff, long[] b, int boff)
  {
    for (int i = 0; i < 16; i++) { a[(i + aoff)] -= b[(i + boff)];
    }
  }
  


  private static void M(long[] o, long[] a, long[] b)
  {
    M(o, 0, a, 0, b, 0);
  }
  



  private static void M(long[] o, int ooff, long[] a, int aoff, long[] b, int boff)
  {
    long t0 = 0L;long t1 = 0L;long t2 = 0L;long t3 = 0L;long t4 = 0L;long t5 = 0L;long t6 = 0L;long t7 = 0L;
    long t8 = 0L;long t9 = 0L;long t10 = 0L;long t11 = 0L;long t12 = 0L;long t13 = 0L;long t14 = 0L;long t15 = 0L;
    long t16 = 0L;long t17 = 0L;long t18 = 0L;long t19 = 0L;long t20 = 0L;long t21 = 0L;long t22 = 0L;long t23 = 0L;
    long t24 = 0L;long t25 = 0L;long t26 = 0L;long t27 = 0L;long t28 = 0L;long t29 = 0L;long t30 = 0L;
    long b0 = b[(0 + boff)];
    long b1 = b[(1 + boff)];
    long b2 = b[(2 + boff)];
    long b3 = b[(3 + boff)];
    long b4 = b[(4 + boff)];
    long b5 = b[(5 + boff)];
    long b6 = b[(6 + boff)];
    long b7 = b[(7 + boff)];
    long b8 = b[(8 + boff)];
    long b9 = b[(9 + boff)];
    long b10 = b[(10 + boff)];
    long b11 = b[(11 + boff)];
    long b12 = b[(12 + boff)];
    long b13 = b[(13 + boff)];
    long b14 = b[(14 + boff)];
    long b15 = b[(15 + boff)];
    
    long v = a[(0 + aoff)];
    t0 += v * b0;
    t1 += v * b1;
    t2 += v * b2;
    t3 += v * b3;
    t4 += v * b4;
    t5 += v * b5;
    t6 += v * b6;
    t7 += v * b7;
    t8 += v * b8;
    t9 += v * b9;
    t10 += v * b10;
    t11 += v * b11;
    t12 += v * b12;
    t13 += v * b13;
    t14 += v * b14;
    t15 += v * b15;
    v = a[(1 + aoff)];
    t1 += v * b0;
    t2 += v * b1;
    t3 += v * b2;
    t4 += v * b3;
    t5 += v * b4;
    t6 += v * b5;
    t7 += v * b6;
    t8 += v * b7;
    t9 += v * b8;
    t10 += v * b9;
    t11 += v * b10;
    t12 += v * b11;
    t13 += v * b12;
    t14 += v * b13;
    t15 += v * b14;
    t16 += v * b15;
    v = a[(2 + aoff)];
    t2 += v * b0;
    t3 += v * b1;
    t4 += v * b2;
    t5 += v * b3;
    t6 += v * b4;
    t7 += v * b5;
    t8 += v * b6;
    t9 += v * b7;
    t10 += v * b8;
    t11 += v * b9;
    t12 += v * b10;
    t13 += v * b11;
    t14 += v * b12;
    t15 += v * b13;
    t16 += v * b14;
    t17 += v * b15;
    v = a[(3 + aoff)];
    t3 += v * b0;
    t4 += v * b1;
    t5 += v * b2;
    t6 += v * b3;
    t7 += v * b4;
    t8 += v * b5;
    t9 += v * b6;
    t10 += v * b7;
    t11 += v * b8;
    t12 += v * b9;
    t13 += v * b10;
    t14 += v * b11;
    t15 += v * b12;
    t16 += v * b13;
    t17 += v * b14;
    t18 += v * b15;
    v = a[(4 + aoff)];
    t4 += v * b0;
    t5 += v * b1;
    t6 += v * b2;
    t7 += v * b3;
    t8 += v * b4;
    t9 += v * b5;
    t10 += v * b6;
    t11 += v * b7;
    t12 += v * b8;
    t13 += v * b9;
    t14 += v * b10;
    t15 += v * b11;
    t16 += v * b12;
    t17 += v * b13;
    t18 += v * b14;
    t19 += v * b15;
    v = a[(5 + aoff)];
    t5 += v * b0;
    t6 += v * b1;
    t7 += v * b2;
    t8 += v * b3;
    t9 += v * b4;
    t10 += v * b5;
    t11 += v * b6;
    t12 += v * b7;
    t13 += v * b8;
    t14 += v * b9;
    t15 += v * b10;
    t16 += v * b11;
    t17 += v * b12;
    t18 += v * b13;
    t19 += v * b14;
    t20 += v * b15;
    v = a[(6 + aoff)];
    t6 += v * b0;
    t7 += v * b1;
    t8 += v * b2;
    t9 += v * b3;
    t10 += v * b4;
    t11 += v * b5;
    t12 += v * b6;
    t13 += v * b7;
    t14 += v * b8;
    t15 += v * b9;
    t16 += v * b10;
    t17 += v * b11;
    t18 += v * b12;
    t19 += v * b13;
    t20 += v * b14;
    t21 += v * b15;
    v = a[(7 + aoff)];
    t7 += v * b0;
    t8 += v * b1;
    t9 += v * b2;
    t10 += v * b3;
    t11 += v * b4;
    t12 += v * b5;
    t13 += v * b6;
    t14 += v * b7;
    t15 += v * b8;
    t16 += v * b9;
    t17 += v * b10;
    t18 += v * b11;
    t19 += v * b12;
    t20 += v * b13;
    t21 += v * b14;
    t22 += v * b15;
    v = a[(8 + aoff)];
    t8 += v * b0;
    t9 += v * b1;
    t10 += v * b2;
    t11 += v * b3;
    t12 += v * b4;
    t13 += v * b5;
    t14 += v * b6;
    t15 += v * b7;
    t16 += v * b8;
    t17 += v * b9;
    t18 += v * b10;
    t19 += v * b11;
    t20 += v * b12;
    t21 += v * b13;
    t22 += v * b14;
    t23 += v * b15;
    v = a[(9 + aoff)];
    t9 += v * b0;
    t10 += v * b1;
    t11 += v * b2;
    t12 += v * b3;
    t13 += v * b4;
    t14 += v * b5;
    t15 += v * b6;
    t16 += v * b7;
    t17 += v * b8;
    t18 += v * b9;
    t19 += v * b10;
    t20 += v * b11;
    t21 += v * b12;
    t22 += v * b13;
    t23 += v * b14;
    t24 += v * b15;
    v = a[(10 + aoff)];
    t10 += v * b0;
    t11 += v * b1;
    t12 += v * b2;
    t13 += v * b3;
    t14 += v * b4;
    t15 += v * b5;
    t16 += v * b6;
    t17 += v * b7;
    t18 += v * b8;
    t19 += v * b9;
    t20 += v * b10;
    t21 += v * b11;
    t22 += v * b12;
    t23 += v * b13;
    t24 += v * b14;
    t25 += v * b15;
    v = a[(11 + aoff)];
    t11 += v * b0;
    t12 += v * b1;
    t13 += v * b2;
    t14 += v * b3;
    t15 += v * b4;
    t16 += v * b5;
    t17 += v * b6;
    t18 += v * b7;
    t19 += v * b8;
    t20 += v * b9;
    t21 += v * b10;
    t22 += v * b11;
    t23 += v * b12;
    t24 += v * b13;
    t25 += v * b14;
    t26 += v * b15;
    v = a[(12 + aoff)];
    t12 += v * b0;
    t13 += v * b1;
    t14 += v * b2;
    t15 += v * b3;
    t16 += v * b4;
    t17 += v * b5;
    t18 += v * b6;
    t19 += v * b7;
    t20 += v * b8;
    t21 += v * b9;
    t22 += v * b10;
    t23 += v * b11;
    t24 += v * b12;
    t25 += v * b13;
    t26 += v * b14;
    t27 += v * b15;
    v = a[(13 + aoff)];
    t13 += v * b0;
    t14 += v * b1;
    t15 += v * b2;
    t16 += v * b3;
    t17 += v * b4;
    t18 += v * b5;
    t19 += v * b6;
    t20 += v * b7;
    t21 += v * b8;
    t22 += v * b9;
    t23 += v * b10;
    t24 += v * b11;
    t25 += v * b12;
    t26 += v * b13;
    t27 += v * b14;
    t28 += v * b15;
    v = a[(14 + aoff)];
    t14 += v * b0;
    t15 += v * b1;
    t16 += v * b2;
    t17 += v * b3;
    t18 += v * b4;
    t19 += v * b5;
    t20 += v * b6;
    t21 += v * b7;
    t22 += v * b8;
    t23 += v * b9;
    t24 += v * b10;
    t25 += v * b11;
    t26 += v * b12;
    t27 += v * b13;
    t28 += v * b14;
    t29 += v * b15;
    v = a[(15 + aoff)];
    t15 += v * b0;
    t16 += v * b1;
    t17 += v * b2;
    t18 += v * b3;
    t19 += v * b4;
    t20 += v * b5;
    t21 += v * b6;
    t22 += v * b7;
    t23 += v * b8;
    t24 += v * b9;
    t25 += v * b10;
    t26 += v * b11;
    t27 += v * b12;
    t28 += v * b13;
    t29 += v * b14;
    t30 += v * b15;
    
    t0 += 38L * t16;
    t1 += 38L * t17;
    t2 += 38L * t18;
    t3 += 38L * t19;
    t4 += 38L * t20;
    t5 += 38L * t21;
    t6 += 38L * t22;
    t7 += 38L * t23;
    t8 += 38L * t24;
    t9 += 38L * t25;
    t10 += 38L * t26;
    t11 += 38L * t27;
    t12 += 38L * t28;
    t13 += 38L * t29;
    t14 += 38L * t30;
    


    long c = 1L;
    v = t0 + c + 65535L;c = v >> 16;t0 = v - c * 65536L;
    v = t1 + c + 65535L;c = v >> 16;t1 = v - c * 65536L;
    v = t2 + c + 65535L;c = v >> 16;t2 = v - c * 65536L;
    v = t3 + c + 65535L;c = v >> 16;t3 = v - c * 65536L;
    v = t4 + c + 65535L;c = v >> 16;t4 = v - c * 65536L;
    v = t5 + c + 65535L;c = v >> 16;t5 = v - c * 65536L;
    v = t6 + c + 65535L;c = v >> 16;t6 = v - c * 65536L;
    v = t7 + c + 65535L;c = v >> 16;t7 = v - c * 65536L;
    v = t8 + c + 65535L;c = v >> 16;t8 = v - c * 65536L;
    v = t9 + c + 65535L;c = v >> 16;t9 = v - c * 65536L;
    v = t10 + c + 65535L;c = v >> 16;t10 = v - c * 65536L;
    v = t11 + c + 65535L;c = v >> 16;t11 = v - c * 65536L;
    v = t12 + c + 65535L;c = v >> 16;t12 = v - c * 65536L;
    v = t13 + c + 65535L;c = v >> 16;t13 = v - c * 65536L;
    v = t14 + c + 65535L;c = v >> 16;t14 = v - c * 65536L;
    v = t15 + c + 65535L;c = v >> 16;t15 = v - c * 65536L;
    t0 += c - 1L + 37L * (c - 1L);
    

    c = 1L;
    v = t0 + c + 65535L;c = v >> 16;t0 = v - c * 65536L;
    v = t1 + c + 65535L;c = v >> 16;t1 = v - c * 65536L;
    v = t2 + c + 65535L;c = v >> 16;t2 = v - c * 65536L;
    v = t3 + c + 65535L;c = v >> 16;t3 = v - c * 65536L;
    v = t4 + c + 65535L;c = v >> 16;t4 = v - c * 65536L;
    v = t5 + c + 65535L;c = v >> 16;t5 = v - c * 65536L;
    v = t6 + c + 65535L;c = v >> 16;t6 = v - c * 65536L;
    v = t7 + c + 65535L;c = v >> 16;t7 = v - c * 65536L;
    v = t8 + c + 65535L;c = v >> 16;t8 = v - c * 65536L;
    v = t9 + c + 65535L;c = v >> 16;t9 = v - c * 65536L;
    v = t10 + c + 65535L;c = v >> 16;t10 = v - c * 65536L;
    v = t11 + c + 65535L;c = v >> 16;t11 = v - c * 65536L;
    v = t12 + c + 65535L;c = v >> 16;t12 = v - c * 65536L;
    v = t13 + c + 65535L;c = v >> 16;t13 = v - c * 65536L;
    v = t14 + c + 65535L;c = v >> 16;t14 = v - c * 65536L;
    v = t15 + c + 65535L;c = v >> 16;t15 = v - c * 65536L;
    t0 += c - 1L + 37L * (c - 1L);
    
    o[(0 + ooff)] = t0;
    o[(1 + ooff)] = t1;
    o[(2 + ooff)] = t2;
    o[(3 + ooff)] = t3;
    o[(4 + ooff)] = t4;
    o[(5 + ooff)] = t5;
    o[(6 + ooff)] = t6;
    o[(7 + ooff)] = t7;
    o[(8 + ooff)] = t8;
    o[(9 + ooff)] = t9;
    o[(10 + ooff)] = t10;
    o[(11 + ooff)] = t11;
    o[(12 + ooff)] = t12;
    o[(13 + ooff)] = t13;
    o[(14 + ooff)] = t14;
    o[(15 + ooff)] = t15;
  }
  


  private static void S(long[] o, long[] a)
  {
    S(o, 0, a, 0);
  }
  

  private static void S(long[] o, int ooff, long[] a, int aoff)
  {
    M(o, ooff, a, aoff, a, aoff);
  }
  


  private static void inv25519(long[] o, int ooff, long[] i, int ioff)
  {
    long[] c = new long[16];
    
    for (int a = 0; a < 16; a++) c[a] = i[(a + ioff)];
    for (a = 253; a >= 0; a--) {
      S(c, 0, c, 0);
      if ((a != 2) && (a != 4)) M(c, 0, c, 0, i, ioff);
    }
    for (a = 0; a < 16; a++) o[(a + ooff)] = c[a];
  }
  
  private static void pow2523(long[] o, long[] i)
  {
    long[] c = new long[16];
    

    for (int a = 0; a < 16; a++) { c[a] = i[a];
    }
    for (a = 250; a >= 0; a--) {
      S(c, 0, c, 0);
      if (a != 1) { M(c, 0, c, 0, i, 0);
      }
    }
    for (a = 0; a < 16; a++) o[a] = c[a];
  }
  
  public static int crypto_scalarmult(byte[] q, byte[] n, byte[] p)
  {
    byte[] z = new byte[32];
    long[] x = new long[80];
    
    long[] a = new long[16];long[] b = new long[16];long[] c = new long[16];
    long[] d = new long[16];long[] e = new long[16];long[] f = new long[16];
    for (int i = 0; i < 31; i++) z[i] = n[i];
    z[31] = ((byte)((n[31] & 0x7F | 0x40) & 0xFF)); int 
      tmp92_91 = 0; byte[] tmp92_90 = z;tmp92_90[tmp92_91] = ((byte)(tmp92_90[tmp92_91] & 0xF8));
    unpack25519(x, p);
    for (i = 0; i < 16; i++) {
      b[i] = x[i]; long 
        tmp141_140 = (c[i] = 0L);a[i] = tmp141_140;d[i] = tmp141_140;
    }
    long tmp157_156 = 1L;d[0] = tmp157_156;a[0] = tmp157_156;
    for (i = 254; i >= 0; i--) {
      int r = z[(i >>> 3)] >>> (i & 0x7) & 0x1;
      sel25519(a, b, r);
      sel25519(c, d, r);
      A(e, a, c);
      Z(a, a, c);
      A(c, b, d);
      Z(b, b, d);
      S(d, e);
      S(f, a);
      M(a, c, a);
      M(c, b, e);
      A(e, a, c);
      Z(a, a, c);
      S(b, a);
      Z(c, d, f);
      M(a, c, _121665);
      A(a, a, d);
      M(c, c, a);
      M(a, d, f);
      M(d, b, x);
      S(b, e);
      sel25519(a, b, r);
      sel25519(c, d, r);
    }
    for (i = 0; i < 16; i++) {
      x[(i + 16)] = a[i];
      x[(i + 32)] = c[i];
      x[(i + 48)] = b[i];
      x[(i + 64)] = d[i];
    }
    inv25519(x, 32, x, 32);
    M(x, 16, x, 16, x, 32);
    pack25519(q, x, 16);
    
    return 0;
  }
  
  public static int crypto_scalarmult_base(byte[] q, byte[] n)
  {
    return crypto_scalarmult(q, n, _9);
  }
  
  public static int crypto_box_keypair(byte[] y, byte[] x)
  {
    randombytes(x, 32);
    return crypto_scalarmult_base(y, x);
  }
  
  public static int crypto_box_beforenm(byte[] k, byte[] y, byte[] x)
  {
    byte[] s = new byte[32];
    crypto_scalarmult(s, x, y);
    












    return crypto_core_hsalsa20(k, _0, s, sigma);
  }
  
  public static int crypto_box_afternm(byte[] c, byte[] m, int d, byte[] n, byte[] k)
  {
    return crypto_secretbox(c, m, d, n, k);
  }
  
  public static int crypto_box_open_afternm(byte[] m, byte[] c, int d, byte[] n, byte[] k)
  {
    return crypto_secretbox_open(m, c, d, n, k);
  }
  
  public static int crypto_box(byte[] c, byte[] m, int d, byte[] n, byte[] y, byte[] x)
  {
    byte[] k = new byte[32];
    


    crypto_box_beforenm(k, y, x);
    return crypto_box_afternm(c, m, d, n, k);
  }
  
  public static int crypto_box_open(byte[] m, byte[] c, int d, byte[] n, byte[] y, byte[] x)
  {
    byte[] k = new byte[32];
    crypto_box_beforenm(k, y, x);
    return crypto_box_open_afternm(m, c, d, n, k);
  }
  
  private static final long[] K = { 4794697086780616226L, 8158064640168781261L, -5349999486874862801L, -1606136188198331460L, 4131703408338449720L, 6480981068601479193L, -7908458776815382629L, -6116909921290321640L, -2880145864133508542L, 1334009975649890238L, 2608012711638119052L, 6128411473006802146L, 8268148722764581231L, -9160688886553864527L, -7215885187991268811L, -4495734319001033068L, -1973867731355612462L, -1171420211273849373L, 1135362057144423861L, 2597628984639134821L, 3308224258029322869L, 5365058923640841347L, 6679025012923562964L, 8573033837759648693L, -7476448914759557205L, -6327057829258317296L, -5763719355590565569L, -4658551843659510044L, -4116276920077217854L, -3051310485924567259L, 489312712824947311L, 1452737877330783856L, 2861767655752347644L, 3322285676063803686L, 5560940570517711597L, 5996557281743188959L, 7280758554555802590L, 8532644243296465576L, -9096487096722542874L, -7894198246740708037L, -6719396339535248540L, -6333637450476146687L, -4446306890439682159L, -4076793802049405392L, -3345356375505022440L, -2983346525034927856L, -860691631967231958L, 1182934255886127544L, 1847814050463011016L, 2177327727835720531L, 2830643537854262169L, 3796741975233480872L, 4115178125766777443L, 5681478168544905931L, 6601373596472566643L, 7507060721942968483L, 8399075790359081724L, 8693463985226723168L, -8878714635349349518L, -8302665154208450068L, -8016688836872298968L, -6606660893046293015L, -4685533653050689259L, -4147400797238176981L, -3880063495543823972L, -3348786107499101689L, -1523767162380948706L, -757361751448694408L, 500013540394364858L, 748580250866718886L, 1242879168328830382L, 1977374033974150939L, 2944078676154940804L, 3659926193048069267L, 4368137639120453308L, 4836135668995329356L, 5532061633213252278L, 6448918945643986474L, 6902733635092675308L, 7801388544844847127L };
  

























  private static int crypto_hashblocks_hl(int[] hh, int[] hl, byte[] m, int moff, int n)
  {
    int[] wh = new int[16];int[] wl = new int[16];
    



    int ah0 = hh[0];
    int ah1 = hh[1];
    int ah2 = hh[2];
    int ah3 = hh[3];
    int ah4 = hh[4];
    int ah5 = hh[5];
    int ah6 = hh[6];
    int ah7 = hh[7];
    
    int al0 = hl[0];
    int al1 = hl[1];
    int al2 = hl[2];
    int al3 = hl[3];
    int al4 = hl[4];
    int al5 = hl[5];
    int al6 = hl[6];
    int al7 = hl[7];
    
    int pos = 0;
    while (n >= 128) {
      for (int i = 0; i < 16; i++) {
        int j = 8 * i + pos;
        wh[i] = ((m[(j + 0 + moff)] & 0xFF) << 24 | (m[(j + 1 + moff)] & 0xFF) << 16 | (m[(j + 2 + moff)] & 0xFF) << 8 | (m[(j + 3 + moff)] & 0xFF) << 0);
        wl[i] = ((m[(j + 4 + moff)] & 0xFF) << 24 | (m[(j + 5 + moff)] & 0xFF) << 16 | (m[(j + 6 + moff)] & 0xFF) << 8 | (m[(j + 7 + moff)] & 0xFF) << 0);
      }
      for (i = 0; i < 80; i++) {
        int bh0 = ah0;
        int bh1 = ah1;
        int bh2 = ah2;
        int bh3 = ah3;
        int bh4 = ah4;
        int bh5 = ah5;
        int bh6 = ah6;
        int bh7 = ah7;
        
        int bl0 = al0;
        int bl1 = al1;
        int bl2 = al2;
        int bl3 = al3;
        int bl4 = al4;
        int bl5 = al5;
        int bl6 = al6;
        int bl7 = al7;
        

        int h = ah7;
        int l = al7;
        
        int a = l & 0xFFFF;int b = l >>> 16;
        int c = h & 0xFFFF;int d = h >>> 16;
        

        h = (ah4 >>> 14 | al4 << 18) ^ (ah4 >>> 18 | al4 << 14) ^ (al4 >>> 9 | ah4 << 23);
        l = (al4 >>> 14 | ah4 << 18) ^ (al4 >>> 18 | ah4 << 14) ^ (ah4 >>> 9 | al4 << 23);
        
        a += (l & 0xFFFF);b += (l >>> 16);
        c += (h & 0xFFFF);d += (h >>> 16);
        

        h = ah4 & ah5 ^ (ah4 ^ 0xFFFFFFFF) & ah6;
        l = al4 & al5 ^ (al4 ^ 0xFFFFFFFF) & al6;
        
        a += (l & 0xFFFF);b += (l >>> 16);
        c += (h & 0xFFFF);d += (h >>> 16);
        



        h = (int)(K[i] >>> 32 & 0xFFFFFFFFFFFFFFFF);
        l = (int)(K[i] >>> 0 & 0xFFFFFFFFFFFFFFFF);
        


        a += (l & 0xFFFF);b += (l >>> 16);
        c += (h & 0xFFFF);d += (h >>> 16);
        

        h = wh[(i % 16)];
        l = wl[(i % 16)];
        
        a += (l & 0xFFFF);b += (l >>> 16);
        c += (h & 0xFFFF);d += (h >>> 16);
        
        b += (a >>> 16);
        c += (b >>> 16);
        d += (c >>> 16);
        
        int th = c & 0xFFFF | d << 16;
        int tl = a & 0xFFFF | b << 16;
        

        h = th;
        l = tl;
        
        a = l & 0xFFFF;b = l >>> 16;
        c = h & 0xFFFF;d = h >>> 16;
        

        h = (ah0 >>> 28 | al0 << 4) ^ (al0 >>> 2 | ah0 << 30) ^ (al0 >>> 7 | ah0 << 25);
        l = (al0 >>> 28 | ah0 << 4) ^ (ah0 >>> 2 | al0 << 30) ^ (ah0 >>> 7 | al0 << 25);
        
        a += (l & 0xFFFF);b += (l >>> 16);
        c += (h & 0xFFFF);d += (h >>> 16);
        

        h = ah0 & ah1 ^ ah0 & ah2 ^ ah1 & ah2;
        l = al0 & al1 ^ al0 & al2 ^ al1 & al2;
        
        a += (l & 0xFFFF);b += (l >>> 16);
        c += (h & 0xFFFF);d += (h >>> 16);
        
        b += (a >>> 16);
        c += (b >>> 16);
        d += (c >>> 16);
        
        bh7 = c & 0xFFFF | d << 16;
        bl7 = a & 0xFFFF | b << 16;
        

        h = bh3;
        l = bl3;
        
        a = l & 0xFFFF;b = l >>> 16;
        c = h & 0xFFFF;d = h >>> 16;
        
        h = th;
        l = tl;
        
        a += (l & 0xFFFF);b += (l >>> 16);
        c += (h & 0xFFFF);d += (h >>> 16);
        
        b += (a >>> 16);
        c += (b >>> 16);
        d += (c >>> 16);
        
        bh3 = c & 0xFFFF | d << 16;
        bl3 = a & 0xFFFF | b << 16;
        
        ah1 = bh0;
        ah2 = bh1;
        ah3 = bh2;
        ah4 = bh3;
        ah5 = bh4;
        ah6 = bh5;
        ah7 = bh6;
        ah0 = bh7;
        
        al1 = bl0;
        al2 = bl1;
        al3 = bl2;
        al4 = bl3;
        al5 = bl4;
        al6 = bl5;
        al7 = bl6;
        al0 = bl7;
        
        if (i % 16 == 15) {
          for (int j = 0; j < 16; j++)
          {
            h = wh[j];
            l = wl[j];
            
            a = l & 0xFFFF;b = l >>> 16;
            c = h & 0xFFFF;d = h >>> 16;
            
            h = wh[((j + 9) % 16)];
            l = wl[((j + 9) % 16)];
            
            a += (l & 0xFFFF);b += (l >>> 16);
            c += (h & 0xFFFF);d += (h >>> 16);
            

            th = wh[((j + 1) % 16)];
            tl = wl[((j + 1) % 16)];
            h = (th >>> 1 | tl << 31) ^ (th >>> 8 | tl << 24) ^ th >>> 7;
            l = (tl >>> 1 | th << 31) ^ (tl >>> 8 | th << 24) ^ (tl >>> 7 | th << 25);
            
            a += (l & 0xFFFF);b += (l >>> 16);
            c += (h & 0xFFFF);d += (h >>> 16);
            

            th = wh[((j + 14) % 16)];
            tl = wl[((j + 14) % 16)];
            h = (th >>> 19 | tl << 13) ^ (tl >>> 29 | th << 3) ^ th >>> 6;
            l = (tl >>> 19 | th << 13) ^ (th >>> 29 | tl << 3) ^ (tl >>> 6 | th << 26);
            
            a += (l & 0xFFFF);b += (l >>> 16);
            c += (h & 0xFFFF);d += (h >>> 16);
            
            b += (a >>> 16);
            c += (b >>> 16);
            d += (c >>> 16);
            
            wh[j] = (c & 0xFFFF | d << 16);
            wl[j] = (a & 0xFFFF | b << 16);
          }
        }
      }
      

      int h = ah0;
      int l = al0;
      
      int a = l & 0xFFFF;int b = l >>> 16;
      int c = h & 0xFFFF;int d = h >>> 16;
      
      h = hh[0];
      l = hl[0];
      
      a += (l & 0xFFFF);b += (l >>> 16);
      c += (h & 0xFFFF);d += (h >>> 16);
      
      b += (a >>> 16);
      c += (b >>> 16);
      d += (c >>> 16); int 
      
        tmp1827_1826 = (c & 0xFFFF | d << 16);ah0 = tmp1827_1826;hh[0] = tmp1827_1826; int 
        tmp1844_1843 = (a & 0xFFFF | b << 16);al0 = tmp1844_1843;hl[0] = tmp1844_1843;
      
      h = ah1;
      l = al1;
      
      a = l & 0xFFFF;b = l >>> 16;
      c = h & 0xFFFF;d = h >>> 16;
      
      h = hh[1];
      l = hl[1];
      
      a += (l & 0xFFFF);b += (l >>> 16);
      c += (h & 0xFFFF);d += (h >>> 16);
      
      b += (a >>> 16);
      c += (b >>> 16);
      d += (c >>> 16); int 
      
        tmp1977_1976 = (c & 0xFFFF | d << 16);ah1 = tmp1977_1976;hh[1] = tmp1977_1976; int 
        tmp1994_1993 = (a & 0xFFFF | b << 16);al1 = tmp1994_1993;hl[1] = tmp1994_1993;
      
      h = ah2;
      l = al2;
      
      a = l & 0xFFFF;b = l >>> 16;
      c = h & 0xFFFF;d = h >>> 16;
      
      h = hh[2];
      l = hl[2];
      
      a += (l & 0xFFFF);b += (l >>> 16);
      c += (h & 0xFFFF);d += (h >>> 16);
      
      b += (a >>> 16);
      c += (b >>> 16);
      d += (c >>> 16); int 
      
        tmp2127_2126 = (c & 0xFFFF | d << 16);ah2 = tmp2127_2126;hh[2] = tmp2127_2126; int 
        tmp2144_2143 = (a & 0xFFFF | b << 16);al2 = tmp2144_2143;hl[2] = tmp2144_2143;
      
      h = ah3;
      l = al3;
      
      a = l & 0xFFFF;b = l >>> 16;
      c = h & 0xFFFF;d = h >>> 16;
      
      h = hh[3];
      l = hl[3];
      
      a += (l & 0xFFFF);b += (l >>> 16);
      c += (h & 0xFFFF);d += (h >>> 16);
      
      b += (a >>> 16);
      c += (b >>> 16);
      d += (c >>> 16); int 
      
        tmp2277_2276 = (c & 0xFFFF | d << 16);ah3 = tmp2277_2276;hh[3] = tmp2277_2276; int 
        tmp2294_2293 = (a & 0xFFFF | b << 16);al3 = tmp2294_2293;hl[3] = tmp2294_2293;
      
      h = ah4;
      l = al4;
      
      a = l & 0xFFFF;b = l >>> 16;
      c = h & 0xFFFF;d = h >>> 16;
      
      h = hh[4];
      l = hl[4];
      
      a += (l & 0xFFFF);b += (l >>> 16);
      c += (h & 0xFFFF);d += (h >>> 16);
      
      b += (a >>> 16);
      c += (b >>> 16);
      d += (c >>> 16); int 
      
        tmp2427_2426 = (c & 0xFFFF | d << 16);ah4 = tmp2427_2426;hh[4] = tmp2427_2426; int 
        tmp2444_2443 = (a & 0xFFFF | b << 16);al4 = tmp2444_2443;hl[4] = tmp2444_2443;
      
      h = ah5;
      l = al5;
      
      a = l & 0xFFFF;b = l >>> 16;
      c = h & 0xFFFF;d = h >>> 16;
      
      h = hh[5];
      l = hl[5];
      
      a += (l & 0xFFFF);b += (l >>> 16);
      c += (h & 0xFFFF);d += (h >>> 16);
      
      b += (a >>> 16);
      c += (b >>> 16);
      d += (c >>> 16); int 
      
        tmp2577_2576 = (c & 0xFFFF | d << 16);ah5 = tmp2577_2576;hh[5] = tmp2577_2576; int 
        tmp2594_2593 = (a & 0xFFFF | b << 16);al5 = tmp2594_2593;hl[5] = tmp2594_2593;
      
      h = ah6;
      l = al6;
      
      a = l & 0xFFFF;b = l >>> 16;
      c = h & 0xFFFF;d = h >>> 16;
      
      h = hh[6];
      l = hl[6];
      
      a += (l & 0xFFFF);b += (l >>> 16);
      c += (h & 0xFFFF);d += (h >>> 16);
      
      b += (a >>> 16);
      c += (b >>> 16);
      d += (c >>> 16); int 
      
        tmp2730_2729 = (c & 0xFFFF | d << 16);ah6 = tmp2730_2729;hh[6] = tmp2730_2729; int 
        tmp2748_2747 = (a & 0xFFFF | b << 16);al6 = tmp2748_2747;hl[6] = tmp2748_2747;
      
      h = ah7;
      l = al7;
      
      a = l & 0xFFFF;b = l >>> 16;
      c = h & 0xFFFF;d = h >>> 16;
      
      h = hh[7];
      l = hl[7];
      
      a += (l & 0xFFFF);b += (l >>> 16);
      c += (h & 0xFFFF);d += (h >>> 16);
      
      b += (a >>> 16);
      c += (b >>> 16);
      d += (c >>> 16); int 
      
        tmp2884_2883 = (c & 0xFFFF | d << 16);ah7 = tmp2884_2883;hh[7] = tmp2884_2883; int 
        tmp2902_2901 = (a & 0xFFFF | b << 16);al7 = tmp2902_2901;hl[7] = tmp2902_2901;
      
      pos += 128;
      n -= 128;
    }
    








    return n;
  }
  


  public static int crypto_hash(byte[] out, byte[] m, int moff, int n)
  {
    int[] hh = new int[8];
    int[] hl = new int[8];
    byte[] x = new byte['Ā'];
    int b = n;
    

    hh[0] = 1779033703;
    hh[1] = -1150833019;
    hh[2] = 1013904242;
    hh[3] = -1521486534;
    hh[4] = 1359893119;
    hh[5] = -1694144372;
    hh[6] = 528734635;
    hh[7] = 1541459225;
    
    hl[0] = -205731576;
    hl[1] = -2067093701;
    hl[2] = -23791573;
    hl[3] = 1595750129;
    hl[4] = -1377402159;
    hl[5] = 725511199;
    hl[6] = -79577749;
    hl[7] = 327033209;
    
    if (n >= 128) {
      crypto_hashblocks_hl(hh, hl, m, moff, n);
      n %= 128;
    }
    
    for (int i = 0; i < n; i++) x[i] = m[(b - n + i + moff)];
    x[n] = Byte.MIN_VALUE;
    
    n = 256 - '' * (n < 112 ? 1 : 0);
    x[(n - 9)] = 0;
    
    ts64(x, n - 8, b << 3);
    
    crypto_hashblocks_hl(hh, hl, x, 0, n);
    
    for (i = 0; i < 8; i++) {
      long u = hh[i];u <<= 32;u |= hl[i] & 0xFFFFFFFF;
      ts64(out, 8 * i, u);
    }
    
    return 0;
  }
  
  public static int crypto_hash(byte[] out, byte[] m) { return crypto_hash(out, m, 0, m != null ? m.length : 0); }
  



  private static void add(long[][] p, long[][] q)
  {
    long[] a = new long[16];
    long[] b = new long[16];
    long[] c = new long[16];
    long[] d = new long[16];
    long[] t = new long[16];
    long[] e = new long[16];
    long[] f = new long[16];
    long[] g = new long[16];
    long[] h = new long[16];
    

    long[] p0 = p[0];
    long[] p1 = p[1];
    long[] p2 = p[2];
    long[] p3 = p[3];
    
    long[] q0 = q[0];
    long[] q1 = q[1];
    long[] q2 = q[2];
    long[] q3 = q[3];
    
    Z(a, 0, p1, 0, p0, 0);
    Z(t, 0, q1, 0, q0, 0);
    M(a, 0, a, 0, t, 0);
    A(b, 0, p0, 0, p1, 0);
    A(t, 0, q0, 0, q1, 0);
    M(b, 0, b, 0, t, 0);
    M(c, 0, p3, 0, q3, 0);
    M(c, 0, c, 0, D2, 0);
    M(d, 0, p2, 0, q2, 0);
    
    A(d, 0, d, 0, d, 0);
    Z(e, 0, b, 0, a, 0);
    Z(f, 0, d, 0, c, 0);
    A(g, 0, d, 0, c, 0);
    A(h, 0, b, 0, a, 0);
    
    M(p0, 0, e, 0, f, 0);
    M(p1, 0, h, 0, g, 0);
    M(p2, 0, g, 0, f, 0);
    M(p3, 0, e, 0, h, 0);
  }
  


  private static void cswap(long[][] p, long[][] q, byte b)
  {
    for (int i = 0; i < 4; i++) {
      sel25519(p[i], 0, q[i], 0, b);
    }
  }
  
  private static void pack(byte[] r, long[][] p) {
    long[] tx = new long[16];
    long[] ty = new long[16];
    long[] zi = new long[16];
    
    inv25519(zi, 0, p[2], 0);
    
    M(tx, 0, p[0], 0, zi, 0);
    M(ty, 0, p[1], 0, zi, 0);
    
    pack25519(r, ty, 0);
    
    r[31] = ((byte)(r[31] ^ par25519(tx, 0) << 7));
  }
  


  private static void scalarmult(long[][] p, long[][] q, byte[] s, int soff)
  {
    set25519(p[0], gf0);
    set25519(p[1], gf1);
    set25519(p[2], gf1);
    set25519(p[3], gf0);
    
    for (int i = 255; i >= 0; i--) {
      byte b = (byte)(s[(i / 8 + soff)] >>> (i & 0x7) & 0x1);
      
      cswap(p, q, b);
      add(q, p);
      add(p, p);
      cswap(p, q, b);
    }
  }
  




  private static void scalarbase(long[][] p, byte[] s, int soff)
  {
    long[][] q = new long[4][];
    
    q[0] = new long[16];
    q[1] = new long[16];
    q[2] = new long[16];
    q[3] = new long[16];
    
    set25519(q[0], X);
    set25519(q[1], Y);
    set25519(q[2], gf1);
    M(q[3], 0, X, 0, Y, 0);
    scalarmult(p, q, s, soff);
  }
  
  public static int crypto_sign_keypair(byte[] pk, byte[] sk, boolean seeded) {
    byte[] d = new byte[64];
    long[][] p = new long[4][];
    
    p[0] = new long[16];
    p[1] = new long[16];
    p[2] = new long[16];
    p[3] = new long[16];
    


    if (!seeded) randombytes(sk, 32);
    crypto_hash(d, sk, 0, 32); int 
      tmp65_64 = 0; byte[] tmp65_63 = d;tmp65_63[tmp65_64] = ((byte)(tmp65_63[tmp65_64] & 0xF8)); byte[] 
      tmp76_73 = d;tmp76_73[31] = ((byte)(tmp76_73[31] & 0x7F)); byte[] 
      tmp86_83 = d;tmp86_83[31] = ((byte)(tmp86_83[31] | 0x40));
    
    scalarbase(p, d, 0);
    pack(pk, p);
    
    for (int i = 0; i < 32; i++) sk[(i + 32)] = pk[i];
    return 0;
  }
  
  private static final long[] L = { 237L, 211L, 245L, 92L, 26L, 99L, 18L, 88L, 214L, 156L, 247L, 162L, 222L, 249L, 222L, 20L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 16L };
  








  private static void modL(byte[] r, int roff, long[] x)
  {
    for (int i = 63; i >= 32; i--) {
      long carry = 0L;
      for (int j = i - 32; j < i - 12; j++) {
        x[j] += carry - 16L * x[i] * L[(j - (i - 32))];
        carry = x[j] + 128L >> 8;
        x[j] -= (carry << 8);
      }
      x[j] += carry;
      x[i] = 0L;
    }
    long carry = 0L;
    
    for (int j = 0; j < 32; j++) {
      x[j] += carry - (x[31] >> 4) * L[j];
      carry = x[j] >> 8;
      x[j] &= 0xFF;
    }
    
    for (j = 0; j < 32; j++) { x[j] -= carry * L[j];
    }
    for (i = 0; i < 32; i++) {
      x[(i + 1)] += (x[i] >> 8);
      r[(i + roff)] = ((byte)(int)(x[i] & 0xFF));
    }
  }
  
  private static void reduce(byte[] r)
  {
    long[] x = new long[64];
    

    for (int i = 0; i < 64; i++) { x[i] = (r[i] & 0xFF);
    }
    for (i = 0; i < 64; i++) { r[i] = 0;
    }
    modL(r, 0, x);
  }
  


  public static int crypto_sign(byte[] sm, long dummy, byte[] m, int moff, int n, byte[] sk)
  {
    byte[] d = new byte[64];byte[] h = new byte[64];byte[] r = new byte[64];
    

    long[] x = new long[64];
    
    long[][] p = new long[4][];
    p[0] = new long[16];
    p[1] = new long[16];
    p[2] = new long[16];
    p[3] = new long[16];
    
    crypto_hash(d, sk, 0, 32); int 
      tmp76_75 = 0; byte[] tmp76_73 = d;tmp76_73[tmp76_75] = ((byte)(tmp76_73[tmp76_75] & 0xF8)); byte[] 
      tmp88_84 = d;tmp88_84[31] = ((byte)(tmp88_84[31] & 0x7F)); byte[] 
      tmp99_95 = d;tmp99_95[31] = ((byte)(tmp99_95[31] | 0x40));
    


    for (int i = 0; i < n; i++) { sm[(64 + i)] = m[(i + moff)];
    }
    for (i = 0; i < 32; i++) { sm[(32 + i)] = d[(32 + i)];
    }
    crypto_hash(r, sm, 32, n + 32);
    reduce(r);
    scalarbase(tmp76_75, r, 0);
    pack(sm, tmp76_75);
    
    for (i = 0; i < 32; i++) sm[(i + 32)] = sk[(i + 32)];
    crypto_hash(h, sm, 0, n + 64);
    reduce(h);
    
    for (i = 0; i < 64; i++) { x[i] = 0L;
    }
    for (i = 0; i < 32; i++) { x[i] = (r[i] & 0xFF);
    }
    for (i = 0; i < 32; i++) for (int j = 0; j < 32; j++) { x[(i + j)] += (h[i] & 0xFF) * (d[j] & 0xFF);
      }
    modL(sm, 32, x);
    
    return 0;
  }
  
  private static int unpackneg(long[][] r, byte[] p)
  {
    long[] t = new long[16];
    long[] chk = new long[16];
    long[] num = new long[16];
    long[] den = new long[16];
    long[] den2 = new long[16];
    long[] den4 = new long[16];
    long[] den6 = new long[16];
    
    set25519(r[2], gf1);
    unpack25519(r[1], p);
    S(num, r[1]);
    M(den, num, D);
    Z(num, num, r[2]);
    A(den, r[2], den);
    
    S(den2, den);
    S(den4, den2);
    M(den6, den4, den2);
    M(t, den6, num);
    M(t, t, den);
    
    pow2523(t, t);
    M(t, t, num);
    M(t, t, den);
    M(t, t, den);
    M(r[0], t, den);
    
    S(chk, r[0]);
    M(chk, chk, den);
    if (neq25519(chk, num) != 0) { M(r[0], r[0], I);
    }
    S(chk, r[0]);
    M(chk, chk, den);
    if (neq25519(chk, num) != 0) { return -1;
    }
    if (par25519(r[0]) == (p[31] & 0xFF) >>> 7) { Z(r[0], gf0, r[0]);
    }
    M(r[3], r[0], r[1]);
    
    return 0;
  }
  



  public static int crypto_sign_open(byte[] m, long dummy, byte[] sm, int smoff, int n, byte[] pk)
  {
    byte[] t = new byte[32];byte[] h = new byte[64];
    
    long[][] p = new long[4][];
    p[0] = new long[16];
    p[1] = new long[16];
    p[2] = new long[16];
    p[3] = new long[16];
    
    long[][] q = new long[4][];
    q[0] = new long[16];
    q[1] = new long[16];
    q[2] = new long[16];
    q[3] = new long[16];
    


    if (n < 64) { return -1;
    }
    if (unpackneg(q, pk) != 0) { return -1;
    }
    for (int i = 0; i < n; i++) { m[i] = sm[(i + smoff)];
    }
    for (i = 0; i < 32; i++) { m[(i + 32)] = pk[i];
    }
    crypto_hash(h, m, 0, n);
    
    reduce(h);
    scalarmult(p, q, h, 0);
    
    scalarbase(q, sm, 32 + smoff);
    add(p, q);
    pack(t, p);
    
    n -= 64;
    if (crypto_verify_32(sm, smoff, t, 0) != 0)
    {

      return -1;
    }
    




    return 0;
  }
  




  private static final SecureRandom jrandom = new SecureRandom();
  
  public static byte[] randombytes(byte[] x) {
    jrandom.nextBytes(x);
    return x;
  }
  
  public static byte[] randombytes(int len) {
    return randombytes(new byte[len]);
  }
  
  public static byte[] randombytes(byte[] x, int len) {
    byte[] b = randombytes(len);
    System.arraycopy(b, 0, x, 0, len);
    return x;
  }
  























  public static byte[] makeBoxNonce()
  {
    return randombytes(24);
  }
  
  public static byte[] makeSecretBoxNonce() {
    return randombytes(24);
  }
  
  public static String base64EncodeToString(byte[] b) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
  }
  
  public static byte[] base64Decode(String s)
  {
    return Base64.getUrlDecoder().decode(s);
  }
  
  public static String hexEncodeToString(byte[] raw)
  {
    String HEXES = "0123456789ABCDEF";
    StringBuilder hex = new StringBuilder(2 * raw.length);
    for (byte b : raw)
    {
      hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt(b & 0xF));
    }
    return hex.toString();
  }
  
  public static byte[] hexDecode(String s) {
    byte[] b = new byte[s.length() / 2];
    for (int i = 0; i < s.length(); i += 2)
    {
      b[(i / 2)] = ((byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16)));
    }
    return b;
  }
  
  public TweetNaclFast() {}
}
