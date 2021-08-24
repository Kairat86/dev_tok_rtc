/*    */ package org.webrtc;
/*    */ 
/*    */ import android.opengl.GLES20;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.ByteOrder;
/*    */ import java.nio.FloatBuffer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GlUtil
/*    */ {
/*    */   public static class GlOutOfMemoryException
/*    */     extends RuntimeException
/*    */   {
/*    */     public GlOutOfMemoryException(String msg) {
/* 27 */       super(msg);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public static void checkNoGLES2Error(String msg) {
/* 33 */     int error = GLES20.glGetError();
/* 34 */     if (error != 0) {
/* 35 */       throw (error == 1285) ? 
/* 36 */         new GlOutOfMemoryException(msg) : 
/* 37 */         new RuntimeException(msg + ": GLES20 error: " + error);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public static FloatBuffer createFloatBuffer(float[] coords) {
/* 43 */     ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
/* 44 */     bb.order(ByteOrder.nativeOrder());
/* 45 */     FloatBuffer fb = bb.asFloatBuffer();
/* 46 */     fb.put(coords);
/* 47 */     fb.position(0);
/* 48 */     return fb;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int generateTexture(int target) {
/* 55 */     int[] textureArray = new int[1];
/* 56 */     GLES20.glGenTextures(1, textureArray, 0);
/* 57 */     int textureId = textureArray[0];
/* 58 */     GLES20.glBindTexture(target, textureId);
/* 59 */     GLES20.glTexParameterf(target, 10241, 9729.0F);
/* 60 */     GLES20.glTexParameterf(target, 10240, 9729.0F);
/* 61 */     GLES20.glTexParameterf(target, 10242, 33071.0F);
/* 62 */     GLES20.glTexParameterf(target, 10243, 33071.0F);
/* 63 */     checkNoGLES2Error("generateTexture");
/* 64 */     return textureId;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/GlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */