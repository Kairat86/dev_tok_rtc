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
/*    */ public class NV21Buffer
/*    */   implements VideoFrame.Buffer
/*    */ {
/*    */   private final byte[] data;
/*    */   private final int width;
/*    */   private final int height;
/*    */   private final RefCountDelegate refCountDelegate;
/*    */   
/*    */   public NV21Buffer(byte[] data, int width, int height, @Nullable Runnable releaseCallback) {
/* 23 */     this.data = data;
/* 24 */     this.width = width;
/* 25 */     this.height = height;
/* 26 */     this.refCountDelegate = new RefCountDelegate(releaseCallback);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getWidth() {
/* 31 */     return this.width;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getHeight() {
/* 36 */     return this.height;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public VideoFrame.I420Buffer toI420() {
/* 42 */     return (VideoFrame.I420Buffer)cropAndScale(0, 0, this.width, this.height, this.width, this.height);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void retain() {
/* 48 */     this.refCountDelegate.retain();
/*    */   }
/*    */ 
/*    */   
/*    */   public void release() {
/* 53 */     this.refCountDelegate.release();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public VideoFrame.Buffer cropAndScale(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
/* 59 */     JavaI420Buffer newBuffer = JavaI420Buffer.allocate(scaleWidth, scaleHeight);
/* 60 */     nativeCropAndScale(cropX, cropY, cropWidth, cropHeight, scaleWidth, scaleHeight, this.data, this.width, this.height, newBuffer
/* 61 */         .getDataY(), newBuffer.getStrideY(), newBuffer.getDataU(), newBuffer
/* 62 */         .getStrideU(), newBuffer.getDataV(), newBuffer.getStrideV());
/* 63 */     return newBuffer;
/*    */   }
/*    */   
/*    */   private static native void nativeCropAndScale(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, byte[] paramArrayOfbyte, int paramInt7, int paramInt8, ByteBuffer paramByteBuffer1, int paramInt9, ByteBuffer paramByteBuffer2, int paramInt10, ByteBuffer paramByteBuffer3, int paramInt11);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/NV21Buffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */