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
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;

public class RxFirebaseDatabase {

   /**
    * Listener for changes in te data at the given query location.
    *
    * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
    * @return a {@link Flowable} which emits when a value of the database change in the given query.
    */
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

   /**
    * Listener for a single change in te data at the given query location.
    *
    * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @return a {@link Maybe} which emits the actual state of the database for the given query.
    */
   @NonNull
   public static Maybe<DataSnapshot> observeSingleValueEvent(@NonNull final Query query) {
      return Maybe.create(new MaybeOnSubscribe<DataSnapshot>() {
         @Override
         public void subscribe(final MaybeEmitter<DataSnapshot> emitter) throws Exception {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                  emitter.onSuccess(dataSnapshot);
                  emitter.onComplete();
               }

               @Override
               public void onCancelled(DatabaseError error) {
                  emitter.onError(new RxFirebaseDataException(error));
               }
            });
         }
      });
   }

   /**
    * Run a transaction on the data at this location. For more information on running transactions, see
    *
    * @param ref              reference represents a particular location in your database.
    * @param fireLocalEvents  boolean which allow to receive calls of your transaction in your local device.
    * @param transactionValue value of the transaction.
    * @return a {@link Single} which emits the final {@link DataSnapshot} value if the transaction success.
    */
   @NonNull
   public static Single<DataSnapshot> runTransaction(@NonNull final DatabaseReference ref,
                                                     @NonNull final boolean fireLocalEvents,
                                                     @NonNull final long transactionValue) {
      return Single.create(new SingleOnSubscribe<DataSnapshot>() {
         @Override public void subscribe(final SingleEmitter emitter) throws Exception {
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
                  if (databaseError != null) {
                     emitter.onError(new RxFirebaseDataException(databaseError));
                  } else {
                     emitter.onSuccess(dataSnapshot);
                  }
               }
            }, fireLocalEvents);
         }
      });
   }

   /**
    * Update the specific child keys to the specified values.
    *
    * @param ref        reference represents a particular location in your database.
    * @param updateData The paths to update and their new values
    * @return a {@link Completable} which is complete when the update children call finish successfully.
    */
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

   /**
    * Listener for for child events occurring at the given query location.
    *
    * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
    * @return a {@link Flowable} which emits when a value of a child int the database change on the given query.
    */
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
                     new RxFirebaseChildEvent<>(dataSnapshot.getKey(), dataSnapshot, previousChildName,
                        RxFirebaseChildEvent.EventType.ADDED));
               }

               @Override
               public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                  emitter.onNext(
                     new RxFirebaseChildEvent<>(dataSnapshot.getKey(), dataSnapshot, previousChildName,
                        RxFirebaseChildEvent.EventType.CHANGED));
               }

               @Override
               public void onChildRemoved(DataSnapshot dataSnapshot) {
                  emitter.onNext(new RxFirebaseChildEvent<>(dataSnapshot.getKey(), dataSnapshot,
                     RxFirebaseChildEvent.EventType.REMOVED));
               }

               @Override
               public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                  emitter.onNext(
                     new RxFirebaseChildEvent<>(dataSnapshot.getKey(), dataSnapshot, previousChildName,
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

   /**
    * Listener for changes in te data at the given query location.
    *
    * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param clazz    class type for the {@link DataSnapshot} items.
    * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
    * @return a {@link Flowable} which emits when a value of the database change in the given query.
    */
   @NonNull
   public static <T> Flowable<T> observeValueEvent(@NonNull final Query query,
                                                   @NonNull final Class<T> clazz,
                                                   @NonNull BackpressureStrategy strategy) {
      return observeValueEvent(query, DataSnapshotMapper.of(clazz), strategy);
   }

   /**
    * Listener for a single change in te data at the given query location.
    *
    * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param clazz class type for the {@link DataSnapshot} items.
    * @return a {@link Maybe} which emits the actual state of the database for the given query.
    */
   @NonNull
   public static <T> Maybe<T> observeSingleValueEvent(@NonNull final Query query,
                                                      @NonNull final Class<T> clazz) {
      return observeSingleValueEvent(query, DataSnapshotMapper.of(clazz));
   }

   /**
    * Listener for for child events occurring at the given query location.
    *
    * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param clazz    class type for the {@link DataSnapshot} items.
    * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
    * @return a {@link Flowable} which emits when a value of a child int the database change on the given query.
    */
   @NonNull
   public static <T> Flowable<RxFirebaseChildEvent<T>> observeChildEvent(
      @NonNull final Query query, @NonNull final Class<T> clazz,
      @NonNull BackpressureStrategy strategy) {
      return observeChildEvent(query, DataSnapshotMapper.ofChildEvent(clazz), strategy);
   }

   /**
    * Listener for changes in te data at the given query location.
    *
    * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param mapper   specific function to map the dispatched events.
    * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
    * @return a {@link Flowable} which emits when a value of the database change in the given query.
    */
   @NonNull
   public static <T> Flowable<T> observeValueEvent(@NonNull final Query query,
                                                   @NonNull final Function<? super DataSnapshot, ? extends T> mapper,
                                                   @NonNull BackpressureStrategy strategy) {
      return observeValueEvent(query, strategy).map(mapper);
   }

   /**
    * Listener for a single change in te data at the given query location.
    *
    * @param query  reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param mapper specific function to map the dispatched events.
    * @return a {@link Maybe} which emits the actual state of the database for the given query.
    */
   @NonNull
   public static <T> Maybe<T> observeSingleValueEvent(@NonNull final Query query,
                                                      @NonNull final Function<? super DataSnapshot, ? extends T> mapper) {
      return observeSingleValueEvent(query).map(mapper);
   }

   /**
    * Listener for for child events occurring at the given query location.
    *
    * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param mapper   specific function to map the dispatched events.
    * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
    * @return a {@link Flowable} which emits when a value of a child int the database change on the given query.
    */
   @NonNull
   public static <T> Flowable<RxFirebaseChildEvent<T>> observeChildEvent(
      @NonNull final Query query, @NonNull final Function<? super RxFirebaseChildEvent<DataSnapshot>,
      ? extends RxFirebaseChildEvent<T>> mapper, @NonNull BackpressureStrategy strategy) {
      return observeChildEvent(query, strategy).map(mapper);
   }

   /**
    * Listener for changes in the data at the given query location.
    *
    * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @return a {@link Flowable} which emits when a value of the database change in the given query.
    */
   @NonNull
   public static Flowable<DataSnapshot> observeValueEvent(@NonNull final Query query) {
      return observeValueEvent(query, BackpressureStrategy.DROP);
   }


   /**
    * Listener for for child events occurring at the given query location.
    *
    * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @return a {@link Flowable} which emits when a value of a child int the database change on the given query.
    */
   @NonNull
   public static Flowable<RxFirebaseChildEvent<DataSnapshot>> observeChildEvent(
      @NonNull final Query query) {
      return observeChildEvent(query, BackpressureStrategy.DROP);
   }

   /**
    * Run a transaction on the data at this location. For more information on running transactions, see
    *
    * @param ref              reference represents a particular location in your database.
    * @param transactionValue value of the transaction.
    * @return a {@link Single} which emits the final {@link DataSnapshot} value if the transaction success.
    */
   @NonNull
   public static Single<DataSnapshot> runTransaction(@NonNull final DatabaseReference ref,
                                                     @NonNull final long transactionValue) {
      return runTransaction(ref, true, transactionValue);
   }

   /**
    * Listener for changes in te data at the given query location.
    *
    * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param clazz class type for the {@link DataSnapshot} items.
    * @return a {@link Flowable} which emits when a value of the database change in the given query.
    */
   @NonNull
   public static <T> Flowable<T> observeValueEvent(@NonNull final Query query,
                                                   @NonNull final Class<T> clazz) {
      return observeValueEvent(query, DataSnapshotMapper.of(clazz), BackpressureStrategy.DROP);
   }

   /**
    * Listener for for child events occurring at the given query location.
    *
    * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param clazz class type for the {@link DataSnapshot} items.
    * @return a {@link Flowable} which emits when a value of a child int the database change on the given query.
    */
   @NonNull
   public static <T> Flowable<RxFirebaseChildEvent<T>> observeChildEvent(
      @NonNull final Query query, @NonNull final Class<T> clazz) {
      return observeChildEvent(query, DataSnapshotMapper.ofChildEvent(clazz), BackpressureStrategy.DROP);
   }

   /**
    * Listener for changes in te data at the given query location.
    *
    * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @return a {@link Flowable} which emits when a value of the database change in the given query.
    */
   @NonNull
   public static <T> Flowable<T> observeValueEvent(@NonNull final Query query,
                                                   @NonNull final Function<? super DataSnapshot, ? extends T> mapper) {
      return observeValueEvent(query, BackpressureStrategy.DROP).map(mapper);
   }

   /**
    * Listener for for child events occurring at the given query location.
    *
    * @param query  reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    * @param mapper specific function to map the dispatched events.
    * @return a {@link Flowable} which emits when a value of a child int the database change on the given query.
    */
   @NonNull
   public static <T> Flowable<RxFirebaseChildEvent<T>> observeChildEvent(
      @NonNull final Query query, @NonNull final Function<? super RxFirebaseChildEvent<DataSnapshot>,
      ? extends RxFirebaseChildEvent<T>> mapper) {
      return observeChildEvent(query, BackpressureStrategy.DROP).map(mapper);
   }
}