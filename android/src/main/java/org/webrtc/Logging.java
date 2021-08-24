/*     */ package org.webrtc;
/*     */ 
/*     */ import androidx.annotation.Nullable;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.EnumSet;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Logging
/*     */ {
/*  43 */   private static final Logger fallbackLogger = createFallbackLogger();
/*     */   
/*     */   private static volatile boolean loggingEnabled;
/*     */ 
/*     */   
/*     */   private static Logger createFallbackLogger() {
/*  49 */     Logger fallbackLogger = Logger.getLogger("org.webrtc.Logging");
/*  50 */     fallbackLogger.setLevel(Level.ALL);
/*  51 */     return fallbackLogger;
/*     */   } @Nullable
/*     */   private static Loggable loggable; private static Severity loggableSeverity;
/*     */   static void injectLoggable(Loggable injectedLoggable, Severity severity) {
/*  55 */     if (injectedLoggable != null) {
/*  56 */       loggable = injectedLoggable;
/*  57 */       loggableSeverity = severity;
/*     */     } 
/*     */   }
/*     */   
/*     */   static void deleteInjectedLoggable() {
/*  62 */     loggable = null;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public enum TraceLevel
/*     */   {
/*  68 */     TRACE_NONE(0),
/*  69 */     TRACE_STATEINFO(1),
/*  70 */     TRACE_WARNING(2),
/*  71 */     TRACE_ERROR(4),
/*  72 */     TRACE_CRITICAL(8),
/*  73 */     TRACE_APICALL(16),
/*  74 */     TRACE_DEFAULT(255),
/*  75 */     TRACE_MODULECALL(32),
/*  76 */     TRACE_MEMORY(256),
/*  77 */     TRACE_TIMER(512),
/*  78 */     TRACE_STREAM(1024),
/*  79 */     TRACE_DEBUG(2048),
/*  80 */     TRACE_INFO(4096),
/*  81 */     TRACE_TERSEINFO(8192),
/*  82 */     TRACE_ALL(65535);
/*     */     public final int level;
/*     */     
/*     */     TraceLevel(int level) {
/*  86 */       this.level = level;
/*     */     }
/*     */   }
/*     */   
/*     */   public enum Severity {
/*  91 */     LS_VERBOSE, LS_INFO, LS_WARNING, LS_ERROR, LS_NONE; }
/*     */   
/*     */   public static void enableLogThreads() {
/*  94 */     nativeEnableLogThreads();
/*     */   }
/*     */   
/*     */   public static void enableLogTimeStamps() {
/*  98 */     nativeEnableLogTimeStamps();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void enableTracing(String path, EnumSet<TraceLevel> levels) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void enableLogToDebugOutput(Severity severity) {
/* 111 */     if (loggable != null) {
/* 112 */       throw new IllegalStateException("Logging to native debug output not supported while Loggable is injected. Delete the Loggable before calling this method.");
/*     */     }
/*     */ 
/*     */     
/* 116 */     nativeEnableLogToDebugOutput(severity.ordinal());
/* 117 */     loggingEnabled = true;
/*     */   }
/*     */   public static void log(Severity severity, String tag, String message) {
/*     */     Level level;
/* 121 */     if (tag == null || message == null) {
/* 122 */       throw new IllegalArgumentException("Logging tag or message may not be null.");
/*     */     }
/* 124 */     if (loggable != null) {
/*     */       
/* 126 */       if (severity.ordinal() < loggableSeverity.ordinal()) {
/*     */         return;
/*     */       }
/* 129 */       loggable.onLogMessage(message, severity, tag);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 134 */     if (loggingEnabled) {
/* 135 */       nativeLog(severity.ordinal(), tag, message);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 141 */     switch (severity) {
/*     */       case LS_ERROR:
/* 143 */         level = Level.SEVERE;
/*     */         break;
/*     */       case LS_WARNING:
/* 146 */         level = Level.WARNING;
/*     */         break;
/*     */       case LS_INFO:
/* 149 */         level = Level.INFO;
/*     */         break;
/*     */       default:
/* 152 */         level = Level.FINE;
/*     */         break;
/*     */     } 
/* 155 */     fallbackLogger.log(level, tag + ": " + message);
/*     */   }
/*     */   
/*     */   public static void d(String tag, String message) {
/* 159 */     log(Severity.LS_INFO, tag, message);
/*     */   }
/*     */   
/*     */   public static void e(String tag, String message) {
/* 163 */     log(Severity.LS_ERROR, tag, message);
/*     */   }
/*     */   
/*     */   public static void w(String tag, String message) {
/* 167 */     log(Severity.LS_WARNING, tag, message);
/*     */   }
/*     */   
/*     */   public static void e(String tag, String message, Throwable e) {
/* 171 */     log(Severity.LS_ERROR, tag, message);
/* 172 */     log(Severity.LS_ERROR, tag, e.toString());
/* 173 */     log(Severity.LS_ERROR, tag, getStackTraceString(e));
/*     */   }
/*     */   
/*     */   public static void w(String tag, String message, Throwable e) {
/* 177 */     log(Severity.LS_WARNING, tag, message);
/* 178 */     log(Severity.LS_WARNING, tag, e.toString());
/* 179 */     log(Severity.LS_WARNING, tag, getStackTraceString(e));
/*     */   }
/*     */   
/*     */   public static void v(String tag, String message) {
/* 183 */     log(Severity.LS_VERBOSE, tag, message);
/*     */   }
/*     */   
/*     */   private static String getStackTraceString(Throwable e) {
/* 187 */     if (e == null) {
/* 188 */       return "";
/*     */     }
/*     */     
/* 191 */     StringWriter sw = new StringWriter();
/* 192 */     PrintWriter pw = new PrintWriter(sw);
/* 193 */     e.printStackTrace(pw);
/* 194 */     return sw.toString();
/*     */   }
/*     */   
/*     */   private static native void nativeEnableLogToDebugOutput(int paramInt);
/*     */   
/*     */   private static native void nativeEnableLogThreads();
/*     */   
/*     */   private static native void nativeEnableLogTimeStamps();
/*     */   
/*     */   private static native void nativeLog(int paramInt, String paramString1, String paramString2);
/*     */ }


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/Logging.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */