/*    */ package org.webrtc;
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
/*    */ enum VideoCodecMimeType
/*    */ {
/* 15 */   VP8("video/x-vnd.on2.vp8"),
/* 16 */   VP9("video/x-vnd.on2.vp9"),
/* 17 */   H264("video/avc");
/*    */   
/*    */   private final String mimeType;
/*    */   
/*    */   VideoCodecMimeType(String mimeType) {
/* 22 */     this.mimeType = mimeType;
/*    */   }
/*    */   
/*    */   String mimeType() {
/* 26 */     return this.mimeType;
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoCodecMimeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */