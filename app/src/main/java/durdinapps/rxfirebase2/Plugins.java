package durdinapps.rxfirebase2;

import android.os.Looper;

public class Plugins {

    public static boolean allowMainThreadQueries = false;

    public static void throwExceptionIfMainThread() {
        if (!allowMainThreadQueries && Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Network on main thread is not permitted");
        }
    }
}
