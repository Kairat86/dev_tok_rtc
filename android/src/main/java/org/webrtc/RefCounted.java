package org.webrtc;

public interface RefCounted {
  @CalledByNative
  void retain();
  
  @CalledByNative
  void release();
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RefCounted.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */