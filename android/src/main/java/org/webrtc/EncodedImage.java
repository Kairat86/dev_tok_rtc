/*     */ package org.webrtc;
/*     */ 
/*     */ import androidx.annotation.Nullable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class EncodedImage
/*     */   implements RefCounted
/*     */ {
/*     */   private final RefCountDelegate refCountDelegate;
/*     */   public final ByteBuffer buffer;
/*     */   public final int encodedWidth;
/*     */   public final int encodedHeight;
/*     */   public final long captureTimeMs;
/*     */   public final long captureTimeNs;
/*     */   public final FrameType frameType;
/*     */   public final int rotation;
/*     */   public final boolean completeFrame;
/*     */   @Nullable
/*     */   public final Integer qp;
/*     */   
/*     */   public enum FrameType
/*     */   {
/*  24 */     EmptyFrame(0),
/*  25 */     VideoFrameKey(3),
/*  26 */     VideoFrameDelta(4);
/*     */     
/*     */     private final int nativeIndex;
/*     */     
/*     */     FrameType(int nativeIndex) {
/*  31 */       this.nativeIndex = nativeIndex;
/*     */     }
/*     */     
/*     */     public int getNative() {
/*  35 */       return this.nativeIndex;
/*     */     }
/*     */     
/*     */     @CalledByNative("FrameType")
/*     */     static FrameType fromNativeIndex(int nativeIndex) {
/*  40 */       for (FrameType type : values()) {
/*  41 */         if (type.getNative() == nativeIndex) {
/*  42 */           return type;
/*     */         }
/*     */       } 
/*  45 */       throw new IllegalArgumentException("Unknown native frame type: " + nativeIndex);
/*     */     }
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
/*     */   public void retain() {
/*  63 */     this.refCountDelegate.retain();
/*     */   }
/*     */ 
/*     */   
/*     */   public void release() {
/*  68 */     this.refCountDelegate.release();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   private EncodedImage(ByteBuffer buffer, @Nullable Runnable releaseCallback, int encodedWidth, int encodedHeight, long captureTimeNs, FrameType frameType, int rotation, boolean completeFrame, @Nullable Integer qp) {
/*  75 */     this.buffer = buffer;
/*  76 */     this.encodedWidth = encodedWidth;
/*  77 */     this.encodedHeight = encodedHeight;
/*  78 */     this.captureTimeMs = TimeUnit.NANOSECONDS.toMillis(captureTimeNs);
/*  79 */     this.captureTimeNs = captureTimeNs;
/*  80 */     this.frameType = frameType;
/*  81 */     this.rotation = rotation;
/*  82 */     this.completeFrame = completeFrame;
/*  83 */     this.qp = qp;
/*  84 */     this.refCountDelegate = new RefCountDelegate(releaseCallback);
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private ByteBuffer getBuffer() {
/*  89 */     return this.buffer;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private int getEncodedWidth() {
/*  94 */     return this.encodedWidth;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private int getEncodedHeight() {
/*  99 */     return this.encodedHeight;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private long getCaptureTimeNs() {
/* 104 */     return this.captureTimeNs;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private int getFrameType() {
/* 109 */     return this.frameType.getNative();
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private int getRotation() {
/* 114 */     return this.rotation;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private boolean getCompleteFrame() {
/* 119 */     return this.completeFrame;
/*     */   }
/*     */   @CalledByNative
/*     */   @Nullable
/*     */   private Integer getQp() {
/* 124 */     return this.qp;
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/* 128 */     return new Builder();
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     private ByteBuffer buffer;
/*     */     
/*     */     @Nullable
/*     */     private Runnable releaseCallback;
/*     */     
/*     */     private int encodedWidth;
/*     */     
/*     */     private int encodedHeight;
/*     */     private long captureTimeNs;
/*     */     
/*     */     public Builder setBuffer(ByteBuffer buffer, @Nullable Runnable releaseCallback) {
/* 145 */       this.buffer = buffer;
/* 146 */       this.releaseCallback = releaseCallback;
/* 147 */       return this;
/*     */     } private EncodedImage.FrameType frameType; private int rotation; private boolean completeFrame; @Nullable
/*     */     private Integer qp; private Builder() {}
/*     */     public Builder setEncodedWidth(int encodedWidth) {
/* 151 */       this.encodedWidth = encodedWidth;
/* 152 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setEncodedHeight(int encodedHeight) {
/* 156 */       this.encodedHeight = encodedHeight;
/* 157 */       return this;
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public Builder setCaptureTimeMs(long captureTimeMs) {
/* 162 */       this.captureTimeNs = TimeUnit.MILLISECONDS.toNanos(captureTimeMs);
/* 163 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCaptureTimeNs(long captureTimeNs) {
/* 167 */       this.captureTimeNs = captureTimeNs;
/* 168 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setFrameType(EncodedImage.FrameType frameType) {
/* 172 */       this.frameType = frameType;
/* 173 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setRotation(int rotation) {
/* 177 */       this.rotation = rotation;
/* 178 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCompleteFrame(boolean completeFrame) {
/* 182 */       this.completeFrame = completeFrame;
/* 183 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setQp(@Nullable Integer qp) {
/* 187 */       this.qp = qp;
/* 188 */       return this;
/*     */     }
/*     */     
/*     */     public EncodedImage createEncodedImage() {
/* 192 */       return new EncodedImage(this.buffer, this.releaseCallback, this.encodedWidth, this.encodedHeight, this.captureTimeNs, this.frameType, this.rotation, this.completeFrame, this.qp);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/EncodedImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */