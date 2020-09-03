package durdinapps.rxfirebase2;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import io.reactivex.CompletableEmitter;


public class RxCompletableHandler implements OnFailureListener, OnSuccessListener, OnCompleteListener {

    private final CompletableEmitter completableEmitter;

    private RxCompletableHandler(CompletableEmitter completableEmitter) {
        this.completableEmitter = completableEmitter;
    }

    public static <T> void assignOnTask(CompletableEmitter completableEmitter, Task<T> task) {
        RxCompletableHandler handler = new RxCompletableHandler(completableEmitter);
        task.addOnFailureListener(handler);
        task.addOnSuccessListener(handler);
        try {
            task.addOnCompleteListener(handler);
        } catch (Throwable t) {
            // ignore
        }
    }


    @Override
    public void onFailure(@NonNull Exception e) {
        if (!completableEmitter.isDisposed())
            completableEmitter.onError(e);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        completableEmitter.onComplete();
    }

    @Override
    public void onSuccess(Object o) {
        completableEmitter.onComplete();
    }
}
