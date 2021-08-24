/*     */ package org.webrtc;
/*     */ 
/*     */ import android.graphics.Matrix;
/*     */ import android.opengl.GLES20;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class YuvConverter
/*     */ {
/*     */   private static final String FRAGMENT_SHADER = "uniform vec2 xUnit;\nuniform vec4 coeffs;\n\nvoid main() {\n  gl_FragColor.r = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 1.5 * xUnit).rgb);\n  gl_FragColor.g = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 0.5 * xUnit).rgb);\n  gl_FragColor.b = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 0.5 * xUnit).rgb);\n  gl_FragColor.a = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 1.5 * xUnit).rgb);\n}\n";
/*     */   
/*     */   private static class ShaderCallbacks
/*     */     implements GlGenericDrawer.ShaderCallbacks
/*     */   {
/*     */     private ShaderCallbacks() {}
/*     */     
/*  61 */     private static final float[] yCoeffs = new float[] { 0.256788F, 0.504129F, 0.0979059F, 0.0627451F };
/*     */     
/*  63 */     private static final float[] uCoeffs = new float[] { -0.148223F, -0.290993F, 0.439216F, 0.501961F };
/*     */     
/*  65 */     private static final float[] vCoeffs = new float[] { 0.439216F, -0.367788F, -0.0714274F, 0.501961F };
/*     */     
/*     */     private int xUnitLoc;
/*     */     
/*     */     private int coeffsLoc;
/*     */     
/*     */     private float[] coeffs;
/*     */     private float stepSize;
/*     */     
/*     */     public void setPlaneY() {
/*  75 */       this.coeffs = yCoeffs;
/*  76 */       this.stepSize = 1.0F;
/*     */     }
/*     */     
/*     */     public void setPlaneU() {
/*  80 */       this.coeffs = uCoeffs;
/*  81 */       this.stepSize = 2.0F;
/*     */     }
/*     */     
/*     */     public void setPlaneV() {
/*  85 */       this.coeffs = vCoeffs;
/*  86 */       this.stepSize = 2.0F;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onNewShader(GlShader shader) {
/*  91 */       this.xUnitLoc = shader.getUniformLocation("xUnit");
/*  92 */       this.coeffsLoc = shader.getUniformLocation("coeffs");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onPrepareShader(GlShader shader, float[] texMatrix, int frameWidth, int frameHeight, int viewportWidth, int viewportHeight) {
/*  98 */       GLES20.glUniform4fv(this.coeffsLoc, 1, this.coeffs, 0);
/*     */       
/* 100 */       GLES20.glUniform2f(this.xUnitLoc, this.stepSize * texMatrix[0] / frameWidth, this.stepSize * texMatrix[1] / frameWidth);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 105 */   private final ThreadUtils.ThreadChecker threadChecker = new ThreadUtils.ThreadChecker();
/* 106 */   private final GlTextureFrameBuffer i420TextureFrameBuffer = new GlTextureFrameBuffer(6408);
/*     */   
/* 108 */   private final ShaderCallbacks shaderCallbacks = new ShaderCallbacks();
/* 109 */   private final GlGenericDrawer drawer = new GlGenericDrawer("uniform vec2 xUnit;\nuniform vec4 coeffs;\n\nvoid main() {\n  gl_FragColor.r = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 1.5 * xUnit).rgb);\n  gl_FragColor.g = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 0.5 * xUnit).rgb);\n  gl_FragColor.b = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 0.5 * xUnit).rgb);\n  gl_FragColor.a = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 1.5 * xUnit).rgb);\n}\n", this.shaderCallbacks);
/*     */ 
/*     */   
/*     */   private final VideoFrameDrawer videoFrameDrawer;
/*     */ 
/*     */   
/*     */   public YuvConverter() {
/* 116 */     this(new VideoFrameDrawer());
/*     */   }
/*     */   
/*     */   public YuvConverter(VideoFrameDrawer videoFrameDrawer) {
/* 120 */     this.videoFrameDrawer = videoFrameDrawer;
/* 121 */     this.threadChecker.detachThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public VideoFrame.I420Buffer convert(VideoFrame.TextureBuffer inputTextureBuffer) {
/* 126 */     this.threadChecker.checkIsOnValidThread();
/*     */     
/* 128 */     VideoFrame.TextureBuffer preparedBuffer = (VideoFrame.TextureBuffer)this.videoFrameDrawer.prepareBufferForViewportSize(inputTextureBuffer, inputTextureBuffer
/* 129 */         .getWidth(), inputTextureBuffer.getHeight());
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
/* 159 */     int frameWidth = preparedBuffer.getWidth();
/* 160 */     int frameHeight = preparedBuffer.getHeight();
/* 161 */     int stride = (frameWidth + 7) / 8 * 8;
/* 162 */     int uvHeight = (frameHeight + 1) / 2;
/*     */     
/* 164 */     int totalHeight = frameHeight + uvHeight;
/* 165 */     ByteBuffer i420ByteBuffer = JniCommon.nativeAllocateByteBuffer(stride * totalHeight);
/*     */ 
/*     */     
/* 168 */     int viewportWidth = stride / 4;
/*     */ 
/*     */     
/* 171 */     Matrix renderMatrix = new Matrix();
/* 172 */     renderMatrix.preTranslate(0.5F, 0.5F);
/* 173 */     renderMatrix.preScale(1.0F, -1.0F);
/* 174 */     renderMatrix.preTranslate(-0.5F, -0.5F);
/*     */     
/* 176 */     this.i420TextureFrameBuffer.setSize(viewportWidth, totalHeight);
/*     */ 
/*     */     
/* 179 */     GLES20.glBindFramebuffer(36160, this.i420TextureFrameBuffer.getFrameBufferId());
/* 180 */     GlUtil.checkNoGLES2Error("glBindFramebuffer");
/*     */ 
/*     */     
/* 183 */     this.shaderCallbacks.setPlaneY();
/* 184 */     VideoFrameDrawer.drawTexture(this.drawer, preparedBuffer, renderMatrix, frameWidth, frameHeight, 0, 0, viewportWidth, frameHeight);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 189 */     this.shaderCallbacks.setPlaneU();
/* 190 */     VideoFrameDrawer.drawTexture(this.drawer, preparedBuffer, renderMatrix, frameWidth, frameHeight, 0, frameHeight, viewportWidth / 2, uvHeight);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 195 */     this.shaderCallbacks.setPlaneV();
/* 196 */     VideoFrameDrawer.drawTexture(this.drawer, preparedBuffer, renderMatrix, frameWidth, frameHeight, viewportWidth / 2, frameHeight, viewportWidth / 2, uvHeight);
/*     */ 
/*     */ 
/*     */     
/* 200 */     GLES20.glReadPixels(0, 0, this.i420TextureFrameBuffer.getWidth(), this.i420TextureFrameBuffer.getHeight(), 6408, 5121, i420ByteBuffer);
/*     */ 
/*     */     
/* 203 */     GlUtil.checkNoGLES2Error("YuvConverter.convert");
/*     */ 
/*     */     
/* 206 */     GLES20.glBindFramebuffer(36160, 0);
/*     */ 
/*     */     
/* 209 */     int yPos = 0;
/* 210 */     int uPos = 0 + stride * frameHeight;
/*     */     
/* 212 */     int vPos = uPos + stride / 2;
/*     */     
/* 214 */     i420ByteBuffer.position(0);
/* 215 */     i420ByteBuffer.limit(0 + stride * frameHeight);
/* 216 */     ByteBuffer dataY = i420ByteBuffer.slice();
/*     */     
/* 218 */     i420ByteBuffer.position(uPos);
/*     */     
/* 220 */     int uvSize = stride * (uvHeight - 1) + stride / 2;
/* 221 */     i420ByteBuffer.limit(uPos + uvSize);
/* 222 */     ByteBuffer dataU = i420ByteBuffer.slice();
/*     */     
/* 224 */     i420ByteBuffer.position(vPos);
/* 225 */     i420ByteBuffer.limit(vPos + uvSize);
/* 226 */     ByteBuffer dataV = i420ByteBuffer.slice();
/*     */     
/* 228 */     preparedBuffer.release();
/*     */     
/* 230 */     return JavaI420Buffer.wrap(frameWidth, frameHeight, dataY, stride, dataU, stride, dataV, stride, () -> JniCommon.nativeFreeByteBuffer(i420ByteBuffer));
/*     */   }
/*     */ 
/*     */   
/*     */   public void release() {
/* 235 */     this.threadChecker.checkIsOnValidThread();
/* 236 */     this.drawer.release();
/* 237 */     this.i420TextureFrameBuffer.release();
/* 238 */     this.videoFrameDrawer.release();
/*     */     
/* 240 */     this.threadChecker.detachThread();
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/YuvConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */