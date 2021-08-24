/*    */ package org.webrtc;
/*    */ 
/*    */ import androidx.annotation.Nullable;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ public interface NetworkChangeDetector
/*    */ {
/*    */   ConnectionType getCurrentConnectionType();
/*    */   
/*    */   boolean supportNetworkCallback();
/*    */   
/*    */   @Nullable
/*    */   List<NetworkInformation> getActiveNetworkList();
/*    */   
/*    */   void destroy();
/*    */   
/*    */   public enum ConnectionType
/*    */   {
/* 20 */     CONNECTION_UNKNOWN,
/* 21 */     CONNECTION_ETHERNET,
/* 22 */     CONNECTION_WIFI,
/* 23 */     CONNECTION_5G,
/* 24 */     CONNECTION_4G,
/* 25 */     CONNECTION_3G,
/* 26 */     CONNECTION_2G,
/* 27 */     CONNECTION_UNKNOWN_CELLULAR,
/* 28 */     CONNECTION_BLUETOOTH,
/* 29 */     CONNECTION_VPN,
/* 30 */     CONNECTION_NONE;
/*    */   }
/*    */   
/*    */   public static class IPAddress {
/*    */     public final byte[] address;
/*    */     
/*    */     public IPAddress(byte[] address) {
/* 37 */       this.address = address;
/*    */     }
/*    */     
/*    */     @CalledByNative("IPAddress")
/*    */     private byte[] getAddress() {
/* 42 */       return this.address;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public static class NetworkInformation
/*    */   {
/*    */     public final String name;
/*    */     
/*    */     public final NetworkChangeDetector.ConnectionType type;
/*    */     public final NetworkChangeDetector.ConnectionType underlyingTypeForVpn;
/*    */     public final long handle;
/*    */     public final NetworkChangeDetector.IPAddress[] ipAddresses;
/*    */     
/*    */     public NetworkInformation(String name, NetworkChangeDetector.ConnectionType type, NetworkChangeDetector.ConnectionType underlyingTypeForVpn, long handle, NetworkChangeDetector.IPAddress[] addresses) {
/* 57 */       this.name = name;
/* 58 */       this.type = type;
/* 59 */       this.underlyingTypeForVpn = underlyingTypeForVpn;
/* 60 */       this.handle = handle;
/* 61 */       this.ipAddresses = addresses;
/*    */     }
/*    */     
/*    */     @CalledByNative("NetworkInformation")
/*    */     private NetworkChangeDetector.IPAddress[] getIpAddresses() {
/* 66 */       return this.ipAddresses;
/*    */     }
/*    */     
/*    */     @CalledByNative("NetworkInformation")
/*    */     private NetworkChangeDetector.ConnectionType getConnectionType() {
/* 71 */       return this.type;
/*    */     }
/*    */     
/*    */     @CalledByNative("NetworkInformation")
/*    */     private NetworkChangeDetector.ConnectionType getUnderlyingConnectionTypeForVpn() {
/* 76 */       return this.underlyingTypeForVpn;
/*    */     }
/*    */     
/*    */     @CalledByNative("NetworkInformation")
/*    */     private long getHandle() {
/* 81 */       return this.handle;
/*    */     }
/*    */     
/*    */     @CalledByNative("NetworkInformation")
/*    */     private String getName() {
/* 86 */       return this.name;
/*    */     }
/*    */   }
/*    */   
/*    */   public static interface Observer {
/*    */     void onConnectionTypeChanged(NetworkChangeDetector.ConnectionType param1ConnectionType);
/*    */     
/*    */     void onNetworkConnect(NetworkChangeDetector.NetworkInformation param1NetworkInformation);
/*    */     
/*    */     void onNetworkDisconnect(long param1Long);
/*    */     
/*    */     void onNetworkPreference(List<NetworkChangeDetector.ConnectionType> param1List, int param1Int);
/*    */   }
/*    */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/NetworkChangeDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */