/*     */ package org.webrtc;
/*     */ 
/*     */ import android.media.MediaCodecInfo;
/*     */ import android.media.MediaCodecList;
/*     */ import android.os.Build;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ class MediaCodecVideoDecoderFactory
/*     */   implements VideoDecoderFactory
/*     */ {
/*     */   private static final String TAG = "MediaCodecVideoDecoderFactory";
/*     */   @Nullable
/*     */   private final EglBase.Context sharedContext;
/*     */   @Nullable
/*     */   private final Predicate<MediaCodecInfo> codecAllowedPredicate;
/*     */   
/*     */   public MediaCodecVideoDecoderFactory(@Nullable EglBase.Context sharedContext, @Nullable Predicate<MediaCodecInfo> codecAllowedPredicate) {
/*  42 */     this.sharedContext = sharedContext;
/*  43 */     this.codecAllowedPredicate = codecAllowedPredicate;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public VideoDecoder createDecoder(VideoCodecInfo codecType) {
/*  49 */     VideoCodecMimeType type = VideoCodecMimeType.valueOf(codecType.getName());
/*  50 */     MediaCodecInfo info = findCodecForType(type);
/*     */     
/*  52 */     if (info == null) {
/*  53 */       return null;
/*     */     }
/*     */     
/*  56 */     MediaCodecInfo.CodecCapabilities capabilities = info.getCapabilitiesForType(type.mimeType());
/*  57 */     return new AndroidVideoDecoder(new MediaCodecWrapperFactoryImpl(), info.getName(), type, 
/*  58 */         MediaCodecUtils.selectColorFormat(MediaCodecUtils.DECODER_COLOR_FORMATS, capabilities).intValue(), this.sharedContext);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public VideoCodecInfo[] getSupportedCodecs() {
/*  64 */     List<VideoCodecInfo> supportedCodecInfos = new ArrayList<>();
/*     */ 
/*     */     
/*  67 */     for (VideoCodecMimeType type : new VideoCodecMimeType[] { VideoCodecMimeType.VP8, VideoCodecMimeType.VP9, VideoCodecMimeType.H264 }) {
/*     */       
/*  69 */       MediaCodecInfo codec = findCodecForType(type);
/*  70 */       if (codec != null) {
/*  71 */         String name = type.name();
/*  72 */         if (type == VideoCodecMimeType.H264 && isH264HighProfileSupported(codec)) {
/*  73 */           supportedCodecInfos.add(new VideoCodecInfo(name, 
/*  74 */                 MediaCodecUtils.getCodecProperties(type, true)));
/*     */         }
/*     */         
/*  77 */         supportedCodecInfos.add(new VideoCodecInfo(name, 
/*  78 */               MediaCodecUtils.getCodecProperties(type, false)));
/*     */       } 
/*     */     } 
/*     */     
/*  82 */     return supportedCodecInfos.<VideoCodecInfo>toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private MediaCodecInfo findCodecForType(VideoCodecMimeType type) {
/*  87 */     if (Build.VERSION.SDK_INT < 19) {
/*  88 */       return null;
/*     */     }
/*     */     
/*  91 */     for (int i = 0; i < MediaCodecList.getCodecCount(); i++) {
/*  92 */       MediaCodecInfo info = null;
/*     */       try {
/*  94 */         info = MediaCodecList.getCodecInfoAt(i);
/*  95 */       } catch (IllegalArgumentException e) {
/*  96 */         Logging.e("MediaCodecVideoDecoderFactory", "Cannot retrieve decoder codec info", e);
/*     */       } 
/*     */       
/*  99 */       if (info != null && !info.isEncoder())
/*     */       {
/*     */ 
/*     */         
/* 103 */         if (isSupportedCodec(info, type)) {
/* 104 */           return info;
/*     */         }
/*     */       }
/*     */     } 
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isSupportedCodec(MediaCodecInfo info, VideoCodecMimeType type) {
/* 113 */     String name = info.getName();
/* 114 */     if (!MediaCodecUtils.codecSupportsType(info, type)) {
/* 115 */       return false;
/*     */     }
/*     */     
/* 118 */     if (MediaCodecUtils.selectColorFormat(MediaCodecUtils.DECODER_COLOR_FORMATS, info
/* 119 */         .getCapabilitiesForType(type.mimeType())) == null)
/*     */     {
/* 121 */       return false;
/*     */     }
/* 123 */     return isCodecAllowed(info);
/*     */   }
/*     */   
/*     */   private boolean isCodecAllowed(MediaCodecInfo info) {
/* 127 */     if (this.codecAllowedPredicate == null) {
/* 128 */       return true;
/*     */     }
/* 130 */     return this.codecAllowedPredicate.test(info);
/*     */   }
/*     */   
/*     */   private boolean isH264HighProfileSupported(MediaCodecInfo info) {
/* 134 */     String name = info.getName();
/*     */     
/* 136 */     if (Build.VERSION.SDK_INT >= 21 && name.startsWith("OMX.qcom.")) {
/* 137 */       return true;
/*     */     }
/*     */     
/* 140 */     if (Build.VERSION.SDK_INT >= 23 && name.startsWith("OMX.Exynos.")) {
/* 141 */       return true;
/*     */     }
/* 143 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/MediaCodecVideoDecoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */