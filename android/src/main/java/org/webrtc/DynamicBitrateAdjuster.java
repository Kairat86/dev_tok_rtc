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
/*    */ class DynamicBitrateAdjuster
/*    */   extends BaseBitrateAdjuster
/*    */ {
/*    */   private static final double BITRATE_ADJUSTMENT_SEC = 3.0D;
/*    */   private static final double BITRATE_ADJUSTMENT_MAX_SCALE = 4.0D;
/*    */   private static final int BITRATE_ADJUSTMENT_STEPS = 20;
/*    */   private static final double BITS_PER_BYTE = 8.0D;
/*    */   private double deviationBytes;
/*    */   private double timeSinceLastAdjustmentMs;
/*    */   private int bitrateAdjustmentScaleExp;
/*    */   
/*    */   public void setTargets(int targetBitrateBps, int targetFps) {
/* 35 */     if (this.targetBitrateBps > 0 && targetBitrateBps < this.targetBitrateBps)
/*    */     {
/* 37 */       this.deviationBytes = this.deviationBytes * targetBitrateBps / this.targetBitrateBps;
/*    */     }
/* 39 */     super.setTargets(targetBitrateBps, targetFps);
/*    */   }
/*    */ 
/*    */   
/*    */   public void reportEncodedFrame(int size) {
/* 44 */     if (this.targetFps == 0) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 49 */     double expectedBytesPerFrame = this.targetBitrateBps / 8.0D / this.targetFps;
/* 50 */     this.deviationBytes += size - expectedBytesPerFrame;
/* 51 */     this.timeSinceLastAdjustmentMs += 1000.0D / this.targetFps;
/*    */ 
/*    */ 
/*    */     
/* 55 */     double deviationThresholdBytes = this.targetBitrateBps / 8.0D;
/*    */ 
/*    */ 
/*    */     
/* 59 */     double deviationCap = 3.0D * deviationThresholdBytes;
/* 60 */     this.deviationBytes = Math.min(this.deviationBytes, deviationCap);
/* 61 */     this.deviationBytes = Math.max(this.deviationBytes, -deviationCap);
/*    */ 
/*    */ 
/*    */     
/* 65 */     if (this.timeSinceLastAdjustmentMs <= 3000.0D) {
/*    */       return;
/*    */     }
/*    */     
/* 69 */     if (this.deviationBytes > deviationThresholdBytes) {
/*    */       
/* 71 */       int bitrateAdjustmentInc = (int)(this.deviationBytes / deviationThresholdBytes + 0.5D);
/* 72 */       this.bitrateAdjustmentScaleExp -= bitrateAdjustmentInc;
/*    */ 
/*    */       
/* 75 */       this.bitrateAdjustmentScaleExp = Math.max(this.bitrateAdjustmentScaleExp, -20);
/* 76 */       this.deviationBytes = deviationThresholdBytes;
/* 77 */     } else if (this.deviationBytes < -deviationThresholdBytes) {
/*    */       
/* 79 */       int bitrateAdjustmentInc = (int)(-this.deviationBytes / deviationThresholdBytes + 0.5D);
/* 80 */       this.bitrateAdjustmentScaleExp += bitrateAdjustmentInc;
/*    */ 
/*    */       
/* 83 */       this.bitrateAdjustmentScaleExp = Math.min(this.bitrateAdjustmentScaleExp, 20);
/* 84 */       this.deviationBytes = -deviationThresholdBytes;
/*    */     } 
/* 86 */     this.timeSinceLastAdjustmentMs = 0.0D;
/*    */   }
/*    */   
/*    */   private double getBitrateAdjustmentScale() {
/* 90 */     return Math.pow(4.0D, this.bitrateAdjustmentScaleExp / 20.0D);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getAdjustedBitrateBps() {
/* 96 */     return (int)(this.targetBitrateBps * getBitrateAdjustmentScale());
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/DynamicBitrateAdjuster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */