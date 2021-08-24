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
/*    */ public class HardwareVideoDecoderFactory
/*    */   extends MediaCodecVideoDecoderFactory
/*    */ {
/* 19 */   private static final Predicate<MediaCodecInfo> defaultAllowedPredicate = new Predicate<MediaCodecInfo>()
/*    */     {
/*    */       public boolean test(MediaCodecInfo arg)
/*    */       {
/* 23 */         return MediaCodecUtils.isHardwareAccelerated(arg);
/*    */       }
/*    */     };
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public HardwareVideoDecoderFactory() {
/* 30 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HardwareVideoDecoderFactory(@Nullable EglBase.Context sharedContext) {
/* 40 */     this(sharedContext, null);
/*    */   }
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
/*    */   public HardwareVideoDecoderFactory(@Nullable EglBase.Context sharedContext, @Nullable Predicate<MediaCodecInfo> codecAllowedPredicate) {
/* 53 */     super(sharedContext, 
/* 54 */         (codecAllowedPredicate == null) ? defaultAllowedPredicate : 
/* 55 */         codecAllowedPredicate.and(defaultAllowedPredicate));
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/HardwareVideoDecoderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */