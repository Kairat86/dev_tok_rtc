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
/*    */ public class TurnCustomizer
/*    */ {
/*    */   private long nativeTurnCustomizer;
/*    */   
/*    */   public TurnCustomizer(long nativeTurnCustomizer) {
/* 18 */     this.nativeTurnCustomizer = nativeTurnCustomizer;
/*    */   }
/*    */   
/*    */   public void dispose() {
/* 22 */     checkTurnCustomizerExists();
/* 23 */     nativeFreeTurnCustomizer(this.nativeTurnCustomizer);
/* 24 */     this.nativeTurnCustomizer = 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   private static native void nativeFreeTurnCustomizer(long paramLong);
/*    */   
/*    */   @CalledByNative
/*    */   long getNativeTurnCustomizer() {
/* 32 */     checkTurnCustomizerExists();
/* 33 */     return this.nativeTurnCustomizer;
/*    */   }
/*    */   
/*    */   private void checkTurnCustomizerExists() {
/* 37 */     if (this.nativeTurnCustomizer == 0L)
/* 38 */       throw new IllegalStateException("TurnCustomizer has been disposed."); 
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/TurnCustomizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */