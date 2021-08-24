/*    */ package org.webrtc.voiceengine;
/*    */ 
/*    */ import android.os.Build;
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
/*    */ public final class BuildInfo
/*    */ {
/*    */   public static String getDevice() {
/* 17 */     return Build.DEVICE;
/*    */   }
/*    */   
/*    */   public static String getDeviceModel() {
/* 21 */     return Build.MODEL;
/*    */   }
/*    */   
/*    */   public static String getProduct() {
/* 25 */     return Build.PRODUCT;
/*    */   }
/*    */   
/*    */   public static String getBrand() {
/* 29 */     return Build.BRAND;
/*    */   }
/*    */   
/*    */   public static String getDeviceManufacturer() {
/* 33 */     return Build.MANUFACTURER;
/*    */   }
/*    */   
/*    */   public static String getAndroidBuildId() {
/* 37 */     return Build.ID;
/*    */   }
/*    */   
/*    */   public static String getBuildType() {
/* 41 */     return Build.TYPE;
/*    */   }
/*    */   
/*    */   public static String getBuildRelease() {
/* 45 */     return Build.VERSION.RELEASE;
/*    */   }
/*    */   
/*    */   public static int getSdkVersion() {
/* 49 */     return Build.VERSION.SDK_INT;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/voiceengine/BuildInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */