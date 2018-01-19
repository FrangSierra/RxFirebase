package durdinapps.rxfirebase2;


import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentListenOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

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
            public void subscribe(CompletableEmitter emitter) throws Exception {
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
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, batch.commit());
            }
        });
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
            public void subscribe(final SingleEmitter<DocumentReference> emitter) throws Exception {
                ref.add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
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
            public void subscribe(final SingleEmitter<DocumentReference> emitter) throws Exception {
                ref.add(pojo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
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
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, ref.update(updateFieldsMap));
            }
        });
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
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, ref.update(field, value, moreFieldsAndValues));
            }
        });
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
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, ref.update(fieldPath, value, moreFieldsAndValues));
            }
        });
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
            public void subscribe(CompletableEmitter emitter) throws Exception {
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
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, ref.set(pojo, options));
            }
        });
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
     * Deletes the document referred to by this DocumentReference.
     *
     * @param ref The given Document reference.
     */
    @NonNull
    public static Completable deleteDocument(@NonNull final DocumentReference ref) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, ref.delete());
            }
        });
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
            public void subscribe(final MaybeEmitter<DocumentSnapshot> emitter) throws Exception {
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
            public void subscribe(final MaybeEmitter<QuerySnapshot> emitter) throws Exception {
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
     * @param ref      The given Document reference.
     * @param options  The options to use for this listen.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final DocumentListenOptions options,
                                                                @NonNull BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
                final ListenerRegistration registration = ref.addSnapshotListener(options, new EventListener<DocumentSnapshot>() {
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
     * @param ref      The given Document reference.
     * @param executor The executor to use to call the listener.
     * @param options  The options to use for this listen.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Executor executor,
                                                                @NonNull final DocumentListenOptions options,
                                                                @NonNull BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
                final ListenerRegistration registration = ref.addSnapshotListener(executor, options, new EventListener<DocumentSnapshot>() {
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
     * @param ref      The given Document reference.
     * @param activity The activity to scope the listener to.
     * @param options  The options to use for this listen.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Activity activity,
                                                                @NonNull final DocumentListenOptions options,
                                                                @NonNull BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
                final ListenerRegistration registration = ref.addSnapshotListener(activity, options, new EventListener<DocumentSnapshot>() {
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
     * Starts listening to the document referenced by this DocumentReference.
     *
     * @param ref The given Document reference.
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref) {
        return observeDocumentRef(ref, new DocumentListenOptions(), BackpressureStrategy.DROP);
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
        return observeDocumentRef(ref, executor, new DocumentListenOptions(), BackpressureStrategy.DROP);
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
        return observeDocumentRef(ref, activity, new DocumentListenOptions(), BackpressureStrategy.DROP);
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
        return observeDocumentRef(ref, executor, new DocumentListenOptions(), strategy);
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
        return observeDocumentRef(ref, activity, new DocumentListenOptions(), strategy);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref      The given Document reference.
     * @param executor The executor to use to call the listener.
     * @param options  The options to use for this listen.
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Executor executor,
                                                                @NonNull final DocumentListenOptions options) {
        return observeDocumentRef(ref, executor, options, BackpressureStrategy.DROP);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref      The given Document reference.
     * @param activity The activity to scope the listener to.
     * @param options  The options to use for this listen.
     */
    @NonNull
    public static Flowable<DocumentSnapshot> observeDocumentRef(@NonNull final DocumentReference ref,
                                                                @NonNull final Activity activity,
                                                                @NonNull final DocumentListenOptions options) {
        return observeDocumentRef(ref, activity, options, BackpressureStrategy.DROP);
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
        return observeDocumentRef(ref, new DocumentListenOptions(), strategy)
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
        return observeDocumentRef(ref, executor, new DocumentListenOptions(), strategy)
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
        return observeDocumentRef(ref, activity, new DocumentListenOptions(), strategy)
            .filter(DOCUMENT_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref      The given Document reference.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param executor The executor to use to call the listener.
     * @param options  The options to use for this listen.
     * @param mapper   specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                     @NonNull BackpressureStrategy strategy,
                                                     @NonNull final Executor executor,
                                                     @NonNull final DocumentListenOptions options,
                                                     @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
        return observeDocumentRef(ref, executor, options, strategy)
            .filter(DOCUMENT_EXISTENCE_PREDICATE)
            .map(mapper);
    }

    /**
     * Starts listening to the document referenced by this DocumentReference using an Activity-scoped listener.
     * The listener will be automatically removed during onStop().
     *
     * @param ref      The given Document reference.
     * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
     * @param activity The activity to scope the listener to.
     * @param options  The options to use for this listen.
     * @param mapper   specific function to map the dispatched events.
     */
    @NonNull
    public static <T> Flowable<T> observeDocumentRef(@NonNull final DocumentReference ref,
                                                     @NonNull BackpressureStrategy strategy,
                                                     @NonNull final Activity activity,
                                                     @NonNull final DocumentListenOptions options,
                                                     @NonNull final Function<? super DocumentSnapshot, ? extends T> mapper) {
        return observeDocumentRef(ref, activity, options, strategy)
            .filter(DOCUMENT_EXISTENCE_PREDICATE)
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
    private static <T> Maybe<List<T>> getCollection(CollectionReference ref,
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
    private static <T> Maybe<List<T>> getCollection(@NonNull Query query,
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
