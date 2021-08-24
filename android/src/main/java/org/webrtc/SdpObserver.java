package org.webrtc;

public interface SdpObserver {
  @CalledByNative
  void onCreateSuccess(SessionDescription paramSessionDescription);
  
  @CalledByNative
  void onSetSuccess();
  
  @CalledByNative
  void onCreateFailure(String paramString);
  
  @CalledByNative
  void onSetFailure(String paramString);
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/SdpObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */