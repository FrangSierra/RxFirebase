package durdinapps.rxfirebase2;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import durdinapps.rxfirebase2.exceptions.RxFirebaseNullDataException;
import io.reactivex.MaybeEmitter;

public class RxHandler<T> implements OnSuccessListener<T>, OnFailureListener, OnCompleteListener<T> {

    private final MaybeEmitter<? super T> emitter;

    private RxHandler(MaybeEmitter<? super T> emitter) {
        this.emitter = emitter;
    }

    public static <T> void assignOnTask(MaybeEmitter<? super T> emitter, Task<T> task) {
        RxHandler handler = new RxHandler(emitter);
        task.addOnSuccessListener(handler);
        task.addOnFailureListener(handler);
        try {
            task.addOnCompleteListener(handler);
        } catch (Throwable t) {
            // ignore
        }
    }

    @Override
    public void onSuccess(T res) {
        if (res != null) {
            emitter.onSuccess(res);
        } else {
            emitter.onError(new RxFirebaseNullDataException("Observables can't emit null values"));
        }
    }

    @Override
    public void onComplete(@NonNull Task<T> task) {
        emitter.onComplete();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (!emitter.isDisposed())
            emitter.onError(e);
    }
}