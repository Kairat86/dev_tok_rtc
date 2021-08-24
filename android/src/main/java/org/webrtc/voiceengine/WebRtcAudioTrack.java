/*     */ package org.webrtc.voiceengine;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.media.AudioAttributes;
/*     */ import android.media.AudioFormat;
/*     */ import android.media.AudioManager;
/*     */ import android.media.AudioTrack;
/*     */ import android.os.Build;
/*     */ import android.os.Process;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.webrtc.ContextUtils;
/*     */ import org.webrtc.Logging;
/*     */ import org.webrtc.ThreadUtils;
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
/*     */ public class WebRtcAudioTrack
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private static final String TAG = "WebRtcAudioTrack";
/*     */   private static final int BITS_PER_SAMPLE = 16;
/*     */   private static final int CALLBACK_BUFFER_SIZE_MS = 10;
/*     */   private static final int BUFFERS_PER_SECOND = 100;
/*     */   private static final long AUDIO_TRACK_THREAD_JOIN_TIMEOUT_MS = 2000L;
/*  49 */   private static final int DEFAULT_USAGE = getDefaultUsageAttribute();
/*  50 */   private static int usageAttribute = DEFAULT_USAGE;
/*     */   
/*     */   private final long nativeAudioTrack;
/*     */   
/*     */   private final AudioManager audioManager;
/*     */ 
/*     */   
/*     */   public static synchronized void setAudioTrackUsageAttribute(int usage) {
/*  58 */     Logging.w("WebRtcAudioTrack", "Default usage attribute is changed from: " + DEFAULT_USAGE + " to " + usage);
/*     */     
/*  60 */     usageAttribute = usage;
/*     */   }
/*     */   
/*     */   private static int getDefaultUsageAttribute() {
/*  64 */     if (Build.VERSION.SDK_INT >= 21) {
/*  65 */       return 2;
/*     */     }
/*     */     
/*  68 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  74 */   private final ThreadUtils.ThreadChecker threadChecker = new ThreadUtils.ThreadChecker();
/*     */   private ByteBuffer byteBuffer;
/*     */   @Nullable
/*     */   private AudioTrack audioTrack;
/*     */   @Nullable
/*     */   private AudioTrackThread audioThread;
/*     */   private static volatile boolean speakerMute;
/*     */   private byte[] emptyBytes;
/*     */   @Nullable
/*     */   private static WebRtcAudioTrackErrorCallback errorCallbackOld;
/*     */   @Nullable
/*     */   private static ErrorCallback errorCallback;
/*     */   
/*     */   public enum AudioTrackStartErrorCode {
/*  88 */     AUDIO_TRACK_START_EXCEPTION,
/*  89 */     AUDIO_TRACK_START_STATE_MISMATCH;
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
/*     */   @Deprecated
/*     */   public static void setErrorCallback(WebRtcAudioTrackErrorCallback errorCallback) {
/* 111 */     Logging.d("WebRtcAudioTrack", "Set error callback (deprecated");
/* 112 */     errorCallbackOld = errorCallback;
/*     */   } @Deprecated
/*     */   public static interface WebRtcAudioTrackErrorCallback {
/*     */     void onWebRtcAudioTrackInitError(String param1String); void onWebRtcAudioTrackStartError(String param1String); void onWebRtcAudioTrackError(String param1String); } public static void setErrorCallback(ErrorCallback errorCallback) {
/* 116 */     Logging.d("WebRtcAudioTrack", "Set extended error callback");
/* 117 */     WebRtcAudioTrack.errorCallback = errorCallback;
/*     */   }
/*     */   
/*     */   public static interface ErrorCallback {
/*     */     void onWebRtcAudioTrackInitError(String param1String);
/*     */     
/*     */     void onWebRtcAudioTrackStartError(WebRtcAudioTrack.AudioTrackStartErrorCode param1AudioTrackStartErrorCode, String param1String);
/*     */     
/*     */     void onWebRtcAudioTrackError(String param1String);
/*     */   }
/*     */   
/*     */   private class AudioTrackThread extends Thread {
/*     */     public AudioTrackThread(String name) {
/* 130 */       super(name);
/*     */     }
/*     */     private volatile boolean keepAlive = true;
/*     */     
/*     */     public void run() {
/* 135 */       Process.setThreadPriority(-19);
/* 136 */       Logging.d("WebRtcAudioTrack", "AudioTrackThread" + WebRtcAudioUtils.getThreadInfo());
/* 137 */       WebRtcAudioTrack.assertTrue((WebRtcAudioTrack.this.audioTrack.getPlayState() == 3));
/*     */ 
/*     */ 
/*     */       
/* 141 */       int sizeInBytes = WebRtcAudioTrack.this.byteBuffer.capacity();
/*     */       
/* 143 */       while (this.keepAlive) {
/*     */ 
/*     */ 
/*     */         
/* 147 */         WebRtcAudioTrack.this.nativeGetPlayoutData(sizeInBytes, WebRtcAudioTrack.this.nativeAudioTrack);
/*     */ 
/*     */ 
/*     */         
/* 151 */         WebRtcAudioTrack.assertTrue((sizeInBytes <= WebRtcAudioTrack.this.byteBuffer.remaining()));
/* 152 */         if (WebRtcAudioTrack.speakerMute) {
/* 153 */           WebRtcAudioTrack.this.byteBuffer.clear();
/* 154 */           WebRtcAudioTrack.this.byteBuffer.put(WebRtcAudioTrack.this.emptyBytes);
/* 155 */           WebRtcAudioTrack.this.byteBuffer.position(0);
/*     */         } 
/* 157 */         int bytesWritten = writeBytes(WebRtcAudioTrack.this.audioTrack, WebRtcAudioTrack.this.byteBuffer, sizeInBytes);
/* 158 */         if (bytesWritten != sizeInBytes) {
/* 159 */           Logging.e("WebRtcAudioTrack", "AudioTrack.write played invalid number of bytes: " + bytesWritten);
/*     */ 
/*     */           
/* 162 */           if (bytesWritten < 0) {
/* 163 */             this.keepAlive = false;
/* 164 */             WebRtcAudioTrack.this.reportWebRtcAudioTrackError("AudioTrack.write failed: " + bytesWritten);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 170 */         WebRtcAudioTrack.this.byteBuffer.rewind();
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 180 */       if (WebRtcAudioTrack.this.audioTrack != null) {
/* 181 */         Logging.d("WebRtcAudioTrack", "Calling AudioTrack.stop...");
/*     */         try {
/* 183 */           WebRtcAudioTrack.this.audioTrack.stop();
/* 184 */           Logging.d("WebRtcAudioTrack", "AudioTrack.stop is done.");
/* 185 */         } catch (IllegalStateException e) {
/* 186 */           Logging.e("WebRtcAudioTrack", "AudioTrack.stop failed: " + e.getMessage());
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     private int writeBytes(AudioTrack audioTrack, ByteBuffer byteBuffer, int sizeInBytes) {
/* 192 */       if (Build.VERSION.SDK_INT >= 21) {
/* 193 */         return audioTrack.write(byteBuffer, sizeInBytes, 0);
/*     */       }
/* 195 */       return audioTrack.write(byteBuffer.array(), byteBuffer.arrayOffset(), sizeInBytes);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void stopThread() {
/* 202 */       Logging.d("WebRtcAudioTrack", "stopThread");
/* 203 */       this.keepAlive = false;
/*     */     }
/*     */   }
/*     */   
/*     */   WebRtcAudioTrack(long nativeAudioTrack) {
/* 208 */     this.threadChecker.checkIsOnValidThread();
/* 209 */     Logging.d("WebRtcAudioTrack", "ctor" + WebRtcAudioUtils.getThreadInfo());
/* 210 */     this.nativeAudioTrack = nativeAudioTrack;
/* 211 */     this
/* 212 */       .audioManager = (AudioManager)ContextUtils.getApplicationContext().getSystemService("audio");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int initPlayout(int sampleRate, int channels, double bufferSizeFactor) {
/* 219 */     this.threadChecker.checkIsOnValidThread();
/* 220 */     Logging.d("WebRtcAudioTrack", "initPlayout(sampleRate=" + sampleRate + ", channels=" + channels + ", bufferSizeFactor=" + bufferSizeFactor + ")");
/*     */ 
/*     */     
/* 223 */     int bytesPerFrame = channels * 2;
/* 224 */     this.byteBuffer = ByteBuffer.allocateDirect(bytesPerFrame * sampleRate / 100);
/* 225 */     Logging.d("WebRtcAudioTrack", "byteBuffer.capacity: " + this.byteBuffer.capacity());
/* 226 */     this.emptyBytes = new byte[this.byteBuffer.capacity()];
/*     */ 
/*     */ 
/*     */     
/* 230 */     nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioTrack);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 235 */     int channelConfig = channelCountToConfiguration(channels);
/* 236 */     int minBufferSizeInBytes = (int)(AudioTrack.getMinBufferSize(sampleRate, channelConfig, 2) * bufferSizeFactor);
/*     */ 
/*     */     
/* 239 */     Logging.d("WebRtcAudioTrack", "minBufferSizeInBytes: " + minBufferSizeInBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 245 */     if (minBufferSizeInBytes < this.byteBuffer.capacity()) {
/* 246 */       reportWebRtcAudioTrackInitError("AudioTrack.getMinBufferSize returns an invalid value.");
/* 247 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 252 */     if (this.audioTrack != null) {
/* 253 */       reportWebRtcAudioTrackInitError("Conflict with existing AudioTrack.");
/* 254 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 260 */       if (Build.VERSION.SDK_INT >= 21) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 266 */         this.audioTrack = createAudioTrackOnLollipopOrHigher(sampleRate, channelConfig, minBufferSizeInBytes);
/*     */       }
/*     */       else {
/*     */         
/* 270 */         this
/* 271 */           .audioTrack = createAudioTrackOnLowerThanLollipop(sampleRate, channelConfig, minBufferSizeInBytes);
/*     */       } 
/* 273 */     } catch (IllegalArgumentException e) {
/* 274 */       reportWebRtcAudioTrackInitError(e.getMessage());
/* 275 */       releaseAudioResources();
/* 276 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 282 */     if (this.audioTrack == null || this.audioTrack.getState() != 1) {
/* 283 */       reportWebRtcAudioTrackInitError("Initialization of audio track failed.");
/* 284 */       releaseAudioResources();
/* 285 */       return -1;
/*     */     } 
/* 287 */     logMainParameters();
/* 288 */     logMainParametersExtended();
/* 289 */     return minBufferSizeInBytes;
/*     */   }
/*     */   
/*     */   private boolean startPlayout() {
/* 293 */     this.threadChecker.checkIsOnValidThread();
/* 294 */     Logging.d("WebRtcAudioTrack", "startPlayout");
/* 295 */     assertTrue((this.audioTrack != null));
/* 296 */     assertTrue((this.audioThread == null));
/*     */ 
/*     */     
/*     */     try {
/* 300 */       this.audioTrack.play();
/* 301 */     } catch (IllegalStateException e) {
/* 302 */       reportWebRtcAudioTrackStartError(AudioTrackStartErrorCode.AUDIO_TRACK_START_EXCEPTION, "AudioTrack.play failed: " + e
/* 303 */           .getMessage());
/* 304 */       releaseAudioResources();
/* 305 */       return false;
/*     */     } 
/* 307 */     if (this.audioTrack.getPlayState() != 3) {
/* 308 */       reportWebRtcAudioTrackStartError(AudioTrackStartErrorCode.AUDIO_TRACK_START_STATE_MISMATCH, "AudioTrack.play failed - incorrect state :" + this.audioTrack
/*     */ 
/*     */           
/* 311 */           .getPlayState());
/* 312 */       releaseAudioResources();
/* 313 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 319 */     this.audioThread = new AudioTrackThread("AudioTrackJavaThread");
/* 320 */     this.audioThread.start();
/* 321 */     return true;
/*     */   }
/*     */   
/*     */   private boolean stopPlayout() {
/* 325 */     this.threadChecker.checkIsOnValidThread();
/* 326 */     Logging.d("WebRtcAudioTrack", "stopPlayout");
/* 327 */     assertTrue((this.audioThread != null));
/* 328 */     logUnderrunCount();
/* 329 */     this.audioThread.stopThread();
/*     */     
/* 331 */     Logging.d("WebRtcAudioTrack", "Stopping the AudioTrackThread...");
/* 332 */     this.audioThread.interrupt();
/* 333 */     if (!ThreadUtils.joinUninterruptibly(this.audioThread, 2000L)) {
/* 334 */       Logging.e("WebRtcAudioTrack", "Join of AudioTrackThread timed out.");
/* 335 */       WebRtcAudioUtils.logAudioState("WebRtcAudioTrack");
/*     */     } 
/* 337 */     Logging.d("WebRtcAudioTrack", "AudioTrackThread has now been stopped.");
/* 338 */     this.audioThread = null;
/* 339 */     releaseAudioResources();
/* 340 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private int getStreamMaxVolume() {
/* 345 */     this.threadChecker.checkIsOnValidThread();
/* 346 */     Logging.d("WebRtcAudioTrack", "getStreamMaxVolume");
/* 347 */     assertTrue((this.audioManager != null));
/* 348 */     return this.audioManager.getStreamMaxVolume(0);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean setStreamVolume(int volume) {
/* 353 */     this.threadChecker.checkIsOnValidThread();
/* 354 */     Logging.d("WebRtcAudioTrack", "setStreamVolume(" + volume + ")");
/* 355 */     assertTrue((this.audioManager != null));
/* 356 */     if (isVolumeFixed()) {
/* 357 */       Logging.e("WebRtcAudioTrack", "The device implements a fixed volume policy.");
/* 358 */       return false;
/*     */     } 
/* 360 */     this.audioManager.setStreamVolume(0, volume, 0);
/* 361 */     return true;
/*     */   }
/*     */   
/*     */   private boolean isVolumeFixed() {
/* 365 */     if (Build.VERSION.SDK_INT < 21)
/* 366 */       return false; 
/* 367 */     return this.audioManager.isVolumeFixed();
/*     */   }
/*     */ 
/*     */   
/*     */   private int getStreamVolume() {
/* 372 */     this.threadChecker.checkIsOnValidThread();
/* 373 */     Logging.d("WebRtcAudioTrack", "getStreamVolume");
/* 374 */     assertTrue((this.audioManager != null));
/* 375 */     return this.audioManager.getStreamVolume(0);
/*     */   }
/*     */   
/*     */   private void logMainParameters() {
/* 379 */     Logging.d("WebRtcAudioTrack", "AudioTrack: session ID: " + this.audioTrack
/* 380 */         .getAudioSessionId() + ", channels: " + this.audioTrack
/* 381 */         .getChannelCount() + ", sample rate: " + this.audioTrack
/* 382 */         .getSampleRate() + ", max gain: " + 
/*     */         
/* 384 */         AudioTrack.getMaxVolume());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @TargetApi(21)
/*     */   private static AudioTrack createAudioTrackOnLollipopOrHigher(int sampleRateInHz, int channelConfig, int bufferSizeInBytes) {
/* 393 */     Logging.d("WebRtcAudioTrack", "createAudioTrackOnLollipopOrHigher");
/*     */ 
/*     */ 
/*     */     
/* 397 */     int nativeOutputSampleRate = AudioTrack.getNativeOutputSampleRate(0);
/* 398 */     Logging.d("WebRtcAudioTrack", "nativeOutputSampleRate: " + nativeOutputSampleRate);
/* 399 */     if (sampleRateInHz != nativeOutputSampleRate) {
/* 400 */       Logging.w("WebRtcAudioTrack", "Unable to use fast mode since requested sample rate is not native");
/*     */     }
/* 402 */     if (usageAttribute != DEFAULT_USAGE) {
/* 403 */       Logging.w("WebRtcAudioTrack", "A non default usage attribute is used: " + usageAttribute);
/*     */     }
/*     */     
/* 406 */     return new AudioTrack((new AudioAttributes.Builder())
/*     */         
/* 408 */         .setUsage(usageAttribute)
/* 409 */         .setContentType(1)
/* 410 */         .build(), (new AudioFormat.Builder())
/*     */         
/* 412 */         .setEncoding(2)
/* 413 */         .setSampleRate(sampleRateInHz)
/* 414 */         .setChannelMask(channelConfig)
/* 415 */         .build(), bufferSizeInBytes, 1, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static AudioTrack createAudioTrackOnLowerThanLollipop(int sampleRateInHz, int channelConfig, int bufferSizeInBytes) {
/* 424 */     return new AudioTrack(0, sampleRateInHz, channelConfig, 2, bufferSizeInBytes, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   private void logBufferSizeInFrames() {
/* 429 */     if (Build.VERSION.SDK_INT >= 23) {
/* 430 */       Logging.d("WebRtcAudioTrack", "AudioTrack: buffer size in frames: " + this.audioTrack
/*     */           
/* 432 */           .getBufferSizeInFrames());
/*     */     }
/*     */   }
/*     */   
/*     */   private int getBufferSizeInFrames() {
/* 437 */     if (Build.VERSION.SDK_INT >= 23) {
/* 438 */       return this.audioTrack.getBufferSizeInFrames();
/*     */     }
/* 440 */     return -1;
/*     */   }
/*     */   
/*     */   private void logBufferCapacityInFrames() {
/* 444 */     if (Build.VERSION.SDK_INT >= 24) {
/* 445 */       Logging.d("WebRtcAudioTrack", "AudioTrack: buffer capacity in frames: " + this.audioTrack
/*     */ 
/*     */           
/* 448 */           .getBufferCapacityInFrames());
/*     */     }
/*     */   }
/*     */   
/*     */   private void logMainParametersExtended() {
/* 453 */     logBufferSizeInFrames();
/* 454 */     logBufferCapacityInFrames();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void logUnderrunCount() {
/* 464 */     if (Build.VERSION.SDK_INT >= 24) {
/* 465 */       Logging.d("WebRtcAudioTrack", "underrun count: " + this.audioTrack.getUnderrunCount());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void assertTrue(boolean condition) {
/* 471 */     if (!condition) {
/* 472 */       throw new AssertionError("Expected condition to be true");
/*     */     }
/*     */   }
/*     */   
/*     */   private int channelCountToConfiguration(int channels) {
/* 477 */     return (channels == 1) ? 4 : 12;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setSpeakerMute(boolean mute) {
/* 487 */     Logging.w("WebRtcAudioTrack", "setSpeakerMute(" + mute + ")");
/* 488 */     speakerMute = mute;
/*     */   }
/*     */ 
/*     */   
/*     */   private void releaseAudioResources() {
/* 493 */     Logging.d("WebRtcAudioTrack", "releaseAudioResources");
/* 494 */     if (this.audioTrack != null) {
/* 495 */       this.audioTrack.release();
/* 496 */       this.audioTrack = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void reportWebRtcAudioTrackInitError(String errorMessage) {
/* 501 */     Logging.e("WebRtcAudioTrack", "Init playout error: " + errorMessage);
/* 502 */     WebRtcAudioUtils.logAudioState("WebRtcAudioTrack");
/* 503 */     if (errorCallbackOld != null) {
/* 504 */       errorCallbackOld.onWebRtcAudioTrackInitError(errorMessage);
/*     */     }
/* 506 */     if (errorCallback != null) {
/* 507 */       errorCallback.onWebRtcAudioTrackInitError(errorMessage);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void reportWebRtcAudioTrackStartError(AudioTrackStartErrorCode errorCode, String errorMessage) {
/* 513 */     Logging.e("WebRtcAudioTrack", "Start playout error: " + errorCode + ". " + errorMessage);
/* 514 */     WebRtcAudioUtils.logAudioState("WebRtcAudioTrack");
/* 515 */     if (errorCallbackOld != null) {
/* 516 */       errorCallbackOld.onWebRtcAudioTrackStartError(errorMessage);
/*     */     }
/* 518 */     if (errorCallback != null) {
/* 519 */       errorCallback.onWebRtcAudioTrackStartError(errorCode, errorMessage);
/*     */     }
/*     */   }
/*     */   
/*     */   private void reportWebRtcAudioTrackError(String errorMessage) {
/* 524 */     Logging.e("WebRtcAudioTrack", "Run-time playback error: " + errorMessage);
/* 525 */     WebRtcAudioUtils.logAudioState("WebRtcAudioTrack");
/* 526 */     if (errorCallbackOld != null) {
/* 527 */       errorCallbackOld.onWebRtcAudioTrackError(errorMessage);
/*     */     }
/* 529 */     if (errorCallback != null)
/* 530 */       errorCallback.onWebRtcAudioTrackError(errorMessage); 
/*     */   }
/*     */   
/*     */   private native void nativeCacheDirectBufferAddress(ByteBuffer paramByteBuffer, long paramLong);
/*     */   
/*     */   private native void nativeGetPlayoutData(int paramInt, long paramLong);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/voiceengine/WebRtcAudioTrack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */