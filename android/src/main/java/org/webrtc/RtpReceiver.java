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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RtpReceiver
/*    */ {
/*    */   private long nativeRtpReceiver;
/*    */   private long nativeObserver;
/*    */   @Nullable
/*    */   private MediaStreamTrack cachedTrack;
/*    */   
/*    */   @CalledByNative
/*    */   public RtpReceiver(long nativeRtpReceiver) {
/* 32 */     this.nativeRtpReceiver = nativeRtpReceiver;
/* 33 */     long nativeTrack = nativeGetTrack(nativeRtpReceiver);
/* 34 */     this.cachedTrack = MediaStreamTrack.createMediaStreamTrack(nativeTrack);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public MediaStreamTrack track() {
/* 39 */     return this.cachedTrack;
/*    */   }
/*    */   
/*    */   public RtpParameters getParameters() {
/* 43 */     checkRtpReceiverExists();
/* 44 */     return nativeGetParameters(this.nativeRtpReceiver);
/*    */   }
/*    */   
/*    */   public String id() {
/* 48 */     checkRtpReceiverExists();
/* 49 */     return nativeGetId(this.nativeRtpReceiver);
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   public void dispose() {
/* 54 */     checkRtpReceiverExists();
/* 55 */     this.cachedTrack.dispose();
/* 56 */     if (this.nativeObserver != 0L) {
/* 57 */       nativeUnsetObserver(this.nativeRtpReceiver, this.nativeObserver);
/* 58 */       this.nativeObserver = 0L;
/*    */     } 
/* 60 */     JniCommon.nativeReleaseRef(this.nativeRtpReceiver);
/* 61 */     this.nativeRtpReceiver = 0L;
/*    */   }
/*    */   
/*    */   public void SetObserver(Observer observer) {
/* 65 */     checkRtpReceiverExists();
/*    */     
/* 67 */     if (this.nativeObserver != 0L) {
/* 68 */       nativeUnsetObserver(this.nativeRtpReceiver, this.nativeObserver);
/*    */     }
/* 70 */     this.nativeObserver = nativeSetObserver(this.nativeRtpReceiver, observer);
/*    */   }
/*    */   
/*    */   public void setFrameDecryptor(FrameDecryptor frameDecryptor) {
/* 74 */     checkRtpReceiverExists();
/* 75 */     nativeSetFrameDecryptor(this.nativeRtpReceiver, frameDecryptor.getNativeFrameDecryptor());
/*    */   }
/*    */   
/*    */   private void checkRtpReceiverExists() {
/* 79 */     if (this.nativeRtpReceiver == 0L)
/* 80 */       throw new IllegalStateException("RtpReceiver has been disposed."); 
/*    */   }
/*    */   
/*    */   private static native long nativeGetTrack(long paramLong);
/*    */   
/*    */   private static native RtpParameters nativeGetParameters(long paramLong);
/*    */   
/*    */   private static native String nativeGetId(long paramLong);
/*    */   
/*    */   private static native long nativeSetObserver(long paramLong, Observer paramObserver);
/*    */   
/*    */   private static native void nativeUnsetObserver(long paramLong1, long paramLong2);
/*    */   
/*    */   private static native void nativeSetFrameDecryptor(long paramLong1, long paramLong2);
/*    */   
/*    */   public static interface Observer {
/*    */     @CalledByNative("Observer")
/*    */     void onFirstPacketReceived(MediaStreamTrack.MediaType param1MediaType);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RtpReceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */