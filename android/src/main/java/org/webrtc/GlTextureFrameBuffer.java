/*     */ package org.webrtc;
/*     */ 
/*     */ import android.opengl.GLES20;
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
/*     */ public class GlTextureFrameBuffer
/*     */ {
/*     */   private final int pixelFormat;
/*     */   private int frameBufferId;
/*     */   private int textureId;
/*     */   private int width;
/*     */   private int height;
/*     */   
/*     */   public GlTextureFrameBuffer(int pixelFormat) {
/*  33 */     switch (pixelFormat) {
/*     */       case 6407:
/*     */       case 6408:
/*     */       case 6409:
/*  37 */         this.pixelFormat = pixelFormat;
/*     */         break;
/*     */       default:
/*  40 */         throw new IllegalArgumentException("Invalid pixel format: " + pixelFormat);
/*     */     } 
/*  42 */     this.width = 0;
/*  43 */     this.height = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSize(int width, int height) {
/*  52 */     if (width <= 0 || height <= 0) {
/*  53 */       throw new IllegalArgumentException("Invalid size: " + width + "x" + height);
/*     */     }
/*  55 */     if (width == this.width && height == this.height) {
/*     */       return;
/*     */     }
/*  58 */     this.width = width;
/*  59 */     this.height = height;
/*     */     
/*  61 */     if (this.textureId == 0) {
/*  62 */       this.textureId = GlUtil.generateTexture(3553);
/*     */     }
/*  64 */     if (this.frameBufferId == 0) {
/*  65 */       int[] frameBuffers = new int[1];
/*  66 */       GLES20.glGenFramebuffers(1, frameBuffers, 0);
/*  67 */       this.frameBufferId = frameBuffers[0];
/*     */     } 
/*     */ 
/*     */     
/*  71 */     GLES20.glActiveTexture(33984);
/*  72 */     GLES20.glBindTexture(3553, this.textureId);
/*  73 */     GLES20.glTexImage2D(3553, 0, this.pixelFormat, width, height, 0, this.pixelFormat, 5121, null);
/*     */     
/*  75 */     GLES20.glBindTexture(3553, 0);
/*  76 */     GlUtil.checkNoGLES2Error("GlTextureFrameBuffer setSize");
/*     */ 
/*     */     
/*  79 */     GLES20.glBindFramebuffer(36160, this.frameBufferId);
/*  80 */     GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.textureId, 0);
/*     */ 
/*     */ 
/*     */     
/*  84 */     int status = GLES20.glCheckFramebufferStatus(36160);
/*  85 */     if (status != 36053) {
/*  86 */       throw new IllegalStateException("Framebuffer not complete, status: " + status);
/*     */     }
/*     */     
/*  89 */     GLES20.glBindFramebuffer(36160, 0);
/*     */   }
/*     */   
/*     */   public int getWidth() {
/*  93 */     return this.width;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/*  97 */     return this.height;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFrameBufferId() {
/* 102 */     return this.frameBufferId;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTextureId() {
/* 107 */     return this.textureId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/* 115 */     GLES20.glDeleteTextures(1, new int[] { this.textureId }, 0);
/* 116 */     this.textureId = 0;
/* 117 */     GLES20.glDeleteFramebuffers(1, new int[] { this.frameBufferId }, 0);
/* 118 */     this.frameBufferId = 0;
/* 119 */     this.width = 0;
/* 120 */     this.height = 0;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/GlTextureFrameBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */