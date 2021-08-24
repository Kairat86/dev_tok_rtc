/*     */ package org.webrtc;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.os.Process;
/*     */ import androidx.annotation.Nullable;
/*     */ import java.util.List;
/*     */ import org.webrtc.audio.AudioDeviceModule;
/*     */ import org.webrtc.audio.JavaAudioDeviceModule;
/*     */ 
/*     */ 
/*     */ public class PeerConnectionFactory
/*     */ {
/*     */   public static final String TRIAL_ENABLED = "Enabled";
/*     */   @Deprecated
/*     */   public static final String VIDEO_FRAME_EMIT_TRIAL = "VideoFrameEmit";
/*     */   private static final String TAG = "PeerConnectionFactory";
/*     */   private static final String VIDEO_CAPTURER_THREAD_NAME = "VideoCapturerThread";
/*     */   private static volatile boolean internalTracerInitialized;
/*     */   @Nullable
/*     */   private static ThreadInfo staticNetworkThread;
/*     */   @Nullable
/*     */   private static ThreadInfo staticWorkerThread;
/*     */   @Nullable
/*     */   private static ThreadInfo staticSignalingThread;
/*     */   private long nativeFactory;
/*     */   @Nullable
/*     */   private volatile ThreadInfo networkThread;
/*     */   @Nullable
/*     */   private volatile ThreadInfo workerThread;
/*     */   @Nullable
/*     */   private volatile ThreadInfo signalingThread;
/*     */   
/*     */   private static class ThreadInfo
/*     */   {
/*     */     final Thread thread;
/*     */     final int tid;
/*     */     
/*     */     public static ThreadInfo getCurrent() {
/*  39 */       return new ThreadInfo(Thread.currentThread(), Process.myTid());
/*     */     }
/*     */     
/*     */     private ThreadInfo(Thread thread, int tid) {
/*  43 */       this.thread = thread;
/*  44 */       this.tid = tid;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class InitializationOptions
/*     */   {
/*     */     final Context applicationContext;
/*     */ 
/*     */     
/*     */     final String fieldTrials;
/*     */ 
/*     */     
/*     */     final boolean enableInternalTracer;
/*     */ 
/*     */     
/*     */     final NativeLibraryLoader nativeLibraryLoader;
/*     */     
/*     */     final String nativeLibraryName;
/*     */     
/*     */     @Nullable
/*     */     Loggable loggable;
/*     */     
/*     */     @Nullable
/*     */     Logging.Severity loggableSeverity;
/*     */ 
/*     */     
/*     */     private InitializationOptions(Context applicationContext, String fieldTrials, boolean enableInternalTracer, NativeLibraryLoader nativeLibraryLoader, String nativeLibraryName, @Nullable Loggable loggable, @Nullable Logging.Severity loggableSeverity) {
/*  73 */       this.applicationContext = applicationContext;
/*  74 */       this.fieldTrials = fieldTrials;
/*  75 */       this.enableInternalTracer = enableInternalTracer;
/*  76 */       this.nativeLibraryLoader = nativeLibraryLoader;
/*  77 */       this.nativeLibraryName = nativeLibraryName;
/*  78 */       this.loggable = loggable;
/*  79 */       this.loggableSeverity = loggableSeverity;
/*     */     }
/*     */     
/*     */     public static Builder builder(Context applicationContext) {
/*  83 */       return new Builder(applicationContext);
/*     */     }
/*     */     
/*     */     public static class Builder {
/*     */       private final Context applicationContext;
/*  88 */       private String fieldTrials = "";
/*     */       private boolean enableInternalTracer;
/*  90 */       private NativeLibraryLoader nativeLibraryLoader = new NativeLibrary.DefaultLoader();
/*  91 */       private String nativeLibraryName = "jingle_peerconnection_so"; @Nullable
/*     */       private Loggable loggable; @Nullable
/*     */       private Logging.Severity loggableSeverity;
/*     */       
/*     */       Builder(Context applicationContext) {
/*  96 */         this.applicationContext = applicationContext;
/*     */       }
/*     */       
/*     */       public Builder setFieldTrials(String fieldTrials) {
/* 100 */         this.fieldTrials = fieldTrials;
/* 101 */         return this;
/*     */       }
/*     */       
/*     */       public Builder setEnableInternalTracer(boolean enableInternalTracer) {
/* 105 */         this.enableInternalTracer = enableInternalTracer;
/* 106 */         return this;
/*     */       }
/*     */       
/*     */       public Builder setNativeLibraryLoader(NativeLibraryLoader nativeLibraryLoader) {
/* 110 */         this.nativeLibraryLoader = nativeLibraryLoader;
/* 111 */         return this;
/*     */       }
/*     */       
/*     */       public Builder setNativeLibraryName(String nativeLibraryName) {
/* 115 */         this.nativeLibraryName = nativeLibraryName;
/* 116 */         return this;
/*     */       }
/*     */       
/*     */       public Builder setInjectableLogger(Loggable loggable, Logging.Severity severity) {
/* 120 */         this.loggable = loggable;
/* 121 */         this.loggableSeverity = severity;
/* 122 */         return this;
/*     */       }
/*     */       
/*     */       public PeerConnectionFactory.InitializationOptions createInitializationOptions() {
/* 126 */         return new PeerConnectionFactory.InitializationOptions(this.applicationContext, this.fieldTrials, this.enableInternalTracer, this.nativeLibraryLoader, this.nativeLibraryName, this.loggable, this.loggableSeverity);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Options
/*     */   {
/*     */     static final int ADAPTER_TYPE_UNKNOWN = 0;
/*     */     
/*     */     static final int ADAPTER_TYPE_ETHERNET = 1;
/*     */     
/*     */     static final int ADAPTER_TYPE_WIFI = 2;
/*     */     
/*     */     static final int ADAPTER_TYPE_CELLULAR = 4;
/*     */     
/*     */     static final int ADAPTER_TYPE_VPN = 8;
/*     */     static final int ADAPTER_TYPE_LOOPBACK = 16;
/*     */     static final int ADAPTER_TYPE_ANY = 32;
/*     */     public int networkIgnoreMask;
/*     */     public boolean disableEncryption;
/*     */     public boolean disableNetworkMonitor;
/*     */     
/*     */     @CalledByNative("Options")
/*     */     int getNetworkIgnoreMask() {
/* 151 */       return this.networkIgnoreMask;
/*     */     }
/*     */     
/*     */     @CalledByNative("Options")
/*     */     boolean getDisableEncryption() {
/* 156 */       return this.disableEncryption;
/*     */     }
/*     */     
/*     */     @CalledByNative("Options")
/*     */     boolean getDisableNetworkMonitor() {
/* 161 */       return this.disableNetworkMonitor;
/*     */     } }
/*     */   
/*     */   public static class Builder { @Nullable
/*     */     private PeerConnectionFactory.Options options;
/*     */     @Nullable
/*     */     private AudioDeviceModule audioDeviceModule;
/* 168 */     private AudioEncoderFactoryFactory audioEncoderFactoryFactory = new BuiltinAudioEncoderFactoryFactory();
/*     */     
/* 170 */     private AudioDecoderFactoryFactory audioDecoderFactoryFactory = new BuiltinAudioDecoderFactoryFactory();
/*     */     
/*     */     @Nullable
/*     */     private VideoEncoderFactory videoEncoderFactory;
/*     */     
/*     */     @Nullable
/*     */     private VideoDecoderFactory videoDecoderFactory;
/*     */     
/*     */     @Nullable
/*     */     private AudioProcessingFactory audioProcessingFactory;
/*     */ 
/*     */     
/*     */     public Builder setOptions(PeerConnectionFactory.Options options) {
/* 183 */       this.options = options;
/* 184 */       return this; } @Nullable
/*     */     private FecControllerFactoryFactoryInterface fecControllerFactoryFactory; @Nullable
/*     */     private NetworkControllerFactoryFactory networkControllerFactoryFactory; @Nullable
/*     */     private NetworkStatePredictorFactoryFactory networkStatePredictorFactoryFactory; @Nullable
/* 188 */     private NetEqFactoryFactory neteqFactoryFactory; public Builder setAudioDeviceModule(AudioDeviceModule audioDeviceModule) { this.audioDeviceModule = audioDeviceModule;
/* 189 */       return this; }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setAudioEncoderFactoryFactory(AudioEncoderFactoryFactory audioEncoderFactoryFactory) {
/* 194 */       if (audioEncoderFactoryFactory == null) {
/* 195 */         throw new IllegalArgumentException("PeerConnectionFactory.Builder does not accept a null AudioEncoderFactoryFactory.");
/*     */       }
/*     */       
/* 198 */       this.audioEncoderFactoryFactory = audioEncoderFactoryFactory;
/* 199 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder setAudioDecoderFactoryFactory(AudioDecoderFactoryFactory audioDecoderFactoryFactory) {
/* 204 */       if (audioDecoderFactoryFactory == null) {
/* 205 */         throw new IllegalArgumentException("PeerConnectionFactory.Builder does not accept a null AudioDecoderFactoryFactory.");
/*     */       }
/*     */       
/* 208 */       this.audioDecoderFactoryFactory = audioDecoderFactoryFactory;
/* 209 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setVideoEncoderFactory(VideoEncoderFactory videoEncoderFactory) {
/* 213 */       this.videoEncoderFactory = videoEncoderFactory;
/* 214 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setVideoDecoderFactory(VideoDecoderFactory videoDecoderFactory) {
/* 218 */       this.videoDecoderFactory = videoDecoderFactory;
/* 219 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setAudioProcessingFactory(AudioProcessingFactory audioProcessingFactory) {
/* 223 */       if (audioProcessingFactory == null) {
/* 224 */         throw new NullPointerException("PeerConnectionFactory builder does not accept a null AudioProcessingFactory.");
/*     */       }
/*     */       
/* 227 */       this.audioProcessingFactory = audioProcessingFactory;
/* 228 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder setFecControllerFactoryFactoryInterface(FecControllerFactoryFactoryInterface fecControllerFactoryFactory) {
/* 233 */       this.fecControllerFactoryFactory = fecControllerFactoryFactory;
/* 234 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder setNetworkControllerFactoryFactory(NetworkControllerFactoryFactory networkControllerFactoryFactory) {
/* 239 */       this.networkControllerFactoryFactory = networkControllerFactoryFactory;
/* 240 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder setNetworkStatePredictorFactoryFactory(NetworkStatePredictorFactoryFactory networkStatePredictorFactoryFactory) {
/* 245 */       this.networkStatePredictorFactoryFactory = networkStatePredictorFactoryFactory;
/* 246 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setNetEqFactoryFactory(NetEqFactoryFactory neteqFactoryFactory) {
/* 256 */       this.neteqFactoryFactory = neteqFactoryFactory;
/* 257 */       return this;
/*     */     }
/*     */     
/*     */     public PeerConnectionFactory createPeerConnectionFactory() {
/* 261 */       PeerConnectionFactory.checkInitializeHasBeenCalled();
/* 262 */       if (this.audioDeviceModule == null) {
/* 263 */         this
/* 264 */           .audioDeviceModule = (AudioDeviceModule)JavaAudioDeviceModule.builder(ContextUtils.getApplicationContext()).createAudioDeviceModule();
/*     */       }
/* 266 */       return PeerConnectionFactory.nativeCreatePeerConnectionFactory(ContextUtils.getApplicationContext(), this.options, this.audioDeviceModule
/* 267 */           .getNativeAudioDeviceModulePointer(), this.audioEncoderFactoryFactory
/* 268 */           .createNativeAudioEncoderFactory(), this.audioDecoderFactoryFactory
/* 269 */           .createNativeAudioDecoderFactory(), this.videoEncoderFactory, this.videoDecoderFactory, 
/*     */           
/* 271 */           (this.audioProcessingFactory == null) ? 0L : this.audioProcessingFactory.createNative(), 
/* 272 */           (this.fecControllerFactoryFactory == null) ? 0L : this.fecControllerFactoryFactory.createNative(), 
/* 273 */           (this.networkControllerFactoryFactory == null) ? 
/* 274 */           0L : 
/* 275 */           this.networkControllerFactoryFactory.createNativeNetworkControllerFactory(), 
/* 276 */           (this.networkStatePredictorFactoryFactory == null) ? 
/* 277 */           0L : 
/* 278 */           this.networkStatePredictorFactoryFactory.createNativeNetworkStatePredictorFactory(), 
/* 279 */           (this.neteqFactoryFactory == null) ? 0L : this.neteqFactoryFactory.createNativeNetEqFactory());
/*     */     }
/*     */     private Builder() {} }
/*     */   
/*     */   public static Builder builder() {
/* 284 */     return new Builder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void initialize(InitializationOptions options) {
/* 293 */     ContextUtils.initialize(options.applicationContext);
/* 294 */     NativeLibrary.initialize(options.nativeLibraryLoader, options.nativeLibraryName);
/* 295 */     nativeInitializeAndroidGlobals();
/* 296 */     nativeInitializeFieldTrials(options.fieldTrials);
/* 297 */     if (options.enableInternalTracer && !internalTracerInitialized) {
/* 298 */       initializeInternalTracer();
/*     */     }
/* 300 */     if (options.loggable != null) {
/* 301 */       Logging.injectLoggable(options.loggable, options.loggableSeverity);
/* 302 */       nativeInjectLoggable(new JNILogging(options.loggable), options.loggableSeverity.ordinal());
/*     */     } else {
/* 304 */       Logging.d("PeerConnectionFactory", "PeerConnectionFactory was initialized without an injected Loggable. Any existing Loggable will be deleted.");
/*     */ 
/*     */       
/* 307 */       Logging.deleteInjectedLoggable();
/* 308 */       nativeDeleteLoggable();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void checkInitializeHasBeenCalled() {
/* 313 */     if (!NativeLibrary.isLoaded() || ContextUtils.getApplicationContext() == null) {
/* 314 */       throw new IllegalStateException("PeerConnectionFactory.initialize was not called before creating a PeerConnectionFactory.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void initializeInternalTracer() {
/* 321 */     internalTracerInitialized = true;
/* 322 */     nativeInitializeInternalTracer();
/*     */   }
/*     */   
/*     */   public static void shutdownInternalTracer() {
/* 326 */     internalTracerInitialized = false;
/* 327 */     nativeShutdownInternalTracer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void initializeFieldTrials(String fieldTrialsInitString) {
/* 335 */     nativeInitializeFieldTrials(fieldTrialsInitString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String fieldTrialsFindFullName(String name) {
/* 346 */     return NativeLibrary.isLoaded() ? nativeFindFieldTrialsFullName(name) : "";
/*     */   }
/*     */   
/*     */   public static boolean startInternalTracingCapture(String tracingFilename) {
/* 350 */     return nativeStartInternalTracingCapture(tracingFilename);
/*     */   }
/*     */   
/*     */   public static void stopInternalTracingCapture() {
/* 354 */     nativeStopInternalTracingCapture();
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   PeerConnectionFactory(long nativeFactory) {
/* 359 */     checkInitializeHasBeenCalled();
/* 360 */     if (nativeFactory == 0L) {
/* 361 */       throw new RuntimeException("Failed to initialize PeerConnectionFactory!");
/*     */     }
/* 363 */     this.nativeFactory = nativeFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   PeerConnection createPeerConnectionInternal(PeerConnection.RTCConfiguration rtcConfig, MediaConstraints constraints, PeerConnection.Observer observer, SSLCertificateVerifier sslCertificateVerifier) {
/* 373 */     checkPeerConnectionFactoryExists();
/* 374 */     long nativeObserver = PeerConnection.createNativePeerConnectionObserver(observer);
/* 375 */     if (nativeObserver == 0L) {
/* 376 */       return null;
/*     */     }
/* 378 */     long nativePeerConnection = nativeCreatePeerConnection(this.nativeFactory, rtcConfig, constraints, nativeObserver, sslCertificateVerifier);
/*     */     
/* 380 */     if (nativePeerConnection == 0L) {
/* 381 */       return null;
/*     */     }
/* 383 */     return new PeerConnection(nativePeerConnection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, MediaConstraints constraints, PeerConnection.Observer observer) {
/* 394 */     return createPeerConnectionInternal(rtcConfig, constraints, observer, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public PeerConnection createPeerConnection(List<PeerConnection.IceServer> iceServers, MediaConstraints constraints, PeerConnection.Observer observer) {
/* 406 */     PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
/* 407 */     return createPeerConnection(rtcConfig, constraints, observer);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PeerConnection createPeerConnection(List<PeerConnection.IceServer> iceServers, PeerConnection.Observer observer) {
/* 413 */     PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
/* 414 */     return createPeerConnection(rtcConfig, observer);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, PeerConnection.Observer observer) {
/* 420 */     return createPeerConnection(rtcConfig, (MediaConstraints)null, observer);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, PeerConnectionDependencies dependencies) {
/* 426 */     return createPeerConnectionInternal(rtcConfig, null, dependencies
/* 427 */         .getObserver(), dependencies.getSSLCertificateVerifier());
/*     */   }
/*     */   
/*     */   public MediaStream createLocalMediaStream(String label) {
/* 431 */     checkPeerConnectionFactoryExists();
/* 432 */     return new MediaStream(nativeCreateLocalMediaStream(this.nativeFactory, label));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VideoSource createVideoSource(boolean isScreencast, boolean alignTimestamps) {
/* 443 */     checkPeerConnectionFactoryExists();
/* 444 */     return new VideoSource(nativeCreateVideoSource(this.nativeFactory, isScreencast, alignTimestamps));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VideoSource createVideoSource(boolean isScreencast) {
/* 453 */     return createVideoSource(isScreencast, true);
/*     */   }
/*     */   
/*     */   public VideoTrack createVideoTrack(String id, VideoSource source) {
/* 457 */     checkPeerConnectionFactoryExists();
/* 458 */     return new VideoTrack(
/* 459 */         nativeCreateVideoTrack(this.nativeFactory, id, source.getNativeVideoTrackSource()));
/*     */   }
/*     */   
/*     */   public AudioSource createAudioSource(MediaConstraints constraints) {
/* 463 */     checkPeerConnectionFactoryExists();
/* 464 */     return new AudioSource(nativeCreateAudioSource(this.nativeFactory, constraints));
/*     */   }
/*     */   
/*     */   public AudioTrack createAudioTrack(String id, AudioSource source) {
/* 468 */     checkPeerConnectionFactoryExists();
/* 469 */     return new AudioTrack(nativeCreateAudioTrack(this.nativeFactory, id, source.getNativeAudioSource()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean startAecDump(int file_descriptor, int filesize_limit_bytes) {
/* 476 */     checkPeerConnectionFactoryExists();
/* 477 */     return nativeStartAecDump(this.nativeFactory, file_descriptor, filesize_limit_bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void stopAecDump() {
/* 483 */     checkPeerConnectionFactoryExists();
/* 484 */     nativeStopAecDump(this.nativeFactory);
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 488 */     checkPeerConnectionFactoryExists();
/* 489 */     nativeFreeFactory(this.nativeFactory);
/* 490 */     this.networkThread = null;
/* 491 */     this.workerThread = null;
/* 492 */     this.signalingThread = null;
/* 493 */     this.nativeFactory = 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getNativePeerConnectionFactory() {
/* 498 */     checkPeerConnectionFactoryExists();
/* 499 */     return nativeGetNativePeerConnectionFactory(this.nativeFactory);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getNativeOwnedFactoryAndThreads() {
/* 504 */     checkPeerConnectionFactoryExists();
/* 505 */     return this.nativeFactory;
/*     */   }
/*     */   
/*     */   private void checkPeerConnectionFactoryExists() {
/* 509 */     if (this.nativeFactory == 0L) {
/* 510 */       throw new IllegalStateException("PeerConnectionFactory has been disposed.");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void printStackTrace(@Nullable ThreadInfo threadInfo, boolean printNativeStackTrace) {
/* 516 */     if (threadInfo == null) {
/*     */       return;
/*     */     }
/*     */     
/* 520 */     String threadName = threadInfo.thread.getName();
/* 521 */     StackTraceElement[] stackTraces = threadInfo.thread.getStackTrace();
/* 522 */     if (stackTraces.length > 0) {
/* 523 */       Logging.w("PeerConnectionFactory", threadName + " stacktrace:");
/* 524 */       for (StackTraceElement stackTrace : stackTraces) {
/* 525 */         Logging.w("PeerConnectionFactory", stackTrace.toString());
/*     */       }
/*     */     } 
/* 528 */     if (printNativeStackTrace) {
/*     */ 
/*     */       
/* 531 */       Logging.w("PeerConnectionFactory", "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***");
/* 532 */       Logging.w("PeerConnectionFactory", "pid: " + 
/* 533 */           Process.myPid() + ", tid: " + threadInfo.tid + ", name: " + threadName + "  >>> WebRTC <<<");
/*     */       
/* 535 */       nativePrintStackTrace(threadInfo.tid);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void printStackTraces() {
/* 542 */     printStackTrace(staticNetworkThread, false);
/* 543 */     printStackTrace(staticWorkerThread, false);
/* 544 */     printStackTrace(staticSignalingThread, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printInternalStackTraces(boolean printNativeStackTraces) {
/* 553 */     printStackTrace(this.signalingThread, printNativeStackTraces);
/* 554 */     printStackTrace(this.workerThread, printNativeStackTraces);
/* 555 */     printStackTrace(this.networkThread, printNativeStackTraces);
/* 556 */     if (printNativeStackTraces) {
/* 557 */       nativePrintStackTracesOfRegisteredThreads();
/*     */     }
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private void onNetworkThreadReady() {
/* 563 */     this.networkThread = ThreadInfo.getCurrent();
/* 564 */     staticNetworkThread = this.networkThread;
/* 565 */     Logging.d("PeerConnectionFactory", "onNetworkThreadReady");
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private void onWorkerThreadReady() {
/* 570 */     this.workerThread = ThreadInfo.getCurrent();
/* 571 */     staticWorkerThread = this.workerThread;
/* 572 */     Logging.d("PeerConnectionFactory", "onWorkerThreadReady");
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   private void onSignalingThreadReady() {
/* 577 */     this.signalingThread = ThreadInfo.getCurrent();
/* 578 */     staticSignalingThread = this.signalingThread;
/* 579 */     Logging.d("PeerConnectionFactory", "onSignalingThreadReady");
/*     */   }
/*     */   
/*     */   private static native void nativeInitializeAndroidGlobals();
/*     */   
/*     */   private static native void nativeInitializeFieldTrials(String paramString);
/*     */   
/*     */   private static native String nativeFindFieldTrialsFullName(String paramString);
/*     */   
/*     */   private static native void nativeInitializeInternalTracer();
/*     */   
/*     */   private static native void nativeShutdownInternalTracer();
/*     */   
/*     */   private static native boolean nativeStartInternalTracingCapture(String paramString);
/*     */   
/*     */   private static native void nativeStopInternalTracingCapture();
/*     */   
/*     */   private static native PeerConnectionFactory nativeCreatePeerConnectionFactory(Context paramContext, Options paramOptions, long paramLong1, long paramLong2, long paramLong3, VideoEncoderFactory paramVideoEncoderFactory, VideoDecoderFactory paramVideoDecoderFactory, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8);
/*     */   
/*     */   private static native long nativeCreatePeerConnection(long paramLong1, PeerConnection.RTCConfiguration paramRTCConfiguration, MediaConstraints paramMediaConstraints, long paramLong2, SSLCertificateVerifier paramSSLCertificateVerifier);
/*     */   
/*     */   private static native long nativeCreateLocalMediaStream(long paramLong, String paramString);
/*     */   
/*     */   private static native long nativeCreateVideoSource(long paramLong, boolean paramBoolean1, boolean paramBoolean2);
/*     */   
/*     */   private static native long nativeCreateVideoTrack(long paramLong1, String paramString, long paramLong2);
/*     */   
/*     */   private static native long nativeCreateAudioSource(long paramLong, MediaConstraints paramMediaConstraints);
/*     */   
/*     */   private static native long nativeCreateAudioTrack(long paramLong1, String paramString, long paramLong2);
/*     */   
/*     */   private static native boolean nativeStartAecDump(long paramLong, int paramInt1, int paramInt2);
/*     */   
/*     */   private static native void nativeStopAecDump(long paramLong);
/*     */   
/*     */   private static native void nativeFreeFactory(long paramLong);
/*     */   
/*     */   private static native long nativeGetNativePeerConnectionFactory(long paramLong);
/*     */   
/*     */   private static native void nativeInjectLoggable(JNILogging paramJNILogging, int paramInt);
/*     */   
/*     */   private static native void nativeDeleteLoggable();
/*     */   
/*     */   private static native void nativePrintStackTrace(int paramInt);
/*     */   
/*     */   private static native void nativePrintStackTracesOfRegisteredThreads();
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/PeerConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */