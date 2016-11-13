package durdinapps.rxfirebase2;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class RxFirebaseStorage {

    @NonNull
    public static Observable<byte[]> getBytes(@NonNull final StorageReference storageRef,
                                              final long maxDownloadSizeBytes) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.getBytes(maxDownloadSizeBytes));
            }
        });
    }

    @NonNull
    public static Observable<Uri> getDownloadUrl(@NonNull final StorageReference storageRef) {
        return Observable.create(new ObservableOnSubscribe<Uri>() {
            @Override
            public void subscribe(ObservableEmitter<Uri> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.getDownloadUrl());
            }
        });
    }

    @NonNull
    public static Observable<FileDownloadTask.TaskSnapshot> getFile(@NonNull final StorageReference storageRef,
                                                                    @NonNull final File destinationFile) {
        return Observable.create(new ObservableOnSubscribe<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<FileDownloadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.getFile(destinationFile));
            }
        });
    }

    @NonNull
    public static Observable<FileDownloadTask.TaskSnapshot> getFile(@NonNull final StorageReference storageRef,
                                                                    @NonNull final Uri destinationUri) {
        return Observable.create(new ObservableOnSubscribe<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<FileDownloadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.getFile(destinationUri));
            }
        });
    }

    @NonNull
    public static Observable<StorageMetadata> getMetadata(@NonNull final StorageReference storageRef) {
        return Observable.create(new ObservableOnSubscribe<StorageMetadata>() {
            @Override
            public void subscribe(ObservableEmitter<StorageMetadata> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.getMetadata());
            }
        });
    }

    @NonNull
    public static Observable<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef) {
        return Observable.create(new ObservableOnSubscribe<StreamDownloadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<StreamDownloadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.getStream());
            }
        });
    }

    @NonNull
    public static Observable<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef,
                                                                        @NonNull final StreamDownloadTask.StreamProcessor processor) {
        return Observable.create(new ObservableOnSubscribe<StreamDownloadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<StreamDownloadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.getStream(processor));
            }
        });
    }


    @NonNull
    public static Observable<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                               @NonNull final byte[] bytes) {
        return Observable.create(new ObservableOnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.putBytes(bytes));
            }
        });
    }

    @NonNull
    public static Observable<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                               @NonNull final byte[] bytes,
                                                               @NonNull final StorageMetadata metadata) {
        return Observable.create(new ObservableOnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.putBytes(bytes, metadata));
            }
        });
    }

    @NonNull
    public static Observable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                              @NonNull final Uri uri) {
        return Observable.create(new ObservableOnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.putFile(uri));
            }
        });
    }

    @NonNull
    public static Observable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                              @NonNull final Uri uri,
                                                              @NonNull final StorageMetadata metadata) {
        return Observable.create(new ObservableOnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.putFile(uri, metadata));
            }
        });
    }

    @NonNull
    public static Observable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                              @NonNull final Uri uri,
                                                              @NonNull final StorageMetadata metadata,
                                                              @NonNull final Uri existingUploadUri) {
        return Observable.create(new ObservableOnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.putFile(uri, metadata, existingUploadUri));
            }
        });
    }

    @NonNull
    public static Observable<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                                @NonNull final InputStream stream,
                                                                @NonNull final StorageMetadata metadata) {
        return Observable.create(new ObservableOnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.putStream(stream, metadata));
            }
        });
    }

    @NonNull
    public static Observable<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                                @NonNull final InputStream stream) {
        return Observable.create(new ObservableOnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void subscribe(ObservableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.putStream(stream));
            }
        });
    }

    @NonNull
    public static Observable<StorageMetadata> updateMetadata(@NonNull final StorageReference storageRef,
                                                             @NonNull final StorageMetadata metadata) {
        return Observable.create(new ObservableOnSubscribe<StorageMetadata>() {
            @Override
            public void subscribe(ObservableEmitter<StorageMetadata> emitter) throws Exception {
                RxHandler.assignOnTask(emitter, storageRef.updateMetadata(metadata));
            }
        });
    }

    @NonNull
    public static Completable delete(@NonNull final StorageReference storageRef) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, storageRef.delete());
            }
        });
    }


}