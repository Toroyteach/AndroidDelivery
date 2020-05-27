package com.example.frsystem.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.frsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.text.TextUtils.isEmpty;

//this is users update profile info

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "activity";
    private EditText mNameField, mEmailField, mPassword, ENumber;
    private ImageView mProfileImage;

    private ImageView mBack;
    private Button buttonUpdate;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference mDriverDatabase;
    private FirebaseFirestore db;
    Map<String, Object> user = new HashMap<>();

    private String userID;
    private String mName;
    private String mEmail;
    private String mPass;
    private String mNumber;
    private String mProfileImageUrl;
    private int number;

    private Uri resultUri;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeUi(); // initialize ui of the activity
        initialfirebase(); // initialize firebase

        getUserInfo(userID);


    }

    public void initialfirebase(){
        mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();

        final String uid = mAuth.getUid();
        loadDriverdetails(uid);
    }

    private void loadDriverdetails(String uid) {

        DocumentReference docRef = db.collection("driver").document(uid);

        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.CACHE;

        // Get the document, forcing the SDK to use the offline cache
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    Log.d(TAG, "Cached document data: " + document.get("profilepicuri"));
                    String profileuri = Objects.requireNonNull(document.get("profilepicuri")).toString();
                    Picasso.get().load(profileuri).resize(250, 400).error(R.drawable.ic_account_user).into(mProfileImage);
                } else {
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });

    }

    public void initializeUi(){
        mNameField =  findViewById(R.id.profileusername);
        mEmailField =  findViewById(R.id.profileemail);
        mPassword =  findViewById(R.id.profilepassword);
        ENumber =  findViewById(R.id.profilenumber);
        mProfileImage =  findViewById(R.id.profile_image_appabr);
        buttonUpdate = findViewById(R.id.updateprofile);

        buttonUpdate.setOnClickListener(v -> saveUserInformation());

        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });
    }

    private void getUserInfo(String userId){
        Log.d(TAG, "update userinfo");

        Log.d(TAG, userId);
        db.collection("driver").whereEqualTo("uId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, "retrieving data");
                            mNameField.setText((CharSequence) document.get("username"));
                            mEmailField.setText((CharSequence) document.get("email"));
                            //mPassword.setText((CharSequence) document.get("username"));
                            ENumber.setText(String.valueOf(document.get("number")));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void saveUserInformation() {

        if(isEmpty(mNameField.getText()) || mEmailField.length() == 0 || ENumber.length() < 6 || mPassword == null)
        {
            //EditText is empty
            Toast.makeText(SettingsActivity.this, "Please ensure you have filled every field properly", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.show();

        mName = mNameField.getText().toString();
        mEmail = mEmailField.getText().toString();
        mPass = mPassword.getText().toString();
        mNumber = ENumber.getText().toString();
        number = Integer.parseInt(mNumber);

        if(resultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(e -> {
                Log.d(TAG, "on failure state listner");
                progressDialog.dismiss();
            });
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                String imageuri = downloadUrl.toString();
                insertfirestore(userID, number, mName, mEmail, imageuri); // this method that updates the user information
                getprofilepicuri(filePath);
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this, "Updated Successfuly", Toast.LENGTH_SHORT).show();
            });
        }  else {
            insertfirestore(userID, number, mName, mEmail, "n/a");
            progressDialog.dismiss();
            Toast.makeText(SettingsActivity.this, "Updated Successfuly", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            resultUri = data.getData();
            mProfileImage.setImageURI(resultUri);
        }
    }

    private void updateprofilepic(Uri uri){

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        Log.d(TAG, "should havd replaced image");

        assert user != null;
        user.updateProfile(request).addOnSuccessListener(aVoid -> {

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void getprofilepicuri(StorageReference ref){

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                updateprofilepic(uri);
            }
        });
    }

    public void insertfirestore(String user_id, int number, String username, String email, String uri){

        user.put("email", email);
        user.put("number", number);
        user.put("username", username);
        user.put("profilepicuri", uri);
        user.put("datecreated", FieldValue.serverTimestamp());
        user.put("datemodified", FieldValue.serverTimestamp());

        db.collection("driver").document(user_id)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }
}
