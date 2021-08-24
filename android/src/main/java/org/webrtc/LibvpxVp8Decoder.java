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
/*    */ public class LibvpxVp8Decoder
/*    */   extends WrappedNativeVideoDecoder
/*    */ {
/*    */   public long createNativeVideoDecoder() {
/* 16 */     return nativeCreateDecoder();
/*    */   }
/*    */   
/*    */   static native long nativeCreateDecoder();
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/LibvpxVp8Decoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */