package durdinapps.rxfirebase2;

import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

public class RxFirebaseAuth {

    @NonNull
    public static Observable<AuthResult> signInAnonymously(@NonNull final FirebaseAuth firebaseAuth) {
        return Observable.create(new ObservableOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(ObservableEmitter<AuthResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseAuth.signInAnonymously());
            }
        });
    }

    @NonNull
    public static Observable<AuthResult> signInWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                    @NonNull final String email,
                                                                    @NonNull final String password) {
        return Observable.create(new ObservableOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(ObservableEmitter<AuthResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseAuth.signInWithEmailAndPassword(email, password));
            }
        });
    }

    @NonNull
    public static Observable<AuthResult> signInWithCredential(@NonNull final FirebaseAuth firebaseAuth,
                                                              @NonNull final AuthCredential credential) {
        return Observable.create(new ObservableOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(ObservableEmitter<AuthResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseAuth.signInWithCredential(credential));
            }
        });
    }

    @NonNull
    public static Observable<AuthResult> signInWithCustomToken(@NonNull final FirebaseAuth firebaseAuth,
                                                               @NonNull final String token) {
        return Observable.create(new ObservableOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(ObservableEmitter<AuthResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseAuth.signInWithCustomToken(token));
            }
        });
    }

    @NonNull
    public static Observable<AuthResult> createUserWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                        @NonNull final String email,
                                                                        @NonNull final String password) {
        return Observable.create(new ObservableOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(ObservableEmitter<AuthResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseAuth.createUserWithEmailAndPassword(email, password));
            }
        });
    }

    @NonNull
    public static Observable<ProviderQueryResult> fetchProvidersForEmail(@NonNull final FirebaseAuth firebaseAuth,
                                                                         @NonNull final String email) {
        return Observable.create(new ObservableOnSubscribe<ProviderQueryResult>() {
            @Override
            public void subscribe(ObservableEmitter<ProviderQueryResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseAuth.fetchProvidersForEmail(email));
            }
        });
    }

    @NonNull
    public static Completable sendPasswordResetEmail(@NonNull final FirebaseAuth firebaseAuth,
                                                     @NonNull final String email) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseAuth.sendPasswordResetEmail(email));
            }
        });
    }

    @NonNull
    public static Observable<FirebaseUser> observeAuthState(@NonNull final FirebaseAuth firebaseAuth) {

        return Observable.create(new ObservableOnSubscribe<FirebaseUser>() {
            @Override
            public void subscribe(final ObservableEmitter<FirebaseUser> emitter) throws Exception {
                final FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            emitter.onNext(firebaseAuth.getCurrentUser());
                        }
                    }
                };
                firebaseAuth.addAuthStateListener(authStateListener);
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        firebaseAuth.removeAuthStateListener(authStateListener);
                    }
                });

            }
        });
    }

}