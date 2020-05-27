package com.example.frsystem.ui.orderslider.activejobs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frsystem.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/*
 *  this fragment displays data from (own)selected jobs list from firestore and displays to user as active jobs.
 */


public class ActiveOrdersFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "sample";

    //firebase firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ordersref, jobsref;
    private DocumentReference driverref;
    private ActiveJobsAdapter orderAdapter;
    protected RecyclerView mRecyclerView;


    private FirebaseAuth mAuth;

    //popup
    Dialog popupDialog;
    Button showpop;
    private Activity ctx;

    Activejobsclass[] myListData;


    public ActiveOrdersFragment() {
        // Required empty public constructor
    }


    public static ActiveOrdersFragment newInstance(String param1, String param2) {
        ActiveOrdersFragment fragment = new ActiveOrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activejobs, container, false);

        ctx = getActivity();
        mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getUid();

        //firebase reference
        ordersref = db.collection("driver");
        driverref = ordersref.document(uid);
        jobsref = driverref.collection("jobs");

        Query query = jobsref.whereEqualTo("status","active_pending");
        FirestoreRecyclerOptions<Activejobsclass> options = new FirestoreRecyclerOptions.Builder<Activejobsclass>().setQuery(query, Activejobsclass.class).build();
        orderAdapter = new ActiveJobsAdapter(options, ctx);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewactiveorders);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(orderAdapter);


        //Log.d(TAG, "item was clicked");
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set page background

    }

    @Override
    public void onStart() {
        super.onStart();
        orderAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        orderAdapter.stopListening();
    }

}
