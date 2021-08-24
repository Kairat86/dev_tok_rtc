/*     */ package org.webrtc.audio;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.content.Context;
/*     */ import android.media.AudioAttributes;
/*     */ import android.media.AudioFormat;
/*     */ import android.media.AudioManager;
/*     */ import android.media.AudioTrack;
/*     */ import android.os.Build;
/*     */ import android.os.Process;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.webrtc.CalledByNative;
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
/*     */ 
/*     */ class WebRtcAudioTrack
/*     */ {
/*     */   private static final String TAG = "WebRtcAudioTrackExternal";
/*     */   private static final int BITS_PER_SAMPLE = 16;
/*     */   private static final int CALLBACK_BUFFER_SIZE_MS = 10;
/*     */   private static final int BUFFERS_PER_SECOND = 100;
/*     */   private static final long AUDIO_TRACK_THREAD_JOIN_TIMEOUT_MS = 2000L;
/*  50 */   private static final int DEFAULT_USAGE = getDefaultUsageAttribute();
/*     */   
/*     */   private static int getDefaultUsageAttribute() {
/*  53 */     if (Build.VERSION.SDK_INT >= 21) {
/*  54 */       return 2;
/*     */     }
/*     */     
/*  57 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final int AUDIO_TRACK_START = 0;
/*     */   
/*     */   private static final int AUDIO_TRACK_STOP = 1;
/*     */   
/*     */   private long nativeAudioTrack;
/*     */   
/*     */   private final Context context;
/*     */   
/*     */   private final AudioManager audioManager;
/*  70 */   private final ThreadUtils.ThreadChecker threadChecker = new ThreadUtils.ThreadChecker();
/*     */   
/*     */   private ByteBuffer byteBuffer;
/*     */   
/*     */   @Nullable
/*     */   private AudioTrack audioTrack;
/*     */   
/*     */   @Nullable
/*     */   private AudioTrackThread audioThread;
/*     */   
/*     */   private final VolumeLogger volumeLogger;
/*     */   
/*     */   private volatile boolean speakerMute;
/*     */   
/*     */   private byte[] emptyBytes;
/*     */   @Nullable
/*     */   private final JavaAudioDeviceModule.AudioTrackErrorCallback errorCallback;
/*     */   @Nullable
/*     */   private final JavaAudioDeviceModule.AudioTrackStateCallback stateCallback;
/*     */   
/*     */   private class AudioTrackThread
/*     */     extends Thread
/*     */   {
/*     */     private volatile boolean keepAlive = true;
/*     */     
/*     */     public AudioTrackThread(String name) {
/*  96 */       super(name);
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 101 */       Process.setThreadPriority(-19);
/* 102 */       Logging.d("WebRtcAudioTrackExternal", "AudioTrackThread" + WebRtcAudioUtils.getThreadInfo());
/* 103 */       WebRtcAudioTrack.assertTrue((WebRtcAudioTrack.this.audioTrack.getPlayState() == 3));
/*     */ 
/*     */       
/* 106 */       WebRtcAudioTrack.this.doAudioTrackStateCallback(0);
/*     */ 
/*     */ 
/*     */       
/* 110 */       int sizeInBytes = WebRtcAudioTrack.this.byteBuffer.capacity();
/*     */       
/* 112 */       while (this.keepAlive) {
/*     */ 
/*     */ 
/*     */         
/* 116 */         WebRtcAudioTrack.nativeGetPlayoutData(WebRtcAudioTrack.this.nativeAudioTrack, sizeInBytes);
/*     */ 
/*     */ 
/*     */         
/* 120 */         WebRtcAudioTrack.assertTrue((sizeInBytes <= WebRtcAudioTrack.this.byteBuffer.remaining()));
/* 121 */         if (WebRtcAudioTrack.this.speakerMute) {
/* 122 */           WebRtcAudioTrack.this.byteBuffer.clear();
/* 123 */           WebRtcAudioTrack.this.byteBuffer.put(WebRtcAudioTrack.this.emptyBytes);
/* 124 */           WebRtcAudioTrack.this.byteBuffer.position(0);
/*     */         } 
/* 126 */         int bytesWritten = writeBytes(WebRtcAudioTrack.this.audioTrack, WebRtcAudioTrack.this.byteBuffer, sizeInBytes);
/* 127 */         if (bytesWritten != sizeInBytes) {
/* 128 */           Logging.e("WebRtcAudioTrackExternal", "AudioTrack.write played invalid number of bytes: " + bytesWritten);
/*     */ 
/*     */           
/* 131 */           if (bytesWritten < 0) {
/* 132 */             this.keepAlive = false;
/* 133 */             WebRtcAudioTrack.this.reportWebRtcAudioTrackError("AudioTrack.write failed: " + bytesWritten);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 139 */         WebRtcAudioTrack.this.byteBuffer.rewind();
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int writeBytes(AudioTrack audioTrack, ByteBuffer byteBuffer, int sizeInBytes) {
/* 148 */       if (Build.VERSION.SDK_INT >= 21) {
/* 149 */         return audioTrack.write(byteBuffer, sizeInBytes, 0);
/*     */       }
/* 151 */       return audioTrack.write(byteBuffer.array(), byteBuffer.arrayOffset(), sizeInBytes);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void stopThread() {
/* 158 */       Logging.d("WebRtcAudioTrackExternal", "stopThread");
/* 159 */       this.keepAlive = false;
/*     */     }
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   WebRtcAudioTrack(Context context, AudioManager audioManager) {
/* 165 */     this(context, audioManager, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   WebRtcAudioTrack(Context context, AudioManager audioManager, @Nullable JavaAudioDeviceModule.AudioTrackErrorCallback errorCallback, @Nullable JavaAudioDeviceModule.AudioTrackStateCallback stateCallback) {
/* 171 */     this.threadChecker.detachThread();
/* 172 */     this.context = context;
/* 173 */     this.audioManager = audioManager;
/* 174 */     this.errorCallback = errorCallback;
/* 175 */     this.stateCallback = stateCallback;
/* 176 */     this.volumeLogger = new VolumeLogger(audioManager);
/* 177 */     Logging.d("WebRtcAudioTrackExternal", "ctor" + WebRtcAudioUtils.getThreadInfo());
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   public void setNativeAudioTrack(long nativeAudioTrack) {
/* 182 */     this.nativeAudioTrack = nativeAudioTrack;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private int initPlayout(int sampleRate, int channels, double bufferSizeFactor) {
/* 187 */     this.threadChecker.checkIsOnValidThread();
/* 188 */     Logging.d("WebRtcAudioTrackExternal", "initPlayout(sampleRate=" + sampleRate + ", channels=" + channels + ", bufferSizeFactor=" + bufferSizeFactor + ")");
/*     */ 
/*     */     
/* 191 */     int bytesPerFrame = channels * 2;
/* 192 */     this.byteBuffer = ByteBuffer.allocateDirect(bytesPerFrame * sampleRate / 100);
/* 193 */     Logging.d("WebRtcAudioTrackExternal", "byteBuffer.capacity: " + this.byteBuffer.capacity());
/* 194 */     this.emptyBytes = new byte[this.byteBuffer.capacity()];
/*     */ 
/*     */ 
/*     */     
/* 198 */     nativeCacheDirectBufferAddress(this.nativeAudioTrack, this.byteBuffer);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 203 */     int channelConfig = channelCountToConfiguration(channels);
/* 204 */     int minBufferSizeInBytes = (int)(AudioTrack.getMinBufferSize(sampleRate, channelConfig, 2) * bufferSizeFactor);
/*     */ 
/*     */     
/* 207 */     Logging.d("WebRtcAudioTrackExternal", "minBufferSizeInBytes: " + minBufferSizeInBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 213 */     if (minBufferSizeInBytes < this.byteBuffer.capacity()) {
/* 214 */       reportWebRtcAudioTrackInitError("AudioTrack.getMinBufferSize returns an invalid value.");
/* 215 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 220 */     if (this.audioTrack != null) {
/* 221 */       reportWebRtcAudioTrackInitError("Conflict with existing AudioTrack.");
/* 222 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 228 */       if (Build.VERSION.SDK_INT >= 21) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 234 */         this
/* 235 */           .audioTrack = createAudioTrackOnLollipopOrHigher(sampleRate, channelConfig, minBufferSizeInBytes);
/*     */       } else {
/*     */         
/* 238 */         this
/* 239 */           .audioTrack = createAudioTrackOnLowerThanLollipop(sampleRate, channelConfig, minBufferSizeInBytes);
/*     */       } 
/* 241 */     } catch (IllegalArgumentException e) {
/* 242 */       reportWebRtcAudioTrackInitError(e.getMessage());
/* 243 */       releaseAudioResources();
/* 244 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 250 */     if (this.audioTrack == null || this.audioTrack.getState() != 1) {
/* 251 */       reportWebRtcAudioTrackInitError("Initialization of audio track failed.");
/* 252 */       releaseAudioResources();
/* 253 */       return -1;
/*     */     } 
/* 255 */     logMainParameters();
/* 256 */     logMainParametersExtended();
/* 257 */     return minBufferSizeInBytes;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private boolean startPlayout() {
/* 262 */     this.threadChecker.checkIsOnValidThread();
/* 263 */     this.volumeLogger.start();
/* 264 */     Logging.d("WebRtcAudioTrackExternal", "startPlayout");
/* 265 */     assertTrue((this.audioTrack != null));
/* 266 */     assertTrue((this.audioThread == null));
/*     */ 
/*     */     
/*     */     try {
/* 270 */       this.audioTrack.play();
/* 271 */     } catch (IllegalStateException e) {
/* 272 */       reportWebRtcAudioTrackStartError(JavaAudioDeviceModule.AudioTrackStartErrorCode.AUDIO_TRACK_START_EXCEPTION, "AudioTrack.play failed: " + e
/* 273 */           .getMessage());
/* 274 */       releaseAudioResources();
/* 275 */       return false;
/*     */     } 
/* 277 */     if (this.audioTrack.getPlayState() != 3) {
/* 278 */       reportWebRtcAudioTrackStartError(JavaAudioDeviceModule.AudioTrackStartErrorCode.AUDIO_TRACK_START_STATE_MISMATCH, "AudioTrack.play failed - incorrect state :" + this.audioTrack
/* 279 */           .getPlayState());
/* 280 */       releaseAudioResources();
/* 281 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 287 */     this.audioThread = new AudioTrackThread("AudioTrackJavaThread");
/* 288 */     this.audioThread.start();
/* 289 */     return true;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private boolean stopPlayout() {
/* 294 */     this.threadChecker.checkIsOnValidThread();
/* 295 */     this.volumeLogger.stop();
/* 296 */     Logging.d("WebRtcAudioTrackExternal", "stopPlayout");
/* 297 */     assertTrue((this.audioThread != null));
/* 298 */     logUnderrunCount();
/* 299 */     this.audioThread.stopThread();
/*     */     
/* 301 */     Logging.d("WebRtcAudioTrackExternal", "Stopping the AudioTrackThread...");
/* 302 */     this.audioThread.interrupt();
/* 303 */     if (!ThreadUtils.joinUninterruptibly(this.audioThread, 2000L)) {
/* 304 */       Logging.e("WebRtcAudioTrackExternal", "Join of AudioTrackThread timed out.");
/* 305 */       WebRtcAudioUtils.logAudioState("WebRtcAudioTrackExternal", this.context, this.audioManager);
/*     */     } 
/* 307 */     Logging.d("WebRtcAudioTrackExternal", "AudioTrackThread has now been stopped.");
/* 308 */     this.audioThread = null;
/* 309 */     if (this.audioTrack != null) {
/* 310 */       Logging.d("WebRtcAudioTrackExternal", "Calling AudioTrack.stop...");
/*     */       try {
/* 312 */         this.audioTrack.stop();
/* 313 */         Logging.d("WebRtcAudioTrackExternal", "AudioTrack.stop is done.");
/* 314 */         doAudioTrackStateCallback(1);
/* 315 */       } catch (IllegalStateException e) {
/* 316 */         Logging.e("WebRtcAudioTrackExternal", "AudioTrack.stop failed: " + e.getMessage());
/*     */       } 
/*     */     } 
/* 319 */     releaseAudioResources();
/* 320 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   private int getStreamMaxVolume() {
/* 326 */     this.threadChecker.checkIsOnValidThread();
/* 327 */     Logging.d("WebRtcAudioTrackExternal", "getStreamMaxVolume");
/* 328 */     return this.audioManager.getStreamMaxVolume(0);
/*     */   }
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   private boolean setStreamVolume(int volume) {
/* 334 */     this.threadChecker.checkIsOnValidThread();
/* 335 */     Logging.d("WebRtcAudioTrackExternal", "setStreamVolume(" + volume + ")");
/* 336 */     if (isVolumeFixed()) {
/* 337 */       Logging.e("WebRtcAudioTrackExternal", "The device implements a fixed volume policy.");
/* 338 */       return false;
/*     */     } 
/* 340 */     this.audioManager.setStreamVolume(0, volume, 0);
/* 341 */     return true;
/*     */   }
/*     */   
/*     */   private boolean isVolumeFixed() {
/* 345 */     if (Build.VERSION.SDK_INT < 21)
/* 346 */       return false; 
/* 347 */     return this.audioManager.isVolumeFixed();
/*     */   }
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   private int getStreamVolume() {
/* 353 */     this.threadChecker.checkIsOnValidThread();
/* 354 */     Logging.d("WebRtcAudioTrackExternal", "getStreamVolume");
/* 355 */     return this.audioManager.getStreamVolume(0);
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private int GetPlayoutUnderrunCount() {
/* 360 */     if (Build.VERSION.SDK_INT >= 24) {
/* 361 */       if (this.audioTrack != null) {
/* 362 */         return this.audioTrack.getUnderrunCount();
/*     */       }
/* 364 */       return -1;
/*     */     } 
/*     */     
/* 367 */     return -2;
/*     */   }
/*     */ 
/*     */   
/*     */   private void logMainParameters() {
/* 372 */     Logging.d("WebRtcAudioTrackExternal", "AudioTrack: session ID: " + this.audioTrack
/*     */         
/* 374 */         .getAudioSessionId() + ", channels: " + this.audioTrack
/* 375 */         .getChannelCount() + ", sample rate: " + this.audioTrack
/* 376 */         .getSampleRate() + ", max gain: " + 
/*     */ 
/*     */         
/* 379 */         AudioTrack.getMaxVolume());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @TargetApi(21)
/*     */   private static AudioTrack createAudioTrackOnLollipopOrHigher(int sampleRateInHz, int channelConfig, int bufferSizeInBytes) {
/* 388 */     Logging.d("WebRtcAudioTrackExternal", "createAudioTrackOnLollipopOrHigher");
/*     */ 
/*     */ 
/*     */     
/* 392 */     int nativeOutputSampleRate = AudioTrack.getNativeOutputSampleRate(0);
/* 393 */     Logging.d("WebRtcAudioTrackExternal", "nativeOutputSampleRate: " + nativeOutputSampleRate);
/* 394 */     if (sampleRateInHz != nativeOutputSampleRate) {
/* 395 */       Logging.w("WebRtcAudioTrackExternal", "Unable to use fast mode since requested sample rate is not native");
/*     */     }
/*     */     
/* 398 */     return new AudioTrack((new AudioAttributes.Builder())
/* 399 */         .setUsage(DEFAULT_USAGE)
/* 400 */         .setContentType(1)
/* 401 */         .build(), (new AudioFormat.Builder())
/*     */         
/* 403 */         .setEncoding(2)
/* 404 */         .setSampleRate(sampleRateInHz)
/* 405 */         .setChannelMask(channelConfig)
/* 406 */         .build(), bufferSizeInBytes, 1, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static AudioTrack createAudioTrackOnLowerThanLollipop(int sampleRateInHz, int channelConfig, int bufferSizeInBytes) {
/* 413 */     return new AudioTrack(0, sampleRateInHz, channelConfig, 2, bufferSizeInBytes, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   private void logBufferSizeInFrames() {
/* 418 */     if (Build.VERSION.SDK_INT >= 23) {
/* 419 */       Logging.d("WebRtcAudioTrackExternal", "AudioTrack: buffer size in frames: " + this.audioTrack
/*     */ 
/*     */           
/* 422 */           .getBufferSizeInFrames());
/*     */     }
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private int getBufferSizeInFrames() {
/* 428 */     if (Build.VERSION.SDK_INT >= 23) {
/* 429 */       return this.audioTrack.getBufferSizeInFrames();
/*     */     }
/* 431 */     return -1;
/*     */   }
/*     */   
/*     */   private void logBufferCapacityInFrames() {
/* 435 */     if (Build.VERSION.SDK_INT >= 24) {
/* 436 */       Logging.d("WebRtcAudioTrackExternal", "AudioTrack: buffer capacity in frames: " + this.audioTrack
/*     */ 
/*     */           
/* 439 */           .getBufferCapacityInFrames());
/*     */     }
/*     */   }
/*     */   
/*     */   private void logMainParametersExtended() {
/* 444 */     logBufferSizeInFrames();
/* 445 */     logBufferCapacityInFrames();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void logUnderrunCount() {
/* 455 */     if (Build.VERSION.SDK_INT >= 24) {
/* 456 */       Logging.d("WebRtcAudioTrackExternal", "underrun count: " + this.audioTrack.getUnderrunCount());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void assertTrue(boolean condition) {
/* 462 */     if (!condition) {
/* 463 */       throw new AssertionError("Expected condition to be true");
/*     */     }
/*     */   }
/*     */   
/*     */   private int channelCountToConfiguration(int channels) {
/* 468 */     return (channels == 1) ? 4 : 12;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSpeakerMute(boolean mute) {
/* 478 */     Logging.w("WebRtcAudioTrackExternal", "setSpeakerMute(" + mute + ")");
/* 479 */     this.speakerMute = mute;
/*     */   }
/*     */ 
/*     */   
/*     */   private void releaseAudioResources() {
/* 484 */     Logging.d("WebRtcAudioTrackExternal", "releaseAudioResources");
/* 485 */     if (this.audioTrack != null) {
/* 486 */       this.audioTrack.release();
/* 487 */       this.audioTrack = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void reportWebRtcAudioTrackInitError(String errorMessage) {
/* 492 */     Logging.e("WebRtcAudioTrackExternal", "Init playout error: " + errorMessage);
/* 493 */     WebRtcAudioUtils.logAudioState("WebRtcAudioTrackExternal", this.context, this.audioManager);
/* 494 */     if (this.errorCallback != null) {
/* 495 */       this.errorCallback.onWebRtcAudioTrackInitError(errorMessage);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void reportWebRtcAudioTrackStartError(JavaAudioDeviceModule.AudioTrackStartErrorCode errorCode, String errorMessage) {
/* 501 */     Logging.e("WebRtcAudioTrackExternal", "Start playout error: " + errorCode + ". " + errorMessage);
/* 502 */     WebRtcAudioUtils.logAudioState("WebRtcAudioTrackExternal", this.context, this.audioManager);
/* 503 */     if (this.errorCallback != null) {
/* 504 */       this.errorCallback.onWebRtcAudioTrackStartError(errorCode, errorMessage);
/*     */     }
/*     */   }
/*     */   
/*     */   private void reportWebRtcAudioTrackError(String errorMessage) {
/* 509 */     Logging.e("WebRtcAudioTrackExternal", "Run-time playback error: " + errorMessage);
/* 510 */     WebRtcAudioUtils.logAudioState("WebRtcAudioTrackExternal", this.context, this.audioManager);
/* 511 */     if (this.errorCallback != null) {
/* 512 */       this.errorCallback.onWebRtcAudioTrackError(errorMessage);
/*     */     }
/*     */   }
/*     */   
/*     */   private void doAudioTrackStateCallback(int audioState) {
/* 517 */     Logging.d("WebRtcAudioTrackExternal", "doAudioTrackStateCallback: " + audioState);
/* 518 */     if (this.stateCallback != null)
/* 519 */       if (audioState == 0) {
/* 520 */         this.stateCallback.onWebRtcAudioTrackStart();
/* 521 */       } else if (audioState == 1) {
/* 522 */         this.stateCallback.onWebRtcAudioTrackStop();
/*     */       } else {
/* 524 */         Logging.e("WebRtcAudioTrackExternal", "Invalid audio state");
/*     */       }  
/*     */   }
/*     */   
/*     */   private static native void nativeCacheDirectBufferAddress(long paramLong, ByteBuffer paramByteBuffer);
/*     */   
/*     */   private static native void nativeGetPlayoutData(long paramLong, int paramInt);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/audio/WebRtcAudioTrack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */