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
/*    */ public interface VideoDecoderFactory
/*    */ {
/*    */   @Deprecated
/*    */   @Nullable
/*    */   default VideoDecoder createDecoder(String codecType) {
/* 24 */     throw new UnsupportedOperationException("Deprecated and not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   @CalledByNative
/*    */   default VideoDecoder createDecoder(VideoCodecInfo info) {
/* 31 */     return createDecoder(info.getName());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   default VideoCodecInfo[] getSupportedCodecs() {
/* 39 */     return new VideoCodecInfo[0];
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoDecoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */