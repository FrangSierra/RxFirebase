package durdinapps.rxfirebase2;

import androidx.annotation.NonNull;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

public class RxFirebaseRemote {

    /**
     * Fetches parameter values for your app.
     * Configuration values may be served from the Default Config (local cache) or from the Remote Config Server,
     * depending on how much time has elapsed since parameter values were last fetched from the Remote Config server.
     * This method lets the caller specify the cache expiration in seconds.
     * <p>
     * To identify the current app instance, the fetch request creates a Firebase Instance ID token,
     * which periodically sends data to the Firebase backend. To stop the periodic sync, call deleteInstanceId();
     * calling fetchConfig again creates a new token and resumes the periodic sync.
     *
     * @param config        firebase remote config instance.
     * @param cacheLifeTime If the data in the cache was fetched no longer than this many seconds ago, this method will return the cached data.
     *                      If not, a fetch from the Remote Config Server will be attempted.
     * @return a {@link Completable} which emits when the action is completed.
     */
    @NonNull
    public static Completable fetch(@NonNull final FirebaseRemoteConfig config,
                                    @NonNull final long cacheLifeTime) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, config.fetch(cacheLifeTime));
            }
        });
    }

    /**
     * Fetches parameter values for your app.
     * Configuration values may be served from the Default Config (local cache) or from the Remote Config Server,
     * depending on how much time has elapsed since parameter values were last fetched from the Remote Config server.
     * This method lets the caller specify the cache expiration in seconds.
     * <p>
     * To identify the current app instance, the fetch request creates a Firebase Instance ID token,
     * which periodically sends data to the Firebase backend. To stop the periodic sync, call deleteInstanceId();
     * calling fetchConfig again creates a new token and resumes the periodic sync.
     *
     * @param config firebase remote config instance.
     * @return a {@link Completable} which emits when the action is completed.
     */
    @NonNull
    public static Completable fetch(@NonNull final FirebaseRemoteConfig config) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, config.fetch(43200L));
            }
        });
    }
}
