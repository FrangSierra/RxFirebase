package durdinapps.rxfirebase2;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static durdinapps.rxfirebase2.RxTestUtil.ANY_EMAIL;
import static durdinapps.rxfirebase2.RxTestUtil.ANY_PASSWORD;
import static durdinapps.rxfirebase2.RxTestUtil.ANY_TOKEN;
import static durdinapps.rxfirebase2.RxTestUtil.EXCEPTION;
import static durdinapps.rxfirebase2.RxTestUtil.setupTask;
import static durdinapps.rxfirebase2.RxTestUtil.testOnCompleteListener;
import static durdinapps.rxfirebase2.RxTestUtil.testOnFailureListener;
import static durdinapps.rxfirebase2.RxTestUtil.testOnSuccessListener;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RxFirebaseAuthTest {

   @Mock
   private FirebaseAuth firebaseAuth;

   @Mock
   private Task<AuthResult> authResultTask;

   @Mock
   private Task<ProviderQueryResult> providerQueryResultTask;

   @Mock
   private Task<Void> voidTask;

   @Mock
   private AuthResult authResult;

   @Mock
   private ProviderQueryResult providerQueryResult;

   @Mock
   private DataSnapshot dataSnapshot;

   @Mock
   private AuthCredential authCredential;

   @Mock
   private FirebaseUser firebaseUser;


   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      setupTask(authResultTask);
      setupTask(providerQueryResultTask);
      setupTask(voidTask);

      when(firebaseAuth.signInAnonymously()).thenReturn(authResultTask);
      when(firebaseAuth.signInWithEmailAndPassword(ANY_EMAIL, ANY_PASSWORD)).thenReturn(authResultTask);
      when(firebaseAuth.signInWithCredential(authCredential)).thenReturn(authResultTask);
      when(firebaseAuth.signInWithCustomToken(ANY_TOKEN)).thenReturn(authResultTask);
      when(firebaseAuth.createUserWithEmailAndPassword(ANY_EMAIL, ANY_PASSWORD)).thenReturn(authResultTask);
      when(firebaseAuth.fetchProvidersForEmail(ANY_EMAIL)).thenReturn(providerQueryResultTask);
      when(firebaseAuth.sendPasswordResetEmail(ANY_EMAIL)).thenReturn(voidTask);

      when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);

   }

   @Test
   public void signInAnonymously() throws InterruptedException {

      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .signInAnonymously(firebaseAuth)
         .test();

      testOnSuccessListener.getValue().onSuccess(authResult);
      testOnCompleteListener.getValue().onComplete(authResultTask);

      verify(firebaseAuth).signInAnonymously();

      authTestObserver
         .assertNoErrors()
         .assertValueCount(1)
         .assertValueSet(Collections.singletonList(authResult))
         .assertComplete();
   }

   @Test
   public void signInAnonymouslyError() throws InterruptedException {

      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .signInAnonymously(firebaseAuth)
         .test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseAuth).signInAnonymously();

      authTestObserver.assertError(EXCEPTION)
         .assertNotComplete();
   }

   @Test
   public void createUserWithEmailAndPassword() throws InterruptedException {
      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .createUserWithEmailAndPassword(firebaseAuth, ANY_EMAIL, ANY_PASSWORD)
         .test();

      testOnSuccessListener.getValue().onSuccess(authResult);
      testOnCompleteListener.getValue().onComplete(authResultTask);

      verify(firebaseAuth).createUserWithEmailAndPassword(ANY_EMAIL, ANY_PASSWORD);

      authTestObserver.assertNoErrors()
         .assertValueCount(1)
         .assertValueSet(Collections.singletonList(authResult))
         .assertComplete()
         .dispose();
   }

   @Test
   public void createUserWithEmailAndPasswordError() throws InterruptedException {

      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .createUserWithEmailAndPassword(firebaseAuth, ANY_EMAIL, ANY_PASSWORD)
         .test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseAuth).createUserWithEmailAndPassword(ANY_EMAIL, ANY_PASSWORD);

      authTestObserver.assertError(EXCEPTION)
         .assertNotComplete()
         .dispose();
   }

   @Test
   public void signInWithEmailAndPassword() throws InterruptedException {

      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .signInWithEmailAndPassword(firebaseAuth, ANY_EMAIL, ANY_PASSWORD)
         .test();

      testOnSuccessListener.getValue().onSuccess(authResult);
      testOnCompleteListener.getValue().onComplete(authResultTask);

      verify(firebaseAuth).signInWithEmailAndPassword(eq(ANY_EMAIL), eq(ANY_PASSWORD));

      authTestObserver.assertNoErrors()
         .assertValueCount(1)
         .assertValueSet(Collections.singletonList(authResult))
         .assertComplete()
         .dispose();
   }

   @Test
   public void signInWithEmailAndPasswordError() throws InterruptedException {

      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .signInWithEmailAndPassword(firebaseAuth, ANY_EMAIL, ANY_PASSWORD)
         .test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseAuth).signInWithEmailAndPassword(eq(ANY_EMAIL), eq(ANY_PASSWORD));

      authTestObserver.assertError(EXCEPTION)
         .assertNotComplete()
         .dispose();
   }

   @Test
   public void signInWithCredential() throws InterruptedException {

      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .signInWithCredential(firebaseAuth, authCredential)
         .test();

      testOnSuccessListener.getValue().onSuccess(authResult);
      testOnCompleteListener.getValue().onComplete(authResultTask);

      verify(firebaseAuth).signInWithCredential(authCredential);

      authTestObserver.assertNoErrors()
         .assertValueCount(1)
         .assertValueSet(Collections.singletonList(authResult))
         .assertComplete()
         .dispose();
   }

   @Test
   public void signInWithCredentialError() throws InterruptedException {

      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .signInWithCredential(firebaseAuth, authCredential)
         .test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseAuth).signInWithCredential(authCredential);

      authTestObserver.assertError(EXCEPTION)
         .assertNotComplete()
         .dispose();
   }

   @Test
   public void signInWithCustomToken() throws InterruptedException {

      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .signInWithCustomToken(firebaseAuth, ANY_TOKEN)
         .test();

      testOnSuccessListener.getValue().onSuccess(authResult);
      testOnCompleteListener.getValue().onComplete(authResultTask);

      verify(firebaseAuth).signInWithCustomToken(eq(ANY_TOKEN));

      authTestObserver.assertNoErrors()
         .assertValueCount(1)
         .assertValueSet(Collections.singletonList(authResult))
         .assertComplete()
         .dispose();
   }

   @Test
   public void signInWithCustomTokenError() throws InterruptedException {

      TestSubscriber<AuthResult> authTestObserver = RxFirebaseAuth
         .signInWithCustomToken(firebaseAuth, ANY_TOKEN)
         .test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseAuth).signInWithCustomToken(eq(ANY_TOKEN));

      authTestObserver.assertError(EXCEPTION)
         .assertNotComplete()
         .dispose();
   }

   @Test
   public void fetchProvidersForEmail() throws InterruptedException {

      TestSubscriber<ProviderQueryResult> authTestObserver = RxFirebaseAuth
         .fetchProvidersForEmail(firebaseAuth, ANY_EMAIL)
         .test();

      testOnSuccessListener.getValue().onSuccess(providerQueryResult);
      testOnCompleteListener.getValue().onComplete(providerQueryResultTask);

      verify(firebaseAuth).fetchProvidersForEmail(eq(ANY_EMAIL));

      authTestObserver.assertNoErrors()
         .assertValueCount(1)
         .assertValueSet(Collections.singletonList(providerQueryResult))
         .assertComplete()
         .dispose();
   }

   @Test
   public void fetchProvidersForEmailError() throws InterruptedException {

      TestSubscriber<ProviderQueryResult> authTestObserver = RxFirebaseAuth
         .fetchProvidersForEmail(firebaseAuth, ANY_EMAIL)
         .test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseAuth).fetchProvidersForEmail(ANY_EMAIL);

      authTestObserver.assertError(EXCEPTION)
         .assertNotComplete()
         .dispose();
   }

   @Test
   public void sendPasswordResetEmail() throws InterruptedException {
      TestObserver authTestObserver = RxFirebaseAuth
         .sendPasswordResetEmail(firebaseAuth, ANY_EMAIL)
         .test();

      testOnCompleteListener.getValue().onComplete(voidTask);

      verify(firebaseAuth).sendPasswordResetEmail(eq(ANY_EMAIL));

      authTestObserver.assertNoErrors()
         .assertValueSet(Collections.singletonList(voidTask))
         .assertComplete()
         .dispose();
   }

   @Test
   public void sendPasswordResetEmailError() throws InterruptedException {

      TestObserver authTestObserver = RxFirebaseAuth
         .sendPasswordResetEmail(firebaseAuth, ANY_EMAIL)
         .test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseAuth).sendPasswordResetEmail(eq(ANY_EMAIL));

      authTestObserver.assertError(EXCEPTION)
         .assertNotComplete()
         .dispose();
   }

   @Test
   public void testObserveAuthState() throws InterruptedException {

      TestSubscriber<FirebaseAuth> authTestObserver = RxFirebaseAuth
         .observeAuthState(firebaseAuth)
         .test();

      ArgumentCaptor<FirebaseAuth.AuthStateListener> argument = ArgumentCaptor.forClass(FirebaseAuth.AuthStateListener.class);
      verify(firebaseAuth).addAuthStateListener(argument.capture());
      argument.getValue().onAuthStateChanged(firebaseAuth);

      authTestObserver.assertNoErrors()
         .assertValueCount(1)
         .assertValueSet(Collections.singletonList(firebaseAuth))
         .assertNotComplete()
         .dispose();
   }
}
