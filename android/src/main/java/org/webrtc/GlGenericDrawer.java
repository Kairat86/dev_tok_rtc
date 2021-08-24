/*     */ package org.webrtc;
/*     */ 
/*     */ import android.opengl.GLES20;
/*     */ import androidx.annotation.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class GlGenericDrawer
/*     */   implements RendererCommon.GlDrawer
/*     */ {
/*     */   private static final String INPUT_VERTEX_COORDINATE_NAME = "in_pos";
/*     */   private static final String INPUT_TEXTURE_COORDINATE_NAME = "in_tc";
/*     */   private static final String TEXTURE_MATRIX_NAME = "tex_mat";
/*     */   private static final String DEFAULT_VERTEX_SHADER_STRING = "varying vec2 tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\nuniform mat4 tex_mat;\nvoid main() {\n  gl_Position = in_pos;\n  tc = (tex_mat * in_tc).xy;\n}\n";
/*     */   
/*     */   public static interface ShaderCallbacks
/*     */   {
/*     */     void onNewShader(GlShader param1GlShader);
/*     */     
/*     */     void onPrepareShader(GlShader param1GlShader, float[] param1ArrayOffloat, int param1Int1, int param1Int2, int param1Int3, int param1Int4);
/*     */   }
/*     */   
/*     */   public enum ShaderType
/*     */   {
/*  39 */     OES, RGB, YUV;
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
/*  75 */   private static final FloatBuffer FULL_RECTANGLE_BUFFER = GlUtil.createFloatBuffer(new float[] { -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   private static final FloatBuffer FULL_RECTANGLE_TEXTURE_BUFFER = GlUtil.createFloatBuffer(new float[] { 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F });
/*     */   private final String genericFragmentSource;
/*     */   private final String vertexShader;
/*     */   private final ShaderCallbacks shaderCallbacks;
/*     */   @Nullable
/*     */   private ShaderType currentShaderType;
/*     */   
/*     */   static String createFragmentShaderString(String genericFragmentSource, ShaderType shaderType) {
/*  92 */     StringBuilder stringBuilder = new StringBuilder();
/*  93 */     if (shaderType == ShaderType.OES) {
/*  94 */       stringBuilder.append("#extension GL_OES_EGL_image_external : require\n");
/*     */     }
/*  96 */     stringBuilder.append("precision mediump float;\n");
/*  97 */     stringBuilder.append("varying vec2 tc;\n");
/*     */     
/*  99 */     if (shaderType == ShaderType.YUV) {
/* 100 */       stringBuilder.append("uniform sampler2D y_tex;\n");
/* 101 */       stringBuilder.append("uniform sampler2D u_tex;\n");
/* 102 */       stringBuilder.append("uniform sampler2D v_tex;\n");
/*     */ 
/*     */ 
/*     */       
/* 106 */       stringBuilder.append("vec4 sample(vec2 p) {\n");
/* 107 */       stringBuilder.append("  float y = texture2D(y_tex, p).r * 1.16438;\n");
/* 108 */       stringBuilder.append("  float u = texture2D(u_tex, p).r;\n");
/* 109 */       stringBuilder.append("  float v = texture2D(v_tex, p).r;\n");
/* 110 */       stringBuilder.append("  return vec4(y + 1.59603 * v - 0.874202,\n");
/* 111 */       stringBuilder.append("    y - 0.391762 * u - 0.812968 * v + 0.531668,\n");
/* 112 */       stringBuilder.append("    y + 2.01723 * u - 1.08563, 1);\n");
/* 113 */       stringBuilder.append("}\n");
/* 114 */       stringBuilder.append(genericFragmentSource);
/*     */     } else {
/* 116 */       String samplerName = (shaderType == ShaderType.OES) ? "samplerExternalOES" : "sampler2D";
/* 117 */       stringBuilder.append("uniform ").append(samplerName).append(" tex;\n");
/*     */ 
/*     */       
/* 120 */       stringBuilder.append(genericFragmentSource.replace("sample(", "texture2D(tex, "));
/*     */     } 
/*     */     
/* 123 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private GlShader currentShader;
/*     */   
/*     */   private int inPosLocation;
/*     */   
/*     */   private int inTcLocation;
/*     */   private int texMatrixLocation;
/*     */   
/*     */   public GlGenericDrawer(String genericFragmentSource, ShaderCallbacks shaderCallbacks) {
/* 136 */     this("varying vec2 tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\nuniform mat4 tex_mat;\nvoid main() {\n  gl_Position = in_pos;\n  tc = (tex_mat * in_tc).xy;\n}\n", genericFragmentSource, shaderCallbacks);
/*     */   }
/*     */ 
/*     */   
/*     */   public GlGenericDrawer(String vertexShader, String genericFragmentSource, ShaderCallbacks shaderCallbacks) {
/* 141 */     this.vertexShader = vertexShader;
/* 142 */     this.genericFragmentSource = genericFragmentSource;
/* 143 */     this.shaderCallbacks = shaderCallbacks;
/*     */   }
/*     */ 
/*     */   
/*     */   GlShader createShader(ShaderType shaderType) {
/* 148 */     return new GlShader(this.vertexShader, 
/* 149 */         createFragmentShaderString(this.genericFragmentSource, shaderType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawOes(int oesTextureId, float[] texMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
/* 159 */     prepareShader(ShaderType.OES, texMatrix, frameWidth, frameHeight, viewportWidth, viewportHeight);
/*     */ 
/*     */     
/* 162 */     GLES20.glActiveTexture(33984);
/* 163 */     GLES20.glBindTexture(36197, oesTextureId);
/*     */     
/* 165 */     GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
/* 166 */     GLES20.glDrawArrays(5, 0, 4);
/*     */     
/* 168 */     GLES20.glBindTexture(36197, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawRgb(int textureId, float[] texMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
/* 178 */     prepareShader(ShaderType.RGB, texMatrix, frameWidth, frameHeight, viewportWidth, viewportHeight);
/*     */ 
/*     */     
/* 181 */     GLES20.glActiveTexture(33984);
/* 182 */     GLES20.glBindTexture(3553, textureId);
/*     */     
/* 184 */     GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
/* 185 */     GLES20.glDrawArrays(5, 0, 4);
/*     */     
/* 187 */     GLES20.glBindTexture(3553, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawYuv(int[] yuvTextures, float[] texMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
/* 197 */     prepareShader(ShaderType.YUV, texMatrix, frameWidth, frameHeight, viewportWidth, viewportHeight);
/*     */     
/*     */     int i;
/* 200 */     for (i = 0; i < 3; i++) {
/* 201 */       GLES20.glActiveTexture(33984 + i);
/* 202 */       GLES20.glBindTexture(3553, yuvTextures[i]);
/*     */     } 
/*     */     
/* 205 */     GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
/* 206 */     GLES20.glDrawArrays(5, 0, 4);
/*     */     
/* 208 */     for (i = 0; i < 3; i++) {
/* 209 */       GLES20.glActiveTexture(33984 + i);
/* 210 */       GLES20.glBindTexture(3553, 0);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void prepareShader(ShaderType shaderType, float[] texMatrix, int frameWidth, int frameHeight, int viewportWidth, int viewportHeight) {
/*     */     GlShader shader;
/* 217 */     if (shaderType.equals(this.currentShaderType)) {
/*     */       
/* 219 */       shader = this.currentShader;
/*     */     } else {
/*     */       
/* 222 */       this.currentShaderType = shaderType;
/* 223 */       if (this.currentShader != null) {
/* 224 */         this.currentShader.release();
/*     */       }
/* 226 */       shader = createShader(shaderType);
/* 227 */       this.currentShader = shader;
/*     */       
/* 229 */       shader.useProgram();
/*     */       
/* 231 */       if (shaderType == ShaderType.YUV) {
/* 232 */         GLES20.glUniform1i(shader.getUniformLocation("y_tex"), 0);
/* 233 */         GLES20.glUniform1i(shader.getUniformLocation("u_tex"), 1);
/* 234 */         GLES20.glUniform1i(shader.getUniformLocation("v_tex"), 2);
/*     */       } else {
/* 236 */         GLES20.glUniform1i(shader.getUniformLocation("tex"), 0);
/*     */       } 
/*     */       
/* 239 */       GlUtil.checkNoGLES2Error("Create shader");
/* 240 */       this.shaderCallbacks.onNewShader(shader);
/* 241 */       this.texMatrixLocation = shader.getUniformLocation("tex_mat");
/* 242 */       this.inPosLocation = shader.getAttribLocation("in_pos");
/* 243 */       this.inTcLocation = shader.getAttribLocation("in_tc");
/*     */     } 
/*     */     
/* 246 */     shader.useProgram();
/*     */ 
/*     */     
/* 249 */     GLES20.glEnableVertexAttribArray(this.inPosLocation);
/* 250 */     GLES20.glVertexAttribPointer(this.inPosLocation, 2, 5126, false, 0, FULL_RECTANGLE_BUFFER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 255 */     GLES20.glEnableVertexAttribArray(this.inTcLocation);
/* 256 */     GLES20.glVertexAttribPointer(this.inTcLocation, 2, 5126, false, 0, FULL_RECTANGLE_TEXTURE_BUFFER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 261 */     GLES20.glUniformMatrix4fv(this.texMatrixLocation, 1, false, texMatrix, 0);
/*     */ 
/*     */ 
/*     */     
/* 265 */     this.shaderCallbacks.onPrepareShader(shader, texMatrix, frameWidth, frameHeight, viewportWidth, viewportHeight);
/*     */     
/* 267 */     GlUtil.checkNoGLES2Error("Prepare shader");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/* 275 */     if (this.currentShader != null) {
/* 276 */       this.currentShader.release();
/* 277 */       this.currentShader = null;
/* 278 */       this.currentShaderType = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/GlGenericDrawer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */