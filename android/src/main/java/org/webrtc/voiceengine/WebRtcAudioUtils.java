/*     */ package org.webrtc.voiceengine;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.media.AudioDeviceInfo;
/*     */ import android.media.AudioManager;
/*     */ import android.os.Build;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class WebRtcAudioUtils
/*     */ {
/*     */   private static final String TAG = "WebRtcAudioUtils";
/*  35 */   private static final String[] BLACKLISTED_OPEN_SL_ES_MODELS = new String[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   private static final String[] BLACKLISTED_AEC_MODELS = new String[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   private static final String[] BLACKLISTED_NS_MODELS = new String[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int DEFAULT_SAMPLE_RATE_HZ = 16000;
/*     */ 
/*     */ 
/*     */   
/*  59 */   private static int defaultSampleRateHz = 16000;
/*     */ 
/*     */   
/*     */   private static boolean isDefaultSampleRateOverridden;
/*     */ 
/*     */   
/*     */   private static boolean useWebRtcBasedAcousticEchoCanceler;
/*     */ 
/*     */   
/*     */   private static boolean useWebRtcBasedNoiseSuppressor;
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setWebRtcBasedAcousticEchoCanceler(boolean enable) {
/*  73 */     useWebRtcBasedAcousticEchoCanceler = enable;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setWebRtcBasedNoiseSuppressor(boolean enable) {
/*  79 */     useWebRtcBasedNoiseSuppressor = enable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setWebRtcBasedAutomaticGainControl(boolean enable) {
/*  86 */     Logging.w("WebRtcAudioUtils", "setWebRtcBasedAutomaticGainControl() is deprecated");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized boolean useWebRtcBasedAcousticEchoCanceler() {
/*  92 */     if (useWebRtcBasedAcousticEchoCanceler) {
/*  93 */       Logging.w("WebRtcAudioUtils", "Overriding default behavior; now using WebRTC AEC!");
/*     */     }
/*  95 */     return useWebRtcBasedAcousticEchoCanceler;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized boolean useWebRtcBasedNoiseSuppressor() {
/* 101 */     if (useWebRtcBasedNoiseSuppressor) {
/* 102 */       Logging.w("WebRtcAudioUtils", "Overriding default behavior; now using WebRTC NS!");
/*     */     }
/* 104 */     return useWebRtcBasedNoiseSuppressor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized boolean useWebRtcBasedAutomaticGainControl() {
/* 112 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAcousticEchoCancelerSupported() {
/* 122 */     return WebRtcAudioEffects.canUseAcousticEchoCanceler();
/*     */   }
/*     */   public static boolean isNoiseSuppressorSupported() {
/* 125 */     return WebRtcAudioEffects.canUseNoiseSuppressor();
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAutomaticGainControlSupported() {
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setDefaultSampleRateHz(int sampleRateHz) {
/* 139 */     isDefaultSampleRateOverridden = true;
/* 140 */     defaultSampleRateHz = sampleRateHz;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized boolean isDefaultSampleRateOverridden() {
/* 146 */     return isDefaultSampleRateOverridden;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized int getDefaultSampleRateHz() {
/* 152 */     return defaultSampleRateHz;
/*     */   }
/*     */   
/*     */   public static List<String> getBlackListedModelsForAecUsage() {
/* 156 */     return Arrays.asList(BLACKLISTED_AEC_MODELS);
/*     */   }
/*     */   
/*     */   public static List<String> getBlackListedModelsForNsUsage() {
/* 160 */     return Arrays.asList(BLACKLISTED_NS_MODELS);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getThreadInfo() {
/* 165 */     return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean runningOnEmulator() {
/* 171 */     return (Build.HARDWARE.equals("goldfish") && Build.BRAND.startsWith("generic_"));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean deviceIsBlacklistedForOpenSLESUsage() {
/* 176 */     List<String> blackListedModels = Arrays.asList(BLACKLISTED_OPEN_SL_ES_MODELS);
/* 177 */     return blackListedModels.contains(Build.MODEL);
/*     */   }
/*     */ 
/*     */   
/*     */   static void logDeviceInfo(String tag) {
/* 182 */     Logging.d(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", Release: " + Build.VERSION.RELEASE + ", Brand: " + Build.BRAND + ", Device: " + Build.DEVICE + ", Id: " + Build.ID + ", Hardware: " + Build.HARDWARE + ", Manufacturer: " + Build.MANUFACTURER + ", Model: " + Build.MODEL + ", Product: " + Build.PRODUCT);
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
/*     */   static void logAudioState(String tag) {
/* 197 */     logDeviceInfo(tag);
/* 198 */     Context context = ContextUtils.getApplicationContext();
/*     */     
/* 200 */     AudioManager audioManager = (AudioManager)context.getSystemService("audio");
/* 201 */     logAudioStateBasic(tag, audioManager);
/* 202 */     logAudioStateVolume(tag, audioManager);
/* 203 */     logAudioDeviceInfo(tag, audioManager);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void logAudioStateBasic(String tag, AudioManager audioManager) {
/* 208 */     Logging.d(tag, "Audio State: audio mode: " + 
/* 209 */         modeToString(audioManager.getMode()) + ", has mic: " + 
/* 210 */         hasMicrophone() + ", mic muted: " + audioManager
/* 211 */         .isMicrophoneMute() + ", music active: " + audioManager
/* 212 */         .isMusicActive() + ", speakerphone: " + audioManager
/* 213 */         .isSpeakerphoneOn() + ", BT SCO: " + audioManager
/* 214 */         .isBluetoothScoOn());
/*     */   }
/*     */   
/*     */   private static boolean isVolumeFixed(AudioManager audioManager) {
/* 218 */     if (Build.VERSION.SDK_INT < 21) {
/* 219 */       return false;
/*     */     }
/* 221 */     return audioManager.isVolumeFixed();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void logAudioStateVolume(String tag, AudioManager audioManager) {
/* 226 */     int[] streams = { 0, 3, 2, 4, 5, 1 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 234 */     Logging.d(tag, "Audio State: ");
/*     */     
/* 236 */     boolean fixedVolume = isVolumeFixed(audioManager);
/* 237 */     Logging.d(tag, "  fixed volume=" + fixedVolume);
/* 238 */     if (!fixedVolume) {
/* 239 */       for (int stream : streams) {
/* 240 */         StringBuilder info = new StringBuilder();
/* 241 */         info.append("  " + streamTypeToString(stream) + ": ");
/* 242 */         info.append("volume=").append(audioManager.getStreamVolume(stream));
/* 243 */         info.append(", max=").append(audioManager.getStreamMaxVolume(stream));
/* 244 */         logIsStreamMute(tag, audioManager, stream, info);
/* 245 */         Logging.d(tag, info.toString());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void logIsStreamMute(String tag, AudioManager audioManager, int stream, StringBuilder info) {
/* 252 */     if (Build.VERSION.SDK_INT >= 23) {
/* 253 */       info.append(", muted=").append(audioManager.isStreamMute(stream));
/*     */     }
/*     */   }
/*     */   
/*     */   private static void logAudioDeviceInfo(String tag, AudioManager audioManager) {
/* 258 */     if (Build.VERSION.SDK_INT < 23) {
/*     */       return;
/*     */     }
/*     */     
/* 262 */     AudioDeviceInfo[] devices = audioManager.getDevices(3);
/* 263 */     if (devices.length == 0) {
/*     */       return;
/*     */     }
/* 266 */     Logging.d(tag, "Audio Devices: ");
/* 267 */     for (AudioDeviceInfo device : devices) {
/* 268 */       StringBuilder info = new StringBuilder();
/* 269 */       info.append("  ").append(deviceTypeToString(device.getType()));
/* 270 */       info.append(device.isSource() ? "(in): " : "(out): ");
/*     */       
/* 272 */       if ((device.getChannelCounts()).length > 0) {
/* 273 */         info.append("channels=").append(Arrays.toString(device.getChannelCounts()));
/* 274 */         info.append(", ");
/*     */       } 
/* 276 */       if ((device.getEncodings()).length > 0) {
/*     */         
/* 278 */         info.append("encodings=").append(Arrays.toString(device.getEncodings()));
/* 279 */         info.append(", ");
/*     */       } 
/* 281 */       if ((device.getSampleRates()).length > 0) {
/* 282 */         info.append("sample rates=").append(Arrays.toString(device.getSampleRates()));
/* 283 */         info.append(", ");
/*     */       } 
/* 285 */       info.append("id=").append(device.getId());
/* 286 */       Logging.d(tag, info.toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static String modeToString(int mode) {
/* 292 */     switch (mode) {
/*     */       case 2:
/* 294 */         return "MODE_IN_CALL";
/*     */       case 3:
/* 296 */         return "MODE_IN_COMMUNICATION";
/*     */       case 0:
/* 298 */         return "MODE_NORMAL";
/*     */       case 1:
/* 300 */         return "MODE_RINGTONE";
/*     */     } 
/* 302 */     return "MODE_INVALID";
/*     */   }
/*     */ 
/*     */   
/*     */   private static String streamTypeToString(int stream) {
/* 307 */     switch (stream) {
/*     */       case 0:
/* 309 */         return "STREAM_VOICE_CALL";
/*     */       case 3:
/* 311 */         return "STREAM_MUSIC";
/*     */       case 2:
/* 313 */         return "STREAM_RING";
/*     */       case 4:
/* 315 */         return "STREAM_ALARM";
/*     */       case 5:
/* 317 */         return "STREAM_NOTIFICATION";
/*     */       case 1:
/* 319 */         return "STREAM_SYSTEM";
/*     */     } 
/* 321 */     return "STREAM_INVALID";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String deviceTypeToString(int type) {
/* 327 */     switch (type) {
/*     */       case 0:
/* 329 */         return "TYPE_UNKNOWN";
/*     */       case 1:
/* 331 */         return "TYPE_BUILTIN_EARPIECE";
/*     */       case 2:
/* 333 */         return "TYPE_BUILTIN_SPEAKER";
/*     */       case 3:
/* 335 */         return "TYPE_WIRED_HEADSET";
/*     */       case 4:
/* 337 */         return "TYPE_WIRED_HEADPHONES";
/*     */       case 5:
/* 339 */         return "TYPE_LINE_ANALOG";
/*     */       case 6:
/* 341 */         return "TYPE_LINE_DIGITAL";
/*     */       case 7:
/* 343 */         return "TYPE_BLUETOOTH_SCO";
/*     */       case 8:
/* 345 */         return "TYPE_BLUETOOTH_A2DP";
/*     */       case 9:
/* 347 */         return "TYPE_HDMI";
/*     */       case 10:
/* 349 */         return "TYPE_HDMI_ARC";
/*     */       case 11:
/* 351 */         return "TYPE_USB_DEVICE";
/*     */       case 12:
/* 353 */         return "TYPE_USB_ACCESSORY";
/*     */       case 13:
/* 355 */         return "TYPE_DOCK";
/*     */       case 14:
/* 357 */         return "TYPE_FM";
/*     */       case 15:
/* 359 */         return "TYPE_BUILTIN_MIC";
/*     */       case 16:
/* 361 */         return "TYPE_FM_TUNER";
/*     */       case 17:
/* 363 */         return "TYPE_TV_TUNER";
/*     */       case 18:
/* 365 */         return "TYPE_TELEPHONY";
/*     */       case 19:
/* 367 */         return "TYPE_AUX_LINE";
/*     */       case 20:
/* 369 */         return "TYPE_IP";
/*     */       case 21:
/* 371 */         return "TYPE_BUS";
/*     */       case 22:
/* 373 */         return "TYPE_USB_HEADSET";
/*     */     } 
/* 375 */     return "TYPE_UNKNOWN";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean hasMicrophone() {
/* 381 */     return ContextUtils.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.microphone");
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/voiceengine/WebRtcAudioUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */