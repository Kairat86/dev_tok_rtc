/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
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
/*    */ class RefCountDelegate
/*    */   implements RefCounted
/*    */ {
/* 20 */   private final AtomicInteger refCount = new AtomicInteger(1);
/*    */   
/*    */   @Nullable
/*    */   private final Runnable releaseCallback;
/*    */ 
/*    */   
/*    */   public RefCountDelegate(@Nullable Runnable releaseCallback) {
/* 27 */     this.releaseCallback = releaseCallback;
/*    */   }
/*    */ 
/*    */   
/*    */   public void retain() {
/* 32 */     int updated_count = this.refCount.incrementAndGet();
/* 33 */     if (updated_count < 2) {
/* 34 */       throw new IllegalStateException("retain() called on an object with refcount < 1");
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void release() {
/* 40 */     int updated_count = this.refCount.decrementAndGet();
/* 41 */     if (updated_count < 0) {
/* 42 */       throw new IllegalStateException("release() called on an object with refcount < 1");
/*    */     }
/* 44 */     if (updated_count == 0 && this.releaseCallback != null) {
/* 45 */       this.releaseCallback.run();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   boolean safeRetain() {
/* 54 */     int currentRefCount = this.refCount.get();
/* 55 */     while (currentRefCount != 0) {
/* 56 */       if (this.refCount.weakCompareAndSet(currentRefCount, currentRefCount + 1)) {
/* 57 */         return true;
/*    */       }
/* 59 */       currentRefCount = this.refCount.get();
/*    */     } 
/* 61 */     return false;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RefCountDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */