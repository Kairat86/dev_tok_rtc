/*     */ package org.webrtc.audio;
/*     */ 
/*     */ import android.media.audiofx.AcousticEchoCanceler;
/*     */ import android.media.audiofx.AudioEffect;
/*     */ import android.media.audiofx.NoiseSuppressor;
/*     */ import android.os.Build;
/*     */ import androidx.annotation.Nullable;
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
/*     */ class WebRtcAudioEffects
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private static final String TAG = "WebRtcAudioEffectsExternal";
/*  34 */   private static final UUID AOSP_ACOUSTIC_ECHO_CANCELER = UUID.fromString("bb392ec0-8d4d-11e0-a896-0002a5d5c51b");
/*     */   
/*  36 */   private static final UUID AOSP_NOISE_SUPPRESSOR = UUID.fromString("c06c8400-8e06-11e0-9cb6-0002a5d5c51b");
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static AudioEffect.Descriptor[] cachedEffects;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private AcousticEchoCanceler aec;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private NoiseSuppressor ns;
/*     */ 
/*     */   
/*     */   private boolean shouldEnableAec;
/*     */   
/*     */   private boolean shouldEnableNs;
/*     */ 
/*     */   
/*     */   public static boolean isAcousticEchoCancelerSupported() {
/*  57 */     if (Build.VERSION.SDK_INT < 18)
/*  58 */       return false; 
/*  59 */     return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AEC, AOSP_ACOUSTIC_ECHO_CANCELER);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isNoiseSuppressorSupported() {
/*  64 */     if (Build.VERSION.SDK_INT < 18)
/*  65 */       return false; 
/*  66 */     return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS, AOSP_NOISE_SUPPRESSOR);
/*     */   }
/*     */   
/*     */   public WebRtcAudioEffects() {
/*  70 */     Logging.d("WebRtcAudioEffectsExternal", "ctor" + WebRtcAudioUtils.getThreadInfo());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setAEC(boolean enable) {
/*  78 */     Logging.d("WebRtcAudioEffectsExternal", "setAEC(" + enable + ")");
/*  79 */     if (!isAcousticEchoCancelerSupported()) {
/*  80 */       Logging.w("WebRtcAudioEffectsExternal", "Platform AEC is not supported");
/*  81 */       this.shouldEnableAec = false;
/*  82 */       return false;
/*     */     } 
/*  84 */     if (this.aec != null && enable != this.shouldEnableAec) {
/*  85 */       Logging.e("WebRtcAudioEffectsExternal", "Platform AEC state can't be modified while recording");
/*  86 */       return false;
/*     */     } 
/*  88 */     this.shouldEnableAec = enable;
/*  89 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setNS(boolean enable) {
/*  97 */     Logging.d("WebRtcAudioEffectsExternal", "setNS(" + enable + ")");
/*  98 */     if (!isNoiseSuppressorSupported()) {
/*  99 */       Logging.w("WebRtcAudioEffectsExternal", "Platform NS is not supported");
/* 100 */       this.shouldEnableNs = false;
/* 101 */       return false;
/*     */     } 
/* 103 */     if (this.ns != null && enable != this.shouldEnableNs) {
/* 104 */       Logging.e("WebRtcAudioEffectsExternal", "Platform NS state can't be modified while recording");
/* 105 */       return false;
/*     */     } 
/* 107 */     this.shouldEnableNs = enable;
/* 108 */     return true;
/*     */   }
/*     */   
/*     */   public void enable(int audioSession) {
/* 112 */     Logging.d("WebRtcAudioEffectsExternal", "enable(audioSession=" + audioSession + ")");
/* 113 */     assertTrue((this.aec == null));
/* 114 */     assertTrue((this.ns == null));
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
/* 131 */     if (isAcousticEchoCancelerSupported()) {
/*     */ 
/*     */       
/* 134 */       this.aec = AcousticEchoCanceler.create(audioSession);
/* 135 */       if (this.aec != null) {
/* 136 */         boolean enabled = this.aec.getEnabled();
/* 137 */         boolean enable = (this.shouldEnableAec && isAcousticEchoCancelerSupported());
/* 138 */         if (this.aec.setEnabled(enable) != 0) {
/* 139 */           Logging.e("WebRtcAudioEffectsExternal", "Failed to set the AcousticEchoCanceler state");
/*     */         }
/* 141 */         Logging.d("WebRtcAudioEffectsExternal", "AcousticEchoCanceler: was " + (
/* 142 */             enabled ? "enabled" : "disabled") + ", enable: " + enable + ", is now: " + (
/* 143 */             this.aec.getEnabled() ? "enabled" : "disabled"));
/*     */       } else {
/* 145 */         Logging.e("WebRtcAudioEffectsExternal", "Failed to create the AcousticEchoCanceler instance");
/*     */       } 
/*     */     } 
/*     */     
/* 149 */     if (isNoiseSuppressorSupported()) {
/*     */ 
/*     */       
/* 152 */       this.ns = NoiseSuppressor.create(audioSession);
/* 153 */       if (this.ns != null) {
/* 154 */         boolean enabled = this.ns.getEnabled();
/* 155 */         boolean enable = (this.shouldEnableNs && isNoiseSuppressorSupported());
/* 156 */         if (this.ns.setEnabled(enable) != 0) {
/* 157 */           Logging.e("WebRtcAudioEffectsExternal", "Failed to set the NoiseSuppressor state");
/*     */         }
/* 159 */         Logging.d("WebRtcAudioEffectsExternal", "NoiseSuppressor: was " + (
/* 160 */             enabled ? "enabled" : "disabled") + ", enable: " + enable + ", is now: " + (
/* 161 */             this.ns.getEnabled() ? "enabled" : "disabled"));
/*     */       } else {
/* 163 */         Logging.e("WebRtcAudioEffectsExternal", "Failed to create the NoiseSuppressor instance");
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/* 172 */     Logging.d("WebRtcAudioEffectsExternal", "release");
/* 173 */     if (this.aec != null) {
/* 174 */       this.aec.release();
/* 175 */       this.aec = null;
/*     */     } 
/* 177 */     if (this.ns != null) {
/* 178 */       this.ns.release();
/* 179 */       this.ns = null;
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
/* 191 */     if (Build.VERSION.SDK_INT < 18) {
/* 192 */       return false;
/*     */     }
/* 194 */     return ((AudioEffect.EFFECT_TYPE_AEC.equals(type) && isAcousticEchoCancelerSupported()) || (AudioEffect.EFFECT_TYPE_NS
/* 195 */       .equals(type) && isNoiseSuppressorSupported()));
/*     */   }
/*     */ 
/*     */   
/*     */   private static void assertTrue(boolean condition) {
/* 200 */     if (!condition) {
/* 201 */       throw new AssertionError("Expected condition to be true");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static AudioEffect.Descriptor[] getAvailableEffects() {
/* 208 */     if (cachedEffects != null) {
/* 209 */       return cachedEffects;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 215 */     cachedEffects = AudioEffect.queryEffects();
/* 216 */     return cachedEffects;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isEffectTypeAvailable(UUID effectType, UUID blockListedUuid) {
/* 223 */     AudioEffect.Descriptor[] effects = getAvailableEffects();
/* 224 */     if (effects == null) {
/* 225 */       return false;
/*     */     }
/* 227 */     for (AudioEffect.Descriptor d : effects) {
/* 228 */       if (d.type.equals(effectType)) {
/* 229 */         return !d.uuid.equals(blockListedUuid);
/*     */       }
/*     */     } 
/* 232 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/audio/WebRtcAudioEffects.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */