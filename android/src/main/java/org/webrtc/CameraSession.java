/*    */ package org.webrtc;
/*    */ import android.content.Context;
import android.graphics.Matrix;
import android.view.WindowManager;

/*    */ interface CameraSession { void stop();
/*    */   
/*    */   public static interface Events { void onCameraOpening();
/*    */     
/*    */     void onCameraError(CameraSession param1CameraSession, String param1String);
/*    */     
/*    */     void onCameraDisconnected(CameraSession param1CameraSession);
/*    */     
/*    */     void onCameraClosed(CameraSession param1CameraSession);
/*    */     
/*    */     void onFrameCaptured(CameraSession param1CameraSession, VideoFrame param1VideoFrame); }
/*    */   
/*    */   public static interface CreateSessionCallback { void onDone(CameraSession param1CameraSession);
/*    */     
/*    */     void onFailure(CameraSession.FailureType param1FailureType, String param1String); }
/*    */   
/* 19 */   public enum FailureType { ERROR, DISCONNECTED; }
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static int getDeviceOrientation(Context context) {
/* 43 */     WindowManager wm = (WindowManager)context.getSystemService("window");
/* 44 */     switch (wm.getDefaultDisplay().getRotation()) {
/*    */       case 1:
/* 46 */         return 90;
/*    */       case 2:
/* 48 */         return 180;
/*    */       case 3:
/* 50 */         return 270;
/*    */     } 
/*    */     
/* 53 */     return 0;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   static VideoFrame.TextureBuffer createTextureBufferWithModifiedTransformMatrix(TextureBufferImpl buffer, boolean mirror, int rotation) {
/* 59 */     Matrix transformMatrix = new Matrix();
/*    */     
/* 61 */     transformMatrix.preTranslate(0.5F, 0.5F);
/* 62 */     if (mirror) {
/* 63 */       transformMatrix.preScale(-1.0F, 1.0F);
/*    */     }
/* 65 */     transformMatrix.preRotate(rotation);
/* 66 */     transformMatrix.preTranslate(-0.5F, -0.5F);
/*    */ 
/*    */ 
/*    */     
/* 70 */     return buffer.applyTransformMatrix(transformMatrix, buffer.getWidth(), buffer.getHeight());
/*    */   } }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/CameraSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */