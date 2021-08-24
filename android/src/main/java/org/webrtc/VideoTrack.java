/*    */ package org.webrtc;
/*    */ 
/*    */ import java.util.IdentityHashMap;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VideoTrack
/*    */   extends MediaStreamTrack
/*    */ {
/* 17 */   private final IdentityHashMap<VideoSink, Long> sinks = new IdentityHashMap<>();
/*    */   
/*    */   public VideoTrack(long nativeTrack) {
/* 20 */     super(nativeTrack);
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
/*    */   public void addSink(VideoSink sink) {
/* 32 */     if (sink == null) {
/* 33 */       throw new IllegalArgumentException("The VideoSink is not allowed to be null");
/*    */     }
/*    */ 
/*    */     
/* 37 */     if (!this.sinks.containsKey(sink)) {
/* 38 */       long nativeSink = nativeWrapSink(sink);
/* 39 */       this.sinks.put(sink, Long.valueOf(nativeSink));
/* 40 */       nativeAddSink(getNativeMediaStreamTrack(), nativeSink);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void removeSink(VideoSink sink) {
/* 50 */     Long nativeSink = this.sinks.remove(sink);
/* 51 */     if (nativeSink != null) {
/* 52 */       nativeRemoveSink(getNativeMediaStreamTrack(), nativeSink.longValue());
/* 53 */       nativeFreeSink(nativeSink.longValue());
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void dispose() {
/* 59 */     for (Iterator<Long> iterator = this.sinks.values().iterator(); iterator.hasNext(); ) { long nativeSink = ((Long)iterator.next()).longValue();
/* 60 */       nativeRemoveSink(getNativeMediaStreamTrack(), nativeSink);
/* 61 */       nativeFreeSink(nativeSink); }
/*    */     
/* 63 */     this.sinks.clear();
/* 64 */     super.dispose();
/*    */   }
/*    */ 
/*    */   
/*    */   long getNativeVideoTrack() {
/* 69 */     return getNativeMediaStreamTrack();
/*    */   }
/*    */   
/*    */   private static native void nativeAddSink(long paramLong1, long paramLong2);
/*    */   
/*    */   private static native void nativeRemoveSink(long paramLong1, long paramLong2);
/*    */   
/*    */   private static native long nativeWrapSink(VideoSink paramVideoSink);
/*    */   
/*    */   private static native void nativeFreeSink(long paramLong);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoTrack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */