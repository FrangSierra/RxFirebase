package durdinapps.rxfirebase2.exceptions;


import android.support.annotation.NonNull;

public class RxFirebaseNullDataException extends NullPointerException {

   public RxFirebaseNullDataException() {
   }

   public RxFirebaseNullDataException(@NonNull String detailMessage) {
      super(detailMessage);
   }

}
