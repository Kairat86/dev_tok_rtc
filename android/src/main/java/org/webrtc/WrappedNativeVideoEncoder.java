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
/*    */ public abstract class WrappedNativeVideoEncoder
/*    */   implements VideoEncoder
/*    */ {
/*    */   public abstract long createNativeVideoEncoder();
/*    */   
/*    */   public abstract boolean isHardwareEncoder();
/*    */   
/*    */   public final VideoCodecStatus initEncode(VideoEncoder.Settings settings, VideoEncoder.Callback encodeCallback) {
/* 22 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   public final VideoCodecStatus release() {
/* 27 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   public final VideoCodecStatus encode(VideoFrame frame, VideoEncoder.EncodeInfo info) {
/* 32 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   public final VideoCodecStatus setRateAllocation(VideoEncoder.BitrateAllocation allocation, int framerate) {
/* 37 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   public final VideoEncoder.ScalingSettings getScalingSettings() {
/* 42 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   public final String getImplementationName() {
/* 47 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/WrappedNativeVideoEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */