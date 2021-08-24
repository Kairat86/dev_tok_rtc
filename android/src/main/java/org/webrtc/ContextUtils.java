/*    */ package org.webrtc;
/*    */ 
/*    */ import android.content.Context;
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
/*    */ public class ContextUtils
/*    */ {
/*    */   private static final String TAG = "ContextUtils";
/*    */   private static Context applicationContext;
/*    */   
/*    */   public static void initialize(Context applicationContext) {
/* 29 */     if (applicationContext == null) {
/* 30 */       throw new IllegalArgumentException("Application context cannot be null for ContextUtils.initialize.");
/*    */     }
/*    */     
/* 33 */     ContextUtils.applicationContext = applicationContext;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public static Context getApplicationContext() {
/* 43 */     return applicationContext;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/ContextUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */