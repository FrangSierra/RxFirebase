package durdinapps.rxfirebase2;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import io.reactivex.observers.TestObserver;

import static durdinapps.rxfirebase2.RxTestUtil.ANY_EMAIL;
import static durdinapps.rxfirebase2.RxTestUtil.ANY_TIME;
import static durdinapps.rxfirebase2.RxTestUtil.EXCEPTION;
import static durdinapps.rxfirebase2.RxTestUtil.setupTask;
import static durdinapps.rxfirebase2.RxTestUtil.testOnCompleteListener;
import static durdinapps.rxfirebase2.RxTestUtil.testOnFailureListener;
import static durdinapps.rxfirebase2.RxTestUtil.testOnSuccessListener;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RxFirebaseRemoteTest {

    @Mock
    private Task<Void> voidTask;

    @Mock
    private FirebaseRemoteConfig firebaseConfig;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        setupTask(voidTask);

        when(firebaseConfig.fetch(ANY_TIME)).thenReturn(voidTask);
    }

    @Test
    public void fetchRemoteConfig() {
        TestObserver fetchTestObserver = RxFirebaseRemote
            .fetch(firebaseConfig, ANY_TIME)
            .test();

        testOnCompleteListener.getValue().onComplete(voidTask);

        verify(firebaseConfig).fetch(eq(ANY_TIME));

        fetchTestObserver.assertNoErrors()
            .assertValueSet(Collections.singletonList(voidTask))
            .assertComplete()
            .dispose();
    }

    @Test
    public void fetchRemoteFailure() {
        TestObserver fetchTestObserver = RxFirebaseRemote
            .fetch(firebaseConfig, ANY_TIME)
            .test();

        testOnFailureListener.getValue().onFailure(EXCEPTION);

        verify(firebaseConfig).fetch(eq(ANY_TIME));

        fetchTestObserver.assertError(EXCEPTION)
            .assertNotComplete()
            .dispose();
    }
}
