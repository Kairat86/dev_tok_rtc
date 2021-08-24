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
/*    */ public class CallSessionFileRotatingLogSink
/*    */ {
/*    */   private long nativeSink;
/*    */   
/*    */   public static byte[] getLogData(String dirPath) {
/* 17 */     if (dirPath == null) {
/* 18 */       throw new IllegalArgumentException("dirPath may not be null.");
/*    */     }
/* 20 */     return nativeGetLogData(dirPath);
/*    */   }
/*    */ 
/*    */   
/*    */   public CallSessionFileRotatingLogSink(String dirPath, int maxFileSize, Logging.Severity severity) {
/* 25 */     if (dirPath == null) {
/* 26 */       throw new IllegalArgumentException("dirPath may not be null.");
/*    */     }
/* 28 */     this.nativeSink = nativeAddSink(dirPath, maxFileSize, severity.ordinal());
/*    */   }
/*    */   
/*    */   public void dispose() {
/* 32 */     if (this.nativeSink != 0L) {
/* 33 */       nativeDeleteSink(this.nativeSink);
/* 34 */       this.nativeSink = 0L;
/*    */     } 
/*    */   }
/*    */   
/*    */   private static native long nativeAddSink(String paramString, int paramInt1, int paramInt2);
/*    */   
/*    */   private static native void nativeDeleteSink(long paramLong);
/*    */   
/*    */   private static native byte[] nativeGetLogData(String paramString);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/CallSessionFileRotatingLogSink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */