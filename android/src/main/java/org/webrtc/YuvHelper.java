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
/*     */ public class YuvHelper
/*     */ {
/*     */   public static void I420Copy(ByteBuffer srcY, int srcStrideY, ByteBuffer srcU, int srcStrideU, ByteBuffer srcV, int srcStrideV, ByteBuffer dst, int width, int height) {
/*  20 */     int chromaHeight = (height + 1) / 2;
/*  21 */     int chromaWidth = (width + 1) / 2;
/*     */     
/*  23 */     int minSize = width * height + chromaWidth * chromaHeight * 2;
/*  24 */     if (dst.capacity() < minSize) {
/*  25 */       throw new IllegalArgumentException("Expected destination buffer capacity to be at least " + minSize + " was " + dst
/*  26 */           .capacity());
/*     */     }
/*     */     
/*  29 */     int startY = 0;
/*  30 */     int startU = height * width;
/*  31 */     int startV = startU + chromaHeight * chromaWidth;
/*     */     
/*  33 */     dst.position(0);
/*  34 */     ByteBuffer dstY = dst.slice();
/*  35 */     dst.position(startU);
/*  36 */     ByteBuffer dstU = dst.slice();
/*  37 */     dst.position(startV);
/*  38 */     ByteBuffer dstV = dst.slice();
/*     */     
/*  40 */     nativeI420Copy(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dstY, width, dstU, chromaWidth, dstV, chromaWidth, width, height);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void I420ToNV12(ByteBuffer srcY, int srcStrideY, ByteBuffer srcU, int srcStrideU, ByteBuffer srcV, int srcStrideV, ByteBuffer dst, int width, int height) {
/*  47 */     int chromaWidth = (width + 1) / 2;
/*  48 */     int chromaHeight = (height + 1) / 2;
/*     */     
/*  50 */     int minSize = width * height + chromaWidth * chromaHeight * 2;
/*  51 */     if (dst.capacity() < minSize) {
/*  52 */       throw new IllegalArgumentException("Expected destination buffer capacity to be at least " + minSize + " was " + dst
/*  53 */           .capacity());
/*     */     }
/*     */     
/*  56 */     int startY = 0;
/*  57 */     int startUV = height * width;
/*     */     
/*  59 */     dst.position(0);
/*  60 */     ByteBuffer dstY = dst.slice();
/*  61 */     dst.position(startUV);
/*  62 */     ByteBuffer dstUV = dst.slice();
/*     */     
/*  64 */     nativeI420ToNV12(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dstY, width, dstUV, chromaWidth * 2, width, height);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void I420Rotate(ByteBuffer srcY, int srcStrideY, ByteBuffer srcU, int srcStrideU, ByteBuffer srcV, int srcStrideV, ByteBuffer dst, int srcWidth, int srcHeight, int rotationMode) {
/*  72 */     int dstWidth = (rotationMode % 180 == 0) ? srcWidth : srcHeight;
/*  73 */     int dstHeight = (rotationMode % 180 == 0) ? srcHeight : srcWidth;
/*     */     
/*  75 */     int dstChromaHeight = (dstHeight + 1) / 2;
/*  76 */     int dstChromaWidth = (dstWidth + 1) / 2;
/*     */     
/*  78 */     int minSize = dstWidth * dstHeight + dstChromaWidth * dstChromaHeight * 2;
/*  79 */     if (dst.capacity() < minSize) {
/*  80 */       throw new IllegalArgumentException("Expected destination buffer capacity to be at least " + minSize + " was " + dst
/*  81 */           .capacity());
/*     */     }
/*     */     
/*  84 */     int startY = 0;
/*  85 */     int startU = dstHeight * dstWidth;
/*  86 */     int startV = startU + dstChromaHeight * dstChromaWidth;
/*     */     
/*  88 */     dst.position(0);
/*  89 */     ByteBuffer dstY = dst.slice();
/*  90 */     dst.position(startU);
/*  91 */     ByteBuffer dstU = dst.slice();
/*  92 */     dst.position(startV);
/*  93 */     ByteBuffer dstV = dst.slice();
/*     */     
/*  95 */     nativeI420Rotate(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dstY, dstWidth, dstU, dstChromaWidth, dstV, dstChromaWidth, srcWidth, srcHeight, rotationMode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copyPlane(ByteBuffer src, int srcStride, ByteBuffer dst, int dstStride, int width, int height) {
/* 102 */     nativeCopyPlane(src, srcStride, dst, dstStride, width, height);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void ABGRToI420(ByteBuffer src, int srcStride, ByteBuffer dstY, int dstStrideY, ByteBuffer dstU, int dstStrideU, ByteBuffer dstV, int dstStrideV, int width, int height) {
/* 108 */     nativeABGRToI420(src, srcStride, dstY, dstStrideY, dstU, dstStrideU, dstV, dstStrideV, width, height);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void I420Copy(ByteBuffer srcY, int srcStrideY, ByteBuffer srcU, int srcStrideU, ByteBuffer srcV, int srcStrideV, ByteBuffer dstY, int dstStrideY, ByteBuffer dstU, int dstStrideU, ByteBuffer dstV, int dstStrideV, int width, int height) {
/* 115 */     nativeI420Copy(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dstY, dstStrideY, dstU, dstStrideU, dstV, dstStrideV, width, height);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void I420ToNV12(ByteBuffer srcY, int srcStrideY, ByteBuffer srcU, int srcStrideU, ByteBuffer srcV, int srcStrideV, ByteBuffer dstY, int dstStrideY, ByteBuffer dstUV, int dstStrideUV, int width, int height) {
/* 122 */     nativeI420ToNV12(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dstY, dstStrideY, dstUV, dstStrideUV, width, height);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void I420Rotate(ByteBuffer srcY, int srcStrideY, ByteBuffer srcU, int srcStrideU, ByteBuffer srcV, int srcStrideV, ByteBuffer dstY, int dstStrideY, ByteBuffer dstU, int dstStrideU, ByteBuffer dstV, int dstStrideV, int srcWidth, int srcHeight, int rotationMode) {
/* 130 */     nativeI420Rotate(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dstY, dstStrideY, dstU, dstStrideU, dstV, dstStrideV, srcWidth, srcHeight, rotationMode);
/*     */   }
/*     */   
/*     */   private static native void nativeCopyPlane(ByteBuffer paramByteBuffer1, int paramInt1, ByteBuffer paramByteBuffer2, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */   private static native void nativeI420Copy(ByteBuffer paramByteBuffer1, int paramInt1, ByteBuffer paramByteBuffer2, int paramInt2, ByteBuffer paramByteBuffer3, int paramInt3, ByteBuffer paramByteBuffer4, int paramInt4, ByteBuffer paramByteBuffer5, int paramInt5, ByteBuffer paramByteBuffer6, int paramInt6, int paramInt7, int paramInt8);
/*     */   
/*     */   private static native void nativeI420ToNV12(ByteBuffer paramByteBuffer1, int paramInt1, ByteBuffer paramByteBuffer2, int paramInt2, ByteBuffer paramByteBuffer3, int paramInt3, ByteBuffer paramByteBuffer4, int paramInt4, ByteBuffer paramByteBuffer5, int paramInt5, int paramInt6, int paramInt7);
/*     */   
/*     */   private static native void nativeI420Rotate(ByteBuffer paramByteBuffer1, int paramInt1, ByteBuffer paramByteBuffer2, int paramInt2, ByteBuffer paramByteBuffer3, int paramInt3, ByteBuffer paramByteBuffer4, int paramInt4, ByteBuffer paramByteBuffer5, int paramInt5, ByteBuffer paramByteBuffer6, int paramInt6, int paramInt7, int paramInt8, int paramInt9);
/*     */   
/*     */   private static native void nativeABGRToI420(ByteBuffer paramByteBuffer1, int paramInt1, ByteBuffer paramByteBuffer2, int paramInt2, ByteBuffer paramByteBuffer3, int paramInt3, ByteBuffer paramByteBuffer4, int paramInt4, int paramInt5, int paramInt6);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/YuvHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */