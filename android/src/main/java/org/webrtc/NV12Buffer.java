/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NV12Buffer
/*    */   implements VideoFrame.Buffer
/*    */ {
/*    */   private final int width;
/*    */   private final int height;
/*    */   private final int stride;
/*    */   private final int sliceHeight;
/*    */   private final ByteBuffer buffer;
/*    */   private final RefCountDelegate refCountDelegate;
/*    */   
/*    */   public NV12Buffer(int width, int height, int stride, int sliceHeight, ByteBuffer buffer, @Nullable Runnable releaseCallback) {
/* 26 */     this.width = width;
/* 27 */     this.height = height;
/* 28 */     this.stride = stride;
/* 29 */     this.sliceHeight = sliceHeight;
/* 30 */     this.buffer = buffer;
/* 31 */     this.refCountDelegate = new RefCountDelegate(releaseCallback);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getWidth() {
/* 36 */     return this.width;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getHeight() {
/* 41 */     return this.height;
/*    */   }
/*    */ 
/*    */   
/*    */   public VideoFrame.I420Buffer toI420() {
/* 46 */     return (VideoFrame.I420Buffer)cropAndScale(0, 0, this.width, this.height, this.width, this.height);
/*    */   }
/*    */ 
/*    */   
/*    */   public void retain() {
/* 51 */     this.refCountDelegate.retain();
/*    */   }
/*    */ 
/*    */   
/*    */   public void release() {
/* 56 */     this.refCountDelegate.release();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public VideoFrame.Buffer cropAndScale(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
/* 62 */     JavaI420Buffer newBuffer = JavaI420Buffer.allocate(scaleWidth, scaleHeight);
/* 63 */     nativeCropAndScale(cropX, cropY, cropWidth, cropHeight, scaleWidth, scaleHeight, this.buffer, this.width, this.height, this.stride, this.sliceHeight, newBuffer
/* 64 */         .getDataY(), newBuffer.getStrideY(), newBuffer
/* 65 */         .getDataU(), newBuffer.getStrideU(), newBuffer.getDataV(), newBuffer.getStrideV());
/* 66 */     return newBuffer;
/*    */   }
/*    */   
/*    */   private static native void nativeCropAndScale(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, ByteBuffer paramByteBuffer1, int paramInt7, int paramInt8, int paramInt9, int paramInt10, ByteBuffer paramByteBuffer2, int paramInt11, ByteBuffer paramByteBuffer3, int paramInt12, ByteBuffer paramByteBuffer4, int paramInt13);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/NV12Buffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */