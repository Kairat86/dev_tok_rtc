/*     */ package org.webrtc;
/*     */ 
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
/*     */ 
/*     */ class WrappedNativeI420Buffer
/*     */   implements VideoFrame.I420Buffer
/*     */ {
/*     */   private final int width;
/*     */   private final int height;
/*     */   private final ByteBuffer dataY;
/*     */   private final int strideY;
/*     */   private final ByteBuffer dataU;
/*     */   private final int strideU;
/*     */   private final ByteBuffer dataV;
/*     */   private final int strideV;
/*     */   private final long nativeBuffer;
/*     */   
/*     */   @CalledByNative
/*     */   WrappedNativeI420Buffer(int width, int height, ByteBuffer dataY, int strideY, ByteBuffer dataU, int strideU, ByteBuffer dataV, int strideV, long nativeBuffer) {
/*  32 */     this.width = width;
/*  33 */     this.height = height;
/*  34 */     this.dataY = dataY;
/*  35 */     this.strideY = strideY;
/*  36 */     this.dataU = dataU;
/*  37 */     this.strideU = strideU;
/*  38 */     this.dataV = dataV;
/*  39 */     this.strideV = strideV;
/*  40 */     this.nativeBuffer = nativeBuffer;
/*     */     
/*  42 */     retain();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWidth() {
/*  47 */     return this.width;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/*  52 */     return this.height;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer getDataY() {
/*  58 */     return this.dataY.slice();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer getDataU() {
/*  64 */     return this.dataU.slice();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer getDataV() {
/*  70 */     return this.dataV.slice();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStrideY() {
/*  75 */     return this.strideY;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStrideU() {
/*  80 */     return this.strideU;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStrideV() {
/*  85 */     return this.strideV;
/*     */   }
/*     */ 
/*     */   
/*     */   public VideoFrame.I420Buffer toI420() {
/*  90 */     retain();
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void retain() {
/*  96 */     JniCommon.nativeAddRef(this.nativeBuffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void release() {
/* 101 */     JniCommon.nativeReleaseRef(this.nativeBuffer);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public VideoFrame.Buffer cropAndScale(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
/* 107 */     return JavaI420Buffer.cropAndScaleI420(this, cropX, cropY, cropWidth, cropHeight, scaleWidth, scaleHeight);
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/WrappedNativeI420Buffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */