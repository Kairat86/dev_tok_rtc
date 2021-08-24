/*     */ package org.webrtc;
/*     */ 
/*     */ import androidx.annotation.Nullable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class RtpParameters
/*     */ {
/*     */   public final String transactionId;
/*     */   @Nullable
/*     */   public DegradationPreference degradationPreference;
/*     */   private final Rtcp rtcp;
/*     */   private final List<HeaderExtension> headerExtensions;
/*     */   public final List<Encoding> encodings;
/*     */   public final List<Codec> codecs;
/*     */   
/*     */   public enum DegradationPreference
/*     */   {
/*  32 */     DISABLED,
/*     */     
/*  34 */     MAINTAIN_FRAMERATE,
/*     */     
/*  36 */     MAINTAIN_RESOLUTION,
/*     */     
/*  38 */     BALANCED;
/*     */     
/*     */     @CalledByNative("DegradationPreference")
/*     */     static DegradationPreference fromNativeIndex(int nativeIndex) {
/*  42 */       return values()[nativeIndex];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Encoding
/*     */   {
/*     */     @Nullable
/*     */     public String rid;
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean active = true;
/*     */ 
/*     */ 
/*     */     
/*  61 */     public double bitratePriority = 1.0D;
/*     */ 
/*     */ 
/*     */     
/*  65 */     public int networkPriority = 1;
/*     */     
/*     */     @Nullable
/*     */     public Integer maxBitrateBps;
/*     */     
/*     */     @Nullable
/*     */     public Integer minBitrateBps;
/*     */     
/*     */     @Nullable
/*     */     public Integer maxFramerate;
/*     */     
/*     */     @Nullable
/*     */     public Integer numTemporalLayers;
/*     */     
/*     */     @Nullable
/*     */     public Double scaleResolutionDownBy;
/*     */     
/*     */     public Long ssrc;
/*     */     
/*     */     public Encoding(String rid, boolean active, Double scaleResolutionDownBy) {
/*  85 */       this.rid = rid;
/*  86 */       this.active = active;
/*  87 */       this.scaleResolutionDownBy = scaleResolutionDownBy;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @CalledByNative("Encoding")
/*     */     Encoding(String rid, boolean active, double bitratePriority, int networkPriority, Integer maxBitrateBps, Integer minBitrateBps, Integer maxFramerate, Integer numTemporalLayers, Double scaleResolutionDownBy, Long ssrc) {
/*  94 */       this.rid = rid;
/*  95 */       this.active = active;
/*  96 */       this.bitratePriority = bitratePriority;
/*  97 */       this.networkPriority = networkPriority;
/*  98 */       this.maxBitrateBps = maxBitrateBps;
/*  99 */       this.minBitrateBps = minBitrateBps;
/* 100 */       this.maxFramerate = maxFramerate;
/* 101 */       this.numTemporalLayers = numTemporalLayers;
/* 102 */       this.scaleResolutionDownBy = scaleResolutionDownBy;
/* 103 */       this.ssrc = ssrc;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     @CalledByNative("Encoding")
/*     */     String getRid() {
/* 109 */       return this.rid;
/*     */     }
/*     */     
/*     */     @CalledByNative("Encoding")
/*     */     boolean getActive() {
/* 114 */       return this.active;
/*     */     }
/*     */     
/*     */     @CalledByNative("Encoding")
/*     */     double getBitratePriority() {
/* 119 */       return this.bitratePriority;
/*     */     }
/*     */ 
/*     */     
/*     */     @CalledByNative("Encoding")
/*     */     int getNetworkPriority() {
/* 125 */       return this.networkPriority;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     @CalledByNative("Encoding")
/*     */     Integer getMaxBitrateBps() {
/* 131 */       return this.maxBitrateBps;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     @CalledByNative("Encoding")
/*     */     Integer getMinBitrateBps() {
/* 137 */       return this.minBitrateBps;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     @CalledByNative("Encoding")
/*     */     Integer getMaxFramerate() {
/* 143 */       return this.maxFramerate;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     @CalledByNative("Encoding")
/*     */     Integer getNumTemporalLayers() {
/* 149 */       return this.numTemporalLayers;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     @CalledByNative("Encoding")
/*     */     Double getScaleResolutionDownBy() {
/* 155 */       return this.scaleResolutionDownBy;
/*     */     }
/*     */     
/*     */     @CalledByNative("Encoding")
/*     */     Long getSsrc() {
/* 160 */       return this.ssrc;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Codec
/*     */   {
/*     */     public int payloadType;
/*     */     
/*     */     public String name;
/*     */     
/*     */     MediaStreamTrack.MediaType kind;
/*     */     
/*     */     public Integer clockRate;
/*     */     
/*     */     public Integer numChannels;
/*     */     
/*     */     public Map<String, String> parameters;
/*     */     
/*     */     @CalledByNative("Codec")
/*     */     Codec(int payloadType, String name, MediaStreamTrack.MediaType kind, Integer clockRate, Integer numChannels, Map<String, String> parameters) {
/* 181 */       this.payloadType = payloadType;
/* 182 */       this.name = name;
/* 183 */       this.kind = kind;
/* 184 */       this.clockRate = clockRate;
/* 185 */       this.numChannels = numChannels;
/* 186 */       this.parameters = parameters;
/*     */     }
/*     */     
/*     */     @CalledByNative("Codec")
/*     */     int getPayloadType() {
/* 191 */       return this.payloadType;
/*     */     }
/*     */     
/*     */     @CalledByNative("Codec")
/*     */     String getName() {
/* 196 */       return this.name;
/*     */     }
/*     */     
/*     */     @CalledByNative("Codec")
/*     */     MediaStreamTrack.MediaType getKind() {
/* 201 */       return this.kind;
/*     */     }
/*     */     
/*     */     @CalledByNative("Codec")
/*     */     Integer getClockRate() {
/* 206 */       return this.clockRate;
/*     */     }
/*     */     
/*     */     @CalledByNative("Codec")
/*     */     Integer getNumChannels() {
/* 211 */       return this.numChannels;
/*     */     }
/*     */     
/*     */     @CalledByNative("Codec")
/*     */     Map getParameters() {
/* 216 */       return this.parameters;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Rtcp
/*     */   {
/*     */     private final String cname;
/*     */     private final boolean reducedSize;
/*     */     
/*     */     @CalledByNative("Rtcp")
/*     */     Rtcp(String cname, boolean reducedSize) {
/* 228 */       this.cname = cname;
/* 229 */       this.reducedSize = reducedSize;
/*     */     }
/*     */     
/*     */     @CalledByNative("Rtcp")
/*     */     public String getCname() {
/* 234 */       return this.cname;
/*     */     }
/*     */     
/*     */     @CalledByNative("Rtcp")
/*     */     public boolean getReducedSize() {
/* 239 */       return this.reducedSize;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class HeaderExtension
/*     */   {
/*     */     private final String uri;
/*     */     
/*     */     private final int id;
/*     */     private final boolean encrypted;
/*     */     
/*     */     @CalledByNative("HeaderExtension")
/*     */     HeaderExtension(String uri, int id, boolean encrypted) {
/* 253 */       this.uri = uri;
/* 254 */       this.id = id;
/* 255 */       this.encrypted = encrypted;
/*     */     }
/*     */     
/*     */     @CalledByNative("HeaderExtension")
/*     */     public String getUri() {
/* 260 */       return this.uri;
/*     */     }
/*     */     
/*     */     @CalledByNative("HeaderExtension")
/*     */     public int getId() {
/* 265 */       return this.id;
/*     */     }
/*     */     
/*     */     @CalledByNative("HeaderExtension")
/*     */     public boolean getEncrypted() {
/* 270 */       return this.encrypted;
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
/*     */   @CalledByNative
/*     */   RtpParameters(String transactionId, DegradationPreference degradationPreference, Rtcp rtcp, List<HeaderExtension> headerExtensions, List<Encoding> encodings, List<Codec> codecs) {
/* 293 */     this.transactionId = transactionId;
/* 294 */     this.degradationPreference = degradationPreference;
/* 295 */     this.rtcp = rtcp;
/* 296 */     this.headerExtensions = headerExtensions;
/* 297 */     this.encodings = encodings;
/* 298 */     this.codecs = codecs;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   String getTransactionId() {
/* 303 */     return this.transactionId;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   DegradationPreference getDegradationPreference() {
/* 308 */     return this.degradationPreference;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   public Rtcp getRtcp() {
/* 313 */     return this.rtcp;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   public List<HeaderExtension> getHeaderExtensions() {
/* 318 */     return this.headerExtensions;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   List<Encoding> getEncodings() {
/* 323 */     return this.encodings;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   List<Codec> getCodecs() {
/* 328 */     return this.codecs;
/*     */   }
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/RtpParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */