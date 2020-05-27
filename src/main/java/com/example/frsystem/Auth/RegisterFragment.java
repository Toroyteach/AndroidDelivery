package com.example.frsystem.Auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.frsystem.R;
import com.example.frsystem.home.DirectionsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = "FIRESTOREFIREBASE";
    private EditText mEmail, mPassword,  mRePassword, mfullname, mNumber;
    private Button bregister;

    private ProgressBar progressBar;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> user = new HashMap<>();

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String currentdatetime;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(getActivity(), DirectionsActivity.class);
                    startActivity(intent);
                    return;
                }
            }
        };

        mfullname = (EditText) view.findViewById(R.id.et_name);
        mNumber = (EditText) view.findViewById(R.id.et_number);
        mEmail = (EditText) view.findViewById(R.id.et_email);
        mPassword = (EditText) view.findViewById(R.id.et_password);
        mRePassword = (EditText) view.findViewById(R.id.et_repassword);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarregister);

        bregister = (Button) view.findViewById(R.id.btn_register);

        currentdatetime = formatter.format(date);

        bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public void signup(){

        final String name = mfullname.getText().toString();
        final String phonenumber =  mNumber.getText().toString();//Integer.parseInt(mNumber.getText().toString());
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        final String passwordRecheck = mRePassword.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phonenumber) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordRecheck)) {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(getActivity(), "Password too short, enter minimum 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password != passwordRecheck ) {
            Toast.makeText(getActivity(), "Your Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        final int number = Integer.parseInt(phonenumber);


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "sign up error", Toast.LENGTH_SHORT).show();
                }else{
                    String user_id = mAuth.getCurrentUser().getUid();
                    //DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("name");
                    //current_user_db.setValue(email);
                    insertfirestore(user_id, currentdatetime, currentdatetime, number, name, email);
                    progressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(getActivity(), DirectionsActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                }
            }
        });

    }

    //inserts to firestore as a copy of records
    public void insertfirestore(String user_id, String datecreated, String datemodified, int number, String username, String email){

        user.put("datecreated", datecreated);
        user.put("datemodified", datemodified);
        user.put("email", email);
        user.put("number", number);
        user.put("username", username);
        user.put("uId", user_id);

        db.collection("driver").document(user_id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
