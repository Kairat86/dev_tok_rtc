package org.webrtc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Priority {
  public static final int VERY_LOW = 0;
  
  public static final int LOW = 1;
  
  public static final int MEDIUM = 2;
  
  public static final int HIGH = 3;
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/Priority.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */