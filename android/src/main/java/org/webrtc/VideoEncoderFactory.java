/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface VideoEncoderFactory
/*    */ {
/*    */   @Nullable
/*    */   @CalledByNative
/*    */   VideoEncoder createEncoder(VideoCodecInfo paramVideoCodecInfo);
/*    */   
/*    */   @CalledByNative
/*    */   VideoCodecInfo[] getSupportedCodecs();
/*    */   
/*    */   @CalledByNative
/*    */   default VideoCodecInfo[] getImplementations() {
/* 51 */     return getSupportedCodecs();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   default VideoEncoderSelector getEncoderSelector() {
/* 60 */     return null;
/*    */   }
/*    */   
/*    */   public static interface VideoEncoderSelector {
/*    */     @CalledByNative("VideoEncoderSelector")
/*    */     void onCurrentEncoder(VideoCodecInfo param1VideoCodecInfo);
/*    */     
/*    */     @Nullable
/*    */     @CalledByNative("VideoEncoderSelector")
/*    */     VideoCodecInfo onAvailableBitrate(int param1Int);
/*    */     
/*    */     @Nullable
/*    */     @CalledByNative("VideoEncoderSelector")
/*    */     VideoCodecInfo onEncoderBroken();
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoEncoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */