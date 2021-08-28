/*     */ package org.webrtc;
/*     */ 
/*     */ import android.view.SurfaceHolder;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CountDownLatch;
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
/*     */ public class SurfaceEglRenderer
/*     */   extends EglRenderer
/*     */   implements SurfaceHolder.Callback
/*     */ {
/*     */   private static final String TAG = "SurfaceEglRenderer";
/*     */   private RendererCommon.RendererEvents rendererEvents;
/*  30 */   private final Object layoutLock = new Object();
/*     */   
/*     */   private boolean isRenderingPaused;
/*     */   
/*     */   private boolean isFirstFrameRendered;
/*     */   
/*     */   private int rotatedFrameWidth;
/*     */   private int rotatedFrameHeight;
/*     */   private int frameRotation;
/*     */   
/*     */   public SurfaceEglRenderer(String name) {
/*  41 */     super(name);
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
/*     */   public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents, int[] configAttributes, RendererCommon.GlDrawer drawer) {
/*  53 */     ThreadUtils.checkIsOnMainThread();
/*  54 */     this.rendererEvents = rendererEvents;
/*  55 */     synchronized (this.layoutLock) {
/*  56 */       this.isFirstFrameRendered = false;
/*  57 */       this.rotatedFrameWidth = 0;
/*  58 */       this.rotatedFrameHeight = 0;
/*  59 */       this.frameRotation = 0;
/*     */     } 
/*  61 */     super.init(sharedContext, configAttributes, drawer);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(EglBase.Context sharedContext, int[] configAttributes, RendererCommon.GlDrawer drawer) {
/*  67 */     init(sharedContext, (RendererCommon.RendererEvents)null, configAttributes, drawer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFpsReduction(float fps) {
/*  78 */     synchronized (this.layoutLock) {
/*  79 */       this.isRenderingPaused = (fps == 0.0F);
/*     */     } 
/*  81 */     super.setFpsReduction(fps);
/*     */   }
/*     */ 
/*     */   
/*     */   public void disableFpsReduction() {
/*  86 */     synchronized (this.layoutLock) {
/*  87 */       this.isRenderingPaused = false;
/*     */     } 
/*  89 */     super.disableFpsReduction();
/*     */   }
/*     */ 
/*     */   
/*     */   public void pauseVideo() {
/*  94 */     synchronized (this.layoutLock) {
/*  95 */       this.isRenderingPaused = true;
/*     */     } 
/*  97 */     super.pauseVideo();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onFrame(VideoFrame frame) {
/* 103 */     updateFrameDimensionsAndReportEvents(frame);
/* 104 */     super.onFrame(frame);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void surfaceCreated(SurfaceHolder holder) {
/* 110 */     ThreadUtils.checkIsOnMainThread();
/* 111 */     createEglSurface(holder.getSurface());
/*     */   }
/*     */ 
/*     */   
/*     */   public void surfaceDestroyed(SurfaceHolder holder) {
/* 116 */     ThreadUtils.checkIsOnMainThread();
/* 117 */     CountDownLatch completionLatch = new CountDownLatch(1);
/* 118 */     Objects.requireNonNull(completionLatch); releaseEglSurface(completionLatch::countDown);
/* 119 */     ThreadUtils.awaitUninterruptedly(completionLatch);
/*     */   }
/*     */ 
/*     */   
/*     */   public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
/* 124 */     ThreadUtils.checkIsOnMainThread();
/* 125 */     logD("surfaceChanged: format: " + format + " size: " + width + "x" + height);
/*     */   }
/*     */ 
/*     */   
/*     */   private void updateFrameDimensionsAndReportEvents(VideoFrame frame) {
/* 130 */     synchronized (this.layoutLock) {
/* 131 */       if (this.isRenderingPaused) {
/*     */         return;
/*     */       }
/* 134 */       if (!this.isFirstFrameRendered) {
/* 135 */         this.isFirstFrameRendered = true;
/* 136 */         logD("Reporting first rendered frame.");
/* 137 */         if (this.rendererEvents != null) {
/* 138 */           this.rendererEvents.onFirstFrameRendered();
/*     */         }
/*     */       } 
/* 141 */       if (this.rotatedFrameWidth != frame.getRotatedWidth() || this.rotatedFrameHeight != frame
/* 142 */         .getRotatedHeight() || this.frameRotation != frame
/* 143 */         .getRotation()) {
/* 144 */         logD("Reporting frame resolution changed to " + frame.getBuffer().getWidth() + "x" + frame
/* 145 */             .getBuffer().getHeight() + " with rotation " + frame.getRotation());
/* 146 */         if (this.rendererEvents != null) {
/* 147 */           this.rendererEvents.onFrameResolutionChanged(frame
/* 148 */               .getBuffer().getWidth(), frame.getBuffer().getHeight(), frame.getRotation());
/*     */         }
/* 150 */         this.rotatedFrameWidth = frame.getRotatedWidth();
/* 151 */         this.rotatedFrameHeight = frame.getRotatedHeight();
/* 152 */         this.frameRotation = frame.getRotation();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void logD(String string) {
/* 158 */     Logging.d("SurfaceEglRenderer", this.name + ": " + string);
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/SurfaceEglRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */