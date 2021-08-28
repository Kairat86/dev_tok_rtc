/*     */ package org.webrtc;
/*     */ 
/*     */ import android.os.Handler;
/*     */ import android.os.HandlerThread;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
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
/*     */ public class VideoFileRenderer
/*     */   implements VideoSink
/*     */ {
/*     */   private static final String TAG = "VideoFileRenderer";
/*     */   private final HandlerThread renderThread;
/*     */   private final Handler renderThreadHandler;
/*     */   private final HandlerThread fileThread;
/*     */   private final Handler fileThreadHandler;
/*     */   private final FileOutputStream videoOutFile;
/*     */   private final String outputFileName;
/*     */   private final int outputFileWidth;
/*     */   private final int outputFileHeight;
/*     */   private final int outputFrameSize;
/*     */   private final ByteBuffer outputFrameBuffer;
/*     */   private EglBase eglBase;
/*     */   private YuvConverter yuvConverter;
/*     */   private int frameCount;
/*     */   
/*     */   public VideoFileRenderer(String outputFile, int outputFileWidth, int outputFileHeight, final EglBase.Context sharedContext) throws IOException {
/*  43 */     if (outputFileWidth % 2 == 1 || outputFileHeight % 2 == 1) {
/*  44 */       throw new IllegalArgumentException("Does not support uneven width or height");
/*     */     }
/*     */     
/*  47 */     this.outputFileName = outputFile;
/*  48 */     this.outputFileWidth = outputFileWidth;
/*  49 */     this.outputFileHeight = outputFileHeight;
/*     */     
/*  51 */     this.outputFrameSize = outputFileWidth * outputFileHeight * 3 / 2;
/*  52 */     this.outputFrameBuffer = ByteBuffer.allocateDirect(this.outputFrameSize);
/*     */     
/*  54 */     this.videoOutFile = new FileOutputStream(outputFile);
/*  55 */     this.videoOutFile.write(("YUV4MPEG2 C420 W" + outputFileWidth + " H" + outputFileHeight + " Ip F30:1 A1:1\n")
/*     */         
/*  57 */         .getBytes(Charset.forName("US-ASCII")));
/*     */     
/*  59 */     this.renderThread = new HandlerThread("VideoFileRendererRenderThread");
/*  60 */     this.renderThread.start();
/*  61 */     this.renderThreadHandler = new Handler(this.renderThread.getLooper());
/*     */     
/*  63 */     this.fileThread = new HandlerThread("VideoFileRendererFileThread");
/*  64 */     this.fileThread.start();
/*  65 */     this.fileThreadHandler = new Handler(this.fileThread.getLooper());
/*     */     
/*  67 */     ThreadUtils.invokeAtFrontUninterruptedly(this.renderThreadHandler, new Runnable()
/*     */         {
/*     */           public void run() {
/*  70 */             VideoFileRenderer.this.eglBase = EglBase.create(sharedContext, EglBase.CONFIG_PIXEL_BUFFER);
/*  71 */             VideoFileRenderer.this.eglBase.createDummyPbufferSurface();
/*  72 */             VideoFileRenderer.this.eglBase.makeCurrent();
/*  73 */             VideoFileRenderer.this.yuvConverter = new YuvConverter();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public void onFrame(VideoFrame frame) {
/*  80 */     frame.retain();
/*  81 */     this.renderThreadHandler.post(() -> renderFrameOnRenderThread(frame));
/*     */   }
/*     */   
/*     */   private void renderFrameOnRenderThread(VideoFrame frame) {
/*  85 */     VideoFrame.Buffer buffer = frame.getBuffer();
/*     */ 
/*     */ 
/*     */     
/*  89 */     int targetWidth = (frame.getRotation() % 180 == 0) ? this.outputFileWidth : this.outputFileHeight;
/*  90 */     int targetHeight = (frame.getRotation() % 180 == 0) ? this.outputFileHeight : this.outputFileWidth;
/*     */     
/*  92 */     float frameAspectRatio = buffer.getWidth() / buffer.getHeight();
/*  93 */     float fileAspectRatio = targetWidth / targetHeight;
/*     */ 
/*     */     
/*  96 */     int cropWidth = buffer.getWidth();
/*  97 */     int cropHeight = buffer.getHeight();
/*  98 */     if (fileAspectRatio > frameAspectRatio) {
/*  99 */       cropHeight = (int)(cropHeight * frameAspectRatio / fileAspectRatio);
/*     */     } else {
/* 101 */       cropWidth = (int)(cropWidth * fileAspectRatio / frameAspectRatio);
/*     */     } 
/*     */     
/* 104 */     int cropX = (buffer.getWidth() - cropWidth) / 2;
/* 105 */     int cropY = (buffer.getHeight() - cropHeight) / 2;
/*     */ 
/*     */     
/* 108 */     VideoFrame.Buffer scaledBuffer = buffer.cropAndScale(cropX, cropY, cropWidth, cropHeight, targetWidth, targetHeight);
/* 109 */     frame.release();
/*     */     
/* 111 */     VideoFrame.I420Buffer i420 = scaledBuffer.toI420();
/* 112 */     scaledBuffer.release();
/*     */     
/* 114 */     this.fileThreadHandler.post(() -> {
/*     */           YuvHelper.I420Rotate(i420.getDataY(), i420.getStrideY(), i420.getDataU(), i420.getStrideU(), i420.getDataV(), i420.getStrideV(), this.outputFrameBuffer, i420.getWidth(), i420.getHeight(), frame.getRotation());
/*     */ 
/*     */           
/*     */           i420.release();
/*     */           
/*     */           try {
/*     */             this.videoOutFile.write("FRAME\n".getBytes(Charset.forName("US-ASCII")));
/*     */             
/*     */             this.videoOutFile.write(this.outputFrameBuffer.array(), this.outputFrameBuffer.arrayOffset(), this.outputFrameSize);
/* 124 */           } catch (IOException e) {
/*     */             throw new RuntimeException("Error writing video to disk", e);
/*     */           } 
/*     */           this.frameCount++;
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/* 135 */     CountDownLatch cleanupBarrier = new CountDownLatch(1);
/* 136 */     this.renderThreadHandler.post(() -> {
/*     */           this.yuvConverter.release();
/*     */           this.eglBase.release();
/*     */           this.renderThread.quit();
/*     */           cleanupBarrier.countDown();
/*     */         });
/* 142 */     ThreadUtils.awaitUninterruptedly(cleanupBarrier);
/* 143 */     this.fileThreadHandler.post(() -> {
/*     */           try {
/*     */             this.videoOutFile.close();
/*     */ 
/*     */ 
/*     */             
/*     */             Logging.d("VideoFileRenderer", "Video written to disk as " + this.outputFileName + ". The number of frames is " + this.frameCount + " and the dimensions of the frames are " + this.outputFileWidth + "x" + this.outputFileHeight + ".");
/* 150 */           } catch (IOException e) {
/*     */             throw new RuntimeException("Error closing output file", e);
/*     */           } 
/*     */           this.fileThread.quit();
/*     */         });
/*     */     try {
/* 156 */       this.fileThread.join();
/* 157 */     } catch (InterruptedException e) {
/* 158 */       Thread.currentThread().interrupt();
/* 159 */       Logging.e("VideoFileRenderer", "Interrupted while waiting for the write to disk to complete.", e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoFileRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */