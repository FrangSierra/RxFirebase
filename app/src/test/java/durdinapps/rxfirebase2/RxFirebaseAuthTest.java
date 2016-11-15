package durdinapps.rxfirebase2;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.functions.Action;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RxFirebaseAuthTest {


    @Mock
    private FirebaseAuth mockAuth;

    @Mock
    private Task<AuthResult> mockAuthTask;

    @Mock
    private Task<ProviderQueryResult> mockProviderQueryResultTask;

    @Mock
    private Task<Void> mockVoidTask;

    @Mock
    private AuthResult mockAuthResult;

    @Mock
    private ProviderQueryResult mockProviderQueryResult;

    @Mock
    private DataSnapshot mockDataSnapshot;

    @Mock
    private AuthCredential mockCredentials;

    @Mock
    private FirebaseUser mockUser;

    private ArgumentCaptor<OnCompleteListener> testOnCompleteListener;
    private ArgumentCaptor<OnSuccessListener> testOnSuccessListener;
    private ArgumentCaptor<OnFailureListener> testOnFailureListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        testOnCompleteListener = ArgumentCaptor.forClass(OnCompleteListener.class);
        testOnSuccessListener = ArgumentCaptor.forClass(OnSuccessListener.class);
        testOnFailureListener = ArgumentCaptor.forClass(OnFailureListener.class);

        setupTask(mockAuthTask);
        setupTask(mockProviderQueryResultTask);
        setupTask(mockVoidTask);

        when(mockAuth.signInAnonymously()).thenReturn(mockAuthTask);
        when(mockAuth.signInWithEmailAndPassword("email", "password")).thenReturn(mockAuthTask);
        when(mockAuth.signInWithCredential(mockCredentials)).thenReturn(mockAuthTask);
        when(mockAuth.signInWithCustomToken("token")).thenReturn(mockAuthTask);
        when(mockAuth.createUserWithEmailAndPassword("email", "password")).thenReturn(mockAuthTask);
        when(mockAuth.fetchProvidersForEmail("email")).thenReturn(mockProviderQueryResultTask);
         when(mockAuth.sendPasswordResetEmail("email")).thenReturn(mockVoidTask);

        when(mockAuth.getCurrentUser()).thenReturn(mockUser);

    }

    private <T> void setupTask(Task<T> task) {
        when(task.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(task);
        when(task.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(task);
        when(task.addOnFailureListener(testOnFailureListener.capture())).thenReturn(task);
    }

    /**
     * A class containing a completable instance and counts the number of subscribers.
     */
    static final class NormalCompletable extends AtomicInteger {

        public final Completable completable = Completable.unsafeCreate(new CompletableSource() {
            @Override
            public void subscribe(CompletableObserver s) {
                getAndIncrement();
                EmptyDisposable.complete(s);
            }
        });

        /**
         * Asserts the given number of subscriptions happened.
         * @param n the expected number of subscriptions
         */
        public void assertSubscriptions(int n) {
            Assert.assertEquals(n, get());
        }
    }

    /**
     * A class containing a completable instance that emits a TestException and counts
     * the number of subscribers.
     */
    static final class ErrorCompletable extends AtomicInteger {

        private static final long serialVersionUID = 7192337844700923752L;

        public final Completable completable = Completable.unsafeCreate(new CompletableSource() {
            @Override
            public void subscribe(CompletableObserver s) {
                getAndIncrement();
                EmptyDisposable.error(new RuntimeException(), s);
            }
        });

        /**
         * Asserts the given number of subscriptions happened.
         * @param n the expected number of subscriptions
         */
        public void assertSubscriptions(int n) {
            Assert.assertEquals(n, get());
        }
    }

    /** A normal Completable object. */
    final NormalCompletable normal = new NormalCompletable();

    /** An error Completable object. */
    final ErrorCompletable error = new ErrorCompletable();

    @Test
    public void signInAnonymously() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .signInAnonymously(mockAuth)
                .test();


        testOnSuccessListener.getValue().onSuccess(mockAuthResult);
        testOnCompleteListener.getValue().onComplete(mockAuthTask);

        verify(mockAuth).signInAnonymously();

        authTestObserver
                .assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(mockAuthResult))
                .assertComplete();
    }

    @Test
    public void signInAnonymously_error() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .signInAnonymously(mockAuth)
                .test();

        Exception e = new Exception("something bad happened");
        testOnFailureListener.getValue().onFailure(e);

        verify(mockAuth).signInAnonymously();

        authTestObserver.assertError(e)
                .assertNotComplete();
    }

    @Test
    public void createUserWithEmailAndPassword() throws InterruptedException {
        TestObserver authTestObserver = RxFirebaseAuth
                .createUserWithEmailAndPassword(mockAuth, "email", "password")
                .test();

        testOnSuccessListener.getValue().onSuccess(mockAuthResult);
        testOnCompleteListener.getValue().onComplete(mockAuthTask);

        verify(mockAuth).createUserWithEmailAndPassword("email", "password");

        authTestObserver.assertNoErrors();
        authTestObserver.assertValueCount(1);
        authTestObserver.assertValueSet(Collections.singletonList(mockAuthResult));
        authTestObserver.assertComplete();
    }

    @Test
    public void createUserWithEmailAndPassword_error() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .createUserWithEmailAndPassword(mockAuth, "email", "password")
                .test();

        Exception e = new Exception("Something happend");
        testOnFailureListener.getValue().onFailure(e);

        verify(mockAuth).createUserWithEmailAndPassword("email", "password");

        authTestObserver
                .assertError(e)
                .assertNotComplete();
    }

    @Test
    public void signInWithEmailAndPassword() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .signInWithEmailAndPassword(mockAuth, "email", "password")
                .test();

        testOnSuccessListener.getValue().onSuccess(mockAuthResult);
        testOnCompleteListener.getValue().onComplete(mockAuthTask);

        verify(mockAuth).signInWithEmailAndPassword(eq("email"), eq("password"));

        authTestObserver.assertNoErrors();
        authTestObserver.assertValueCount(1);
        authTestObserver.assertValueSet(Collections.singletonList(mockAuthResult));
        authTestObserver.assertComplete();
    }

    @Test
    public void signInWithEmailAndPassword_error() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .signInWithEmailAndPassword(mockAuth, "email", "password")
                .test();

        Exception e = new Exception("something bad happened");
        testOnFailureListener.getValue().onFailure(e);

        verify(mockAuth).signInWithEmailAndPassword(eq("email"), eq("password"));

        authTestObserver.assertError(e);
        authTestObserver.assertNotComplete();
    }

    @Test
    public void signInWithCredential() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .signInWithCredential(mockAuth, mockCredentials)
                .test();

        testOnSuccessListener.getValue().onSuccess(mockAuthResult);
        testOnCompleteListener.getValue().onComplete(mockAuthTask);

        verify(mockAuth).signInWithCredential(mockCredentials);

        authTestObserver.assertNoErrors();
        authTestObserver.assertValueCount(1);
        authTestObserver.assertValueSet(Collections.singletonList(mockAuthResult));
        authTestObserver.assertComplete();
    }

    @Test
    public void signInWithCredential_error() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .signInWithCredential(mockAuth, mockCredentials)
                .test();

        Exception e = new Exception("something bad happened");
        testOnFailureListener.getValue().onFailure(e);

        verify(mockAuth).signInWithCredential(mockCredentials);

        authTestObserver.assertError(e);
        authTestObserver.assertNotComplete();
    }

    @Test
    public void signInWithCustomToken() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .signInWithCustomToken(mockAuth, "token")
                .test();

        testOnSuccessListener.getValue().onSuccess(mockAuthResult);
        testOnCompleteListener.getValue().onComplete(mockAuthTask);

        verify(mockAuth).signInWithCustomToken(eq("token"));

        authTestObserver.assertNoErrors();
        authTestObserver.assertValueCount(1);
        authTestObserver.assertValueSet(Collections.singletonList(mockAuthResult));
        authTestObserver.assertComplete();
    }

    @Test
    public void signInWithCustomToken_error() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .signInWithCustomToken(mockAuth, "token")
                .test();

        Exception e = new Exception("something bad happened");
        testOnFailureListener.getValue().onFailure(e);

        verify(mockAuth).signInWithCustomToken(eq("token"));

        authTestObserver.assertError(e);
        authTestObserver.assertNotComplete();
    }

    @Test
    public void fetchProvidersForEmail() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .fetchProvidersForEmail(mockAuth, "email")
                .test();

        testOnSuccessListener.getValue().onSuccess(mockProviderQueryResult);
        testOnCompleteListener.getValue().onComplete(mockProviderQueryResultTask);

        verify(mockAuth).fetchProvidersForEmail(eq("email"));

        authTestObserver.assertNoErrors();
        authTestObserver.assertValueCount(1);
        authTestObserver.assertValueSet(Collections.singletonList(mockProviderQueryResult));
        authTestObserver.assertComplete();
    }

    @Test
    public void fetchProvidersForEmail_error() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .fetchProvidersForEmail(mockAuth, "email")
                .test();

        Exception e = new Exception("something bad happened");
        testOnFailureListener.getValue().onFailure(e);

        verify(mockAuth).fetchProvidersForEmail("email");

        authTestObserver.assertError(e);
        authTestObserver.assertNotComplete();
    }

    @Test
    public void sendPasswordResetEmail() throws InterruptedException {
        TestObserver authTestObserver = RxFirebaseAuth
                .sendPasswordResetEmail(mockAuth, "email")
                .test();

        testOnCompleteListener.getValue().onComplete(mockVoidTask);

        verify(mockAuth).sendPasswordResetEmail(eq("email"));

        authTestObserver.assertNoErrors();
        authTestObserver.assertValueSet(Collections.singletonList(mockVoidTask));
        authTestObserver.assertComplete();
    }

    @Test
    public void sendPasswordResetEmail_error() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .sendPasswordResetEmail(mockAuth, "email")
                .test();

        Exception e = new Exception("something bad happened");
        testOnFailureListener.getValue().onFailure(e);

        verify(mockAuth).sendPasswordResetEmail(eq("email"));

        authTestObserver.assertError(e);
        authTestObserver.assertNotComplete();
    }

    @Test
    public void testObserveAuthState() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .observeAuthState(mockAuth)
                .test();

        ArgumentCaptor<FirebaseAuth.AuthStateListener> argument = ArgumentCaptor.forClass(FirebaseAuth.AuthStateListener.class);
        verify(mockAuth).addAuthStateListener(argument.capture());
        argument.getValue().onAuthStateChanged(mockAuth);

        authTestObserver.assertNoErrors();
        authTestObserver.assertValueCount(1);
        authTestObserver.assertValueSet(Collections.singletonList(mockUser));
        authTestObserver.assertNotComplete();
    }
}
