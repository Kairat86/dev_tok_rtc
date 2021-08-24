/*     */
package org.webrtc;
/*     */
/*     */

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
/*     */ public class ThreadUtils
        /*     */ {
    /*     */   public static class ThreadChecker
            /*     */ {
        /*     */
        @Nullable
        /*  26 */ private Thread thread = Thread.currentThread();

        /*     */
        /*     */
        public void checkIsOnValidThread() {
            /*  29 */
            if (this.thread == null) {
                /*  30 */
                this.thread = Thread.currentThread();
                /*     */
            }
            /*  32 */
            if (Thread.currentThread() != this.thread) {
                /*  33 */
                throw new IllegalStateException("Wrong thread");
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */
        public void detachThread() {
            /*  38 */
            this.thread = null;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static void checkIsOnMainThread() {
        /*  46 */
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            /*  47 */
            throw new IllegalStateException("Not on main thread!");
            /*     */
        }
        /*     */
    }

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
    public static void executeUninterruptibly(BlockingOperation operation) {
        /*  65 */
        boolean wasInterrupted = false;
        /*     */
        while (true) {
            /*     */
            try {
                /*  68 */
                operation.run();
                /*     */
                break;
                /*  70 */
            } catch (InterruptedException e) {
                /*     */
                /*     */
                /*  73 */
                wasInterrupted = true;
                /*     */
            }
            /*     */
        }
        /*     */
        /*  77 */
        if (wasInterrupted) {
            /*  78 */
            Thread.currentThread().interrupt();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public static boolean joinUninterruptibly(Thread thread, long timeoutMs) {
        /*  83 */
        long startTimeMs = SystemClock.elapsedRealtime();
        /*  84 */
        long timeRemainingMs = timeoutMs;
        /*  85 */
        boolean wasInterrupted = false;
        /*  86 */
        while (timeRemainingMs > 0L) {
            /*     */
            try {
                /*  88 */
                thread.join(timeRemainingMs);
                /*     */
                break;
                /*  90 */
            } catch (InterruptedException e) {
                /*     */
                /*     */
                /*  93 */
                wasInterrupted = true;
                /*  94 */
                long elapsedTimeMs = SystemClock.elapsedRealtime() - startTimeMs;
                /*  95 */
                timeRemainingMs = timeoutMs - elapsedTimeMs;
                /*     */
            }
            /*     */
        }
        /*     */
        /*  99 */
        if (wasInterrupted) {
            /* 100 */
            Thread.currentThread().interrupt();
            /*     */
        }
        /* 102 */
        return !thread.isAlive();
        /*     */
    }

    /*     */
    /*     */
    public static void joinUninterruptibly(final Thread thread) {
        /* 106 */
        executeUninterruptibly(new BlockingOperation()
                /*     */ {
            /*     */
            public void run() throws InterruptedException {
                /* 109 */
                thread.join();
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public static void awaitUninterruptibly(final CountDownLatch latch) {
        /* 115 */
        executeUninterruptibly(new BlockingOperation()
                /*     */ {
            /*     */
            public void run() throws InterruptedException {
                /* 118 */
                latch.await();
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public static boolean awaitUninterruptibly(CountDownLatch barrier, long timeoutMs) {
        /* 124 */
        long startTimeMs = SystemClock.elapsedRealtime();
        /* 125 */
        long timeRemainingMs = timeoutMs;
        /* 126 */
        boolean wasInterrupted = false;
        /* 127 */
        boolean result = false;
        /*     */
        while (true) {
            /*     */
            /* 130 */
            try {
                result = barrier.await(timeRemainingMs, TimeUnit.MILLISECONDS);
                /*     */
                break;
            }
            /* 132 */ catch (InterruptedException e)
                /*     */
                /*     */ {
                /* 135 */
                wasInterrupted = true;
                /* 136 */
                long elapsedTimeMs = SystemClock.elapsedRealtime() - startTimeMs;
                /* 137 */
                timeRemainingMs = timeoutMs - elapsedTimeMs;
                /*     */
                /* 139 */
                if (timeRemainingMs <= 0L)
                    /*     */ break;
            }
            /* 141 */
        }
        if (wasInterrupted) {
            /* 142 */
            Thread.currentThread().interrupt();
            /*     */
        }
        /* 144 */
        return result;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static <V> V invokeAtFrontUninterruptibly(Handler handler, final Callable<V> callable) {
        /* 152 */
        if (handler.getLooper().getThread() == Thread.currentThread())
            /*     */ try {
            /* 154 */
            return callable.call();
            /* 155 */
        } catch (Exception e) {
            /* 156 */
            throw new RuntimeException(e);
            /*     */
        }
        /*     */
        class CaughtException
                /*     */ {
            /*     */ Exception e;
            /*     */
        }
        ;
        /*     */
        class Result {
            public V value;
        }
        /* 166 */
        final Result result = new Result();
        /* 167 */
        final CaughtException caughtException = new CaughtException();
        /* 168 */
        final CountDownLatch barrier = new CountDownLatch(1);
        /* 169 */
        handler.post(new Runnable()
                /*     */ {
            /*     */
            public void run() {
                /*     */
                try {
                    /* 173 */
                    result.value = callable.call();
                    /* 174 */
                } catch (Exception e) {
                    /* 175 */
                    caughtException.e = e;
                    /*     */
                }
                /* 177 */
                barrier.countDown();
                /*     */
            }
            /*     */
        });
        /* 180 */
        awaitUninterruptibly(barrier);
        /*     */
        /*     */
        /* 183 */
        if (caughtException.e != null) {
            /* 184 */
            RuntimeException runtimeException = new RuntimeException(caughtException.e);
            /* 185 */
            runtimeException.setStackTrace(
                    /* 186 */           concatStackTraces(caughtException.e.getStackTrace(), runtimeException.getStackTrace()));
            /* 187 */
            throw runtimeException;
            /*     */
        }
        /* 189 */
        return result.value;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static void invokeAtFrontUninterruptibly(Handler handler, final Runnable runner) {
        /* 196 */
        invokeAtFrontUninterruptibly(handler, new Callable<Void>()
                /*     */ {
            /*     */
            public Void call() {
                /* 199 */
                runner.run();
                /* 200 */
                return null;
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    /*     */
    static StackTraceElement[] concatStackTraces(StackTraceElement[] inner, StackTraceElement[] outer) {
        /* 207 */
        StackTraceElement[] combined = new StackTraceElement[inner.length + outer.length];
        /* 208 */
        System.arraycopy(inner, 0, combined, 0, inner.length);
        /* 209 */
        System.arraycopy(outer, 0, combined, inner.length, outer.length);
        /* 210 */
        return combined;
        /*     */
    }

    /*     */
    /*     */   public static interface BlockingOperation {
        /*     */     void run() throws InterruptedException;
        /*     */
    }
    /*     */
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/ThreadUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */