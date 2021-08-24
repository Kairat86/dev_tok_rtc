/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SoftwareVideoEncoderFactory
/*    */   implements VideoEncoderFactory
/*    */ {
/*    */   @Nullable
/*    */   public VideoEncoder createEncoder(VideoCodecInfo info) {
/* 22 */     if (info.name.equalsIgnoreCase("VP8")) {
/* 23 */       return new LibvpxVp8Encoder();
/*    */     }
/* 25 */     if (info.name.equalsIgnoreCase("VP9") && LibvpxVp9Encoder.nativeIsSupported()) {
/* 26 */       return new LibvpxVp9Encoder();
/*    */     }
/*    */     
/* 29 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public VideoCodecInfo[] getSupportedCodecs() {
/* 34 */     return supportedCodecs();
/*    */   }
/*    */   
/*    */   static VideoCodecInfo[] supportedCodecs() {
/* 38 */     List<VideoCodecInfo> codecs = new ArrayList<>();
/*    */     
/* 40 */     codecs.add(new VideoCodecInfo("VP8", new HashMap<>()));
/* 41 */     if (LibvpxVp9Encoder.nativeIsSupported()) {
/* 42 */       codecs.add(new VideoCodecInfo("VP9", new HashMap<>()));
/*    */     }
/*    */     
/* 45 */     return codecs.<VideoCodecInfo>toArray(new VideoCodecInfo[codecs.size()]);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/SoftwareVideoEncoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */