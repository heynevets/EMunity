package com.tingyuyeh.a268demo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class C0 extends AppCompatActivity {
    Button logOutButton;
    Button writeButton;
    private FirebaseAuth mAuth;
    TextView greeting;
    EditText dataField;

    FirebaseUser user;
    String DEBUG = "C0_Debug";

    ListView lv;
    DataList listAdapter;

    List<String> senders;
    List<String> msgs;

    FirebaseDatabase database;
    DatabaseReference myRef;


    void initializeDataList() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("TestData");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data value = dataSnapshot.getValue(Data.class);
                senders.add(value.sender);
                msgs.add(value.msg);
                listAdapter.notifyDataSetChanged();
                lv.setBackgroundResource(R.drawable.customshape);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        senders = new ArrayList<>();
        msgs = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c0);
        initializeDataList();

        greeting = findViewById(R.id.greetingText);
        lv = findViewById(R.id.dataListView);
        dataField = findViewById(R.id.textToUpload);
        writeButton = findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                writeToDatabase();
                hideSoftKeyboard(C0.this, v); // MainActivity is the name of the class and v is the View parameter used in the button listener method onClick.
            }
        });

        logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                logout();
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), A1.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.nothing);

            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
//        user = FirebaseAuth.getInstance().getCurrentUser();




        listAdapter = new DataList (C0.this, senders, msgs);

        lv.setAdapter(listAdapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

    }

    void writeToDatabase() {
        Log.i(DEBUG, "writing to db");


        String sender = user.getEmail();
        String msg = dataField.getText().toString();
        if (!msg.equals("")) {
            Data entry = new Data();
            entry.sender = sender;
            entry.msg = msg;
            myRef.push().setValue(entry);
            dataField.setText("");



        }
    }
    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (user != null) {
            String email = user.getEmail();
            greeting.setText("Hello, " + email);
            String uid = user.getUid();
        }





    }

    private void logout() {
        Log.i(DEBUG, "log out");
        mAuth.signOut();
    }




}
