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
/*    */ public class LibvpxVp8Encoder
/*    */   extends WrappedNativeVideoEncoder
/*    */ {
/*    */   public long createNativeVideoEncoder() {
/* 16 */     return nativeCreateEncoder();
/*    */   }
/*    */ 
/*    */   
/*    */   static native long nativeCreateEncoder();
/*    */   
/*    */   public boolean isHardwareEncoder() {
/* 23 */     return false;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/LibvpxVp8Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */