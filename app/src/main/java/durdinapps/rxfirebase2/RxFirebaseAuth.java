package durdinapps.rxfirebase2;

import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;

public class RxFirebaseAuth {

   @NonNull
   public static Flowable<AuthResult> signInAnonymously(@NonNull final FirebaseAuth firebaseAuth,
                                                        @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<AuthResult>() {
         @Override
         public void subscribe(FlowableEmitter<AuthResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.signInAnonymously());
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<AuthResult> signInWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                 @NonNull final String email,
                                                                 @NonNull final String password,
                                                                 @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<AuthResult>() {
         @Override public void subscribe(FlowableEmitter<AuthResult> e) throws Exception {
            RxHandler.assignOnTask(e, firebaseAuth.signInWithEmailAndPassword(email, password));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<AuthResult> signInWithCredential(@NonNull final FirebaseAuth firebaseAuth,
                                                           @NonNull final AuthCredential credential,
                                                           @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<AuthResult>() {
         @Override
         public void subscribe(FlowableEmitter<AuthResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.signInWithCredential(credential));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<AuthResult> signInWithCustomToken(@NonNull final FirebaseAuth firebaseAuth,
                                                            @NonNull final String token,
                                                            @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<AuthResult>() {
         @Override
         public void subscribe(FlowableEmitter<AuthResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.signInWithCustomToken(token));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<AuthResult> createUserWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                     @NonNull final String email,
                                                                     @NonNull final String password,
                                                                     @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<AuthResult>() {
         @Override
         public void subscribe(FlowableEmitter<AuthResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.createUserWithEmailAndPassword(email, password));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<ProviderQueryResult> fetchProvidersForEmail(@NonNull final FirebaseAuth firebaseAuth,
                                                                      @NonNull final String email,
                                                                      @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<ProviderQueryResult>() {
         @Override
         public void subscribe(FlowableEmitter<ProviderQueryResult> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, firebaseAuth.fetchProvidersForEmail(email));
         }
      }, strategy);
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
   public static Flowable<FirebaseUser> observeAuthState(@NonNull final FirebaseAuth firebaseAuth,
                                                         @NonNull BackpressureStrategy strategy) {

      return Flowable.create(new FlowableOnSubscribe<FirebaseUser>() {
         @Override
         public void subscribe(final FlowableEmitter<FirebaseUser> emitter) throws Exception {
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
      }, strategy);
   }

   @NonNull
   public static Flowable<AuthResult> signInWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                 @NonNull final String email,
                                                                 @NonNull final String password) {
      return signInWithEmailAndPassword(firebaseAuth, email, password, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<AuthResult> signInAnonymously(@NonNull final FirebaseAuth firebaseAuth) {
      return signInAnonymously(firebaseAuth, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<AuthResult> signInWithCredential(@NonNull final FirebaseAuth firebaseAuth,
                                                           @NonNull final AuthCredential credential) {
      return signInWithCredential(firebaseAuth, credential, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<AuthResult> signInWithCustomToken(@NonNull final FirebaseAuth firebaseAuth,
                                                            @NonNull final String token) {
      return signInWithCustomToken(firebaseAuth, token, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<AuthResult> createUserWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                     @NonNull final String email,
                                                                     @NonNull final String password) {
      return createUserWithEmailAndPassword(firebaseAuth, email, password, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<ProviderQueryResult> fetchProvidersForEmail(@NonNull final FirebaseAuth firebaseAuth,
                                                                      @NonNull final String email) {
      return fetchProvidersForEmail(firebaseAuth, email, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<FirebaseUser> observeAuthState(@NonNull final FirebaseAuth firebaseAuth) {

      return observeAuthState(firebaseAuth, BackpressureStrategy.DROP);
   }
}