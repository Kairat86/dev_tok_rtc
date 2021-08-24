/*     */ package org.webrtc;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.hardware.display.VirtualDisplay;
/*     */ import android.media.projection.MediaProjection;
/*     */ import android.media.projection.MediaProjectionManager;
/*     */ import android.view.Surface;
/*     */ import androidx.annotation.Nullable;
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
/*     */ @TargetApi(21)
/*     */ public class ScreenCapturerAndroid
/*     */   implements VideoCapturer, VideoSink
/*     */ {
/*     */   private static final int DISPLAY_FLAGS = 3;
/*     */   private static final int VIRTUAL_DISPLAY_DPI = 400;
/*     */   private final Intent mediaProjectionPermissionResultData;
/*     */   private final MediaProjection.Callback mediaProjectionCallback;
/*     */   private int width;
/*     */   private int height;
/*     */   @Nullable
/*     */   private VirtualDisplay virtualDisplay;
/*     */   @Nullable
/*     */   private SurfaceTextureHelper surfaceTextureHelper;
/*     */   @Nullable
/*     */   private CapturerObserver capturerObserver;
/*     */   private long numCapturedFrames;
/*     */   @Nullable
/*     */   private MediaProjection mediaProjection;
/*     */   private boolean isDisposed;
/*     */   @Nullable
/*     */   private MediaProjectionManager mediaProjectionManager;
/*     */   
/*     */   public ScreenCapturerAndroid(Intent mediaProjectionPermissionResultData, MediaProjection.Callback mediaProjectionCallback) {
/*  68 */     this.mediaProjectionPermissionResultData = mediaProjectionPermissionResultData;
/*  69 */     this.mediaProjectionCallback = mediaProjectionCallback;
/*     */   }
/*     */   
/*     */   private void checkNotDisposed() {
/*  73 */     if (this.isDisposed) {
/*  74 */       throw new RuntimeException("capturer is disposed.");
/*     */     }
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public MediaProjection getMediaProjection() {
/*  80 */     return this.mediaProjection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
/*  88 */     checkNotDisposed();
/*     */     
/*  90 */     if (capturerObserver == null) {
/*  91 */       throw new RuntimeException("capturerObserver not set.");
/*     */     }
/*  93 */     this.capturerObserver = capturerObserver;
/*     */     
/*  95 */     if (surfaceTextureHelper == null) {
/*  96 */       throw new RuntimeException("surfaceTextureHelper not set.");
/*     */     }
/*  98 */     this.surfaceTextureHelper = surfaceTextureHelper;
/*     */     
/* 100 */     this.mediaProjectionManager = (MediaProjectionManager)applicationContext.getSystemService("media_projection");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void startCapture(int width, int height, int ignoredFramerate) {
/* 109 */     checkNotDisposed();
/*     */     
/* 111 */     this.width = width;
/* 112 */     this.height = height;
/*     */     
/* 114 */     this.mediaProjection = this.mediaProjectionManager.getMediaProjection(-1, this.mediaProjectionPermissionResultData);
/*     */ 
/*     */ 
/*     */     
/* 118 */     this.mediaProjection.registerCallback(this.mediaProjectionCallback, this.surfaceTextureHelper.getHandler());
/*     */     
/* 120 */     createVirtualDisplay();
/* 121 */     this.capturerObserver.onCapturerStarted(true);
/* 122 */     this.surfaceTextureHelper.startListening(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void stopCapture() {
/* 129 */     checkNotDisposed();
/* 130 */     ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), new Runnable()
/*     */         {
/*     */           public void run() {
/* 133 */             ScreenCapturerAndroid.this.surfaceTextureHelper.stopListening();
/* 134 */             ScreenCapturerAndroid.this.capturerObserver.onCapturerStopped();
/*     */             
/* 136 */             if (ScreenCapturerAndroid.this.virtualDisplay != null) {
/* 137 */               ScreenCapturerAndroid.this.virtualDisplay.release();
/* 138 */               ScreenCapturerAndroid.this.virtualDisplay = null;
/*     */             } 
/*     */             
/* 141 */             if (ScreenCapturerAndroid.this.mediaProjection != null) {
/*     */ 
/*     */               
/* 144 */               ScreenCapturerAndroid.this.mediaProjection.unregisterCallback(ScreenCapturerAndroid.this.mediaProjectionCallback);
/* 145 */               ScreenCapturerAndroid.this.mediaProjection.stop();
/* 146 */               ScreenCapturerAndroid.this.mediaProjection = null;
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void dispose() {
/* 156 */     this.isDisposed = true;
/*     */   }
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
/*     */   public synchronized void changeCaptureFormat(int width, int height, int ignoredFramerate) {
/* 172 */     checkNotDisposed();
/*     */     
/* 174 */     this.width = width;
/* 175 */     this.height = height;
/*     */     
/* 177 */     if (this.virtualDisplay == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 185 */     ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), new Runnable()
/*     */         {
/*     */           public void run() {
/* 188 */             ScreenCapturerAndroid.this.virtualDisplay.release();
/* 189 */             ScreenCapturerAndroid.this.createVirtualDisplay();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private void createVirtualDisplay() {
/* 195 */     this.surfaceTextureHelper.setTextureSize(this.width, this.height);
/* 196 */     this.virtualDisplay = this.mediaProjection.createVirtualDisplay("WebRTC_ScreenCapture", this.width, this.height, 400, 3, new Surface(this.surfaceTextureHelper
/* 197 */           .getSurfaceTexture()), null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onFrame(VideoFrame frame) {
/* 204 */     this.numCapturedFrames++;
/* 205 */     this.capturerObserver.onFrameCaptured(frame);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isScreencast() {
/* 210 */     return true;
/*     */   }
/*     */   
/*     */   public long getNumCapturedFrames() {
/* 214 */     return this.numCapturedFrames;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/ScreenCapturerAndroid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */