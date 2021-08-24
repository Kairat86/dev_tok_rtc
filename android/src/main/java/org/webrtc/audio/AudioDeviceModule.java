package org.webrtc.audio;

public interface AudioDeviceModule {
  long getNativeAudioDeviceModulePointer();
  
  void release();
  
  void setSpeakerMute(boolean paramBoolean);
  
  void setMicrophoneMute(boolean paramBoolean);
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/audio/AudioDeviceModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */