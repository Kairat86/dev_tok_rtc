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
/*    */ public class DefaultVideoEncoderFactory
/*    */   implements VideoEncoderFactory
/*    */ {
/*    */   private final VideoEncoderFactory hardwareVideoEncoderFactory;
/* 20 */   private final VideoEncoderFactory softwareVideoEncoderFactory = new SoftwareVideoEncoderFactory();
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultVideoEncoderFactory(EglBase.Context eglContext, boolean enableIntelVp8Encoder, boolean enableH264HighProfile) {
/* 25 */     this.hardwareVideoEncoderFactory = new HardwareVideoEncoderFactory(eglContext, enableIntelVp8Encoder, enableH264HighProfile);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   DefaultVideoEncoderFactory(VideoEncoderFactory hardwareVideoEncoderFactory) {
/* 31 */     this.hardwareVideoEncoderFactory = hardwareVideoEncoderFactory;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public VideoEncoder createEncoder(VideoCodecInfo info) {
/* 37 */     VideoEncoder softwareEncoder = this.softwareVideoEncoderFactory.createEncoder(info);
/* 38 */     VideoEncoder hardwareEncoder = this.hardwareVideoEncoderFactory.createEncoder(info);
/* 39 */     if (hardwareEncoder != null && softwareEncoder != null)
/*    */     {
/* 41 */       return new VideoEncoderFallback(softwareEncoder, hardwareEncoder);
/*    */     }
/*    */     
/* 44 */     return (hardwareEncoder != null) ? hardwareEncoder : softwareEncoder;
/*    */   }
/*    */ 
/*    */   
/*    */   public VideoCodecInfo[] getSupportedCodecs() {
/* 49 */     LinkedHashSet<VideoCodecInfo> supportedCodecInfos = new LinkedHashSet<>();
/*    */     
/* 51 */     supportedCodecInfos.addAll(Arrays.asList(this.softwareVideoEncoderFactory.getSupportedCodecs()));
/* 52 */     supportedCodecInfos.addAll(Arrays.asList(this.hardwareVideoEncoderFactory.getSupportedCodecs()));
/*    */     
/* 54 */     return (VideoCodecInfo[])supportedCodecInfos.toArray((Object[])new VideoCodecInfo[supportedCodecInfos.size()]);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/DefaultVideoEncoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */