/*    */ package org.webrtc;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ class H264Utils
/*    */ {
/*    */   public static final String H264_FMTP_PROFILE_LEVEL_ID = "profile-level-id";
/*    */   public static final String H264_FMTP_LEVEL_ASYMMETRY_ALLOWED = "level-asymmetry-allowed";
/*    */   public static final String H264_FMTP_PACKETIZATION_MODE = "packetization-mode";
/*    */   public static final String H264_PROFILE_CONSTRAINED_BASELINE = "42e0";
/*    */   public static final String H264_PROFILE_CONSTRAINED_HIGH = "640c";
/*    */   public static final String H264_LEVEL_3_1 = "1f";
/*    */   public static final String H264_CONSTRAINED_HIGH_3_1 = "640c1f";
/*    */   public static final String H264_CONSTRAINED_BASELINE_3_1 = "42e01f";
/*    */   
/*    */   public static Map<String, String> getDefaultH264Params(boolean isHighProfile) {
/* 31 */     Map<String, String> params = new HashMap<>();
/* 32 */     params.put("level-asymmetry-allowed", "1");
/* 33 */     params.put("packetization-mode", "1");
/* 34 */     params.put("profile-level-id", 
/* 35 */         isHighProfile ? "640c1f" : 
/* 36 */         "42e01f");
/* 37 */     return params;
/*    */   }
/*    */   
/* 40 */   public static VideoCodecInfo DEFAULT_H264_BASELINE_PROFILE_CODEC = new VideoCodecInfo("H264", 
/* 41 */       getDefaultH264Params(false));
/* 42 */   public static VideoCodecInfo DEFAULT_H264_HIGH_PROFILE_CODEC = new VideoCodecInfo("H264", 
/* 43 */       getDefaultH264Params(true));
/*    */ 
/*    */   
/*    */   public static boolean isSameH264Profile(Map<String, String> params1, Map<String, String> params2) {
/* 47 */     return nativeIsSameH264Profile(params1, params2);
/*    */   }
/*    */   
/*    */   private static native boolean nativeIsSameH264Profile(Map<String, String> paramMap1, Map<String, String> paramMap2);
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/H264Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */