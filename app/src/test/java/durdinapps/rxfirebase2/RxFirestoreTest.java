package durdinapps.rxfirebase2;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.reactivex.observers.TestObserver;

import static durdinapps.rxfirebase2.RxTestUtil.eventSnapshotListener;
import static durdinapps.rxfirebase2.RxTestUtil.setupOfflineTask;
import static durdinapps.rxfirebase2.RxTestUtil.setupTask;
import static durdinapps.rxfirebase2.RxTestUtil.testOnCompleteListener;
import static durdinapps.rxfirebase2.RxTestUtil.testOnSuccessListener;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RxFirestoreTest {

    @Mock
    private Task<Void> mockVoidTask;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private DocumentReference emptyDocumentReference;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private CollectionReference emptyCollectionReference;

    @Mock
    private Query queryReference;

    @Mock
    private Query emptyQueryReference;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private DocumentSnapshot emptyDocumentSnapshot;

    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private QuerySnapshot emptyQuerySnapshot;

    @Mock
    private Task<DocumentSnapshot> emptyDocumentSnapshotTask;

    @Mock
    private Task<DocumentSnapshot> documentSnapshotTask;

    @Mock
    private Task<DocumentReference> documentRefTask;

    @Mock
    private Task<QuerySnapshot> queryResultTask;

    @Mock
    private Task<QuerySnapshot> emptyQueryResultTask;

    @Mock
    private ListenerRegistration registration;


    private HashMap<String, Object> updateMap = new HashMap<>();
    private ChildDocData setData = new ChildDocData();
    private ChildDocData childData = new ChildDocData();
    private List<ChildDocData> childDataList = new ArrayList<>();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        setupTask(documentSnapshotTask);
        setupTask(emptyDocumentSnapshotTask);
        setupTask(queryResultTask);
        setupTask(emptyQueryResultTask);
        setupTask(mockVoidTask);
        setupOfflineTask(documentReference, registration);

        when(documentReference.get()).thenReturn(documentSnapshotTask);
        when(emptyDocumentReference.get()).thenReturn(emptyDocumentSnapshotTask);
        when(collectionReference.get()).thenReturn(queryResultTask);
        when(emptyCollectionReference.get()).thenReturn(emptyQueryResultTask);
        when(queryReference.get()).thenReturn(queryResultTask);
        when(emptyQueryReference.get()).thenReturn(emptyQueryResultTask);
        when(documentReference.delete()).thenReturn(mockVoidTask);
        when(documentReference.update(updateMap)).thenReturn(mockVoidTask);
        when(collectionReference.add(setData)).thenReturn(documentRefTask);
        when(documentSnapshot.toObject(ChildDocData.class)).thenReturn(childData);
        when(documentSnapshot.exists()).thenReturn(true); //This snapshots exist
        when(documentSnapshot.exists()).thenReturn(true); //This snapshots exist
        when(emptyDocumentSnapshot.exists()).thenReturn(false); //This snapshots exist
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.toObjects(ChildDocData.class)).thenReturn(childDataList);
        when(querySnapshot.iterator()).thenAnswer(new Answer<Iterator<DocumentSnapshot>>() {
            @Override
            public Iterator<DocumentSnapshot> answer(InvocationOnMock invocation) {
                return Collections.singletonList(documentSnapshot).iterator();
            }
        });
        when(emptyQuerySnapshot.isEmpty()).thenReturn(true);
    }

    @Test
    public void testGetDocument() {
        TestObserver<DocumentSnapshot> testObserver = RxFirestore
            .getDocument(documentReference)
            .test();

        testOnSuccessListener.getValue().onSuccess(documentSnapshot);

        verify(documentReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(documentSnapshot))
            .assertComplete();
    }

    @Test
    public void testGetEmptyDocument() {
        TestObserver<DocumentSnapshot> testObserver = RxFirestore
            .getDocument(emptyDocumentReference)
            .test();

        testOnSuccessListener.getValue().onSuccess(emptyDocumentSnapshot);

        verify(emptyDocumentReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(0)
            .assertComplete();
    }

    @Test
    public void testMappedGetDocument() {
        TestObserver<ChildDocData> testObserver = RxFirestore
            .getDocument(documentReference, ChildDocData.class)
            .test();

        testOnSuccessListener.getValue().onSuccess(documentSnapshot);

        verify(documentReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(childData))
            .assertComplete();
    }

    @Test
    public void testMappedGetEmptyDocument() {
        TestObserver<ChildDocData> testObserver = RxFirestore
            .getDocument(emptyDocumentReference, ChildDocData.class)
            .test();

        testOnSuccessListener.getValue().onSuccess(emptyDocumentSnapshot);

        verify(emptyDocumentReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(0)
            .assertComplete();
    }

    @Test
    public void testGetCollection() {
        TestObserver<QuerySnapshot> testObserver = RxFirestore
            .getCollection(collectionReference)
            .test();

        testOnSuccessListener.getValue().onSuccess(querySnapshot);

        verify(collectionReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(querySnapshot))
            .assertComplete();
    }

    @Test
    public void testGetEmptyCollection() {
        TestObserver<QuerySnapshot> testObserver = RxFirestore
            .getCollection(emptyCollectionReference)
            .test();

        testOnSuccessListener.getValue().onSuccess(emptyQuerySnapshot);

        verify(emptyCollectionReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(0)
            .assertComplete();
    }

    @Test
    public void testMappedGetCollection() {
        TestObserver<List<ChildDocData>> testObserver = RxFirestore
            .getCollection(collectionReference, ChildDocData.class)
            .test();

        testOnSuccessListener.getValue().onSuccess(querySnapshot);

        verify(collectionReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(1)
            .assertComplete();
    }

    @Test
    public void testMappedGetEmptyCollection() {
        TestObserver<List<ChildDocData>> testObserver = RxFirestore
            .getCollection(emptyCollectionReference, ChildDocData.class)
            .test();

        testOnSuccessListener.getValue().onSuccess(emptyQuerySnapshot);

        verify(emptyCollectionReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(0)
            .assertComplete();
    }

    @Test
    public void testGetQuery() {
        TestObserver<QuerySnapshot> testObserver = RxFirestore
            .getCollection(queryReference)
            .test();

        testOnSuccessListener.getValue().onSuccess(querySnapshot);

        verify(queryReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(1)
            .assertValueSet(Collections.singletonList(querySnapshot))
            .assertComplete();
    }

    @Test
    public void testGetEmptyQuery() {
        TestObserver<QuerySnapshot> testObserver = RxFirestore
            .getCollection(emptyQueryReference)
            .test();

        testOnSuccessListener.getValue().onSuccess(emptyQuerySnapshot);

        verify(emptyQueryReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(0)
            .assertComplete();
    }

    @Test
    public void testMappedGetQuery() {
        TestObserver<List<ChildDocData>> testObserver = RxFirestore
            .getCollection(queryReference, ChildDocData.class)
            .test();

        testOnSuccessListener.getValue().onSuccess(querySnapshot);

        verify(queryReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(1)
            .assertComplete();
    }

    @Test
    public void testMappedGetEmptyQuery() {
        TestObserver<List<ChildDocData>> testObserver = RxFirestore
            .getCollection(emptyQueryReference, ChildDocData.class)
            .test();

        testOnSuccessListener.getValue().onSuccess(emptyQuerySnapshot);

        verify(emptyQueryReference).get();

        testObserver
            .assertNoErrors()
            .assertValueCount(0)
            .assertComplete();
    }

    @Test
    public void testSetDocumentOffline() {
        TestObserver<Void> testObserver = RxFirestore
            .setDocumentOffline(documentReference, setData)
            .test();

        eventSnapshotListener.getValue().onEvent(documentSnapshot, null);

        verify(documentReference).set(setData);

        testObserver
            .assertNoErrors()
            .assertComplete();
    }

    @Test
    public void testUpdateDocument() {

        TestObserver<Void> storageTestObserver =
            RxFirestore.updateDocument(documentReference, updateMap)
                .test();

        testOnCompleteListener.getValue().onComplete(mockVoidTask);

        verify(documentReference).update(updateMap);

        storageTestObserver.assertNoErrors()
            .assertComplete()
            .dispose();
    }


    @Test
    public void testUpdateDocumentOffline() {
        TestObserver<Void> testObserver = RxFirestore
            .updateDocumentOffline(documentReference, updateMap)
            .test();

        eventSnapshotListener.getValue().onEvent(documentSnapshot, null);

        verify(documentReference).update(updateMap);

        testObserver
            .assertNoErrors()
            .assertComplete();
    }


    @Test
    public void testDeleteDocument() {

        TestObserver<Void> storageTestObserver =
            RxFirestore.deleteDocument(documentReference)
                .test();

        testOnCompleteListener.getValue().onComplete(mockVoidTask);

        verify(documentReference).delete();

        storageTestObserver.assertNoErrors()
            .assertComplete()
            .dispose();
    }

    @Test
    public void testDeleteDocumentOffline() {
        TestObserver<Void> testObserver = RxFirestore
            .deleteDocumentOffline(documentReference)
            .test();

        eventSnapshotListener.getValue().onEvent(documentSnapshot, null);
        verify(documentReference).delete();

        testObserver
            .assertNoErrors()
            .assertComplete();
    }


    class ChildDocData {
        int id;
        String str;
    }
}
