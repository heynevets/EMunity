package com.tingyuyeh.a268demo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.tingyuyeh.a268demo.models.Callback;
import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.NavigationHelper;
import com.tingyuyeh.a268demo.models.Problem;
import com.tingyuyeh.a268demo.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class C0 extends AppCompatActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener {
    FirebaseUser user;
    String DEBUG = "C0";
    ListView lv;
    ProblemList listAdapter;

    Map<String, Marker> markerMap = new HashMap<>();


// from sneha

    private GoogleMap mMap;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mProblems;
    Marker marker;
    List<Problem> problemList;

    ImageView header_profile_image;
    TextView header_email_text;

    private DrawerLayout drawer;

    GPS_Tracker gps;
    boolean gpsInitialized = false;

    private Marker userLocation = null;

    ImageButton button_currentLocation;

//    NestedScrollView nestedScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c0);

//        nestedScrollView = findViewById(R.id.nestedScrollView);

        lv = findViewById(R.id.dataListView);
        lv.setVerticalScrollBarEnabled(false);


        button_currentLocation = findViewById(R.id.button_currentLocation);
        button_currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation.getPosition(), 12));
                } else {
                    Toast.makeText(C0.this, "GPS not ready yet",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



//        FirebaseHelper.getInstance().getAllProblems(new Callback() {
//            @Override
//            public void onSuccess(List<Problem> problems) {
//
//            }
//        });
        List<Problem> problems = FirebaseHelper.getInstance().getAllProblems();
        listAdapter = new ProblemList (C0.this, problems);
        FirebaseHelper.getInstance().registerProblemListener(listAdapter);
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(DEBUG, "currentPos:" + position);
                if (listAdapter.getItemSelected() != null && listAdapter.getItemSelected().equals(position)) {
                    Intent myIntent = new Intent(C0.this, C4.class);
                    myIntent.putExtra("problemId", problems.get(position)._problemId);
                    overridePendingTransition(R.anim.fade_in, R.anim.nothing);
                    startActivity(myIntent);
                } else {
                    zoomInProblem(position);
                }

            }
        });

//        ViewGroup.LayoutParams par = lv.getLayoutParams();
//        float pixels =  C0.this.getResources().getDisplayMetrics().density;
//        par.height = (int) (pixels * 80.0 * (problems.size()+1));
//        lv.setLayoutParams(par);
//        lv.requestLayout();

        // from sneha

// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ChildEventListener mChildEventListener;
        mProblems= FirebaseDatabase.getInstance().getReference("GPS");
        // mProblems.push().setValue(marker);
        problemList = new ArrayList<>();



        // gps
        gps = new GPS_Tracker(C0.this, new Callback() {
            @Override
            public void gpsLocationChange(Location location) {
                super.gpsLocationChange(location);
                if (!gpsInitialized) {
                    gpsInitialized = true;
                    LatLng templatlng = new LatLng(location.getLatitude(), location.getLongitude());
                    userLocation = mMap.addMarker(new MarkerOptions()
                            .title("You Are Here")
                            .position(templatlng)
                            .anchor(0.5f, 1)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(templatlng, 12));

                    listAdapter.updateUserLocation(location);
                    listAdapter.notifyDataSetChanged();
                } else {
                    LatLng coord = new LatLng(location.getLatitude(), location.getLongitude());
                    userLocation.setPosition(coord);

                }
            }
        });


                // navbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);

        header_profile_image = headerLayout.findViewById(R.id.header_profile_image);
        header_email_text = headerLayout.findViewById(R.id.header_email_text);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationHelper.buildNavigation(drawer,
                C0.this,
                navigationView,
                header_profile_image,
                header_email_text,
                getApplicationContext()
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void zoomInProblem(int position) {
        List<Problem> problems = FirebaseHelper.getInstance().getAllProblems();
        Problem p = problems.get(position);

        LatLng coord = new LatLng(p._GPS.get(0), p._GPS.get(1));

        markerMap.get(p._problemId).showInfoWindow();



        mMap.moveCamera(CameraUpdateFactory.newLatLng(coord));
//        nestedScrollView.smoothScrollTo(0, position);

        lv.smoothScrollToPosition(position);

        listAdapter.setItemSelected(position);
        listAdapter.notifyDataSetChanged();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        List<Problem> lp = FirebaseHelper.getInstance().getAllProblems();

        LatLng coord = new LatLng(37.3496, 121.9390);
        for (Problem p : lp) {
            coord = new LatLng(p._GPS.get(0), p._GPS.get(1));
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(coord)
                    .title(p._title)
                    .snippet(String.format("%d people voted", p._ratings))
                    .anchor(0.5f, 1)
            );
            marker.setTag(p);
            markerMap.put(p._problemId, marker);
        }



    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getTag() != null) {
            Problem p = (Problem) marker.getTag();
            int position = listAdapter.getItemPosition(p._problemId);
            Log.d(DEBUG, "scroll to : " + position);
            zoomInProblem(position);
        }
        marker.showInfoWindow();

        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();

        }
    }
}
