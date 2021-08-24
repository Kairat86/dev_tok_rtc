package org.webrtc;

import android.content.Context;

public interface VideoCapturer {
  void initialize(SurfaceTextureHelper paramSurfaceTextureHelper, Context paramContext, CapturerObserver paramCapturerObserver);
  
  void startCapture(int paramInt1, int paramInt2, int paramInt3);
  
  void stopCapture() throws InterruptedException;
  
  void changeCaptureFormat(int paramInt1, int paramInt2, int paramInt3);
  
  void dispose();
  
  boolean isScreencast();
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoCapturer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */