/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IceCandidate
/*    */ {
/*    */   public final String sdpMid;
/*    */   public final int sdpMLineIndex;
/*    */   public final String sdp;
/*    */   public final String serverUrl;
/*    */   public final PeerConnection.AdapterType adapterType;
/*    */   
/*    */   public IceCandidate(String sdpMid, int sdpMLineIndex, String sdp) {
/* 29 */     this.sdpMid = sdpMid;
/* 30 */     this.sdpMLineIndex = sdpMLineIndex;
/* 31 */     this.sdp = sdp;
/* 32 */     this.serverUrl = "";
/* 33 */     this.adapterType = PeerConnection.AdapterType.UNKNOWN;
/*    */   }
/*    */ 
/*    */   
/*    */   @CalledByNative
/*    */   IceCandidate(String sdpMid, int sdpMLineIndex, String sdp, String serverUrl, PeerConnection.AdapterType adapterType) {
/* 39 */     this.sdpMid = sdpMid;
/* 40 */     this.sdpMLineIndex = sdpMLineIndex;
/* 41 */     this.sdp = sdp;
/* 42 */     this.serverUrl = serverUrl;
/* 43 */     this.adapterType = adapterType;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     return this.sdpMid + ":" + this.sdpMLineIndex + ":" + this.sdp + ":" + this.serverUrl + ":" + this.adapterType
/* 49 */       .toString();
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   String getSdpMid() {
/* 54 */     return this.sdpMid;
/*    */   }
/*    */   
/*    */   @CalledByNative
/*    */   String getSdp() {
/* 59 */     return this.sdp;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object object) {
/* 65 */     if (!(object instanceof IceCandidate)) {
/* 66 */       return false;
/*    */     }
/*    */     
/* 69 */     IceCandidate that = (IceCandidate)object;
/* 70 */     return (objectEquals(this.sdpMid, that.sdpMid) && this.sdpMLineIndex == that.sdpMLineIndex && 
/* 71 */       objectEquals(this.sdp, that.sdp));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 76 */     Object[] values = { this.sdpMid, Integer.valueOf(this.sdpMLineIndex), this.sdp };
/* 77 */     return Arrays.hashCode(values);
/*    */   }
/*    */   
/*    */   private static boolean objectEquals(Object o1, Object o2) {
/* 81 */     if (o1 == null) {
/* 82 */       return (o2 == null);
/*    */     }
/* 84 */     return o1.equals(o2);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/IceCandidate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */