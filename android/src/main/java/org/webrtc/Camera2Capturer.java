/*    */ package org.webrtc;
/*    */ 
/*    */ import android.annotation.TargetApi;
/*    */ import android.content.Context;
/*    */ import android.hardware.camera2.CameraManager;
/*    */ import androidx.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @TargetApi(21)
/*    */ public class Camera2Capturer
/*    */   extends CameraCapturer
/*    */ {
/*    */   private final Context context;
/*    */   @Nullable
/*    */   private final CameraManager cameraManager;
/*    */   
/*    */   public Camera2Capturer(Context context, String cameraName, CameraVideoCapturer.CameraEventsHandler eventsHandler) {
/* 24 */     super(cameraName, eventsHandler, new Camera2Enumerator(context));
/*    */     
/* 26 */     this.context = context;
/* 27 */     this.cameraManager = (CameraManager)context.getSystemService("camera");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void createCameraSession(CameraSession.CreateSessionCallback createSessionCallback, CameraSession.Events events, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, String cameraName, int width, int height, int framerate) {
/* 35 */     Camera2Session.create(createSessionCallback, events, applicationContext, this.cameraManager, surfaceTextureHelper, cameraName, width, height, framerate);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/Camera2Capturer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */