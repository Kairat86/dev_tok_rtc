/*     */ package org.webrtc;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CryptoOptions
/*     */ {
/*     */   private final Srtp srtp;
/*     */   private final SFrame sframe;
/*     */   
/*     */   public final class Srtp
/*     */   {
/*     */     private final boolean enableGcmCryptoSuites;
/*     */     private final boolean enableAes128Sha1_32CryptoCipher;
/*     */     private final boolean enableEncryptedRtpHeaderExtensions;
/*     */     
/*     */     private Srtp(boolean enableGcmCryptoSuites, boolean enableAes128Sha1_32CryptoCipher, boolean enableEncryptedRtpHeaderExtensions) {
/*  45 */       this.enableGcmCryptoSuites = enableGcmCryptoSuites;
/*  46 */       this.enableAes128Sha1_32CryptoCipher = enableAes128Sha1_32CryptoCipher;
/*  47 */       this.enableEncryptedRtpHeaderExtensions = enableEncryptedRtpHeaderExtensions;
/*     */     }
/*     */     
/*     */     @CalledByNative("Srtp")
/*     */     public boolean getEnableGcmCryptoSuites() {
/*  52 */       return this.enableGcmCryptoSuites;
/*     */     }
/*     */     
/*     */     @CalledByNative("Srtp")
/*     */     public boolean getEnableAes128Sha1_32CryptoCipher() {
/*  57 */       return this.enableAes128Sha1_32CryptoCipher;
/*     */     }
/*     */     
/*     */     @CalledByNative("Srtp")
/*     */     public boolean getEnableEncryptedRtpHeaderExtensions() {
/*  62 */       return this.enableEncryptedRtpHeaderExtensions;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final class SFrame
/*     */   {
/*     */     private final boolean requireFrameEncryption;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private SFrame(boolean requireFrameEncryption) {
/*  78 */       this.requireFrameEncryption = requireFrameEncryption;
/*     */     }
/*     */     
/*     */     @CalledByNative("SFrame")
/*     */     public boolean getRequireFrameEncryption() {
/*  83 */       return this.requireFrameEncryption;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CryptoOptions(boolean enableGcmCryptoSuites, boolean enableAes128Sha1_32CryptoCipher, boolean enableEncryptedRtpHeaderExtensions, boolean requireFrameEncryption) {
/*  92 */     this.srtp = new Srtp(enableGcmCryptoSuites, enableAes128Sha1_32CryptoCipher, enableEncryptedRtpHeaderExtensions);
/*     */     
/*  94 */     this.sframe = new SFrame(requireFrameEncryption);
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/*  98 */     return new Builder();
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   public Srtp getSrtp() {
/* 103 */     return this.srtp;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   public SFrame getSFrame() {
/* 108 */     return this.sframe;
/*     */   }
/*     */   
/*     */   public static class Builder {
/*     */     private boolean enableGcmCryptoSuites;
/*     */     private boolean enableAes128Sha1_32CryptoCipher;
/*     */     private boolean enableEncryptedRtpHeaderExtensions;
/*     */     private boolean requireFrameEncryption;
/*     */     
/*     */     private Builder() {}
/*     */     
/*     */     public Builder setEnableGcmCryptoSuites(boolean enableGcmCryptoSuites) {
/* 120 */       this.enableGcmCryptoSuites = enableGcmCryptoSuites;
/* 121 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setEnableAes128Sha1_32CryptoCipher(boolean enableAes128Sha1_32CryptoCipher) {
/* 125 */       this.enableAes128Sha1_32CryptoCipher = enableAes128Sha1_32CryptoCipher;
/* 126 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder setEnableEncryptedRtpHeaderExtensions(boolean enableEncryptedRtpHeaderExtensions) {
/* 131 */       this.enableEncryptedRtpHeaderExtensions = enableEncryptedRtpHeaderExtensions;
/* 132 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setRequireFrameEncryption(boolean requireFrameEncryption) {
/* 136 */       this.requireFrameEncryption = requireFrameEncryption;
/* 137 */       return this;
/*     */     }
/*     */     
/*     */     public CryptoOptions createCryptoOptions() {
/* 141 */       return new CryptoOptions(this.enableGcmCryptoSuites, this.enableAes128Sha1_32CryptoCipher, this.enableEncryptedRtpHeaderExtensions, this.requireFrameEncryption);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/CryptoOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */