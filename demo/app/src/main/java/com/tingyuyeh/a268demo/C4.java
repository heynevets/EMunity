package com.tingyuyeh.a268demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tingyuyeh.a268demo.models.Callback;
import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.NavigationHelper;
import com.tingyuyeh.a268demo.models.Problem;
import com.tingyuyeh.a268demo.models.User;

import java.io.IOException;

public class C4 extends AppCompatActivity {

    String problemId;
    Problem problem;
    ImageButton button_up;
    ImageButton button_down;
    TextView text_title;
    TextView text_description;
    TextView text_vote;
    ImageView image;

    Button button_action;
    final String DEBUG = "C4";

    ImageButton button_favourite;


    ImageView header_profile_image;
    TextView header_email_text;

    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c4test);

        button_up = findViewById(R.id.button_up);
        button_down = findViewById(R.id.button_down);
//        button_down.setBackgroundDrawable(getResources().getDrawable(R.drawable.handle_button_change));

        text_title = findViewById(R.id.textView_title);
        text_description = findViewById(R.id.textView_description);
        text_vote = findViewById(R.id.textView_vote);

        image = findViewById(R.id.imageView);

        problemId = getIntent().getStringExtra("problemId");
        problem = FirebaseHelper.getInstance().getProblem(problemId);

        button_action = findViewById(R.id.button_action);

        button_favourite = findViewById(R.id.button_favourite);



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
                C4.this,
                navigationView,
                header_profile_image,
                header_email_text,
                getApplicationContext()
        );



//
//        drawer = findViewById(R.id.drawer_layout);
//        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(C4.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//
//        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        drawer = findViewById(R.id.drawer_layout);

    }

    @Override
    protected void onStart() {
        super.onStart();

        updateVoteButtons();
        button_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseHelper.getInstance().increaseVote(problem)) {
                    Toast.makeText(C4.this, "Up vote completed",
                            Toast.LENGTH_SHORT).show();
                    setVoteText();
                    updateVoteButtons();
                } else {
                    Toast.makeText(C4.this, "You can only up vote once for each problem",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        button_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseHelper.getInstance().decreaseVote(problem)) {
                    Toast.makeText(C4.this, "Down vote completed",
                            Toast.LENGTH_SHORT).show();
                    setVoteText();
                    updateVoteButtons();
                } else {
                    Toast.makeText(C4.this, "You can only down vote once for each problem",
                            Toast.LENGTH_SHORT).show();

                }

            }
        });

        text_title.setText(problem._title);
        text_description.setText(problem._description);
        setVoteText();

        FirebaseHelper.MyAsyncTask task = new FirebaseHelper.MyAsyncTask(new FirebaseHelper.MyAsyncTask.TaskListener() {
            @Override
            public void onFinished(Bitmap result) {
                image.setImageBitmap(result);
            }
        });
        task.execute(problem._imageUri);

        updateActionButton();
        updateFavouriteButton();


    }


    void updateVoteButtons() {
        User user = FirebaseHelper.getInstance().getUser();

        int voteStatus = 0;
        if (user._voteStatusForEachProblem.containsKey(problemId)) {
            voteStatus = user._voteStatusForEachProblem.get(problemId);
        }
        Log.d(DEBUG, "votestatus:" + voteStatus);
        if (voteStatus == 0) {
            button_up.setColorFilter(ContextCompat.getColor(this, R.color.colorText), PorterDuff.Mode.SRC_IN);
            button_down.setColorFilter(ContextCompat.getColor(this, R.color.colorText), PorterDuff.Mode.SRC_IN);
        } else if (voteStatus == -1) {
            button_up.setColorFilter(ContextCompat.getColor(this, R.color.colorText), PorterDuff.Mode.SRC_IN);
            button_down.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        } else {
            button_up.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            button_down.setColorFilter(ContextCompat.getColor(this, R.color.colorText), PorterDuff.Mode.SRC_IN);
        }
    }

    void updateFavouriteButton() {
        User user = FirebaseHelper.getInstance().getUser();
        if (user._idOfFavouriteProblems.contains(problem._problemId)) {
            // set yellow
            button_favourite.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            button_favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseHelper.getInstance().removeFavourite(problem);
                    updateFavouriteButton();
                }
            });
        } else {
            button_favourite.setColorFilter(ContextCompat.getColor(this, R.color.colorText), PorterDuff.Mode.SRC_IN);
            button_favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseHelper.getInstance().addFavourite(problem);
                    updateFavouriteButton();
                }
            });
        }

    }
    void updateActionButton() {
        User user = FirebaseHelper.getInstance().getUser();
        if (user._idOfCompletedProblems.contains(problem._problemId)) {
            setTaskAlreadyCompleteButton();
        } else {
            if (problem._problemId.equals(user._idOfActiveProblem)) {
                setButtonCompleteProblem();
            } else {
                setButtonAddActive();
            }
        }
    }
    void setTaskAlreadyCompleteButton() {
        button_action.setText("Completed");
        button_action.setEnabled(false);
    }
    void setButtonAddActive() {
        button_action.setText("Start Problem");
        button_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseHelper.getInstance().addActive(problem)) {
                    setButtonCompleteProblem();
                } else {
                    Toast.makeText(C4.this, "Please finish your current active task",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void setButtonCompleteProblem() {
        button_action.setText("Complete Problem");
        button_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper.getInstance().completeProblem(new Callback() {
                    @Override
                    public void onComplete(boolean success) {
                        if (success) {
                            setTaskAlreadyCompleteButton();
                        }
                    }
                });
            }
        });
    }

    void setVoteText() {
        String voting = (problem._ratings >= 0) ? "+" + problem._ratings : "-" + problem._ratings;
        text_vote.setText(voting);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }
}
