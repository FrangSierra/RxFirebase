package durdinapps.rxfirebase2.exceptions;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RxFirebaseNullDataException extends NullPointerException {
    private final static String DEFAULT_MESSAGE = "Task result was successfully but data was empty";

    public RxFirebaseNullDataException() {
    }

    public RxFirebaseNullDataException(@NonNull String detailMessage) {
        super(detailMessage);
    }

    public RxFirebaseNullDataException(@Nullable Exception resultException) {
        super(resultException != null ? resultException.getMessage() : DEFAULT_MESSAGE);
    }
}
