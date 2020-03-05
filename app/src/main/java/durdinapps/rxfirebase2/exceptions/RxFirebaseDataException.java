package durdinapps.rxfirebase2.exceptions;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;

public class RxFirebaseDataException extends Exception {

    protected DatabaseError error;

    public RxFirebaseDataException(@NonNull DatabaseError error) {
        this.error = error;
    }

    public DatabaseError getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RxFirebaseDataException{" +
            "error=" + error +
            '}';
    }
}
