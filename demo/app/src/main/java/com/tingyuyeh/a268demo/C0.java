package com.tingyuyeh.a268demo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tingyuyeh.a268demo.models.Callback;
import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.Problem;
import com.tingyuyeh.a268demo.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
    String DEBUG = "C0";

    ListView lv;
    DataList listAdapter;

    List<String> senders;
    List<String> msgs;

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference userRef;
    DatabaseReference problemRef;

    private StorageReference mStorageRef;
    User retrievedUser;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c0);

//        FirebaseHelper.initialize();
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



        Button testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runTest();
            }
        });

        Button testButton2 = findViewById(R.id.testButton2);
        testButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runTest2();
            }
        });
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



    void runTest() {

//        User temp = FirebaseHelper.getUser();
//        Log.d(DEBUG, temp._selfIntroduction);

//        // store user to database
//        String userId = user.getUid();
//        User customUser = new User("MuHAHA");
//        userRef.child(userId).setValue(customUser);


//        // get User from database
//        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                retrievedUser = dataSnapshot.getValue(User.class);
//                new AlertDialog.Builder(C0.this)
//                        .setTitle(retrievedUser._selfIntroduction)
//                        .show();
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });


//        // create problem and save to database
//        Double[] coord = {0.0, 0.0};
//        Problem problem = new Problem(user.getUid(), coord, "hihi", "descriptttt");
//        problemRef.push().setValue(problem);


//        // test of image
//        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
//        final StorageReference riversRef = mStorageRef.child("images/rivers.jpg");
//        UploadTask uploadTask = riversRef.putFile(file);
//        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                // Continue with the task to get the download URL
//                return riversRef.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
//                    String downloadURL = downloadUri.toString();
//                    Double[] coord = {0.0, 0.0};
//                    Problem problem = new Problem(user.getUid(), coord, "hihi", "descriptttt", downloadURL);
//                    problemRef.push().setValue(problem);
//                } else {
//                    // Handle failures
//                    // ...
//                }
//            }
//        });

        // download image
//        File localFile = File.createTempFile("images", "jpg");
//        riversRef.getFile(localFile)
//                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        // Successfully downloaded data to local file
//                        // ...
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle failed download
//                // ...
//            }
//        });


//        DateFormat dateFormat = getDateTimeInstance();
//        Map map = new HashMap<>();
//        map.put("timestamp", ServerValue.TIMESTAMP);

//        Map map2 = new HashMap();
//        map2.put("timestamp", ServerValue.TIMESTAMP);
//        myRef.child("tempWorkingArea").setValue(map2);
//
//
//
//        myRef.child("tempWorkingArea").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final Long curTime = dataSnapshot.getValue(Long.class);
//                userRef.child(user.getUid()).child("startTimeOfCurrentActiveTask").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Long passTime = dataSnapshot.getValue(Long.class);
//                        Log.d(DEBUG, "passTime: " + passTime.toString());
//                        Log.d(DEBUG, "curTime: " + curTime.toString());
//                        Map map = new HashMap();
//                        map.put("timestamp", ServerValue.TIMESTAMP);
//                        userRef.child(user.getUid()).child("startTimeOfCurrentActiveTask").updateChildren(map);
//
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });



//        return dateFormat.format(netDate);
//        parseToLong(temp)
//        admin.initializeApp(functions.config().firebase)
//        Log.d(DEBUG, admin.database.ServerValue.TIMESTAMP);

// EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE
// EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE
// EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE // EXAMPLE




    }

    private void runTest2() {
        Intent intent = new Intent(getApplicationContext(), C2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.nothing);
    }




// not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //
    // not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //
    // not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //

    void initializeDataList() {
//        FirebaseHelper.initialize();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("TestData");
        userRef = database.getReference("UserData");
        problemRef = database.getReference("ProblemData");
        mStorageRef = FirebaseStorage.getInstance().getReference();

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

}
