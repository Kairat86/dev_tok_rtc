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
/*    */ public class BuiltinAudioEncoderFactoryFactory
/*    */   implements AudioEncoderFactoryFactory
/*    */ {
/*    */   public long createNativeAudioEncoderFactory() {
/* 19 */     return nativeCreateBuiltinAudioEncoderFactory();
/*    */   }
/*    */   
/*    */   private static native long nativeCreateBuiltinAudioEncoderFactory();
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/BuiltinAudioEncoderFactoryFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */