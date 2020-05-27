package com.example.frsystem.ui.orderslider.availablejobs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frsystem.R;
import com.example.frsystem.home.Jobscounterclass;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/*
 *  this fragment gets jobs posted on the database and avails them to the user to select as there own task as available jobs
 */

public class JobsFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "onitemclick";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;

    //firebase firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ordersref, jobscounter;
    private DocumentReference dbref;
    private AvailablejobsAdapter Adapter;
    private FirebaseAuth mAuth;
    private String uid;


    //popup
    Dialog popupDialog;
    Button showpop;
    private Activity ctx;

    Map<String, Object> user = new HashMap<>();

    public JobsFragment() {
        // Required empty public constructor
    }

    public static JobsFragment newInstance(String param1, String param2) {
        JobsFragment fragment = new JobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jobs, container, false);
        ctx = getActivity(); // context passed to the recycler adapter
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid(); //get user uid

        initializeFirebase();

        mRecyclerView = rootView.findViewById(R.id.recyclerViewjobs);
        getrecyclerview();

        return  rootView;

    }

    public void initializeFirebase(){
        jobscounter = db.collection("driver"); // reference to increment counter on database
        ordersref = db.collection("orders");
        dbref = db.collection("driver").document(uid);
    }

    public void getrecyclerview(){
        Query query = ordersref.whereEqualTo("status","AVAILABLE");
        FirestoreRecyclerOptions<Availablejobsclass> options = new FirestoreRecyclerOptions.Builder<Availablejobsclass>().setQuery(query, Availablejobsclass.class).build();//query builder
        Adapter = new AvailablejobsAdapter(options, ctx); // custom recycler adapter takes (Firebaserecycleroptions(class)) options and context(ctx)
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(Adapter);

        //adds a custom on item click listener on the recycler items
        //on item clicks shows popup snapshot of the data from availablejobsclass and on long item click ass to add to to active jobs or tasks
        Adapter.setOnClickListener(new AvailablejobsAdapter.ClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Availablejobsclass jobsdata = documentSnapshot.toObject(Availablejobsclass.class);
                String id = documentSnapshot.getId();
                Log.d(TAG, id + " clicked.");

                //gets and populates popupdialog
                final Dialog popupDialog = new Dialog(ctx);
                popupDialog.setContentView(R.layout.popupwindowuserinfo);
                final ImageView clientimage=  popupDialog.findViewById(R.id.profile_image_clientpopup);
                final TextView  clientname =  popupDialog.findViewById(R.id.popupwindowclientname);
                final TextView  clientnumber=  popupDialog.findViewById(R.id.popupwindowclientnumber);
                final TextView  clientlocation =  popupDialog.findViewById(R.id.popupwindowclientlocation);
                final TextView  clientdistance =  popupDialog.findViewById(R.id.popupwindowclientdistance);
                final TextView  clientordeid =  popupDialog.findViewById(R.id.popupwindowclientorderid);
                final TextView closebt = popupDialog.findViewById(R.id.textView4);

                clientname.setText(jobsdata.getClient_name());
                clientnumber.setText(String.valueOf(jobsdata.getClientnumber()));
                clientlocation.setText(jobsdata.getLocationName());
                clientordeid.setText(jobsdata.getOrder_id());
                popupDialog.show();

                closebt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupDialog.hide();
                    }
                });

                popupDialog.show();
            }

            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int position) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                // Set a title for alert dialog
                builder.setTitle("Choose task.");

                // Ask the final question
                builder.setMessage("Add this to active task.");

                // Set the alert dialog yes button click listener
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //availjobs gets data from firestore database of the user
                        Availablejobsclass availJobs = documentSnapshot.toObject(Availablejobsclass.class);
                        //itemid is used to get the specific snapshot
                        String itemid = documentSnapshot.getId();
                        insertjobstodriver(uid, itemid ,availJobs.getClient_name(),availJobs.clientnumber, availJobs.getClient_name(),availJobs.getDesc(), availJobs.getLatlang(), availJobs.getImageref());
                        Toast.makeText(ctx, "Job Added",Toast.LENGTH_SHORT).show();
                    }
                });

                // Set the alert dialog no button click listener
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();

            }
        });
    }

    private int prioritycount; // this method helps make a count for priority queue.
    public void getjobsQueuecount(){
        //this methods checks the number of active jobs then uses this to increment that fiqure to priority comparator in priorityQueue
        DocumentReference docRef = db.collection("driver").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Jobscounterclass count = document.toObject(Jobscounterclass.class);
                        prioritycount = count.getActivejobs();
                    } else {

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    //inserts new data to drivers database of the clients information
    public void insertjobstodriver(String uid, String itemid, String client_name, int clientnumber, String email, String desc, GeoPoint location, String picuri){

        String jobid = db.collection("jobs").document().getId();

        user.put("clientemail", email);
        user.put("clientnumber", clientnumber);
        user.put("clientusername", client_name);
        user.put("clientprofilepicuri", picuri);
        user.put("client_description", desc);
        user.put("orderid", itemid);
        user.put("clientlatlang", location);
        user.put("datecreated", FieldValue.serverTimestamp());
        user.put("datemodified", FieldValue.serverTimestamp());
        user.put("status","active_pending");
        user.put("id", jobid);
        user.put("priority", prioritycount+10);

        dbref.collection("jobs").document(jobid).set(user);
        dbref.update("activejobs", FieldValue.increment(1));

        //dbref.collection("jobs").add(user);
        ordersref.document(itemid).update("status","NOT AVAILABLE");
        getjobsQueuecount();

    }

    @Override
    public void onResume() {
        super.onResume();
        getjobsQueuecount();
    }

    @Override
    public void onStart() {
        super.onStart();
        Adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        Adapter.stopListening();
    }
}
