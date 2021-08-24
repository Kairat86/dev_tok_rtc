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
/*    */ class JNILogging
/*    */ {
/*    */   private final Loggable loggable;
/*    */   
/*    */   public JNILogging(Loggable loggable) {
/* 21 */     this.loggable = loggable;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   public void logToInjectable(String message, Integer severity, String tag) {
/* 26 */     this.loggable.onLogMessage(message, Logging.Severity.values()[severity.intValue()], tag);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/JNILogging.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */