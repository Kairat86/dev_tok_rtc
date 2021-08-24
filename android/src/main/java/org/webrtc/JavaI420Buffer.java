/*     */ package org.webrtc;
/*     */ 
/*     */ import androidx.annotation.Nullable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Objects;
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
/*     */ public class JavaI420Buffer
/*     */   implements VideoFrame.I420Buffer
/*     */ {
/*     */   private final int width;
/*     */   private final int height;
/*     */   private final ByteBuffer dataY;
/*     */   private final ByteBuffer dataU;
/*     */   private final ByteBuffer dataV;
/*     */   private final int strideY;
/*     */   private final int strideU;
/*     */   private final int strideV;
/*     */   private final RefCountDelegate refCountDelegate;
/*     */   
/*     */   private JavaI420Buffer(int width, int height, ByteBuffer dataY, int strideY, ByteBuffer dataU, int strideU, ByteBuffer dataV, int strideV, @Nullable Runnable releaseCallback) {
/*  31 */     this.width = width;
/*  32 */     this.height = height;
/*  33 */     this.dataY = dataY;
/*  34 */     this.dataU = dataU;
/*  35 */     this.dataV = dataV;
/*  36 */     this.strideY = strideY;
/*  37 */     this.strideU = strideU;
/*  38 */     this.strideV = strideV;
/*  39 */     this.refCountDelegate = new RefCountDelegate(releaseCallback);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void checkCapacity(ByteBuffer data, int width, int height, int stride) {
/*  44 */     int minCapacity = stride * (height - 1) + width;
/*  45 */     if (data.capacity() < minCapacity) {
/*  46 */       throw new IllegalArgumentException("Buffer must be at least " + minCapacity + " bytes, but was " + data
/*  47 */           .capacity());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JavaI420Buffer wrap(int width, int height, ByteBuffer dataY, int strideY, ByteBuffer dataU, int strideU, ByteBuffer dataV, int strideV, @Nullable Runnable releaseCallback) {
/*  55 */     if (dataY == null || dataU == null || dataV == null) {
/*  56 */       throw new IllegalArgumentException("Data buffers cannot be null.");
/*     */     }
/*  58 */     if (!dataY.isDirect() || !dataU.isDirect() || !dataV.isDirect()) {
/*  59 */       throw new IllegalArgumentException("Data buffers must be direct byte buffers.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  64 */     dataY = dataY.slice();
/*  65 */     dataU = dataU.slice();
/*  66 */     dataV = dataV.slice();
/*     */     
/*  68 */     int chromaWidth = (width + 1) / 2;
/*  69 */     int chromaHeight = (height + 1) / 2;
/*  70 */     checkCapacity(dataY, width, height, strideY);
/*  71 */     checkCapacity(dataU, chromaWidth, chromaHeight, strideU);
/*  72 */     checkCapacity(dataV, chromaWidth, chromaHeight, strideV);
/*     */     
/*  74 */     return new JavaI420Buffer(width, height, dataY, strideY, dataU, strideU, dataV, strideV, releaseCallback);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static JavaI420Buffer allocate(int width, int height) {
/*  80 */     int chromaHeight = (height + 1) / 2;
/*  81 */     int strideUV = (width + 1) / 2;
/*  82 */     int yPos = 0;
/*  83 */     int uPos = yPos + width * height;
/*  84 */     int vPos = uPos + strideUV * chromaHeight;
/*     */ 
/*     */     
/*  87 */     ByteBuffer buffer = JniCommon.nativeAllocateByteBuffer(width * height + 2 * strideUV * chromaHeight);
/*     */     
/*  89 */     buffer.position(yPos);
/*  90 */     buffer.limit(uPos);
/*  91 */     ByteBuffer dataY = buffer.slice();
/*     */     
/*  93 */     buffer.position(uPos);
/*  94 */     buffer.limit(vPos);
/*  95 */     ByteBuffer dataU = buffer.slice();
/*     */     
/*  97 */     buffer.position(vPos);
/*  98 */     buffer.limit(vPos + strideUV * chromaHeight);
/*  99 */     ByteBuffer dataV = buffer.slice();
/*     */     
/* 101 */     return new JavaI420Buffer(width, height, dataY, width, dataU, strideUV, dataV, strideUV, () -> JniCommon.nativeFreeByteBuffer(buffer));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWidth() {
/* 107 */     return this.width;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/* 112 */     return this.height;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer getDataY() {
/* 118 */     return this.dataY.slice();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer getDataU() {
/* 124 */     return this.dataU.slice();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer getDataV() {
/* 130 */     return this.dataV.slice();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStrideY() {
/* 135 */     return this.strideY;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStrideU() {
/* 140 */     return this.strideU;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStrideV() {
/* 145 */     return this.strideV;
/*     */   }
/*     */ 
/*     */   
/*     */   public VideoFrame.I420Buffer toI420() {
/* 150 */     retain();
/* 151 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void retain() {
/* 156 */     this.refCountDelegate.retain();
/*     */   }
/*     */ 
/*     */   
/*     */   public void release() {
/* 161 */     this.refCountDelegate.release();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public VideoFrame.Buffer cropAndScale(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
/* 167 */     return cropAndScaleI420(this, cropX, cropY, cropWidth, cropHeight, scaleWidth, scaleHeight);
/*     */   }
/*     */ 
/*     */   
/*     */   public static VideoFrame.Buffer cropAndScaleI420(VideoFrame.I420Buffer buffer, int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
/* 172 */     if (cropWidth == scaleWidth && cropHeight == scaleHeight) {
/*     */       
/* 174 */       ByteBuffer dataY = buffer.getDataY();
/* 175 */       ByteBuffer dataU = buffer.getDataU();
/* 176 */       ByteBuffer dataV = buffer.getDataV();
/*     */       
/* 178 */       dataY.position(cropX + cropY * buffer.getStrideY());
/* 179 */       dataU.position(cropX / 2 + cropY / 2 * buffer.getStrideU());
/* 180 */       dataV.position(cropX / 2 + cropY / 2 * buffer.getStrideV());
/*     */       
/* 182 */       buffer.retain();
/*     */       
/* 184 */       Objects.requireNonNull(buffer); return wrap(scaleWidth, scaleHeight, dataY.slice(), buffer.getStrideY(), dataU.slice(), buffer.getStrideU(), dataV.slice(), buffer.getStrideV(), buffer::release);
/*     */     } 
/*     */     
/* 187 */     JavaI420Buffer newBuffer = allocate(scaleWidth, scaleHeight);
/* 188 */     nativeCropAndScaleI420(buffer.getDataY(), buffer.getStrideY(), buffer.getDataU(), buffer
/* 189 */         .getStrideU(), buffer.getDataV(), buffer.getStrideV(), cropX, cropY, cropWidth, cropHeight, newBuffer
/* 190 */         .getDataY(), newBuffer.getStrideY(), newBuffer.getDataU(), newBuffer
/* 191 */         .getStrideU(), newBuffer.getDataV(), newBuffer.getStrideV(), scaleWidth, scaleHeight);
/*     */     
/* 193 */     return newBuffer;
/*     */   }
/*     */   
/*     */   private static native void nativeCropAndScaleI420(ByteBuffer paramByteBuffer1, int paramInt1, ByteBuffer paramByteBuffer2, int paramInt2, ByteBuffer paramByteBuffer3, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, ByteBuffer paramByteBuffer4, int paramInt8, ByteBuffer paramByteBuffer5, int paramInt9, ByteBuffer paramByteBuffer6, int paramInt10, int paramInt11, int paramInt12);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/JavaI420Buffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */