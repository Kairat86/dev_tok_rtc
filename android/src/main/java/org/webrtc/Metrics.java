/*    */ package org.webrtc;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Metrics
/*    */ {
/*    */   private static final String TAG = "Metrics";
/* 34 */   public final Map<String, HistogramInfo> map = new HashMap<>();
/*    */ 
/*    */ 
/*    */   
/*    */   public static class HistogramInfo
/*    */   {
/*    */     public final int min;
/*    */ 
/*    */     
/*    */     public final int max;
/*    */     
/*    */     public final int bucketCount;
/*    */     
/* 47 */     public final Map<Integer, Integer> samples = new HashMap<>();
/*    */ 
/*    */     
/*    */     @CalledByNative("HistogramInfo")
/*    */     public HistogramInfo(int min, int max, int bucketCount) {
/* 52 */       this.min = min;
/* 53 */       this.max = max;
/* 54 */       this.bucketCount = bucketCount;
/*    */     }
/*    */     
/*    */     @CalledByNative("HistogramInfo")
/*    */     public void addSample(int value, int numEvents) {
/* 59 */       this.samples.put(Integer.valueOf(value), Integer.valueOf(numEvents));
/*    */     }
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   private void add(String name, HistogramInfo info) {
/* 65 */     this.map.put(name, info);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static void enable() {
/* 71 */     nativeEnable();
/*    */   }
/*    */ 
/*    */   
/*    */   public static Metrics getAndReset() {
/* 76 */     return nativeGetAndReset();
/*    */   }
/*    */   
/*    */   private static native void nativeEnable();
/*    */   
/*    */   private static native Metrics nativeGetAndReset();
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/Metrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */