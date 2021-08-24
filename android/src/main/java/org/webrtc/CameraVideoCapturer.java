/*     */ package org.webrtc;
/*     */ 
/*     */ import android.media.MediaRecorder;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface CameraVideoCapturer
/*     */   extends VideoCapturer
/*     */ {
/*     */   void switchCamera(CameraSwitchHandler paramCameraSwitchHandler);
/*     */   
/*     */   void switchCamera(CameraSwitchHandler paramCameraSwitchHandler, String paramString);
/*     */   
/*     */   @Deprecated
/*     */   default void addMediaRecorderToCamera(MediaRecorder mediaRecorder, MediaRecorderHandler resultHandler) {
/*  92 */     throw new UnsupportedOperationException("Deprecated and not implemented.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void removeMediaRecorderFromCamera(MediaRecorderHandler resultHandler) {
/* 101 */     throw new UnsupportedOperationException("Deprecated and not implemented.");
/*     */   }
/*     */ 
/*     */   
/*     */   public static class CameraStatistics
/*     */   {
/*     */     private static final String TAG = "CameraStatistics";
/*     */     
/*     */     private static final int CAMERA_OBSERVER_PERIOD_MS = 2000;
/*     */     
/*     */     private static final int CAMERA_FREEZE_REPORT_TIMOUT_MS = 4000;
/*     */     
/*     */     private final SurfaceTextureHelper surfaceTextureHelper;
/*     */     
/*     */     private final CameraVideoCapturer.CameraEventsHandler eventsHandler;
/*     */     private int frameCount;
/*     */     private int freezePeriodCount;
/*     */     
/* 119 */     private final Runnable cameraObserver = new Runnable()
/*     */       {
/*     */         public void run() {
/* 122 */           int cameraFps = Math.round(CameraVideoCapturer.CameraStatistics.this.frameCount * 1000.0F / 2000.0F);
/* 123 */           Logging.d("CameraStatistics", "Camera fps: " + cameraFps + ".");
/* 124 */           if (CameraVideoCapturer.CameraStatistics.this.frameCount == 0) {
/* 125 */             ++CameraVideoCapturer.CameraStatistics.this.freezePeriodCount;
/* 126 */             if (2000 * CameraVideoCapturer.CameraStatistics.this.freezePeriodCount >= 4000 && CameraVideoCapturer.CameraStatistics.this
/* 127 */               .eventsHandler != null) {
/* 128 */               Logging.e("CameraStatistics", "Camera freezed.");
/* 129 */               if (CameraVideoCapturer.CameraStatistics.this.surfaceTextureHelper.isTextureInUse()) {
/*     */                 
/* 131 */                 CameraVideoCapturer.CameraStatistics.this.eventsHandler.onCameraFreezed("Camera failure. Client must return video buffers.");
/*     */               } else {
/* 133 */                 CameraVideoCapturer.CameraStatistics.this.eventsHandler.onCameraFreezed("Camera failure.");
/*     */               } 
/*     */               return;
/*     */             } 
/*     */           } else {
/* 138 */             CameraVideoCapturer.CameraStatistics.this.freezePeriodCount = 0;
/*     */           } 
/* 140 */           CameraVideoCapturer.CameraStatistics.this.frameCount = 0;
/* 141 */           CameraVideoCapturer.CameraStatistics.this.surfaceTextureHelper.getHandler().postDelayed(this, 2000L);
/*     */         }
/*     */       };
/*     */ 
/*     */     
/*     */     public CameraStatistics(SurfaceTextureHelper surfaceTextureHelper, CameraVideoCapturer.CameraEventsHandler eventsHandler) {
/* 147 */       if (surfaceTextureHelper == null) {
/* 148 */         throw new IllegalArgumentException("SurfaceTextureHelper is null");
/*     */       }
/* 150 */       this.surfaceTextureHelper = surfaceTextureHelper;
/* 151 */       this.eventsHandler = eventsHandler;
/* 152 */       this.frameCount = 0;
/* 153 */       this.freezePeriodCount = 0;
/* 154 */       surfaceTextureHelper.getHandler().postDelayed(this.cameraObserver, 2000L);
/*     */     }
/*     */     
/*     */     private void checkThread() {
/* 158 */       if (Thread.currentThread() != this.surfaceTextureHelper.getHandler().getLooper().getThread()) {
/* 159 */         throw new IllegalStateException("Wrong thread");
/*     */       }
/*     */     }
/*     */     
/*     */     public void addFrame() {
/* 164 */       checkThread();
/* 165 */       this.frameCount++;
/*     */     }
/*     */     
/*     */     public void release() {
/* 169 */       this.surfaceTextureHelper.getHandler().removeCallbacks(this.cameraObserver);
/*     */     }
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static interface MediaRecorderHandler {
/*     */     void onMediaRecorderSuccess();
/*     */     
/*     */     void onMediaRecorderError(String param1String);
/*     */   }
/*     */   
/*     */   public static interface CameraSwitchHandler {
/*     */     void onCameraSwitchDone(boolean param1Boolean);
/*     */     
/*     */     void onCameraSwitchError(String param1String);
/*     */   }
/*     */   
/*     */   public static interface CameraEventsHandler {
/*     */     void onCameraError(String param1String);
/*     */     
/*     */     void onCameraDisconnected();
/*     */     
/*     */     void onCameraFreezed(String param1String);
/*     */     
/*     */     void onCameraOpening(String param1String);
/*     */     
/*     */     void onFirstFrameAvailable();
/*     */     
/*     */     void onCameraClosed();
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/CameraVideoCapturer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */