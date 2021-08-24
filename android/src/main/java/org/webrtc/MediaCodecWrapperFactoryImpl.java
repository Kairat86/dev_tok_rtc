/*     */ package org.webrtc;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.media.MediaCodec;
/*     */ import android.media.MediaCrypto;
/*     */ import android.media.MediaFormat;
/*     */ import android.os.Bundle;
/*     */ import android.view.Surface;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
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
/*     */ class MediaCodecWrapperFactoryImpl
/*     */   implements MediaCodecWrapperFactory
/*     */ {
/*     */   private static class MediaCodecWrapperImpl
/*     */     implements MediaCodecWrapper
/*     */   {
/*     */     private final MediaCodec mediaCodec;
/*     */     
/*     */     public MediaCodecWrapperImpl(MediaCodec mediaCodec) {
/*  32 */       this.mediaCodec = mediaCodec;
/*     */     }
/*     */ 
/*     */     
/*     */     public void configure(MediaFormat format, Surface surface, MediaCrypto crypto, int flags) {
/*  37 */       this.mediaCodec.configure(format, surface, crypto, flags);
/*     */     }
/*     */ 
/*     */     
/*     */     public void start() {
/*  42 */       this.mediaCodec.start();
/*     */     }
/*     */ 
/*     */     
/*     */     public void flush() {
/*  47 */       this.mediaCodec.flush();
/*     */     }
/*     */ 
/*     */     
/*     */     public void stop() {
/*  52 */       this.mediaCodec.stop();
/*     */     }
/*     */ 
/*     */     
/*     */     public void release() {
/*  57 */       this.mediaCodec.release();
/*     */     }
/*     */ 
/*     */     
/*     */     public int dequeueInputBuffer(long timeoutUs) {
/*  62 */       return this.mediaCodec.dequeueInputBuffer(timeoutUs);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void queueInputBuffer(int index, int offset, int size, long presentationTimeUs, int flags) {
/*  68 */       this.mediaCodec.queueInputBuffer(index, offset, size, presentationTimeUs, flags);
/*     */     }
/*     */ 
/*     */     
/*     */     public int dequeueOutputBuffer(MediaCodec.BufferInfo info, long timeoutUs) {
/*  73 */       return this.mediaCodec.dequeueOutputBuffer(info, timeoutUs);
/*     */     }
/*     */ 
/*     */     
/*     */     public void releaseOutputBuffer(int index, boolean render) {
/*  78 */       this.mediaCodec.releaseOutputBuffer(index, render);
/*     */     }
/*     */ 
/*     */     
/*     */     public MediaFormat getOutputFormat() {
/*  83 */       return this.mediaCodec.getOutputFormat();
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuffer[] getInputBuffers() {
/*  88 */       return this.mediaCodec.getInputBuffers();
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuffer[] getOutputBuffers() {
/*  93 */       return this.mediaCodec.getOutputBuffers();
/*     */     }
/*     */ 
/*     */     
/*     */     @TargetApi(18)
/*     */     public Surface createInputSurface() {
/*  99 */       return this.mediaCodec.createInputSurface();
/*     */     }
/*     */ 
/*     */     
/*     */     @TargetApi(19)
/*     */     public void setParameters(Bundle params) {
/* 105 */       this.mediaCodec.setParameters(params);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public MediaCodecWrapper createByCodecName(String name) throws IOException {
/* 111 */     return new MediaCodecWrapperImpl(MediaCodec.createByCodecName(name));
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/MediaCodecWrapperFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */