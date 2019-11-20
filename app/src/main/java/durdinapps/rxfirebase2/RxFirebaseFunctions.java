package durdinapps.rxfirebase2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class RxFirebaseFunctions {

    /**
     * Calls a {@link HttpsCallableResult} which represents a reference to a Google Cloud Functions HTTPS callable function.
     *
     * @param functions Instance of {@link FirebaseFunctions}
     * @param name      Name of the Google Cloud function
     * @param data      Params for the request.
     * @return a {@link Single} which will emit the result of the given function.
     */
    @NonNull
    public static Single<HttpsCallableResult> getHttpsCallable(@NonNull final FirebaseFunctions functions,
                                                               @NonNull final String name,
                                                               @Nullable final Object data) {
        return Single.create(new SingleOnSubscribe<HttpsCallableResult>() {
            @Override
            public void subscribe(final SingleEmitter<HttpsCallableResult> emitter) {
                functions.getHttpsCallable(name)
                    .call(data)
                    .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                        @Override
                        public void onSuccess(HttpsCallableResult httpsCallableResult) {
                            emitter.onSuccess(httpsCallableResult);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            emitter.onError(e);
                        }
                    });
            }
        });
    }


    /**
     * Calls a {@link HttpsCallableResult} which represents a reference to a Google Cloud Functions HTTPS callable function.
     *
     * @param functions Instance of {@link FirebaseFunctions}
     * @param name      Name of the Google Cloud function
     * @return a {@link Single} which will emit the result of the given function.
     */
    @NonNull
    public static Single<HttpsCallableResult> getHttpsCallable(@NonNull final FirebaseFunctions functions,
                                                               @NonNull final String name) {
        return getHttpsCallable(functions, name, null);
    }
}
