package durdinapps.rxfirebase2;

import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Cancellable;

public class RxFirebaseAuth {

   @NonNull
   public static Maybe<AuthResult> signInAnonymously(@NonNull final FirebaseAuth firebaseAuth) {
      return Maybe.create(new MaybeOnSubscribe<AuthResult>() {
         @Override
         public void subscribe(MaybeEmitter<AuthResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.signInAnonymously());
         }
      });
   }

   @NonNull
   public static Maybe<AuthResult> signInWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                              @NonNull final String email,
                                                              @NonNull final String password) {
      return Maybe.create(new MaybeOnSubscribe<AuthResult>() {
         @Override public void subscribe(MaybeEmitter<AuthResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.signInWithEmailAndPassword(email, password));
         }
      });
   }

   @NonNull
   public static Maybe<AuthResult> signInWithCredential(@NonNull final FirebaseAuth firebaseAuth,
                                                        @NonNull final AuthCredential credential) {
      return Maybe.create(new MaybeOnSubscribe<AuthResult>() {
         @Override
         public void subscribe(MaybeEmitter<AuthResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.signInWithCredential(credential));
         }
      });
   }

   @NonNull
   public static Maybe<AuthResult> signInWithCustomToken(@NonNull final FirebaseAuth firebaseAuth,
                                                         @NonNull final String token) {
      return Maybe.create(new MaybeOnSubscribe<AuthResult>() {
         @Override
         public void subscribe(MaybeEmitter<AuthResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.signInWithCustomToken(token));
         }
      });
   }

   @NonNull
   public static Maybe<AuthResult> createUserWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                  @NonNull final String email,
                                                                  @NonNull final String password) {
      return Maybe.create(new MaybeOnSubscribe<AuthResult>() {
         @Override
         public void subscribe(MaybeEmitter<AuthResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.createUserWithEmailAndPassword(email, password));
         }
      });
   }

   @NonNull
   public static Maybe<ProviderQueryResult> fetchProvidersForEmail(@NonNull final FirebaseAuth firebaseAuth,
                                                                   @NonNull final String email) {
      return Maybe.create(new MaybeOnSubscribe<ProviderQueryResult>() {
         @Override
         public void subscribe(MaybeEmitter<ProviderQueryResult> emitter) throws Exception {
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
   public static Maybe<FirebaseAuth> observeAuthState(@NonNull final FirebaseAuth firebaseAuth) {

      return Maybe.create(new MaybeOnSubscribe<FirebaseAuth>() {
         @Override
         public void subscribe(final MaybeEmitter<FirebaseAuth> emitter) throws Exception {
            final FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
               @Override
               public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                  emitter.onSuccess(firebaseAuth);
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