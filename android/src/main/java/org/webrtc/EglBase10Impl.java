/*     */ package org.webrtc;
/*     */ 
/*     */ import android.graphics.Canvas;
/*     */ import android.graphics.Rect;
/*     */ import android.graphics.SurfaceTexture;
/*     */ import android.view.Surface;
/*     */ import android.view.SurfaceHolder;
/*     */ import androidx.annotation.Nullable;
/*     */ import javax.microedition.khronos.egl.EGL10;
/*     */ import javax.microedition.khronos.egl.EGLConfig;
/*     */ import javax.microedition.khronos.egl.EGLContext;
/*     */ import javax.microedition.khronos.egl.EGLDisplay;
/*     */ import javax.microedition.khronos.egl.EGLSurface;
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
/*     */ class EglBase10Impl
/*     */   implements EglBase10
/*     */ {
/*     */   private static final String TAG = "EglBase10Impl";
/*     */   private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
/*     */   private final EGL10 egl;
/*     */   private EGLContext eglContext;
/*     */   @Nullable
/*     */   private EGLConfig eglConfig;
/*     */   private EGLDisplay eglDisplay;
/*  38 */   private EGLSurface eglSurface = EGL10.EGL_NO_SURFACE;
/*     */   
/*     */   private static class Context
/*     */     implements EglBase10.Context
/*     */   {
/*     */     private final EGLContext eglContext;
/*     */     
/*     */     public EGLContext getRawContext() {
/*  46 */       return this.eglContext;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getNativeEglContext() {
/*  55 */       return 0L;
/*     */     }
/*     */     
/*     */     public Context(EGLContext eglContext) {
/*  59 */       this.eglContext = eglContext;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public EglBase10Impl(EGLContext sharedContext, int[] configAttributes) {
/*  65 */     this.egl = (EGL10)EGLContext.getEGL();
/*  66 */     this.eglDisplay = getEglDisplay();
/*  67 */     this.eglConfig = getEglConfig(this.eglDisplay, configAttributes);
/*  68 */     int openGlesVersion = EglBase.getOpenGlesVersionFromConfig(configAttributes);
/*  69 */     Logging.d("EglBase10Impl", "Using OpenGL ES version " + openGlesVersion);
/*  70 */     this.eglContext = createEglContext(sharedContext, this.eglDisplay, this.eglConfig, openGlesVersion);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void createSurface(Surface surface) {
/*     */     class FakeSurfaceHolder
/*     */       implements SurfaceHolder
/*     */     {
/*     */       private final Surface surface;
/*     */ 
/*     */ 
/*     */       
/*     */       FakeSurfaceHolder(Surface surface) {
/*  84 */         this.surface = surface;
/*     */       }
/*     */ 
/*     */       
/*     */       public void addCallback(SurfaceHolder.Callback callback) {}
/*     */ 
/*     */       
/*     */       public void removeCallback(SurfaceHolder.Callback callback) {}
/*     */ 
/*     */       
/*     */       public boolean isCreating() {
/*  95 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       @Deprecated
/*     */       public void setType(int i) {}
/*     */ 
/*     */       
/*     */       public void setFixedSize(int i, int i2) {}
/*     */ 
/*     */       
/*     */       public void setSizeFromLayout() {}
/*     */ 
/*     */       
/*     */       public void setFormat(int i) {}
/*     */ 
/*     */       
/*     */       public void setKeepScreenOn(boolean b) {}
/*     */ 
/*     */       
/*     */       @Nullable
/*     */       public Canvas lockCanvas() {
/* 117 */         return null;
/*     */       }
/*     */ 
/*     */       
/*     */       @Nullable
/*     */       public Canvas lockCanvas(Rect rect) {
/* 123 */         return null;
/*     */       }
/*     */ 
/*     */       
/*     */       public void unlockCanvasAndPost(Canvas canvas) {}
/*     */ 
/*     */       
/*     */       @Nullable
/*     */       public Rect getSurfaceFrame() {
/* 132 */         return null;
/*     */       }
/*     */ 
/*     */       
/*     */       public Surface getSurface() {
/* 137 */         return this.surface;
/*     */       }
/*     */     };
/*     */     
/* 141 */     createSurfaceInternal(new FakeSurfaceHolder(surface));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void createSurface(SurfaceTexture surfaceTexture) {
/* 147 */     createSurfaceInternal(surfaceTexture);
/*     */   }
/*     */ 
/*     */   
/*     */   private void createSurfaceInternal(Object nativeWindow) {
/* 152 */     if (!(nativeWindow instanceof SurfaceHolder) && !(nativeWindow instanceof SurfaceTexture)) {
/* 153 */       throw new IllegalStateException("Input must be either a SurfaceHolder or SurfaceTexture");
/*     */     }
/* 155 */     checkIsNotReleased();
/* 156 */     if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
/* 157 */       throw new RuntimeException("Already has an EGLSurface");
/*     */     }
/* 159 */     int[] surfaceAttribs = { 12344 };
/* 160 */     this.eglSurface = this.egl.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, nativeWindow, surfaceAttribs);
/* 161 */     if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
/* 162 */       throw new RuntimeException("Failed to create window surface: 0x" + 
/* 163 */           Integer.toHexString(this.egl.eglGetError()));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void createDummyPbufferSurface() {
/* 170 */     createPbufferSurface(1, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void createPbufferSurface(int width, int height) {
/* 175 */     checkIsNotReleased();
/* 176 */     if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
/* 177 */       throw new RuntimeException("Already has an EGLSurface");
/*     */     }
/* 179 */     int[] surfaceAttribs = { 12375, width, 12374, height, 12344 };
/* 180 */     this.eglSurface = this.egl.eglCreatePbufferSurface(this.eglDisplay, this.eglConfig, surfaceAttribs);
/* 181 */     if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
/* 182 */       throw new RuntimeException("Failed to create pixel buffer surface with size " + width + "x" + height + ": 0x" + 
/* 183 */           Integer.toHexString(this.egl.eglGetError()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public EglBase.Context getEglBaseContext() {
/* 189 */     return new Context(this.eglContext);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasSurface() {
/* 194 */     return (this.eglSurface != EGL10.EGL_NO_SURFACE);
/*     */   }
/*     */ 
/*     */   
/*     */   public int surfaceWidth() {
/* 199 */     int[] widthArray = new int[1];
/* 200 */     this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, widthArray);
/* 201 */     return widthArray[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public int surfaceHeight() {
/* 206 */     int[] heightArray = new int[1];
/* 207 */     this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, heightArray);
/* 208 */     return heightArray[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public void releaseSurface() {
/* 213 */     if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
/* 214 */       this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
/* 215 */       this.eglSurface = EGL10.EGL_NO_SURFACE;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkIsNotReleased() {
/* 220 */     if (this.eglDisplay == EGL10.EGL_NO_DISPLAY || this.eglContext == EGL10.EGL_NO_CONTEXT || this.eglConfig == null)
/*     */     {
/* 222 */       throw new RuntimeException("This object has been released");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void release() {
/* 228 */     checkIsNotReleased();
/* 229 */     releaseSurface();
/* 230 */     detachCurrent();
/* 231 */     this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
/* 232 */     this.egl.eglTerminate(this.eglDisplay);
/* 233 */     this.eglContext = EGL10.EGL_NO_CONTEXT;
/* 234 */     this.eglDisplay = EGL10.EGL_NO_DISPLAY;
/* 235 */     this.eglConfig = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void makeCurrent() {
/* 240 */     checkIsNotReleased();
/* 241 */     if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
/* 242 */       throw new RuntimeException("No EGLSurface - can't make current");
/*     */     }
/* 244 */     synchronized (EglBase.lock) {
/* 245 */       if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
/* 246 */         throw new RuntimeException("eglMakeCurrent failed: 0x" + 
/* 247 */             Integer.toHexString(this.egl.eglGetError()));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void detachCurrent() {
/* 255 */     synchronized (EglBase.lock) {
/* 256 */       if (!this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT))
/*     */       {
/* 258 */         throw new RuntimeException("eglDetachCurrent failed: 0x" + 
/* 259 */             Integer.toHexString(this.egl.eglGetError()));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void swapBuffers() {
/* 266 */     checkIsNotReleased();
/* 267 */     if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
/* 268 */       throw new RuntimeException("No EGLSurface - can't swap buffers");
/*     */     }
/* 270 */     synchronized (EglBase.lock) {
/* 271 */       this.egl.eglSwapBuffers(this.eglDisplay, this.eglSurface);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void swapBuffers(long timeStampNs) {
/* 278 */     swapBuffers();
/*     */   }
/*     */ 
/*     */   
/*     */   private EGLDisplay getEglDisplay() {
/* 283 */     EGLDisplay eglDisplay = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
/* 284 */     if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
/* 285 */       throw new RuntimeException("Unable to get EGL10 display: 0x" + 
/* 286 */           Integer.toHexString(this.egl.eglGetError()));
/*     */     }
/* 288 */     int[] version = new int[2];
/* 289 */     if (!this.egl.eglInitialize(eglDisplay, version)) {
/* 290 */       throw new RuntimeException("Unable to initialize EGL10: 0x" + 
/* 291 */           Integer.toHexString(this.egl.eglGetError()));
/*     */     }
/* 293 */     return eglDisplay;
/*     */   }
/*     */ 
/*     */   
/*     */   private EGLConfig getEglConfig(EGLDisplay eglDisplay, int[] configAttributes) {
/* 298 */     EGLConfig[] configs = new EGLConfig[1];
/* 299 */     int[] numConfigs = new int[1];
/* 300 */     if (!this.egl.eglChooseConfig(eglDisplay, configAttributes, configs, configs.length, numConfigs)) {
/* 301 */       throw new RuntimeException("eglChooseConfig failed: 0x" + 
/* 302 */           Integer.toHexString(this.egl.eglGetError()));
/*     */     }
/* 304 */     if (numConfigs[0] <= 0) {
/* 305 */       throw new RuntimeException("Unable to find any matching EGL config");
/*     */     }
/* 307 */     EGLConfig eglConfig = configs[0];
/* 308 */     if (eglConfig == null) {
/* 309 */       throw new RuntimeException("eglChooseConfig returned null");
/*     */     }
/* 311 */     return eglConfig;
/*     */   }
/*     */ 
/*     */   
/*     */   private EGLContext createEglContext(@Nullable EGLContext sharedContext, EGLDisplay eglDisplay, EGLConfig eglConfig, int openGlesVersion) {
/*     */     EGLContext eglContext;
/* 317 */     if (sharedContext != null && sharedContext == EGL10.EGL_NO_CONTEXT) {
/* 318 */       throw new RuntimeException("Invalid sharedContext");
/*     */     }
/* 320 */     int[] contextAttributes = { 12440, openGlesVersion, 12344 };
/* 321 */     EGLContext rootContext = (sharedContext == null) ? EGL10.EGL_NO_CONTEXT : sharedContext;
/*     */     
/* 323 */     synchronized (EglBase.lock) {
/* 324 */       eglContext = this.egl.eglCreateContext(eglDisplay, eglConfig, rootContext, contextAttributes);
/*     */     } 
/* 326 */     if (eglContext == EGL10.EGL_NO_CONTEXT) {
/* 327 */       throw new RuntimeException("Failed to create EGL context: 0x" + 
/* 328 */           Integer.toHexString(this.egl.eglGetError()));
/*     */     }
/* 330 */     return eglContext;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/EglBase10Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */