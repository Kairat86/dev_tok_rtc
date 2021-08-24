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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TimestampAligner
/*    */ {
/*    */   public static long getRtcTimeNanos() {
/* 27 */     return nativeRtcTimeNanos();
/*    */   }
/*    */   
/* 30 */   private volatile long nativeTimestampAligner = nativeCreateTimestampAligner();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long translateTimestamp(long cameraTimeNs) {
/* 38 */     checkNativeAlignerExists();
/* 39 */     return nativeTranslateTimestamp(this.nativeTimestampAligner, cameraTimeNs);
/*    */   }
/*    */ 
/*    */   
/*    */   public void dispose() {
/* 44 */     checkNativeAlignerExists();
/* 45 */     nativeReleaseTimestampAligner(this.nativeTimestampAligner);
/* 46 */     this.nativeTimestampAligner = 0L;
/*    */   }
/*    */   
/*    */   private void checkNativeAlignerExists() {
/* 50 */     if (this.nativeTimestampAligner == 0L)
/* 51 */       throw new IllegalStateException("TimestampAligner has been disposed."); 
/*    */   }
/*    */   
/*    */   private static native long nativeRtcTimeNanos();
/*    */   
/*    */   private static native long nativeCreateTimestampAligner();
/*    */   
/*    */   private static native void nativeReleaseTimestampAligner(long paramLong);
/*    */   
/*    */   private static native long nativeTranslateTimestamp(long paramLong1, long paramLong2);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/TimestampAligner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */