/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class MediaConstraints
/*    */ {
/*    */   public final List<KeyValuePair> mandatory;
/*    */   public final List<KeyValuePair> optional;
/*    */   
/*    */   public static class KeyValuePair
/*    */   {
/*    */     private final String key;
/*    */     private final String value;
/*    */     
/*    */     public KeyValuePair(String key, String value) {
/* 28 */       this.key = key;
/* 29 */       this.value = value;
/*    */     }
/*    */     
/*    */     @CalledByNative("KeyValuePair")
/*    */     public String getKey() {
/* 34 */       return this.key;
/*    */     }
/*    */     
/*    */     @CalledByNative("KeyValuePair")
/*    */     public String getValue() {
/* 39 */       return this.value;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 44 */       return this.key + ": " + this.value;
/*    */     }
/*    */ 
/*    */     
/*    */     public boolean equals(@Nullable Object other) {
/* 49 */       if (this == other) {
/* 50 */         return true;
/*    */       }
/* 52 */       if (other == null || getClass() != other.getClass()) {
/* 53 */         return false;
/*    */       }
/* 55 */       KeyValuePair that = (KeyValuePair)other;
/* 56 */       return (this.key.equals(that.key) && this.value.equals(that.value));
/*    */     }
/*    */ 
/*    */     
/*    */     public int hashCode() {
/* 61 */       return this.key.hashCode() + this.value.hashCode();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MediaConstraints() {
/* 69 */     this.mandatory = new ArrayList<>();
/* 70 */     this.optional = new ArrayList<>();
/*    */   }
/*    */   
/*    */   private static String stringifyKeyValuePairList(List<KeyValuePair> list) {
/* 74 */     StringBuilder builder = new StringBuilder("[");
/* 75 */     for (KeyValuePair pair : list) {
/* 76 */       if (builder.length() > 1) {
/* 77 */         builder.append(", ");
/*    */       }
/* 79 */       builder.append(pair.toString());
/*    */     } 
/* 81 */     return builder.append("]").toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 86 */     return "mandatory: " + stringifyKeyValuePairList(this.mandatory) + ", optional: " + 
/* 87 */       stringifyKeyValuePairList(this.optional);
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   List<KeyValuePair> getMandatory() {
/* 92 */     return this.mandatory;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   List<KeyValuePair> getOptional() {
/* 97 */     return this.optional;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/MediaConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */