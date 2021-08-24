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
/*    */ class Histogram
/*    */ {
/*    */   private final long handle;
/*    */   
/*    */   private Histogram(long handle) {
/* 26 */     this.handle = handle;
/*    */   }
/*    */   
/*    */   public static Histogram createCounts(String name, int min, int max, int bucketCount) {
/* 30 */     return new Histogram(nativeCreateCounts(name, min, max, bucketCount));
/*    */   }
/*    */   
/*    */   public static Histogram createEnumeration(String name, int max) {
/* 34 */     return new Histogram(nativeCreateEnumeration(name, max));
/*    */   }
/*    */   
/*    */   public void addSample(int sample) {
/* 38 */     nativeAddSample(this.handle, sample);
/*    */   }
/*    */   
/*    */   private static native long nativeCreateCounts(String paramString, int paramInt1, int paramInt2, int paramInt3);
/*    */   
/*    */   private static native long nativeCreateEnumeration(String paramString, int paramInt);
/*    */   
/*    */   private static native void nativeAddSample(long paramLong, int paramInt);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/Histogram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */