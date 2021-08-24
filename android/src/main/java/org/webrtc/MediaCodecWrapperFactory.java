package org.webrtc;

import java.io.IOException;

interface MediaCodecWrapperFactory {
  MediaCodecWrapper createByCodecName(String paramString) throws IOException;
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/MediaCodecWrapperFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */