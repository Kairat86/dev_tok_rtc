/*     */ package org.webrtc;
/*     */ 
/*     */ import android.content.Context;
/*     */
/*     */ import android.graphics.Point;
/*     */ import android.os.Looper;
/*     */ import android.util.AttributeSet;
/*     */ import android.view.SurfaceHolder;
/*     */ import android.view.SurfaceView;
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
/*     */ public class SurfaceViewRenderer
/*     */   extends SurfaceView
/*     */   implements SurfaceHolder.Callback, VideoSink, RendererCommon.RendererEvents
/*     */ {
/*     */   private static final String TAG = "SurfaceViewRenderer";
/*     */   private final String resourceName;
/*  30 */   private final RendererCommon.VideoLayoutMeasure videoLayoutMeasure = new RendererCommon.VideoLayoutMeasure();
/*     */   
/*     */   private final SurfaceEglRenderer eglRenderer;
/*     */   
/*     */   private RendererCommon.RendererEvents rendererEvents;
/*     */   
/*     */   private int rotatedFrameWidth;
/*     */   
/*     */   private int rotatedFrameHeight;
/*     */   
/*     */   private boolean enableFixedSize;
/*     */   
/*     */   private int surfaceWidth;
/*     */   
/*     */   private int surfaceHeight;
/*     */ 
/*     */   
/*     */   public SurfaceViewRenderer(Context context) {
/*  48 */     super(context);
/*  49 */     this.resourceName = getResourceName();
/*  50 */     this.eglRenderer = new SurfaceEglRenderer(this.resourceName);
/*  51 */     getHolder().addCallback(this);
/*  52 */     getHolder().addCallback(this.eglRenderer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SurfaceViewRenderer(Context context, AttributeSet attrs) {
/*  59 */     super(context, attrs);
/*  60 */     this.resourceName = getResourceName();
/*  61 */     this.eglRenderer = new SurfaceEglRenderer(this.resourceName);
/*  62 */     getHolder().addCallback(this);
/*  63 */     getHolder().addCallback(this.eglRenderer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents) {
/*  71 */     init(sharedContext, rendererEvents, EglBase.CONFIG_PLAIN, new GlRectDrawer());
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
/*  83 */     ThreadUtils.checkIsOnMainThread();
/*  84 */     this.rendererEvents = rendererEvents;
/*  85 */     this.rotatedFrameWidth = 0;
/*  86 */     this.rotatedFrameHeight = 0;
/*  87 */     this.eglRenderer.init(sharedContext, this, configAttributes, drawer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/*  97 */     this.eglRenderer.release();
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
/*     */   public void addFrameListener(EglRenderer.FrameListener listener, float scale, RendererCommon.GlDrawer drawerParam) {
/* 111 */     this.eglRenderer.addFrameListener(listener, scale, drawerParam);
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
/*     */   public void addFrameListener(EglRenderer.FrameListener listener, float scale) {
/* 124 */     this.eglRenderer.addFrameListener(listener, scale);
/*     */   }
/*     */   
/*     */   public void removeFrameListener(EglRenderer.FrameListener listener) {
/* 128 */     this.eglRenderer.removeFrameListener(listener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnableHardwareScaler(boolean enabled) {
/* 136 */     ThreadUtils.checkIsOnMainThread();
/* 137 */     this.enableFixedSize = enabled;
/* 138 */     updateSurfaceSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMirror(boolean mirror) {
/* 145 */     this.eglRenderer.setMirror(mirror);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setScalingType(RendererCommon.ScalingType scalingType) {
/* 152 */     ThreadUtils.checkIsOnMainThread();
/* 153 */     this.videoLayoutMeasure.setScalingType(scalingType);
/* 154 */     requestLayout();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setScalingType(RendererCommon.ScalingType scalingTypeMatchOrientation, RendererCommon.ScalingType scalingTypeMismatchOrientation) {
/* 159 */     ThreadUtils.checkIsOnMainThread();
/* 160 */     this.videoLayoutMeasure.setScalingType(scalingTypeMatchOrientation, scalingTypeMismatchOrientation);
/* 161 */     requestLayout();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFpsReduction(float fps) {
/* 171 */     this.eglRenderer.setFpsReduction(fps);
/*     */   }
/*     */   
/*     */   public void disableFpsReduction() {
/* 175 */     this.eglRenderer.disableFpsReduction();
/*     */   }
/*     */   
/*     */   public void pauseVideo() {
/* 179 */     this.eglRenderer.pauseVideo();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onFrame(VideoFrame frame) {
/* 185 */     this.eglRenderer.onFrame(frame);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void onMeasure(int widthSpec, int heightSpec) {
/* 191 */     ThreadUtils.checkIsOnMainThread();
/*     */     
/* 193 */     Point size = this.videoLayoutMeasure.measure(widthSpec, heightSpec, this.rotatedFrameWidth, this.rotatedFrameHeight);
/* 194 */     setMeasuredDimension(size.x, size.y);
/* 195 */     logD("onMeasure(). New size: " + size.x + "x" + size.y);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
/* 200 */     ThreadUtils.checkIsOnMainThread();
/* 201 */     this.eglRenderer.setLayoutAspectRatio((right - left) / (bottom - top));
/* 202 */     updateSurfaceSize();
/*     */   }
/*     */   
/*     */   private void updateSurfaceSize() {
/* 206 */     ThreadUtils.checkIsOnMainThread();
/* 207 */     if (this.enableFixedSize && this.rotatedFrameWidth != 0 && this.rotatedFrameHeight != 0 && getWidth() != 0 && 
/* 208 */       getHeight() != 0) {
/* 209 */       int drawnFrameWidth, drawnFrameHeight; float layoutAspectRatio = getWidth() / getHeight();
/* 210 */       float frameAspectRatio = this.rotatedFrameWidth / this.rotatedFrameHeight;
/*     */ 
/*     */       
/* 213 */       if (frameAspectRatio > layoutAspectRatio) {
/* 214 */         drawnFrameWidth = (int)(this.rotatedFrameHeight * layoutAspectRatio);
/* 215 */         drawnFrameHeight = this.rotatedFrameHeight;
/*     */       } else {
/* 217 */         drawnFrameWidth = this.rotatedFrameWidth;
/* 218 */         drawnFrameHeight = (int)(this.rotatedFrameWidth / layoutAspectRatio);
/*     */       } 
/*     */       
/* 221 */       int width = Math.min(getWidth(), drawnFrameWidth);
/* 222 */       int height = Math.min(getHeight(), drawnFrameHeight);
/* 223 */       logD("updateSurfaceSize. Layout size: " + getWidth() + "x" + getHeight() + ", frame size: " + this.rotatedFrameWidth + "x" + this.rotatedFrameHeight + ", requested surface size: " + width + "x" + height + ", old surface size: " + this.surfaceWidth + "x" + this.surfaceHeight);
/*     */ 
/*     */       
/* 226 */       if (width != this.surfaceWidth || height != this.surfaceHeight) {
/* 227 */         this.surfaceWidth = width;
/* 228 */         this.surfaceHeight = height;
/* 229 */         getHolder().setFixedSize(width, height);
/*     */       } 
/*     */     } else {
/* 232 */       this.surfaceWidth = this.surfaceHeight = 0;
/* 233 */       getHolder().setSizeFromLayout();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void surfaceCreated(SurfaceHolder holder) {
/* 240 */     ThreadUtils.checkIsOnMainThread();
/* 241 */     this.surfaceWidth = this.surfaceHeight = 0;
/* 242 */     updateSurfaceSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public void surfaceDestroyed(SurfaceHolder holder) {}
/*     */ 
/*     */   
/*     */   public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
/*     */   
/*     */   private String getResourceName() {
/*     */     try {
/* 253 */       return getResources().getResourceEntryName(getId());
/* 254 */     } catch (android.content.res.Resources.NotFoundException e) {
/* 255 */       return "";
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearImage() {
/* 263 */     this.eglRenderer.clearImage();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onFirstFrameRendered() {
/* 268 */     if (this.rendererEvents != null) {
/* 269 */       this.rendererEvents.onFirstFrameRendered();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
/* 275 */     if (this.rendererEvents != null) {
/* 276 */       this.rendererEvents.onFrameResolutionChanged(videoWidth, videoHeight, rotation);
/*     */     }
/* 278 */     int rotatedWidth = (rotation == 0 || rotation == 180) ? videoWidth : videoHeight;
/* 279 */     int rotatedHeight = (rotation == 0 || rotation == 180) ? videoHeight : videoWidth;
/*     */     
/* 281 */     postOrRun(() -> {
/*     */           this.rotatedFrameWidth = rotatedWidth;
/*     */           this.rotatedFrameHeight = rotatedHeight;
/*     */           updateSurfaceSize();
/*     */           requestLayout();
/*     */         });
/*     */   }
/*     */   
/*     */   private void postOrRun(Runnable r) {
/* 290 */     if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
/* 291 */       r.run();
/*     */     } else {
/* 293 */       post(r);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void logD(String string) {
/* 298 */     Logging.d("SurfaceViewRenderer", this.resourceName + ": " + string);
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/SurfaceViewRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */