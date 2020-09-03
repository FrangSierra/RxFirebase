package durdinapps.rxfirebase2;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.observers.TestObserver;

import static durdinapps.rxfirebase2.RxTestUtil.EXCEPTION;
import static durdinapps.rxfirebase2.RxTestUtil.setupTask;
import static durdinapps.rxfirebase2.RxTestUtil.testOnCompleteListener;
import static durdinapps.rxfirebase2.RxTestUtil.testOnFailureListener;
import static durdinapps.rxfirebase2.RxTestUtil.testOnSuccessListener;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RxFirebaseMessagingTest {

    @Mock
    private Task<InstanceIdResult> instanceIdResultTask;

    @Mock
    private FirebaseInstanceId instanceId;

    @Mock
    private InstanceIdResult instanceIdResult;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        setupTask(instanceIdResultTask);

        when(instanceId.getInstanceId()).thenReturn(instanceIdResultTask);
    }

    @Test
    public void getInstanceId() {
        TestObserver<InstanceIdResult> instanceIdObserver = RxFirebaseMessaging.getInstanceId(instanceId).test();

        testOnSuccessListener.getValue().onSuccess(instanceIdResult);
        testOnCompleteListener.getValue().onComplete(instanceIdResultTask);

        verify(instanceId).getInstanceId();

        instanceIdObserver.assertComplete()
                .assertNoErrors()
                .assertValueCount(1)
                .dispose();

    }

    @Test
    public void getInstanceIdError() {
        TestObserver<InstanceIdResult> instanceIdObserver = RxFirebaseMessaging.getInstanceId(instanceId).test();

        testOnFailureListener.getValue().onFailure(EXCEPTION);
        verify(instanceId).getInstanceId();

        instanceIdObserver.assertError(EXCEPTION)
                .dispose();

    }
}
