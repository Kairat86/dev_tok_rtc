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
/*    */ class WebRtcClassLoader
/*    */ {
/*    */   @CalledByNative
/*    */   static Object getClassLoader() {
/* 21 */     Object loader = WebRtcClassLoader.class.getClassLoader();
/* 22 */     if (loader == null) {
/* 23 */       throw new RuntimeException("Failed to get WebRTC class loader.");
/*    */     }
/* 25 */     return loader;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/WebRtcClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */