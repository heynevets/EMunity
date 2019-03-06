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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c0);
        lv = findViewById(R.id.dataListView);


        // create intent for C2
        Intent intent = new Intent(getApplicationContext(), C3.class);
        //intent.putExtra("message", "C0");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.nothing);






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


    private void zoomInProblem(int position) {
        List<Problem> problems = FirebaseHelper.getInstance().getAllProblems();
        Problem p = problems.get(position);

        LatLng coord = new LatLng(p._GPS.get(0), p._GPS.get(1));

        markerMap.get(p._problemId).showInfoWindow();



        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 10));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 12));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Problem p = (Problem) marker.getTag();
        int position = listAdapter.getItemPosition(p._problemId);
        marker.showInfoWindow();

        Log.d(DEBUG, "scroll to : " + position);

        zoomInProblem(position);

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
