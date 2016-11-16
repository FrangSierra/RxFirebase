package durdinapps.rxfirebase2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RxFirebaseUserTest {

   public static final String ANY_EMAIL = "email@email.com";
   public static final Exception EXCEPTION = new Exception("Something bad happen");
   public static final String ANY_PASSWORD = "ANY_PASSWORD";
   @Mock
   FirebaseUser firebaseUser;

   @Mock
   GetTokenResult getTokenResult;

   @Mock
   Task<Void> voidTask;

   @Mock
   Task<GetTokenResult> getTokenResultTask;

   @Mock
   UserProfileChangeRequest userProfileChangeRequest;

   @Mock
   AuthCredential authCredential;

   @Mock
   Task<AuthCredential> authCredentialTask;

   @Mock
   AuthResult authResult;

   @Mock
   Task<AuthResult> authResultTask;

   private ArgumentCaptor<OnCompleteListener> testOnCompleteListener;
   private ArgumentCaptor<OnSuccessListener> testOnSuccessListener;
   private ArgumentCaptor<OnFailureListener> testOnFailureListener;


   private boolean ANY_FORCE_REFRESH_VALUE = true;


   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      testOnCompleteListener = ArgumentCaptor.forClass(OnCompleteListener.class);
      testOnSuccessListener = ArgumentCaptor.forClass(OnSuccessListener.class);
      testOnFailureListener = ArgumentCaptor.forClass(OnFailureListener.class);

      setupTask(getTokenResultTask);
      setupTask(voidTask);
      setupTask(authResultTask);
      setupTask(authCredentialTask);

      when(firebaseUser.getToken(ANY_FORCE_REFRESH_VALUE)).thenReturn(getTokenResultTask);
      when(firebaseUser.updateEmail(ANY_EMAIL)).thenReturn(voidTask);
      when(firebaseUser.updatePassword(ANY_PASSWORD)).thenReturn(voidTask);
      when(firebaseUser.updateProfile(userProfileChangeRequest)).thenReturn(voidTask);
      when(firebaseUser.delete()).thenReturn(voidTask);
      when(firebaseUser.reauthenticate(authCredential)).thenReturn(voidTask);
      when(firebaseUser.linkWithCredential(authCredential)).thenReturn(authResultTask);
   }

   private <T> void setupTask(Task<T> task) {
      when(task.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(task);
      when(task.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(task);
      when(task.addOnFailureListener(testOnFailureListener.capture())).thenReturn(task);
   }

   @Test
   public void getToken() throws Exception {
      TestObserver<GetTokenResult> test = RxFirebaseUser.getToken(firebaseUser, ANY_FORCE_REFRESH_VALUE).test();

      testOnSuccessListener.getValue().onSuccess(getTokenResult);
      testOnCompleteListener.getValue().onComplete(getTokenResultTask);

      verify(firebaseUser).getToken(ANY_FORCE_REFRESH_VALUE);

      test.assertComplete()
            .assertNoErrors()
            .assertValueCount(1);
   }

   @Test
   public void getTokenError() throws Exception {
      TestObserver<GetTokenResult> test = RxFirebaseUser.getToken(firebaseUser, ANY_FORCE_REFRESH_VALUE).test();
      testOnFailureListener.getValue().onFailure(EXCEPTION);
      verify(firebaseUser).getToken(ANY_FORCE_REFRESH_VALUE);

      test.assertError(EXCEPTION);

   }

   @Test
   public void updateEmail() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.updateEmail(firebaseUser, ANY_EMAIL).test();

      testOnCompleteListener.getValue().onComplete(voidTask);
      testOnSuccessListener.getValue().onSuccess(voidTask);

      verify(firebaseUser).updateEmail(ANY_EMAIL);

      test.assertComplete();

   }

   @Test
   public void updateEmailError() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.updateEmail(firebaseUser, ANY_EMAIL).test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseUser).updateEmail(ANY_EMAIL);

      test.assertError(EXCEPTION);

   }

   @Test
   public void updatePassword() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.updatePassword(firebaseUser, ANY_PASSWORD).test();
      testOnCompleteListener.getValue().onComplete(voidTask);
      testOnSuccessListener.getValue().onSuccess(voidTask);

      verify(firebaseUser).updatePassword(ANY_PASSWORD);

      test.assertComplete();
   }

   @Test
   public void updatePasswordError() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.updatePassword(firebaseUser, ANY_PASSWORD).test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseUser).updatePassword(ANY_PASSWORD);

      test.assertError(EXCEPTION);

   }

   @Test
   public void updateProfile() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.updateProfile(firebaseUser, userProfileChangeRequest).test();

      testOnCompleteListener.getValue().onComplete(voidTask);
      testOnSuccessListener.getValue().onSuccess(voidTask);

      verify(firebaseUser).updateProfile(userProfileChangeRequest);

      test.assertComplete();

   }

   @Test
   public void updateProfileError() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.updateProfile(firebaseUser, userProfileChangeRequest).test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseUser).updateProfile(userProfileChangeRequest);

      test.assertError(EXCEPTION);

   }

   @Test
   public void delete() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.delete(firebaseUser).test();

      testOnCompleteListener.getValue().onComplete(voidTask);
      testOnSuccessListener.getValue().onSuccess(voidTask);

      verify(firebaseUser).delete();

      test.assertComplete();
   }

   @Test
   public void deleteError() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.delete(firebaseUser).test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);
      verify(firebaseUser).delete();

      test.assertError(EXCEPTION);
   }

   @Test
   public void reAuthenticate() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.reAuthenticate(firebaseUser, authCredential).test();

      testOnCompleteListener.getValue().onComplete(voidTask);
      testOnSuccessListener.getValue().onSuccess(voidTask);

      verify(firebaseUser).reauthenticate(authCredential);

      test.assertComplete();

   }

   @Test
   public void reAuthenticateError() throws Exception {
      TestObserver<Void> test = RxFirebaseUser.reAuthenticate(firebaseUser, authCredential).test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseUser).reauthenticate(authCredential);

      test.assertError(EXCEPTION);
   }

   @Test
   public void linkWithCredendials() throws Exception {
      TestObserver<AuthResult> test = RxFirebaseUser.linkWithCredential(firebaseUser, authCredential).test();

      testOnSuccessListener.getValue().onSuccess(authResult);
      testOnCompleteListener.getValue().onComplete(authResultTask);

      verify(firebaseUser).linkWithCredential(authCredential);


      test.assertNoErrors()
            .assertValueCount(1)
            .assertComplete()
            .assertValueSet(Collections.singletonList(authResult));
   }

   @Test
   public void linkWithCredentialsError() throws Exception {
      TestObserver<AuthResult> test = RxFirebaseUser.linkWithCredential(firebaseUser, authCredential).test();

      testOnFailureListener.getValue().onFailure(EXCEPTION);

      verify(firebaseUser).linkWithCredential(authCredential);

      test.assertError(EXCEPTION);

   }
}