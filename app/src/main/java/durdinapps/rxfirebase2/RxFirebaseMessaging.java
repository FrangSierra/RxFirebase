package durdinapps.rxfirebase2;

import android.support.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

public class RxFirebaseMessaging {

    /**
     * Asynchronously retrieves from an instanceIdResult from the {@link com.google.firebase.iid.FirebaseInstanceId}
     *
     * @param instanceId the firebase instanceId object
     * @return a {@link Maybe} which emits an {@link com.google.firebase.iid.InstanceIdResult} if successful
     */
    @NonNull
    public static Maybe<InstanceIdResult> getInstanceId(@NonNull final FirebaseInstanceId instanceId) {
        return Maybe.create(new MaybeOnSubscribe<InstanceIdResult>() {
            @Override
            public void subscribe(MaybeEmitter<InstanceIdResult> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, instanceId.getInstanceId());
            }
        });
    }
}
