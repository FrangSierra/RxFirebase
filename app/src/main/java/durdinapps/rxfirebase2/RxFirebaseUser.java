package durdinapps.rxfirebase2;

import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class RxFirebaseUser {

    @NonNull
    public static Observable<GetTokenResult> getToken(@NonNull final FirebaseUser firebaseUser,
                                                      final boolean forceRefresh) {
        return Observable.create(new ObservableOnSubscribe<GetTokenResult>() {
            @Override
            public void subscribe(ObservableEmitter<GetTokenResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseUser.getToken(forceRefresh));
            }
        });
    }

    @NonNull
    public static Completable updateEmail(@NonNull final FirebaseUser firebaseUser,
                                          @NonNull final String email) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseUser.updateEmail(email));
            }
        });
    }

    @NonNull
    public static Completable updatePassword(@NonNull final FirebaseUser firebaseUser,
                                             @NonNull final String password) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseUser.updatePassword(password));
            }
        });
    }

    @NonNull
    public static Completable updateProfile(@NonNull final FirebaseUser firebaseUser,
                                            @NonNull final UserProfileChangeRequest request) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseUser.updateProfile(request));
            }
        });
    }

    @NonNull
    public static Completable delete(@NonNull final FirebaseUser firebaseUser) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseUser.delete());
            }
        });
    }

    @NonNull
    public static Completable reAuthenticate(@NonNull final FirebaseUser firebaseUser,
                                             @NonNull final AuthCredential credential) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseUser.reauthenticate(credential));
            }
        });
    }

    @NonNull
    public static Observable<AuthResult> linkWithCredential(@NonNull final FirebaseUser firebaseUser,
                                                            @NonNull final AuthCredential credential) {
        return Observable.create(new ObservableOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(ObservableEmitter<AuthResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseUser.linkWithCredential(credential));
            }
        });
    }


}