/*    */
package org.webrtc;

import android.util.Log;

/*    */ class NativeLibrary
        /*    */ {
    /* 14 */   private static String TAG = "NativeLibrary";

    /*    */
    /*    */   static class DefaultLoader
            /*    */ implements NativeLibraryLoader {
        /*    */
        public boolean load(String name) {
            /* 19 */
            Logging.d(NativeLibrary.TAG, "Loading library: " + name);
            /*    */
            try {
                Log.i(TAG, "name=>" + name);
                System.loadLibrary(name);
            } catch (UnsatisfiedLinkError e) {
                /* 23 */
                Logging.e(NativeLibrary.TAG, "Failed to load native library: " + name, e);
                /* 24 */
                return false;
                /*    */
            }
            /* 26 */
            return true;
            /*    */
        }
        /*    */
    }

    /*    */
    /* 30 */   private static final Object lock = new Object();
    /*    */
    /*    */
    /*    */   private static boolean libraryLoaded;

    /*    */
    /*    */
    /*    */
    /*    */
    static void initialize(NativeLibraryLoader loader, String libraryName) {
        /* 38 */
        synchronized (lock) {
            /* 39 */
            if (libraryLoaded) {
                /* 40 */
                Logging.d(TAG, "Native library has already been loaded.");
                /*    */
                return;
                /*    */
            }
            /* 43 */
            Logging.d(TAG, "Loading native library: " + libraryName);
            /* 44 */
            libraryLoaded = loader.load(libraryName);
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    /*    */
    static boolean isLoaded() {
        /* 50 */
        synchronized (lock) {
            /* 51 */
            return libraryLoaded;
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              /Users/kairatdoshekenov/classes.jar!/org/webrtc/NativeLibrary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */