package durdinapps.rxfirebase2;


import android.app.Activity;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import durdinapps.rxfirebase2.exceptions.RxFirebaseNullDataException;
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
import io.reactivex.schedulers.Schedulers;

import static durdinapps.rxfirebase2.DocumentSnapshotMapper.DOCUMENT_EXISTENCE_PREDICATE;
import static durdinapps.rxfirebase2.DocumentSnapshotMapper.QUERY_EXISTENCE_PREDICATE;

public class RxFirestore {

    /**
     * Executes the given updateFunction and then attempts to commit the changes applied within the transaction.
     * If any document read within the transaction has changed, the updateFunction will be retried.
     * If it fails to commit after 5 attempts, the transaction will fail.
     *
     * @param firestore FirebaseFirestore instance.
     * @param function  The function to execute within the transaction context.
     */
    @NonNull
    public static Completable runTransaction(@NonNull final FirebaseFirestore firestore,
                                             @NonNull final Transaction.Function<Object> function) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, firestore.runTransaction(function));
            }
        });
    }

    /**
     * Execute all of the writes in this write batch as a single atomic unit.
     *
     * @param batch A write batch, used to perform multiple writes as a single atomic unit.
     */
    public static Completable atomicOperation(@NonNull final WriteBatch batch) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, batch.commit());
            }
        });
    }

    /**
     * Execute all of the writes in this write batch as a single atomic unit.
     *
     * @param batches A list of write batched, used to perform multiple writes as a single atomic unit.
     */
    public static Completable atomicOperation(@NonNull final List<WriteBatch> batches) {
        if (batches.isEmpty()) throw new IllegalArgumentException("Batches list can't be empty");

        List<Completable> batchTasks = new ArrayList<>();
        for (final WriteBatch batch : batches) {
            batchTasks.add(Completable.create(new CompletableOnSubscribe() {
                @Override
                public void subscribe(final CompletableEmitter emitter) {
                    batch.commit()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                emitter.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (!emitter.isDisposed())
                                    emitter.onError(e);
                            }
                        });
                }
            }).subscribeOn(Schedulers.io()));
        }

        return Completable.merge(batchTasks);
    }

    /**
     * Adds a new document to this collection with the specified data, assigning it a document ID automatically.
     *
     * @param ref  The given Collection reference.
     * @param data A Map containing the data for the new document..
     * @return a Single which emits the {@link DocumentReference} of the added Document.
     */
    @NonNull
    public static Single<DocumentReference> addDocument(@NonNull final CollectionReference ref,
                                                        @NonNull final Map<String, Object> data) {
        return Single.create(new SingleOnSubscribe<DocumentReference>() {
            @Override
            public void subscribe(final SingleEmitter<DocumentReference> emitter) {
                ref.add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.getResult() == null){
                            emitter.onError(new RxFirebaseNullDataException(task.getException()));
                            return;
                        }
                        emitter.onSuccess(task.getResult());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!emitter.isDisposed())
                            emitter.onError(e);
                    }
                });
            }
        });
    }

    /**
     * Adds a new document to this collection with the specified POJO as contents, assigning it a document ID automatically.
     *
     * @param ref  The given Collection reference.
     * @param pojo The POJO that will be used to populate the contents of the document.
     * @return a Single which emits the {@link DocumentReference} of the added Document.
     */
    @NonNull
    public static Single<DocumentReference> addDocument(@NonNull final CollectionReference ref,
                                                        @NonNull final Object pojo) {
        return Single.create(new SingleOnSubscribe<DocumentReference>() {
            @Override
            public void subscribe(final SingleEmitter<DocumentReference> emitter) {
                ref.add(pojo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.getResult() == null){
                            emitter.onError(new RxFirebaseNullDataException(task.getException()));
                            return;
                        }
                        emitter.onSuccess(task.getResult());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!emitter.isDisposed())
                            emitter.onError(e);
                    }
                });
            }
        });
    }

    /**
     * Adds a new document to this collection with the specified data, assigning it a document ID automatically.
     * <p>
     * This method will just call the API `add` method without wait for any complete listener. This is made in this way because the
     * listeners required connection for this kind of operations.
     *
     * @param ref  The given Collection reference.
     * @param data A Map containing the data for the new document..
     * @return a Single which emits the {@link DocumentReference} of the added Document.
     */
    @NonNull
    public static Completable addDocumentOffline(@NonNull final CollectionReference ref,
                                                  @NonNull final Map<String, Object> data) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                try {
                    ref.add(data);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Adds a new document to this collection with the specified POJO as contents, assigning it a document ID automatically.
     * <p>
     * This method will just call the API `add` method without wait for any complete listener. This is made in this way because the
     * listeners required connection for this kind of operations.
     *
     * @param ref  The given Collection reference.
     * @param pojo The POJO that will be used to populate the contents of the document.
     * @return a Single which emits the {@link DocumentReference} of the added Document.
     */
    @NonNull
    public static Completable addDocumentOffline(@NonNull final CollectionReference ref,
                                                  @NonNull final Object pojo) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                try {
                    ref.add(pojo);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }


    /**
     * Updates fields in the document referred to by this DocumentReference. If no document exists yet, the update will fail.
     *
     * @param ref             The given Document reference.
     * @param updateFieldsMap A map of field / value pairs to update. Fields can contain dots to reference nested fields within the document.
     */
    @NonNull
    public static Completable updateDocument(@NonNull final DocumentReference ref,
                                             @NonNull final Map<String, Object> updateFieldsMap) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, ref.update(updateFieldsMap));
            }
        });
    }

    /**
     * Updates fields in the document referred to by this DocumentReference. If no document exists yet, the update will fail.
     * <p>
     * This method will just call the API `update` method without wait for any complete listener. This is made in this way because the
     * listeners required connection for this kind of operations.
     *
     * @param ref             The given Document reference.
     * @param updateFieldsMap A map of field / value pairs to update. Fields can contain dots to reference nested fields within the document.
     */
    @NonNull
    public static Completable updateDocumentOffline(@NonNull final DocumentReference ref,
                                                    @NonNull final Map<String, Object> updateFieldsMap) {
        ref.update(updateFieldsMap);
        return RxFirestoreOfflineHandler.listenOfflineListener(ref);
    }

    /**
     * Updates fields in the document referred to by this DocumentReference. If no document exists yet, the update will fail.
     *
     * @param ref                 The given Document reference.
     * @param field               The first field to update. Fields can contain dots to reference a nested field within the document.
     * @param value               The first value
     * @param moreFieldsAndValues Additional field/value pairs.
     */
    @NonNull
    public static Completable updateDocument(@NonNull final DocumentReference ref,
                                             @NonNull final String field,
                                             final Object value,
                                             final Object... moreFieldsAndValues) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, ref.update(field, value, moreFieldsAndValues));
            }
        });
    }

    /**
     * Updates fields in the document referred to by this DocumentReference. If no document exists yet, the update will fail.
     * <p>
     * This method will just call the API `update` method without wait for any complete listener. This is made in this way because the
     * listeners required connection for this kind of operations.
     *
     * @param ref                 The given Document reference.
     * @param field               The first field to update. Fields can contain dots to reference a nested field within the document.
     * @param value               The first value
     * @param moreFieldsAndValues Additional field/value pairs.
     */
    @NonNull
    public static Completable updateDocumentOffline(@NonNull final DocumentReference ref,
                                                    @NonNull final String field,
                                                    final Object value,
                                                    final Object... moreFieldsAndValues) {
        ref.update(field, value, moreFieldsAndValues);
        return RxFirestoreOfflineHandler.listenOfflineListener(ref);
    }


    /**
     * Updates fields in the document referred to by this DocumentReference. If no document exists yet, the update will fail.
     *
     * @param ref                 The given Document reference.
     * @param fieldPath           The first field to update. Fields can contain dots to reference a nested field within the document.
     * @param value               The first value
     * @param moreFieldsAndValues Additional field/value pairs.
     */
    @NonNull
    public static Completable updateDocument(@NonNull final DocumentReference ref,
                                             @NonNull final FieldPath fieldPath,
                                             final Object value,
                                             final Object... moreFieldsAndValues) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, ref.update(fieldPath, value, moreFieldsAndValues));
            }
        });
    }

    /**
     * Updates fields in the document referred to by this DocumentReference. If no document exists yet, the update will fail.
     * <p>
     * This method will just call the API `update` method without wait for any complete listener. This is made in this way because the
     * listeners required connection for this kind of operations.
     *
     * @param ref                 The given Document reference.
     * @param fieldPath           The first field to update. Fields can contain dots to reference a nested field within the document.
     * @param value               The first value
     * @param moreFieldsAndValues Additional field/value pairs.
     */
    @NonNull
    public static Completable updateDocumentOffline(@NonNull final DocumentReference ref,
                                                    @NonNull final FieldPath fieldPath,
                                                    final Object value,
                                                    final Object... moreFieldsAndValues) {
        ref.update(fieldPath, value, moreFieldsAndValues);
        return RxFirestoreOfflineHandler.listenOfflineListener(ref);
    }

    /**
     * Overwrites the document referred to by this DocumentReference. If the document does not yet exist, it will be created. If a document already exists, it will be overwritten.
     *
     * @param ref          The given Document reference.
     * @param setFieldsMap A map of the fields and values for the document.
     * @param options      An object to configure the set behavior.
     */
    @NonNull
    public static Completable setDocument(@NonNull final DocumentReference ref,
                                          @NonNull final Map<String, Object> setFieldsMap,
                                          @NonNull final SetOptions options) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, ref.set(setFieldsMap, options));
            }
        });
    }

    /**
     * Overwrites the document referred to by this DocumentReference. If the document does not yet exist, it will be created. If a document already exists, it will be overwritten.
     *
     * @param ref     The given Document reference.
     * @param pojo    The POJO that will be used to populate the document contents.
     * @param options An object to configure the set behavior.
     */
    @NonNull
    public static Completable setDocument(@NonNull final DocumentReference ref,
                                          @NonNull final Object pojo,
                                          @NonNull final SetOptions options) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, ref.set(pojo, options));
            }
        });
    }

    /**
     * Overwrites the document referred to by this DocumentReference. If the document does not yet exist, it will be created. If a document already exists, it will be overwritten.
     * <p>
     * This method will just call the API `set` method without wait for any complete listener. This is made in this way because the
     * listeners required connection for this kind of operations.
     *
     * @param ref     The given Document reference.
     * @param pojo    The POJO that will be used to populate the document contents.
     * @param options An object to configure the set behavior.
     */
    @NonNull
    public static Completable setDocumentOffline(@NonNull final DocumentReference ref,
                                                 @NonNull final Object pojo,
                                                 @NonNull final SetOptions options) {
        ref.set(pojo, options);
        return RxFirestoreOfflineHandler.listenOfflineListener(ref);
    }

    /**
     * Overwrites the document referred to by this DocumentReference. If the document does not yet exist, it will be created. If a document already exists, it will be overwritten.
     *
     * @param ref          The given Document reference.
     * @param setFieldsMap A map of the fields and values for the document.
     */
    @NonNull
    public static Completable setDocument(@NonNull final DocumentReference ref,
                                          @NonNull final Map<String, Object> setFieldsMap) {
        return setDocument(ref, setFieldsMap, SetOptions.merge());
    }

    /**
     * Overwrites the document referred to by this DocumentReference. If the document does not yet exist, it will be created. If a document already exists, it will be overwritten.
     * <p>
     * This method will just call the API `set` method without wait for any complete listener. This is made in this way because the
     * listeners required connection for this kind of operations.
     *
     * @param ref          The given Document reference.
     * @param setFieldsMap A map of the fields and values for the document.
     */
    @NonNull
    public static Completable setDocumentOffline(@NonNull final DocumentReference ref,
                                                 @NonNull final Map<String, Object> setFieldsMap) {
        ref.set(setFieldsMap);
        return RxFirestoreOfflineHandler.listenOfflineListener(ref);
    }

    /**
     * Overwrites the document referred to by this DocumentReference. If the document does not yet exist, it will be created. If a document already exists, it will be overwritten.
     *
     * @param ref  The given Document reference.
     * @param pojo The POJO that will be used to populate the document contents.
     */
    @NonNull
    public static Completable setDocument(@NonNull final DocumentReference ref,
                                          @NonNull final Object pojo) {
        return setDocument(ref, pojo, SetOptions.merge());
    }

    /**
     * Overwrites the document referred to by this DocumentReference. If the document does not yet exist, it will be created. If a document already exists, it will be overwritten.
     * <p>
     * This method will just call the API `set` method without wait for any complete listener. This is made in this way because the
     * listeners required connection for this kind of operations.
     *
     * @param ref  The given Document reference.
     * @param pojo The POJO that will be used to populate the document contents.
     */
    @NonNull
    public static Completable setDocumentOffline(@NonNull final DocumentReference ref,
                                                 @NonNull final Object pojo) {
        ref.set(pojo);
        return RxFirestoreOfflineHandler.listenOfflineListener(ref);
    }

    /**
     * Deletes the document referred to by this DocumentReference.
     *
     * @param ref The given Document reference.
     */
    @NonNull
    public static Completable deleteDocument(@NonNull final DocumentReference ref) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                RxCompletableHandler.assignOnTask(emitter, ref.delete());
            }
        });
    }

    /**
     * Deletes the document referred to by this DocumentReference.
     *
     * @param ref The given Document reference.
     */
    @NonNull
    public static Completable deleteDocumentOffline(@NonNull final DocumentReference ref) {
        ref.delete();
        return RxFirestoreOfflineHandler.listenOfflineListener(ref);
    }

    /**
     * Reads the document referenced by this DocumentReference.
     *
     * @param ref The given Document reference.
     */
    @NonNull
    public static Maybe<DocumentSnapshot> getDocument(@NonNull final DocumentReference ref) {
        return Maybe.create(new MaybeOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final MaybeEmitter<DocumentSnapshot> emitter) {
                ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            emitter.onSuccess(documentSnapshot);
                        } else {
                            emitter.onComplete();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!emitter.isDisposed())
                            emitter.onError(e);
                    }
                });
            }
        });
    }

    /**
     * Reads the collection referenced by this DocumentReference
     *
     * @param ref The given Collection reference.
     */
    @NonNull
    public static Maybe<QuerySnapshot> getCollection(@NonNull final CollectionReference ref) {
        return Maybe.create(new MaybeOnSubscribe<QuerySnapshot>() {
            @Override
            public void subscribe(final MaybeEmitter<QuerySnapshot> emitter) throws Exception {
                ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            emitter.onComplete();
                        } else {
                            emitter.onSuccess(documentSnapshots);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!emitter.isDisposed())
                            emitter.onError(e);
                    }
                });
            }
        });
    }


    /**
     * Reads the collection referenced by this DocumentReference
     *
     * @param query The given Collection query.
     */
    @NonNull
    public static Maybe<QuerySnapshot> getCollection(@NonNull final Query query) {
        return Maybe.create(new MaybeOnSubscribe<QuerySnapshot>() {
            @Override
            public void subscribe(final MaybeEmitter<QuerySnapshot> emitter) {
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            emitter.onComplete();
                        } else {
                            emitter.onSuccess(documentSnapshots);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!emitter.isDisposed())
                            emitter.onError(e);
                    }
                });
            }
        });
    }

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref             The given Document reference.
     * @param metadataChanges Listen for metadata changes
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final MetadataChanges metadataChanges,
                                                                @NonNull BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
                final ListenerRegistration registration = ref.addSnapshotListener(metadataChanges, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (e != null && !emitter.isCancelled()) {
                            emitter.onError(e);
                            return;
                        }
                        emitter.onNext(documentSnapshot);
                    }
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        registration.remove();
                    }
                });
            }
        }, strategy);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref             The given Document reference.
     * @param executor        The executor to use to call the listener.
     * @param metadataChanges Listen for metadata changes
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Executor executor,
                                                                @NonNull final MetadataChanges metadataChanges,
                                                                @NonNull BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
                final ListenerRegistration registration = ref.addSnapshotListener(executor, metadataChanges, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (e != null && !emitter.isCancelled()) {
                            emitter.onError(e);
                            return;
                        }
                        emitter.onNext(documentSnapshot);
                    }
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        registration.remove();
                    }
                });
            }
        }, strategy);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref             The given Document reference.
     * @param activity        The activity to scope the listener to.
     * @param metadataChanges Listen for metadata changes
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Activity activity,
                                                                @NonNull final MetadataChanges metadataChanges,
                                                                @NonNull BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
                final ListenerRegistration registration = ref.addSnapshotListener(activity, metadataChanges, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (e != null && !emitter.isCancelled()) {
                            emitter.onError(e);
                            return;
                        }
                        emitter.onNext(documentSnapshot);
                    }
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        registration.remove();
                    }
                });
            }
        }, strategy);
    }

    /**
     * Starts listening to the document referenced by this Query with the given options.
     *
     * @param ref             The given Query reference.
     * @param metadataChanges Listen for metadata changes
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref,
                                                          @NonNull final MetadataChanges metadataChanges,
                                                          @NonNull BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<QuerySnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<QuerySnapshot> emitter) throws Exception {
                final ListenerRegistration registration = ref.addSnapshotListener(metadataChanges, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        if (e != null && !emitter.isCancelled()) {
                            emitter.onError(e);
                            return;
                        }
                        emitter.onNext(querySnapshot);
                    }
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        registration.remove();
                    }
                });
            }
        }, strategy);
    }

    /**
     * Starts listening to the document referenced by this Query with the given options.
     *
     * @param ref             The given Query reference.
     * @param executor        The executor to use to call the listener.
     * @param metadataChanges Listen for metadata changes
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref,
                                                          @NonNull final Executor executor,
                                                          @NonNull final MetadataChanges metadataChanges,
                                                          @NonNull BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<QuerySnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<QuerySnapshot> emitter) {
                final ListenerRegistration registration = ref.addSnapshotListener(executor, metadataChanges, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (e != null && !emitter.isCancelled()) {
                            emitter.onError(e);
                            return;
                        }
                        emitter.onNext(documentSnapshot);
                    }
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() {
                        registration.remove();
                    }
                });
            }
        }, strategy);
    }

    /**
     * Starts listening to the document referenced by this Query using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref             The given Query reference.
     * @param activity        The activity to scope the listener to.
     * @param metadataChanges Listen for metadata changes
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref,
                                                          @NonNull final Activity activity,
                                                          @NonNull final MetadataChanges metadataChanges,
                                                          @NonNull BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<QuerySnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<QuerySnapshot> emitter) {
                final ListenerRegistration registration = ref.addSnapshotListener(activity, metadataChanges, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (e != null && !emitter.isCancelled()) {
                            emitter.onError(e);
                            return;
                        }
                        emitter.onNext(documentSnapshot);
                    }
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() {
                        registration.remove();
                    }
                });
            }
        }, strategy);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference.
     *
     * @param ref The given Document reference.
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref) {
        return observeDocumentRef(ref, MetadataChanges.EXCLUDE, BackpressureStrategy.DROP);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference.
     *
     * @param ref      The given Document reference.
     * @param executor The executor to use to call the listener.
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Executor executor) {
        return observeDocumentRef(ref, executor, MetadataChanges.EXCLUDE, BackpressureStrategy.DROP);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref      The given Document reference.
     * @param activity The activity to scope the listener to.
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Activity activity) {
        return observeDocumentRef(ref, activity, MetadataChanges.EXCLUDE, BackpressureStrategy.DROP);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference.
     *
     * @param ref      The given Document reference.
     * @param executor The executor to use to call the listener.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Executor executor,
                                                                @NonNull BackpressureStrategy strategy) {
        return observeDocumentRef(ref, executor, MetadataChanges.EXCLUDE, strategy);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref      The given Document reference.
     * @param activity The activity to scope the listener to.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Activity activity,
                                                                @NonNull BackpressureStrategy strategy) {
        return observeDocumentRef(ref, activity, MetadataChanges.EXCLUDE, strategy);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref             The given Document reference.
     * @param executor        The executor to use to call the listener.
     * @param metadataChanges Listen for metadata changes
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Executor executor,
                                                                @NonNull final MetadataChanges metadataChanges) {
        return observeDocumentRef(ref, executor, metadataChanges, BackpressureStrategy.DROP);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref             The given Document reference.
     * @param activity        The activity to scope the listener to.
     * @param metadataChanges Listen for metadata changes
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Activity activity,
                                                                @NonNull final MetadataChanges metadataChanges) {
        return observeDocumentRef(ref, activity, metadataChanges, BackpressureStrategy.DROP);
    }

    @NonNull
    public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                     @NonNull final Class<T> clazz) {
        return observeDocumentRef(ref, DocumentSnapshotMapper.of(clazz));
    }

    /**
     * Starts listening to the document referenced by this DocumentReference.
     *
     * @param ref    The given Document reference.
     * @param mapper specific function to map the dispatched events.
     */
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

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref      The given Document reference.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param mapper   specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                     @NonNull BackpressureStrategy strategy,
                                                     @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
        return observeDocumentRef(ref, MetadataChanges.EXCLUDE, strategy)
            .filter(DOCUMENT_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref      The given Document reference.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param executor The executor to use to call the listener.
     * @param mapper   specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                     @NonNull BackpressureStrategy strategy,
                                                     @NonNull final Executor executor,
                                                     @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
        return observeDocumentRef(ref, executor, MetadataChanges.EXCLUDE, strategy)
            .filter(DOCUMENT_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref      The given Document reference.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param activity The activity to scope the listener to.
     * @param mapper   specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                     @NonNull BackpressureStrategy strategy,
                                                     @NonNull final Activity activity,
                                                     @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
        return observeDocumentRef(ref, activity, MetadataChanges.EXCLUDE, strategy)
            .filter(DOCUMENT_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref             The given Document reference.
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param executor        The executor to use to call the listener.
     * @param metadataChanges Listen for metadata changes
     * @param mapper          specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                     @NonNull BackpressureStrategy strategy,
                                                     @NonNull final Executor executor,
                                                     @NonNull final MetadataChanges metadataChanges,
                                                     @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
        return observeDocumentRef(ref, executor, metadataChanges, strategy)
            .filter(DOCUMENT_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref             The given Document reference.
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param activity        The activity to scope the listener to.
     * @param metadataChanges Listen for metadata changes
     * @param mapper          specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                     @NonNull BackpressureStrategy strategy,
                                                     @NonNull final Activity activity,
                                                     @NonNull final MetadataChanges metadataChanges,
                                                     @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
        return observeDocumentRef(ref, activity, metadataChanges, strategy)
            .filter(DOCUMENT_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this Query.
     *
     * @param ref The given Query reference.
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref) {
        return observeQueryRef(ref, MetadataChanges.EXCLUDE, BackpressureStrategy.DROP);
    }

    /**
     * Starts listening to the document referenced by this Query.
     *
     * @param ref      The given Query reference.
     * @param executor The executor to use to call the listener.
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref,
                                                          @NonNull final Executor executor) {
        return observeQueryRef(ref, executor, MetadataChanges.EXCLUDE, BackpressureStrategy.DROP);
    }

    /**
     * Starts listening to the document referenced by this Query using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref      The given Query reference.
     * @param activity The activity to scope the listener to.
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref,
                                                          @NonNull final Activity activity) {
        return observeQueryRef(ref, activity, MetadataChanges.EXCLUDE, BackpressureStrategy.DROP);
    }

    /**
     * Starts listening to the document referenced by this Query.
     *
     * @param ref      The given Query reference.
     * @param executor The executor to use to call the listener.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref,
                                                          @NonNull final Executor executor,
                                                          @NonNull BackpressureStrategy strategy) {
        return observeQueryRef(ref, executor, MetadataChanges.EXCLUDE, strategy);
    }

    /**
     * Starts listening to the document referenced by this Query using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref      The given Query reference.
     * @param activity The activity to scope the listener to.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref,
                                                          @NonNull final Activity activity,
                                                          @NonNull BackpressureStrategy strategy) {
        return observeQueryRef(ref, activity, MetadataChanges.EXCLUDE, strategy);
    }

    /**
     * Starts listening to the document referenced by this Query with the given options.
     *
     * @param ref             The given Query reference.
     * @param executor        The executor to use to call the listener.
     * @param metadataChanges Listen for metadata changes
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref,
                                                          @NonNull final Executor executor,
                                                          @NonNull final MetadataChanges metadataChanges) {
        return observeQueryRef(ref, executor, metadataChanges, BackpressureStrategy.DROP);
    }

    /**
     * Starts listening to the document referenced by this Query using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref             The given Query reference.
     * @param activity        The activity to scope the listener to.
     * @param metadataChanges Listen for metadata changes
     */
    @NonNull
    public static Flowable<QuerySnapshot> observeQueryRef(@NonNull final Query ref,
                                                          @NonNull final Activity activity,
                                                          @NonNull final MetadataChanges metadataChanges) {
        return observeQueryRef(ref, activity, metadataChanges, BackpressureStrategy.DROP);
    }

    @NonNull
    public static <T> Flowable<List<T>> observeQueryRef(@NonNull final Query ref,
                                                        @NonNull final Class<T> clazz) {
        return observeQueryRef(ref, DocumentSnapshotMapper.listOf(clazz));
    }

    /**
     * Starts listening to the document referenced by this Query.
     *
     * @param ref    The given Query reference.
     * @param mapper specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<List<T>> observeQueryRef(@NonNull final Query ref,
                                                        @NonNull final Function<? super QuerySnapshot, ? extends List<T>> mapper) {
        return observeQueryRef(ref)
            .filter(QUERY_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    @NonNull
    public static <T> Flowable<List<T>> observeQueryRef(@NonNull final Query ref,
                                                        @NonNull BackpressureStrategy strategy,
                                                        @NonNull final Class<T> clazz) {
        return observeQueryRef(ref, strategy, DocumentSnapshotMapper.listOf(clazz));
    }

    /**
     * Starts listening to the document referenced by this Query with the given options.
     *
     * @param ref      The given Query reference.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param mapper   specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<List<T>> observeQueryRef(@NonNull final Query ref,
                                                        @NonNull BackpressureStrategy strategy,
                                                        @NonNull final Function<? super QuerySnapshot, ? extends List<T>> mapper) {
        return observeQueryRef(ref, MetadataChanges.EXCLUDE, strategy)
            .filter(QUERY_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this Query with the given options.
     *
     * @param ref      The given Query reference.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param executor The executor to use to call the listener.
     * @param mapper   specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeQueryRef(@NonNull final Query ref,
                                                  @NonNull BackpressureStrategy strategy,
                                                  @NonNull final Executor executor,
                                                  @NonNull final Function<? super QuerySnapshot, ? extends T> mapper) {
        return observeQueryRef(ref, executor, MetadataChanges.EXCLUDE, strategy)
            .filter(QUERY_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this Query with the given options.
     *
     * @param ref      The given Query reference.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param activity The activity to scope the listener to.
     * @param mapper   specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeQueryRef(@NonNull final Query ref,
                                                  @NonNull BackpressureStrategy strategy,
                                                  @NonNull final Activity activity,
                                                  @NonNull final Function<? super QuerySnapshot, ? extends T> mapper) {
        return observeQueryRef(ref, activity, MetadataChanges.EXCLUDE, strategy)
            .filter(QUERY_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this Query with the given options.
     *
     * @param ref             The given Query reference.
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param executor        The executor to use to call the listener.
     * @param metadataChanges Listen for metadata changes
     * @param mapper          specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeQueryRef(@NonNull final Query ref,
                                                  @NonNull BackpressureStrategy strategy,
                                                  @NonNull final Executor executor,
                                                  @NonNull final MetadataChanges metadataChanges,
                                                  @NonNull final Function<? super QuerySnapshot, ? extends T> mapper) {
        return observeQueryRef(ref, executor, metadataChanges, strategy)
            .filter(QUERY_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this Query using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref             The given Query reference.
     * @param strategy        {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param activity        The activity to scope the listener to.
     * @param metadataChanges Listen for metadata changes
     * @param mapper          specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeQueryRef(@NonNull final Query ref,
                                                  @NonNull BackpressureStrategy strategy,
                                                  @NonNull final Activity activity,
                                                  @NonNull final MetadataChanges metadataChanges,
                                                  @NonNull final Function<? super QuerySnapshot, ? extends T> mapper) {
        return observeQueryRef(ref, activity, metadataChanges, strategy)
            .filter(QUERY_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Reads the collection referenced by this CollectionReference.
     *
     * @param ref   The given Collection reference.
     * @param clazz class type for the {@link DocumentSnapshot} items.
     */
    @NonNull
    public static <T> Maybe<List<T>> getCollection(@NonNull final CollectionReference ref,
                                                   @NonNull final Class<T> clazz) {
        return getCollection(ref, DocumentSnapshotMapper.listOf(clazz));
    }

    /**
     * SReads the collection referenced by this CollectionReference.
     *
     * @param ref    The given Collection reference.
     * @param mapper specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Maybe<List<T>> getCollection(CollectionReference ref,
                                                    DocumentSnapshotMapper<QuerySnapshot,
                                                        List<T>> mapper) {
        return getCollection(ref)
            .filter(QUERY_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Reads the collection referenced by this Query.
     *
     * @param query The given Collection query.
     * @param clazz class type for the {@link DocumentSnapshot} items.
     */
    @NonNull
    public static <T> Maybe<List<T>> getCollection(@NonNull final Query query,
                                                   @NonNull final Class<T> clazz) {
        return getCollection(query, DocumentSnapshotMapper.listOf(clazz));
    }

    /**
     * Reads the collection referenced by this Query.
     *
     * @param query  The given Collection query.
     * @param mapper specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Maybe<List<T>> getCollection(@NonNull Query query,
                                                    @NonNull DocumentSnapshotMapper<QuerySnapshot,
                                                        List<T>> mapper) {
        return getCollection(query)
            .filter(QUERY_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Reads the document referenced by this DocumentReference.
     *
     * @param ref   The given Document reference.
     * @param clazz class type for the {@link DocumentSnapshot} items.
     */
    @NonNull
    public static <T> Maybe<T> getDocument(@NonNull final DocumentReference ref,
                                           @NonNull final Class<T> clazz) {
        return getDocument(ref, DocumentSnapshotMapper.of(clazz));
    }

    /**
     * Reads the document referenced by this DocumentReference.
     *
     * @param ref    The given Document reference.
     * @param mapper specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Maybe<T> getDocument(@NonNull final DocumentReference ref,
                                           @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
        return getDocument(ref)
            .filter(DOCUMENT_EXISTENCE_PREDICATE)
            .map(mapper);
    }
}
