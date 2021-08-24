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
/*    */ public class BuiltinAudioDecoderFactoryFactory
/*    */   implements AudioDecoderFactoryFactory
/*    */ {
/*    */   public long createNativeAudioDecoderFactory() {
/* 19 */     return nativeCreateBuiltinAudioDecoderFactory();
/*    */   }
/*    */   
/*    */   private static native long nativeCreateBuiltinAudioDecoderFactory();
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/BuiltinAudioDecoderFactoryFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */