package durdinapps.rxfirebase2;


import android.app.DownloadManager;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import durdinapps.rxfirebase2.exceptions.RxFirebaseDataException;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static durdinapps.rxfirebase2.RxTestUtil.ANY_KEY;
import static durdinapps.rxfirebase2.RxTestUtil.PREVIOUS_CHILD_NAME;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RxFirebaseDatabaseTest {

    @Mock
    private DatabaseReference databaseReference;
    @Mock
    private DatabaseReference databaseReferenceTwo;

    @Mock
    private Query query;

    @Mock
    private DataSnapshot dataSnapshot;
    @Mock
    private DataSnapshot dataSnapshotTwo;

    @Mock
    private Task<Void> voidTask;

    private ChildData childData = new ChildData();
    private List<ChildData> childDataList = new ArrayList<>();
    private Map<String, ChildData> childDataMap = new HashMap<>();
    private Map<String, Object> updatedData = new HashMap<>();

    private RxFirebaseChildEvent<ChildData> childEventAdded;
    private RxFirebaseChildEvent<ChildData> childEventChanged;
    private RxFirebaseChildEvent<ChildData> childEventRemoved;
    private RxFirebaseChildEvent<ChildData> childEventMoved;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        childDataList.add(childData);
        childDataMap.put(ANY_KEY, childData);
        updatedData.put(databaseReference.toString(), childData);

        childEventAdded = new RxFirebaseChildEvent<>(ANY_KEY, childData, PREVIOUS_CHILD_NAME, RxFirebaseChildEvent.EventType.ADDED);
        childEventChanged = new RxFirebaseChildEvent<>(ANY_KEY, childData, PREVIOUS_CHILD_NAME, RxFirebaseChildEvent.EventType.CHANGED);
        childEventRemoved = new RxFirebaseChildEvent<>(ANY_KEY, childData, RxFirebaseChildEvent.EventType.REMOVED);
        childEventMoved = new RxFirebaseChildEvent<>(ANY_KEY, childData, PREVIOUS_CHILD_NAME, RxFirebaseChildEvent.EventType.MOVED);

        when(dataSnapshot.exists()).thenReturn(true);
        when(dataSnapshot.getValue(ChildData.class)).thenReturn(childData);
        when(dataSnapshot.getKey()).thenReturn(ANY_KEY);
        when(dataSnapshot.getChildren()).thenReturn(Arrays.asList(dataSnapshot));

        when(dataSnapshotTwo.exists()).thenReturn(true);
        when(dataSnapshotTwo.getValue(ChildData.class)).thenReturn(childData);
        when(dataSnapshotTwo.getKey()).thenReturn(ANY_KEY);
        when(dataSnapshotTwo.getChildren()).thenReturn(Arrays.asList(dataSnapshotTwo));

        when(databaseReference.updateChildren(updatedData)).thenReturn(voidTask);
    }

    @Test
    public void testObserveSingleValue() throws InterruptedException {
        TestObserver<ChildData> testObserver = RxFirebaseDatabase
            .observeSingleValueEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(dataSnapshot);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childData))
            .assertComplete()
            .dispose();
    }

    @Test
    public void testObserveSingleNoData() throws InterruptedException {

        DataSnapshot mockFirebaseDataSnapshotNoData = mock(DataSnapshot.class);
        when(mockFirebaseDataSnapshotNoData.exists()).thenReturn(false);

        TestObserver<ChildData> testObserver = RxFirebaseDatabase
            .observeSingleValueEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(mockFirebaseDataSnapshotNoData);

        testObserver.assertValueCount(0)
            .assertComplete()
            .dispose();
    }

    @Test
    public void testObserveSingleWrongType() throws InterruptedException {

        TestObserver<WrongType> testObserver = RxFirebaseDatabase
            .observeSingleValueEvent(databaseReference, WrongType.class)
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(dataSnapshot);

        testObserver.assertError(RuntimeException.class)
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testObserveSingleValue_Disconnected() throws InterruptedException {

        TestObserver<ChildData> testObserver = RxFirebaseDatabase
            .observeSingleValueEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onCancelled(DatabaseError.zza(DatabaseError.DISCONNECTED));

        testObserver.assertError(RxFirebaseDataException.class)
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testObserveSingleValueEventFailed() throws InterruptedException {

        TestObserver<ChildData> testObserver = RxFirebaseDatabase
            .observeSingleValueEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onCancelled(DatabaseError.zza(DatabaseError.OPERATION_FAILED));

        testObserver.assertError(RxFirebaseDataException.class)
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testObserveValueEvent() throws InterruptedException {

        TestSubscriber<ChildData> testObserver = RxFirebaseDatabase
            .observeValueEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addValueEventListener(argument.capture());
        argument.getValue().onDataChange(dataSnapshot);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childData))
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testMultipleSingleValueEvent() throws InterruptedException {

        TestSubscriber<DataSnapshot> testObserver = RxFirebaseDatabase
            .observeMultipleSingleValueEvent(databaseReference, databaseReferenceTwo)
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(dataSnapshot);
        verify(databaseReferenceTwo).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(dataSnapshotTwo);

        testObserver.assertNoErrors()
            .assertValueCount(2)
            .assertComplete()
            .dispose();
    }

    @Test
    public void testSingleValueEvent() throws InterruptedException {


        TestObserver<ChildData> testObserver = RxFirebaseDatabase
            .observeSingleValueEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(dataSnapshot);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childData))
            .assertComplete()
            .dispose();
    }

    @Test
    public void testObserveValueEventList() throws InterruptedException {

        TestObserver<ChildData> testObserver = RxFirebaseDatabase
            .observeSingleValueEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(dataSnapshot);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertComplete()
            .dispose();
    }

    @Test
    public void testObserveValuesMap() throws InterruptedException {
        TestObserver<Map<String, ChildData>> testObserver = RxFirebaseDatabase
            .observeSingleValueEvent(databaseReference)
            .map(new Function<DataSnapshot, Map<String, ChildData>>() {
                @Override
                public LinkedHashMap<String, ChildData> apply(DataSnapshot dataSnapshot) throws Exception {
                    LinkedHashMap<String, ChildData> map = new LinkedHashMap<>();
                    map.put(dataSnapshot.getKey(), dataSnapshot.getValue(ChildData.class));
                    return map;
                }
            }).test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(databaseReference).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(dataSnapshot);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childDataMap))
            .dispose();
    }

    @Test
    public void testObserveChildEventAdded() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<ChildData>> testObserver = RxFirebaseDatabase
            .observeChildEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(databaseReference).addChildEventListener(argument.capture());
        argument.getValue().onChildAdded(dataSnapshot, PREVIOUS_CHILD_NAME);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childEventAdded))
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testObserveChildEventChanged() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<ChildData>> testObserver = RxFirebaseDatabase
            .observeChildEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(databaseReference).addChildEventListener(argument.capture());
        argument.getValue().onChildChanged(dataSnapshot, PREVIOUS_CHILD_NAME);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childEventChanged))
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testObserveChildEventRemoved() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<ChildData>> testObserver = RxFirebaseDatabase
            .observeChildEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(databaseReference).addChildEventListener(argument.capture());
        argument.getValue().onChildRemoved(dataSnapshot);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childEventRemoved))
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testObserveChildEventMoved() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<ChildData>> testObserver = RxFirebaseDatabase
            .observeChildEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(databaseReference).addChildEventListener(argument.capture());
        argument.getValue().onChildMoved(dataSnapshot, PREVIOUS_CHILD_NAME);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childEventMoved))
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testObserveChildEventCancelled() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<ChildData>> testObserver = RxFirebaseDatabase
            .observeChildEvent(databaseReference, ChildData.class)
            .test();

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(databaseReference).addChildEventListener(argument.capture());
        argument.getValue().onCancelled(DatabaseError.zza(DatabaseError.DISCONNECTED));

        testObserver.assertError(RxFirebaseDataException.class)
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testObserveListWithDataSnapshotMapper() {
        TestSubscriber<List<ChildData>> testObserver = RxFirebaseDatabase
            .observeValueEvent(query, DataSnapshotMapper.listOf(ChildData.class))
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(query).addValueEventListener(argument.capture());
        argument.getValue().onDataChange(dataSnapshot);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childDataList))
            .assertNotComplete()
            .dispose();
    }

    @Test
    public void testObserveListWithDataSnapshotCustomMapper() throws Exception {
        //noinspection unchecked
        Function<DataSnapshot, ChildData> mapper = (Function<DataSnapshot, ChildData>) mock(Function.class);
        doReturn(childData).when(mapper).apply(eq(dataSnapshot));

        TestSubscriber<List<ChildData>> testObserver = RxFirebaseDatabase
            .observeValueEvent(query, DataSnapshotMapper.listOf(ChildData.class, mapper))
            .test();

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(query).addValueEventListener(argument.capture());
        argument.getValue().onDataChange(dataSnapshot);

        verify(mapper).apply(dataSnapshot);

        testObserver.assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childDataList))
            .assertNotComplete()
            .dispose();
    }

    class ChildData {
        int id;
        String str;
    }

    class WrongType {
        String somethingWrong;
        long more;
    }
}
