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
/*    */ public class AudioSource
/*    */   extends MediaSource
/*    */ {
/*    */   public AudioSource(long nativeSource) {
/* 19 */     super(nativeSource);
/*    */   }
/*    */ 
/*    */   
/*    */   long getNativeAudioSource() {
/* 24 */     return getNativeMediaSource();
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/AudioSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */