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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import io.reactivex.observers.TestObserver;

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
            //setupTask(mockVoidTask);

            when(mockAuth.signInAnonymously()).thenReturn(mockAuthTask);
            when(mockAuth.signInWithEmailAndPassword("email", "password")).thenReturn(mockAuthTask);
            when(mockAuth.signInWithCredential(mockCredentials)).thenReturn(mockAuthTask);
            when(mockAuth.signInWithCustomToken("token")).thenReturn(mockAuthTask);
            when(mockAuth.createUserWithEmailAndPassword("email", "password")).thenReturn(mockAuthTask);
            when(mockAuth.fetchProvidersForEmail("email")).thenReturn(mockProviderQueryResultTask);
           // when(mockAuth.sendPasswordResetEmail("email")).thenReturn(mockVoidTask);

            when(mockAuth.getCurrentUser()).thenReturn(mockUser);

    }

    private <T> void setupTask(Task<T> task) {
        when(task.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(task);
        when(task.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(task);
        when(task.addOnFailureListener(testOnFailureListener.capture())).thenReturn(task);
    }

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
    public void signInAnonymously_Failed() throws InterruptedException {

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
    public void createAccount_Failed() throws InterruptedException {

        TestObserver authTestObserver = RxFirebaseAuth
                .createUserWithEmailAndPassword(mockAuth, "email", "password")
                .test();

        Exception e = new Exception("Something happend");
        testOnFailureListener.getValue().onFailure(e);

        verify(mockAuth).createUserWithEmailAndPassword("email", "password");

        authTestObserver.assertError(e)
        .assertNotComplete();
    }


}
