/*     */ package org.webrtc;
/*     */ 
/*     */ import android.media.MediaCodecInfo;
/*     */ import android.media.MediaCodecList;
/*     */ import android.os.Build;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
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
/*     */ public class HardwareVideoEncoderFactory
/*     */   implements VideoEncoderFactory
/*     */ {
/*     */   private static final String TAG = "HardwareVideoEncoderFactory";
/*     */   private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_L_MS = 15000;
/*     */   private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS = 20000;
/*     */   private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_N_MS = 15000;
/*  39 */   private static final List<String> H264_HW_EXCEPTION_MODELS = Arrays.asList(new String[] { "SAMSUNG-SGH-I337", "Nexus 7", "Nexus 4" });
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final EglBase14.Context sharedContext;
/*     */ 
/*     */   
/*     */   private final boolean enableIntelVp8Encoder;
/*     */ 
/*     */   
/*     */   private final boolean enableH264HighProfile;
/*     */   
/*     */   @Nullable
/*     */   private final Predicate<MediaCodecInfo> codecAllowedPredicate;
/*     */ 
/*     */   
/*     */   public HardwareVideoEncoderFactory(EglBase.Context sharedContext, boolean enableIntelVp8Encoder, boolean enableH264HighProfile) {
/*  56 */     this(sharedContext, enableIntelVp8Encoder, enableH264HighProfile, null);
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
/*     */   public HardwareVideoEncoderFactory(EglBase.Context sharedContext, boolean enableIntelVp8Encoder, boolean enableH264HighProfile, @Nullable Predicate<MediaCodecInfo> codecAllowedPredicate) {
/*  73 */     if (sharedContext instanceof EglBase14.Context) {
/*  74 */       this.sharedContext = (EglBase14.Context)sharedContext;
/*     */     } else {
/*  76 */       Logging.w("HardwareVideoEncoderFactory", "No shared EglBase.Context.  Encoders will not use texture mode.");
/*  77 */       this.sharedContext = null;
/*     */     } 
/*  79 */     this.enableIntelVp8Encoder = enableIntelVp8Encoder;
/*  80 */     this.enableH264HighProfile = enableH264HighProfile;
/*  81 */     this.codecAllowedPredicate = codecAllowedPredicate;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public HardwareVideoEncoderFactory(boolean enableIntelVp8Encoder, boolean enableH264HighProfile) {
/*  86 */     this(null, enableIntelVp8Encoder, enableH264HighProfile);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public VideoEncoder createEncoder(VideoCodecInfo input) {
/*  93 */     if (Build.VERSION.SDK_INT < 19) {
/*  94 */       return null;
/*     */     }
/*     */     
/*  97 */     VideoCodecMimeType type = VideoCodecMimeType.valueOf(input.name);
/*  98 */     MediaCodecInfo info = findCodecForType(type);
/*     */     
/* 100 */     if (info == null) {
/* 101 */       return null;
/*     */     }
/*     */     
/* 104 */     String codecName = info.getName();
/* 105 */     String mime = type.mimeType();
/* 106 */     Integer surfaceColorFormat = MediaCodecUtils.selectColorFormat(MediaCodecUtils.TEXTURE_COLOR_FORMATS, info
/* 107 */         .getCapabilitiesForType(mime));
/* 108 */     Integer yuvColorFormat = MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, info
/* 109 */         .getCapabilitiesForType(mime));
/*     */     
/* 111 */     if (type == VideoCodecMimeType.H264) {
/* 112 */       boolean isHighProfile = H264Utils.isSameH264Profile(input.params, 
/* 113 */           MediaCodecUtils.getCodecProperties(type, true));
/* 114 */       boolean isBaselineProfile = H264Utils.isSameH264Profile(input.params, 
/* 115 */           MediaCodecUtils.getCodecProperties(type, false));
/*     */       
/* 117 */       if (!isHighProfile && !isBaselineProfile) {
/* 118 */         return null;
/*     */       }
/* 120 */       if (isHighProfile && !isH264HighProfileSupported(info)) {
/* 121 */         return null;
/*     */       }
/*     */     } 
/*     */     
/* 125 */     return new HardwareVideoEncoder(new MediaCodecWrapperFactoryImpl(), codecName, type, surfaceColorFormat, yuvColorFormat, input.params, 
/* 126 */         getKeyFrameIntervalSec(type), 
/* 127 */         getForcedKeyFrameIntervalMs(type, codecName), createBitrateAdjuster(type, codecName), this.sharedContext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VideoCodecInfo[] getSupportedCodecs() {
/* 134 */     if (Build.VERSION.SDK_INT < 19) {
/* 135 */       return new VideoCodecInfo[0];
/*     */     }
/*     */     
/* 138 */     List<VideoCodecInfo> supportedCodecInfos = new ArrayList<>();
/*     */ 
/*     */     
/* 141 */     for (VideoCodecMimeType type : new VideoCodecMimeType[] { VideoCodecMimeType.VP8, VideoCodecMimeType.VP9, VideoCodecMimeType.H264 }) {
/*     */       
/* 143 */       MediaCodecInfo codec = findCodecForType(type);
/* 144 */       if (codec != null) {
/* 145 */         String name = type.name();
/*     */ 
/*     */         
/* 148 */         if (type == VideoCodecMimeType.H264 && isH264HighProfileSupported(codec)) {
/* 149 */           supportedCodecInfos.add(new VideoCodecInfo(name, 
/* 150 */                 MediaCodecUtils.getCodecProperties(type, true)));
/*     */         }
/*     */         
/* 153 */         supportedCodecInfos.add(new VideoCodecInfo(name, 
/* 154 */               MediaCodecUtils.getCodecProperties(type, false)));
/*     */       } 
/*     */     } 
/*     */     
/* 158 */     return supportedCodecInfos.<VideoCodecInfo>toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
/*     */   }
/*     */   @Nullable
/*     */   private MediaCodecInfo findCodecForType(VideoCodecMimeType type) {
/* 162 */     for (int i = 0; i < MediaCodecList.getCodecCount(); i++) {
/* 163 */       MediaCodecInfo info = null;
/*     */       try {
/* 165 */         info = MediaCodecList.getCodecInfoAt(i);
/* 166 */       } catch (IllegalArgumentException e) {
/* 167 */         Logging.e("HardwareVideoEncoderFactory", "Cannot retrieve encoder codec info", e);
/*     */       } 
/*     */       
/* 170 */       if (info != null && info.isEncoder())
/*     */       {
/*     */ 
/*     */         
/* 174 */         if (isSupportedCodec(info, type))
/* 175 */           return info; 
/*     */       }
/*     */     } 
/* 178 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isSupportedCodec(MediaCodecInfo info, VideoCodecMimeType type) {
/* 183 */     if (!MediaCodecUtils.codecSupportsType(info, type)) {
/* 184 */       return false;
/*     */     }
/*     */     
/* 187 */     if (MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, info
/* 188 */         .getCapabilitiesForType(type.mimeType())) == null)
/*     */     {
/* 190 */       return false;
/*     */     }
/* 192 */     return (isHardwareSupportedInCurrentSdk(info, type) && isMediaCodecAllowed(info));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isHardwareSupportedInCurrentSdk(MediaCodecInfo info, VideoCodecMimeType type) {
/* 198 */     switch (type) {
/*     */       case VP8:
/* 200 */         return isHardwareSupportedInCurrentSdkVp8(info);
/*     */       case VP9:
/* 202 */         return isHardwareSupportedInCurrentSdkVp9(info);
/*     */       case H264:
/* 204 */         return isHardwareSupportedInCurrentSdkH264(info);
/*     */     } 
/* 206 */     return false;
/*     */   }
/*     */   
/*     */   private boolean isHardwareSupportedInCurrentSdkVp8(MediaCodecInfo info) {
/* 210 */     String name = info.getName();
/*     */     
/* 212 */     return ((name.startsWith("OMX.qcom.") && Build.VERSION.SDK_INT >= 19) || (name
/*     */       
/* 214 */       .startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 23) || (name
/*     */       
/* 216 */       .startsWith("OMX.Intel.") && Build.VERSION.SDK_INT >= 21 && this.enableIntelVp8Encoder));
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isHardwareSupportedInCurrentSdkVp9(MediaCodecInfo info) {
/* 221 */     String name = info.getName();
/* 222 */     return ((name.startsWith("OMX.qcom.") || name.startsWith("OMX.Exynos.")) && Build.VERSION.SDK_INT >= 24);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isHardwareSupportedInCurrentSdkH264(MediaCodecInfo info) {
/* 229 */     if (H264_HW_EXCEPTION_MODELS.contains(Build.MODEL)) {
/* 230 */       return false;
/*     */     }
/* 232 */     String name = info.getName();
/*     */     
/* 234 */     return ((name.startsWith("OMX.qcom.") && Build.VERSION.SDK_INT >= 19) || (name
/*     */       
/* 236 */       .startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 21));
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isMediaCodecAllowed(MediaCodecInfo info) {
/* 241 */     if (this.codecAllowedPredicate == null) {
/* 242 */       return true;
/*     */     }
/* 244 */     return this.codecAllowedPredicate.test(info);
/*     */   }
/*     */   
/*     */   private int getKeyFrameIntervalSec(VideoCodecMimeType type) {
/* 248 */     switch (type) {
/*     */       case VP8:
/*     */       case VP9:
/* 251 */         return 100;
/*     */       case H264:
/* 253 */         return 20;
/*     */     } 
/* 255 */     throw new IllegalArgumentException("Unsupported VideoCodecMimeType " + type);
/*     */   }
/*     */   
/*     */   private int getForcedKeyFrameIntervalMs(VideoCodecMimeType type, String codecName) {
/* 259 */     if (type == VideoCodecMimeType.VP8 && codecName.startsWith("OMX.qcom.")) {
/* 260 */       if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22)
/*     */       {
/* 262 */         return 15000; } 
/* 263 */       if (Build.VERSION.SDK_INT == 23)
/* 264 */         return 20000; 
/* 265 */       if (Build.VERSION.SDK_INT > 23) {
/* 266 */         return 15000;
/*     */       }
/*     */     } 
/*     */     
/* 270 */     return 0;
/*     */   }
/*     */   
/*     */   private BitrateAdjuster createBitrateAdjuster(VideoCodecMimeType type, String codecName) {
/* 274 */     if (codecName.startsWith("OMX.Exynos.")) {
/* 275 */       if (type == VideoCodecMimeType.VP8)
/*     */       {
/* 277 */         return new DynamicBitrateAdjuster();
/*     */       }
/*     */       
/* 280 */       return new FramerateBitrateAdjuster();
/*     */     } 
/*     */ 
/*     */     
/* 284 */     return new BaseBitrateAdjuster();
/*     */   }
/*     */   
/*     */   private boolean isH264HighProfileSupported(MediaCodecInfo info) {
/* 288 */     return (this.enableH264HighProfile && Build.VERSION.SDK_INT > 23 && info
/* 289 */       .getName().startsWith("OMX.Exynos."));
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/HardwareVideoEncoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */