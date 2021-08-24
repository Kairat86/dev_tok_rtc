/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
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
/*    */ public final class PeerConnectionDependencies
/*    */ {
/*    */   private final PeerConnection.Observer observer;
/*    */   private final SSLCertificateVerifier sslCertificateVerifier;
/*    */   
/*    */   public static class Builder
/*    */   {
/*    */     private PeerConnection.Observer observer;
/*    */     private SSLCertificateVerifier sslCertificateVerifier;
/*    */     
/*    */     private Builder(PeerConnection.Observer observer) {
/* 33 */       this.observer = observer;
/*    */     }
/*    */     
/*    */     public Builder setSSLCertificateVerifier(SSLCertificateVerifier sslCertificateVerifier) {
/* 37 */       this.sslCertificateVerifier = sslCertificateVerifier;
/* 38 */       return this;
/*    */     }
/*    */ 
/*    */     
/*    */     public PeerConnectionDependencies createPeerConnectionDependencies() {
/* 43 */       return new PeerConnectionDependencies(this.observer, this.sslCertificateVerifier);
/*    */     }
/*    */   }
/*    */   
/*    */   public static Builder builder(PeerConnection.Observer observer) {
/* 48 */     return new Builder(observer);
/*    */   }
/*    */   
/*    */   PeerConnection.Observer getObserver() {
/* 52 */     return this.observer;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   SSLCertificateVerifier getSSLCertificateVerifier() {
/* 57 */     return this.sslCertificateVerifier;
/*    */   }
/*    */ 
/*    */   
/*    */   private PeerConnectionDependencies(PeerConnection.Observer observer, SSLCertificateVerifier sslCertificateVerifier) {
/* 62 */     this.observer = observer;
/* 63 */     this.sslCertificateVerifier = sslCertificateVerifier;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/PeerConnectionDependencies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */