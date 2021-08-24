/*    */ package org.webrtc;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
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
/*    */ class JniHelper
/*    */ {
/*    */   @CalledByNative
/*    */   static byte[] getStringBytes(String s) {
/*    */     try {
/* 25 */       return s.getBytes("ISO-8859-1");
/* 26 */     } catch (UnsupportedEncodingException e) {
/* 27 */       throw new RuntimeException("ISO-8859-1 is unsupported");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   static Object getStringClass() {
/* 34 */     return String.class;
/*    */   }
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   static Object getKey(Map.Entry entry) {
/* 40 */     return entry.getKey();
/*    */   }
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   static Object getValue(Map.Entry entry) {
/* 46 */     return entry.getValue();
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/JniHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */