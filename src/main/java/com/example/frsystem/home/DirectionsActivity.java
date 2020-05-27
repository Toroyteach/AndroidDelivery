package com.example.frsystem.home;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.frsystem.Auth.AuthActivity;
import com.example.frsystem.NetworkConnectivity.InternetConnectivityReceiver;
import com.example.frsystem.R;
import com.example.frsystem.Stats.BargraphActivity;
import com.example.frsystem.home.GeoFence.Constants;
import com.example.frsystem.home.GeoFence.GeoFenceReceiver;
import com.example.frsystem.home.GeoFence.GeofenceErrorMessages;
import com.example.frsystem.home.LocationService.LocationUpdatesService;
import com.example.frsystem.home.LocationService.Utils;
import com.example.frsystem.settings.SettingsActivity;
import com.example.frsystem.ui.orderslider.OrderSliderActivity;
import com.example.frsystem.ui.orderslider.activejobs.Activejobsclass;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import io.grpc.android.BuildConfig;

/*
 *  the main activity called by the main thread when launched
 */

public class DirectionsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        TaskLoadedCallback,
        OnCompleteListener<Void>,
        GeoQueryEventListener{

    SwitchButton switchButton;
    private LinearLayout customerdisplayinfol;

    private LinearLayout showjobstatecount, callbtn;
    private Button startnav;
    private TextView Tusername;
    private TextView Tdistance;
    private TextView Tlocation;
    private TextView Temail;
    private TextView Tnumber;
    @SuppressLint("StaticFieldLeak")
    private static TextView Tjobscounter;
    private ImageView userpopup, ImgCallbtn, peekimg, driverimg;
    private LatLng mycurrentlatlang;
    private static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String TAG = "MyActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private DrawerLayout drawerLayoutl;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvView;
    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private Context mContext;

    private MyReceiver myReceiver;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("location");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomTheme);
        setContentView(R.layout.activity_directions);

        //firestore reference to point your jobs.
        initializefirestoreref();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        initializeUi();

        myReceiver = new MyReceiver();// instantiates the broadcast receiver start receiving location updates. the starts delivery method is called
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //initializeGeoFence();
    }

    public void initializeUi(){
        //drawer layout of the activity
        drawerLayoutl = findViewById(R.id.activity_directions);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayoutl, R.string.open, R.string.close);
        drawerLayoutl.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        nvView = findViewById(R.id.nav);
        nvView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //on navigation item selected calls a switch method that returns the intent representeed by the name of the nav item.
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        drawerLayoutl.closeDrawer(GravityCompat.START);
                        Log.d(TAG, "url home");
                        //Intent intent = new Intent(DirectionsActivity.this, NavActivity.class);
                        //startActivity(intent);
                        //finish();
                        break;
                    case R.id.nav_gallery:
                        drawerLayoutl.closeDrawer(GravityCompat.START);
                        Log.d(TAG, "url orders");
                        Intent intent2 = new Intent(DirectionsActivity.this, OrderSliderActivity.class);
                        startActivity(intent2);
                        //finish();
                        break;
                    case R.id.nav_slideshow:
                        drawerLayoutl.closeDrawer(GravityCompat.START);
                        Log.d(TAG, "url settings");
                        Intent intent3 = new Intent(DirectionsActivity.this, SettingsActivity.class);
                        startActivity(intent3);
                        //finish();
                        break;
                    case R.id.nav_Statistics:
                        drawerLayoutl.closeDrawer(GravityCompat.START);
                        Log.d(TAG, "url bargraph");
                        Intent intent4 = new Intent(DirectionsActivity.this, BargraphActivity.class);
                        startActivity(intent4);
                        //finish();
                        break;
                    case R.id.nav_logout:
                        drawerLayoutl.closeDrawer(GravityCompat.START);
                        Log.d(TAG, "url bargraph");
                        Toast.makeText(DirectionsActivity.this, "logout", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }
                return false;
            }

        });

        //activates driver location and gets jobs on queue or allows driver ro start navigation
        Tjobscounter = findViewById(R.id.textviewjobscounter);
        customerdisplayinfol = findViewById(R.id.userinfodisplaylayout);
        switchButton = findViewById(R.id.switch_button);
        activatejobswtch();

        ImgCallbtn = findViewById(R.id.callbutton);
        showjobstatecount = findViewById(R.id.imgshowjobstate);
        callbtn = findViewById(R.id.callbuttonlayout);

        //start navigation with data from queue
        startnav = findViewById(R.id.buttonstartnavigation);
        mContext = getApplicationContext();
        startnavbtn();

        //show next items on que(user info window(slideup_popup)) peek
        Tusername = findViewById(R.id.nametextViewuserwindow);
        Tdistance = findViewById(R.id.distancetextViewuserwindow);
        Tlocation = findViewById(R.id.locationtextViewuserwindow);
        Tnumber = findViewById(R.id.numbertextViewuserwindow);
        Temail = findViewById(R.id.emailtextViewuserwindow);
        peekimg = findViewById(R.id.peek_image);
        userpopup = findViewById(R.id.nextquejob);
        userpopup();
    }

    public void initializefirestoreref(){
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(DirectionsActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        //firebase reference
        mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getUid();

        orderjobsupdate = db.collection("orders");
        ordersref = db.collection("driver");
        driverref = ordersref.document(uid);
        jobsref = driverref.collection("jobs");
        rogueref = db.collection("roque");
        geoFire = new GeoFire(myRef);

        loadDriverdetails(uid);//loads the current active driver details. eg profile image
    }

    private void loadDriverdetails(String uid) {

        //load active driver image
        driverimg = findViewById(R.id.direction_profile_image);

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
                    Log.d(TAG, "Cached document data: " + document.get("profilepicuri"));
                    String profileuri = document.get("profilepicuri").toString();
                    Picasso.get().load(profileuri).resize(250, 400).error(R.drawable.ic_account_user).into(driverimg);
                } else {
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });

    }

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;
    // Tracks the bound state of the service.
    private boolean mBound = false;
    // Monitors the state of the connection to the service. for the location updates
    private boolean JobActive = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            LatLng geoPoint = null;
            if (location != null) {
                //Toast.makeText(DirectionsActivity.this, Utils.getLocationText(location), Toast.LENGTH_SHORT).show();
                if(!JobActive){
                    return;
                } else {
                    double lat  = location.getLatitude();
                    double lang = location.getLongitude();
                    float bearing = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        bearing = location.getBearing();
                    }
                    geoPoint = new LatLng(lat, lang);
                    changecamerafordeliveryDirection(geoPoint, bearing);
                    geoFire.setLocation("YOU", new GeoLocation(lat, lang));
                }
            }
        }
    }

    //Geo fencing to determine contact with client
    //geofence and necessary variable start here
    //till line 449 is setting up geofence.NOT WORKING
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GeofencingClient mGeofencingClient;
    private enum PendingGeofenceTask {ADD, REMOVE, NONE}
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
    public void initializeGeoFence(){

        mGeofenceList = new ArrayList<>();
        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;
        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();
        mGeofencingClient = LocationServices.getGeofencingClient(this);
    }
    private void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, Constants.GEOFENCE_RADIUS_IN_METERS)

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(this);
    }
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.GEOFENCES_ADDED_KEY, false);
    }
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constants.GEOFENCES_ADDED_KEY, added).apply();
    }
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeoFenceReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }
    //Performs the geofencing task that was pending until location permission was granted.
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());

            int messageId = getGeofencesAdded() ? R.string.geofences_added : R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }


    //check to ensure users did not revoke permission on system settings
    private void RecheckPermission(){
        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
    }

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    //Requests permission from the user to access c
    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(findViewById(R.id.activity_directions), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(DirectionsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    }).show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(DirectionsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    //on permission result check from the users
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                //setButtonsState(false);
                Snackbar.make(findViewById(R.id.activity_directions), R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }


    //this method that gets number and enables the driver to call the client if need be
    private int phonenumber = 0;
    public void Callclient(int number){
        String incall = String.valueOf(number);
        String tel = "tel:0"+incall;

        if(incall == null ){
            ImgCallbtn.setEnabled(false);
            return;
        } else {

            ImgCallbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(tel));
                    startActivity(intent);
                }
            });
        }
    }

    private boolean JobStatus = false; // checks and monitors the Drivers Active State
    public void activatejobswtch(){
        RecheckPermission();
        performPendingGeofenceTask();
        AddJobs();
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    if (queueJobs.isEmpty()) {
                        Toast.makeText(getApplication(), "Sorry NO Jobs Available",Toast.LENGTH_SHORT).show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switchButton.setChecked(false);
                            }
                        },3000);
                        return;
                    } else {
                        Toast.makeText(getApplication(), "Jobs count "+queueJobs.size(),Toast.LENGTH_SHORT).show();
                        Tjobscounter.setVisibility(View.VISIBLE);
                        Tjobscounter.setText(String.valueOf(queueJobs.size()));
                        displaypopupuserinfo("up");
                        getcurrentlocation();
                    }
                }else{
                    customerdisplayinfol.setVisibility(View.GONE);
                    Tjobscounter.setVisibility(View.GONE);
                    mMap.clear();
                }
            }
        });
    } // enables the driver to become online or not
    public void startnavbtn(){
        startnav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaypopupuserinfo("down");
                startDelivery();
                startnav.setEnabled(false);
            }
        });
    } // on click of start delivery button
    public void userpopup(){
        userpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(switchButton.isChecked()){
                    //check if queues list is empty or if jobstatus is true
                    if (queueJobs.isEmpty() && !JobStatus) {
                        Toast.makeText(getApplication(), "No More Jobs At the moment", Toast.LENGTH_SHORT).show();
                    } else {
                        //animates user pop with visibility
                        displaypopupuserinfo("up");
                        Toast.makeText(getApplication(), queueJobs.size()+" more Job in Que", Toast.LENGTH_SHORT).show();

                        //delay disappering of the user popup info
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                displaypopupuserinfo("down");
                            }
                        }, 10000);
                    }
                } else {
                    return;
                }

            }
        });
    } // displays user popup to peek on the currectn active job or the next on the que

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ordersref, jobsref, orderjobsupdate, rogueref;
    private DocumentReference driverref;
    private LatLng clientposition;
    private void startDelivery(){

          if (!checkPermissions()) {
                requestPermissions();
                return;
            } else {
                this.JobActive = true;
                mService.requestLocationUpdates(); // starts the location update service
                //addGeofences();
            }

        final Map<String, Object> data = new HashMap<>();
        String uid = mAuth.getUid();
        data.put("driverallocated", uid);
        Log.d(TAG, queueJobs.size()+" ******Size of queue before poll");
        Activejobsclass activejob = queueJobs.poll(); // gets the first priority job and populates activejobsclass
        Tusername.setText(activejob.getClientusername());// userinfo popup is populated with details of th priority job.
        Tdistance.setText(activejob.getClientusername());
        Tlocation.setText(activejob.getClientusername());
        Tnumber.setText(String.valueOf(activejob.getClientnumber()));
        Temail.setText(activejob.getClientemail());
        phonenumber = activejob.getClientnumber();
        Callclient(phonenumber); // call client method to pass number to all the driver to call the client
        showjobstatecount.setVisibility(View.VISIBLE);
        ImgCallbtn.setEnabled(true);

        this.clientposition = new LatLng(activejob.getClientlatlang().getLatitude(),activejob.getClientlatlang().getLongitude());
        setCircle(clientposition);//sets circle to help determine destination location. adds a custom geofence default the circle size. to help with complete delivery
        LatLng pointB = new LatLng(clientposition.latitude,clientposition.longitude);
        this.jobrefid = activejob.getId();

        orderjobsupdate.document(activejob.getOrderid()).set(data, SetOptions.merge());
        DocumentReference doc = jobsref.document(activejob.getId());
        doc.update("status","ACTIVE!!!");
        MarkerOptions destinationpoint = new MarkerOptions().position(new LatLng(pointB.latitude, pointB.longitude)).title("Destination");
        mMap.addMarker(destinationpoint); //adds the destination point on map
        Log.d(TAG, "startDelivery:Point A lat"+mycurrentlatlang.latitude+"*** lang"+mycurrentlatlang.longitude+" **Point B lat"+pointB.latitude+" lang"+pointB.longitude);
        new FetchURL(DirectionsActivity.this).execute(getUrl(mycurrentlatlang, pointB), "driving"); // gets route details from google and populates the mao with polyline
        switchButton.setEnabled(false); // make the button unuseable since driver should dedicate to job
        Log.d(TAG, queueJobs.size()+" ******Size of queue after poll");
        Tjobscounter.setText(String.valueOf(queueJobs.size()));
        this.JobStatus = true;
    } //this the method that handles starting delivery. gets data from priority, updates database, fetch directions url, sets up geofence

    //method called when user reaches the geopoint location. completes rides updates database
    private String jobrefid;
    private void completeDelivery(){

        if(jobrefid == null){
            Log.d(TAG, " error finishing job. Job Ref id is empty");
            return;
        }

        DocumentReference doc = jobsref.document(jobrefid);

        String uid = mAuth.getUid();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set a title for alert dialog
        builder.setTitle("Complete task.");

        // Ask the final question
        builder.setMessage("Job Completed Successfully.");

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMap.clear();
                mService.removeLocationUpdates();
                //removeGeofences();
                clearuserpopup();
                ordersref.document(uid).update("completedjobs", FieldValue.increment(1));
                doc.update("status","COMPLETED");
                peekonqueue();
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
    } // methos is called when user leaves the goefence area. signalling completion of job

    //clears the user popup after job is complete
    public void clearuserpopup(){
        switchButton.setEnabled(true);
        mMap.clear();
        Tusername.setText("");
        Tdistance.setText("");
        Tlocation.setText("");
        Tnumber.setText("");
        Temail.setText("");
        phonenumber = 0;
        this.JobStatus = false;
        startnav.setEnabled(true);
        ImgCallbtn.setEnabled(false);
    }

    // set the cirld(remedy to geofence) to help determine the location of the user in map. this the Goefence am using and working
    private GeoFire geoFire;
    public void setCircle(LatLng circlepoint){
        if(circlepoint != null){
            double lat = circlepoint.latitude;
            double lang = circlepoint.longitude;
            Circle circle = mMap.addCircle(new CircleOptions().center(new LatLng(lat, lang)).radius(100).strokeColor(R.color.mygray).fillColor(R.color.subtitle_dark));

            //Log.d(TAG, circlepoint.latitude()+" lat "+circlepoint.longitude()+" long");
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat,lang),0.1f);//500m
            geoQuery.addGeoQueryEventListener(DirectionsActivity.this);
        }
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.d(TAG, " geofence enter ");
        Toast.makeText(getApplication(), "You have entered the Delivery zone ",Toast.LENGTH_SHORT).show();
    } // when driver enters the area

    @Override
    public void onKeyExited(String key) {
        Log.d(TAG, " geofence exit ");
        Toast.makeText(getApplication(), "Exiting Delivery zone ",Toast.LENGTH_SHORT).show();
        completeDelivery();
    } // when the driver leaves the area

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.d(TAG, " geofence dwell ");
        Toast.makeText(getApplication(), "You are inside the delivery Zone",Toast.LENGTH_SHORT).show();
    } //when the driver dwell inside the area

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Toast.makeText(getApplication(), "geofence error "+error.getMessage(),Toast.LENGTH_SHORT).show();
        Log.d(TAG, " geofence error "+error.getMessage());

    }

    //peek on the queue to display next active jobs
    public void peekonqueue(){
        if (queueJobs.isEmpty()) {
            Toast.makeText(getApplication(), "no jobs",Toast.LENGTH_SHORT).show();
            switchButton.setChecked(false);
            return;
        } else {
            Activejobsclass jobsque = queueJobs.peek();
            Tusername.setText(jobsque.getClientusername());
            Tdistance.setText(jobsque.getClientusername());
            Tlocation.setText(jobsque.getClientusername());
            Tnumber.setText(String.valueOf(jobsque.getClientnumber()));
            Temail.setText(jobsque.getClientemail());
            phonenumber = jobsque.getClientnumber();
            //Glide.with(getApplicationContext()).load(jobsque.getClientprofilepicuri()).into(peekimg);
            Picasso.get().load(jobsque.getClientprofilepicuri()).resize(250, 400).error(R.drawable.ic_account_user).into(peekimg);
            Callclient(phonenumber);
        }
    }

    //animate slide up/down of userpop up info
    public void displaypopupuserinfo(String direction){
        ObjectAnimator animation, inJobanimation;

        switch(direction) {
            case "up":
                    animation = ObjectAnimator.ofFloat(customerdisplayinfol, "translationY", -600f);
                    animation.setDuration(1000);
                    animation.start();
                    //run peek on the queue
                    peekonqueue();
                    customerdisplayinfol.setVisibility(View.VISIBLE);
                break;

            case "down":
                    animation = ObjectAnimator.ofFloat(customerdisplayinfol, "translationY", 100f);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animation.setDuration(10000);
                            animation.start();
                        }
                    },3000);
                break;
        }
    }

    //queue that holds jobs selected and the next job to be excecuted
    private static PriorityQueue<Activejobsclass> queueJobs = new PriorityQueue<Activejobsclass>();
    //private static int prioritycount = 0; deletion
    public void AddJobs(){
        List<Activejobsclass> firebaselist = new ArrayList<Activejobsclass>();
        List<Object> quelist = new ArrayList<>();

        jobsref.whereEqualTo("status","active_pending").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            //only when job is added is when its added to list
                            Activejobsclass newjob = dc.getDocument().toObject(Activejobsclass.class);
                            queueJobs.add(newjob);
                            Toast.makeText(getApplication(), "New Jobs Added",Toast.LENGTH_SHORT).show();
                            break;
                        case MODIFIED:
                            //Toast.makeText(getApplication(), "Jobs Modified",Toast.LENGTH_SHORT).show();
                            break;
                        case REMOVED:
                            //Toast.makeText(getApplication(), "Jobs Removed Successfully",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

            }
        });
    }

    //gets active jobs counter and displays on user. same method is called on resume of activity
    private void getjobscounter(){
        if(queueJobs.isEmpty()){
            Tjobscounter.setText("0");
        } else {
            Tjobscounter.setText(String.valueOf(queueJobs.size()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskDone(Object... values) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraPosition googlePlex = CameraPosition.builder().target(new LatLng(0,17.0)).zoom(1).tilt(0).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
        enableMyLocation();
    }

    //adds polyline to the map from another class
    public static void addpolyline(PolylineOptions polyline){
        mMap.addPolyline(polyline);
    }

    //active user location awarenes by checking if permission is granted
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(DirectionsActivity.this), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }

    public void getcurrentlocation(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();
                    loaduserlocation(latitude, longitude);
                    mycurrentlatlang = new LatLng(latitude,longitude);
                }

                //error geting location
            }
        });
    } //gets the cuurent locations of the driver and uses it as Point A of the route directions

    //gets user latlang  moves the camera to that position and adds marker to represent your location
    public void loaduserlocation(double lat, double lang){
             //take camera to user location
             CameraPosition googlePlex = CameraPosition.builder().target(new LatLng(lat,lang)).zoom(15).bearing(0).tilt(45).build();
             mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null);
             MarkerOptions userplace = new MarkerOptions().position(new LatLng(lat, lang)).title("Your Here!");
             mMap.addMarker(userplace);
    }

    public void changecamerafordeliveryDirection(LatLng geoPoint, float bearing){
        double lat = geoPoint.latitude;
        double lang = geoPoint.longitude;
        CameraPosition googlePlex = CameraPosition.builder().target(new LatLng(lat,lang)).zoom(20).bearing(bearing).tilt(60).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
    } // change camera every 2 seconds depending on the location service updates to display driver motion

    //gets the url of the Pont A and Point B of hte route the driver is to follow to the destionation
    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&";
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    //SystemsOverride methods and others that use them to operate
    private boolean connectionchecked = false;
    InternetConnectivityReceiver internetConnectivityReceiver = new InternetConnectivityReceiver();
    public void RegisterInternetConnectivityBroadcast(){
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if(connectionchecked){
            return;
        } else {
            registerReceiver(internetConnectivityReceiver, intentFilter);
            connectionchecked = true;
        }
    } // Connectivity manager connections to check if it was registered before and stop it from doing it again

    private void getlastDriverLocation(){
        // check if job was completed if not. get last location and flag the driver status.
        if(!JobStatus){

            return;
        } else {

            mAuth = FirebaseAuth.getInstance();
            final String uid = mAuth.getUid();
            flagdriverdetails(uid);
        }
    }
    private Map<String, Object> user = new HashMap<>();
    public void flagdriverdetails(String uid){
        String jobid = db.collection("roque").document().getId();
        double driverLongitude = 0;
        //broadcast receiver to receive the location updates from the system and call the change camera of the user.
        double driverLatitude = 0;
        GeoPoint lastdriverlocation = new GeoPoint(driverLatitude, driverLongitude);
        Activejobsclass activejob = new Activejobsclass();
        String orderidabandonded = activejob.getOrderid();
        String orderRef = activejob.getId();

        user.put("driveruid", uid);
        user.put("orderid_abandoned", orderidabandonded);
        user.put("dateabandoned", FieldValue.serverTimestamp());
        user.put("lastknonwlatlang", lastdriverlocation);
        db.collection("roque").document(jobid).set(user);
        ordersref.document(uid).update("incompletejobs", FieldValue.increment(1));
        jobsref.document(orderRef).update("status","ABANDONED");
        Log.d(TAG, "flagdriverdetails: successful");
        Log.d(TAG, "flagdriverdetails:uid= "+uid+" orderid= "+orderidabandonded+" ");
    }

    private void stopInternetConnectivityUpdates() {
        unregisterReceiver(internetConnectivityReceiver);
    } // stop the connectivity manager from the onDestroy method of the activity

    @Override
    public void onResume() {
        super.onResume();
        getjobscounter();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    protected void onStart() {
        super.onStart();
        RegisterInternetConnectivityBroadcast();
        //PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopInternetConnectivityUpdates();
        //getlastDriverLocation();
    }

}
