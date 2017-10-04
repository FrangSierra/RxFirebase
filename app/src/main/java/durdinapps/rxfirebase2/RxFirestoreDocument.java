package durdinapps.rxfirebase2;


import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.Map;

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
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;

import static durdinapps.rxfirebase2.DocumentSnapshotMapper.DOCUMENT_EXISTENCE_PREDICATE;

public class RxFirestoreDocument {

   @NonNull
   public static <T> Completable runTransaction(@NonNull final FirebaseFirestore firestore,
                                                @NonNull final Transaction.Function<T> function) {
      return Completable.create(new CompletableOnSubscribe() {
         @Override
         public void subscribe(CompletableEmitter emitter) throws Exception {
            RxCompletableHandler.assignOnTask(emitter, firestore.runTransaction(function));
         }
      });
   }

   public static Completable atomicOperation(@NonNull final WriteBatch batch) {
      return Completable.create(new CompletableOnSubscribe() {
         @Override
         public void subscribe(CompletableEmitter emitter) throws Exception {
            RxCompletableHandler.assignOnTask(emitter, batch.commit());
         }
      });
   }

   @NonNull
   public static Completable updateDocument(@NonNull final DocumentReference ref,
                                            @NonNull final Map<String, Object> updateFieldsMap) {
      return Completable.create(new CompletableOnSubscribe() {
         @Override
         public void subscribe(CompletableEmitter emitter) throws Exception {
            RxCompletableHandler.assignOnTask(emitter, ref.update(updateFieldsMap));
         }
      });
   }

   @NonNull
   public static Completable updateDocument(@NonNull final DocumentReference ref,
                                            @NonNull final String field,
                                            final Object value,
                                            final Object... moreFieldsAndValues) {
      return Completable.create(new CompletableOnSubscribe() {
         @Override
         public void subscribe(CompletableEmitter emitter) throws Exception {
            RxCompletableHandler.assignOnTask(emitter, ref.update(field, value, moreFieldsAndValues));
         }
      });
   }

   @NonNull
   public static Completable updateDocument(@NonNull final DocumentReference ref,
                                            @NonNull final FieldPath fieldPath,
                                            final Object value,
                                            final Object... moreFieldsAndValues) {
      return Completable.create(new CompletableOnSubscribe() {
         @Override
         public void subscribe(CompletableEmitter emitter) throws Exception {
            RxCompletableHandler.assignOnTask(emitter, ref.update(fieldPath, value, moreFieldsAndValues));
         }
      });
   }

   @NonNull
   public static Completable setDocument(@NonNull final DocumentReference ref,
                                         @NonNull final Map<String, Object> setFieldsMap,
                                         @NonNull final SetOptions options) {
      return Completable.create(new CompletableOnSubscribe() {
         @Override
         public void subscribe(CompletableEmitter emitter) throws Exception {
            RxCompletableHandler.assignOnTask(emitter, ref.set(setFieldsMap, options));
         }
      });
   }


   @NonNull
   public static Completable setDocument(@NonNull final DocumentReference ref,
                                         @NonNull final Object pojo,
                                         @NonNull final SetOptions options) {
      return Completable.create(new CompletableOnSubscribe() {
         @Override
         public void subscribe(CompletableEmitter emitter) throws Exception {
            RxCompletableHandler.assignOnTask(emitter, ref.set(pojo, options));
         }
      });
   }

   @NonNull
   public static Completable setDocument(@NonNull final DocumentReference ref,
                                         @NonNull final Map<String, Object> setFieldsMap) {
      return setDocument(ref, setFieldsMap, SetOptions.merge());
   }

   @NonNull
   public static Completable setDocument(@NonNull final DocumentReference ref,
                                         @NonNull final Object pojo) {
      return setDocument(ref, pojo, SetOptions.merge());
   }

   @NonNull
   public static Completable delete(@NonNull final DocumentReference ref) {
      return Completable.create(new CompletableOnSubscribe() {
         @Override
         public void subscribe(CompletableEmitter emitter) throws Exception {
            RxCompletableHandler.assignOnTask(emitter, ref.delete());
         }
      });
   }

   @NonNull
   public static Maybe<DocumentSnapshot> getDocument(@NonNull final DocumentReference ref) {
      return Maybe.create(new MaybeOnSubscribe<DocumentSnapshot>() {
         @Override
         public void subscribe(final MaybeEmitter<DocumentSnapshot> emitter) throws Exception {
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.isSuccessful()) {
                     DocumentSnapshot document = task.getResult();
                     if (document != null) {
                        emitter.onSuccess(task.getResult());
                     }
                     emitter.onComplete();
                  } else {
                     emitter.onError(task.getException());
                  }
               }
            });
         }
      });
   }

   @NonNull
   public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                               @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
         @Override
         public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
            final ListenerRegistration registration = ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
               @Override
               public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                  if (e != null) {
                     emitter.onError(e);
                     return;
                  }

                  if (documentSnapshot != null && documentSnapshot.exists()) {
                     emitter.onNext(documentSnapshot);
                  }
               }
            });
            emitter.setCancellable(new Cancellable() {
               @Override public void cancel() throws Exception {
                  registration.remove();
               }
            });
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref) {
      return observeDocumentRef(ref, BackpressureStrategy.DROP);
   }

   @NonNull
   public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                    @NonNull final Class<T> clazz) {
      return observeDocumentRef(ref, DocumentSnapshotMapper.of(clazz));
   }

   @NonNull
   public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                    @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
      return observeDocumentRef(ref)
         .filter(DOCUMENT_EXISTENCE_PREDICATE)
         .map(mapper);
   }

   @NonNull
   public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                    @NonNull BackpressureStrategy strategy,
                                                    @NonNull final Class<T> clazz) {
      return observeDocumentRef(ref, strategy, DocumentSnapshotMapper.of(clazz));
   }

   @NonNull
   public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                    @NonNull BackpressureStrategy strategy,
                                                    @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
      return observeDocumentRef(ref, strategy)
         .filter(DOCUMENT_EXISTENCE_PREDICATE)
         .map(mapper);
   }

   @NonNull
   public static <T> Maybe<T> getDocument(@NonNull final DocumentReference ref,
                                          @NonNull final Class<T> clazz) {
      return getDocument(ref, DocumentSnapshotMapper.of(clazz));
   }

   @NonNull
   public static <T> Maybe<T> getDocument(@NonNull final DocumentReference ref,
                                          @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
      return getDocument(ref)
         .filter(DOCUMENT_EXISTENCE_PREDICATE)
         .map(mapper);
   }
}
