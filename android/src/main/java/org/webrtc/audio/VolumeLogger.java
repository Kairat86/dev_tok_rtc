/*    */ package org.webrtc.audio;
/*    */ 
/*    */ import android.media.AudioManager;
/*    */ import androidx.annotation.Nullable;
/*    */ import java.util.Timer;
/*    */ import java.util.TimerTask;
/*    */ import org.webrtc.Logging;
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
/*    */ class VolumeLogger
/*    */ {
/*    */   private static final String TAG = "VolumeLogger";
/*    */   private static final String THREAD_NAME = "WebRtcVolumeLevelLoggerThread";
/*    */   private static final int TIMER_PERIOD_IN_SECONDS = 30;
/*    */   private final AudioManager audioManager;
/*    */   @Nullable
/*    */   private Timer timer;
/*    */   
/*    */   public VolumeLogger(AudioManager audioManager) {
/* 35 */     this.audioManager = audioManager;
/*    */   }
/*    */   
/*    */   public void start() {
/* 39 */     Logging.d("VolumeLogger", "start" + WebRtcAudioUtils.getThreadInfo());
/* 40 */     if (this.timer != null) {
/*    */       return;
/*    */     }
/* 43 */     Logging.d("VolumeLogger", "audio mode is: " + WebRtcAudioUtils.modeToString(this.audioManager.getMode()));
/*    */     
/* 45 */     this.timer = new Timer("WebRtcVolumeLevelLoggerThread");
/* 46 */     this.timer.schedule(new LogVolumeTask(this.audioManager.getStreamMaxVolume(2), this.audioManager
/* 47 */           .getStreamMaxVolume(0)), 0L, 30000L);
/*    */   }
/*    */   
/*    */   private class LogVolumeTask
/*    */     extends TimerTask {
/*    */     private final int maxRingVolume;
/*    */     private final int maxVoiceCallVolume;
/*    */     
/*    */     LogVolumeTask(int maxRingVolume, int maxVoiceCallVolume) {
/* 56 */       this.maxRingVolume = maxRingVolume;
/* 57 */       this.maxVoiceCallVolume = maxVoiceCallVolume;
/*    */     }
/*    */ 
/*    */     
/*    */     public void run() {
/* 62 */       int mode = VolumeLogger.this.audioManager.getMode();
/* 63 */       if (mode == 1) {
/* 64 */         Logging.d("VolumeLogger", "STREAM_RING stream volume: " + VolumeLogger.this
/* 65 */             .audioManager.getStreamVolume(2) + " (max=" + this.maxRingVolume + ")");
/*    */       }
/* 67 */       else if (mode == 3) {
/* 68 */         Logging.d("VolumeLogger", "VOICE_CALL stream volume: " + VolumeLogger.this
/*    */             
/* 70 */             .audioManager.getStreamVolume(0) + " (max=" + this.maxVoiceCallVolume + ")");
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void stop() {
/* 77 */     Logging.d("VolumeLogger", "stop" + WebRtcAudioUtils.getThreadInfo());
/* 78 */     if (this.timer != null) {
/* 79 */       this.timer.cancel();
/* 80 */       this.timer = null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/audio/VolumeLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */