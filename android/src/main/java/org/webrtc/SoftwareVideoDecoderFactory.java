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
/*    */ public class SoftwareVideoDecoderFactory
/*    */   implements VideoDecoderFactory
/*    */ {
/*    */   @Deprecated
/*    */   @Nullable
/*    */   public VideoDecoder createDecoder(String codecType) {
/* 23 */     return createDecoder(new VideoCodecInfo(codecType, new HashMap<>()));
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public VideoDecoder createDecoder(VideoCodecInfo codecType) {
/* 29 */     if (codecType.getName().equalsIgnoreCase("VP8")) {
/* 30 */       return new LibvpxVp8Decoder();
/*    */     }
/* 32 */     if (codecType.getName().equalsIgnoreCase("VP9") && LibvpxVp9Decoder.nativeIsSupported()) {
/* 33 */       return new LibvpxVp9Decoder();
/*    */     }
/*    */     
/* 36 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public VideoCodecInfo[] getSupportedCodecs() {
/* 41 */     return supportedCodecs();
/*    */   }
/*    */   
/*    */   static VideoCodecInfo[] supportedCodecs() {
/* 45 */     List<VideoCodecInfo> codecs = new ArrayList<>();
/*    */     
/* 47 */     codecs.add(new VideoCodecInfo("VP8", new HashMap<>()));
/* 48 */     if (LibvpxVp9Decoder.nativeIsSupported()) {
/* 49 */       codecs.add(new VideoCodecInfo("VP9", new HashMap<>()));
/*    */     }
/*    */     
/* 52 */     return codecs.<VideoCodecInfo>toArray(new VideoCodecInfo[codecs.size()]);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/SoftwareVideoDecoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */