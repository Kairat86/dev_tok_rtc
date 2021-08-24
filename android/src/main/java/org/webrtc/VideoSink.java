package org.webrtc;

public interface VideoSink {
  @CalledByNative
  void onFrame(VideoFrame paramVideoFrame);
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/VideoSink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */