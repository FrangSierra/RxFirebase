package durdinapps.rxfirebase2;


import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public abstract class DocumentSnapshotMapper<T, U> implements Function<T, U> {

   private DocumentSnapshotMapper() {
   }

   public static <U> DocumentSnapshotMapper<DocumentSnapshot, U> of(Class<U> clazz) {
      return new DocumentSnapshotMapper.TypedDataSnapshotMapper<U>(clazz);
   }

   private static <U> U getDataSnapshotTypedValue(DocumentSnapshot documentSnapshot, Class<U> clazz) {
      return documentSnapshot.toObject(clazz);
   }

   private static class TypedDataSnapshotMapper<U> extends DocumentSnapshotMapper<DocumentSnapshot, U> {

      private final Class<U> clazz;

      public TypedDataSnapshotMapper(final Class<U> clazz) {
         this.clazz = clazz;
      }

      @Override
      public U apply(final DocumentSnapshot documentSnapshot) {
         return getDataSnapshotTypedValue(documentSnapshot, clazz);
      }
   }

   static final Predicate<DocumentSnapshot> DOCUMENT_EXISTENCE_PREDICATE= new Predicate<DocumentSnapshot>() {
      @Override
      public boolean test(@NonNull DocumentSnapshot documentSnapshot) throws Exception {
         return documentSnapshot.exists();
      }
   };
}
