/*     */ package org.webrtc.voiceengine;
/*     */ 
/*     */ import android.media.audiofx.AcousticEchoCanceler;
/*     */ import android.media.audiofx.AudioEffect;
/*     */ import android.media.audiofx.NoiseSuppressor;
/*     */ import android.os.Build;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
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
/*     */ public class WebRtcAudioEffects
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private static final String TAG = "WebRtcAudioEffects";
/*  35 */   private static final UUID AOSP_ACOUSTIC_ECHO_CANCELER = UUID.fromString("bb392ec0-8d4d-11e0-a896-0002a5d5c51b");
/*     */   
/*  37 */   private static final UUID AOSP_NOISE_SUPPRESSOR = UUID.fromString("c06c8400-8e06-11e0-9cb6-0002a5d5c51b");
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static AudioEffect.Descriptor[] cachedEffects;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private AcousticEchoCanceler aec;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private NoiseSuppressor ns;
/*     */ 
/*     */   
/*     */   private boolean shouldEnableAec;
/*     */ 
/*     */   
/*     */   private boolean shouldEnableNs;
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAcousticEchoCancelerSupported() {
/*  63 */     return isAcousticEchoCancelerEffectAvailable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNoiseSuppressorSupported() {
/*  72 */     return isNoiseSuppressorEffectAvailable();
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAcousticEchoCancelerBlacklisted() {
/*  77 */     List<String> blackListedModels = WebRtcAudioUtils.getBlackListedModelsForAecUsage();
/*  78 */     boolean isBlacklisted = blackListedModels.contains(Build.MODEL);
/*  79 */     if (isBlacklisted) {
/*  80 */       Logging.w("WebRtcAudioEffects", Build.MODEL + " is blacklisted for HW AEC usage!");
/*     */     }
/*  82 */     return isBlacklisted;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isNoiseSuppressorBlacklisted() {
/*  87 */     List<String> blackListedModels = WebRtcAudioUtils.getBlackListedModelsForNsUsage();
/*  88 */     boolean isBlacklisted = blackListedModels.contains(Build.MODEL);
/*  89 */     if (isBlacklisted) {
/*  90 */       Logging.w("WebRtcAudioEffects", Build.MODEL + " is blacklisted for HW NS usage!");
/*     */     }
/*  92 */     return isBlacklisted;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isAcousticEchoCancelerExcludedByUUID() {
/*  98 */     if (Build.VERSION.SDK_INT < 18)
/*  99 */       return false; 
/* 100 */     for (AudioEffect.Descriptor d : getAvailableEffects()) {
/* 101 */       if (d.type.equals(AudioEffect.EFFECT_TYPE_AEC) && d.uuid
/* 102 */         .equals(AOSP_ACOUSTIC_ECHO_CANCELER)) {
/* 103 */         return true;
/*     */       }
/*     */     } 
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isNoiseSuppressorExcludedByUUID() {
/* 112 */     if (Build.VERSION.SDK_INT < 18)
/* 113 */       return false; 
/* 114 */     for (AudioEffect.Descriptor d : getAvailableEffects()) {
/* 115 */       if (d.type.equals(AudioEffect.EFFECT_TYPE_NS) && d.uuid.equals(AOSP_NOISE_SUPPRESSOR)) {
/* 116 */         return true;
/*     */       }
/*     */     } 
/* 119 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isAcousticEchoCancelerEffectAvailable() {
/* 124 */     if (Build.VERSION.SDK_INT < 18)
/* 125 */       return false; 
/* 126 */     return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AEC);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isNoiseSuppressorEffectAvailable() {
/* 131 */     if (Build.VERSION.SDK_INT < 18)
/* 132 */       return false; 
/* 133 */     return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean canUseAcousticEchoCanceler() {
/* 141 */     boolean canUseAcousticEchoCanceler = (isAcousticEchoCancelerSupported() && !WebRtcAudioUtils.useWebRtcBasedAcousticEchoCanceler() && !isAcousticEchoCancelerBlacklisted() && !isAcousticEchoCancelerExcludedByUUID());
/* 142 */     Logging.d("WebRtcAudioEffects", "canUseAcousticEchoCanceler: " + canUseAcousticEchoCanceler);
/* 143 */     return canUseAcousticEchoCanceler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean canUseNoiseSuppressor() {
/* 151 */     boolean canUseNoiseSuppressor = (isNoiseSuppressorSupported() && !WebRtcAudioUtils.useWebRtcBasedNoiseSuppressor() && !isNoiseSuppressorBlacklisted() && !isNoiseSuppressorExcludedByUUID());
/* 152 */     Logging.d("WebRtcAudioEffects", "canUseNoiseSuppressor: " + canUseNoiseSuppressor);
/* 153 */     return canUseNoiseSuppressor;
/*     */   }
/*     */   
/*     */   public static WebRtcAudioEffects create() {
/* 157 */     return new WebRtcAudioEffects();
/*     */   }
/*     */   
/*     */   private WebRtcAudioEffects() {
/* 161 */     Logging.d("WebRtcAudioEffects", "ctor" + WebRtcAudioUtils.getThreadInfo());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setAEC(boolean enable) {
/* 169 */     Logging.d("WebRtcAudioEffects", "setAEC(" + enable + ")");
/* 170 */     if (!canUseAcousticEchoCanceler()) {
/* 171 */       Logging.w("WebRtcAudioEffects", "Platform AEC is not supported");
/* 172 */       this.shouldEnableAec = false;
/* 173 */       return false;
/*     */     } 
/* 175 */     if (this.aec != null && enable != this.shouldEnableAec) {
/* 176 */       Logging.e("WebRtcAudioEffects", "Platform AEC state can't be modified while recording");
/* 177 */       return false;
/*     */     } 
/* 179 */     this.shouldEnableAec = enable;
/* 180 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setNS(boolean enable) {
/* 188 */     Logging.d("WebRtcAudioEffects", "setNS(" + enable + ")");
/* 189 */     if (!canUseNoiseSuppressor()) {
/* 190 */       Logging.w("WebRtcAudioEffects", "Platform NS is not supported");
/* 191 */       this.shouldEnableNs = false;
/* 192 */       return false;
/*     */     } 
/* 194 */     if (this.ns != null && enable != this.shouldEnableNs) {
/* 195 */       Logging.e("WebRtcAudioEffects", "Platform NS state can't be modified while recording");
/* 196 */       return false;
/*     */     } 
/* 198 */     this.shouldEnableNs = enable;
/* 199 */     return true;
/*     */   }
/*     */   
/*     */   public void enable(int audioSession) {
/* 203 */     Logging.d("WebRtcAudioEffects", "enable(audioSession=" + audioSession + ")");
/* 204 */     assertTrue((this.aec == null));
/* 205 */     assertTrue((this.ns == null));
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
/* 221 */     if (isAcousticEchoCancelerSupported()) {
/*     */ 
/*     */       
/* 224 */       this.aec = AcousticEchoCanceler.create(audioSession);
/* 225 */       if (this.aec != null) {
/* 226 */         boolean enabled = this.aec.getEnabled();
/* 227 */         boolean enable = (this.shouldEnableAec && canUseAcousticEchoCanceler());
/* 228 */         if (this.aec.setEnabled(enable) != 0) {
/* 229 */           Logging.e("WebRtcAudioEffects", "Failed to set the AcousticEchoCanceler state");
/*     */         }
/* 231 */         Logging.d("WebRtcAudioEffects", "AcousticEchoCanceler: was " + (enabled ? "enabled" : "disabled") + ", enable: " + enable + ", is now: " + (
/*     */             
/* 233 */             this.aec.getEnabled() ? "enabled" : "disabled"));
/*     */       } else {
/* 235 */         Logging.e("WebRtcAudioEffects", "Failed to create the AcousticEchoCanceler instance");
/*     */       } 
/*     */     } 
/*     */     
/* 239 */     if (isNoiseSuppressorSupported()) {
/*     */ 
/*     */       
/* 242 */       this.ns = NoiseSuppressor.create(audioSession);
/* 243 */       if (this.ns != null) {
/* 244 */         boolean enabled = this.ns.getEnabled();
/* 245 */         boolean enable = (this.shouldEnableNs && canUseNoiseSuppressor());
/* 246 */         if (this.ns.setEnabled(enable) != 0) {
/* 247 */           Logging.e("WebRtcAudioEffects", "Failed to set the NoiseSuppressor state");
/*     */         }
/* 249 */         Logging.d("WebRtcAudioEffects", "NoiseSuppressor: was " + (enabled ? "enabled" : "disabled") + ", enable: " + enable + ", is now: " + (
/* 250 */             this.ns.getEnabled() ? "enabled" : "disabled"));
/*     */       } else {
/* 252 */         Logging.e("WebRtcAudioEffects", "Failed to create the NoiseSuppressor instance");
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/* 261 */     Logging.d("WebRtcAudioEffects", "release");
/* 262 */     if (this.aec != null) {
/* 263 */       this.aec.release();
/* 264 */       this.aec = null;
/*     */     } 
/* 266 */     if (this.ns != null) {
/* 267 */       this.ns.release();
/* 268 */       this.ns = null;
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
/*     */   private boolean effectTypeIsVoIP(UUID type) {
/* 280 */     if (Build.VERSION.SDK_INT < 18) {
/* 281 */       return false;
/*     */     }
/* 283 */     return ((AudioEffect.EFFECT_TYPE_AEC.equals(type) && isAcousticEchoCancelerSupported()) || (AudioEffect.EFFECT_TYPE_NS
/* 284 */       .equals(type) && isNoiseSuppressorSupported()));
/*     */   }
/*     */ 
/*     */   
/*     */   private static void assertTrue(boolean condition) {
/* 289 */     if (!condition) {
/* 290 */       throw new AssertionError("Expected condition to be true");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static AudioEffect.Descriptor[] getAvailableEffects() {
/* 297 */     if (cachedEffects != null) {
/* 298 */       return cachedEffects;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 304 */     cachedEffects = AudioEffect.queryEffects();
/* 305 */     return cachedEffects;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isEffectTypeAvailable(UUID effectType) {
/* 312 */     AudioEffect.Descriptor[] effects = getAvailableEffects();
/* 313 */     if (effects == null) {
/* 314 */       return false;
/*     */     }
/* 316 */     for (AudioEffect.Descriptor d : effects) {
/* 317 */       if (d.type.equals(effectType)) {
/* 318 */         return true;
/*     */       }
/*     */     } 
/* 321 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/voiceengine/WebRtcAudioEffects.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */