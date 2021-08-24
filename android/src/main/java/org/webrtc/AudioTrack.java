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
/*    */ public class AudioTrack
/*    */   extends MediaStreamTrack
/*    */ {
/*    */   public AudioTrack(long nativeTrack) {
/* 16 */     super(nativeTrack);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setVolume(double volume) {
/* 23 */     nativeSetVolume(getNativeAudioTrack(), volume);
/*    */   }
/*    */ 
/*    */   
/*    */   long getNativeAudioTrack() {
/* 28 */     return getNativeMediaStreamTrack();
/*    */   }
/*    */   
/*    */   private static native void nativeSetVolume(long paramLong, double paramDouble);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/AudioTrack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */