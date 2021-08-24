/*     */ package org.webrtc;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.os.Build;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkMonitor
/*     */ {
/*     */   private static final String TAG = "NetworkMonitor";
/*     */   
/*     */   private static class InstanceHolder
/*     */   {
/*  41 */     static final NetworkMonitor instance = new NetworkMonitor();
/*     */   }
/*     */ 
/*     */   
/*  45 */   private NetworkChangeDetectorFactory networkChangeDetectorFactory = new NetworkChangeDetectorFactory()
/*     */     {
/*     */       
/*     */       public NetworkChangeDetector create(NetworkChangeDetector.Observer observer, Context context)
/*     */       {
/*  50 */         return new NetworkMonitorAutoDetect(observer, context);
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private final ArrayList<Long> nativeNetworkObservers;
/*     */   
/*     */   private final ArrayList<NetworkObserver> networkObservers;
/*     */   
/*  59 */   private final Object networkChangeDetectorLock = new Object();
/*     */   
/*     */   @Nullable
/*     */   private NetworkChangeDetector networkChangeDetector;
/*     */   
/*     */   private int numObservers;
/*     */   private volatile NetworkChangeDetector.ConnectionType currentConnectionType;
/*     */   
/*     */   private NetworkMonitor() {
/*  68 */     this.nativeNetworkObservers = new ArrayList<>();
/*  69 */     this.networkObservers = new ArrayList<>();
/*  70 */     this.numObservers = 0;
/*  71 */     this.currentConnectionType = NetworkChangeDetector.ConnectionType.CONNECTION_UNKNOWN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNetworkChangeDetectorFactory(NetworkChangeDetectorFactory factory) {
/*  79 */     assertIsTrue((this.numObservers == 0));
/*  80 */     this.networkChangeDetectorFactory = factory;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void init(Context context) {}
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   public static NetworkMonitor getInstance() {
/*  90 */     return InstanceHolder.instance;
/*     */   }
/*     */   
/*     */   private static void assertIsTrue(boolean condition) {
/*  94 */     if (!condition) {
/*  95 */       throw new AssertionError("Expected to be true");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void startMonitoring(Context applicationContext) {
/* 105 */     synchronized (this.networkChangeDetectorLock) {
/* 106 */       this.numObservers++;
/* 107 */       if (this.networkChangeDetector == null) {
/* 108 */         this.networkChangeDetector = createNetworkChangeDetector(applicationContext);
/*     */       }
/* 110 */       this.currentConnectionType = this.networkChangeDetector.getCurrentConnectionType();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void startMonitoring() {
/* 117 */     startMonitoring(ContextUtils.getApplicationContext());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   private void startMonitoring(@Nullable Context applicationContext, long nativeObserver) {
/* 127 */     Logging.d("NetworkMonitor", "Start monitoring with native observer " + nativeObserver);
/*     */     
/* 129 */     startMonitoring(
/* 130 */         (applicationContext != null) ? applicationContext : ContextUtils.getApplicationContext());
/*     */     
/* 132 */     synchronized (this.nativeNetworkObservers) {
/* 133 */       this.nativeNetworkObservers.add(Long.valueOf(nativeObserver));
/*     */     } 
/* 135 */     updateObserverActiveNetworkList(nativeObserver);
/*     */ 
/*     */     
/* 138 */     notifyObserversOfConnectionTypeChange(this.currentConnectionType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stopMonitoring() {
/* 146 */     synchronized (this.networkChangeDetectorLock) {
/* 147 */       if (--this.numObservers == 0) {
/* 148 */         this.networkChangeDetector.destroy();
/* 149 */         this.networkChangeDetector = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private void stopMonitoring(long nativeObserver) {
/* 156 */     Logging.d("NetworkMonitor", "Stop monitoring with native observer " + nativeObserver);
/* 157 */     stopMonitoring();
/* 158 */     synchronized (this.nativeNetworkObservers) {
/* 159 */       this.nativeNetworkObservers.remove(Long.valueOf(nativeObserver));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   private boolean networkBindingSupported() {
/* 166 */     synchronized (this.networkChangeDetectorLock) {
/* 167 */       return (this.networkChangeDetector != null && this.networkChangeDetector.supportNetworkCallback());
/*     */     } 
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private static int androidSdkInt() {
/* 173 */     return Build.VERSION.SDK_INT;
/*     */   }
/*     */   
/*     */   private NetworkChangeDetector.ConnectionType getCurrentConnectionType() {
/* 177 */     return this.currentConnectionType;
/*     */   }
/*     */   
/*     */   private NetworkChangeDetector createNetworkChangeDetector(Context appContext) {
/* 181 */     return this.networkChangeDetectorFactory.create(new NetworkChangeDetector.Observer()
/*     */         {
/*     */           public void onConnectionTypeChanged(NetworkChangeDetector.ConnectionType newConnectionType) {
/* 184 */             NetworkMonitor.this.updateCurrentConnectionType(newConnectionType);
/*     */           }
/*     */ 
/*     */           
/*     */           public void onNetworkConnect(NetworkChangeDetector.NetworkInformation networkInfo) {
/* 189 */             NetworkMonitor.this.notifyObserversOfNetworkConnect(networkInfo);
/*     */           }
/*     */ 
/*     */           
/*     */           public void onNetworkDisconnect(long networkHandle) {
/* 194 */             NetworkMonitor.this.notifyObserversOfNetworkDisconnect(networkHandle);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public void onNetworkPreference(List<NetworkChangeDetector.ConnectionType> types, int preference) {
/* 200 */             NetworkMonitor.this.notifyObserversOfNetworkPreference(types, preference);
/*     */           }
/*     */         },appContext);
/*     */   }
/*     */   
/*     */   private void updateCurrentConnectionType(NetworkChangeDetector.ConnectionType newConnectionType) {
/* 206 */     this.currentConnectionType = newConnectionType;
/* 207 */     notifyObserversOfConnectionTypeChange(newConnectionType);
/*     */   }
/*     */ 
/*     */   
/*     */   private void notifyObserversOfConnectionTypeChange(NetworkChangeDetector.ConnectionType newConnectionType) {
/*     */     List<NetworkObserver> javaObservers;
/* 213 */     List<Long> nativeObservers = getNativeNetworkObserversSync();
/* 214 */     for (Long nativeObserver : nativeObservers) {
/* 215 */       nativeNotifyConnectionTypeChanged(nativeObserver.longValue());
/*     */     }
/*     */ 
/*     */     
/* 219 */     synchronized (this.networkObservers) {
/* 220 */       javaObservers = new ArrayList<>(this.networkObservers);
/*     */     } 
/* 222 */     for (NetworkObserver observer : javaObservers) {
/* 223 */       observer.onConnectionTypeChanged(newConnectionType);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void notifyObserversOfNetworkConnect(NetworkChangeDetector.NetworkInformation networkInfo) {
/* 229 */     List<Long> nativeObservers = getNativeNetworkObserversSync();
/* 230 */     for (Long nativeObserver : nativeObservers) {
/* 231 */       nativeNotifyOfNetworkConnect(nativeObserver.longValue(), networkInfo);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyObserversOfNetworkDisconnect(long networkHandle) {
/* 236 */     List<Long> nativeObservers = getNativeNetworkObserversSync();
/* 237 */     for (Long nativeObserver : nativeObservers) {
/* 238 */       nativeNotifyOfNetworkDisconnect(nativeObserver.longValue(), networkHandle);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void notifyObserversOfNetworkPreference(List<NetworkChangeDetector.ConnectionType> types, int preference) {
/* 244 */     List<Long> nativeObservers = getNativeNetworkObserversSync();
/* 245 */     for (NetworkChangeDetector.ConnectionType type : types) {
/* 246 */       for (Long nativeObserver : nativeObservers) {
/* 247 */         nativeNotifyOfNetworkPreference(nativeObserver.longValue(), type, preference);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void updateObserverActiveNetworkList(long nativeObserver) {
/*     */     List<NetworkChangeDetector.NetworkInformation> networkInfoList;
/* 254 */     synchronized (this.networkChangeDetectorLock) {
/*     */       
/* 256 */       networkInfoList = (this.networkChangeDetector == null) ? null : this.networkChangeDetector.getActiveNetworkList();
/*     */     } 
/* 258 */     if (networkInfoList == null || networkInfoList.size() == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 263 */     NetworkChangeDetector.NetworkInformation[] networkInfos = new NetworkChangeDetector.NetworkInformation[networkInfoList.size()];
/* 264 */     networkInfos = networkInfoList.<NetworkChangeDetector.NetworkInformation>toArray(networkInfos);
/* 265 */     nativeNotifyOfActiveNetworkList(nativeObserver, networkInfos);
/*     */   }
/*     */   
/*     */   private List<Long> getNativeNetworkObserversSync() {
/* 269 */     synchronized (this.nativeNetworkObservers) {
/* 270 */       return new ArrayList<>(this.nativeNetworkObservers);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void addNetworkObserver(NetworkObserver observer) {
/* 281 */     getInstance().addObserver(observer);
/*     */   }
/*     */   
/*     */   public void addObserver(NetworkObserver observer) {
/* 285 */     synchronized (this.networkObservers) {
/* 286 */       this.networkObservers.add(observer);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void removeNetworkObserver(NetworkObserver observer) {
/* 297 */     getInstance().removeObserver(observer);
/*     */   }
/*     */   
/*     */   public void removeObserver(NetworkObserver observer) {
/* 301 */     synchronized (this.networkObservers) {
/* 302 */       this.networkObservers.remove(observer);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isOnline() {
/* 308 */     NetworkChangeDetector.ConnectionType connectionType = getInstance().getCurrentConnectionType();
/* 309 */     return (connectionType != NetworkChangeDetector.ConnectionType.CONNECTION_NONE);
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
/*     */   @Nullable
/*     */   NetworkChangeDetector getNetworkChangeDetector() {
/* 329 */     synchronized (this.networkChangeDetectorLock) {
/* 330 */       return this.networkChangeDetector;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   int getNumObservers() {
/* 336 */     synchronized (this.networkChangeDetectorLock) {
/* 337 */       return this.numObservers;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static NetworkMonitorAutoDetect createAndSetAutoDetectForTest(Context context) {
/* 343 */     NetworkMonitor networkMonitor = getInstance();
/*     */     
/* 345 */     NetworkChangeDetector networkChangeDetector = networkMonitor.createNetworkChangeDetector(context);
/* 346 */     networkMonitor.networkChangeDetector = networkChangeDetector;
/* 347 */     return (NetworkMonitorAutoDetect)networkChangeDetector;
/*     */   }
/*     */   
/*     */   private native void nativeNotifyConnectionTypeChanged(long paramLong);
/*     */   
/*     */   private native void nativeNotifyOfNetworkConnect(long paramLong, NetworkChangeDetector.NetworkInformation paramNetworkInformation);
/*     */   
/*     */   private native void nativeNotifyOfNetworkDisconnect(long paramLong1, long paramLong2);
/*     */   
/*     */   private native void nativeNotifyOfActiveNetworkList(long paramLong, NetworkChangeDetector.NetworkInformation[] paramArrayOfNetworkInformation);
/*     */   
/*     */   private native void nativeNotifyOfNetworkPreference(long paramLong, NetworkChangeDetector.ConnectionType paramConnectionType, int paramInt);
/*     */   
/*     */   public static interface NetworkObserver {
/*     */     void onConnectionTypeChanged(NetworkChangeDetector.ConnectionType param1ConnectionType);
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/NetworkMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */