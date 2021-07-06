package tomp2p.opuswrapper;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;










public abstract interface Opus
  extends Library
{
  public static final Opus INSTANCE = (Opus)Native.loadLibrary(System.getProperty("opus.lib"), Opus.class);
  
  public static final int OPUS_GET_LSB_DEPTH_REQUEST = 4037;
  
  public static final int OPUS_GET_APPLICATION_REQUEST = 4001;
  
  public static final int OPUS_GET_FORCE_CHANNELS_REQUEST = 4023;
  
  public static final int OPUS_GET_VBR_REQUEST = 4007;
  
  public static final int OPUS_GET_BANDWIDTH_REQUEST = 4009;
  
  public static final int OPUS_SET_BITRATE_REQUEST = 4002;
  
  public static final int OPUS_SET_BANDWIDTH_REQUEST = 4008;
  
  public static final int OPUS_SIGNAL_MUSIC = 3002;
  
  public static final int OPUS_RESET_STATE = 4028;
  
  public static final int OPUS_FRAMESIZE_2_5_MS = 5001;
  
  public static final int OPUS_GET_COMPLEXITY_REQUEST = 4011;
  
  public static final int OPUS_FRAMESIZE_40_MS = 5005;
  
  public static final int OPUS_SET_PACKET_LOSS_PERC_REQUEST = 4014;
  
  public static final int OPUS_GET_VBR_CONSTRAINT_REQUEST = 4021;
  
  public static final int OPUS_SET_INBAND_FEC_REQUEST = 4012;
  
  public static final int OPUS_APPLICATION_RESTRICTED_LOWDELAY = 2051;
  
  public static final int OPUS_BANDWIDTH_FULLBAND = 1105;
  
  public static final int OPUS_SET_VBR_REQUEST = 4006;
  
  public static final int OPUS_BANDWIDTH_SUPERWIDEBAND = 1104;
  
  public static final int OPUS_SET_FORCE_CHANNELS_REQUEST = 4022;
  
  public static final int OPUS_APPLICATION_VOIP = 2048;
  
  public static final int OPUS_SIGNAL_VOICE = 3001;
  
  public static final int OPUS_GET_FINAL_RANGE_REQUEST = 4031;
  
  public static final int OPUS_BUFFER_TOO_SMALL = -2;
  
  public static final int OPUS_SET_COMPLEXITY_REQUEST = 4010;
  
  public static final int OPUS_FRAMESIZE_ARG = 5000;
  
  public static final int OPUS_GET_LOOKAHEAD_REQUEST = 4027;
  
  public static final int OPUS_GET_INBAND_FEC_REQUEST = 4013;
  
  public static final int OPUS_BITRATE_MAX = -1;
  
  public static final int OPUS_FRAMESIZE_5_MS = 5002;
  
  public static final int OPUS_BAD_ARG = -1;
  
  public static final int OPUS_GET_PITCH_REQUEST = 4033;
  
  public static final int OPUS_SET_SIGNAL_REQUEST = 4024;
  
  public static final int OPUS_FRAMESIZE_20_MS = 5004;
  
  public static final int OPUS_APPLICATION_AUDIO = 2049;
  
  public static final int OPUS_GET_DTX_REQUEST = 4017;
  
  public static final int OPUS_FRAMESIZE_10_MS = 5003;
  
  public static final int OPUS_SET_LSB_DEPTH_REQUEST = 4036;
  
  public static final int OPUS_UNIMPLEMENTED = -5;
  
  public static final int OPUS_GET_PACKET_LOSS_PERC_REQUEST = 4015;
  
  public static final int OPUS_INVALID_STATE = -6;
  
  public static final int OPUS_SET_EXPERT_FRAME_DURATION_REQUEST = 4040;
  
  public static final int OPUS_FRAMESIZE_60_MS = 5006;
  
  public static final int OPUS_GET_BITRATE_REQUEST = 4003;
  
  public static final int OPUS_INTERNAL_ERROR = -3;
  
  public static final int OPUS_SET_MAX_BANDWIDTH_REQUEST = 4004;
  
  public static final int OPUS_SET_VBR_CONSTRAINT_REQUEST = 4020;
  
  public static final int OPUS_GET_MAX_BANDWIDTH_REQUEST = 4005;
  
  public static final int OPUS_BANDWIDTH_NARROWBAND = 1101;
  
  public static final int OPUS_SET_GAIN_REQUEST = 4034;
  
  public static final int OPUS_SET_PREDICTION_DISABLED_REQUEST = 4042;
  
  public static final int OPUS_SET_APPLICATION_REQUEST = 4000;
  
  public static final int OPUS_SET_DTX_REQUEST = 4016;
  
  public static final int OPUS_BANDWIDTH_MEDIUMBAND = 1102;
  
  public static final int OPUS_GET_SAMPLE_RATE_REQUEST = 4029;
  
  public static final int OPUS_GET_EXPERT_FRAME_DURATION_REQUEST = 4041;
  
  public static final int OPUS_AUTO = -1000;
  
  public static final int OPUS_GET_SIGNAL_REQUEST = 4025;
  
  public static final int OPUS_GET_LAST_PACKET_DURATION_REQUEST = 4039;
  
  public static final int OPUS_GET_PREDICTION_DISABLED_REQUEST = 4043;
  
  public static final int OPUS_GET_GAIN_REQUEST = 4045;
  
  public static final int OPUS_BANDWIDTH_WIDEBAND = 1103;
  
  public static final int OPUS_INVALID_PACKET = -4;
  public static final int OPUS_ALLOC_FAIL = -7;
  public static final int OPUS_OK = 0;
  public static final int OPUS_MULTISTREAM_GET_DECODER_STATE_REQUEST = 5122;
  public static final int OPUS_MULTISTREAM_GET_ENCODER_STATE_REQUEST = 5120;
  
  public abstract int opus_encoder_get_size(int paramInt);
  
  public abstract PointerByReference opus_encoder_create(int paramInt1, int paramInt2, int paramInt3, IntBuffer paramIntBuffer);
  
  public abstract int opus_encoder_init(PointerByReference paramPointerByReference, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract int opus_encode(PointerByReference paramPointerByReference, ShortBuffer paramShortBuffer, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2);
  
  public abstract int opus_encode(PointerByReference paramPointerByReference, ShortByReference paramShortByReference, int paramInt1, Pointer paramPointer, int paramInt2);
  
  public abstract int opus_encode_float(PointerByReference paramPointerByReference, float[] paramArrayOfFloat, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2);
  
  public abstract int opus_encode_float(PointerByReference paramPointerByReference, FloatByReference paramFloatByReference, int paramInt1, Pointer paramPointer, int paramInt2);
  
  public abstract void opus_encoder_destroy(PointerByReference paramPointerByReference);
  
  public abstract int opus_encoder_ctl(PointerByReference paramPointerByReference, int paramInt, Object... paramVarArgs);
  
  public abstract int opus_decoder_get_size(int paramInt);
  
  public abstract PointerByReference opus_decoder_create(int paramInt1, int paramInt2, IntBuffer paramIntBuffer);
  
  public abstract int opus_decoder_init(PointerByReference paramPointerByReference, int paramInt1, int paramInt2);
  
  public abstract int opus_decode(PointerByReference paramPointerByReference, byte[] paramArrayOfByte, int paramInt1, ShortBuffer paramShortBuffer, int paramInt2, int paramInt3);
  
  public abstract int opus_decode(PointerByReference paramPointerByReference, Pointer paramPointer, int paramInt1, ShortByReference paramShortByReference, int paramInt2, int paramInt3);
  
  public abstract int opus_decode_float(PointerByReference paramPointerByReference, byte[] paramArrayOfByte, int paramInt1, FloatBuffer paramFloatBuffer, int paramInt2, int paramInt3);
  
  public abstract int opus_decode_float(PointerByReference paramPointerByReference, Pointer paramPointer, int paramInt1, FloatByReference paramFloatByReference, int paramInt2, int paramInt3);
  
  public abstract int opus_decoder_ctl(PointerByReference paramPointerByReference, int paramInt, Object... paramVarArgs);
  
  public abstract void opus_decoder_destroy(PointerByReference paramPointerByReference);
  
  public abstract int opus_packet_parse(byte[] paramArrayOfByte1, int paramInt, ByteBuffer paramByteBuffer, byte[] paramArrayOfByte2, ShortBuffer paramShortBuffer, IntBuffer paramIntBuffer);
  
  public abstract int opus_packet_get_bandwidth(byte[] paramArrayOfByte);
  
  public abstract int opus_packet_get_samples_per_frame(byte[] paramArrayOfByte, int paramInt);
  
  public abstract int opus_packet_get_nb_channels(byte[] paramArrayOfByte);
  
  public abstract int opus_packet_get_nb_frames(byte[] paramArrayOfByte, int paramInt);
  
  public abstract int opus_packet_get_nb_samples(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract int opus_decoder_get_nb_samples(PointerByReference paramPointerByReference, byte[] paramArrayOfByte, int paramInt);
  
  public abstract int opus_decoder_get_nb_samples(PointerByReference paramPointerByReference, Pointer paramPointer, int paramInt);
  
  public abstract void opus_pcm_soft_clip(FloatBuffer paramFloatBuffer1, int paramInt1, int paramInt2, FloatBuffer paramFloatBuffer2);
  
  public abstract int opus_repacketizer_get_size();
  
  public abstract PointerByReference opus_repacketizer_init(PointerByReference paramPointerByReference);
  
  public abstract PointerByReference opus_repacketizer_create();
  
  public abstract void opus_repacketizer_destroy(PointerByReference paramPointerByReference);
  
  public abstract int opus_repacketizer_cat(PointerByReference paramPointerByReference, byte[] paramArrayOfByte, int paramInt);
  
  public abstract int opus_repacketizer_cat(PointerByReference paramPointerByReference, Pointer paramPointer, int paramInt);
  
  public abstract int opus_repacketizer_out_range(PointerByReference paramPointerByReference, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer, int paramInt3);
  
  public abstract int opus_repacketizer_out_range(PointerByReference paramPointerByReference, int paramInt1, int paramInt2, Pointer paramPointer, int paramInt3);
  
  public abstract int opus_repacketizer_get_nb_frames(PointerByReference paramPointerByReference);
  
  public abstract int opus_repacketizer_out(PointerByReference paramPointerByReference, ByteBuffer paramByteBuffer, int paramInt);
  
  public abstract int opus_repacketizer_out(PointerByReference paramPointerByReference, Pointer paramPointer, int paramInt);
  
  public abstract int opus_packet_pad(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2);
  
  public abstract int opus_packet_unpad(ByteBuffer paramByteBuffer, int paramInt);
  
  public abstract int opus_multistream_packet_pad(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract int opus_multistream_packet_unpad(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2);
  
  public abstract String opus_strerror(int paramInt);
  
  public abstract String opus_get_version_string();
  
  public static class OpusDecoder
    extends PointerType
  {
    public OpusDecoder(Pointer address)
    {
      super();
    }
    
    public OpusDecoder() {}
  }
  
  public static class OpusEncoder extends PointerType {
    public OpusEncoder(Pointer address) {
      super();
    }
    
    public OpusEncoder() {}
  }
  
  public static class OpusRepacketizer extends PointerType {
    public OpusRepacketizer(Pointer address) {
      super();
    }
    

    public OpusRepacketizer() {}
  }
  

  public abstract int opus_multistream_encoder_get_size(int paramInt1, int paramInt2);
  

  public abstract int opus_multistream_surround_encoder_get_size(int paramInt1, int paramInt2);
  

  public abstract PointerByReference opus_multistream_encoder_create(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, IntBuffer paramIntBuffer);
  

  public abstract PointerByReference opus_multistream_surround_encoder_create(int paramInt1, int paramInt2, int paramInt3, IntBuffer paramIntBuffer1, IntBuffer paramIntBuffer2, ByteBuffer paramByteBuffer, int paramInt4, IntBuffer paramIntBuffer3);
  

  public abstract int opus_multistream_encoder_init(PointerByReference paramPointerByReference, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5);
  
  public abstract int opus_multistream_encoder_init(PointerByReference paramPointerByReference, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Pointer paramPointer, int paramInt5);
  
  public abstract int opus_multistream_surround_encoder_init(PointerByReference paramPointerByReference, int paramInt1, int paramInt2, int paramInt3, IntBuffer paramIntBuffer1, IntBuffer paramIntBuffer2, ByteBuffer paramByteBuffer, int paramInt4);
  
  public abstract int opus_multistream_surround_encoder_init(PointerByReference paramPointerByReference, int paramInt1, int paramInt2, int paramInt3, IntByReference paramIntByReference1, IntByReference paramIntByReference2, Pointer paramPointer, int paramInt4);
  
  public abstract int opus_multistream_encode(PointerByReference paramPointerByReference, ShortBuffer paramShortBuffer, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2);
  
  public abstract int opus_multistream_encode(PointerByReference paramPointerByReference, ShortByReference paramShortByReference, int paramInt1, Pointer paramPointer, int paramInt2);
  
  public abstract int opus_multistream_encode_float(PointerByReference paramPointerByReference, float[] paramArrayOfFloat, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2);
  
  public abstract int opus_multistream_encode_float(PointerByReference paramPointerByReference, FloatByReference paramFloatByReference, int paramInt1, Pointer paramPointer, int paramInt2);
  
  public abstract void opus_multistream_encoder_destroy(PointerByReference paramPointerByReference);
  
  public abstract int opus_multistream_encoder_ctl(PointerByReference paramPointerByReference, int paramInt, Object... paramVarArgs);
  
  public abstract int opus_multistream_decoder_get_size(int paramInt1, int paramInt2);
  
  public abstract PointerByReference opus_multistream_decoder_create(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, IntBuffer paramIntBuffer);
  
  public abstract int opus_multistream_decoder_init(PointerByReference paramPointerByReference, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte);
  
  public abstract int opus_multistream_decoder_init(PointerByReference paramPointerByReference, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Pointer paramPointer);
  
  public abstract int opus_multistream_decode(PointerByReference paramPointerByReference, byte[] paramArrayOfByte, int paramInt1, ShortBuffer paramShortBuffer, int paramInt2, int paramInt3);
  
  public abstract int opus_multistream_decode(PointerByReference paramPointerByReference, Pointer paramPointer, int paramInt1, ShortByReference paramShortByReference, int paramInt2, int paramInt3);
  
  public abstract int opus_multistream_decode_float(PointerByReference paramPointerByReference, byte[] paramArrayOfByte, int paramInt1, FloatBuffer paramFloatBuffer, int paramInt2, int paramInt3);
  
  public abstract int opus_multistream_decode_float(PointerByReference paramPointerByReference, Pointer paramPointer, int paramInt1, FloatByReference paramFloatByReference, int paramInt2, int paramInt3);
  
  public static class OpusMSEncoder
    extends PointerType
  {
    public OpusMSEncoder(Pointer address)
    {
      super();
    }
    
    public OpusMSEncoder() {}
  }
  
  public static class OpusMSDecoder
    extends PointerType {
    public OpusMSDecoder(Pointer address) { super(); }
    
    public OpusMSDecoder() {} }
  
  public abstract int opus_multistream_decoder_ctl(PointerByReference paramPointerByReference, int paramInt, Object... paramVarArgs);
  
  public abstract void opus_multistream_decoder_destroy(PointerByReference paramPointerByReference);
  
  public abstract PointerByReference opus_custom_mode_create(int paramInt1, int paramInt2, IntBuffer paramIntBuffer);
  
  public abstract void opus_custom_mode_destroy(PointerByReference paramPointerByReference);
  
  public abstract int opus_custom_encoder_get_size(PointerByReference paramPointerByReference, int paramInt);
  
  public abstract PointerByReference opus_custom_encoder_create(PointerByReference paramPointerByReference, int paramInt, IntBuffer paramIntBuffer);
  
  public abstract PointerByReference opus_custom_encoder_create(PointerByReference paramPointerByReference, int paramInt, IntByReference paramIntByReference);
  
  public abstract void opus_custom_encoder_destroy(PointerByReference paramPointerByReference);
  
  public abstract int opus_custom_encode_float(PointerByReference paramPointerByReference, float[] paramArrayOfFloat, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2);
  
  public abstract int opus_custom_encode_float(PointerByReference paramPointerByReference, FloatByReference paramFloatByReference, int paramInt1, Pointer paramPointer, int paramInt2);
  
  public abstract int opus_custom_encode(PointerByReference paramPointerByReference, ShortBuffer paramShortBuffer, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2);
  
  public abstract int opus_custom_encode(PointerByReference paramPointerByReference, ShortByReference paramShortByReference, int paramInt1, Pointer paramPointer, int paramInt2);
  
  public abstract int opus_custom_encoder_ctl(PointerByReference paramPointerByReference, int paramInt, Object... paramVarArgs);
  
  public abstract int opus_custom_decoder_get_size(PointerByReference paramPointerByReference, int paramInt);
  
  public abstract int opus_custom_decoder_init(PointerByReference paramPointerByReference1, PointerByReference paramPointerByReference2, int paramInt);
  
  public abstract PointerByReference opus_custom_decoder_create(PointerByReference paramPointerByReference, int paramInt, IntBuffer paramIntBuffer);
  
  public abstract PointerByReference opus_custom_decoder_create(PointerByReference paramPointerByReference, int paramInt, IntByReference paramIntByReference);
  
  public abstract void opus_custom_decoder_destroy(PointerByReference paramPointerByReference);
  
  public abstract int opus_custom_decode_float(PointerByReference paramPointerByReference, byte[] paramArrayOfByte, int paramInt1, FloatBuffer paramFloatBuffer, int paramInt2);
  
  public abstract int opus_custom_decode_float(PointerByReference paramPointerByReference, Pointer paramPointer, int paramInt1, FloatByReference paramFloatByReference, int paramInt2);
  
  public abstract int opus_custom_decode(PointerByReference paramPointerByReference, byte[] paramArrayOfByte, int paramInt1, ShortBuffer paramShortBuffer, int paramInt2);
  
  public abstract int opus_custom_decode(PointerByReference paramPointerByReference, Pointer paramPointer, int paramInt1, ShortByReference paramShortByReference, int paramInt2);
  
  public abstract int opus_custom_decoder_ctl(PointerByReference paramPointerByReference, int paramInt, Object... paramVarArgs);
  
  public static class OpusCustomDecoder extends PointerType { public OpusCustomDecoder(Pointer address) { super(); }
    
    public OpusCustomDecoder() {}
  }
  
  public static class OpusCustomEncoder extends PointerType
  {
    public OpusCustomEncoder(Pointer address) {
      super();
    }
    
    public OpusCustomEncoder() {}
  }
  
  public static class OpusCustomMode extends PointerType {
    public OpusCustomMode(Pointer address) {
      super();
    }
    
    public OpusCustomMode() {}
  }
}
