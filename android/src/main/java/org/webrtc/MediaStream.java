/*     */ package org.webrtc;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
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
/*     */ public class MediaStream
/*     */ {
/*     */   private static final String TAG = "MediaStream";
/*  21 */   public final List<AudioTrack> audioTracks = new ArrayList<>();
/*  22 */   public final List<VideoTrack> videoTracks = new ArrayList<>();
/*  23 */   public final List<VideoTrack> preservedVideoTracks = new ArrayList<>();
/*     */   private long nativeStream;
/*     */   
/*     */   @CalledByNative
/*     */   public MediaStream(long nativeStream) {
/*  28 */     this.nativeStream = nativeStream;
/*     */   }
/*     */   
/*     */   public boolean addTrack(AudioTrack track) {
/*  32 */     checkMediaStreamExists();
/*  33 */     if (nativeAddAudioTrackToNativeStream(this.nativeStream, track.getNativeAudioTrack())) {
/*  34 */       this.audioTracks.add(track);
/*  35 */       return true;
/*     */     } 
/*  37 */     return false;
/*     */   }
/*     */   
/*     */   public boolean addTrack(VideoTrack track) {
/*  41 */     checkMediaStreamExists();
/*  42 */     if (nativeAddVideoTrackToNativeStream(this.nativeStream, track.getNativeVideoTrack())) {
/*  43 */       this.videoTracks.add(track);
/*  44 */       return true;
/*     */     } 
/*  46 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addPreservedTrack(VideoTrack track) {
/*  53 */     checkMediaStreamExists();
/*  54 */     if (nativeAddVideoTrackToNativeStream(this.nativeStream, track.getNativeVideoTrack())) {
/*  55 */       this.preservedVideoTracks.add(track);
/*  56 */       return true;
/*     */     } 
/*  58 */     return false;
/*     */   }
/*     */   
/*     */   public boolean removeTrack(AudioTrack track) {
/*  62 */     checkMediaStreamExists();
/*  63 */     this.audioTracks.remove(track);
/*  64 */     return nativeRemoveAudioTrack(this.nativeStream, track.getNativeAudioTrack());
/*     */   }
/*     */   
/*     */   public boolean removeTrack(VideoTrack track) {
/*  68 */     checkMediaStreamExists();
/*  69 */     this.videoTracks.remove(track);
/*  70 */     this.preservedVideoTracks.remove(track);
/*  71 */     return nativeRemoveVideoTrack(this.nativeStream, track.getNativeVideoTrack());
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   public void dispose() {
/*  76 */     checkMediaStreamExists();
/*     */     
/*  78 */     while (!this.audioTracks.isEmpty()) {
/*  79 */       AudioTrack track = this.audioTracks.get(0);
/*  80 */       removeTrack(track);
/*  81 */       track.dispose();
/*     */     } 
/*  83 */     while (!this.videoTracks.isEmpty()) {
/*  84 */       VideoTrack track = this.videoTracks.get(0);
/*  85 */       removeTrack(track);
/*  86 */       track.dispose();
/*     */     } 
/*     */     
/*  89 */     while (!this.preservedVideoTracks.isEmpty()) {
/*  90 */       removeTrack(this.preservedVideoTracks.get(0));
/*     */     }
/*  92 */     JniCommon.nativeReleaseRef(this.nativeStream);
/*  93 */     this.nativeStream = 0L;
/*     */   }
/*     */   
/*     */   public String getId() {
/*  97 */     checkMediaStreamExists();
/*  98 */     return nativeGetId(this.nativeStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 103 */     return "[" + getId() + ":A=" + this.audioTracks.size() + ":V=" + this.videoTracks.size() + "]";
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   void addNativeAudioTrack(long nativeTrack) {
/* 108 */     this.audioTracks.add(new AudioTrack(nativeTrack));
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   void addNativeVideoTrack(long nativeTrack) {
/* 113 */     this.videoTracks.add(new VideoTrack(nativeTrack));
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   void removeAudioTrack(long nativeTrack) {
/* 118 */     removeMediaStreamTrack((List)this.audioTracks, nativeTrack);
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   void removeVideoTrack(long nativeTrack) {
/* 123 */     removeMediaStreamTrack((List)this.videoTracks, nativeTrack);
/*     */   }
/*     */ 
/*     */   
/*     */   long getNativeMediaStream() {
/* 128 */     checkMediaStreamExists();
/* 129 */     return this.nativeStream;
/*     */   }
/*     */   
/*     */   private void checkMediaStreamExists() {
/* 133 */     if (this.nativeStream == 0L) {
/* 134 */       throw new IllegalStateException("MediaStream has been disposed.");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void removeMediaStreamTrack(List<? extends MediaStreamTrack> tracks, long nativeTrack) {
/* 140 */     Iterator<? extends MediaStreamTrack> it = tracks.iterator();
/* 141 */     while (it.hasNext()) {
/* 142 */       MediaStreamTrack track = it.next();
/* 143 */       if (track.getNativeMediaStreamTrack() == nativeTrack) {
/* 144 */         track.dispose();
/* 145 */         it.remove();
/*     */         return;
/*     */       } 
/*     */     } 
/* 149 */     Logging.e("MediaStream", "Couldn't not find track");
/*     */   }
/*     */   
/*     */   private static native boolean nativeAddAudioTrackToNativeStream(long paramLong1, long paramLong2);
/*     */   
/*     */   private static native boolean nativeAddVideoTrackToNativeStream(long paramLong1, long paramLong2);
/*     */   
/*     */   private static native boolean nativeRemoveAudioTrack(long paramLong1, long paramLong2);
/*     */   
/*     */   private static native boolean nativeRemoveVideoTrack(long paramLong1, long paramLong2);
/*     */   
/*     */   private static native String nativeGetId(long paramLong);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/MediaStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */