package org.webrtc;

import java.util.List;

public interface CameraEnumerator {
  String[] getDeviceNames();
  
  boolean isFrontFacing(String paramString);
  
  boolean isBackFacing(String paramString);
  
  List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(String paramString);
  
  CameraVideoCapturer createCapturer(String paramString, CameraVideoCapturer.CameraEventsHandler paramCameraEventsHandler);
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/CameraEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */