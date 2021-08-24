package org.webrtc;

import javax.microedition.khronos.egl.EGLContext;

public interface EglBase10 extends EglBase {
  public static interface Context extends EglBase.Context {
    EGLContext getRawContext();
  }
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/EglBase10.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */