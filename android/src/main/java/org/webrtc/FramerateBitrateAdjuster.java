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
/*    */ class FramerateBitrateAdjuster
/*    */   extends BaseBitrateAdjuster
/*    */ {
/*    */   private static final int INITIAL_FPS = 30;
/*    */   
/*    */   public void setTargets(int targetBitrateBps, int targetFps) {
/* 22 */     if (this.targetFps == 0)
/*    */     {
/* 24 */       targetFps = 30;
/*    */     }
/* 26 */     super.setTargets(targetBitrateBps, targetFps);
/*    */     
/* 28 */     this.targetBitrateBps = this.targetBitrateBps * 30 / this.targetFps;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCodecConfigFramerate() {
/* 33 */     return 30;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/FramerateBitrateAdjuster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */