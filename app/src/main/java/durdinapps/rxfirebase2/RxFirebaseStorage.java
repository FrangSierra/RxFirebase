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
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

public class RxFirebaseStorage {

   /**
    * Asynchronously downloads the object from this {@link StorageReference} a byte array will be allocated large enough to hold the entire file in memory.
    *
    * @param storageRef           represents a reference to a Google Cloud Storage object.
    * @param maxDownloadSizeBytes the maximum allowed size in bytes that will be allocated. Set this parameter to prevent out of memory conditions from occurring.
    *                             If the download exceeds this limit, the task will fail and an IndexOutOfBoundsException will be returned.
    * @return a {@link Maybe} which emits an byte[] if success.
    */
   @NonNull
   public static Maybe<byte[]> getBytes(@NonNull final StorageReference storageRef,
                                        final long maxDownloadSizeBytes) {
      return Maybe.create(new MaybeOnSubscribe<byte[]>() {
         @Override
         public void subscribe(MaybeEmitter<byte[]> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getBytes(maxDownloadSizeBytes));
         }
      });
   }

   /**
    * Asynchronously retrieves a long lived download URL with a revocable token.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @return a {@link Maybe} which emits an {@link Uri} if success.
    */
   @NonNull
   public static Maybe<Uri> getDownloadUrl(@NonNull final StorageReference storageRef) {
      return Maybe.create(new MaybeOnSubscribe<Uri>() {
         @Override
         public void subscribe(MaybeEmitter<Uri> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getDownloadUrl());
         }
      });
   }

   /**
    * Asynchronously downloads the object at this {@link StorageReference} to a specified system filepath.
    *
    * @param storageRef      represents a reference to a Google Cloud Storage object.
    * @param destinationFile a File representing the path the object should be downloaded to.
    * @return a {@link Maybe} which emits an {@link FileDownloadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<FileDownloadTask.TaskSnapshot> getFile(@NonNull final StorageReference storageRef,
                                                              @NonNull final File destinationFile) {
      return Maybe.create(new MaybeOnSubscribe<FileDownloadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<FileDownloadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getFile(destinationFile));
         }
      });
   }

   /**
    * Asynchronously downloads the object at this {@link StorageReference} to a specified system filepath.
    *
    * @param storageRef     represents a reference to a Google Cloud Storage object.
    * @param destinationUri a file system URI representing the path the object should be downloaded to.
    * @return a {@link Maybe} which emits an {@link FileDownloadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<FileDownloadTask.TaskSnapshot> getFile(@NonNull final StorageReference storageRef,
                                                              @NonNull final Uri destinationUri) {
      return Maybe.create(new MaybeOnSubscribe<FileDownloadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<FileDownloadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getFile(destinationUri));
         }
      });
   }

   /**
    * Retrieves metadata associated with an object at this {@link StorageReference}.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @return a {@link Maybe} which emits an {@link StorageMetadata} if success.
    */
   @NonNull
   public static Maybe<StorageMetadata> getMetadata(@NonNull final StorageReference storageRef) {
      return Maybe.create(new MaybeOnSubscribe<StorageMetadata>() {
         @Override
         public void subscribe(MaybeEmitter<StorageMetadata> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getMetadata());
         }
      });
   }

   /**
    * Asynchronously downloads the object at this {@link StorageReference} via a InputStream.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @return a {@link Maybe} which emits an {@link StreamDownloadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef) {
      return Maybe.create(new MaybeOnSubscribe<StreamDownloadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<StreamDownloadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getStream());
         }
      });
   }

   /**
    * Asynchronously downloads the object at this {@link StorageReference} via a InputStream.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @param processor  A StreamDownloadTask.StreamProcessor that is responsible for reading data from the InputStream.
    *                   The StreamDownloadTask.StreamProcessor is called on a background thread and checked exceptions thrown
    *                   from this object will be returned as a failure to the OnFailureListener registered on the StreamDownloadTask.
    * @return a {@link Maybe} which emits an {@link StreamDownloadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef,
                                                                  @NonNull final StreamDownloadTask.StreamProcessor processor) {
      return Maybe.create(new MaybeOnSubscribe<StreamDownloadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<StreamDownloadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getStream(processor));
         }
      });
   }

   /**
    * Asynchronously uploads byte data to this {@link StorageReference}.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @param bytes      The byte[] to upload.
    * @return a {@link Maybe} which emits an {@link UploadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                         @NonNull final byte[] bytes) {
      return Maybe.create(new MaybeOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putBytes(bytes));
         }
      });
   }

   /**
    * Asynchronously uploads byte data to this {@link StorageReference}.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @param bytes      The byte[] to upload.
    * @param metadata   {@link StorageMetadata} containing additional information (MIME type, etc.) about the object being uploaded.
    * @return a {@link Maybe} which emits an {@link UploadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                         @NonNull final byte[] bytes,
                                                         @NonNull final StorageMetadata metadata) {
      return Maybe.create(new MaybeOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putBytes(bytes, metadata));
         }
      });
   }

   /**
    * Asynchronously uploads from a content URI to this {@link StorageReference}.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @param uri        The source of the upload. This can be a file:// scheme or any content URI. A content resolver will be used to load the data.
    * @return a {@link Maybe} which emits an {@link UploadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                        @NonNull final Uri uri) {
      return Maybe.create(new MaybeOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putFile(uri));
         }
      });
   }

   /**
    * Asynchronously uploads from a content URI to this {@link StorageReference}.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @param uri        The source of the upload. This can be a file:// scheme or any content URI. A content resolver will be used to load the data.
    * @param metadata   {@link StorageMetadata} containing additional information (MIME type, etc.) about the object being uploaded.
    * @return a {@link Maybe} which emits an {@link UploadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                        @NonNull final Uri uri,
                                                        @NonNull final StorageMetadata metadata) {
      return Maybe.create(new MaybeOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putFile(uri, metadata));
         }
      });
   }

   /**
    * Asynchronously uploads from a content URI to this {@link StorageReference}.
    *
    * @param storageRef        represents a reference to a Google Cloud Storage object.
    * @param uri               The source of the upload. This can be a file:// scheme or any content URI. A content resolver will be used to load the data.
    * @param metadata          {@link StorageMetadata} containing additional information (MIME type, etc.) about the object being uploaded.
    * @param existingUploadUri If set, an attempt is made to resume an existing upload session as defined by getUploadSessionUri().
    * @return a {@link Maybe} which emits an {@link UploadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                        @NonNull final Uri uri,
                                                        @NonNull final StorageMetadata metadata,
                                                        @NonNull final Uri existingUploadUri) {
      return Maybe.create(new MaybeOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putFile(uri, metadata, existingUploadUri));
         }
      });
   }

   /**
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @param stream     The InputStream to upload.
    * @param metadata   {@link StorageMetadata} containing additional information (MIME type, etc.) about the object being uploaded.
    * @return a {@link Maybe} which emits an {@link UploadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                          @NonNull final InputStream stream,
                                                          @NonNull final StorageMetadata metadata) {
      return Maybe.create(new MaybeOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putStream(stream, metadata));
         }
      });
   }

   /**
    * Asynchronously uploads a stream of data to this {@link StorageReference}.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @param stream     The InputStream to upload.
    * @return a {@link Maybe} which emits an {@link UploadTask.TaskSnapshot} if success.
    */
   @NonNull
   public static Maybe<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                          @NonNull final InputStream stream) {
      return Maybe.create(new MaybeOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putStream(stream));
         }
      });
   }

   /**
    * Asynchronously uploads a stream of data to this {@link StorageReference}.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @param metadata   {@link StorageMetadata} containing additional information (MIME type, etc.) about the object being uploaded.
    * @return a {@link Maybe} which emits an {@link StorageMetadata} if success.
    */
   @NonNull
   public static Maybe<StorageMetadata> updateMetadata(@NonNull final StorageReference storageRef,
                                                       @NonNull final StorageMetadata metadata) {
      return Maybe.create(new MaybeOnSubscribe<StorageMetadata>() {
         @Override
         public void subscribe(MaybeEmitter<StorageMetadata> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.updateMetadata(metadata));
         }
      });
   }

   /**
    * Deletes the object at this {@link StorageReference}.
    *
    * @param storageRef represents a reference to a Google Cloud Storage object.
    * @return a {@link Completable} if the task is complete successfully.
    */
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