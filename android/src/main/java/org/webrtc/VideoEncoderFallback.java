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
/*    */ public class VideoEncoderFallback
/*    */   extends WrappedNativeVideoEncoder
/*    */ {
/*    */   private final VideoEncoder fallback;
/*    */   private final VideoEncoder primary;
/*    */   
/*    */   public VideoEncoderFallback(VideoEncoder fallback, VideoEncoder primary) {
/* 21 */     this.fallback = fallback;
/* 22 */     this.primary = primary;
/*    */   }
/*    */ 
/*    */   
/*    */   public long createNativeVideoEncoder() {
/* 27 */     return nativeCreateEncoder(this.fallback, this.primary);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isHardwareEncoder() {
/* 32 */     return this.primary.isHardwareEncoder();
/*    */   }
/*    */   
/*    */   private static native long nativeCreateEncoder(VideoEncoder paramVideoEncoder1, VideoEncoder paramVideoEncoder2);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoEncoderFallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */