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
/*    */ public class RtcCertificatePem
/*    */ {
/*    */   public final String privateKey;
/*    */   public final String certificate;
/*    */   private static final long DEFAULT_EXPIRY = 2592000L;
/*    */   
/*    */   @CalledByNative
/*    */   public RtcCertificatePem(String privateKey, String certificate) {
/* 29 */     this.privateKey = privateKey;
/* 30 */     this.certificate = certificate;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   String getPrivateKey() {
/* 35 */     return this.privateKey;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   String getCertificate() {
/* 40 */     return this.certificate;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static RtcCertificatePem generateCertificate() {
/* 48 */     return nativeGenerateCertificate(PeerConnection.KeyType.ECDSA, 2592000L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static RtcCertificatePem generateCertificate(PeerConnection.KeyType keyType) {
/* 56 */     return nativeGenerateCertificate(keyType, 2592000L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static RtcCertificatePem generateCertificate(long expires) {
/* 64 */     return nativeGenerateCertificate(PeerConnection.KeyType.ECDSA, expires);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static RtcCertificatePem generateCertificate(PeerConnection.KeyType keyType, long expires) {
/* 70 */     return nativeGenerateCertificate(keyType, expires);
/*    */   }
/*    */   
/*    */   private static native RtcCertificatePem nativeGenerateCertificate(PeerConnection.KeyType paramKeyType, long paramLong);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RtcCertificatePem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */