/*     */ package org.webrtc;
/*     */ 
/*     */ import androidx.annotation.Nullable;
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
/*     */ public class MediaStreamTrack
/*     */ {
/*     */   public static final String AUDIO_TRACK_KIND = "audio";
/*     */   public static final String VIDEO_TRACK_KIND = "video";
/*     */   private long nativeTrack;
/*     */   
/*     */   public enum State
/*     */   {
/*  22 */     LIVE,
/*  23 */     ENDED;
/*     */     
/*     */     @CalledByNative("State")
/*     */     static State fromNativeIndex(int nativeIndex) {
/*  27 */       return values()[nativeIndex];
/*     */     }
/*     */   }
/*     */   
/*     */   public enum MediaType
/*     */   {
/*  33 */     MEDIA_TYPE_AUDIO(0),
/*  34 */     MEDIA_TYPE_VIDEO(1);
/*     */     
/*     */     private final int nativeIndex;
/*     */     
/*     */     MediaType(int nativeIndex) {
/*  39 */       this.nativeIndex = nativeIndex;
/*     */     }
/*     */     
/*     */     @CalledByNative("MediaType")
/*     */     int getNative() {
/*  44 */       return this.nativeIndex;
/*     */     }
/*     */     
/*     */     @CalledByNative("MediaType")
/*     */     static MediaType fromNativeIndex(int nativeIndex) {
/*  49 */       for (MediaType type : values()) {
/*  50 */         if (type.getNative() == nativeIndex) {
/*  51 */           return type;
/*     */         }
/*     */       } 
/*  54 */       throw new IllegalArgumentException("Unknown native media type: " + nativeIndex);
/*     */     }
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   static MediaStreamTrack createMediaStreamTrack(long nativeTrack) {
/*  60 */     if (nativeTrack == 0L) {
/*  61 */       return null;
/*     */     }
/*  63 */     String trackKind = nativeGetKind(nativeTrack);
/*  64 */     if (trackKind.equals("audio"))
/*  65 */       return new AudioTrack(nativeTrack); 
/*  66 */     if (trackKind.equals("video")) {
/*  67 */       return new VideoTrack(nativeTrack);
/*     */     }
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MediaStreamTrack(long nativeTrack) {
/*  76 */     if (nativeTrack == 0L) {
/*  77 */       throw new IllegalArgumentException("nativeTrack may not be null");
/*     */     }
/*  79 */     this.nativeTrack = nativeTrack;
/*     */   }
/*     */   
/*     */   public String id() {
/*  83 */     checkMediaStreamTrackExists();
/*  84 */     return nativeGetId(this.nativeTrack);
/*     */   }
/*     */   
/*     */   public String kind() {
/*  88 */     checkMediaStreamTrackExists();
/*  89 */     return nativeGetKind(this.nativeTrack);
/*     */   }
/*     */   
/*     */   public boolean enabled() {
/*  93 */     checkMediaStreamTrackExists();
/*  94 */     return nativeGetEnabled(this.nativeTrack);
/*     */   }
/*     */   
/*     */   public boolean setEnabled(boolean enable) {
/*  98 */     checkMediaStreamTrackExists();
/*  99 */     return nativeSetEnabled(this.nativeTrack, enable);
/*     */   }
/*     */   
/*     */   public State state() {
/* 103 */     checkMediaStreamTrackExists();
/* 104 */     return nativeGetState(this.nativeTrack);
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 108 */     checkMediaStreamTrackExists();
/* 109 */     JniCommon.nativeReleaseRef(this.nativeTrack);
/* 110 */     this.nativeTrack = 0L;
/*     */   }
/*     */   
/*     */   long getNativeMediaStreamTrack() {
/* 114 */     checkMediaStreamTrackExists();
/* 115 */     return this.nativeTrack;
/*     */   }
/*     */   
/*     */   private void checkMediaStreamTrackExists() {
/* 119 */     if (this.nativeTrack == 0L)
/* 120 */       throw new IllegalStateException("MediaStreamTrack has been disposed."); 
/*     */   }
/*     */   
/*     */   private static native String nativeGetId(long paramLong);
/*     */   
/*     */   private static native String nativeGetKind(long paramLong);
/*     */   
/*     */   private static native boolean nativeGetEnabled(long paramLong);
/*     */   
/*     */   private static native boolean nativeSetEnabled(long paramLong, boolean paramBoolean);
/*     */   
/*     */   private static native State nativeGetState(long paramLong);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/MediaStreamTrack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */