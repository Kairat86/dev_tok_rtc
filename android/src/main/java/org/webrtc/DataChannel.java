/*     */ package org.webrtc;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataChannel
/*     */ {
/*     */   private long nativeDataChannel;
/*     */   private long nativeObserver;
/*     */   
/*     */   public static class Init
/*     */   {
/*     */     public boolean ordered = true;
/*  21 */     public int maxRetransmitTimeMs = -1;
/*     */     
/*  23 */     public int maxRetransmits = -1;
/*  24 */     public String protocol = "";
/*     */     
/*     */     public boolean negotiated;
/*  27 */     public int id = -1;
/*     */     
/*     */     @CalledByNative("Init")
/*     */     boolean getOrdered() {
/*  31 */       return this.ordered;
/*     */     }
/*     */     
/*     */     @CalledByNative("Init")
/*     */     int getMaxRetransmitTimeMs() {
/*  36 */       return this.maxRetransmitTimeMs;
/*     */     }
/*     */     
/*     */     @CalledByNative("Init")
/*     */     int getMaxRetransmits() {
/*  41 */       return this.maxRetransmits;
/*     */     }
/*     */     
/*     */     @CalledByNative("Init")
/*     */     String getProtocol() {
/*  46 */       return this.protocol;
/*     */     }
/*     */     
/*     */     @CalledByNative("Init")
/*     */     boolean getNegotiated() {
/*  51 */       return this.negotiated;
/*     */     }
/*     */     
/*     */     @CalledByNative("Init")
/*     */     int getId() {
/*  56 */       return this.id;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Buffer
/*     */   {
/*     */     public final ByteBuffer data;
/*     */ 
/*     */     
/*     */     public final boolean binary;
/*     */ 
/*     */ 
/*     */     
/*     */     @CalledByNative("Buffer")
/*     */     public Buffer(ByteBuffer data, boolean binary) {
/*  73 */       this.data = data;
/*  74 */       this.binary = binary;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface Observer
/*     */   {
/*     */     @CalledByNative("Observer")
/*     */     void onBufferedAmountChange(long param1Long);
/*     */ 
/*     */     
/*     */     @CalledByNative("Observer")
/*     */     void onStateChange();
/*     */     
/*     */     @CalledByNative("Observer")
/*     */     void onMessage(DataChannel.Buffer param1Buffer);
/*     */   }
/*     */   
/*     */   public enum State
/*     */   {
/*  94 */     CONNECTING,
/*  95 */     OPEN,
/*  96 */     CLOSING,
/*  97 */     CLOSED;
/*     */     
/*     */     @CalledByNative("State")
/*     */     static State fromNativeIndex(int nativeIndex) {
/* 101 */       return values()[nativeIndex];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CalledByNative
/*     */   public DataChannel(long nativeDataChannel) {
/* 110 */     this.nativeDataChannel = nativeDataChannel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerObserver(Observer observer) {
/* 115 */     checkDataChannelExists();
/* 116 */     if (this.nativeObserver != 0L) {
/* 117 */       nativeUnregisterObserver(this.nativeObserver);
/*     */     }
/* 119 */     this.nativeObserver = nativeRegisterObserver(observer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void unregisterObserver() {
/* 124 */     checkDataChannelExists();
/* 125 */     nativeUnregisterObserver(this.nativeObserver);
/*     */   }
/*     */   
/*     */   public String label() {
/* 129 */     checkDataChannelExists();
/* 130 */     return nativeLabel();
/*     */   }
/*     */   
/*     */   public int id() {
/* 134 */     checkDataChannelExists();
/* 135 */     return nativeId();
/*     */   }
/*     */   
/*     */   public State state() {
/* 139 */     checkDataChannelExists();
/* 140 */     return nativeState();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long bufferedAmount() {
/* 149 */     checkDataChannelExists();
/* 150 */     return nativeBufferedAmount();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 155 */     checkDataChannelExists();
/* 156 */     nativeClose();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean send(Buffer buffer) {
/* 161 */     checkDataChannelExists();
/*     */ 
/*     */     
/* 164 */     byte[] data = new byte[buffer.data.remaining()];
/* 165 */     buffer.data.get(data);
/* 166 */     return nativeSend(data, buffer.binary);
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispose() {
/* 171 */     checkDataChannelExists();
/* 172 */     JniCommon.nativeReleaseRef(this.nativeDataChannel);
/* 173 */     this.nativeDataChannel = 0L;
/*     */   }
/*     */   
/*     */   @CalledByNative
/*     */   long getNativeDataChannel() {
/* 178 */     return this.nativeDataChannel;
/*     */   }
/*     */   
/*     */   private void checkDataChannelExists() {
/* 182 */     if (this.nativeDataChannel == 0L)
/* 183 */       throw new IllegalStateException("DataChannel has been disposed."); 
/*     */   }
/*     */   
/*     */   private native long nativeRegisterObserver(Observer paramObserver);
/*     */   
/*     */   private native void nativeUnregisterObserver(long paramLong);
/*     */   
/*     */   private native String nativeLabel();
/*     */   
/*     */   private native int nativeId();
/*     */   
/*     */   private native State nativeState();
/*     */   
/*     */   private native long nativeBufferedAmount();
/*     */   
/*     */   private native void nativeClose();
/*     */   
/*     */   private native boolean nativeSend(byte[] paramArrayOfbyte, boolean paramBoolean);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/DataChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */