/*    */ package org.webrtc;
/*    */ 
/*    */ import android.content.Context;
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
/*    */ public class Camera1Capturer
/*    */   extends CameraCapturer
/*    */ {
/*    */   private final boolean captureToTexture;
/*    */   
/*    */   public Camera1Capturer(String cameraName, CameraVideoCapturer.CameraEventsHandler eventsHandler, boolean captureToTexture) {
/* 20 */     super(cameraName, eventsHandler, new Camera1Enumerator(captureToTexture));
/*    */     
/* 22 */     this.captureToTexture = captureToTexture;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void createCameraSession(CameraSession.CreateSessionCallback createSessionCallback, CameraSession.Events events, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, String cameraName, int width, int height, int framerate) {
/* 30 */     Camera1Session.create(createSessionCallback, events, this.captureToTexture, applicationContext, surfaceTextureHelper, 
/* 31 */         Camera1Enumerator.getCameraIndex(cameraName), width, height, framerate);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/Camera1Capturer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */