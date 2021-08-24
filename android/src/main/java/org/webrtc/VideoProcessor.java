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
/*    */ public interface VideoProcessor
/*    */   extends CapturerObserver
/*    */ {
/*    */   public static class FrameAdaptationParameters
/*    */   {
/*    */     public final int cropX;
/*    */     public final int cropY;
/*    */     public final int cropWidth;
/*    */     public final int cropHeight;
/*    */     public final int scaleWidth;
/*    */     public final int scaleHeight;
/*    */     public final long timestampNs;
/*    */     public final boolean drop;
/*    */     
/*    */     public FrameAdaptationParameters(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight, long timestampNs, boolean drop) {
/* 32 */       this.cropX = cropX;
/* 33 */       this.cropY = cropY;
/* 34 */       this.cropWidth = cropWidth;
/* 35 */       this.cropHeight = cropHeight;
/* 36 */       this.scaleWidth = scaleWidth;
/* 37 */       this.scaleHeight = scaleHeight;
/* 38 */       this.timestampNs = timestampNs;
/* 39 */       this.drop = drop;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default void onFrameCaptured(VideoFrame frame, FrameAdaptationParameters parameters) {
/* 48 */     VideoFrame adaptedFrame = applyFrameAdaptationParameters(frame, parameters);
/* 49 */     if (adaptedFrame != null) {
/* 50 */       onFrameCaptured(adaptedFrame);
/* 51 */       adaptedFrame.release();
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void setSink(@Nullable VideoSink paramVideoSink);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   static VideoFrame applyFrameAdaptationParameters(VideoFrame frame, FrameAdaptationParameters parameters) {
/* 67 */     if (parameters.drop) {
/* 68 */       return null;
/*    */     }
/*    */ 
/*    */     
/* 72 */     VideoFrame.Buffer adaptedBuffer = frame.getBuffer().cropAndScale(parameters.cropX, parameters.cropY, parameters.cropWidth, parameters.cropHeight, parameters.scaleWidth, parameters.scaleHeight);
/*    */     
/* 74 */     return new VideoFrame(adaptedBuffer, frame.getRotation(), parameters.timestampNs);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */