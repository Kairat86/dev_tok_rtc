package org.webrtc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface NetworkPreference {
  public static final int NEUTRAL = 0;
  
  public static final int NOT_PREFERRED = -1;
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/NetworkPreference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */