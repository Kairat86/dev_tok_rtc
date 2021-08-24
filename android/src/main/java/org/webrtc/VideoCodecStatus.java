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
/*    */ public enum VideoCodecStatus
/*    */ {
/* 18 */   REQUEST_SLI(2),
/* 19 */   NO_OUTPUT(1),
/* 20 */   OK(0),
/* 21 */   ERROR(-1),
/* 22 */   LEVEL_EXCEEDED(-2),
/* 23 */   MEMORY(-3),
/* 24 */   ERR_PARAMETER(-4),
/* 25 */   ERR_SIZE(-5),
/* 26 */   TIMEOUT(-6),
/* 27 */   UNINITIALIZED(-7),
/* 28 */   ERR_REQUEST_SLI(-12),
/* 29 */   FALLBACK_SOFTWARE(-13),
/* 30 */   TARGET_BITRATE_OVERSHOOT(-14);
/*    */   
/*    */   private final int number;
/*    */   
/*    */   VideoCodecStatus(int number) {
/* 35 */     this.number = number;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   public int getNumber() {
/* 40 */     return this.number;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoCodecStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */