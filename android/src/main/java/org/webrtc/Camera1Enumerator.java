/*     */ package org.webrtc;
/*     */ 
/*     */ import android.hardware.Camera;
/*     */ import android.os.SystemClock;
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
/*     */ public class Camera1Enumerator
/*     */   implements CameraEnumerator
/*     */ {
/*     */   private static final String TAG = "Camera1Enumerator";
/*     */   private static List<List<CameraEnumerationAndroid.CaptureFormat>> cachedSupportedFormats;
/*     */   private final boolean captureToTexture;
/*     */   
/*     */   public Camera1Enumerator() {
/*  30 */     this(true);
/*     */   }
/*     */   
/*     */   public Camera1Enumerator(boolean captureToTexture) {
/*  34 */     this.captureToTexture = captureToTexture;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getDeviceNames() {
/*  40 */     ArrayList<String> namesList = new ArrayList<>();
/*  41 */     for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
/*  42 */       String name = getDeviceName(i);
/*  43 */       if (name != null) {
/*  44 */         namesList.add(name);
/*  45 */         Logging.d("Camera1Enumerator", "Index: " + i + ". " + name);
/*     */       } else {
/*  47 */         Logging.e("Camera1Enumerator", "Index: " + i + ". Failed to query camera name.");
/*     */       } 
/*     */     } 
/*  50 */     String[] namesArray = new String[namesList.size()];
/*  51 */     return namesList.<String>toArray(namesArray);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFrontFacing(String deviceName) {
/*  56 */     Camera.CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
/*  57 */     return (info != null && info.facing == 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBackFacing(String deviceName) {
/*  62 */     Camera.CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
/*  63 */     return (info != null && info.facing == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(String deviceName) {
/*  68 */     return getSupportedFormats(getCameraIndex(deviceName));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public CameraVideoCapturer createCapturer(String deviceName, CameraVideoCapturer.CameraEventsHandler eventsHandler) {
/*  74 */     return new Camera1Capturer(deviceName, eventsHandler, this.captureToTexture);
/*     */   }
/*     */   @Nullable
/*     */   private static Camera.CameraInfo getCameraInfo(int index) {
/*  78 */     Camera.CameraInfo info = new Camera.CameraInfo();
/*     */     try {
/*  80 */       Camera.getCameraInfo(index, info);
/*  81 */     } catch (Exception e) {
/*  82 */       Logging.e("Camera1Enumerator", "getCameraInfo failed on index " + index, e);
/*  83 */       return null;
/*     */     } 
/*  85 */     return info;
/*     */   }
/*     */   
/*     */   static synchronized List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(int cameraId) {
/*  89 */     if (cachedSupportedFormats == null) {
/*  90 */       cachedSupportedFormats = new ArrayList<>();
/*  91 */       for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
/*  92 */         cachedSupportedFormats.add(enumerateFormats(i));
/*     */       }
/*     */     } 
/*  95 */     return cachedSupportedFormats.get(cameraId);
/*     */   }
/*     */   private static List<CameraEnumerationAndroid.CaptureFormat> enumerateFormats(int cameraId) {
/*     */     Camera.Parameters parameters;
/*  99 */     Logging.d("Camera1Enumerator", "Get supported formats for camera index " + cameraId + ".");
/* 100 */     long startTimeMs = SystemClock.elapsedRealtime();
/*     */     
/* 102 */     Camera camera = null;
/*     */     try {
/* 104 */       Logging.d("Camera1Enumerator", "Opening camera with index " + cameraId);
/* 105 */       camera = Camera.open(cameraId);
/* 106 */       parameters = camera.getParameters();
/* 107 */     } catch (RuntimeException e) {
/* 108 */       Logging.e("Camera1Enumerator", "Open camera failed on camera index " + cameraId, e);
/* 109 */       return new ArrayList();
/*     */     } finally {
/* 111 */       if (camera != null) {
/* 112 */         camera.release();
/*     */       }
/*     */     } 
/*     */     
/* 116 */     List<CameraEnumerationAndroid.CaptureFormat> formatList = new ArrayList<>();
/*     */     try {
/* 118 */       int minFps = 0;
/* 119 */       int maxFps = 0;
/* 120 */       List<int[]> listFpsRange = parameters.getSupportedPreviewFpsRange();
/* 121 */       if (listFpsRange != null) {
/*     */ 
/*     */         
/* 124 */         int[] range = listFpsRange.get(listFpsRange.size() - 1);
/* 125 */         minFps = range[0];
/* 126 */         maxFps = range[1];
/*     */       } 
/* 128 */       for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
/* 129 */         formatList.add(new CameraEnumerationAndroid.CaptureFormat(size.width, size.height, minFps, maxFps));
/*     */       }
/* 131 */     } catch (Exception e) {
/* 132 */       Logging.e("Camera1Enumerator", "getSupportedFormats() failed on camera index " + cameraId, e);
/*     */     } 
/*     */     
/* 135 */     long endTimeMs = SystemClock.elapsedRealtime();
/* 136 */     Logging.d("Camera1Enumerator", "Get supported formats for camera index " + cameraId + " done. Time spent: " + (endTimeMs - startTimeMs) + " ms.");
/*     */     
/* 138 */     return formatList;
/*     */   }
/*     */ 
/*     */   
/*     */   static List<Size> convertSizes(List<Camera.Size> cameraSizes) {
/* 143 */     List<Size> sizes = new ArrayList<>();
/* 144 */     for (Camera.Size size : cameraSizes) {
/* 145 */       sizes.add(new Size(size.width, size.height));
/*     */     }
/* 147 */     return sizes;
/*     */   }
/*     */ 
/*     */   
/*     */   static List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> convertFramerates(List<int[]> arrayRanges) {
/* 152 */     List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> ranges = new ArrayList<>();
/* 153 */     for (int[] range : arrayRanges) {
/* 154 */       ranges.add(new CameraEnumerationAndroid.CaptureFormat.FramerateRange(range[0], range[1]));
/*     */     }
/*     */ 
/*     */     
/* 158 */     return ranges;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static int getCameraIndex(String deviceName) {
/* 164 */     Logging.d("Camera1Enumerator", "getCameraIndex: " + deviceName);
/* 165 */     for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
/* 166 */       if (deviceName.equals(getDeviceName(i))) {
/* 167 */         return i;
/*     */       }
/*     */     } 
/* 170 */     throw new IllegalArgumentException("No such camera: " + deviceName);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   static String getDeviceName(int index) {
/* 176 */     Camera.CameraInfo info = getCameraInfo(index);
/* 177 */     if (info == null) {
/* 178 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 182 */     String facing = (info.facing == 1) ? "front" : "back";
/* 183 */     return "Camera " + index + ", Facing " + facing + ", Orientation " + info.orientation;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/Camera1Enumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */