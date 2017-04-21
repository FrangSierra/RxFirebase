package durdinapps.rxfirebase2;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Function;

import static durdinapps.rxfirebase2.RxFirebaseDatabase.observeMultipleSingleValueEvent;
import static durdinapps.rxfirebase2.RxFirebaseDatabase.observeSingleValueEvent;
import static durdinapps.rxfirebase2.RxFirebaseDatabase.requestFilteredReferenceKeys;


public class RxFirebaseQuery {
   private Maybe<DatabaseReference[]> whereMaybe;

   private RxFirebaseQuery() {
   }

   public static RxFirebaseQuery getInstance() {
      return new RxFirebaseQuery();
   }

   @NonNull
   public RxFirebaseQuery filter(@NonNull Query whereRef,
                                 @NonNull final Function<? super DataSnapshot, ? extends DatabaseReference[]> mapper) {
      whereMaybe = observeSingleValueEvent(whereRef, mapper);
      return this;
   }

   @NonNull
   public RxFirebaseQuery filterByRefs(@NonNull DatabaseReference from,
                                       @NonNull Query whereRef) {
      whereMaybe = requestFilteredReferenceKeys(from, whereRef);
      return this;
   }

   @NonNull
   public Single<List<DataSnapshot>> asList() {
      return execute().toList();
   }

   @NonNull
   public <T> Single<List<T>> asList(@NonNull final Function<? super List<DataSnapshot>, ? extends List<T>> mapper) {
      return execute().toList().map(mapper);
   }

   @NonNull
   public <T> Flowable<T> execute(@NonNull final Function<? super DataSnapshot, ? extends T> mapper) {
      return execute().map(mapper);
   }

   @NonNull
   public Flowable<DataSnapshot> execute() {
      if (whereMaybe == null)
         throw new IllegalArgumentException("It's necessary define a where function to retrieve data");

      return whereMaybe.toFlowable().flatMap(new Function<DatabaseReference[], Flowable<DataSnapshot>>() {
         @Override
         public Flowable<DataSnapshot> apply(@io.reactivex.annotations.NonNull DatabaseReference[] keys) throws Exception {
            return observeMultipleSingleValueEvent(keys);
         }
      });
   }
}

