package org.webrtc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface CalledByNativeUnchecked {
  String value() default "";
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/CalledByNativeUnchecked.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */