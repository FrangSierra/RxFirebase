package durdinapps.rxfirebase2;


import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;

import io.reactivex.observers.TestObserver;

import static durdinapps.rxfirebase2.RxTestUtil.NULL_FIREBASE_EXCEPTION;
import static durdinapps.rxfirebase2.RxTestUtil.setupTask;
import static durdinapps.rxfirebase2.RxTestUtil.testOnCompleteListener;
import static durdinapps.rxfirebase2.RxTestUtil.testOnFailureListener;
import static durdinapps.rxfirebase2.RxTestUtil.testOnSuccessListener;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RxFirebaseStorageTest {

    @Mock
    private StorageReference mockStorageRef;

    @Mock
    private Task<Void> mockVoidTask;

    @Mock
    private Task<byte[]> mockBytesTask;

    @Mock
    private Task<Uri> mockUriTask;

    @Mock
    private FileDownloadTask mockFileDownloadTask;

    @Mock
    private StreamDownloadTask mockStreamDownloadTask;

    @Mock
    private Task<StorageMetadata> mockMetadataTask;

    @Mock
    private UploadTask mockUploadTask;

    @Mock
    private Uri uri;

    @Mock
    private File file;

    @Mock
    private StorageMetadata metadata;

    @Mock
    private FileDownloadTask.TaskSnapshot fileSnapshot;

    @Mock
    private StreamDownloadTask.TaskSnapshot streamSnapshot;

    @Mock
    private UploadTask.TaskSnapshot uploadSnapshot;

    private byte[] nullBytes;

    private byte[] notNullbytes = new byte[0];

    @Mock
    private StreamDownloadTask.StreamProcessor processor;

    @Mock
    private InputStream stream;

    private Void voidData = null;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        setupTask(mockBytesTask);
        setupTask(mockVoidTask);
        setupTask(mockUriTask);
        setupTask(mockFileDownloadTask);
        setupTask(mockStreamDownloadTask);
        setupTask(mockMetadataTask);
        setupTask(mockUploadTask);

        when(mockStorageRef.getBytes(20)).thenReturn(mockBytesTask);
        when(mockStorageRef.getDownloadUrl()).thenReturn(mockUriTask);
        when(mockStorageRef.getFile(file)).thenReturn(mockFileDownloadTask);
        when(mockStorageRef.getFile(uri)).thenReturn(mockFileDownloadTask);
        when(mockStorageRef.getStream()).thenReturn(mockStreamDownloadTask);
        when(mockStorageRef.getStream(processor)).thenReturn(mockStreamDownloadTask);
        when(mockStorageRef.getMetadata()).thenReturn(mockMetadataTask);
        when(mockStorageRef.putBytes(notNullbytes)).thenReturn(mockUploadTask);
        when(mockStorageRef.putBytes(nullBytes)).thenReturn(mockUploadTask);
        when(mockStorageRef.putBytes(notNullbytes, metadata)).thenReturn(mockUploadTask);
        when(mockStorageRef.putBytes(nullBytes, metadata)).thenReturn(mockUploadTask);
        when(mockStorageRef.putFile(uri)).thenReturn(mockUploadTask);
        when(mockStorageRef.putFile(uri, metadata)).thenReturn(mockUploadTask);
        when(mockStorageRef.putFile(uri, metadata, uri)).thenReturn(mockUploadTask);
        when(mockStorageRef.putStream(stream)).thenReturn(mockUploadTask);
        when(mockStorageRef.putStream(stream, metadata)).thenReturn(mockUploadTask);
        when(mockStorageRef.updateMetadata(metadata)).thenReturn(mockMetadataTask);
        when(mockStorageRef.delete()).thenReturn(mockVoidTask);
    }

    @Test
    public void getBytes() throws InterruptedException {
        TestObserver<byte[]> storageTestObserver =
                RxFirebaseStorage.getBytes(mockStorageRef, 20)
                        .test();

        testOnSuccessListener.getValue().onSuccess(notNullbytes);
        testOnCompleteListener.getValue().onComplete(mockBytesTask);

        verify(mockStorageRef).getBytes(20);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(notNullbytes))
                .assertComplete()
                .dispose();
    }

    @Test
    public void getBytesNoData() throws InterruptedException {
        TestObserver<byte[]> storageTestObserver =
                RxFirebaseStorage.getBytes(mockStorageRef, 20)
                        .test();

        testOnFailureListener.getValue().onFailure(NULL_FIREBASE_EXCEPTION);

        verify(mockStorageRef).getBytes(20);

        storageTestObserver.assertError(NULL_FIREBASE_EXCEPTION)
                .assertValueSet(Collections.singletonList(nullBytes))
                .assertNotComplete()
                .dispose();
    }

    @Test
    public void getDownloadUrl() throws InterruptedException {
        TestObserver<Uri> storageTestObserver =
                RxFirebaseStorage.getDownloadUrl(mockStorageRef)
                        .test();

        testOnSuccessListener.getValue().onSuccess(uri);
        testOnCompleteListener.getValue().onComplete(mockUriTask);

        verify(mockStorageRef).getDownloadUrl();

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(uri))
                .assertComplete()
                .dispose();
    }

    @Test
    public void getFile() throws InterruptedException {

        TestObserver<FileDownloadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.getFile(mockStorageRef, file)
                        .test();

        testOnSuccessListener.getValue().onSuccess(fileSnapshot);

        verify(mockStorageRef).getFile(file);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(fileSnapshot))
                .assertComplete()
                .dispose();
    }

    @Test
    public void getFileUri() throws InterruptedException {

        TestObserver<FileDownloadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.getFile(mockStorageRef, uri)
                        .test();

        testOnSuccessListener.getValue().onSuccess(fileSnapshot);

        verify(mockStorageRef).getFile(uri);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(fileSnapshot))
                .assertComplete()
                .dispose();
    }


    @Test
    public void getMetadata() throws InterruptedException {

        TestObserver<StorageMetadata> storageTestObserver =
                RxFirebaseStorage.getMetadata(mockStorageRef)
                        .test();

        testOnSuccessListener.getValue().onSuccess(metadata);
        testOnCompleteListener.getValue().onComplete(mockMetadataTask);

        verify(mockStorageRef).getMetadata();

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(metadata))
                .assertComplete()
                .dispose();
    }


    @Test
    public void getStream() throws InterruptedException {

        TestObserver<StreamDownloadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.getStream(mockStorageRef)
                        .test();

        testOnSuccessListener.getValue().onSuccess(streamSnapshot);

        verify(mockStorageRef).getStream();

        storageTestObserver.assertNoErrors();
        storageTestObserver.assertValueCount(1);
        storageTestObserver.assertValueSet(Collections.singletonList(streamSnapshot));
        storageTestObserver.assertComplete();
        storageTestObserver.dispose();
    }

    @Test
    public void getStreamProcessor() throws InterruptedException {

        TestObserver<StreamDownloadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.getStream(mockStorageRef, processor)
                        .test();

        testOnSuccessListener.getValue().onSuccess(streamSnapshot);

        verify(mockStorageRef).getStream(processor);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(streamSnapshot))
                .assertComplete()
                .dispose();
    }

    @Test
    public void putBytes() throws InterruptedException {

        TestObserver<UploadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.putBytes(mockStorageRef, notNullbytes)
                        .test();

        testOnSuccessListener.getValue().onSuccess(uploadSnapshot);

        verify(mockStorageRef).putBytes(notNullbytes);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(uploadSnapshot))
                .assertComplete()
                .dispose();
    }

    @Test
    public void putBytesNoData() throws InterruptedException {

        TestObserver<UploadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.putBytes(mockStorageRef, nullBytes)
                        .test();

        testOnFailureListener.getValue().onFailure(NULL_FIREBASE_EXCEPTION);

        verify(mockStorageRef).putBytes(nullBytes);

        storageTestObserver.assertError(NULL_FIREBASE_EXCEPTION)
                .assertValueSet(Collections.singletonList(uploadSnapshot))
                .assertNotComplete()
                .dispose();
    }

    @Test
    public void putBytesMetadata() throws InterruptedException {

        TestObserver<UploadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.putBytes(mockStorageRef, notNullbytes, metadata)
                        .test();


        testOnSuccessListener.getValue().onSuccess(uploadSnapshot);
        testOnCompleteListener.getValue().onComplete(mockVoidTask);

        verify(mockStorageRef).putBytes(notNullbytes, metadata);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(uploadSnapshot))
                .assertComplete()
                .dispose();
    }

    @Test
    public void putFile() throws InterruptedException {

        TestObserver<UploadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.putFile(mockStorageRef, uri)
                        .test();

        testOnSuccessListener.getValue().onSuccess(uploadSnapshot);

        verify(mockStorageRef).putFile(uri);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(uploadSnapshot))
                .assertComplete()
                .dispose();
    }

    @Test
    public void putFileMetadata() throws InterruptedException {

        TestObserver<UploadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.putFile(mockStorageRef, uri, metadata)
                        .test();

        testOnSuccessListener.getValue().onSuccess(uploadSnapshot);

        verify(mockStorageRef).putFile(uri, metadata);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(uploadSnapshot))
                .assertComplete()
                .dispose();
    }

    @Test
    public void putFileMetadataAndUri() throws InterruptedException {

        TestObserver<UploadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.putFile(mockStorageRef, uri, metadata, uri)
                        .test();

        testOnSuccessListener.getValue().onSuccess(uploadSnapshot);

        verify(mockStorageRef).putFile(uri, metadata, uri);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(uploadSnapshot))
                .assertComplete()
                .dispose();
    }

    @Test
    public void putStream() throws InterruptedException {

        TestObserver<UploadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.putStream(mockStorageRef, stream)
                        .test();

        testOnSuccessListener.getValue().onSuccess(uploadSnapshot);

        verify(mockStorageRef).putStream(stream);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(uploadSnapshot))
                .assertComplete()
                .dispose();
    }

    @Test
    public void putStreamMetadata() throws InterruptedException {

        TestObserver<UploadTask.TaskSnapshot> storageTestObserver =
                RxFirebaseStorage.putStream(mockStorageRef, stream, metadata)
                        .test();

        testOnSuccessListener.getValue().onSuccess(uploadSnapshot);

        verify(mockStorageRef).putStream(stream, metadata);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(uploadSnapshot))
                .assertComplete()
                .dispose();
    }

    @Test
    public void updateMetadata() throws InterruptedException {

        TestObserver<StorageMetadata> storageTestObserver =
                RxFirebaseStorage.updateMetadata(mockStorageRef, metadata)
                        .test();

        testOnSuccessListener.getValue().onSuccess(metadata);

        verify(mockStorageRef).updateMetadata(metadata);

        storageTestObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueSet(Collections.singletonList(metadata))
                .assertComplete()
                .dispose();
    }

    @Test
    public void delete() throws InterruptedException {

        TestObserver<Void> storageTestObserver =
                RxFirebaseStorage.delete(mockStorageRef)
                        .test();

        testOnSuccessListener.getValue().onSuccess(voidData);
        testOnCompleteListener.getValue().onComplete(mockVoidTask);

        verify(mockStorageRef).delete();

        storageTestObserver.assertNoErrors()
                .assertComplete()
                .dispose();
    }
}
