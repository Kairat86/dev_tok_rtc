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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface Predicate<T>
/*    */ {
/*    */   boolean test(T paramT);
/*    */   
/*    */   default Predicate<T> or(final Predicate<? super T> other) {
/* 35 */     return new Predicate<T>()
/*    */       {
/*    */         public boolean test(T arg) {
/* 38 */           return (Predicate.this.test(arg) || other.test(arg));
/*    */         }
/*    */       };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default Predicate<T> and(final Predicate<? super T> other) {
/* 52 */     return new Predicate<T>()
/*    */       {
/*    */         public boolean test(T arg) {
/* 55 */           return (Predicate.this.test(arg) && other.test(arg));
/*    */         }
/*    */       };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default Predicate<T> negate() {
/* 66 */     return new Predicate<T>()
/*    */       {
/*    */         public boolean test(T arg) {
/* 69 */           return !Predicate.this.test(arg);
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/Predicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */