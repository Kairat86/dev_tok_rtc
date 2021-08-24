/*     */ package org.webrtc;
/*     */ 
/*     */ import android.opengl.GLES20;
/*     */ import java.nio.FloatBuffer;
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
/*     */ public class GlShader
/*     */ {
/*     */   private static final String TAG = "GlShader";
/*     */   private int program;
/*     */   
/*     */   private static int compileShader(int shaderType, String source) {
/*  22 */     int shader = GLES20.glCreateShader(shaderType);
/*  23 */     if (shader == 0) {
/*  24 */       throw new RuntimeException("glCreateShader() failed. GLES20 error: " + GLES20.glGetError());
/*     */     }
/*  26 */     GLES20.glShaderSource(shader, source);
/*  27 */     GLES20.glCompileShader(shader);
/*  28 */     int[] compileStatus = { 0 };
/*  29 */     GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
/*  30 */     if (compileStatus[0] != 1) {
/*  31 */       Logging.e("GlShader", "Compile error " + 
/*  32 */           GLES20.glGetShaderInfoLog(shader) + " in shader:\n" + source);
/*  33 */       throw new RuntimeException(GLES20.glGetShaderInfoLog(shader));
/*     */     } 
/*  35 */     GlUtil.checkNoGLES2Error("compileShader");
/*  36 */     return shader;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public GlShader(String vertexSource, String fragmentSource) {
/*  42 */     int vertexShader = compileShader(35633, vertexSource);
/*  43 */     int fragmentShader = compileShader(35632, fragmentSource);
/*  44 */     this.program = GLES20.glCreateProgram();
/*  45 */     if (this.program == 0) {
/*  46 */       throw new RuntimeException("glCreateProgram() failed. GLES20 error: " + GLES20.glGetError());
/*     */     }
/*  48 */     GLES20.glAttachShader(this.program, vertexShader);
/*  49 */     GLES20.glAttachShader(this.program, fragmentShader);
/*  50 */     GLES20.glLinkProgram(this.program);
/*  51 */     int[] linkStatus = { 0 };
/*  52 */     GLES20.glGetProgramiv(this.program, 35714, linkStatus, 0);
/*  53 */     if (linkStatus[0] != 1) {
/*  54 */       Logging.e("GlShader", "Could not link program: " + GLES20.glGetProgramInfoLog(this.program));
/*  55 */       throw new RuntimeException(GLES20.glGetProgramInfoLog(this.program));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  64 */     GLES20.glDeleteShader(vertexShader);
/*  65 */     GLES20.glDeleteShader(fragmentShader);
/*  66 */     GlUtil.checkNoGLES2Error("Creating GlShader");
/*     */   }
/*     */   
/*     */   public int getAttribLocation(String label) {
/*  70 */     if (this.program == -1) {
/*  71 */       throw new RuntimeException("The program has been released");
/*     */     }
/*  73 */     int location = GLES20.glGetAttribLocation(this.program, label);
/*  74 */     if (location < 0) {
/*  75 */       throw new RuntimeException("Could not locate '" + label + "' in program");
/*     */     }
/*  77 */     return location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVertexAttribArray(String label, int dimension, FloatBuffer buffer) {
/*  85 */     setVertexAttribArray(label, dimension, 0, buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVertexAttribArray(String label, int dimension, int stride, FloatBuffer buffer) {
/*  93 */     if (this.program == -1) {
/*  94 */       throw new RuntimeException("The program has been released");
/*     */     }
/*  96 */     int location = getAttribLocation(label);
/*  97 */     GLES20.glEnableVertexAttribArray(location);
/*  98 */     GLES20.glVertexAttribPointer(location, dimension, 5126, false, stride, buffer);
/*  99 */     GlUtil.checkNoGLES2Error("setVertexAttribArray");
/*     */   }
/*     */   
/*     */   public int getUniformLocation(String label) {
/* 103 */     if (this.program == -1) {
/* 104 */       throw new RuntimeException("The program has been released");
/*     */     }
/* 106 */     int location = GLES20.glGetUniformLocation(this.program, label);
/* 107 */     if (location < 0) {
/* 108 */       throw new RuntimeException("Could not locate uniform '" + label + "' in program");
/*     */     }
/* 110 */     return location;
/*     */   }
/*     */   
/*     */   public void useProgram() {
/* 114 */     if (this.program == -1) {
/* 115 */       throw new RuntimeException("The program has been released");
/*     */     }
/* 117 */     synchronized (EglBase.lock) {
/* 118 */       GLES20.glUseProgram(this.program);
/*     */     } 
/* 120 */     GlUtil.checkNoGLES2Error("glUseProgram");
/*     */   }
/*     */   
/*     */   public void release() {
/* 124 */     Logging.d("GlShader", "Deleting shader.");
/*     */     
/* 126 */     if (this.program != -1) {
/* 127 */       GLES20.glDeleteProgram(this.program);
/* 128 */       this.program = -1;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/GlShader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */