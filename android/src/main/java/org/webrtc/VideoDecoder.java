/*    */ package org.webrtc;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface VideoDecoder
/*    */ {
/*    */   public static class Settings
/*    */   {
/*    */     public final int numberOfCores;
/*    */     public final int width;
/*    */     public final int height;
/*    */     
/*    */     @CalledByNative("Settings")
/*    */     public Settings(int numberOfCores, int width, int height) {
/* 26 */       this.numberOfCores = numberOfCores;
/* 27 */       this.width = width;
/* 28 */       this.height = height;
/*    */     }
/*    */   }
/*    */   
/*    */   public static class DecodeInfo
/*    */   {
/*    */     public final boolean isMissingFrames;
/*    */     public final long renderTimeMs;
/*    */     
/*    */     public DecodeInfo(boolean isMissingFrames, long renderTimeMs) {
/* 38 */       this.isMissingFrames = isMissingFrames;
/* 39 */       this.renderTimeMs = renderTimeMs;
/*    */     }
/*    */   }
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   default long createNativeVideoDecoder() {
/* 73 */     return 0L;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   VideoCodecStatus initDecode(Settings paramSettings, Callback paramCallback);
/*    */   
/*    */   @CalledByNative
/*    */   VideoCodecStatus release();
/*    */   
/*    */   @CalledByNative
/*    */   VideoCodecStatus decode(EncodedImage paramEncodedImage, DecodeInfo paramDecodeInfo);
/*    */   
/*    */   @CalledByNative
/*    */   boolean getPrefersLateDecoding();
/*    */   
/*    */   @CalledByNative
/*    */   String getImplementationName();
/*    */   
/*    */   public static interface Callback {
/*    */     void onDecodedFrame(VideoFrame param1VideoFrame, Integer param1Integer1, Integer param1Integer2);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */