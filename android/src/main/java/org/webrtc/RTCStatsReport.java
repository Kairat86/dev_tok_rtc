/*    */ package org.webrtc;
/*    */ 
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
/*    */ public class RTCStatsReport
/*    */ {
/*    */   private final long timestampUs;
/*    */   private final Map<String, RTCStats> stats;
/*    */   
/*    */   public RTCStatsReport(long timestampUs, Map<String, RTCStats> stats) {
/* 25 */     this.timestampUs = timestampUs;
/* 26 */     this.stats = stats;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getTimestampUs() {
/* 31 */     return this.timestampUs;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, RTCStats> getStatsMap() {
/* 37 */     return this.stats;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 42 */     StringBuilder builder = new StringBuilder();
/* 43 */     builder.append("{ timestampUs: ").append(this.timestampUs).append(", stats: [\n");
/* 44 */     boolean first = true;
/* 45 */     for (RTCStats stat : this.stats.values()) {
/* 46 */       if (!first) {
/* 47 */         builder.append(",\n");
/*    */       }
/* 49 */       builder.append(stat);
/* 50 */       first = false;
/*    */     } 
/* 52 */     builder.append(" ] }");
/* 53 */     return builder.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   private static RTCStatsReport create(long timestampUs, Map<String, RTCStats> stats) {
/* 60 */     return new RTCStatsReport(timestampUs, stats);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RTCStatsReport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */