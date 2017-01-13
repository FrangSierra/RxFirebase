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

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class RxFirebaseStorage {

   @NonNull
   public static Flowable<byte[]> getBytes(@NonNull final StorageReference storageRef,
                                           final long maxDownloadSizeBytes,
                                           @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<byte[]>() {
         @Override
         public void subscribe(FlowableEmitter<byte[]> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getBytes(maxDownloadSizeBytes));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<Uri> getDownloadUrl(@NonNull final StorageReference storageRef,
                                              @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<Uri>() {
         @Override
         public void subscribe(FlowableEmitter<Uri> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getDownloadUrl());
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<FileDownloadTask.TaskSnapshot> getFile(@NonNull final StorageReference storageRef,
                                                                 @NonNull final File destinationFile,
                                                                 @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<FileDownloadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<FileDownloadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getFile(destinationFile));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<FileDownloadTask.TaskSnapshot> getFile(@NonNull final StorageReference storageRef,
                                                                 @NonNull final Uri destinationUri,
                                                                 @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<FileDownloadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<FileDownloadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getFile(destinationUri));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<StorageMetadata> getMetadata(@NonNull final StorageReference storageRef,
                                                       @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<StorageMetadata>() {
         @Override
         public void subscribe(FlowableEmitter<StorageMetadata> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getMetadata());
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef,
                                                                     @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<StreamDownloadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<StreamDownloadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getStream());
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef,
                                                                     @NonNull final StreamDownloadTask.StreamProcessor processor,
                                                                     @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<StreamDownloadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<StreamDownloadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.getStream(processor));
         }
      }, strategy);
   }


   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                            @NonNull final byte[] bytes,
                                                            @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putBytes(bytes));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                            @NonNull final byte[] bytes,
                                                            @NonNull final StorageMetadata metadata,
                                                            @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putBytes(bytes, metadata));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                           @NonNull final Uri uri, @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putFile(uri));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                           @NonNull final Uri uri,
                                                           @NonNull final StorageMetadata metadata,
                                                           @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putFile(uri, metadata));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                           @NonNull final Uri uri,
                                                           @NonNull final StorageMetadata metadata,
                                                           @NonNull final Uri existingUploadUri,
                                                           @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putFile(uri, metadata, existingUploadUri));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                             @NonNull final InputStream stream,
                                                             @NonNull final StorageMetadata metadata,
                                                             @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putStream(stream, metadata));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                             @NonNull final InputStream stream,
                                                             @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<UploadTask.TaskSnapshot>() {
         @Override
         public void subscribe(FlowableEmitter<UploadTask.TaskSnapshot> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.putStream(stream));
         }
      }, strategy);
   }

   @NonNull
   public static Flowable<StorageMetadata> updateMetadata(@NonNull final StorageReference storageRef,
                                                          @NonNull final StorageMetadata metadata,
                                                          @NonNull BackpressureStrategy strategy) {
      return Flowable.create(new FlowableOnSubscribe<StorageMetadata>() {
         @Override
         public void subscribe(FlowableEmitter<StorageMetadata> emitter) throws Exception {
            RxHandler.assignOnTask(emitter, storageRef.updateMetadata(metadata));
         }
      }, strategy);
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

   @NonNull
   public static Flowable<FileDownloadTask.TaskSnapshot> getFile(@NonNull final StorageReference storageRef,
                                                                 @NonNull final Uri destinationUri) {
      return getFile(storageRef, destinationUri, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<StorageMetadata> getMetadata(@NonNull final StorageReference storageRef) {
      return getMetadata(storageRef, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef) {
      return getStream(storageRef, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef,
                                                                     @NonNull final StreamDownloadTask.StreamProcessor processor) {
      return getStream(storageRef, processor, BackpressureStrategy.DROP);
   }


   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                            @NonNull final byte[] bytes) {
      return putBytes(storageRef, bytes, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                            @NonNull final byte[] bytes,
                                                            @NonNull final StorageMetadata metadata) {
      return putBytes(storageRef, bytes, metadata, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                           @NonNull final Uri uri) {
      return putFile(storageRef, uri, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                           @NonNull final Uri uri,
                                                           @NonNull final StorageMetadata metadata) {
      return putFile(storageRef, uri, metadata, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                           @NonNull final Uri uri,
                                                           @NonNull final StorageMetadata metadata,
                                                           @NonNull final Uri existingUploadUri) {
      return putFile(storageRef, uri, metadata, existingUploadUri, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                             @NonNull final InputStream stream,
                                                             @NonNull final StorageMetadata metadata) {
      return putStream(storageRef, stream, metadata, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                             @NonNull final InputStream stream) {
      return putStream(storageRef, stream, BackpressureStrategy.DROP);
   }

   @NonNull
   public static Flowable<StorageMetadata> updateMetadata(@NonNull final StorageReference storageRef,
                                                          @NonNull final StorageMetadata metadata) {
      return updateMetadata(storageRef, metadata, BackpressureStrategy.DROP);
   }
}