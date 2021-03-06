package org.webrtc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface VideoCodecType {
  public static final int VIDEO_CODEC_GENERIC = 0;
  
  public static final int VIDEO_CODEC_VP8 = 1;
  
  public static final int VIDEO_CODEC_VP9 = 2;
  
  public static final int VIDEO_CODEC_AV1 = 3;
  
  public static final int VIDEO_CODEC_H264 = 4;
  
  public static final int VIDEO_CODEC_MULTIPLEX = 5;
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoCodecType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */