package durdinapps.rxfirebase2;

import static durdinapps.rxfirebase2.Plugins.throwExceptionIfMainThread;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;

import io.reactivex.Completable;

public class RxFirestoreOfflineHandler {

    /**
     * Method that listen a given reference and waits for a single change inside of it. However, this method
     * is just called in offline methods of add/set/delete in a new reference. That's why this reference should be
     * updated just once after successfully modify the reference with an offline operation.
     *
     * @param ref Document reference to be listened.
     * @return A Completable which emits when the given reference receives an offline update.
     * @throws IllegalStateException if operation is happening on the main thread
     * @see Plugins
     */
    public static Completable listenOfflineListener(final DocumentReference ref) {
        return Completable.create(emitter -> {
            throwExceptionIfMainThread();

            try {
                final ListenerRegistration listener =
                        ref.addSnapshotListener((documentSnapshot, firebaseError) -> {
                        if (firebaseError != null) {
                            emitter.onError(firebaseError);
                        } else {
                            if (documentSnapshot != null) {
                                emitter.onComplete();
                            } else {
                                emitter.onError(new NullPointerException("Empty Snapshot"));
                            }
                        }
                });

                emitter.setCancellable(listener::remove);
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}
