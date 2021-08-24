package org.webrtc;

public interface SSLCertificateVerifier {
  @CalledByNative
  boolean verify(byte[] paramArrayOfbyte);
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/SSLCertificateVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */