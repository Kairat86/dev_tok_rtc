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
/*    */ class VideoDecoderWrapper
/*    */ {
/*    */   @CalledByNative
/*    */   static VideoDecoder.Callback createDecoderCallback(long nativeDecoder) {
/* 21 */     return (frame, decodeTimeMs, qp) -> nativeOnDecodedFrame(nativeDecoder, frame, decodeTimeMs, qp);
/*    */   }
/*    */   
/*    */   private static native void nativeOnDecodedFrame(long paramLong, VideoFrame paramVideoFrame, Integer paramInteger1, Integer paramInteger2);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoDecoderWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */