package durdinapps.rxfirebase2;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

public class RxFirebaseUser {

    /**
     * Fetches a Firebase Auth ID Token for the user; useful when authenticating against your own backend.
     *
     * @param firebaseUser current firebaseUser instance.
     * @param forceRefresh force to refresh the token ID.
     * @return a {@link Maybe} which emits an {@link GetTokenResult} if success.
     */
    @NonNull
    public static Maybe<GetTokenResult> getIdToken(@NonNull final FirebaseUser firebaseUser,
                                                   final boolean forceRefresh) {
        return Maybe.create(new MaybeOnSubscribe<GetTokenResult>() {
            @Override
            public void subscribe(MaybeEmitter<GetTokenResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseUser.getIdToken(forceRefresh));
            }
        });
    }

    /**
     * Updates the email address of the user.
     *
     * @param firebaseUser current firebaseUser instance.
     * @param email        new email.
     * @return a {@link Completable} if the task is complete successfully.
     */
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

    /**
     * Updates the password of the user.
     *
     * @param firebaseUser current firebaseUser instance.
     * @param password     new password.
     * @return a {@link Completable} if the task is complete successfully.
     */
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

    /**
     * Updates the user profile information.
     *
     * @param firebaseUser current firebaseUser instance.
     * @param request      {@link UserProfileChangeRequest} request for this user.
     * @return a {@link Completable} if the task is complete successfully.
     */
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

    /**
     * Deletes the user record from your Firebase project's database.
     *
     * @param firebaseUser current firebaseUser instance.
     * @return a {@link Completable} if the task is complete successfully.
     */
    @NonNull
    public static Completable delete(@NonNull final FirebaseUser firebaseUser) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseUser.delete());
            }
        });
    }

    /**
     * Reauthenticates the user with the given credential.
     *
     * @param firebaseUser current firebaseUser instance.
     * @param credential   {@link AuthCredential} to re-authenticate.
     * @return a {@link Completable} if the task is complete successfully.
     */
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

    /**
     * Manually refreshes the data of the current user (for example, attached providers, display name, and so on).
     *
     * @param firebaseUser current firebaseUser instance.
     * @return a {@link Completable} if the task is complete successfully.
     */
    @NonNull
    public static Completable reload(@NonNull final FirebaseUser firebaseUser) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseUser.reload());
            }
        });
    }

    /**
     * Initiates email verification for the user.
     *
     * @param firebaseUser current firebaseUser instance.
     * @return a {@link Completable} if the task is complete successfully.
     */
    @NonNull
    public static Completable sendEmailVerification(@NonNull final FirebaseUser firebaseUser) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseUser.sendEmailVerification());
            }
        });
    }

    /**
     * Attaches the given {@link AuthCredential} to the user.
     *
     * @param firebaseUser current firebaseUser instance.
     * @param credential   new {@link AuthCredential} to link.
     * @return a {@link Maybe} which emits an {@link AuthResult} if success.
     */
    @NonNull
    public static Maybe<AuthResult> linkWithCredential(@NonNull final FirebaseUser firebaseUser,
                                                       @NonNull final AuthCredential credential) {
        return Maybe.create(new MaybeOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(MaybeEmitter<AuthResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseUser.linkWithCredential(credential));
            }
        });
    }

    /**
     * Detaches credentials from a given provider type from this user.
     *
     * @param firebaseUser current firebaseUser instance.
     * @param provider     a unique identifier of the type of provider to be unlinked, for example, {@link com.google.firebase.auth.FacebookAuthProvider#PROVIDER_ID}.
     * @return a {@link Maybe} which emits an {@link AuthResult} if success.
     */
    @NonNull
    public static Maybe<AuthResult> unlink(@NonNull final FirebaseUser firebaseUser,
                                           @NonNull final String provider) {
        return Maybe.create(new MaybeOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(MaybeEmitter<AuthResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseUser.unlink(provider));
            }
        });
    }

    /**
     * updates the current phone number for the given user.
     *
     * @param firebaseUser        current firebaseUser instance.
     * @param phoneAuthCredential new phone credential.
     * @return a {@link Completable} if the task is complete successfully.
     */
    @NonNull
    public static Completable updatePhoneNumber(@NonNull final FirebaseUser firebaseUser,
                                                @NonNull final PhoneAuthCredential phoneAuthCredential) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, firebaseUser.updatePhoneNumber(phoneAuthCredential));
            }
        });
    }

    /**
     * Reauthenticates the user with the given credential, and returns the profile data for that account.
     * This is useful for operations that require a recent sign-in, to prevent or resolve a {@link com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException}
     *
     * @param firebaseUser current firebaseUser instance.
     * @param credential   Authcredential used for reauthenticate.
     * @return a {@link Maybe} which emits an {@link AuthResult} if success.
     */
    @NonNull
    public static Maybe<AuthResult> reauthenticateAndRetrieveData(@NonNull final FirebaseUser firebaseUser,
                                                                  @NonNull final AuthCredential credential) {
        return Maybe.create(new MaybeOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(MaybeEmitter<AuthResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, firebaseUser.reauthenticateAndRetrieveData(credential));
            }
        });
    }
}