package durdinapps.rxfirebase2;

import androidx.annotation.NonNull;

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

/**
 * Basic builder to create Firebase queries based on filters from different {@link DatabaseReference references}.
 */
public class RxFirebaseQuery {
    private Maybe<DatabaseReference[]> whereMaybe;

    private RxFirebaseQuery() {
    }

    /**
     * Retrieve a new instance for {@link RxFirebaseQuery}.
     */
    public static RxFirebaseQuery getInstance() {
        return new RxFirebaseQuery();
    }


    /**
     * Generate a filter based on the method {@link RxFirebaseDatabase#requestFilteredReferenceKeys(DatabaseReference, Query)}.
     *
     * @param from     base reference where you want to retrieve the original references.
     * @param whereRef reference that you use as a filter to create your from references.
     * @return the current instance of {@link RxFirebaseQuery}.
     */
    @NonNull
    public RxFirebaseQuery filterByRefs(@NonNull DatabaseReference from,
                                        @NonNull Query whereRef) {
        whereMaybe = requestFilteredReferenceKeys(from, whereRef);
        return this;
    }

    /**
     * Generate a filter based on a given function.
     *
     * @param whereRef reference that you use as a filter to create your from references.
     * @param mapper   Custom mapper to map the retrieved where references to new {@link DatabaseReference}.
     * @return the current instance of {@link RxFirebaseQuery}.
     */
    @NonNull
    public RxFirebaseQuery filter(@NonNull Query whereRef,
                                  @NonNull final Function<? super DataSnapshot, ? extends DatabaseReference[]> mapper) {
        whereMaybe = observeSingleValueEvent(whereRef, mapper);
        return this;
    }

    /**
     * Retrieve the final result as a {@link Single} which emmit the final result of the event as a {@link List} of {@link DataSnapshot}.
     */
    @NonNull
    public Single<List<DataSnapshot>> asList() {
        return create().toList();
    }

    /**
     * Retrieve the final result as a {@link Single} which emmit the final result of the event as a {@link List} of a given type.
     *
     * @param mapper specific function to map the dispatched events.
     */
    @NonNull
    public <T> Single<List<T>> asList(@NonNull final Function<? super List<DataSnapshot>, ? extends List<T>> mapper) {
        return create().toList().map(mapper);
    }

    /**
     * Retrieve the final result of the query as a {@link Flowable} which emmit mapped values.
     *
     * @param mapper specific function to map the dispatched events.
     */
    @NonNull
    public <T> Flowable<T> create(@NonNull final Function<? super DataSnapshot, ? extends T> mapper) {
        return create().map(mapper);
    }

    /**
     * Retrieve the final result of the query as a {@link Flowable} which emmit {@link DataSnapshot}.
     */
    @NonNull
    public Flowable<DataSnapshot> create() {
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

