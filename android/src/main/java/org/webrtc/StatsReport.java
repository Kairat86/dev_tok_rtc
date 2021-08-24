/*    */ package org.webrtc;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StatsReport
/*    */ {
/*    */   public final String id;
/*    */   public final String type;
/*    */   public final double timestamp;
/*    */   public final Value[] values;
/*    */   
/*    */   public static class Value
/*    */   {
/*    */     public final String name;
/*    */     public final String value;
/*    */     
/*    */     @CalledByNative("Value")
/*    */     public Value(String name, String value) {
/* 22 */       this.name = name;
/* 23 */       this.value = value;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 28 */       StringBuilder builder = new StringBuilder();
/* 29 */       builder.append("[").append(this.name).append(": ").append(this.value).append("]");
/* 30 */       return builder.toString();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   public StatsReport(String id, String type, double timestamp, Value[] values) {
/* 42 */     this.id = id;
/* 43 */     this.type = type;
/* 44 */     this.timestamp = timestamp;
/* 45 */     this.values = values;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 50 */     StringBuilder builder = new StringBuilder();
/* 51 */     builder.append("id: ")
/* 52 */       .append(this.id)
/* 53 */       .append(", type: ")
/* 54 */       .append(this.type)
/* 55 */       .append(", timestamp: ")
/* 56 */       .append(this.timestamp)
/* 57 */       .append(", values: ");
/* 58 */     for (int i = 0; i < this.values.length; i++) {
/* 59 */       builder.append(this.values[i].toString()).append(", ");
/*    */     }
/* 61 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/StatsReport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */