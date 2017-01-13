package durdinapps.rxfirebase2;

import android.support.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import durdinapps.rxfirebase2.exceptions.RxFirebaseDataException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;

public class RxFirebaseDatabase {

   @NonNull
   public static Flowable<DataSnapshot> observeValueEvent(@NonNull final Query query,
                                                          @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<DataSnapshot>() {
         @Override
         public void subscribe(final FlowableEmitter<DataSnapshot> emitter) throws Exception {
            final ValueEventListener valueEventListener = new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                  emitter.onNext(dataSnapshot);
               }

               @Override
               public void onCancelled(final DatabaseError error) {
                  emitter.onError(new RxFirebaseDataException(error));
               }
            };
            emitter.setCancellable(new Cancellable() {
               @Override
               public void cancel() throws Exception {
                  query.removeEventListener(valueEventListener);
               }
            });
            query.addValueEventListener(valueEventListener);
         }
      }, strategy);
   }


   @NonNull
   public static Flowable<DataSnapshot> observeSingleValueEvent(@NonNull final Query query,
                                                                @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<DataSnapshot>() {
         @Override
         public void subscribe(final FlowableEmitter<DataSnapshot> emitter) throws Exception {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                  emitter.onNext(dataSnapshot);
                  emitter.onComplete();
               }

               @Override
               public void onCancelled(DatabaseError error) {
                  emitter.onError(new RxFirebaseDataException(error));
               }
            });
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<DataSnapshot> runTransaction(@NonNull final DatabaseReference ref,
                                                       @NonNull final boolean fireLocalEvents,
                                                       @NonNull final long transactionValue,
                                                       @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<DataSnapshot>() {
         @Override
         public void subscribe(final FlowableEmitter<DataSnapshot> emitter) throws Exception {
            ref.runTransaction(new Transaction.Handler() {
               @Override
               public Transaction.Result doTransaction(MutableData mutableData) {
                  Integer currentValue = mutableData.getValue(Integer.class);
                  if (currentValue == null) {
                     mutableData.setValue(transactionValue);
                  } else {
                     mutableData.setValue(currentValue + transactionValue);
                  }
                  return Transaction.success(mutableData);
               }

               @Override
               public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                  emitter.onNext(dataSnapshot);
                  emitter.onComplete();
               }
            }, fireLocalEvents);
         }
      }, strategy);
   }

   @NonNull
   public static Completable updateChildren(@NonNull final DatabaseReference ref,
                                            @NonNull final Map<String, Object> updateData) {
      return Completable.create(new CompletableOnSubscribe() {
         @Override
         public void subscribe(final CompletableEmitter emitter) throws Exception {
            ref.updateChildren(updateData, new DatabaseReference.CompletionListener() {
               @Override
               public void onComplete(DatabaseError error, DatabaseReference databaseReference) {
                  if (error != null) {
                     emitter.onError(new RxFirebaseDataException(error));
                  } else {
                     emitter.onComplete();
                  }
               }
            });
         }
      });
   }

   @NonNull
   public static Flowable<RxFirebaseChildEvent<DataSnapshot>> observeChildEvent(
      @NonNull final Query query, @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<RxFirebaseChildEvent<DataSnapshot>>() {
         @Override
         public void subscribe(final FlowableEmitter<RxFirebaseChildEvent<DataSnapshot>> emitter) throws Exception {
            final ChildEventListener childEventListener = new ChildEventListener() {

               @Override
               public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                  emitter.onNext(
                     new RxFirebaseChildEvent<DataSnapshot>(dataSnapshot.getKey(), dataSnapshot, previousChildName,
                        RxFirebaseChildEvent.EventType.ADDED));
               }

               @Override
               public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                  emitter.onNext(
                     new RxFirebaseChildEvent<DataSnapshot>(dataSnapshot.getKey(), dataSnapshot, previousChildName,
                        RxFirebaseChildEvent.EventType.CHANGED));
               }

               @Override
               public void onChildRemoved(DataSnapshot dataSnapshot) {
                  emitter.onNext(new RxFirebaseChildEvent<DataSnapshot>(dataSnapshot.getKey(), dataSnapshot,
                     RxFirebaseChildEvent.EventType.REMOVED));
               }

               @Override
               public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                  emitter.onNext(
                     new RxFirebaseChildEvent<DataSnapshot>(dataSnapshot.getKey(), dataSnapshot, previousChildName,
                        RxFirebaseChildEvent.EventType.MOVED));
               }

               @Override
               public void onCancelled(DatabaseError error) {
                  emitter.onError(new RxFirebaseDataException(error));
               }
            };
            emitter.setCancellable(new Cancellable() {
               @Override
               public void cancel() throws Exception {
                  query.removeEventListener(childEventListener);
               }
            });
            query.addChildEventListener(childEventListener);

         }
      }, strategy);
   }

   @NonNull
   public static Flowable<DataSnapshot> runTransaction(@NonNull final DatabaseReference ref,
                                                       @NonNull final long transactionValue,
                                                       @NonNull BackpressureStrategy strategy) {
      return runTransaction(ref, true, transactionValue, strategy);
   }

   @NonNull
   public static <T> Flowable<T> observeValueEvent(@NonNull final Query query,
                                                   @NonNull final Class<T> clazz,
                                                   @NonNull BackpressureStrategy strategy) {
      return observeValueEvent(query, DataSnapshotMapper.of(clazz), strategy);
   }


   @NonNull
   public static <T> Flowable<T> observeSingleValueEvent(@NonNull final Query query,
                                                         @NonNull final Class<T> clazz,
                                                         @NonNull BackpressureStrategy strategy) {
      return observeSingleValueEvent(query, DataSnapshotMapper.of(clazz), strategy);
   }

   @NonNull
   public static <T> Flowable<RxFirebaseChildEvent<T>> observeChildEvent(
      @NonNull final Query query, @NonNull final Class<T> clazz,
      @NonNull BackpressureStrategy strategy) {
      return observeChildEvent(query, DataSnapshotMapper.ofChildEvent(clazz), strategy);
   }

   @NonNull
   public static <T> Flowable<T> observeValueEvent(@NonNull final Query query,
                                                   @NonNull final Function<? super DataSnapshot, ? extends T> mapper,
                                                   @NonNull BackpressureStrategy strategy) {
      return observeValueEvent(query, strategy).map(mapper);
   }

   @NonNull
   public static <T> Flowable<T> observeSingleValueEvent(@NonNull final Query query,
                                                         @NonNull final Function<? super DataSnapshot, ? extends T> mapper,
                                                         @NonNull BackpressureStrategy strategy) {
      return observeSingleValueEvent(query, strategy).map(mapper);
   }

   @NonNull
   public static <T> Flowable<RxFirebaseChildEvent<T>> observeChildEvent(
      @NonNull final Query query, @NonNull final Function<? super RxFirebaseChildEvent<DataSnapshot>,
      ? extends RxFirebaseChildEvent<T>> mapper, @NonNull BackpressureStrategy strategy) {
      return observeChildEvent(query, strategy).map(mapper);
   }

   @NonNull
   public static Flowable<DataSnapshot> observeValueEvent(@NonNull final Query query) {
      return observeValueEvent(query, BackpressureStrategy.DROP);
   }


   @NonNull
   public static Flowable<DataSnapshot> observeSingleValueEvent(@NonNull final Query query) {
      return observeSingleValueEvent(query, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<DataSnapshot> runTransaction(@NonNull final DatabaseReference ref,
                                                       @NonNull final boolean fireLocalEvents,
                                                       @NonNull final long transactionValue) {
      return runTransaction(ref, fireLocalEvents, transactionValue, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<RxFirebaseChildEvent<DataSnapshot>> observeChildEvent(
      @NonNull final Query query) {
      return observeChildEvent(query, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<DataSnapshot> runTransaction(@NonNull final DatabaseReference ref,
                                                       @NonNull final long transactionValue) {
      return runTransaction(ref, true, transactionValue, BackpressureStrategy.DROP);
   }

   @NonNull
   public static <T> Flowable<T> observeValueEvent(@NonNull final Query query,
                                                   @NonNull final Class<T> clazz) {
      return observeValueEvent(query, DataSnapshotMapper.of(clazz), BackpressureStrategy.DROP);
   }


   @NonNull
   public static <T> Flowable<T> observeSingleValueEvent(@NonNull final Query query,
                                                         @NonNull final Class<T> clazz) {
      return observeSingleValueEvent(query, DataSnapshotMapper.of(clazz), BackpressureStrategy.DROP);
   }

   @NonNull
   public static <T> Flowable<RxFirebaseChildEvent<T>> observeChildEvent(
      @NonNull final Query query, @NonNull final Class<T> clazz) {
      return observeChildEvent(query, DataSnapshotMapper.ofChildEvent(clazz), BackpressureStrategy.DROP);
   }

   @NonNull
   public static <T> Flowable<T> observeValueEvent(@NonNull final Query query,
                                                   @NonNull final Function<? super DataSnapshot, ? extends T> mapper) {
      return observeValueEvent(query, BackpressureStrategy.DROP).map(mapper);
   }

   @NonNull
   public static <T> Flowable<T> observeSingleValueEvent(@NonNull final Query query,
                                                         @NonNull final Function<? super DataSnapshot, ? extends T> mapper) {
      return observeSingleValueEvent(query, BackpressureStrategy.DROP).map(mapper);
   }

   @NonNull
   public static <T> Flowable<RxFirebaseChildEvent<T>> observeChildEvent(
      @NonNull final Query query, @NonNull final Function<? super RxFirebaseChildEvent<DataSnapshot>,
      ? extends RxFirebaseChildEvent<T>> mapper) {
      return observeChildEvent(query, BackpressureStrategy.DROP).map(mapper);
   }
}