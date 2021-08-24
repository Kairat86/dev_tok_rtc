/*    */ package org.webrtc;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MediaSource
/*    */ {
/*    */   private final RefCountDelegate refCountDelegate;
/*    */   private long nativeSource;
/*    */   
/*    */   public enum State
/*    */   {
/* 17 */     INITIALIZING,
/* 18 */     LIVE,
/* 19 */     ENDED,
/* 20 */     MUTED;
/*    */     
/*    */     @CalledByNative("State")
/*    */     static State fromNativeIndex(int nativeIndex) {
/* 24 */       return values()[nativeIndex];
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MediaSource(long nativeSource) {
/* 32 */     this.refCountDelegate = new RefCountDelegate(() -> JniCommon.nativeReleaseRef(nativeSource));
/* 33 */     this.nativeSource = nativeSource;
/*    */   }
/*    */   
/*    */   public State state() {
/* 37 */     checkMediaSourceExists();
/* 38 */     return nativeGetState(this.nativeSource);
/*    */   }
/*    */   
/*    */   public void dispose() {
/* 42 */     checkMediaSourceExists();
/* 43 */     this.refCountDelegate.release();
/* 44 */     this.nativeSource = 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   protected long getNativeMediaSource() {
/* 49 */     checkMediaSourceExists();
/* 50 */     return this.nativeSource;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void runWithReference(Runnable runnable) {
/* 58 */     if (this.refCountDelegate.safeRetain()) {
/*    */       try {
/* 60 */         runnable.run();
/*    */       } finally {
/* 62 */         this.refCountDelegate.release();
/*    */       } 
/*    */     }
/*    */   }
/*    */   
/*    */   private void checkMediaSourceExists() {
/* 68 */     if (this.nativeSource == 0L)
/* 69 */       throw new IllegalStateException("MediaSource has been disposed."); 
/*    */   }
/*    */   
/*    */   private static native State nativeGetState(long paramLong);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/MediaSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */