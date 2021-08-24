/*     */ package org.webrtc;
/*     */ 
/*     */ import android.annotation.SuppressLint;
/*     */ import android.content.BroadcastReceiver;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.IntentFilter;
/*     */ import android.net.ConnectivityManager;
/*     */ import android.net.LinkAddress;
/*     */ import android.net.LinkProperties;
/*     */ import android.net.Network;
/*     */ import android.net.NetworkCapabilities;
/*     */ import android.net.NetworkInfo;
/*     */ import android.net.NetworkRequest;
/*     */ import android.net.wifi.WifiInfo;
/*     */ import android.net.wifi.p2p.WifiP2pGroup;
/*     */ import android.net.wifi.p2p.WifiP2pManager;
/*     */ import android.os.Build;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkMonitorAutoDetect
/*     */   extends BroadcastReceiver
/*     */   implements NetworkChangeDetector
/*     */ {
/*     */   private static final long INVALID_NET_ID = -1L;
/*     */   private static final String TAG = "NetworkMonitorAutoDetect";
/*     */   private final NetworkChangeDetector.Observer observer;
/*     */   private final IntentFilter intentFilter;
/*     */   private final Context context;
/*     */   @Nullable
/*     */   private final ConnectivityManager.NetworkCallback mobileNetworkCallback;
/*     */   @Nullable
/*     */   private final ConnectivityManager.NetworkCallback allNetworkCallback;
/*     */   private ConnectivityManagerDelegate connectivityManagerDelegate;
/*     */   private WifiManagerDelegate wifiManagerDelegate;
/*     */   private WifiDirectManagerDelegate wifiDirectManagerDelegate;
/*     */   private boolean isRegistered;
/*     */   private NetworkChangeDetector.ConnectionType connectionType;
/*     */   private String wifiSSID;
/*     */   
/*     */   static class NetworkState
/*     */   {
/*     */     private final boolean connected;
/*     */     private final int type;
/*     */     private final int subtype;
/*     */     private final int underlyingNetworkTypeForVpn;
/*     */     private final int underlyingNetworkSubtypeForVpn;
/*     */     
/*     */     public NetworkState(boolean connected, int type, int subtype, int underlyingNetworkTypeForVpn, int underlyingNetworkSubtypeForVpn) {
/*  63 */       this.connected = connected;
/*  64 */       this.type = type;
/*  65 */       this.subtype = subtype;
/*  66 */       this.underlyingNetworkTypeForVpn = underlyingNetworkTypeForVpn;
/*  67 */       this.underlyingNetworkSubtypeForVpn = underlyingNetworkSubtypeForVpn;
/*     */     }
/*     */     
/*     */     public boolean isConnected() {
/*  71 */       return this.connected;
/*     */     }
/*     */     
/*     */     public int getNetworkType() {
/*  75 */       return this.type;
/*     */     }
/*     */     
/*     */     public int getNetworkSubType() {
/*  79 */       return this.subtype;
/*     */     }
/*     */     
/*     */     public int getUnderlyingNetworkTypeForVpn() {
/*  83 */       return this.underlyingNetworkTypeForVpn;
/*     */     }
/*     */     
/*     */     public int getUnderlyingNetworkSubtypeForVpn() {
/*  87 */       return this.underlyingNetworkSubtypeForVpn;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @SuppressLint({"NewApi"})
/*     */   private class SimpleNetworkCallback
/*     */     extends ConnectivityManager.NetworkCallback
/*     */   {
/*     */     private SimpleNetworkCallback() {}
/*     */     
/*     */     public void onAvailable(Network network) {
/*  99 */       Logging.d("NetworkMonitorAutoDetect", "Network becomes available: " + network.toString());
/* 100 */       onNetworkChanged(network);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
/* 107 */       Logging.d("NetworkMonitorAutoDetect", "capabilities changed: " + networkCapabilities.toString());
/* 108 */       onNetworkChanged(network);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
/* 115 */       Logging.d("NetworkMonitorAutoDetect", "link properties changed: " + linkProperties.toString());
/* 116 */       onNetworkChanged(network);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onLosing(Network network, int maxMsToLive) {
/* 123 */       Logging.d("NetworkMonitorAutoDetect", "Network " + network
/* 124 */           .toString() + " is about to lose in " + maxMsToLive + "ms");
/*     */     }
/*     */ 
/*     */     
/*     */     public void onLost(Network network) {
/* 129 */       Logging.d("NetworkMonitorAutoDetect", "Network " + network.toString() + " is disconnected");
/* 130 */       NetworkMonitorAutoDetect.this.observer.onNetworkDisconnect(NetworkMonitorAutoDetect.networkToNetId(network));
/*     */     }
/*     */     
/*     */     private void onNetworkChanged(Network network) {
/* 134 */       NetworkChangeDetector.NetworkInformation networkInformation = NetworkMonitorAutoDetect.this.connectivityManagerDelegate.networkToInfo(network);
/* 135 */       if (networkInformation != null) {
/* 136 */         NetworkMonitorAutoDetect.this.observer.onNetworkConnect(networkInformation);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class ConnectivityManagerDelegate
/*     */   {
/*     */     @Nullable
/*     */     private final ConnectivityManager connectivityManager;
/*     */ 
/*     */     
/*     */     ConnectivityManagerDelegate(Context context) {
/* 150 */       this
/* 151 */         .connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     ConnectivityManagerDelegate() {
/* 157 */       this.connectivityManager = null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     NetworkMonitorAutoDetect.NetworkState getNetworkState() {
/* 165 */       if (this.connectivityManager == null) {
/* 166 */         return new NetworkMonitorAutoDetect.NetworkState(false, -1, -1, -1, -1);
/*     */       }
/* 168 */       return getNetworkState(this.connectivityManager.getActiveNetworkInfo());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @SuppressLint({"NewApi"})
/*     */     NetworkMonitorAutoDetect.NetworkState getNetworkState(@Nullable Network network) {
/* 177 */       if (network == null || this.connectivityManager == null) {
/* 178 */         return new NetworkMonitorAutoDetect.NetworkState(false, -1, -1, -1, -1);
/*     */       }
/* 180 */       NetworkInfo networkInfo = this.connectivityManager.getNetworkInfo(network);
/* 181 */       if (networkInfo == null) {
/* 182 */         Logging.w("NetworkMonitorAutoDetect", "Couldn't retrieve information from network " + network.toString());
/* 183 */         return new NetworkMonitorAutoDetect.NetworkState(false, -1, -1, -1, -1);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 201 */       if (networkInfo.getType() != 17) {
/*     */ 
/*     */         
/* 204 */         NetworkCapabilities networkCapabilities = this.connectivityManager.getNetworkCapabilities(network);
/* 205 */         if (networkCapabilities == null || 
/* 206 */           !networkCapabilities.hasTransport(4)) {
/* 207 */           return getNetworkState(networkInfo);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 212 */         return new NetworkMonitorAutoDetect.NetworkState(networkInfo.isConnected(), 17, -1, networkInfo
/* 213 */             .getType(), networkInfo.getSubtype());
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 223 */       if (networkInfo.getType() == 17) {
/* 224 */         if (Build.VERSION.SDK_INT >= 23 && network
/* 225 */           .equals(this.connectivityManager.getActiveNetwork())) {
/*     */ 
/*     */ 
/*     */           
/* 229 */           NetworkInfo underlyingActiveNetworkInfo = this.connectivityManager.getActiveNetworkInfo();
/*     */           
/* 231 */           if (underlyingActiveNetworkInfo != null && underlyingActiveNetworkInfo
/* 232 */             .getType() != 17) {
/* 233 */             return new NetworkMonitorAutoDetect.NetworkState(networkInfo.isConnected(), 17, -1, underlyingActiveNetworkInfo
/* 234 */                 .getType(), underlyingActiveNetworkInfo.getSubtype());
/*     */           }
/*     */         } 
/* 237 */         return new NetworkMonitorAutoDetect.NetworkState(networkInfo
/* 238 */             .isConnected(), 17, -1, -1, -1);
/*     */       } 
/*     */       
/* 241 */       return getNetworkState(networkInfo);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private NetworkMonitorAutoDetect.NetworkState getNetworkState(@Nullable NetworkInfo networkInfo) {
/* 250 */       if (networkInfo == null || !networkInfo.isConnected()) {
/* 251 */         return new NetworkMonitorAutoDetect.NetworkState(false, -1, -1, -1, -1);
/*     */       }
/* 253 */       return new NetworkMonitorAutoDetect.NetworkState(true, networkInfo.getType(), networkInfo.getSubtype(), -1, -1);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @SuppressLint({"NewApi"})
/*     */     Network[] getAllNetworks() {
/* 262 */       if (this.connectivityManager == null) {
/* 263 */         return new Network[0];
/*     */       }
/* 265 */       return this.connectivityManager.getAllNetworks();
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     List<NetworkChangeDetector.NetworkInformation> getActiveNetworkList() {
/* 270 */       if (!supportNetworkCallback()) {
/* 271 */         return null;
/*     */       }
/* 273 */       ArrayList<NetworkChangeDetector.NetworkInformation> netInfoList = new ArrayList<>();
/* 274 */       for (Network network : getAllNetworks()) {
/* 275 */         NetworkChangeDetector.NetworkInformation info = networkToInfo(network);
/* 276 */         if (info != null) {
/* 277 */           netInfoList.add(info);
/*     */         }
/*     */       } 
/* 280 */       return netInfoList;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @SuppressLint({"NewApi"})
/*     */     long getDefaultNetId() {
/* 290 */       if (!supportNetworkCallback()) {
/* 291 */         return -1L;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 297 */       NetworkInfo defaultNetworkInfo = this.connectivityManager.getActiveNetworkInfo();
/* 298 */       if (defaultNetworkInfo == null) {
/* 299 */         return -1L;
/*     */       }
/* 301 */       Network[] networks = getAllNetworks();
/* 302 */       long defaultNetId = -1L;
/* 303 */       for (Network network : networks) {
/* 304 */         if (hasInternetCapability(network)) {
/*     */ 
/*     */           
/* 307 */           NetworkInfo networkInfo = this.connectivityManager.getNetworkInfo(network);
/* 308 */           if (networkInfo != null && networkInfo.getType() == defaultNetworkInfo.getType()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 316 */             if (defaultNetId != -1L) {
/* 317 */               throw new RuntimeException("Multiple connected networks of same type are not supported.");
/*     */             }
/*     */             
/* 320 */             defaultNetId = NetworkMonitorAutoDetect.networkToNetId(network);
/*     */           } 
/*     */         } 
/* 323 */       }  return defaultNetId;
/*     */     }
/*     */     @SuppressLint({"NewApi"})
/*     */     @Nullable
/*     */     private NetworkChangeDetector.NetworkInformation networkToInfo(@Nullable Network network) {
/* 328 */       if (network == null || this.connectivityManager == null) {
/* 329 */         return null;
/*     */       }
/* 331 */       LinkProperties linkProperties = this.connectivityManager.getLinkProperties(network);
/*     */       
/* 333 */       if (linkProperties == null) {
/* 334 */         Logging.w("NetworkMonitorAutoDetect", "Detected unknown network: " + network.toString());
/* 335 */         return null;
/*     */       } 
/* 337 */       if (linkProperties.getInterfaceName() == null) {
/* 338 */         Logging.w("NetworkMonitorAutoDetect", "Null interface name for network " + network.toString());
/* 339 */         return null;
/*     */       } 
/*     */       
/* 342 */       NetworkMonitorAutoDetect.NetworkState networkState = getNetworkState(network);
/* 343 */       NetworkChangeDetector.ConnectionType connectionType = NetworkMonitorAutoDetect.getConnectionType(networkState);
/* 344 */       if (connectionType == NetworkChangeDetector.ConnectionType.CONNECTION_NONE) {
/*     */ 
/*     */         
/* 347 */         Logging.d("NetworkMonitorAutoDetect", "Network " + network.toString() + " is disconnected");
/* 348 */         return null;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 353 */       if (connectionType == NetworkChangeDetector.ConnectionType.CONNECTION_UNKNOWN || connectionType == NetworkChangeDetector.ConnectionType.CONNECTION_UNKNOWN_CELLULAR)
/*     */       {
/* 355 */         Logging.d("NetworkMonitorAutoDetect", "Network " + network.toString() + " connection type is " + connectionType + " because it has type " + networkState
/* 356 */             .getNetworkType() + " and subtype " + networkState
/* 357 */             .getNetworkSubType());
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 363 */       NetworkChangeDetector.ConnectionType underlyingConnectionTypeForVpn = NetworkMonitorAutoDetect.getUnderlyingConnectionTypeForVpn(networkState);
/*     */ 
/*     */ 
/*     */       
/* 367 */       NetworkChangeDetector.NetworkInformation networkInformation = new NetworkChangeDetector.NetworkInformation(linkProperties.getInterfaceName(), connectionType, underlyingConnectionTypeForVpn, NetworkMonitorAutoDetect.networkToNetId(network), getIPAddresses(linkProperties));
/* 368 */       return networkInformation;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @SuppressLint({"NewApi"})
/*     */     boolean hasInternetCapability(Network network) {
/* 377 */       if (this.connectivityManager == null) {
/* 378 */         return false;
/*     */       }
/* 380 */       NetworkCapabilities capabilities = this.connectivityManager.getNetworkCapabilities(network);
/* 381 */       return (capabilities != null && capabilities
/* 382 */         .hasCapability(12));
/*     */     }
/*     */ 
/*     */     
/*     */     @SuppressLint({"NewApi"})
/*     */     public void registerNetworkCallback(ConnectivityManager.NetworkCallback networkCallback) {
/* 388 */       this.connectivityManager.registerNetworkCallback((new NetworkRequest.Builder())
/*     */           
/* 390 */           .addCapability(12)
/* 391 */           .build(), networkCallback);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @SuppressLint({"NewApi"})
/*     */     public void requestMobileNetwork(ConnectivityManager.NetworkCallback networkCallback) {
/* 398 */       NetworkRequest.Builder builder = new NetworkRequest.Builder();
/* 399 */       builder.addCapability(12)
/* 400 */         .addTransportType(0);
/* 401 */       this.connectivityManager.requestNetwork(builder.build(), networkCallback);
/*     */     }
/*     */     
/*     */     @SuppressLint({"NewApi"})
/*     */     NetworkChangeDetector.IPAddress[] getIPAddresses(LinkProperties linkProperties) {
/* 406 */       NetworkChangeDetector.IPAddress[] ipAddresses = new NetworkChangeDetector.IPAddress[linkProperties.getLinkAddresses().size()];
/* 407 */       int i = 0;
/* 408 */       for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
/* 409 */         ipAddresses[i] = new NetworkChangeDetector.IPAddress(linkAddress.getAddress().getAddress());
/* 410 */         i++;
/*     */       } 
/* 412 */       return ipAddresses;
/*     */     }
/*     */     
/*     */     @SuppressLint({"NewApi"})
/*     */     public void releaseCallback(ConnectivityManager.NetworkCallback networkCallback) {
/* 417 */       if (supportNetworkCallback()) {
/* 418 */         Logging.d("NetworkMonitorAutoDetect", "Unregister network callback");
/* 419 */         this.connectivityManager.unregisterNetworkCallback(networkCallback);
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean supportNetworkCallback() {
/* 424 */       return (Build.VERSION.SDK_INT >= 21 && this.connectivityManager != null);
/*     */     } }
/*     */   
/*     */   static class WifiManagerDelegate {
/*     */     @Nullable
/*     */     private final Context context;
/*     */     
/*     */     WifiManagerDelegate(Context context) {
/* 432 */       this.context = context;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     WifiManagerDelegate() {
/* 438 */       this.context = null;
/*     */     }
/*     */     
/*     */     String getWifiSSID() {
/* 442 */       Intent intent = this.context.registerReceiver(null, new IntentFilter("android.net.wifi.STATE_CHANGE"));
/*     */       
/* 444 */       if (intent != null) {
/* 445 */         WifiInfo wifiInfo = (WifiInfo)intent.getParcelableExtra("wifiInfo");
/* 446 */         if (wifiInfo != null) {
/* 447 */           String ssid = wifiInfo.getSSID();
/* 448 */           if (ssid != null) {
/* 449 */             return ssid;
/*     */           }
/*     */         } 
/*     */       } 
/* 453 */       return "";
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class WifiDirectManagerDelegate
/*     */     extends BroadcastReceiver
/*     */   {
/*     */     private static final int WIFI_P2P_NETWORK_HANDLE = 0;
/*     */     
/*     */     private final Context context;
/*     */     private final NetworkChangeDetector.Observer observer;
/*     */     @Nullable
/*     */     private NetworkChangeDetector.NetworkInformation wifiP2pNetworkInfo;
/*     */     
/*     */     WifiDirectManagerDelegate(NetworkChangeDetector.Observer observer, Context context) {
/* 469 */       this.context = context;
/* 470 */       this.observer = observer;
/* 471 */       IntentFilter intentFilter = new IntentFilter();
/* 472 */       intentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
/* 473 */       intentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
/* 474 */       context.registerReceiver(this, intentFilter);
/* 475 */       if (Build.VERSION.SDK_INT > 28) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 480 */         WifiP2pManager manager = (WifiP2pManager)context.getSystemService("wifip2p");
/*     */         
/* 482 */         WifiP2pManager.Channel channel = manager.initialize(context, context.getMainLooper(), null);
/* 483 */         manager.requestGroupInfo(channel, wifiP2pGroup -> onWifiP2pGroupChange(wifiP2pGroup));
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @SuppressLint({"InlinedApi"})
/*     */     public void onReceive(Context context, Intent intent) {
/* 491 */       if ("android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(intent.getAction())) {
/* 492 */         WifiP2pGroup wifiP2pGroup = (WifiP2pGroup)intent.getParcelableExtra("p2pGroupInfo");
/* 493 */         onWifiP2pGroupChange(wifiP2pGroup);
/* 494 */       } else if ("android.net.wifi.p2p.STATE_CHANGED".equals(intent.getAction())) {
/* 495 */         int state = intent.getIntExtra("wifi_p2p_state", 0);
/* 496 */         onWifiP2pStateChange(state);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void release() {
/* 502 */       this.context.unregisterReceiver(this);
/*     */     }
/*     */     
/*     */     public List<NetworkChangeDetector.NetworkInformation> getActiveNetworkList() {
/* 506 */       if (this.wifiP2pNetworkInfo != null) {
/* 507 */         return Collections.singletonList(this.wifiP2pNetworkInfo);
/*     */       }
/*     */       
/* 510 */       return Collections.emptyList();
/*     */     }
/*     */     
/*     */     private void onWifiP2pGroupChange(@Nullable WifiP2pGroup wifiP2pGroup) {
/*     */       NetworkInterface wifiP2pInterface;
/* 515 */       if (wifiP2pGroup == null || wifiP2pGroup.getInterface() == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/*     */       try {
/* 521 */         wifiP2pInterface = NetworkInterface.getByName(wifiP2pGroup.getInterface());
/* 522 */       } catch (SocketException e) {
/* 523 */         Logging.e("NetworkMonitorAutoDetect", "Unable to get WifiP2p network interface", e);
/*     */         
/*     */         return;
/*     */       } 
/* 527 */       List<InetAddress> interfaceAddresses = Collections.list(wifiP2pInterface.getInetAddresses());
/* 528 */       NetworkChangeDetector.IPAddress[] ipAddresses = new NetworkChangeDetector.IPAddress[interfaceAddresses.size()];
/* 529 */       for (int i = 0; i < interfaceAddresses.size(); i++) {
/* 530 */         ipAddresses[i] = new NetworkChangeDetector.IPAddress(((InetAddress)interfaceAddresses.get(i)).getAddress());
/*     */       }
/*     */       
/* 533 */       this.wifiP2pNetworkInfo = new NetworkChangeDetector.NetworkInformation(wifiP2pGroup.getInterface(), NetworkChangeDetector.ConnectionType.CONNECTION_WIFI, NetworkChangeDetector.ConnectionType.CONNECTION_NONE, 0L, ipAddresses);
/*     */ 
/*     */ 
/*     */       
/* 537 */       this.observer.onNetworkConnect(this.wifiP2pNetworkInfo);
/*     */     }
/*     */ 
/*     */     
/*     */     private void onWifiP2pStateChange(int state) {
/* 542 */       if (state == 1) {
/* 543 */         this.wifiP2pNetworkInfo = null;
/* 544 */         this.observer.onNetworkDisconnect(0L);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SuppressLint({"NewApi"})
/*     */   public NetworkMonitorAutoDetect(NetworkChangeDetector.Observer observer, Context context) {
/* 573 */     this.observer = observer;
/* 574 */     this.context = context;
/* 575 */     this.connectivityManagerDelegate = new ConnectivityManagerDelegate(context);
/* 576 */     this.wifiManagerDelegate = new WifiManagerDelegate(context);
/*     */     
/* 578 */     NetworkState networkState = this.connectivityManagerDelegate.getNetworkState();
/* 579 */     this.connectionType = getConnectionType(networkState);
/* 580 */     this.wifiSSID = getWifiSSID(networkState);
/* 581 */     this.intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
/*     */     
/* 583 */     if (PeerConnectionFactory.fieldTrialsFindFullName("IncludeWifiDirect").equals("Enabled")) {
/* 584 */       this.wifiDirectManagerDelegate = new WifiDirectManagerDelegate(observer, context);
/*     */     }
/*     */     
/* 587 */     registerReceiver();
/* 588 */     if (this.connectivityManagerDelegate.supportNetworkCallback()) {
/*     */ 
/*     */       
/* 591 */       ConnectivityManager.NetworkCallback tempNetworkCallback = new ConnectivityManager.NetworkCallback();
/*     */       try {
/* 593 */         this.connectivityManagerDelegate.requestMobileNetwork(tempNetworkCallback);
/* 594 */       } catch (SecurityException e) {
/* 595 */         Logging.w("NetworkMonitorAutoDetect", "Unable to obtain permission to request a cellular network.");
/* 596 */         tempNetworkCallback = null;
/*     */       } 
/* 598 */       this.mobileNetworkCallback = tempNetworkCallback;
/* 599 */       this.allNetworkCallback = new SimpleNetworkCallback();
/* 600 */       this.connectivityManagerDelegate.registerNetworkCallback(this.allNetworkCallback);
/*     */     } else {
/* 602 */       this.mobileNetworkCallback = null;
/* 603 */       this.allNetworkCallback = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportNetworkCallback() {
/* 609 */     return this.connectivityManagerDelegate.supportNetworkCallback();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setConnectivityManagerDelegateForTests(ConnectivityManagerDelegate delegate) {
/* 616 */     this.connectivityManagerDelegate = delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setWifiManagerDelegateForTests(WifiManagerDelegate delegate) {
/* 623 */     this.wifiManagerDelegate = delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isReceiverRegisteredForTesting() {
/* 631 */     return this.isRegistered;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<NetworkChangeDetector.NetworkInformation> getActiveNetworkList() {
/* 638 */     List<NetworkChangeDetector.NetworkInformation> connectivityManagerList = this.connectivityManagerDelegate.getActiveNetworkList();
/* 639 */     if (connectivityManagerList == null) {
/* 640 */       return null;
/*     */     }
/* 642 */     ArrayList<NetworkChangeDetector.NetworkInformation> result = new ArrayList<>(connectivityManagerList);
/*     */     
/* 644 */     if (this.wifiDirectManagerDelegate != null) {
/* 645 */       result.addAll(this.wifiDirectManagerDelegate.getActiveNetworkList());
/*     */     }
/* 647 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 652 */     if (this.allNetworkCallback != null) {
/* 653 */       this.connectivityManagerDelegate.releaseCallback(this.allNetworkCallback);
/*     */     }
/* 655 */     if (this.mobileNetworkCallback != null) {
/* 656 */       this.connectivityManagerDelegate.releaseCallback(this.mobileNetworkCallback);
/*     */     }
/* 658 */     if (this.wifiDirectManagerDelegate != null) {
/* 659 */       this.wifiDirectManagerDelegate.release();
/*     */     }
/* 661 */     unregisterReceiver();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void registerReceiver() {
/* 668 */     if (this.isRegistered) {
/*     */       return;
/*     */     }
/* 671 */     this.isRegistered = true;
/* 672 */     this.context.registerReceiver(this, this.intentFilter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void unregisterReceiver() {
/* 679 */     if (!this.isRegistered) {
/*     */       return;
/*     */     }
/* 682 */     this.isRegistered = false;
/* 683 */     this.context.unregisterReceiver(this);
/*     */   }
/*     */   
/*     */   public NetworkState getCurrentNetworkState() {
/* 687 */     return this.connectivityManagerDelegate.getNetworkState();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getDefaultNetId() {
/* 697 */     return this.connectivityManagerDelegate.getDefaultNetId();
/*     */   }
/*     */ 
/*     */   
/*     */   private static NetworkChangeDetector.ConnectionType getConnectionType(boolean isConnected, int networkType, int networkSubtype) {
/* 702 */     if (!isConnected) {
/* 703 */       return NetworkChangeDetector.ConnectionType.CONNECTION_NONE;
/*     */     }
/*     */     
/* 706 */     switch (networkType) {
/*     */       case 9:
/* 708 */         return NetworkChangeDetector.ConnectionType.CONNECTION_ETHERNET;
/*     */       case 1:
/* 710 */         return NetworkChangeDetector.ConnectionType.CONNECTION_WIFI;
/*     */       case 6:
/* 712 */         return NetworkChangeDetector.ConnectionType.CONNECTION_4G;
/*     */       case 7:
/* 714 */         return NetworkChangeDetector.ConnectionType.CONNECTION_BLUETOOTH;
/*     */       
/*     */       case 0:
/* 717 */         switch (networkSubtype) {
/*     */           case 1:
/*     */           case 2:
/*     */           case 4:
/*     */           case 7:
/*     */           case 11:
/*     */           case 16:
/* 724 */             return NetworkChangeDetector.ConnectionType.CONNECTION_2G;
/*     */           case 3:
/*     */           case 5:
/*     */           case 6:
/*     */           case 8:
/*     */           case 9:
/*     */           case 10:
/*     */           case 12:
/*     */           case 14:
/*     */           case 15:
/*     */           case 17:
/* 735 */             return NetworkChangeDetector.ConnectionType.CONNECTION_3G;
/*     */           case 13:
/*     */           case 18:
/* 738 */             return NetworkChangeDetector.ConnectionType.CONNECTION_4G;
/*     */           case 20:
/* 740 */             return NetworkChangeDetector.ConnectionType.CONNECTION_5G;
/*     */         } 
/* 742 */         return NetworkChangeDetector.ConnectionType.CONNECTION_UNKNOWN_CELLULAR;
/*     */       
/*     */       case 17:
/* 745 */         return NetworkChangeDetector.ConnectionType.CONNECTION_VPN;
/*     */     } 
/* 747 */     return NetworkChangeDetector.ConnectionType.CONNECTION_UNKNOWN;
/*     */   }
/*     */ 
/*     */   
/*     */   public static NetworkChangeDetector.ConnectionType getConnectionType(NetworkState networkState) {
/* 752 */     return getConnectionType(networkState.isConnected(), networkState.getNetworkType(), networkState
/* 753 */         .getNetworkSubType());
/*     */   }
/*     */ 
/*     */   
/*     */   public NetworkChangeDetector.ConnectionType getCurrentConnectionType() {
/* 758 */     return getConnectionType(getCurrentNetworkState());
/*     */   }
/*     */ 
/*     */   
/*     */   private static NetworkChangeDetector.ConnectionType getUnderlyingConnectionTypeForVpn(NetworkState networkState) {
/* 763 */     if (networkState.getNetworkType() != 17) {
/* 764 */       return NetworkChangeDetector.ConnectionType.CONNECTION_NONE;
/*     */     }
/* 766 */     return getConnectionType(networkState.isConnected(), networkState
/* 767 */         .getUnderlyingNetworkTypeForVpn(), networkState
/* 768 */         .getUnderlyingNetworkSubtypeForVpn());
/*     */   }
/*     */   
/*     */   private String getWifiSSID(NetworkState networkState) {
/* 772 */     if (getConnectionType(networkState) != NetworkChangeDetector.ConnectionType.CONNECTION_WIFI)
/* 773 */       return ""; 
/* 774 */     return this.wifiManagerDelegate.getWifiSSID();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onReceive(Context context, Intent intent) {
/* 780 */     NetworkState networkState = getCurrentNetworkState();
/* 781 */     if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
/* 782 */       connectionTypeChanged(networkState);
/*     */     }
/*     */   }
/*     */   
/*     */   private void connectionTypeChanged(NetworkState networkState) {
/* 787 */     NetworkChangeDetector.ConnectionType newConnectionType = getConnectionType(networkState);
/* 788 */     String newWifiSSID = getWifiSSID(networkState);
/* 789 */     if (newConnectionType == this.connectionType && newWifiSSID.equals(this.wifiSSID)) {
/*     */       return;
/*     */     }
/* 792 */     this.connectionType = newConnectionType;
/* 793 */     this.wifiSSID = newWifiSSID;
/* 794 */     Logging.d("NetworkMonitorAutoDetect", "Network connectivity changed, type is: " + this.connectionType);
/* 795 */     this.observer.onConnectionTypeChanged(newConnectionType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SuppressLint({"NewApi"})
/*     */   private static long networkToNetId(Network network) {
/* 805 */     if (Build.VERSION.SDK_INT >= 23) {
/* 806 */       return network.getNetworkHandle();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 811 */     return Integer.parseInt(network.toString());
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/NetworkMonitorAutoDetect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */