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
/*    */ public class DtmfSender
/*    */ {
/*    */   private long nativeDtmfSender;
/*    */   
/*    */   public DtmfSender(long nativeDtmfSender) {
/* 18 */     this.nativeDtmfSender = nativeDtmfSender;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean canInsertDtmf() {
/* 25 */     checkDtmfSenderExists();
/* 26 */     return nativeCanInsertDtmf(this.nativeDtmfSender);
/*    */   }
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
/*    */   public boolean insertDtmf(String tones, int duration, int interToneGap) {
/* 47 */     checkDtmfSenderExists();
/* 48 */     return nativeInsertDtmf(this.nativeDtmfSender, tones, duration, interToneGap);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String tones() {
/* 55 */     checkDtmfSenderExists();
/* 56 */     return nativeTones(this.nativeDtmfSender);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int duration() {
/* 64 */     checkDtmfSenderExists();
/* 65 */     return nativeDuration(this.nativeDtmfSender);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int interToneGap() {
/* 74 */     checkDtmfSenderExists();
/* 75 */     return nativeInterToneGap(this.nativeDtmfSender);
/*    */   }
/*    */   
/*    */   public void dispose() {
/* 79 */     checkDtmfSenderExists();
/* 80 */     JniCommon.nativeReleaseRef(this.nativeDtmfSender);
/* 81 */     this.nativeDtmfSender = 0L;
/*    */   }
/*    */   
/*    */   private void checkDtmfSenderExists() {
/* 85 */     if (this.nativeDtmfSender == 0L)
/* 86 */       throw new IllegalStateException("DtmfSender has been disposed."); 
/*    */   }
/*    */   
/*    */   private static native boolean nativeCanInsertDtmf(long paramLong);
/*    */   
/*    */   private static native boolean nativeInsertDtmf(long paramLong, String paramString, int paramInt1, int paramInt2);
/*    */   
/*    */   private static native String nativeTones(long paramLong);
/*    */   
/*    */   private static native int nativeDuration(long paramLong);
/*    */   
/*    */   private static native int nativeInterToneGap(long paramLong);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/DtmfSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */