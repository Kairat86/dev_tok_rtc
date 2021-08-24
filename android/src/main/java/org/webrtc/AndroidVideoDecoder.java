/*     */ package org.webrtc;
/*     */ 
/*     */ import android.media.MediaCodec;
/*     */ import android.media.MediaFormat;
/*     */ import android.os.SystemClock;
/*     */ import android.view.Surface;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.BlockingDeque;
/*     */ import java.util.concurrent.LinkedBlockingDeque;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class AndroidVideoDecoder
/*     */   implements VideoDecoder, VideoSink
/*     */ {
/*     */   private static final String TAG = "AndroidVideoDecoder";
/*     */   private static final String MEDIA_FORMAT_KEY_STRIDE = "stride";
/*     */   private static final String MEDIA_FORMAT_KEY_SLICE_HEIGHT = "slice-height";
/*     */   private static final String MEDIA_FORMAT_KEY_CROP_LEFT = "crop-left";
/*     */   private static final String MEDIA_FORMAT_KEY_CROP_RIGHT = "crop-right";
/*     */   private static final String MEDIA_FORMAT_KEY_CROP_TOP = "crop-top";
/*     */   private static final String MEDIA_FORMAT_KEY_CROP_BOTTOM = "crop-bottom";
/*     */   private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
/*     */   private static final int DEQUEUE_INPUT_TIMEOUT_US = 500000;
/*     */   private static final int DEQUEUE_OUTPUT_BUFFER_TIMEOUT_US = 100000;
/*     */   private final MediaCodecWrapperFactory mediaCodecWrapperFactory;
/*     */   private final String codecName;
/*     */   private final VideoCodecMimeType codecType;
/*     */   private final BlockingDeque<FrameInfo> frameInfos;
/*     */   private int colorFormat;
/*     */   @Nullable
/*     */   private Thread outputThread;
/*     */   private ThreadUtils.ThreadChecker outputThreadChecker;
/*     */   private ThreadUtils.ThreadChecker decoderThreadChecker;
/*     */   private volatile boolean running;
/*     */   @Nullable
/*     */   private volatile Exception shutdownException;
/*     */   
/*     */   private static class FrameInfo
/*     */   {
/*     */     final long decodeStartTimeMs;
/*     */     final int rotation;
/*     */     
/*     */     FrameInfo(long decodeStartTimeMs, int rotation) {
/*  66 */       this.decodeStartTimeMs = decodeStartTimeMs;
/*  67 */       this.rotation = rotation;
/*     */     }
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
/*  91 */   private final Object dimensionLock = new Object();
/*     */   
/*     */   private int width;
/*     */   
/*     */   private int height;
/*     */   
/*     */   private int stride;
/*     */   
/*     */   private int sliceHeight;
/*     */   private boolean hasDecodedFirstFrame;
/*     */   private boolean keyFrameRequired;
/*     */   @Nullable
/*     */   private final EglBase.Context sharedContext;
/*     */   @Nullable
/*     */   private SurfaceTextureHelper surfaceTextureHelper;
/*     */   @Nullable
/*     */   private Surface surface;
/*     */   
/*     */   private static class DecodedTextureMetadata
/*     */   {
/*     */     final long presentationTimestampUs;
/*     */     final Integer decodeTimeMs;
/*     */     
/*     */     DecodedTextureMetadata(long presentationTimestampUs, Integer decodeTimeMs) {
/* 115 */       this.presentationTimestampUs = presentationTimestampUs;
/* 116 */       this.decodeTimeMs = decodeTimeMs;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 121 */   private final Object renderedTextureMetadataLock = new Object();
/*     */   
/*     */   @Nullable
/*     */   private DecodedTextureMetadata renderedTextureMetadata;
/*     */   
/*     */   @Nullable
/*     */   private VideoDecoder.Callback callback;
/*     */   
/*     */   @Nullable
/*     */   private MediaCodecWrapper codec;
/*     */   
/*     */   AndroidVideoDecoder(MediaCodecWrapperFactory mediaCodecWrapperFactory, String codecName, VideoCodecMimeType codecType, int colorFormat, @Nullable EglBase.Context sharedContext) {
/* 133 */     if (!isSupportedColorFormat(colorFormat)) {
/* 134 */       throw new IllegalArgumentException("Unsupported color format: " + colorFormat);
/*     */     }
/* 136 */     Logging.d("AndroidVideoDecoder", "ctor name: " + codecName + " type: " + codecType + " color format: " + colorFormat + " context: " + sharedContext);
/*     */ 
/*     */     
/* 139 */     this.mediaCodecWrapperFactory = mediaCodecWrapperFactory;
/* 140 */     this.codecName = codecName;
/* 141 */     this.codecType = codecType;
/* 142 */     this.colorFormat = colorFormat;
/* 143 */     this.sharedContext = sharedContext;
/* 144 */     this.frameInfos = new LinkedBlockingDeque<>();
/*     */   }
/*     */ 
/*     */   
/*     */   public VideoCodecStatus initDecode(VideoDecoder.Settings settings, VideoDecoder.Callback callback) {
/* 149 */     this.decoderThreadChecker = new ThreadUtils.ThreadChecker();
/*     */     
/* 151 */     this.callback = callback;
/* 152 */     if (this.sharedContext != null) {
/* 153 */       this.surfaceTextureHelper = createSurfaceTextureHelper();
/* 154 */       this.surface = new Surface(this.surfaceTextureHelper.getSurfaceTexture());
/* 155 */       this.surfaceTextureHelper.startListening(this);
/*     */     } 
/* 157 */     return initDecodeInternal(settings.width, settings.height);
/*     */   }
/*     */ 
/*     */   
/*     */   private VideoCodecStatus initDecodeInternal(int width, int height) {
/* 162 */     this.decoderThreadChecker.checkIsOnValidThread();
/* 163 */     Logging.d("AndroidVideoDecoder", "initDecodeInternal name: " + this.codecName + " type: " + this.codecType + " width: " + width + " height: " + height);
/*     */ 
/*     */     
/* 166 */     if (this.outputThread != null) {
/* 167 */       Logging.e("AndroidVideoDecoder", "initDecodeInternal called while the codec is already running");
/* 168 */       return VideoCodecStatus.FALLBACK_SOFTWARE;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 173 */     this.width = width;
/* 174 */     this.height = height;
/*     */     
/* 176 */     this.stride = width;
/* 177 */     this.sliceHeight = height;
/* 178 */     this.hasDecodedFirstFrame = false;
/* 179 */     this.keyFrameRequired = true;
/*     */     
/*     */     try {
/* 182 */       this.codec = this.mediaCodecWrapperFactory.createByCodecName(this.codecName);
/* 183 */     } catch (IOException|IllegalArgumentException|IllegalStateException e) {
/* 184 */       Logging.e("AndroidVideoDecoder", "Cannot create media decoder " + this.codecName);
/* 185 */       return VideoCodecStatus.FALLBACK_SOFTWARE;
/*     */     } 
/*     */     try {
/* 188 */       MediaFormat format = MediaFormat.createVideoFormat(this.codecType.mimeType(), width, height);
/* 189 */       if (this.sharedContext == null) {
/* 190 */         format.setInteger("color-format", this.colorFormat);
/*     */       }
/* 192 */       this.codec.configure(format, this.surface, null, 0);
/* 193 */       this.codec.start();
/* 194 */     } catch (IllegalStateException|IllegalArgumentException e) {
/* 195 */       Logging.e("AndroidVideoDecoder", "initDecode failed", e);
/* 196 */       release();
/* 197 */       return VideoCodecStatus.FALLBACK_SOFTWARE;
/*     */     } 
/* 199 */     this.running = true;
/* 200 */     this.outputThread = createOutputThread();
/* 201 */     this.outputThread.start();
/*     */     
/* 203 */     Logging.d("AndroidVideoDecoder", "initDecodeInternal done");
/* 204 */     return VideoCodecStatus.OK;
/*     */   }
/*     */   public VideoCodecStatus decode(EncodedImage frame, VideoDecoder.DecodeInfo info) {
/*     */     int width, height, index;
/*     */     ByteBuffer buffer;
/* 209 */     this.decoderThreadChecker.checkIsOnValidThread();
/* 210 */     if (this.codec == null || this.callback == null) {
/* 211 */       Logging.d("AndroidVideoDecoder", "decode uninitalized, codec: " + ((this.codec != null) ? 1 : 0) + ", callback: " + this.callback);
/* 212 */       return VideoCodecStatus.UNINITIALIZED;
/*     */     } 
/*     */     
/* 215 */     if (frame.buffer == null) {
/* 216 */       Logging.e("AndroidVideoDecoder", "decode() - no input data");
/* 217 */       return VideoCodecStatus.ERR_PARAMETER;
/*     */     } 
/*     */     
/* 220 */     int size = frame.buffer.remaining();
/* 221 */     if (size == 0) {
/* 222 */       Logging.e("AndroidVideoDecoder", "decode() - input buffer empty");
/* 223 */       return VideoCodecStatus.ERR_PARAMETER;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 229 */     synchronized (this.dimensionLock) {
/* 230 */       width = this.width;
/* 231 */       height = this.height;
/*     */     } 
/*     */ 
/*     */     
/* 235 */     if (frame.encodedWidth * frame.encodedHeight > 0 && (frame.encodedWidth != width || frame.encodedHeight != height)) {
/*     */       
/* 237 */       VideoCodecStatus status = reinitDecode(frame.encodedWidth, frame.encodedHeight);
/* 238 */       if (status != VideoCodecStatus.OK) {
/* 239 */         return status;
/*     */       }
/*     */     } 
/*     */     
/* 243 */     if (this.keyFrameRequired) {
/*     */       
/* 245 */       if (frame.frameType != EncodedImage.FrameType.VideoFrameKey) {
/* 246 */         Logging.e("AndroidVideoDecoder", "decode() - key frame required first");
/* 247 */         return VideoCodecStatus.NO_OUTPUT;
/*     */       } 
/* 249 */       if (!frame.completeFrame) {
/* 250 */         Logging.e("AndroidVideoDecoder", "decode() - complete frame required first");
/* 251 */         return VideoCodecStatus.NO_OUTPUT;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 257 */       index = this.codec.dequeueInputBuffer(500000L);
/* 258 */     } catch (IllegalStateException e) {
/* 259 */       Logging.e("AndroidVideoDecoder", "dequeueInputBuffer failed", e);
/* 260 */       return VideoCodecStatus.ERROR;
/*     */     } 
/* 262 */     if (index < 0) {
/*     */ 
/*     */       
/* 265 */       Logging.e("AndroidVideoDecoder", "decode() - no HW buffers available; decoder falling behind");
/* 266 */       return VideoCodecStatus.ERROR;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 271 */       buffer = this.codec.getInputBuffers()[index];
/* 272 */     } catch (IllegalStateException e) {
/* 273 */       Logging.e("AndroidVideoDecoder", "getInputBuffers failed", e);
/* 274 */       return VideoCodecStatus.ERROR;
/*     */     } 
/*     */     
/* 277 */     if (buffer.capacity() < size) {
/* 278 */       Logging.e("AndroidVideoDecoder", "decode() - HW buffer too small");
/* 279 */       return VideoCodecStatus.ERROR;
/*     */     } 
/* 281 */     buffer.put(frame.buffer);
/*     */     
/* 283 */     this.frameInfos.offer(new FrameInfo(SystemClock.elapsedRealtime(), frame.rotation));
/*     */     try {
/* 285 */       this.codec.queueInputBuffer(index, 0, size, TimeUnit.NANOSECONDS
/* 286 */           .toMicros(frame.captureTimeNs), 0);
/* 287 */     } catch (IllegalStateException e) {
/* 288 */       Logging.e("AndroidVideoDecoder", "queueInputBuffer failed", e);
/* 289 */       this.frameInfos.pollLast();
/* 290 */       return VideoCodecStatus.ERROR;
/*     */     } 
/* 292 */     if (this.keyFrameRequired) {
/* 293 */       this.keyFrameRequired = false;
/*     */     }
/* 295 */     return VideoCodecStatus.OK;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getPrefersLateDecoding() {
/* 300 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getImplementationName() {
/* 305 */     return this.codecName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VideoCodecStatus release() {
/* 313 */     Logging.d("AndroidVideoDecoder", "release");
/* 314 */     VideoCodecStatus status = releaseInternal();
/* 315 */     if (this.surface != null) {
/* 316 */       releaseSurface();
/* 317 */       this.surface = null;
/* 318 */       this.surfaceTextureHelper.stopListening();
/* 319 */       this.surfaceTextureHelper.dispose();
/* 320 */       this.surfaceTextureHelper = null;
/*     */     } 
/* 322 */     synchronized (this.renderedTextureMetadataLock) {
/* 323 */       this.renderedTextureMetadata = null;
/*     */     } 
/* 325 */     this.callback = null;
/* 326 */     this.frameInfos.clear();
/* 327 */     return status;
/*     */   }
/*     */ 
/*     */   
/*     */   private VideoCodecStatus releaseInternal() {
/* 332 */     if (!this.running) {
/* 333 */       Logging.d("AndroidVideoDecoder", "release: Decoder is not running.");
/* 334 */       return VideoCodecStatus.OK;
/*     */     } 
/*     */     
/*     */     try {
/* 338 */       this.running = false;
/* 339 */       if (!ThreadUtils.joinUninterruptibly(this.outputThread, 5000L)) {
/*     */         
/* 341 */         Logging.e("AndroidVideoDecoder", "Media decoder release timeout", new RuntimeException());
/* 342 */         return VideoCodecStatus.TIMEOUT;
/*     */       } 
/* 344 */       if (this.shutdownException != null) {
/*     */ 
/*     */         
/* 347 */         Logging.e("AndroidVideoDecoder", "Media decoder release error", new RuntimeException(this.shutdownException));
/* 348 */         this.shutdownException = null;
/* 349 */         return VideoCodecStatus.ERROR;
/*     */       } 
/*     */     } finally {
/* 352 */       this.codec = null;
/* 353 */       this.outputThread = null;
/*     */     } 
/* 355 */     return VideoCodecStatus.OK;
/*     */   }
/*     */   
/*     */   private VideoCodecStatus reinitDecode(int newWidth, int newHeight) {
/* 359 */     this.decoderThreadChecker.checkIsOnValidThread();
/* 360 */     VideoCodecStatus status = releaseInternal();
/* 361 */     if (status != VideoCodecStatus.OK) {
/* 362 */       return status;
/*     */     }
/* 364 */     return initDecodeInternal(newWidth, newHeight);
/*     */   }
/*     */   
/*     */   private Thread createOutputThread() {
/* 368 */     return new Thread("AndroidVideoDecoder.outputThread")
/*     */       {
/*     */         public void run() {
/* 371 */           AndroidVideoDecoder.this.outputThreadChecker = new ThreadUtils.ThreadChecker();
/* 372 */           while (AndroidVideoDecoder.this.running) {
/* 373 */             AndroidVideoDecoder.this.deliverDecodedFrame();
/*     */           }
/* 375 */           AndroidVideoDecoder.this.releaseCodecOnOutputThread();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deliverDecodedFrame() {
/* 382 */     this.outputThreadChecker.checkIsOnValidThread();
/*     */     try {
/* 384 */       MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 389 */       int result = this.codec.dequeueOutputBuffer(info, 100000L);
/* 390 */       if (result == -2) {
/* 391 */         reformat(this.codec.getOutputFormat());
/*     */         
/*     */         return;
/*     */       } 
/* 395 */       if (result < 0) {
/* 396 */         Logging.v("AndroidVideoDecoder", "dequeueOutputBuffer returned " + result);
/*     */         
/*     */         return;
/*     */       } 
/* 400 */       FrameInfo frameInfo = this.frameInfos.poll();
/* 401 */       Integer decodeTimeMs = null;
/* 402 */       int rotation = 0;
/* 403 */       if (frameInfo != null) {
/* 404 */         decodeTimeMs = Integer.valueOf((int)(SystemClock.elapsedRealtime() - frameInfo.decodeStartTimeMs));
/* 405 */         rotation = frameInfo.rotation;
/*     */       } 
/*     */       
/* 408 */       this.hasDecodedFirstFrame = true;
/*     */       
/* 410 */       if (this.surfaceTextureHelper != null) {
/* 411 */         deliverTextureFrame(result, info, rotation, decodeTimeMs);
/*     */       } else {
/* 413 */         deliverByteFrame(result, info, rotation, decodeTimeMs);
/*     */       }
/*     */     
/* 416 */     } catch (IllegalStateException e) {
/* 417 */       Logging.e("AndroidVideoDecoder", "deliverDecodedFrame failed", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void deliverTextureFrame(int index, MediaCodec.BufferInfo info, int rotation, Integer decodeTimeMs) {
/*     */     int width;
/*     */     int height;
/* 426 */     synchronized (this.dimensionLock) {
/* 427 */       width = this.width;
/* 428 */       height = this.height;
/*     */     } 
/*     */     
/* 431 */     synchronized (this.renderedTextureMetadataLock) {
/* 432 */       if (this.renderedTextureMetadata != null) {
/* 433 */         this.codec.releaseOutputBuffer(index, false);
/*     */         return;
/*     */       } 
/* 436 */       this.surfaceTextureHelper.setTextureSize(width, height);
/* 437 */       this.surfaceTextureHelper.setFrameRotation(rotation);
/* 438 */       this.renderedTextureMetadata = new DecodedTextureMetadata(info.presentationTimeUs, decodeTimeMs);
/* 439 */       this.codec.releaseOutputBuffer(index, true);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onFrame(VideoFrame frame) {
/*     */     Integer decodeTimeMs;
/*     */     long timestampNs;
/* 448 */     synchronized (this.renderedTextureMetadataLock) {
/* 449 */       if (this.renderedTextureMetadata == null) {
/* 450 */         throw new IllegalStateException("Rendered texture metadata was null in onTextureFrameAvailable.");
/*     */       }
/*     */       
/* 453 */       timestampNs = this.renderedTextureMetadata.presentationTimestampUs * 1000L;
/* 454 */       decodeTimeMs = this.renderedTextureMetadata.decodeTimeMs;
/* 455 */       this.renderedTextureMetadata = null;
/*     */     } 
/*     */ 
/*     */     
/* 459 */     VideoFrame frameWithModifiedTimeStamp = new VideoFrame(frame.getBuffer(), frame.getRotation(), timestampNs);
/* 460 */     this.callback.onDecodedFrame(frameWithModifiedTimeStamp, decodeTimeMs, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void deliverByteFrame(int result, MediaCodec.BufferInfo info, int rotation, Integer decodeTimeMs) {
/*     */     int width, height, stride, sliceHeight;
/*     */     VideoFrame.Buffer frameBuffer;
/* 470 */     synchronized (this.dimensionLock) {
/* 471 */       width = this.width;
/* 472 */       height = this.height;
/* 473 */       stride = this.stride;
/* 474 */       sliceHeight = this.sliceHeight;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 479 */     if (info.size < width * height * 3 / 2) {
/* 480 */       Logging.e("AndroidVideoDecoder", "Insufficient output buffer size: " + info.size);
/*     */       
/*     */       return;
/*     */     } 
/* 484 */     if (info.size < stride * height * 3 / 2 && sliceHeight == height && stride > width)
/*     */     {
/*     */ 
/*     */       
/* 488 */       stride = info.size * 2 / height * 3;
/*     */     }
/*     */     
/* 491 */     ByteBuffer buffer = this.codec.getOutputBuffers()[result];
/* 492 */     buffer.position(info.offset);
/* 493 */     buffer.limit(info.offset + info.size);
/* 494 */     buffer = buffer.slice();
/*     */ 
/*     */     
/* 497 */     if (this.colorFormat == 19) {
/* 498 */       frameBuffer = copyI420Buffer(buffer, stride, sliceHeight, width, height);
/*     */     } else {
/*     */       
/* 501 */       frameBuffer = copyNV12ToI420Buffer(buffer, stride, sliceHeight, width, height);
/*     */     } 
/* 503 */     this.codec.releaseOutputBuffer(result, false);
/*     */     
/* 505 */     long presentationTimeNs = info.presentationTimeUs * 1000L;
/* 506 */     VideoFrame frame = new VideoFrame(frameBuffer, rotation, presentationTimeNs);
/*     */ 
/*     */     
/* 509 */     this.callback.onDecodedFrame(frame, decodeTimeMs, null);
/* 510 */     frame.release();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private VideoFrame.Buffer copyNV12ToI420Buffer(ByteBuffer buffer, int stride, int sliceHeight, int width, int height) {
/* 516 */     return (new NV12Buffer(width, height, stride, sliceHeight, buffer, null))
/* 517 */       .toI420();
/*     */   }
/*     */ 
/*     */   
/*     */   private VideoFrame.Buffer copyI420Buffer(ByteBuffer buffer, int stride, int sliceHeight, int width, int height) {
/* 522 */     if (stride % 2 != 0) {
/* 523 */       throw new AssertionError("Stride is not divisible by two: " + stride);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 532 */     int chromaWidth = (width + 1) / 2;
/* 533 */     int chromaHeight = (sliceHeight % 2 == 0) ? ((height + 1) / 2) : (height / 2);
/*     */     
/* 535 */     int uvStride = stride / 2;
/*     */     
/* 537 */     int yPos = 0;
/* 538 */     int yEnd = 0 + stride * height;
/* 539 */     int uPos = 0 + stride * sliceHeight;
/* 540 */     int uEnd = uPos + uvStride * chromaHeight;
/* 541 */     int vPos = uPos + uvStride * sliceHeight / 2;
/* 542 */     int vEnd = vPos + uvStride * chromaHeight;
/*     */     
/* 544 */     VideoFrame.I420Buffer frameBuffer = allocateI420Buffer(width, height);
/*     */     
/* 546 */     buffer.limit(yEnd);
/* 547 */     buffer.position(0);
/* 548 */     copyPlane(buffer
/* 549 */         .slice(), stride, frameBuffer.getDataY(), frameBuffer.getStrideY(), width, height);
/*     */     
/* 551 */     buffer.limit(uEnd);
/* 552 */     buffer.position(uPos);
/* 553 */     copyPlane(buffer.slice(), uvStride, frameBuffer.getDataU(), frameBuffer.getStrideU(), chromaWidth, chromaHeight);
/*     */     
/* 555 */     if (sliceHeight % 2 == 1) {
/* 556 */       buffer.position(uPos + uvStride * (chromaHeight - 1));
/*     */       
/* 558 */       ByteBuffer dataU = frameBuffer.getDataU();
/* 559 */       dataU.position(frameBuffer.getStrideU() * chromaHeight);
/* 560 */       dataU.put(buffer);
/*     */     } 
/*     */     
/* 563 */     buffer.limit(vEnd);
/* 564 */     buffer.position(vPos);
/* 565 */     copyPlane(buffer.slice(), uvStride, frameBuffer.getDataV(), frameBuffer.getStrideV(), chromaWidth, chromaHeight);
/*     */     
/* 567 */     if (sliceHeight % 2 == 1) {
/* 568 */       buffer.position(vPos + uvStride * (chromaHeight - 1));
/*     */       
/* 570 */       ByteBuffer dataV = frameBuffer.getDataV();
/* 571 */       dataV.position(frameBuffer.getStrideV() * chromaHeight);
/* 572 */       dataV.put(buffer);
/*     */     } 
/*     */     
/* 575 */     return frameBuffer;
/*     */   }
/*     */   private void reformat(MediaFormat format) {
/*     */     int newWidth, newHeight;
/* 579 */     this.outputThreadChecker.checkIsOnValidThread();
/* 580 */     Logging.d("AndroidVideoDecoder", "Decoder format changed: " + format.toString());
/*     */ 
/*     */     
/* 583 */     if (format.containsKey("crop-left") && format
/* 584 */       .containsKey("crop-right") && format
/* 585 */       .containsKey("crop-bottom") && format
/* 586 */       .containsKey("crop-top")) {
/*     */       
/* 588 */       newWidth = 1 + format.getInteger("crop-right") - format.getInteger("crop-left");
/*     */       
/* 590 */       newHeight = 1 + format.getInteger("crop-bottom") - format.getInteger("crop-top");
/*     */     } else {
/* 592 */       newWidth = format.getInteger("width");
/* 593 */       newHeight = format.getInteger("height");
/*     */     } 
/*     */     
/* 596 */     synchronized (this.dimensionLock) {
/* 597 */       if (this.hasDecodedFirstFrame && (this.width != newWidth || this.height != newHeight)) {
/* 598 */         stopOnOutputThread(new RuntimeException("Unexpected size change. Configured " + this.width + "*" + this.height + ". New " + newWidth + "*" + newHeight));
/*     */         
/*     */         return;
/*     */       } 
/* 602 */       this.width = newWidth;
/* 603 */       this.height = newHeight;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 608 */     if (this.surfaceTextureHelper == null && format.containsKey("color-format")) {
/* 609 */       this.colorFormat = format.getInteger("color-format");
/* 610 */       Logging.d("AndroidVideoDecoder", "Color: 0x" + Integer.toHexString(this.colorFormat));
/* 611 */       if (!isSupportedColorFormat(this.colorFormat)) {
/* 612 */         stopOnOutputThread(new IllegalStateException("Unsupported color format: " + this.colorFormat));
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 618 */     synchronized (this.dimensionLock) {
/* 619 */       if (format.containsKey("stride")) {
/* 620 */         this.stride = format.getInteger("stride");
/*     */       }
/* 622 */       if (format.containsKey("slice-height")) {
/* 623 */         this.sliceHeight = format.getInteger("slice-height");
/*     */       }
/* 625 */       Logging.d("AndroidVideoDecoder", "Frame stride and slice height: " + this.stride + " x " + this.sliceHeight);
/* 626 */       this.stride = Math.max(this.width, this.stride);
/* 627 */       this.sliceHeight = Math.max(this.height, this.sliceHeight);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void releaseCodecOnOutputThread() {
/* 632 */     this.outputThreadChecker.checkIsOnValidThread();
/* 633 */     Logging.d("AndroidVideoDecoder", "Releasing MediaCodec on output thread");
/*     */     try {
/* 635 */       this.codec.stop();
/* 636 */     } catch (Exception e) {
/* 637 */       Logging.e("AndroidVideoDecoder", "Media decoder stop failed", e);
/*     */     } 
/*     */     try {
/* 640 */       this.codec.release();
/* 641 */     } catch (Exception e) {
/* 642 */       Logging.e("AndroidVideoDecoder", "Media decoder release failed", e);
/*     */       
/* 644 */       this.shutdownException = e;
/*     */     } 
/* 646 */     Logging.d("AndroidVideoDecoder", "Release on output thread done");
/*     */   }
/*     */   
/*     */   private void stopOnOutputThread(Exception e) {
/* 650 */     this.outputThreadChecker.checkIsOnValidThread();
/* 651 */     this.running = false;
/* 652 */     this.shutdownException = e;
/*     */   }
/*     */   
/*     */   private boolean isSupportedColorFormat(int colorFormat) {
/* 656 */     for (int supported : MediaCodecUtils.DECODER_COLOR_FORMATS) {
/* 657 */       if (supported == colorFormat) {
/* 658 */         return true;
/*     */       }
/*     */     } 
/* 661 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SurfaceTextureHelper createSurfaceTextureHelper() {
/* 666 */     return SurfaceTextureHelper.create("decoder-texture-thread", this.sharedContext);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void releaseSurface() {
/* 672 */     this.surface.release();
/*     */   }
/*     */ 
/*     */   
/*     */   protected VideoFrame.I420Buffer allocateI420Buffer(int width, int height) {
/* 677 */     return JavaI420Buffer.allocate(width, height);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void copyPlane(ByteBuffer src, int srcStride, ByteBuffer dst, int dstStride, int width, int height) {
/* 683 */     YuvHelper.copyPlane(src, srcStride, dst, dstStride, width, height);
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/AndroidVideoDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */