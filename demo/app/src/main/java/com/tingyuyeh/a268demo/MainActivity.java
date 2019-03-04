package com.tingyuyeh.a268demo;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

//added by Sneha on 03/04
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//added by Sneha

import com.tingyuyeh.a268demo.models.FirebaseHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

???        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        Intent i=new Intent(MainActivity.this,MapsActivity.class);
        startActivity(i);

        Thread timer= new Thread()
        {
            public void run()
            {
                try
                {
                    //Display for 3 seconds
                    sleep(1500);
                }
                catch (InterruptedException e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                finally
                {
                    //Goes to Activity  StartingPoint.java(STARTINGPOINT)
                    Intent intent = new Intent(getApplicationContext(), A1.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.nothing);

                }
            }
        };
        timer.start();


    }
    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        finish();

    }

}
