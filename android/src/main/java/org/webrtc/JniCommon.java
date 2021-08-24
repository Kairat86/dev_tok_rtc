package org.webrtc;

import java.nio.ByteBuffer;

public class JniCommon {
  public static native void nativeAddRef(long paramLong);
  
  public static native void nativeReleaseRef(long paramLong);
  
  public static native ByteBuffer nativeAllocateByteBuffer(int paramInt);
  
  public static native void nativeFreeByteBuffer(ByteBuffer paramByteBuffer);
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/JniCommon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */