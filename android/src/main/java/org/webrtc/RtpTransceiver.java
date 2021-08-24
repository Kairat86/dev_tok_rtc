/*     */ package org.webrtc;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ public class RtpTransceiver
/*     */ {
/*     */   private long nativeRtpTransceiver;
/*     */   private RtpSender cachedSender;
/*     */   private RtpReceiver cachedReceiver;
/*     */   
/*     */   public enum RtpTransceiverDirection
/*     */   {
/*  38 */     SEND_RECV(0),
/*  39 */     SEND_ONLY(1),
/*  40 */     RECV_ONLY(2),
/*  41 */     INACTIVE(3);
/*     */     
/*     */     private final int nativeIndex;
/*     */     
/*     */     RtpTransceiverDirection(int nativeIndex) {
/*  46 */       this.nativeIndex = nativeIndex;
/*     */     }
/*     */     
/*     */     @CalledByNative("RtpTransceiverDirection")
/*     */     int getNativeIndex() {
/*  51 */       return this.nativeIndex;
/*     */     }
/*     */     
/*     */     @CalledByNative("RtpTransceiverDirection")
/*     */     static RtpTransceiverDirection fromNativeIndex(int nativeIndex) {
/*  56 */       for (RtpTransceiverDirection type : values()) {
/*  57 */         if (type.getNativeIndex() == nativeIndex) {
/*  58 */           return type;
/*     */         }
/*     */       } 
/*  61 */       throw new IllegalArgumentException("Uknown native RtpTransceiverDirection type" + nativeIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class RtpTransceiverInit
/*     */   {
/*     */     private final RtpTransceiver.RtpTransceiverDirection direction;
/*     */ 
/*     */     
/*     */     private final List<String> streamIds;
/*     */     
/*     */     private final List<RtpParameters.Encoding> sendEncodings;
/*     */ 
/*     */     
/*     */     public RtpTransceiverInit() {
/*  78 */       this(RtpTransceiver.RtpTransceiverDirection.SEND_RECV);
/*     */     }
/*     */     
/*     */     public RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection direction) {
/*  82 */       this(direction, Collections.emptyList(), Collections.emptyList());
/*     */     }
/*     */     
/*     */     public RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection direction, List<String> streamIds) {
/*  86 */       this(direction, streamIds, Collections.emptyList());
/*     */     }
/*     */ 
/*     */     
/*     */     public RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection direction, List<String> streamIds, List<RtpParameters.Encoding> sendEncodings) {
/*  91 */       this.direction = direction;
/*  92 */       this.streamIds = new ArrayList<>(streamIds);
/*  93 */       this.sendEncodings = new ArrayList<>(sendEncodings);
/*     */     }
/*     */     
/*     */     @CalledByNative("RtpTransceiverInit")
/*     */     int getDirectionNativeIndex() {
/*  98 */       return this.direction.getNativeIndex();
/*     */     }
/*     */     
/*     */     @CalledByNative("RtpTransceiverInit")
/*     */     List<String> getStreamIds() {
/* 103 */       return new ArrayList<>(this.streamIds);
/*     */     }
/*     */     
/*     */     @CalledByNative("RtpTransceiverInit")
/*     */     List<RtpParameters.Encoding> getSendEncodings() {
/* 108 */       return new ArrayList<>(this.sendEncodings);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   protected RtpTransceiver(long nativeRtpTransceiver) {
/* 118 */     this.nativeRtpTransceiver = nativeRtpTransceiver;
/* 119 */     this.cachedSender = nativeGetSender(nativeRtpTransceiver);
/* 120 */     this.cachedReceiver = nativeGetReceiver(nativeRtpTransceiver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MediaStreamTrack.MediaType getMediaType() {
/* 128 */     checkRtpTransceiverExists();
/* 129 */     return nativeGetMediaType(this.nativeRtpTransceiver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMid() {
/* 139 */     checkRtpTransceiverExists();
/* 140 */     return nativeGetMid(this.nativeRtpTransceiver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RtpSender getSender() {
/* 150 */     return this.cachedSender;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RtpReceiver getReceiver() {
/* 160 */     return this.cachedReceiver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStopped() {
/* 171 */     checkRtpTransceiverExists();
/* 172 */     return nativeStopped(this.nativeRtpTransceiver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RtpTransceiverDirection getDirection() {
/* 181 */     checkRtpTransceiverExists();
/* 182 */     return nativeDirection(this.nativeRtpTransceiver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RtpTransceiverDirection getCurrentDirection() {
/* 192 */     checkRtpTransceiverExists();
/* 193 */     return nativeCurrentDirection(this.nativeRtpTransceiver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setDirection(RtpTransceiverDirection rtpTransceiverDirection) {
/* 204 */     checkRtpTransceiverExists();
/* 205 */     return nativeSetDirection(this.nativeRtpTransceiver, rtpTransceiverDirection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 213 */     checkRtpTransceiverExists();
/* 214 */     nativeStopInternal(this.nativeRtpTransceiver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stopInternal() {
/* 222 */     checkRtpTransceiverExists();
/* 223 */     nativeStopInternal(this.nativeRtpTransceiver);
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
/*     */   public void stopStandard() {
/* 235 */     checkRtpTransceiverExists();
/* 236 */     nativeStopStandard(this.nativeRtpTransceiver);
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   public void dispose() {
/* 241 */     checkRtpTransceiverExists();
/* 242 */     this.cachedSender.dispose();
/* 243 */     this.cachedReceiver.dispose();
/* 244 */     JniCommon.nativeReleaseRef(this.nativeRtpTransceiver);
/* 245 */     this.nativeRtpTransceiver = 0L;
/*     */   }
/*     */   
/*     */   private void checkRtpTransceiverExists() {
/* 249 */     if (this.nativeRtpTransceiver == 0L)
/* 250 */       throw new IllegalStateException("RtpTransceiver has been disposed."); 
/*     */   }
/*     */   
/*     */   private static native MediaStreamTrack.MediaType nativeGetMediaType(long paramLong);
/*     */   
/*     */   private static native String nativeGetMid(long paramLong);
/*     */   
/*     */   private static native RtpSender nativeGetSender(long paramLong);
/*     */   
/*     */   private static native RtpReceiver nativeGetReceiver(long paramLong);
/*     */   
/*     */   private static native boolean nativeStopped(long paramLong);
/*     */   
/*     */   private static native RtpTransceiverDirection nativeDirection(long paramLong);
/*     */   
/*     */   private static native RtpTransceiverDirection nativeCurrentDirection(long paramLong);
/*     */   
/*     */   private static native void nativeStopInternal(long paramLong);
/*     */   
/*     */   private static native void nativeStopStandard(long paramLong);
/*     */   
/*     */   private static native boolean nativeSetDirection(long paramLong, RtpTransceiverDirection paramRtpTransceiverDirection);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RtpTransceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */