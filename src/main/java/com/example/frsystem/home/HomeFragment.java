package com.example.frsystem.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.frsystem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

public class HomeFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, TaskLoadedCallback {


    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String TAG = "MyActivity";
    private static final LatLng tumaini = new LatLng(-1.397782, 36.757185);
    private static double deliverylat;
    private static double deliveryLang;
    private Polyline currentPolyline;
    private Button getDirection;
    private MarkerOptions place1, place2;

    private Button showpopup;
    private PopupWindow mPopupWindow;
    private RelativeLayout popupRelativeLayout;

    private Switch mActiveSwitch;
    private RelativeLayout mCustomerInfo;
    private Button prevuserinfobtn, startdeliverybtn, nextdeliverybtn;
    private Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        prevuserinfobtn = rootView.findViewById(R.id.buttonpreviousinfo);
        startdeliverybtn = rootView.findViewById(R.id.buttonstartdelivery);
        nextdeliverybtn = rootView.findViewById(R.id.buttonextinfo);
        mCustomerInfo = rootView.findViewById(R.id.userinfo);
        mActiveSwitch = rootView.findViewById(R.id.switch1);
        //cv1 = rootView.findViewById(R.id.cv_feature1);


        return rootView;
    }

    private void getAssignedCustomerInfo(){
        mCustomerInfo.setVisibility(View.VISIBLE);
    }

    private CardView cv1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        place1 = new MarkerOptions().position(new LatLng(-1.394681, 36.764562)).title("Delivery 1");
        place2 = new MarkerOptions().position(new LatLng(-1.390959, 36.737295)).title("Delivery 2");

        //getDirection = view.findViewById(R.id.btnGetDirection);
        //getDirection.setOnClickListener(new View.OnClickListener() {
          //  @Override
          //  public void onClick(View view) {
          //      new FetchURL(getActivity()).execute(getUrl(place1.getPosition(), place2.getPosition()), "driving");
          //      Log.d("mylog", "get direction button clicked");
          //  }
        //});
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getDriverLocation();

        mActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    getAssignedCustomerInfo();
                }else{
                    mCustomerInfo.setVisibility(View.GONE);
                }
            }
        });

        nextdeliverybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("mylog", "next button clicked");
                new FetchURL(mContext).execute(getUrl(place1.getPosition(), place2.getPosition()), "driving");
                //dialog = new ActionDialog(R.string.title_no_location_dialog, R.string.subtitle_no_location_dialog, R.string.understood_label, getActivity());
                //dialog.showDialog();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public void getDriverLocation(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            deliverylat = location.getLatitude();
                            deliveryLang = location.getLongitude();

                            //String Latlang = Double.toString(lat)+Double.toString(lang);
                            //Log.d(TAG, Latlang.toString());
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        mMap.addMarker(place1);
        mMap.addMarker(place2);
    }

    AlertDialog.Builder builder;
    public void checkrider(){

    }

    @Override
    public void onTaskDone(Object... values) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    public void drawpoline(){
        //get user location the display on camera
        CameraPosition googlePlex = CameraPosition.builder().target(new LatLng(-1.395562,36.752114)).zoom(6).bearing(0).tilt(45).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + "driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);

            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission((AppCompatActivity) requireActivity(), LOCATION_PERMISSION_REQUEST_CODE,Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }
}
