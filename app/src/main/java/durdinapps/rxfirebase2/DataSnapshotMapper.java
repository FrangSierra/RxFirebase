package durdinapps.rxfirebase2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import durdinapps.rxfirebase2.exceptions.RxFirebaseDataCastException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public abstract class DataSnapshotMapper<T, U> implements Function<T, U> {

    private DataSnapshotMapper() {
    }

    public static <U> DataSnapshotMapper<DataSnapshot, U> of(Class<U> clazz) {
        return new TypedDataSnapshotMapper<U>(clazz);
    }

    public static <U> DataSnapshotMapper<DataSnapshot, List<U>> listOf(Class<U> clazz) {
        return new TypedListDataSnapshotMapper<>(clazz);
    }

    public static <U> DataSnapshotMapper<DataSnapshot, List<U>> listOf(Class<U> clazz, Function<DataSnapshot, U> mapper) {
        return new TypedListDataSnapshotMapper<>(clazz, mapper);
    }

    public static <U> DataSnapshotMapper<DataSnapshot, LinkedHashMap<String, U>> mapOf(Class<U> clazz) {
        return new TypedMapDataSnapshotMapper<>(clazz);
    }

    public static <U> DataSnapshotMapper<DataSnapshot, U> of(GenericTypeIndicator<U> genericTypeIndicator) {
        return new GenericTypedDataSnapshotMapper<U>(genericTypeIndicator);
    }

    public static <U> DataSnapshotMapper<RxFirebaseChildEvent<DataSnapshot>, RxFirebaseChildEvent<U>> ofChildEvent(Class<U> clazz) {
        return new ChildEventDataSnapshotMapper<U>(clazz);
    }

    private static <U> U getDataSnapshotTypedValue(DataSnapshot dataSnapshot, Class<U> clazz) {
        U value;
        try {
            value = dataSnapshot.getValue(clazz);
        } catch (Exception ex) {
            throw Exceptions.propagate(new RxFirebaseDataCastException(
                "There was a problem trying to cast " + dataSnapshot.toString() + " to " + clazz.getSimpleName(), ex));
        }
        if (value == null) {
            throw Exceptions.propagate(new RxFirebaseDataCastException(
                "The value after cast  " + dataSnapshot.toString() + " to " + clazz.getSimpleName() + "is null."));
        }
        return value;
    }

    private static class TypedDataSnapshotMapper<U> extends DataSnapshotMapper<DataSnapshot, U> {

        private final Class<U> clazz;

        public TypedDataSnapshotMapper(final Class<U> clazz) {
            this.clazz = clazz;
        }

        @Override
        public U apply(final DataSnapshot dataSnapshot) {
            return getDataSnapshotTypedValue(dataSnapshot, clazz);
        }
    }

    private static class TypedListDataSnapshotMapper<U> extends DataSnapshotMapper<DataSnapshot, List<U>> {

        private final Class<U> clazz;
        private final Function<DataSnapshot, U> mapper;

        TypedListDataSnapshotMapper(final Class<U> clazz) {
            this(clazz, null);
        }

        TypedListDataSnapshotMapper(final Class<U> clazz, Function<DataSnapshot, U> mapper) {
            this.clazz = clazz;
            this.mapper = mapper;
        }

        @Override
        public List<U> apply(final DataSnapshot dataSnapshot) throws Exception {
            List<U> items = new ArrayList<>();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                items.add(mapper != null
                    ? mapper.apply(childSnapshot)
                    : getDataSnapshotTypedValue(childSnapshot, clazz));
            }
            return items;
        }
    }

    private static class TypedMapDataSnapshotMapper<U> extends DataSnapshotMapper<DataSnapshot, LinkedHashMap<String, U>> {

        private final Class<U> clazz;

        TypedMapDataSnapshotMapper(final Class<U> clazz) {
            this.clazz = clazz;
        }

        @Override
        public LinkedHashMap<String, U> apply(final DataSnapshot dataSnapshot) {
            LinkedHashMap<String, U> items = new LinkedHashMap<>();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                items.put(childSnapshot.getKey(), getDataSnapshotTypedValue(childSnapshot, clazz));
            }
            return items;
        }
    }

    private static class GenericTypedDataSnapshotMapper<U> extends DataSnapshotMapper<DataSnapshot, U> {

        private final GenericTypeIndicator<U> genericTypeIndicator;

        public GenericTypedDataSnapshotMapper(GenericTypeIndicator<U> genericTypeIndicator) {
            this.genericTypeIndicator = genericTypeIndicator;
        }

        @Override
        public U apply(DataSnapshot dataSnapshot) {
            U value = dataSnapshot.getValue(genericTypeIndicator);
            if (value == null) {
                throw Exceptions.propagate(new RxFirebaseDataCastException(
                    "unable to cast firebase data response to generic type"));
            }
            return value;
        }
    }

    private static class ChildEventDataSnapshotMapper<U>
        extends DataSnapshotMapper<RxFirebaseChildEvent<DataSnapshot>, RxFirebaseChildEvent<U>> {

        private final Class<U> clazz;

        public ChildEventDataSnapshotMapper(final Class<U> clazz) {
            this.clazz = clazz;
        }

        @Override
        public RxFirebaseChildEvent<U> apply(final RxFirebaseChildEvent<DataSnapshot> rxFirebaseChildEvent) {
            DataSnapshot dataSnapshot = rxFirebaseChildEvent.getValue();
            if (dataSnapshot.exists()) {
                return new RxFirebaseChildEvent<U>(
                    dataSnapshot.getKey(),
                    getDataSnapshotTypedValue(dataSnapshot, clazz),
                    rxFirebaseChildEvent.getPreviousChildName(),
                    rxFirebaseChildEvent.getEventType());
            } else {
                throw Exceptions.propagate(new RuntimeException("child dataSnapshot doesn't exist"));
            }
        }
    }

    static final Predicate<DataSnapshot> DATA_SNAPSHOT_EXISTENCE_PREDICATE = new Predicate<DataSnapshot>() {
        @Override
        public boolean test(@NonNull DataSnapshot dataSnapshot) throws Exception {
            return dataSnapshot.exists();
        }
    };
}