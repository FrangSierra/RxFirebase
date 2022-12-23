package durdinapps.rxfirebase2;

import android.os.Looper;

/**
 * Utility class to make behavior of extensions more flexible and testable
 */
public class Plugins {

    /**
     * The flag, that controls can be extensions be called on the main thread or not
     * @apiNote some extensions not supported this feature, so see documentation
     */
    public static boolean allowMainThreadQueries = false;

    /**
     * Throws an error if you make network call to Firebase on the main thread
     * @throws IllegalStateException in case of calling on the main thread
     */
    public static void throwExceptionIfMainThread() {
        if (!allowMainThreadQueries && Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Network on main thread is not permitted");
        }
    }
}
