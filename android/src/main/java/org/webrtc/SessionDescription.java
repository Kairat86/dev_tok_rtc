/*    */ package org.webrtc;
/*    */ 
/*    */ import java.util.Locale;
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
/*    */ public class SessionDescription
/*    */ {
/*    */   public final Type type;
/*    */   public final String description;
/*    */   
/*    */   public enum Type
/*    */   {
/* 23 */     OFFER,
/* 24 */     PRANSWER,
/* 25 */     ANSWER;
/*    */     
/*    */     public String canonicalForm() {
/* 28 */       return name().toLowerCase(Locale.US);
/*    */     }
/*    */     
/*    */     @CalledByNative("Type")
/*    */     public static Type fromCanonicalForm(String canonical) {
/* 33 */       return (Type)valueOf(Type.class, canonical.toUpperCase(Locale.US));
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   public SessionDescription(Type type, String description) {
/* 42 */     this.type = type;
/* 43 */     this.description = description;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   String getDescription() {
/* 48 */     return this.description;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   String getTypeInCanonicalForm() {
/* 53 */     return this.type.canonicalForm();
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/SessionDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */