package org.webrtc;

public interface CapturerObserver {
  void onCapturerStarted(boolean paramBoolean);
  
  void onCapturerStopped();
  
  void onFrameCaptured(VideoFrame paramVideoFrame);
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/CapturerObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */