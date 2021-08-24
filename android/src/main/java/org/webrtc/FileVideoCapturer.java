/*     */ package org.webrtc;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.os.SystemClock;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class FileVideoCapturer
/*     */   implements VideoCapturer
/*     */ {
/*     */   private static final String TAG = "FileVideoCapturer";
/*     */   private final VideoReader videoReader;
/*     */   private CapturerObserver capturerObserver;
/*     */   
/*     */   private static class VideoReaderY4M
/*     */     implements VideoReader
/*     */   {
/*     */     private static final String TAG = "VideoReaderY4M";
/*     */     private static final String Y4M_FRAME_DELIMETER = "FRAME";
/*  37 */     private static final int FRAME_DELIMETER_LENGTH = "FRAME".length() + 1;
/*     */     
/*     */     private final int frameWidth;
/*     */     
/*     */     private final int frameHeight;
/*     */     private final long videoStart;
/*     */     private final RandomAccessFile mediaFile;
/*     */     private final FileChannel mediaFileChannel;
/*     */     
/*     */     public VideoReaderY4M(String file) throws IOException {
/*  47 */       this.mediaFile = new RandomAccessFile(file, "r");
/*  48 */       this.mediaFileChannel = this.mediaFile.getChannel();
/*  49 */       StringBuilder builder = new StringBuilder();
/*     */       while (true) {
/*  51 */         int c = this.mediaFile.read();
/*  52 */         if (c == -1)
/*     */         {
/*  54 */           throw new RuntimeException("Found end of file before end of header for file: " + file);
/*     */         }
/*  56 */         if (c == 10) {
/*     */           break;
/*     */         }
/*     */         
/*  60 */         builder.append((char)c);
/*     */       } 
/*  62 */       this.videoStart = this.mediaFileChannel.position();
/*  63 */       String header = builder.toString();
/*  64 */       String[] headerTokens = header.split("[ ]");
/*  65 */       int w = 0;
/*  66 */       int h = 0;
/*  67 */       String colorSpace = "";
/*  68 */       for (String tok : headerTokens) {
/*  69 */         char c = tok.charAt(0);
/*  70 */         switch (c) {
/*     */           case 'W':
/*  72 */             w = Integer.parseInt(tok.substring(1));
/*     */             break;
/*     */           case 'H':
/*  75 */             h = Integer.parseInt(tok.substring(1));
/*     */             break;
/*     */           case 'C':
/*  78 */             colorSpace = tok.substring(1);
/*     */             break;
/*     */         } 
/*     */       } 
/*  82 */       Logging.d("VideoReaderY4M", "Color space: " + colorSpace);
/*  83 */       if (!colorSpace.equals("420") && !colorSpace.equals("420mpeg2")) {
/*  84 */         throw new IllegalArgumentException("Does not support any other color space than I420 or I420mpeg2");
/*     */       }
/*     */       
/*  87 */       if (w % 2 == 1 || h % 2 == 1) {
/*  88 */         throw new IllegalArgumentException("Does not support odd width or height");
/*     */       }
/*  90 */       this.frameWidth = w;
/*  91 */       this.frameHeight = h;
/*  92 */       Logging.d("VideoReaderY4M", "frame dim: (" + w + ", " + h + ")");
/*     */     }
/*     */ 
/*     */     
/*     */     public VideoFrame getNextFrame() {
/*  97 */       long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
/*  98 */       JavaI420Buffer buffer = JavaI420Buffer.allocate(this.frameWidth, this.frameHeight);
/*  99 */       ByteBuffer dataY = buffer.getDataY();
/* 100 */       ByteBuffer dataU = buffer.getDataU();
/* 101 */       ByteBuffer dataV = buffer.getDataV();
/* 102 */       int chromaHeight = (this.frameHeight + 1) / 2;
/* 103 */       int sizeY = this.frameHeight * buffer.getStrideY();
/* 104 */       int sizeU = chromaHeight * buffer.getStrideU();
/* 105 */       int sizeV = chromaHeight * buffer.getStrideV();
/*     */       
/*     */       try {
/* 108 */         ByteBuffer frameDelim = ByteBuffer.allocate(FRAME_DELIMETER_LENGTH);
/* 109 */         if (this.mediaFileChannel.read(frameDelim) < FRAME_DELIMETER_LENGTH) {
/*     */           
/* 111 */           this.mediaFileChannel.position(this.videoStart);
/* 112 */           if (this.mediaFileChannel.read(frameDelim) < FRAME_DELIMETER_LENGTH) {
/* 113 */             throw new RuntimeException("Error looping video");
/*     */           }
/*     */         } 
/* 116 */         String frameDelimStr = new String(frameDelim.array(), Charset.forName("US-ASCII"));
/* 117 */         if (!frameDelimStr.equals("FRAME\n")) {
/* 118 */           throw new RuntimeException("Frames should be delimited by FRAME plus newline, found delimter was: '" + frameDelimStr + "'");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 123 */         this.mediaFileChannel.read(dataY);
/* 124 */         this.mediaFileChannel.read(dataU);
/* 125 */         this.mediaFileChannel.read(dataV);
/* 126 */       } catch (IOException e) {
/* 127 */         throw new RuntimeException(e);
/*     */       } 
/*     */       
/* 130 */       return new VideoFrame(buffer, 0, captureTimeNs);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() {
/*     */       try {
/* 137 */         this.mediaFile.close();
/* 138 */       } catch (IOException e) {
/* 139 */         Logging.e("VideoReaderY4M", "Problem closing file", e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 147 */   private final Timer timer = new Timer();
/*     */   
/* 149 */   private final TimerTask tickTask = new TimerTask()
/*     */     {
/*     */       public void run() {
/* 152 */         FileVideoCapturer.this.tick();
/*     */       }
/*     */     };
/*     */   
/*     */   public FileVideoCapturer(String inputFile) throws IOException {
/*     */     try {
/* 158 */       this.videoReader = new VideoReaderY4M(inputFile);
/* 159 */     } catch (IOException e) {
/* 160 */       Logging.d("FileVideoCapturer", "Could not open video file: " + inputFile);
/* 161 */       throw e;
/*     */     } 
/*     */   } private static interface VideoReader {
/*     */     VideoFrame getNextFrame(); void close(); }
/*     */   public void tick() {
/* 166 */     VideoFrame videoFrame = this.videoReader.getNextFrame();
/* 167 */     this.capturerObserver.onFrameCaptured(videoFrame);
/* 168 */     videoFrame.release();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
/* 174 */     this.capturerObserver = capturerObserver;
/*     */   }
/*     */ 
/*     */   
/*     */   public void startCapture(int width, int height, int framerate) {
/* 179 */     this.timer.schedule(this.tickTask, 0L, (1000 / framerate));
/*     */   }
/*     */ 
/*     */   
/*     */   public void stopCapture() throws InterruptedException {
/* 184 */     this.timer.cancel();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void changeCaptureFormat(int width, int height, int framerate) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispose() {
/* 194 */     this.videoReader.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isScreencast() {
/* 199 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/FileVideoCapturer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */