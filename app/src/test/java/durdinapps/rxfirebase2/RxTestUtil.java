package durdinapps.rxfirebase2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.mockito.ArgumentCaptor;

import durdinapps.rxfirebase2.exceptions.RxFirebaseNullDataException;

import static org.mockito.Mockito.when;

public class RxTestUtil {
    public static final String ANY_EMAIL = "email@email.com";
    public static final String RESULT_CODE = "ABC";
    public static final String ANY_CODE = "ABCDE";
    public static final Exception EXCEPTION = new Exception("Something bad happen");
    public static final Exception NULL_FIREBASE_EXCEPTION = new RxFirebaseNullDataException();
    public static final String ANY_PASSWORD = "ANY_PASSWORD";
    public static final String ANY_TOKEN = "ANY_KEY";
    public static final String ANY_KEY = "_token_";
    public static final String PREVIOUS_CHILD_NAME = "NONE";

    public static ArgumentCaptor<OnCompleteListener> testOnCompleteListener = ArgumentCaptor.forClass(OnCompleteListener.class);
    public static ArgumentCaptor<OnSuccessListener> testOnSuccessListener = ArgumentCaptor.forClass(OnSuccessListener.class);
    public static ArgumentCaptor<OnFailureListener> testOnFailureListener = ArgumentCaptor.forClass(OnFailureListener.class);

    public static <T> void setupTask(Task<T> task) {
        when(task.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(task);
        when(task.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(task);
        when(task.addOnFailureListener(testOnFailureListener.capture())).thenReturn(task);
    }
}
