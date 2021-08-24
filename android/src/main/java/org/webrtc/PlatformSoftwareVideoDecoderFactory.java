/*    */ package org.webrtc;
/*    */ 
/*    */ import android.media.MediaCodecInfo;
/*    */ import androidx.annotation.Nullable;
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
/*    */ public class PlatformSoftwareVideoDecoderFactory
/*    */   extends MediaCodecVideoDecoderFactory
/*    */ {
/* 22 */   private static final Predicate<MediaCodecInfo> defaultAllowedPredicate = new Predicate<MediaCodecInfo>()
/*    */     {
/*    */       public boolean test(MediaCodecInfo arg)
/*    */       {
/* 26 */         return MediaCodecUtils.isSoftwareOnly(arg);
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PlatformSoftwareVideoDecoderFactory(@Nullable EglBase.Context sharedContext) {
/* 37 */     super(sharedContext, defaultAllowedPredicate);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/PlatformSoftwareVideoDecoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */