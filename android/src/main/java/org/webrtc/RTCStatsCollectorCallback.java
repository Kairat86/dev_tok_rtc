package org.webrtc;

public interface RTCStatsCollectorCallback {
  @CalledByNative
  void onStatsDelivered(RTCStatsReport paramRTCStatsReport);
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RTCStatsCollectorCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */