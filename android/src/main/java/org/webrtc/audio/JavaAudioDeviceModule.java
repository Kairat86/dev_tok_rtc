/*     */ package org.webrtc.audio;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.media.AudioDeviceInfo;
/*     */ import android.media.AudioManager;
/*     */ import androidx.annotation.RequiresApi;
/*     */ import org.webrtc.JniCommon;
/*     */ import org.webrtc.Logging;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JavaAudioDeviceModule
/*     */   implements AudioDeviceModule
/*     */ {
/*     */   private static final String TAG = "JavaAudioDeviceModule";
/*     */   private final Context context;
/*     */   private final AudioManager audioManager;
/*     */   private final WebRtcAudioRecord audioInput;
/*     */   private final WebRtcAudioTrack audioOutput;
/*     */   private final int inputSampleRate;
/*     */   private final int outputSampleRate;
/*     */   private final boolean useStereoInput;
/*     */   private final boolean useStereoOutput;
/*     */   
/*     */   public static Builder builder(Context context) {
/*  29 */     return new Builder(context);
/*     */   }
/*     */   
/*     */   public static class Builder {
/*     */     private final Context context;
/*     */     private final AudioManager audioManager;
/*     */     private int inputSampleRate;
/*     */     private int outputSampleRate;
/*  37 */     private int audioSource = 7;
/*  38 */     private int audioFormat = 2;
/*     */     private JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback;
/*     */     private JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback;
/*     */     private JavaAudioDeviceModule.SamplesReadyCallback samplesReadyCallback;
/*     */     private JavaAudioDeviceModule.AudioTrackStateCallback audioTrackStateCallback;
/*     */     private JavaAudioDeviceModule.AudioRecordStateCallback audioRecordStateCallback;
/*  44 */     private boolean useHardwareAcousticEchoCanceler = JavaAudioDeviceModule.isBuiltInAcousticEchoCancelerSupported();
/*  45 */     private boolean useHardwareNoiseSuppressor = JavaAudioDeviceModule.isBuiltInNoiseSuppressorSupported();
/*     */     private boolean useStereoInput;
/*     */     private boolean useStereoOutput;
/*     */     
/*     */     private Builder(Context context) {
/*  50 */       this.context = context;
/*  51 */       this.audioManager = (AudioManager)context.getSystemService("audio");
/*  52 */       this.inputSampleRate = WebRtcAudioManager.getSampleRate(this.audioManager);
/*  53 */       this.outputSampleRate = WebRtcAudioManager.getSampleRate(this.audioManager);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setSampleRate(int sampleRate) {
/*  62 */       Logging.d("JavaAudioDeviceModule", "Input/Output sample rate overridden to: " + sampleRate);
/*  63 */       this.inputSampleRate = sampleRate;
/*  64 */       this.outputSampleRate = sampleRate;
/*  65 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setInputSampleRate(int inputSampleRate) {
/*  72 */       Logging.d("JavaAudioDeviceModule", "Input sample rate overridden to: " + inputSampleRate);
/*  73 */       this.inputSampleRate = inputSampleRate;
/*  74 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setOutputSampleRate(int outputSampleRate) {
/*  81 */       Logging.d("JavaAudioDeviceModule", "Output sample rate overridden to: " + outputSampleRate);
/*  82 */       this.outputSampleRate = outputSampleRate;
/*  83 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setAudioSource(int audioSource) {
/*  91 */       this.audioSource = audioSource;
/*  92 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setAudioFormat(int audioFormat) {
/* 102 */       this.audioFormat = audioFormat;
/* 103 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setAudioTrackErrorCallback(JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback) {
/* 110 */       this.audioTrackErrorCallback = audioTrackErrorCallback;
/* 111 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setAudioRecordErrorCallback(JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback) {
/* 118 */       this.audioRecordErrorCallback = audioRecordErrorCallback;
/* 119 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setSamplesReadyCallback(JavaAudioDeviceModule.SamplesReadyCallback samplesReadyCallback) {
/* 126 */       this.samplesReadyCallback = samplesReadyCallback;
/* 127 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setAudioTrackStateCallback(JavaAudioDeviceModule.AudioTrackStateCallback audioTrackStateCallback) {
/* 134 */       this.audioTrackStateCallback = audioTrackStateCallback;
/* 135 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setAudioRecordStateCallback(JavaAudioDeviceModule.AudioRecordStateCallback audioRecordStateCallback) {
/* 142 */       this.audioRecordStateCallback = audioRecordStateCallback;
/* 143 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setUseHardwareNoiseSuppressor(boolean useHardwareNoiseSuppressor) {
/* 151 */       if (useHardwareNoiseSuppressor && !JavaAudioDeviceModule.isBuiltInNoiseSuppressorSupported()) {
/* 152 */         Logging.e("JavaAudioDeviceModule", "HW NS not supported");
/* 153 */         useHardwareNoiseSuppressor = false;
/*     */       } 
/* 155 */       this.useHardwareNoiseSuppressor = useHardwareNoiseSuppressor;
/* 156 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setUseHardwareAcousticEchoCanceler(boolean useHardwareAcousticEchoCanceler) {
/* 165 */       if (useHardwareAcousticEchoCanceler && !JavaAudioDeviceModule.isBuiltInAcousticEchoCancelerSupported()) {
/* 166 */         Logging.e("JavaAudioDeviceModule", "HW AEC not supported");
/* 167 */         useHardwareAcousticEchoCanceler = false;
/*     */       } 
/* 169 */       this.useHardwareAcousticEchoCanceler = useHardwareAcousticEchoCanceler;
/* 170 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setUseStereoInput(boolean useStereoInput) {
/* 177 */       this.useStereoInput = useStereoInput;
/* 178 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setUseStereoOutput(boolean useStereoOutput) {
/* 185 */       this.useStereoOutput = useStereoOutput;
/* 186 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JavaAudioDeviceModule createAudioDeviceModule() {
/* 194 */       Logging.d("JavaAudioDeviceModule", "createAudioDeviceModule");
/* 195 */       if (this.useHardwareNoiseSuppressor) {
/* 196 */         Logging.d("JavaAudioDeviceModule", "HW NS will be used.");
/*     */       } else {
/* 198 */         if (JavaAudioDeviceModule.isBuiltInNoiseSuppressorSupported()) {
/* 199 */           Logging.d("JavaAudioDeviceModule", "Overriding default behavior; now using WebRTC NS!");
/*     */         }
/* 201 */         Logging.d("JavaAudioDeviceModule", "HW NS will not be used.");
/*     */       } 
/* 203 */       if (this.useHardwareAcousticEchoCanceler) {
/* 204 */         Logging.d("JavaAudioDeviceModule", "HW AEC will be used.");
/*     */       } else {
/* 206 */         if (JavaAudioDeviceModule.isBuiltInAcousticEchoCancelerSupported()) {
/* 207 */           Logging.d("JavaAudioDeviceModule", "Overriding default behavior; now using WebRTC AEC!");
/*     */         }
/* 209 */         Logging.d("JavaAudioDeviceModule", "HW AEC will not be used.");
/*     */       } 
/* 211 */       WebRtcAudioRecord audioInput = new WebRtcAudioRecord(this.context, this.audioManager, this.audioSource, this.audioFormat, this.audioRecordErrorCallback, this.audioRecordStateCallback, this.samplesReadyCallback, this.useHardwareAcousticEchoCanceler, this.useHardwareNoiseSuppressor);
/*     */ 
/*     */       
/* 214 */       WebRtcAudioTrack audioOutput = new WebRtcAudioTrack(this.context, this.audioManager, this.audioTrackErrorCallback, this.audioTrackStateCallback);
/*     */       
/* 216 */       return new JavaAudioDeviceModule(this.context, this.audioManager, audioInput, audioOutput, this.inputSampleRate, this.outputSampleRate, this.useStereoInput, this.useStereoOutput);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum AudioRecordStartErrorCode
/*     */   {
/* 224 */     AUDIO_RECORD_START_EXCEPTION,
/* 225 */     AUDIO_RECORD_START_STATE_MISMATCH;
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface AudioRecordErrorCallback
/*     */   {
/*     */     void onWebRtcAudioRecordInitError(String param1String);
/*     */ 
/*     */     
/*     */     void onWebRtcAudioRecordStartError(JavaAudioDeviceModule.AudioRecordStartErrorCode param1AudioRecordStartErrorCode, String param1String);
/*     */     
/*     */     void onWebRtcAudioRecordError(String param1String);
/*     */   }
/*     */   
/*     */   public static interface AudioRecordStateCallback
/*     */   {
/*     */     void onWebRtcAudioRecordStart();
/*     */     
/*     */     void onWebRtcAudioRecordStop();
/*     */   }
/*     */   
/*     */   public static class AudioSamples
/*     */   {
/*     */     private final int audioFormat;
/*     */     private final int channelCount;
/*     */     private final int sampleRate;
/*     */     private final byte[] data;
/*     */     
/*     */     public AudioSamples(int audioFormat, int channelCount, int sampleRate, byte[] data) {
/* 254 */       this.audioFormat = audioFormat;
/* 255 */       this.channelCount = channelCount;
/* 256 */       this.sampleRate = sampleRate;
/* 257 */       this.data = data;
/*     */     }
/*     */     
/*     */     public int getAudioFormat() {
/* 261 */       return this.audioFormat;
/*     */     }
/*     */     
/*     */     public int getChannelCount() {
/* 265 */       return this.channelCount;
/*     */     }
/*     */     
/*     */     public int getSampleRate() {
/* 269 */       return this.sampleRate;
/*     */     }
/*     */     
/*     */     public byte[] getData() {
/* 273 */       return this.data;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface SamplesReadyCallback
/*     */   {
/*     */     void onWebRtcAudioRecordSamplesReady(JavaAudioDeviceModule.AudioSamples param1AudioSamples);
/*     */   }
/*     */   
/*     */   public enum AudioTrackStartErrorCode
/*     */   {
/* 285 */     AUDIO_TRACK_START_EXCEPTION,
/* 286 */     AUDIO_TRACK_START_STATE_MISMATCH;
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
/*     */   public static boolean isBuiltInAcousticEchoCancelerSupported() {
/* 306 */     return WebRtcAudioEffects.isAcousticEchoCancelerSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBuiltInNoiseSuppressorSupported() {
/* 314 */     return WebRtcAudioEffects.isNoiseSuppressorSupported();
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
/* 326 */   private final Object nativeLock = new Object();
/*     */   
/*     */   private long nativeAudioDeviceModule;
/*     */ 
/*     */   
/*     */   private JavaAudioDeviceModule(Context context, AudioManager audioManager, WebRtcAudioRecord audioInput, WebRtcAudioTrack audioOutput, int inputSampleRate, int outputSampleRate, boolean useStereoInput, boolean useStereoOutput) {
/* 332 */     this.context = context;
/* 333 */     this.audioManager = audioManager;
/* 334 */     this.audioInput = audioInput;
/* 335 */     this.audioOutput = audioOutput;
/* 336 */     this.inputSampleRate = inputSampleRate;
/* 337 */     this.outputSampleRate = outputSampleRate;
/* 338 */     this.useStereoInput = useStereoInput;
/* 339 */     this.useStereoOutput = useStereoOutput;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getNativeAudioDeviceModulePointer() {
/* 344 */     synchronized (this.nativeLock) {
/* 345 */       if (this.nativeAudioDeviceModule == 0L) {
/* 346 */         this.nativeAudioDeviceModule = nativeCreateAudioDeviceModule(this.context, this.audioManager, this.audioInput, this.audioOutput, this.inputSampleRate, this.outputSampleRate, this.useStereoInput, this.useStereoOutput);
/*     */       }
/*     */       
/* 349 */       return this.nativeAudioDeviceModule;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void release() {
/* 355 */     synchronized (this.nativeLock) {
/* 356 */       if (this.nativeAudioDeviceModule != 0L) {
/* 357 */         JniCommon.nativeReleaseRef(this.nativeAudioDeviceModule);
/* 358 */         this.nativeAudioDeviceModule = 0L;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSpeakerMute(boolean mute) {
/* 365 */     Logging.d("JavaAudioDeviceModule", "setSpeakerMute: " + mute);
/* 366 */     this.audioOutput.setSpeakerMute(mute);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMicrophoneMute(boolean mute) {
/* 371 */     Logging.d("JavaAudioDeviceModule", "setMicrophoneMute: " + mute);
/* 372 */     this.audioInput.setMicrophoneMute(mute);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @RequiresApi(23)
/*     */   public void setPreferredInputDevice(AudioDeviceInfo preferredInputDevice) {
/* 383 */     Logging.d("JavaAudioDeviceModule", "setPreferredInputDevice: " + preferredInputDevice);
/* 384 */     this.audioInput.setPreferredDevice(preferredInputDevice);
/*     */   }
/*     */   
/*     */   private static native long nativeCreateAudioDeviceModule(Context paramContext, AudioManager paramAudioManager, WebRtcAudioRecord paramWebRtcAudioRecord, WebRtcAudioTrack paramWebRtcAudioTrack, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2);
/*     */   
/*     */   public static interface AudioTrackErrorCallback {
/*     */     void onWebRtcAudioTrackInitError(String param1String);
/*     */     
/*     */     void onWebRtcAudioTrackStartError(JavaAudioDeviceModule.AudioTrackStartErrorCode param1AudioTrackStartErrorCode, String param1String);
/*     */     
/*     */     void onWebRtcAudioTrackError(String param1String);
/*     */   }
/*     */   
/*     */   public static interface AudioTrackStateCallback {
/*     */     void onWebRtcAudioTrackStart();
/*     */     
/*     */     void onWebRtcAudioTrackStop();
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/audio/JavaAudioDeviceModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */