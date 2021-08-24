/*     */ package org.webrtc;
/*     */ 
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RTCStats
/*     */ {
/*     */   private final long timestampUs;
/*     */   private final String type;
/*     */   private final String id;
/*     */   private final Map<String, Object> members;
/*     */   
/*     */   public RTCStats(long timestampUs, String type, String id, Map<String, Object> members) {
/*  30 */     this.timestampUs = timestampUs;
/*  31 */     this.type = type;
/*  32 */     this.id = id;
/*  33 */     this.members = members;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getTimestampUs() {
/*  38 */     return this.timestampUs;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getType() {
/*  44 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getId() {
/*  50 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Object> getMembers() {
/*  67 */     return this.members;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  72 */     StringBuilder builder = new StringBuilder();
/*  73 */     builder.append("{ timestampUs: ")
/*  74 */       .append(this.timestampUs)
/*  75 */       .append(", type: ")
/*  76 */       .append(this.type)
/*  77 */       .append(", id: ")
/*  78 */       .append(this.id);
/*  79 */     boolean first = true;
/*  80 */     for (Map.Entry<String, Object> entry : this.members.entrySet()) {
/*  81 */       builder.append(", ").append(entry.getKey()).append(": ");
/*  82 */       appendValue(builder, entry.getValue());
/*     */     } 
/*  84 */     builder.append(" }");
/*  85 */     return builder.toString();
/*     */   }
/*     */   
/*     */   private static void appendValue(StringBuilder builder, Object value) {
/*  89 */     if (value instanceof Object[]) {
/*  90 */       Object[] arrayValue = (Object[])value;
/*  91 */       builder.append('[');
/*  92 */       for (int i = 0; i < arrayValue.length; i++) {
/*  93 */         if (i != 0) {
/*  94 */           builder.append(", ");
/*     */         }
/*  96 */         appendValue(builder, arrayValue[i]);
/*     */       } 
/*  98 */       builder.append(']');
/*  99 */     } else if (value instanceof String) {
/*     */       
/* 101 */       builder.append('"').append(value).append('"');
/*     */     } else {
/* 103 */       builder.append(value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   static RTCStats create(long timestampUs, String type, String id, Map<String, Object> members) {
/* 111 */     return new RTCStats(timestampUs, type, id, members);
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RTCStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */