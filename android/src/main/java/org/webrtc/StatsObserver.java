package org.webrtc;

public interface StatsObserver {
  @CalledByNative
  void onComplete(StatsReport[] paramArrayOfStatsReport);
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/StatsObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */