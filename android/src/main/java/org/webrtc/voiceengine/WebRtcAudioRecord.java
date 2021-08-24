/*     */ package org.webrtc.voiceengine;
/*     */ 
/*     */ import android.media.AudioRecord;
/*     */ import android.os.Build;
/*     */ import android.os.Process;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebRtcAudioRecord
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private static final String TAG = "WebRtcAudioRecord";
/*     */   private static final int BITS_PER_SAMPLE = 16;
/*     */   private static final int CALLBACK_BUFFER_SIZE_MS = 10;
/*     */   private static final int BUFFERS_PER_SECOND = 100;
/*     */   private static final int BUFFER_SIZE_FACTOR = 2;
/*     */   private static final long AUDIO_RECORD_THREAD_JOIN_TIMEOUT_MS = 2000L;
/*  50 */   private static final int DEFAULT_AUDIO_SOURCE = getDefaultAudioSource(); private final long nativeAudioRecord; @Nullable private WebRtcAudioEffects effects; private ByteBuffer byteBuffer; @Nullable private AudioRecord audioRecord; @Nullable
/*  51 */   private AudioRecordThread audioThread; private static int audioSource = DEFAULT_AUDIO_SOURCE;
/*     */ 
/*     */   
/*     */   private static volatile boolean microphoneMute;
/*     */   
/*     */   private byte[] emptyBytes;
/*     */   
/*     */   @Nullable
/*     */   private static WebRtcAudioRecordErrorCallback errorCallback;
/*     */   
/*     */   @Nullable
/*     */   private static WebRtcAudioRecordSamplesReadyCallback audioSamplesReadyCallback;
/*     */ 
/*     */   
/*     */   public enum AudioRecordStartErrorCode
/*     */   {
/*  67 */     AUDIO_RECORD_START_EXCEPTION,
/*  68 */     AUDIO_RECORD_START_STATE_MISMATCH;
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
/*     */   public static void setErrorCallback(WebRtcAudioRecordErrorCallback errorCallback) {
/*  80 */     Logging.d("WebRtcAudioRecord", "Set error callback");
/*  81 */     WebRtcAudioRecord.errorCallback = errorCallback;
/*     */   }
/*     */   
/*     */   public static interface WebRtcAudioRecordErrorCallback {
/*     */     void onWebRtcAudioRecordInitError(String param1String);
/*     */     
/*     */     void onWebRtcAudioRecordStartError(WebRtcAudioRecord.AudioRecordStartErrorCode param1AudioRecordStartErrorCode, String param1String);
/*     */     
/*     */     void onWebRtcAudioRecordError(String param1String);
/*     */   }
/*     */   
/*     */   public static class AudioSamples {
/*     */     private final int audioFormat;
/*     */     private final int channelCount;
/*     */     private final int sampleRate;
/*     */     private final byte[] data;
/*     */     
/*     */     private AudioSamples(AudioRecord audioRecord, byte[] data) {
/*  99 */       this.audioFormat = audioRecord.getAudioFormat();
/* 100 */       this.channelCount = audioRecord.getChannelCount();
/* 101 */       this.sampleRate = audioRecord.getSampleRate();
/* 102 */       this.data = data;
/*     */     }
/*     */     
/*     */     public int getAudioFormat() {
/* 106 */       return this.audioFormat;
/*     */     }
/*     */     
/*     */     public int getChannelCount() {
/* 110 */       return this.channelCount;
/*     */     }
/*     */     
/*     */     public int getSampleRate() {
/* 114 */       return this.sampleRate;
/*     */     }
/*     */     
/*     */     public byte[] getData() {
/* 118 */       return this.data;
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
/*     */   public static void setOnAudioSamplesReady(WebRtcAudioRecordSamplesReadyCallback callback) {
/* 130 */     audioSamplesReadyCallback = callback;
/*     */   }
/*     */   
/*     */   public static interface WebRtcAudioRecordSamplesReadyCallback
/*     */   {
/*     */     void onWebRtcAudioRecordSamplesReady(WebRtcAudioRecord.AudioSamples param1AudioSamples);
/*     */   }
/*     */   
/*     */   private class AudioRecordThread
/*     */     extends Thread {
/*     */     private volatile boolean keepAlive = true;
/*     */     
/*     */     public AudioRecordThread(String name) {
/* 143 */       super(name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 150 */       Process.setThreadPriority(-19);
/* 151 */       Logging.d("WebRtcAudioRecord", "AudioRecordThread" + WebRtcAudioUtils.getThreadInfo());
/* 152 */       WebRtcAudioRecord.assertTrue((WebRtcAudioRecord.this.audioRecord.getRecordingState() == 3));
/*     */       
/* 154 */       long lastTime = System.nanoTime();
/* 155 */       while (this.keepAlive) {
/* 156 */         int bytesRead = WebRtcAudioRecord.this.audioRecord.read(WebRtcAudioRecord.this.byteBuffer, WebRtcAudioRecord.this.byteBuffer.capacity());
/* 157 */         if (bytesRead == WebRtcAudioRecord.this.byteBuffer.capacity()) {
/* 158 */           if (WebRtcAudioRecord.microphoneMute) {
/* 159 */             WebRtcAudioRecord.this.byteBuffer.clear();
/* 160 */             WebRtcAudioRecord.this.byteBuffer.put(WebRtcAudioRecord.this.emptyBytes);
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 165 */           if (this.keepAlive) {
/* 166 */             WebRtcAudioRecord.this.nativeDataIsRecorded(bytesRead, WebRtcAudioRecord.this.nativeAudioRecord);
/*     */           }
/* 168 */           if (WebRtcAudioRecord.audioSamplesReadyCallback != null) {
/*     */ 
/*     */             
/* 171 */             byte[] data = Arrays.copyOf(WebRtcAudioRecord.this.byteBuffer.array(), WebRtcAudioRecord.this.byteBuffer.capacity());
/* 172 */             WebRtcAudioRecord.audioSamplesReadyCallback.onWebRtcAudioRecordSamplesReady(new WebRtcAudioRecord.AudioSamples(WebRtcAudioRecord.this
/* 173 */                   .audioRecord, data));
/*     */           }  continue;
/*     */         } 
/* 176 */         String errorMessage = "AudioRecord.read failed: " + bytesRead;
/* 177 */         Logging.e("WebRtcAudioRecord", errorMessage);
/* 178 */         if (bytesRead == -3) {
/* 179 */           this.keepAlive = false;
/* 180 */           WebRtcAudioRecord.this.reportWebRtcAudioRecordError(errorMessage);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 192 */         if (WebRtcAudioRecord.this.audioRecord != null) {
/* 193 */           WebRtcAudioRecord.this.audioRecord.stop();
/*     */         }
/* 195 */       } catch (IllegalStateException e) {
/* 196 */         Logging.e("WebRtcAudioRecord", "AudioRecord.stop failed: " + e.getMessage());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void stopThread() {
/* 203 */       Logging.d("WebRtcAudioRecord", "stopThread");
/* 204 */       this.keepAlive = false;
/*     */     }
/*     */   }
/*     */   
/*     */   WebRtcAudioRecord(long nativeAudioRecord) {
/* 209 */     Logging.d("WebRtcAudioRecord", "ctor" + WebRtcAudioUtils.getThreadInfo());
/* 210 */     this.nativeAudioRecord = nativeAudioRecord;
/*     */ 
/*     */ 
/*     */     
/* 214 */     this.effects = WebRtcAudioEffects.create();
/*     */   }
/*     */   
/*     */   private boolean enableBuiltInAEC(boolean enable) {
/* 218 */     Logging.d("WebRtcAudioRecord", "enableBuiltInAEC(" + enable + ')');
/* 219 */     if (this.effects == null) {
/* 220 */       Logging.e("WebRtcAudioRecord", "Built-in AEC is not supported on this platform");
/* 221 */       return false;
/*     */     } 
/* 223 */     return this.effects.setAEC(enable);
/*     */   }
/*     */   
/*     */   private boolean enableBuiltInNS(boolean enable) {
/* 227 */     Logging.d("WebRtcAudioRecord", "enableBuiltInNS(" + enable + ')');
/* 228 */     if (this.effects == null) {
/* 229 */       Logging.e("WebRtcAudioRecord", "Built-in NS is not supported on this platform");
/* 230 */       return false;
/*     */     } 
/* 232 */     return this.effects.setNS(enable);
/*     */   }
/*     */   
/*     */   private int initRecording(int sampleRate, int channels) {
/* 236 */     Logging.d("WebRtcAudioRecord", "initRecording(sampleRate=" + sampleRate + ", channels=" + channels + ")");
/* 237 */     if (this.audioRecord != null) {
/* 238 */       reportWebRtcAudioRecordInitError("InitRecording called twice without StopRecording.");
/* 239 */       return -1;
/*     */     } 
/* 241 */     int bytesPerFrame = channels * 2;
/* 242 */     int framesPerBuffer = sampleRate / 100;
/* 243 */     this.byteBuffer = ByteBuffer.allocateDirect(bytesPerFrame * framesPerBuffer);
/* 244 */     Logging.d("WebRtcAudioRecord", "byteBuffer.capacity: " + this.byteBuffer.capacity());
/* 245 */     this.emptyBytes = new byte[this.byteBuffer.capacity()];
/*     */ 
/*     */ 
/*     */     
/* 249 */     nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioRecord);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 254 */     int channelConfig = channelCountToConfiguration(channels);
/*     */     
/* 256 */     int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, 2);
/* 257 */     if (minBufferSize == -1 || minBufferSize == -2) {
/* 258 */       reportWebRtcAudioRecordInitError("AudioRecord.getMinBufferSize failed: " + minBufferSize);
/* 259 */       return -1;
/*     */     } 
/* 261 */     Logging.d("WebRtcAudioRecord", "AudioRecord.getMinBufferSize: " + minBufferSize);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 266 */     int bufferSizeInBytes = Math.max(2 * minBufferSize, this.byteBuffer.capacity());
/* 267 */     Logging.d("WebRtcAudioRecord", "bufferSizeInBytes: " + bufferSizeInBytes);
/*     */     try {
/* 269 */       this.audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, 2, bufferSizeInBytes);
/*     */     }
/* 271 */     catch (IllegalArgumentException e) {
/* 272 */       reportWebRtcAudioRecordInitError("AudioRecord ctor error: " + e.getMessage());
/* 273 */       releaseAudioResources();
/* 274 */       return -1;
/*     */     } 
/* 276 */     if (this.audioRecord == null || this.audioRecord.getState() != 1) {
/* 277 */       reportWebRtcAudioRecordInitError("Failed to create a new AudioRecord instance");
/* 278 */       releaseAudioResources();
/* 279 */       return -1;
/*     */     } 
/* 281 */     if (this.effects != null) {
/* 282 */       this.effects.enable(this.audioRecord.getAudioSessionId());
/*     */     }
/* 284 */     logMainParameters();
/* 285 */     logMainParametersExtended();
/* 286 */     return framesPerBuffer;
/*     */   }
/*     */   
/*     */   private boolean startRecording() {
/* 290 */     Logging.d("WebRtcAudioRecord", "startRecording");
/* 291 */     assertTrue((this.audioRecord != null));
/* 292 */     assertTrue((this.audioThread == null));
/*     */     try {
/* 294 */       this.audioRecord.startRecording();
/* 295 */     } catch (IllegalStateException e) {
/* 296 */       reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode.AUDIO_RECORD_START_EXCEPTION, "AudioRecord.startRecording failed: " + e
/* 297 */           .getMessage());
/* 298 */       return false;
/*     */     } 
/* 300 */     if (this.audioRecord.getRecordingState() != 3) {
/* 301 */       reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode.AUDIO_RECORD_START_STATE_MISMATCH, "AudioRecord.startRecording failed - incorrect state :" + this.audioRecord
/*     */ 
/*     */           
/* 304 */           .getRecordingState());
/* 305 */       return false;
/*     */     } 
/* 307 */     this.audioThread = new AudioRecordThread("AudioRecordJavaThread");
/* 308 */     this.audioThread.start();
/* 309 */     return true;
/*     */   }
/*     */   
/*     */   private boolean stopRecording() {
/* 313 */     Logging.d("WebRtcAudioRecord", "stopRecording");
/* 314 */     assertTrue((this.audioThread != null));
/* 315 */     this.audioThread.stopThread();
/* 316 */     if (!ThreadUtils.joinUninterruptibly(this.audioThread, 2000L)) {
/* 317 */       Logging.e("WebRtcAudioRecord", "Join of AudioRecordJavaThread timed out");
/* 318 */       WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
/*     */     } 
/* 320 */     this.audioThread = null;
/* 321 */     if (this.effects != null) {
/* 322 */       this.effects.release();
/*     */     }
/* 324 */     releaseAudioResources();
/* 325 */     return true;
/*     */   }
/*     */   
/*     */   private void logMainParameters() {
/* 329 */     Logging.d("WebRtcAudioRecord", "AudioRecord: session ID: " + this.audioRecord
/* 330 */         .getAudioSessionId() + ", channels: " + this.audioRecord
/* 331 */         .getChannelCount() + ", sample rate: " + this.audioRecord
/* 332 */         .getSampleRate());
/*     */   }
/*     */   
/*     */   private void logMainParametersExtended() {
/* 336 */     if (Build.VERSION.SDK_INT >= 23) {
/* 337 */       Logging.d("WebRtcAudioRecord", "AudioRecord: buffer size in frames: " + this.audioRecord
/*     */           
/* 339 */           .getBufferSizeInFrames());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void assertTrue(boolean condition) {
/* 345 */     if (!condition) {
/* 346 */       throw new AssertionError("Expected condition to be true");
/*     */     }
/*     */   }
/*     */   
/*     */   private int channelCountToConfiguration(int channels) {
/* 351 */     return (channels == 1) ? 16 : 12;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setAudioSource(int source) {
/* 360 */     Logging.w("WebRtcAudioRecord", "Audio source is changed from: " + audioSource + " to " + source);
/*     */     
/* 362 */     audioSource = source;
/*     */   }
/*     */   
/*     */   private static int getDefaultAudioSource() {
/* 366 */     return 7;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setMicrophoneMute(boolean mute) {
/* 372 */     Logging.w("WebRtcAudioRecord", "setMicrophoneMute(" + mute + ")");
/* 373 */     microphoneMute = mute;
/*     */   }
/*     */ 
/*     */   
/*     */   private void releaseAudioResources() {
/* 378 */     Logging.d("WebRtcAudioRecord", "releaseAudioResources");
/* 379 */     if (this.audioRecord != null) {
/* 380 */       this.audioRecord.release();
/* 381 */       this.audioRecord = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void reportWebRtcAudioRecordInitError(String errorMessage) {
/* 386 */     Logging.e("WebRtcAudioRecord", "Init recording error: " + errorMessage);
/* 387 */     WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
/* 388 */     if (errorCallback != null) {
/* 389 */       errorCallback.onWebRtcAudioRecordInitError(errorMessage);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode errorCode, String errorMessage) {
/* 395 */     Logging.e("WebRtcAudioRecord", "Start recording error: " + errorCode + ". " + errorMessage);
/* 396 */     WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
/* 397 */     if (errorCallback != null) {
/* 398 */       errorCallback.onWebRtcAudioRecordStartError(errorCode, errorMessage);
/*     */     }
/*     */   }
/*     */   
/*     */   private void reportWebRtcAudioRecordError(String errorMessage) {
/* 403 */     Logging.e("WebRtcAudioRecord", "Run-time recording error: " + errorMessage);
/* 404 */     WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
/* 405 */     if (errorCallback != null)
/* 406 */       errorCallback.onWebRtcAudioRecordError(errorMessage); 
/*     */   }
/*     */   
/*     */   private native void nativeCacheDirectBufferAddress(ByteBuffer paramByteBuffer, long paramLong);
/*     */   
/*     */   private native void nativeDataIsRecorded(int paramInt, long paramLong);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/voiceengine/WebRtcAudioRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */