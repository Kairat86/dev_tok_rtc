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
/*    */ class BaseBitrateAdjuster
/*    */   implements BitrateAdjuster
/*    */ {
/*    */   protected int targetBitrateBps;
/*    */   protected int targetFps;
/*    */   
/*    */   public void setTargets(int targetBitrateBps, int targetFps) {
/* 20 */     this.targetBitrateBps = targetBitrateBps;
/* 21 */     this.targetFps = targetFps;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void reportEncodedFrame(int size) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public int getAdjustedBitrateBps() {
/* 31 */     return this.targetBitrateBps;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCodecConfigFramerate() {
/* 36 */     return this.targetFps;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/BaseBitrateAdjuster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */