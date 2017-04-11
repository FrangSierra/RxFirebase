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

   @NonNull
   public static Maybe<Uri> getDownloadUrl(@NonNull final StorageReference storageRef) {
      return Maybe.create(new MaybeOnSubscribe<Uri>() {
         @Override
         public void subscribe(MaybeEmitter<Uri> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getDownloadUrl());
         }
      });
   }

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

   @NonNull
   public static Maybe<StorageMetadata> getMetadata(@NonNull final StorageReference storageRef) {
      return Maybe.create(new MaybeOnSubscribe<StorageMetadata>() {
         @Override
         public void subscribe(MaybeEmitter<StorageMetadata> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getMetadata());
         }
      });
   }

   @NonNull
   public static Maybe<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef) {
      return Maybe.create(new MaybeOnSubscribe<StreamDownloadTask.TaskSnapshot>() {
         @Override
         public void subscribe(MaybeEmitter<StreamDownloadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getStream());
         }
      });
   }

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