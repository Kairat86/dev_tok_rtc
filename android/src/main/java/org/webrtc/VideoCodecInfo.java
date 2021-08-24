/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
/*    */ import java.util.Arrays;
/*    */ import java.util.Locale;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VideoCodecInfo
/*    */ {
/*    */   public static final String H264_FMTP_PROFILE_LEVEL_ID = "profile-level-id";
/*    */   public static final String H264_FMTP_LEVEL_ASYMMETRY_ALLOWED = "level-asymmetry-allowed";
/*    */   public static final String H264_FMTP_PACKETIZATION_MODE = "packetization-mode";
/*    */   public static final String H264_PROFILE_CONSTRAINED_BASELINE = "42e0";
/*    */   public static final String H264_PROFILE_CONSTRAINED_HIGH = "640c";
/*    */   public static final String H264_LEVEL_3_1 = "1f";
/*    */   public static final String H264_CONSTRAINED_HIGH_3_1 = "640c1f";
/*    */   public static final String H264_CONSTRAINED_BASELINE_3_1 = "42e01f";
/*    */   public final String name;
/*    */   public final Map<String, String> params;
/*    */   @Deprecated
/*    */   public final int payload;
/*    */   
/*    */   @CalledByNative
/*    */   public VideoCodecInfo(String name, Map<String, String> params) {
/* 41 */     this.payload = 0;
/* 42 */     this.name = name;
/* 43 */     this.params = params;
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public VideoCodecInfo(int payload, String name, Map<String, String> params) {
/* 48 */     this.payload = payload;
/* 49 */     this.name = name;
/* 50 */     this.params = params;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object obj) {
/* 55 */     if (obj == null)
/* 56 */       return false; 
/* 57 */     if (obj == this)
/* 58 */       return true; 
/* 59 */     if (!(obj instanceof VideoCodecInfo)) {
/* 60 */       return false;
/*    */     }
/* 62 */     VideoCodecInfo otherInfo = (VideoCodecInfo)obj;
/* 63 */     return (this.name.equalsIgnoreCase(otherInfo.name) && this.params.equals(otherInfo.params));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 68 */     Object[] values = { this.name.toUpperCase(Locale.ROOT), this.params };
/* 69 */     return Arrays.hashCode(values);
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   String getName() {
/* 74 */     return this.name;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   Map getParams() {
/* 79 */     return this.params;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoCodecInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */