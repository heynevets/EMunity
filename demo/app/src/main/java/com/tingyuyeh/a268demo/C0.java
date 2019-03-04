package com.tingyuyeh.a268demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.tingyuyeh.a268demo.models.Callback;
import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.Problem;
import com.tingyuyeh.a268demo.models.User;

import java.util.List;

public class C0 extends AppCompatActivity {
    Button logOutButton;
    Button writeButton;
    private FirebaseAuth mAuth;
    TextView greeting;
    EditText dataField;

    FirebaseUser user;
    String DEBUG = "C0";

    ListView lv;
    ProblemList listAdapter;

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
//        initializeDataList();

        lv = findViewById(R.id.dataListView);

        writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                writeToDatabase();
                hideSoftKeyboard(C0.this, v); // MainActivity is the name of the class and v is the View parameter used in the button listener method onClick.
            }
        });


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
//        user = FirebaseAuth.getInstance().getCurrentUser();





//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        FirebaseHelper.getInstance().getAllProblems(new Callback() {
            @Override
            public void onSuccess(List<Problem> problems) {
                listAdapter = new ProblemList (C0.this, problems);
                FirebaseHelper.getInstance().registerProblemListener(listAdapter);
                lv.setAdapter(listAdapter);
                lv.setBackgroundResource(R.drawable.customshape);
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
    protected void onResume() {
        super.onResume();



//        listAdapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//
//                listAdapter.notifyDataSetChanged();
//            }
//        });
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
        FirebaseHelper.destroyInstance();
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

        // refer to https://stackoverflow.com/questions/47847694/how-to-return-datasnapshot-value-as-a-result-of-a-method/47853774
        // get user

//
//        // get all problems
//        List<Problem> problems = FirebaseHelper.getInstance().getAllProblems();
//
//        // add Favourite
//        FirebaseHelper.getInstance().addFavourite(problems.get(0));
//
//        // Add active
//        FirebaseHelper.getInstance().addActive(problems.get(0));
//
//        // complete Problem
//        FirebaseHelper.getInstance().completeProblem();
//
//        // Vote Problem
//        FirebaseHelper.getInstance().increaseVote(problems.get(0));
//
//        // get Active Minute
//        FirebaseHelper.getInstance().getActiveMinute(new Callback() {
//            @Override
//            public void onSuccess(int activeMinute) {
//                Log.d(DEBUG, "current active: " + activeMinute);
//            }
//            @Override
//            public void onFailure(Exception e) {
//                Log.d(DEBUG,  e.getMessage());
//            }
//        });

//        // get Active problem
//        FirebaseHelper.getInstance().getActiveProblem()._problemId





    }

    private void runTest2() {
        Intent intent = new Intent(getApplicationContext(), C2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.nothing);
    }




// not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //
    // not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //
    // not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //// not using //

//    void initializeDataList() {
////        FirebaseHelper.initialize();
//
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("TestData");
//        userRef = database.getReference("UserData");
//        problemRef = database.getReference("ProblemData");
//        mStorageRef = FirebaseStorage.getInstance().getReference();
//
//        myRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Data value = dataSnapshot.getValue(Data.class);
//                senders.add(value.sender);
//                msgs.add(value.msg);
//                listAdapter.notifyDataSetChanged();
//                lv.setBackgroundResource(R.drawable.customshape);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        senders = new ArrayList<>();
//        msgs = new ArrayList<>();
//    }

}
