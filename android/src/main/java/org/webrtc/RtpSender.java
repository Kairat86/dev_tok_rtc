/*     */ package org.webrtc;
/*     */ 
/*     */ import androidx.annotation.Nullable;
/*     */ import java.util.List;
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
/*     */ public class RtpSender
/*     */ {
/*     */   private long nativeRtpSender;
/*     */   @Nullable
/*     */   private MediaStreamTrack cachedTrack;
/*     */   private boolean ownsTrack = true;
/*     */   @Nullable
/*     */   private final DtmfSender dtmfSender;
/*     */   
/*     */   @CalledByNative
/*     */   public RtpSender(long nativeRtpSender) {
/*  26 */     this.nativeRtpSender = nativeRtpSender;
/*  27 */     long nativeTrack = nativeGetTrack(nativeRtpSender);
/*  28 */     this.cachedTrack = MediaStreamTrack.createMediaStreamTrack(nativeTrack);
/*     */     
/*  30 */     long nativeDtmfSender = nativeGetDtmfSender(nativeRtpSender);
/*  31 */     this.dtmfSender = (nativeDtmfSender != 0L) ? new DtmfSender(nativeDtmfSender) : null;
/*     */   }
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
/*     */   public boolean setTrack(@Nullable MediaStreamTrack track, boolean takeOwnership) {
/*  49 */     checkRtpSenderExists();
/*  50 */     if (!nativeSetTrack(this.nativeRtpSender, (track == null) ? 0L : track.getNativeMediaStreamTrack())) {
/*  51 */       return false;
/*     */     }
/*  53 */     if (this.cachedTrack != null && this.ownsTrack) {
/*  54 */       this.cachedTrack.dispose();
/*     */     }
/*  56 */     this.cachedTrack = track;
/*  57 */     this.ownsTrack = takeOwnership;
/*  58 */     return true;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public MediaStreamTrack track() {
/*  63 */     return this.cachedTrack;
/*     */   }
/*     */   
/*     */   public void setStreams(List<String> streamIds) {
/*  67 */     checkRtpSenderExists();
/*  68 */     nativeSetStreams(this.nativeRtpSender, streamIds);
/*     */   }
/*     */   
/*     */   public List<String> getStreams() {
/*  72 */     checkRtpSenderExists();
/*  73 */     return nativeGetStreams(this.nativeRtpSender);
/*     */   }
/*     */   
/*     */   public boolean setParameters(RtpParameters parameters) {
/*  77 */     checkRtpSenderExists();
/*  78 */     return nativeSetParameters(this.nativeRtpSender, parameters);
/*     */   }
/*     */   
/*     */   public RtpParameters getParameters() {
/*  82 */     checkRtpSenderExists();
/*  83 */     return nativeGetParameters(this.nativeRtpSender);
/*     */   }
/*     */   
/*     */   public String id() {
/*  87 */     checkRtpSenderExists();
/*  88 */     return nativeGetId(this.nativeRtpSender);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public DtmfSender dtmf() {
/*  93 */     return this.dtmfSender;
/*     */   }
/*     */   
/*     */   public void setFrameEncryptor(FrameEncryptor frameEncryptor) {
/*  97 */     checkRtpSenderExists();
/*  98 */     nativeSetFrameEncryptor(this.nativeRtpSender, frameEncryptor.getNativeFrameEncryptor());
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 102 */     checkRtpSenderExists();
/* 103 */     if (this.dtmfSender != null) {
/* 104 */       this.dtmfSender.dispose();
/*     */     }
/* 106 */     if (this.cachedTrack != null && this.ownsTrack) {
/* 107 */       this.cachedTrack.dispose();
/*     */     }
/* 109 */     JniCommon.nativeReleaseRef(this.nativeRtpSender);
/* 110 */     this.nativeRtpSender = 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   long getNativeRtpSender() {
/* 115 */     checkRtpSenderExists();
/* 116 */     return this.nativeRtpSender;
/*     */   }
/*     */   
/*     */   private void checkRtpSenderExists() {
/* 120 */     if (this.nativeRtpSender == 0L)
/* 121 */       throw new IllegalStateException("RtpSender has been disposed."); 
/*     */   }
/*     */   
/*     */   private static native boolean nativeSetTrack(long paramLong1, long paramLong2);
/*     */   
/*     */   private static native long nativeGetTrack(long paramLong);
/*     */   
/*     */   private static native void nativeSetStreams(long paramLong, List<String> paramList);
/*     */   
/*     */   private static native List<String> nativeGetStreams(long paramLong);
/*     */   
/*     */   private static native long nativeGetDtmfSender(long paramLong);
/*     */   
/*     */   private static native boolean nativeSetParameters(long paramLong, RtpParameters paramRtpParameters);
/*     */   
/*     */   private static native RtpParameters nativeGetParameters(long paramLong);
/*     */   
/*     */   private static native String nativeGetId(long paramLong);
/*     */   
/*     */   private static native void nativeSetFrameEncryptor(long paramLong1, long paramLong2);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RtpSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */