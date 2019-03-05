package com.tingyuyeh.a268demo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import com.tingyuyeh.a268demo.models.Problem;
import com.tingyuyeh.a268demo.models.User;

import java.util.ArrayList;
import java.util.List;


public class C0 extends AppCompatActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener {
    FirebaseUser user;
    String DEBUG = "C0";
    ListView lv;
    ProblemList listAdapter;

// from sneha

    private GoogleMap mMap;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mProblems;
    Marker marker;
    List<Problem> problemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c0);
        lv = findViewById(R.id.dataListView);

        FirebaseHelper.getInstance().getAllProblems(new Callback() {
            @Override
            public void onSuccess(List<Problem> problems) {
            listAdapter = new ProblemList (C0.this, problems);
            FirebaseHelper.getInstance().registerProblemListener(listAdapter);
            lv.setAdapter(listAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(DEBUG, problems.get(position)._problemId);
                    Intent myIntent = new Intent(C0.this, C4.class);
                    myIntent.putExtra("problemId", problems.get(position)._problemId);
                    overridePendingTransition(R.anim.fade_in, R.anim.nothing);
                    startActivity(myIntent);
                }
            });
            }
        });

        // from sneha

// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ChildEventListener mChildEventListener;
        mProblems= FirebaseDatabase.getInstance().getReference("GPS");
        // mProblems.push().setValue(marker);
        problemList = new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//        mProblems.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot s : dataSnapshot.getChildren()){
//                    Problem p = s.getValue(Problem.class);
//
//                    problemList.add(p);
//                    for (int i = 0; i < problemList.size(); i++)
//                    {
//                        LatLng latLng = new LatLng(p._GPS.get(0),p._GPS.get(1));
//                        if (mMap != null) {
//                            mMap.addMarker(new MarkerOptions().position(latLng).title(p._title)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
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
}
