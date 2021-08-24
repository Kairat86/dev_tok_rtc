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
/*    */ public class VideoDecoderFallback
/*    */   extends WrappedNativeVideoDecoder
/*    */ {
/*    */   private final VideoDecoder fallback;
/*    */   private final VideoDecoder primary;
/*    */   
/*    */   public VideoDecoderFallback(VideoDecoder fallback, VideoDecoder primary) {
/* 21 */     this.fallback = fallback;
/* 22 */     this.primary = primary;
/*    */   }
/*    */ 
/*    */   
/*    */   public long createNativeVideoDecoder() {
/* 27 */     return nativeCreateDecoder(this.fallback, this.primary);
/*    */   }
/*    */   
/*    */   private static native long nativeCreateDecoder(VideoDecoder paramVideoDecoder1, VideoDecoder paramVideoDecoder2);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoDecoderFallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */