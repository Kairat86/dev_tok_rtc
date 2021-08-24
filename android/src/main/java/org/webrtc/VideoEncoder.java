/*     */ package org.webrtc;
/*     */ 
/*     */ import androidx.annotation.Nullable;
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
/*     */ public interface VideoEncoder
/*     */ {
/*     */   public static class Settings
/*     */   {
/*     */     public final int numberOfCores;
/*     */     public final int width;
/*     */     public final int height;
/*     */     public final int startBitrate;
/*     */     public final int maxFramerate;
/*     */     public final int numberOfSimulcastStreams;
/*     */     public final boolean automaticResizeOn;
/*     */     public final VideoEncoder.Capabilities capabilities;
/*     */     
/*     */     @Deprecated
/*     */     public Settings(int numberOfCores, int width, int height, int startBitrate, int maxFramerate, int numberOfSimulcastStreams, boolean automaticResizeOn) {
/*  37 */       this(numberOfCores, width, height, startBitrate, maxFramerate, numberOfSimulcastStreams, automaticResizeOn, new VideoEncoder.Capabilities(false));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @CalledByNative("Settings")
/*     */     public Settings(int numberOfCores, int width, int height, int startBitrate, int maxFramerate, int numberOfSimulcastStreams, boolean automaticResizeOn, VideoEncoder.Capabilities capabilities) {
/*  44 */       this.numberOfCores = numberOfCores;
/*  45 */       this.width = width;
/*  46 */       this.height = height;
/*  47 */       this.startBitrate = startBitrate;
/*  48 */       this.maxFramerate = maxFramerate;
/*  49 */       this.numberOfSimulcastStreams = numberOfSimulcastStreams;
/*  50 */       this.automaticResizeOn = automaticResizeOn;
/*  51 */       this.capabilities = capabilities;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Capabilities
/*     */   {
/*     */     public final boolean lossNotification;
/*     */ 
/*     */ 
/*     */     
/*     */     @CalledByNative("Capabilities")
/*     */     public Capabilities(boolean lossNotification) {
/*  65 */       this.lossNotification = lossNotification;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class EncodeInfo
/*     */   {
/*     */     public final EncodedImage.FrameType[] frameTypes;
/*     */     
/*     */     @CalledByNative("EncodeInfo")
/*     */     public EncodeInfo(EncodedImage.FrameType[] frameTypes) {
/*  75 */       this.frameTypes = frameTypes;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CodecSpecificInfo {}
/*     */ 
/*     */   
/*     */   public static class CodecSpecificInfoVP8
/*     */     extends CodecSpecificInfo {}
/*     */ 
/*     */   
/*     */   public static class CodecSpecificInfoVP9
/*     */     extends CodecSpecificInfo {}
/*     */ 
/*     */   
/*     */   public static class CodecSpecificInfoH264
/*     */     extends CodecSpecificInfo {}
/*     */ 
/*     */   
/*     */   public static class BitrateAllocation
/*     */   {
/*     */     public final int[][] bitratesBbs;
/*     */ 
/*     */     
/*     */     @CalledByNative("BitrateAllocation")
/*     */     public BitrateAllocation(int[][] bitratesBbs) {
/* 103 */       this.bitratesBbs = bitratesBbs;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getSum() {
/* 110 */       int sum = 0;
/* 111 */       for (int[] spatialLayer : this.bitratesBbs) {
/* 112 */         for (int bitrate : spatialLayer) {
/* 113 */           sum += bitrate;
/*     */         }
/*     */       } 
/* 116 */       return sum;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class ScalingSettings
/*     */   {
/*     */     public final boolean on;
/*     */     
/*     */     @Nullable
/*     */     public final Integer low;
/*     */     @Nullable
/*     */     public final Integer high;
/* 129 */     public static final ScalingSettings OFF = new ScalingSettings();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ScalingSettings(int low, int high) {
/* 138 */       this.on = true;
/* 139 */       this.low = Integer.valueOf(low);
/* 140 */       this.high = Integer.valueOf(high);
/*     */     }
/*     */     
/*     */     private ScalingSettings() {
/* 144 */       this.on = false;
/* 145 */       this.low = null;
/* 146 */       this.high = null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public ScalingSettings(boolean on) {
/* 159 */       this.on = on;
/* 160 */       this.low = null;
/* 161 */       this.high = null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public ScalingSettings(boolean on, int low, int high) {
/* 173 */       this.on = on;
/* 174 */       this.low = Integer.valueOf(low);
/* 175 */       this.high = Integer.valueOf(high);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 180 */       return this.on ? ("[ " + this.low + ", " + this.high + " ]") : "OFF";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class ResolutionBitrateLimits
/*     */   {
/*     */     public final int frameSizePixels;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final int minStartBitrateBps;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final int minBitrateBps;
/*     */ 
/*     */ 
/*     */     
/*     */     public final int maxBitrateBps;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ResolutionBitrateLimits(int frameSizePixels, int minStartBitrateBps, int minBitrateBps, int maxBitrateBps) {
/* 210 */       this.frameSizePixels = frameSizePixels;
/* 211 */       this.minStartBitrateBps = minStartBitrateBps;
/* 212 */       this.minBitrateBps = minBitrateBps;
/* 213 */       this.maxBitrateBps = maxBitrateBps;
/*     */     }
/*     */     
/*     */     @CalledByNative("ResolutionBitrateLimits")
/*     */     public int getFrameSizePixels() {
/* 218 */       return this.frameSizePixels;
/*     */     }
/*     */     
/*     */     @CalledByNative("ResolutionBitrateLimits")
/*     */     public int getMinStartBitrateBps() {
/* 223 */       return this.minStartBitrateBps;
/*     */     }
/*     */     
/*     */     @CalledByNative("ResolutionBitrateLimits")
/*     */     public int getMinBitrateBps() {
/* 228 */       return this.minBitrateBps;
/*     */     }
/*     */     
/*     */     @CalledByNative("ResolutionBitrateLimits")
/*     */     public int getMaxBitrateBps() {
/* 233 */       return this.maxBitrateBps;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   default long createNativeVideoEncoder() {
/* 270 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   default boolean isHardwareEncoder() {
/* 278 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   VideoCodecStatus initEncode(Settings paramSettings, Callback paramCallback);
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   VideoCodecStatus release();
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   VideoCodecStatus encode(VideoFrame paramVideoFrame, EncodeInfo paramEncodeInfo);
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   VideoCodecStatus setRateAllocation(BitrateAllocation paramBitrateAllocation, int paramInt);
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   ScalingSettings getScalingSettings();
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   default ResolutionBitrateLimits[] getResolutionBitrateLimits() {
/* 306 */     ResolutionBitrateLimits[] bitrate_limits = new ResolutionBitrateLimits[0];
/* 307 */     return bitrate_limits;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   String getImplementationName();
/*     */   
/*     */   public static interface Callback {
/*     */     void onEncodedFrame(EncodedImage param1EncodedImage, VideoEncoder.CodecSpecificInfo param1CodecSpecificInfo);
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */