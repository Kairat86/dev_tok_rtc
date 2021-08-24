/*     */ package org.webrtc.audio;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.content.Context;
/*     */ import android.media.AudioDeviceInfo;
/*     */ import android.media.AudioManager;
/*     */ import android.os.Build;
/*     */ import java.util.Arrays;
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
/*     */ final class WebRtcAudioUtils
/*     */ {
/*     */   private static final String TAG = "WebRtcAudioUtilsExternal";
/*     */   
/*     */   public static String getThreadInfo() {
/*  35 */     return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean runningOnEmulator() {
/*  41 */     return (Build.HARDWARE.equals("goldfish") && Build.BRAND.startsWith("generic_"));
/*     */   }
/*     */ 
/*     */   
/*     */   static void logDeviceInfo(String tag) {
/*  46 */     Logging.d(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", Release: " + Build.VERSION.RELEASE + ", Brand: " + Build.BRAND + ", Device: " + Build.DEVICE + ", Id: " + Build.ID + ", Hardware: " + Build.HARDWARE + ", Manufacturer: " + Build.MANUFACTURER + ", Model: " + Build.MODEL + ", Product: " + Build.PRODUCT);
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
/*     */   static void logAudioState(String tag, Context context, AudioManager audioManager) {
/*  62 */     logDeviceInfo(tag);
/*  63 */     logAudioStateBasic(tag, context, audioManager);
/*  64 */     logAudioStateVolume(tag, audioManager);
/*  65 */     logAudioDeviceInfo(tag, audioManager);
/*     */   }
/*     */ 
/*     */   
/*     */   static String deviceTypeToString(int type) {
/*  70 */     switch (type) {
/*     */       case 0:
/*  72 */         return "TYPE_UNKNOWN";
/*     */       case 1:
/*  74 */         return "TYPE_BUILTIN_EARPIECE";
/*     */       case 2:
/*  76 */         return "TYPE_BUILTIN_SPEAKER";
/*     */       case 3:
/*  78 */         return "TYPE_WIRED_HEADSET";
/*     */       case 4:
/*  80 */         return "TYPE_WIRED_HEADPHONES";
/*     */       case 5:
/*  82 */         return "TYPE_LINE_ANALOG";
/*     */       case 6:
/*  84 */         return "TYPE_LINE_DIGITAL";
/*     */       case 7:
/*  86 */         return "TYPE_BLUETOOTH_SCO";
/*     */       case 8:
/*  88 */         return "TYPE_BLUETOOTH_A2DP";
/*     */       case 9:
/*  90 */         return "TYPE_HDMI";
/*     */       case 10:
/*  92 */         return "TYPE_HDMI_ARC";
/*     */       case 11:
/*  94 */         return "TYPE_USB_DEVICE";
/*     */       case 12:
/*  96 */         return "TYPE_USB_ACCESSORY";
/*     */       case 13:
/*  98 */         return "TYPE_DOCK";
/*     */       case 14:
/* 100 */         return "TYPE_FM";
/*     */       case 15:
/* 102 */         return "TYPE_BUILTIN_MIC";
/*     */       case 16:
/* 104 */         return "TYPE_FM_TUNER";
/*     */       case 17:
/* 106 */         return "TYPE_TV_TUNER";
/*     */       case 18:
/* 108 */         return "TYPE_TELEPHONY";
/*     */       case 19:
/* 110 */         return "TYPE_AUX_LINE";
/*     */       case 20:
/* 112 */         return "TYPE_IP";
/*     */       case 21:
/* 114 */         return "TYPE_BUS";
/*     */       case 22:
/* 116 */         return "TYPE_USB_HEADSET";
/*     */     } 
/* 118 */     return "TYPE_UNKNOWN";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @TargetApi(24)
/*     */   public static String audioSourceToString(int source) {
/* 125 */     int VOICE_PERFORMANCE = 10;
/* 126 */     switch (source) {
/*     */       case 0:
/* 128 */         return "DEFAULT";
/*     */       case 1:
/* 130 */         return "MIC";
/*     */       case 2:
/* 132 */         return "VOICE_UPLINK";
/*     */       case 3:
/* 134 */         return "VOICE_DOWNLINK";
/*     */       case 4:
/* 136 */         return "VOICE_CALL";
/*     */       case 5:
/* 138 */         return "CAMCORDER";
/*     */       case 6:
/* 140 */         return "VOICE_RECOGNITION";
/*     */       case 7:
/* 142 */         return "VOICE_COMMUNICATION";
/*     */       case 9:
/* 144 */         return "UNPROCESSED";
/*     */       case 10:
/* 146 */         return "VOICE_PERFORMANCE";
/*     */     } 
/* 148 */     return "INVALID";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String channelMaskToString(int mask) {
/* 156 */     switch (mask) {
/*     */       case 12:
/* 158 */         return "IN_STEREO";
/*     */       case 16:
/* 160 */         return "IN_MONO";
/*     */     } 
/* 162 */     return "INVALID";
/*     */   }
/*     */ 
/*     */   
/*     */   @TargetApi(24)
/*     */   public static String audioEncodingToString(int enc) {
/* 168 */     switch (enc) {
/*     */       case 0:
/* 170 */         return "INVALID";
/*     */       case 2:
/* 172 */         return "PCM_16BIT";
/*     */       case 3:
/* 174 */         return "PCM_8BIT";
/*     */       case 4:
/* 176 */         return "PCM_FLOAT";
/*     */       case 5:
/* 178 */         return "AC3";
/*     */       case 6:
/* 180 */         return "AC3";
/*     */       case 7:
/* 182 */         return "DTS";
/*     */       case 8:
/* 184 */         return "DTS_HD";
/*     */       case 9:
/* 186 */         return "MP3";
/*     */     } 
/* 188 */     return "Invalid encoding: " + enc;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void logAudioStateBasic(String tag, Context context, AudioManager audioManager) {
/* 194 */     Logging.d(tag, "Audio State: audio mode: " + 
/*     */         
/* 196 */         modeToString(audioManager.getMode()) + ", has mic: " + 
/* 197 */         hasMicrophone(context) + ", mic muted: " + audioManager
/* 198 */         .isMicrophoneMute() + ", music active: " + audioManager
/* 199 */         .isMusicActive() + ", speakerphone: " + audioManager
/* 200 */         .isSpeakerphoneOn() + ", BT SCO: " + audioManager
/* 201 */         .isBluetoothScoOn());
/*     */   }
/*     */   
/*     */   private static boolean isVolumeFixed(AudioManager audioManager) {
/* 205 */     if (Build.VERSION.SDK_INT < 21) {
/* 206 */       return false;
/*     */     }
/* 208 */     return audioManager.isVolumeFixed();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void logAudioStateVolume(String tag, AudioManager audioManager) {
/* 213 */     int[] streams = { 0, 3, 2, 4, 5, 1 };
/*     */ 
/*     */     
/* 216 */     Logging.d(tag, "Audio State: ");
/*     */     
/* 218 */     boolean fixedVolume = isVolumeFixed(audioManager);
/* 219 */     Logging.d(tag, "  fixed volume=" + fixedVolume);
/* 220 */     if (!fixedVolume) {
/* 221 */       for (int stream : streams) {
/* 222 */         StringBuilder info = new StringBuilder();
/* 223 */         info.append("  " + streamTypeToString(stream) + ": ");
/* 224 */         info.append("volume=").append(audioManager.getStreamVolume(stream));
/* 225 */         info.append(", max=").append(audioManager.getStreamMaxVolume(stream));
/* 226 */         logIsStreamMute(tag, audioManager, stream, info);
/* 227 */         Logging.d(tag, info.toString());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void logIsStreamMute(String tag, AudioManager audioManager, int stream, StringBuilder info) {
/* 234 */     if (Build.VERSION.SDK_INT >= 23) {
/* 235 */       info.append(", muted=").append(audioManager.isStreamMute(stream));
/*     */     }
/*     */   }
/*     */   
/*     */   private static void logAudioDeviceInfo(String tag, AudioManager audioManager) {
/* 240 */     if (Build.VERSION.SDK_INT < 23) {
/*     */       return;
/*     */     }
/* 243 */     AudioDeviceInfo[] devices = audioManager.getDevices(3);
/* 244 */     if (devices.length == 0) {
/*     */       return;
/*     */     }
/* 247 */     Logging.d(tag, "Audio Devices: ");
/* 248 */     for (AudioDeviceInfo device : devices) {
/* 249 */       StringBuilder info = new StringBuilder();
/* 250 */       info.append("  ").append(deviceTypeToString(device.getType()));
/* 251 */       info.append(device.isSource() ? "(in): " : "(out): ");
/*     */       
/* 253 */       if ((device.getChannelCounts()).length > 0) {
/* 254 */         info.append("channels=").append(Arrays.toString(device.getChannelCounts()));
/* 255 */         info.append(", ");
/*     */       } 
/* 257 */       if ((device.getEncodings()).length > 0) {
/*     */         
/* 259 */         info.append("encodings=").append(Arrays.toString(device.getEncodings()));
/* 260 */         info.append(", ");
/*     */       } 
/* 262 */       if ((device.getSampleRates()).length > 0) {
/* 263 */         info.append("sample rates=").append(Arrays.toString(device.getSampleRates()));
/* 264 */         info.append(", ");
/*     */       } 
/* 266 */       info.append("id=").append(device.getId());
/* 267 */       Logging.d(tag, info.toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static String modeToString(int mode) {
/* 273 */     switch (mode) {
/*     */       case 2:
/* 275 */         return "MODE_IN_CALL";
/*     */       case 3:
/* 277 */         return "MODE_IN_COMMUNICATION";
/*     */       case 0:
/* 279 */         return "MODE_NORMAL";
/*     */       case 1:
/* 281 */         return "MODE_RINGTONE";
/*     */     } 
/* 283 */     return "MODE_INVALID";
/*     */   }
/*     */ 
/*     */   
/*     */   private static String streamTypeToString(int stream) {
/* 288 */     switch (stream) {
/*     */       case 0:
/* 290 */         return "STREAM_VOICE_CALL";
/*     */       case 3:
/* 292 */         return "STREAM_MUSIC";
/*     */       case 2:
/* 294 */         return "STREAM_RING";
/*     */       case 4:
/* 296 */         return "STREAM_ALARM";
/*     */       case 5:
/* 298 */         return "STREAM_NOTIFICATION";
/*     */       case 1:
/* 300 */         return "STREAM_SYSTEM";
/*     */     } 
/* 302 */     return "STREAM_INVALID";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean hasMicrophone(Context context) {
/* 308 */     return context.getPackageManager().hasSystemFeature("android.hardware.microphone");
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/audio/WebRtcAudioUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */