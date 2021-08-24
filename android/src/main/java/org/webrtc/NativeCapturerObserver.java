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
/*    */ 
/*    */ class NativeCapturerObserver
/*    */   implements CapturerObserver
/*    */ {
/*    */   private final NativeAndroidVideoTrackSource nativeAndroidVideoTrackSource;
/*    */   
/*    */   @CalledByNative
/*    */   public NativeCapturerObserver(long nativeSource) {
/* 24 */     this.nativeAndroidVideoTrackSource = new NativeAndroidVideoTrackSource(nativeSource);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onCapturerStarted(boolean success) {
/* 29 */     this.nativeAndroidVideoTrackSource.setState(success);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onCapturerStopped() {
/* 34 */     this.nativeAndroidVideoTrackSource.setState(false);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onFrameCaptured(VideoFrame frame) {
/* 40 */     VideoProcessor.FrameAdaptationParameters parameters = this.nativeAndroidVideoTrackSource.adaptFrame(frame);
/* 41 */     if (parameters == null) {
/*    */       return;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 47 */     VideoFrame.Buffer adaptedBuffer = frame.getBuffer().cropAndScale(parameters.cropX, parameters.cropY, parameters.cropWidth, parameters.cropHeight, parameters.scaleWidth, parameters.scaleHeight);
/*    */     
/* 49 */     this.nativeAndroidVideoTrackSource.onFrameCaptured(new VideoFrame(adaptedBuffer, frame
/* 50 */           .getRotation(), parameters.timestampNs));
/* 51 */     adaptedBuffer.release();
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/NativeCapturerObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */