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
/*    */ public abstract class WrappedNativeVideoDecoder
/*    */   implements VideoDecoder
/*    */ {
/*    */   public abstract long createNativeVideoDecoder();
/*    */   
/*    */   public final VideoCodecStatus initDecode(VideoDecoder.Settings settings, VideoDecoder.Callback decodeCallback) {
/* 21 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   public final VideoCodecStatus release() {
/* 26 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   public final VideoCodecStatus decode(EncodedImage frame, VideoDecoder.DecodeInfo info) {
/* 31 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean getPrefersLateDecoding() {
/* 36 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ 
/*    */   
/*    */   public final String getImplementationName() {
/* 41 */     throw new UnsupportedOperationException("Not implemented.");
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/WrappedNativeVideoDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */