/*     */ package org.webrtc;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.graphics.SurfaceTexture;
/*     */ import android.opengl.EGL14;
/*     */ import android.opengl.EGLConfig;
/*     */ import android.opengl.EGLContext;
/*     */ import android.opengl.EGLDisplay;
/*     */ import android.opengl.EGLExt;
/*     */ import android.opengl.EGLSurface;
/*     */ import android.os.Build;
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
/*     */ @TargetApi(18)
/*     */ class EglBase14Impl
/*     */   implements EglBase14
/*     */ {
/*     */   private static final String TAG = "EglBase14Impl";
/*     */   private static final int EGLExt_SDK_VERSION = 18;
/*  35 */   private static final int CURRENT_SDK_VERSION = Build.VERSION.SDK_INT;
/*     */   
/*     */   private EGLContext eglContext;
/*     */   
/*  39 */   private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE; @Nullable
/*     */   private EGLConfig eglConfig;
/*     */   private EGLDisplay eglDisplay;
/*     */   
/*     */   public static boolean isEGL14Supported() {
/*  44 */     Logging.d("EglBase14Impl", "SDK version: " + CURRENT_SDK_VERSION + ". isEGL14Supported: " + ((CURRENT_SDK_VERSION >= 18) ? 1 : 0));
/*     */ 
/*     */     
/*  47 */     return (CURRENT_SDK_VERSION >= 18);
/*     */   }
/*     */   
/*     */   public static class Context
/*     */     implements EglBase14.Context {
/*     */     private final EGLContext egl14Context;
/*     */     
/*     */     public EGLContext getRawContext() {
/*  55 */       return this.egl14Context;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @TargetApi(21)
/*     */     public long getNativeEglContext() {
/*  62 */       return (EglBase14Impl.CURRENT_SDK_VERSION >= 21) ? this.egl14Context.getNativeHandle() : 
/*  63 */         this.egl14Context.getHandle();
/*     */     }
/*     */     
/*     */     public Context(EGLContext eglContext) {
/*  67 */       this.egl14Context = eglContext;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public EglBase14Impl(EGLContext sharedContext, int[] configAttributes) {
/*  74 */     this.eglDisplay = getEglDisplay();
/*  75 */     this.eglConfig = getEglConfig(this.eglDisplay, configAttributes);
/*  76 */     int openGlesVersion = EglBase.getOpenGlesVersionFromConfig(configAttributes);
/*  77 */     Logging.d("EglBase14Impl", "Using OpenGL ES version " + openGlesVersion);
/*  78 */     this.eglContext = createEglContext(sharedContext, this.eglDisplay, this.eglConfig, openGlesVersion);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void createSurface(Surface surface) {
/*  84 */     createSurfaceInternal(surface);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void createSurface(SurfaceTexture surfaceTexture) {
/*  90 */     createSurfaceInternal(surfaceTexture);
/*     */   }
/*     */ 
/*     */   
/*     */   private void createSurfaceInternal(Object surface) {
/*  95 */     if (!(surface instanceof Surface) && !(surface instanceof SurfaceTexture)) {
/*  96 */       throw new IllegalStateException("Input must be either a Surface or SurfaceTexture");
/*     */     }
/*  98 */     checkIsNotReleased();
/*  99 */     if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
/* 100 */       throw new RuntimeException("Already has an EGLSurface");
/*     */     }
/* 102 */     int[] surfaceAttribs = { 12344 };
/* 103 */     this.eglSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surface, surfaceAttribs, 0);
/* 104 */     if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
/* 105 */       throw new RuntimeException("Failed to create window surface: 0x" + 
/* 106 */           Integer.toHexString(EGL14.eglGetError()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void createDummyPbufferSurface() {
/* 112 */     createPbufferSurface(1, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void createPbufferSurface(int width, int height) {
/* 117 */     checkIsNotReleased();
/* 118 */     if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
/* 119 */       throw new RuntimeException("Already has an EGLSurface");
/*     */     }
/* 121 */     int[] surfaceAttribs = { 12375, width, 12374, height, 12344 };
/* 122 */     this.eglSurface = EGL14.eglCreatePbufferSurface(this.eglDisplay, this.eglConfig, surfaceAttribs, 0);
/* 123 */     if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
/* 124 */       throw new RuntimeException("Failed to create pixel buffer surface with size " + width + "x" + height + ": 0x" + 
/* 125 */           Integer.toHexString(EGL14.eglGetError()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Context getEglBaseContext() {
/* 131 */     return new Context(this.eglContext);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasSurface() {
/* 136 */     return (this.eglSurface != EGL14.EGL_NO_SURFACE);
/*     */   }
/*     */ 
/*     */   
/*     */   public int surfaceWidth() {
/* 141 */     int[] widthArray = new int[1];
/* 142 */     EGL14.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, widthArray, 0);
/* 143 */     return widthArray[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public int surfaceHeight() {
/* 148 */     int[] heightArray = new int[1];
/* 149 */     EGL14.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, heightArray, 0);
/* 150 */     return heightArray[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public void releaseSurface() {
/* 155 */     if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
/* 156 */       EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
/* 157 */       this.eglSurface = EGL14.EGL_NO_SURFACE;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkIsNotReleased() {
/* 162 */     if (this.eglDisplay == EGL14.EGL_NO_DISPLAY || this.eglContext == EGL14.EGL_NO_CONTEXT || this.eglConfig == null)
/*     */     {
/* 164 */       throw new RuntimeException("This object has been released");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void release() {
/* 170 */     checkIsNotReleased();
/* 171 */     releaseSurface();
/* 172 */     detachCurrent();
/* 173 */     synchronized (EglBase.lock) {
/* 174 */       EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
/*     */     } 
/* 176 */     EGL14.eglReleaseThread();
/* 177 */     EGL14.eglTerminate(this.eglDisplay);
/* 178 */     this.eglContext = EGL14.EGL_NO_CONTEXT;
/* 179 */     this.eglDisplay = EGL14.EGL_NO_DISPLAY;
/* 180 */     this.eglConfig = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void makeCurrent() {
/* 185 */     checkIsNotReleased();
/* 186 */     if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
/* 187 */       throw new RuntimeException("No EGLSurface - can't make current");
/*     */     }
/* 189 */     synchronized (EglBase.lock) {
/* 190 */       if (!EGL14.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
/* 191 */         throw new RuntimeException("eglMakeCurrent failed: 0x" + 
/* 192 */             Integer.toHexString(EGL14.eglGetError()));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void detachCurrent() {
/* 200 */     synchronized (EglBase.lock) {
/* 201 */       if (!EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT))
/*     */       {
/* 203 */         throw new RuntimeException("eglDetachCurrent failed: 0x" + 
/* 204 */             Integer.toHexString(EGL14.eglGetError()));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void swapBuffers() {
/* 211 */     checkIsNotReleased();
/* 212 */     if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
/* 213 */       throw new RuntimeException("No EGLSurface - can't swap buffers");
/*     */     }
/* 215 */     synchronized (EglBase.lock) {
/* 216 */       EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void swapBuffers(long timeStampNs) {
/* 222 */     checkIsNotReleased();
/* 223 */     if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
/* 224 */       throw new RuntimeException("No EGLSurface - can't swap buffers");
/*     */     }
/* 226 */     synchronized (EglBase.lock) {
/*     */ 
/*     */       
/* 229 */       EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, timeStampNs);
/* 230 */       EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static EGLDisplay getEglDisplay() {
/* 236 */     EGLDisplay eglDisplay = EGL14.eglGetDisplay(0);
/* 237 */     if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
/* 238 */       throw new RuntimeException("Unable to get EGL14 display: 0x" + 
/* 239 */           Integer.toHexString(EGL14.eglGetError()));
/*     */     }
/* 241 */     int[] version = new int[2];
/* 242 */     if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
/* 243 */       throw new RuntimeException("Unable to initialize EGL14: 0x" + 
/* 244 */           Integer.toHexString(EGL14.eglGetError()));
/*     */     }
/* 246 */     return eglDisplay;
/*     */   }
/*     */ 
/*     */   
/*     */   private static EGLConfig getEglConfig(EGLDisplay eglDisplay, int[] configAttributes) {
/* 251 */     EGLConfig[] configs = new EGLConfig[1];
/* 252 */     int[] numConfigs = new int[1];
/* 253 */     if (!EGL14.eglChooseConfig(eglDisplay, configAttributes, 0, configs, 0, configs.length, numConfigs, 0))
/*     */     {
/* 255 */       throw new RuntimeException("eglChooseConfig failed: 0x" + 
/* 256 */           Integer.toHexString(EGL14.eglGetError()));
/*     */     }
/* 258 */     if (numConfigs[0] <= 0) {
/* 259 */       throw new RuntimeException("Unable to find any matching EGL config");
/*     */     }
/* 261 */     EGLConfig eglConfig = configs[0];
/* 262 */     if (eglConfig == null) {
/* 263 */       throw new RuntimeException("eglChooseConfig returned null");
/*     */     }
/* 265 */     return eglConfig;
/*     */   }
/*     */ 
/*     */   
/*     */   private static EGLContext createEglContext(@Nullable EGLContext sharedContext, EGLDisplay eglDisplay, EGLConfig eglConfig, int openGlesVersion) {
/*     */     EGLContext eglContext;
/* 271 */     if (sharedContext != null && sharedContext == EGL14.EGL_NO_CONTEXT) {
/* 272 */       throw new RuntimeException("Invalid sharedContext");
/*     */     }
/* 274 */     int[] contextAttributes = { 12440, openGlesVersion, 12344 };
/* 275 */     EGLContext rootContext = (sharedContext == null) ? EGL14.EGL_NO_CONTEXT : sharedContext;
/*     */     
/* 277 */     synchronized (EglBase.lock) {
/* 278 */       eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, rootContext, contextAttributes, 0);
/*     */     } 
/* 280 */     if (eglContext == EGL14.EGL_NO_CONTEXT) {
/* 281 */       throw new RuntimeException("Failed to create EGL context: 0x" + 
/* 282 */           Integer.toHexString(EGL14.eglGetError()));
/*     */     }
/* 284 */     return eglContext;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/EglBase14Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */