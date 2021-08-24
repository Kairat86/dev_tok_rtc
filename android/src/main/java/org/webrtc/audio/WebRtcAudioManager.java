/*     */ package org.webrtc.audio;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.media.AudioManager;
/*     */ import android.media.AudioRecord;
/*     */ import android.media.AudioTrack;
/*     */ import android.os.Build;
/*     */ import org.webrtc.CalledByNative;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WebRtcAudioManager
/*     */ {
/*     */   private static final String TAG = "WebRtcAudioManagerExternal";
/*     */   private static final int DEFAULT_SAMPLE_RATE_HZ = 16000;
/*     */   private static final int BITS_PER_SAMPLE = 16;
/*     */   private static final int DEFAULT_FRAME_PER_BUFFER = 256;
/*     */   
/*     */   @CalledByNative
/*     */   static AudioManager getAudioManager(Context context) {
/*  39 */     return (AudioManager)context.getSystemService("audio");
/*     */   }
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   static int getOutputBufferSize(Context context, AudioManager audioManager, int sampleRate, int numberOfOutputChannels) {
/*  45 */     return isLowLatencyOutputSupported(context) ? 
/*  46 */       getLowLatencyFramesPerBuffer(audioManager) : 
/*  47 */       getMinOutputFrameSize(sampleRate, numberOfOutputChannels);
/*     */   }
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   static int getInputBufferSize(Context context, AudioManager audioManager, int sampleRate, int numberOfInputChannels) {
/*  53 */     return isLowLatencyInputSupported(context) ? 
/*  54 */       getLowLatencyFramesPerBuffer(audioManager) : 
/*  55 */       getMinInputFrameSize(sampleRate, numberOfInputChannels);
/*     */   }
/*     */   
/*     */   private static boolean isLowLatencyOutputSupported(Context context) {
/*  59 */     return context.getPackageManager().hasSystemFeature("android.hardware.audio.low_latency");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isLowLatencyInputSupported(Context context) {
/*  67 */     return (Build.VERSION.SDK_INT >= 21 && isLowLatencyOutputSupported(context));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   static int getSampleRate(AudioManager audioManager) {
/*  77 */     if (WebRtcAudioUtils.runningOnEmulator()) {
/*  78 */       Logging.d("WebRtcAudioManagerExternal", "Running emulator, overriding sample rate to 8 kHz.");
/*  79 */       return 8000;
/*     */     } 
/*     */     
/*  82 */     int sampleRateHz = getSampleRateForApiLevel(audioManager);
/*  83 */     Logging.d("WebRtcAudioManagerExternal", "Sample rate is set to " + sampleRateHz + " Hz");
/*  84 */     return sampleRateHz;
/*     */   }
/*     */   
/*     */   private static int getSampleRateForApiLevel(AudioManager audioManager) {
/*  88 */     if (Build.VERSION.SDK_INT < 17) {
/*  89 */       return 16000;
/*     */     }
/*  91 */     String sampleRateString = audioManager.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
/*  92 */     return (sampleRateString == null) ? 16000 : Integer.parseInt(sampleRateString);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int getLowLatencyFramesPerBuffer(AudioManager audioManager) {
/*  97 */     if (Build.VERSION.SDK_INT < 17) {
/*  98 */       return 256;
/*     */     }
/*     */     
/* 101 */     String framesPerBuffer = audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
/* 102 */     return (framesPerBuffer == null) ? 256 : Integer.parseInt(framesPerBuffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getMinOutputFrameSize(int sampleRateInHz, int numChannels) {
/* 109 */     int bytesPerFrame = numChannels * 2;
/*     */     
/* 111 */     int channelConfig = (numChannels == 1) ? 4 : 12;
/* 112 */     return AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, 2) / bytesPerFrame;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getMinInputFrameSize(int sampleRateInHz, int numChannels) {
/* 121 */     int bytesPerFrame = numChannels * 2;
/*     */     
/* 123 */     int channelConfig = (numChannels == 1) ? 16 : 12;
/* 124 */     return AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, 2) / bytesPerFrame;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/audio/WebRtcAudioManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */