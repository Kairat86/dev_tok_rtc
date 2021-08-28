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

        public void detachThread() {
            this.thread = null;
        }
    }


    public static void checkIsOnMainThread() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("Not on main thread!");
        }
    }

    public static void executeUninterruptedly(BlockingOperation operation) {
        boolean wasInterrupted = false;
        try {
            operation.run();
        } catch (InterruptedException e) {
            wasInterrupted = true;
        }
        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public static boolean joinUninterruptedly(Thread thread, long timeoutMs) {
        long startTimeMs = SystemClock.elapsedRealtime();
        long timeRemainingMs = timeoutMs;
        boolean wasInterrupted = false;
        while (timeRemainingMs > 0L) {
            try {
                thread.join(timeRemainingMs);
                break;
            } catch (InterruptedException e) {
                wasInterrupted = true;
                long elapsedTimeMs = SystemClock.elapsedRealtime() - startTimeMs;
                timeRemainingMs = timeoutMs - elapsedTimeMs;
            }
        }
        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }
        return !thread.isAlive();
    }

    public static void awaitUninterruptedly(final CountDownLatch latch) {
        executeUninterruptedly(latch::await);
    }

    public static <V> V invokeAtFrontUninterruptedly(Handler handler, final Callable<V> callable) {
        if (handler.getLooper().getThread() == Thread.currentThread())
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        class CaughtException {
            Exception e;
        }
        class Result {
            public V value;
        }
        final Result result = new Result();
        final CaughtException caughtException = new CaughtException();
        final CountDownLatch barrier = new CountDownLatch(1);
        handler.post(() -> {
            try {
                result.value = callable.call();
            } catch (Exception e) {
                caughtException.e = e;
            }
            barrier.countDown();
        });
        awaitUninterruptedly(barrier);
        if (caughtException.e != null) {
            RuntimeException runtimeException = new RuntimeException(caughtException.e);
            runtimeException.setStackTrace(
                    concatStackTraces(caughtException.e.getStackTrace(), runtimeException.getStackTrace()));
            throw runtimeException;
        }
        return result.value;
    }

    public static void invokeAtFrontUninterruptedly(Handler handler, final Runnable runner) {
        invokeAtFrontUninterruptedly(handler, (Callable<Void>) () -> {
            runner.run();
            return null;
        });
    }

    static StackTraceElement[] concatStackTraces(StackTraceElement[] inner, StackTraceElement[] outer) {
        StackTraceElement[] combined = new StackTraceElement[inner.length + outer.length];
        System.arraycopy(inner, 0, combined, 0, inner.length);
        System.arraycopy(outer, 0, combined, inner.length, outer.length);
        return combined;
    }

    public interface BlockingOperation {
        void run() throws InterruptedException;
    }
}