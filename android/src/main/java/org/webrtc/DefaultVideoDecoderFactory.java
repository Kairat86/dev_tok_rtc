/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
/*    */ import java.util.Arrays;
/*    */ import java.util.LinkedHashSet;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultVideoDecoderFactory
/*    */   implements VideoDecoderFactory
/*    */ {
/*    */   private final VideoDecoderFactory hardwareVideoDecoderFactory;
/* 22 */   private final VideoDecoderFactory softwareVideoDecoderFactory = new SoftwareVideoDecoderFactory();
/*    */   
/*    */   @Nullable
/*    */   private final VideoDecoderFactory platformSoftwareVideoDecoderFactory;
/*    */ 
/*    */   
/*    */   public DefaultVideoDecoderFactory(@Nullable EglBase.Context eglContext) {
/* 29 */     this.hardwareVideoDecoderFactory = new HardwareVideoDecoderFactory(eglContext);
/* 30 */     this.platformSoftwareVideoDecoderFactory = new PlatformSoftwareVideoDecoderFactory(eglContext);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   DefaultVideoDecoderFactory(VideoDecoderFactory hardwareVideoDecoderFactory) {
/* 37 */     this.hardwareVideoDecoderFactory = hardwareVideoDecoderFactory;
/* 38 */     this.platformSoftwareVideoDecoderFactory = null;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public VideoDecoder createDecoder(VideoCodecInfo codecType) {
/* 43 */     VideoDecoder softwareDecoder = this.softwareVideoDecoderFactory.createDecoder(codecType);
/* 44 */     VideoDecoder hardwareDecoder = this.hardwareVideoDecoderFactory.createDecoder(codecType);
/* 45 */     if (softwareDecoder == null && this.platformSoftwareVideoDecoderFactory != null) {
/* 46 */       softwareDecoder = this.platformSoftwareVideoDecoderFactory.createDecoder(codecType);
/*    */     }
/* 48 */     if (hardwareDecoder != null && softwareDecoder != null)
/*    */     {
/* 50 */       return new VideoDecoderFallback(softwareDecoder, hardwareDecoder);
/*    */     }
/*    */     
/* 53 */     return (hardwareDecoder != null) ? hardwareDecoder : softwareDecoder;
/*    */   }
/*    */ 
/*    */   
/*    */   public VideoCodecInfo[] getSupportedCodecs() {
/* 58 */     LinkedHashSet<VideoCodecInfo> supportedCodecInfos = new LinkedHashSet<>();
/*    */     
/* 60 */     supportedCodecInfos.addAll(Arrays.asList(this.softwareVideoDecoderFactory.getSupportedCodecs()));
/* 61 */     supportedCodecInfos.addAll(Arrays.asList(this.hardwareVideoDecoderFactory.getSupportedCodecs()));
/* 62 */     if (this.platformSoftwareVideoDecoderFactory != null) {
/* 63 */       supportedCodecInfos.addAll(
/* 64 */           Arrays.asList(this.platformSoftwareVideoDecoderFactory.getSupportedCodecs()));
/*    */     }
/*    */     
/* 67 */     return (VideoCodecInfo[])supportedCodecInfos.toArray((Object[])new VideoCodecInfo[supportedCodecInfos.size()]);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/DefaultVideoDecoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */