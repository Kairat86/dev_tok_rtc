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
/*    */ class VideoEncoderWrapper
/*    */ {
/*    */   @CalledByNative
/*    */   static boolean getScalingSettingsOn(VideoEncoder.ScalingSettings scalingSettings) {
/* 23 */     return scalingSettings.on;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   @CalledByNative
/*    */   static Integer getScalingSettingsLow(VideoEncoder.ScalingSettings scalingSettings) {
/* 29 */     return scalingSettings.low;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   @CalledByNative
/*    */   static Integer getScalingSettingsHigh(VideoEncoder.ScalingSettings scalingSettings) {
/* 35 */     return scalingSettings.high;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   static VideoEncoder.Callback createEncoderCallback(long nativeEncoder) {
/* 40 */     return (frame, info) -> nativeOnEncodedFrame(nativeEncoder, frame);
/*    */   }
/*    */   
/*    */   private static native void nativeOnEncodedFrame(long paramLong, EncodedImage paramEncodedImage);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoEncoderWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */