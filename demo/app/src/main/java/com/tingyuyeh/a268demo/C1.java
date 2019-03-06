package com.tingyuyeh.a268demo;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.tingyuyeh.a268demo.models.Callback;
import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.NavigationHelper;
import com.tingyuyeh.a268demo.models.Problem;

import java.util.ArrayList;
import java.util.List;

public class C1 extends AppCompatActivity {

    TextView text_active;
    TextView text_favourite;
    TextView text_complete;

    ListView lv_active;
    ListView lv_favourite;
    ListView lv_complete;

    ProblemList la_active;
    ProblemList la_favourite;
    ProblemList la_complete;

    List<Problem> lp_active;
    List<Problem> lp_favourite;
    List<Problem> lp_complete;

    TextView backgroundText;

    ImageView header_profile_image;
    TextView header_email_text;

    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c1);

    }

    @Override
    protected void onStart() {
        super.onStart();
        text_active = findViewById(R.id.text_active);
        text_favourite = findViewById(R.id.text_favourite);
        text_complete = findViewById(R.id.text_complete);

        lv_active = findViewById(R.id.listview_active);
        lv_active.setVerticalScrollBarEnabled(false);
        lv_favourite = findViewById(R.id.listview_favourite);
        lv_favourite.setVerticalScrollBarEnabled(false);
        lv_complete = findViewById(R.id.listview_completed);
        lv_complete.setVerticalScrollBarEnabled(false);


        backgroundText = findViewById(R.id.backgroundText);



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
                C1.this,
                navigationView,
                header_profile_image,
                header_email_text,
                getApplicationContext()
        );



    }

    @Override
    protected void onResume() {
        super.onResume();
        lp_active = new ArrayList<>();
        if (FirebaseHelper.getInstance().getActiveProblem() != null) {
            lp_active.add(FirebaseHelper.getInstance().getActiveProblem());
        }
        lp_favourite = FirebaseHelper.getInstance().getFavouriteProblems();
        lp_complete = FirebaseHelper.getInstance().getCompletedProblems();

        if (lp_active.size() == 0
                && lp_favourite.size() == 0
                && lp_complete.size() == 0) {

            backgroundText.setVisibility(View.VISIBLE);
        } else {
            backgroundText.setVisibility(View.INVISIBLE);
        }


        initializeAdapter(lv_active, lp_active, la_active, text_active);
        initializeAdapter(lv_favourite, lp_favourite, la_favourite, text_favourite);
        initializeAdapter(lv_complete, lp_complete, la_complete, text_complete);
    }

    void initializeAdapter(ListView lv, List<Problem> lp, ProblemList la, TextView tv) {
        la = new ProblemList(C1.this, lp);
        lv.setAdapter(la);
        if (lp.size() == 0) {
            tv.setVisibility(View.INVISIBLE);
            tv.setHeight(0);
            ViewGroup.LayoutParams par = lv.getLayoutParams();
            par.height = 0;
            lv.setLayoutParams(par);
            lv.requestLayout();
        } else {
            float pixels =  C1.this.getResources().getDisplayMetrics().density;
            tv.setHeight((int) (pixels * 50.0));
            tv.setVisibility(View.VISIBLE);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(C1.this, C4.class);
                    intent.putExtra("problemId", lp.get(position)._problemId);
                    startActivity(intent);
                }
            });
            ViewGroup.LayoutParams par = lv.getLayoutParams();

            par.height = (int) (pixels * 80.0 * lp.size());
            lv.setLayoutParams(par);
            lv.requestLayout();
        }
    }
}

//
//
//
//
//        FirebaseHelper.getInstance().getAllProblems(new Callback() {
//        @Override
//        public void onSuccess(List<Problem > problems) {
//
//        }
//    });
//
//    // from sneha
//
//    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//            .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//    ChildEventListener mChildEventListener;
//    mProblems= FirebaseDatabase.getInstance().getReference("GPS");
//    // mProblems.push().setValue(marker);
//    problemList = new ArrayList<>();
//}
