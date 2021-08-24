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
/*    */ public final class CandidatePairChangeEvent
/*    */ {
/*    */   public final IceCandidate local;
/*    */   public final IceCandidate remote;
/*    */   public final int lastDataReceivedMs;
/*    */   public final String reason;
/*    */   public final int estimatedDisconnectedTimeMs;
/*    */   
/*    */   @CalledByNative
/*    */   CandidatePairChangeEvent(IceCandidate local, IceCandidate remote, int lastDataReceivedMs, String reason, int estimatedDisconnectedTimeMs) {
/* 33 */     this.local = local;
/* 34 */     this.remote = remote;
/* 35 */     this.lastDataReceivedMs = lastDataReceivedMs;
/* 36 */     this.reason = reason;
/* 37 */     this.estimatedDisconnectedTimeMs = estimatedDisconnectedTimeMs;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/CandidatePairChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */