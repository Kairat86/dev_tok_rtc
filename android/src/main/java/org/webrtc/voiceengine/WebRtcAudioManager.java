/*     */ package org.webrtc.voiceengine;
/*     */ 
/*     */ import android.media.AudioManager;
/*     */ import android.media.AudioRecord;
/*     */ import android.media.AudioTrack;
/*     */ import android.os.Build;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import org.webrtc.ContextUtils;
/*     */ import org.webrtc.Logging;
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
/*     */ public class WebRtcAudioManager
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private static final String TAG = "WebRtcAudioManager";
/*     */   private static final boolean blacklistDeviceForAAudioUsage = true;
/*     */   private static boolean useStereoOutput;
/*     */   private static boolean useStereoInput;
/*     */   private static boolean blacklistDeviceForOpenSLESUsage;
/*     */   private static boolean blacklistDeviceForOpenSLESUsageIsOverridden;
/*     */   private static final int BITS_PER_SAMPLE = 16;
/*     */   private static final int DEFAULT_FRAME_PER_BUFFER = 256;
/*     */   private final long nativeAudioManager;
/*     */   private final AudioManager audioManager;
/*     */   private boolean initialized;
/*     */   private int nativeSampleRate;
/*     */   private int nativeChannels;
/*     */   private boolean hardwareAEC;
/*     */   private boolean hardwareAGC;
/*     */   private boolean hardwareNS;
/*     */   private boolean lowLatencyOutput;
/*     */   private boolean lowLatencyInput;
/*     */   private boolean proAudio;
/*     */   private boolean aAudio;
/*     */   private int sampleRate;
/*     */   private int outputChannels;
/*     */   private int inputChannels;
/*     */   private int outputBufferSize;
/*     */   private int inputBufferSize;
/*     */   private final VolumeLogger volumeLogger;
/*     */   
/*     */   public static synchronized void setBlacklistDeviceForOpenSLESUsage(boolean enable) {
/*  58 */     blacklistDeviceForOpenSLESUsageIsOverridden = true;
/*  59 */     blacklistDeviceForOpenSLESUsage = enable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setStereoOutput(boolean enable) {
/*  67 */     Logging.w("WebRtcAudioManager", "Overriding default output behavior: setStereoOutput(" + enable + ')');
/*  68 */     useStereoOutput = enable;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setStereoInput(boolean enable) {
/*  74 */     Logging.w("WebRtcAudioManager", "Overriding default input behavior: setStereoInput(" + enable + ')');
/*  75 */     useStereoInput = enable;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized boolean getStereoOutput() {
/*  81 */     return useStereoOutput;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized boolean getStereoInput() {
/*  87 */     return useStereoInput;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class VolumeLogger
/*     */   {
/*     */     private static final String THREAD_NAME = "WebRtcVolumeLevelLoggerThread";
/*     */ 
/*     */     
/*     */     private static final int TIMER_PERIOD_IN_SECONDS = 30;
/*     */ 
/*     */     
/*     */     private final AudioManager audioManager;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private Timer timer;
/*     */ 
/*     */     
/*     */     public VolumeLogger(AudioManager audioManager) {
/* 108 */       this.audioManager = audioManager;
/*     */     }
/*     */     
/*     */     public void start() {
/* 112 */       this.timer = new Timer("WebRtcVolumeLevelLoggerThread");
/* 113 */       this.timer.schedule(new LogVolumeTask(this.audioManager.getStreamMaxVolume(2), this.audioManager
/* 114 */             .getStreamMaxVolume(0)), 0L, 30000L);
/*     */     }
/*     */     
/*     */     private class LogVolumeTask
/*     */       extends TimerTask {
/*     */       private final int maxRingVolume;
/*     */       private final int maxVoiceCallVolume;
/*     */       
/*     */       LogVolumeTask(int maxRingVolume, int maxVoiceCallVolume) {
/* 123 */         this.maxRingVolume = maxRingVolume;
/* 124 */         this.maxVoiceCallVolume = maxVoiceCallVolume;
/*     */       }
/*     */ 
/*     */       
/*     */       public void run() {
/* 129 */         int mode = WebRtcAudioManager.VolumeLogger.this.audioManager.getMode();
/* 130 */         if (mode == 1) {
/* 131 */           Logging.d("WebRtcAudioManager", "STREAM_RING stream volume: " + WebRtcAudioManager.VolumeLogger.this
/* 132 */               .audioManager.getStreamVolume(2) + " (max=" + this.maxRingVolume + ")");
/*     */         }
/* 134 */         else if (mode == 3) {
/* 135 */           Logging.d("WebRtcAudioManager", "VOICE_CALL stream volume: " + WebRtcAudioManager.VolumeLogger.this
/* 136 */               .audioManager.getStreamVolume(0) + " (max=" + this.maxVoiceCallVolume + ")");
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private void stop() {
/* 143 */       if (this.timer != null) {
/* 144 */         this.timer.cancel();
/* 145 */         this.timer = null;
/*     */       } 
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
/*     */ 
/*     */   
/*     */   WebRtcAudioManager(long nativeAudioManager) {
/* 173 */     Logging.d("WebRtcAudioManager", "ctor" + WebRtcAudioUtils.getThreadInfo());
/* 174 */     this.nativeAudioManager = nativeAudioManager;
/* 175 */     this
/* 176 */       .audioManager = (AudioManager)ContextUtils.getApplicationContext().getSystemService("audio");
/*     */ 
/*     */ 
/*     */     
/* 180 */     this.volumeLogger = new VolumeLogger(this.audioManager);
/* 181 */     storeAudioParameters();
/* 182 */     nativeCacheAudioParameters(this.sampleRate, this.outputChannels, this.inputChannels, this.hardwareAEC, this.hardwareAGC, this.hardwareNS, this.lowLatencyOutput, this.lowLatencyInput, this.proAudio, this.aAudio, this.outputBufferSize, this.inputBufferSize, nativeAudioManager);
/*     */ 
/*     */     
/* 185 */     WebRtcAudioUtils.logAudioState("WebRtcAudioManager");
/*     */   }
/*     */   
/*     */   private boolean init() {
/* 189 */     Logging.d("WebRtcAudioManager", "init" + WebRtcAudioUtils.getThreadInfo());
/* 190 */     if (this.initialized) {
/* 191 */       return true;
/*     */     }
/* 193 */     Logging.d("WebRtcAudioManager", "audio mode is: " + 
/* 194 */         WebRtcAudioUtils.modeToString(this.audioManager.getMode()));
/* 195 */     this.initialized = true;
/* 196 */     this.volumeLogger.start();
/* 197 */     return true;
/*     */   }
/*     */   
/*     */   private void dispose() {
/* 201 */     Logging.d("WebRtcAudioManager", "dispose" + WebRtcAudioUtils.getThreadInfo());
/* 202 */     if (!this.initialized) {
/*     */       return;
/*     */     }
/* 205 */     this.volumeLogger.stop();
/*     */   }
/*     */   
/*     */   private boolean isCommunicationModeEnabled() {
/* 209 */     return (this.audioManager.getMode() == 3);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isDeviceBlacklistedForOpenSLESUsage() {
/* 215 */     boolean blacklisted = blacklistDeviceForOpenSLESUsageIsOverridden ? blacklistDeviceForOpenSLESUsage : WebRtcAudioUtils.deviceIsBlacklistedForOpenSLESUsage();
/* 216 */     if (blacklisted) {
/* 217 */       Logging.d("WebRtcAudioManager", Build.MODEL + " is blacklisted for OpenSL ES usage!");
/*     */     }
/* 219 */     return blacklisted;
/*     */   }
/*     */   
/*     */   private void storeAudioParameters() {
/* 223 */     this.outputChannels = getStereoOutput() ? 2 : 1;
/* 224 */     this.inputChannels = getStereoInput() ? 2 : 1;
/* 225 */     this.sampleRate = getNativeOutputSampleRate();
/* 226 */     this.hardwareAEC = isAcousticEchoCancelerSupported();
/*     */ 
/*     */     
/* 229 */     this.hardwareAGC = false;
/* 230 */     this.hardwareNS = isNoiseSuppressorSupported();
/* 231 */     this.lowLatencyOutput = isLowLatencyOutputSupported();
/* 232 */     this.lowLatencyInput = isLowLatencyInputSupported();
/* 233 */     this.proAudio = isProAudioSupported();
/* 234 */     this.aAudio = isAAudioSupported();
/* 235 */     this
/* 236 */       .outputBufferSize = this.lowLatencyOutput ? getLowLatencyOutputFramesPerBuffer() : getMinOutputFrameSize(this.sampleRate, this.outputChannels);
/* 237 */     this
/* 238 */       .inputBufferSize = this.lowLatencyInput ? getLowLatencyInputFramesPerBuffer() : getMinInputFrameSize(this.sampleRate, this.inputChannels);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean hasEarpiece() {
/* 243 */     return ContextUtils.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.telephony");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isLowLatencyOutputSupported() {
/* 249 */     return ContextUtils.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.audio.low_latency");
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
/*     */   public boolean isLowLatencyInputSupported() {
/* 261 */     return (Build.VERSION.SDK_INT >= 21 && isLowLatencyOutputSupported());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isProAudioSupported() {
/* 267 */     return (Build.VERSION.SDK_INT >= 23 && 
/* 268 */       ContextUtils.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.audio.pro"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isAAudioSupported() {
/* 276 */     Logging.w("WebRtcAudioManager", "AAudio support is currently disabled on all devices!");
/*     */     
/* 278 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getNativeOutputSampleRate() {
/* 285 */     if (WebRtcAudioUtils.runningOnEmulator()) {
/* 286 */       Logging.d("WebRtcAudioManager", "Running emulator, overriding sample rate to 8 kHz.");
/* 287 */       return 8000;
/*     */     } 
/*     */ 
/*     */     
/* 291 */     if (WebRtcAudioUtils.isDefaultSampleRateOverridden()) {
/* 292 */       Logging.d("WebRtcAudioManager", "Default sample rate is overriden to " + 
/* 293 */           WebRtcAudioUtils.getDefaultSampleRateHz() + " Hz");
/* 294 */       return WebRtcAudioUtils.getDefaultSampleRateHz();
/*     */     } 
/*     */ 
/*     */     
/* 298 */     int sampleRateHz = getSampleRateForApiLevel();
/* 299 */     Logging.d("WebRtcAudioManager", "Sample rate is set to " + sampleRateHz + " Hz");
/* 300 */     return sampleRateHz;
/*     */   }
/*     */   
/*     */   private int getSampleRateForApiLevel() {
/* 304 */     if (Build.VERSION.SDK_INT < 17) {
/* 305 */       return WebRtcAudioUtils.getDefaultSampleRateHz();
/*     */     }
/* 307 */     String sampleRateString = this.audioManager.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
/* 308 */     return (sampleRateString == null) ? WebRtcAudioUtils.getDefaultSampleRateHz() : 
/* 309 */       Integer.parseInt(sampleRateString);
/*     */   }
/*     */ 
/*     */   
/*     */   private int getLowLatencyOutputFramesPerBuffer() {
/* 314 */     assertTrue(isLowLatencyOutputSupported());
/* 315 */     if (Build.VERSION.SDK_INT < 17) {
/* 316 */       return 256;
/*     */     }
/*     */     
/* 319 */     String framesPerBuffer = this.audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
/* 320 */     return (framesPerBuffer == null) ? 256 : Integer.parseInt(framesPerBuffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isAcousticEchoCancelerSupported() {
/* 330 */     return WebRtcAudioEffects.canUseAcousticEchoCanceler();
/*     */   }
/*     */   private static boolean isNoiseSuppressorSupported() {
/* 333 */     return WebRtcAudioEffects.canUseNoiseSuppressor();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getMinOutputFrameSize(int sampleRateInHz, int numChannels) {
/* 340 */     int bytesPerFrame = numChannels * 2;
/*     */     
/* 342 */     int channelConfig = (numChannels == 1) ? 4 : 12;
/* 343 */     return AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, 2) / bytesPerFrame;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getLowLatencyInputFramesPerBuffer() {
/* 350 */     assertTrue(isLowLatencyInputSupported());
/* 351 */     return getLowLatencyOutputFramesPerBuffer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getMinInputFrameSize(int sampleRateInHz, int numChannels) {
/* 358 */     int bytesPerFrame = numChannels * 2;
/*     */     
/* 360 */     int channelConfig = (numChannels == 1) ? 16 : 12;
/* 361 */     return AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, 2) / bytesPerFrame;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void assertTrue(boolean condition) {
/* 368 */     if (!condition)
/* 369 */       throw new AssertionError("Expected condition to be true"); 
/*     */   }
/*     */   
/*     */   private native void nativeCacheAudioParameters(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, boolean paramBoolean7, int paramInt4, int paramInt5, long paramLong);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/voiceengine/WebRtcAudioManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */