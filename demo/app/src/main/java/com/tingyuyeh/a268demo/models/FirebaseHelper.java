package com.tingyuyeh.a268demo.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FirebaseHelper {

//
//  X  List<Problem> getAllProblem()
//  X  User getUser()
//  X  boolean increaseVote(Problem)
//  X  boolean decreaseVote(Problem)
//  X  void completeProblem()
//  X  void addFavourite(Problem)
//  X  void addActive(Problem)
//  X  void serveMinute(int)
//  X  void uploadUserPhoto
//  X  int getActiveMinute()

//  X  List<Problem> getFavouriteProblems
//  X  List<Problem> getCompletedProblems
//  X  List<Problem> getProblemsReportedByMe
//  X  List<Problem> getActiveProblem

    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private DatabaseReference problemRef;
    private DatabaseReference tempWorkingRef;
    private StorageReference mStorageRef;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    static final String DEBUG = "FH";
    private User retrievedUser;
    private List<Problem> listOfProblems;
    private Map<String, Problem> mapOfAllProblems;

    static private FirebaseHelper instance = null;
    private FirebaseHelper() {
        // initialize
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("UserData");
        problemRef = database.getReference("ProblemData");
        tempWorkingRef = database.getReference("Temporary");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.d(DEBUG, user.getEmail());
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrievedUser = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mapOfAllProblems = new HashMap<>();
        listOfProblems = new ArrayList<>();
        problemRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(DEBUG, "getAllPr");
                Problem temp = dataSnapshot.getValue(Problem.class);
                Log.d(DEBUG, temp._problemId);
                listOfProblems.add(temp);
                mapOfAllProblems.put(temp._problemId, temp);

//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    Problem temp = ds.getValue(Problem.class);
//                    Log.d(DEBUG, temp._problemId);
//                    listOfProblems.add(ds.getValue(Problem.class));
//                }

//
//                Data value = dataSnapshot.getValue(Data.class);
//                senders.add(value.sender);
//                msgs.add(value.msg);
//                listAdapter.notifyDataSetChanged();
//                lv.setBackgroundResource(R.drawable.customshape);
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


//        problemRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(DEBUG, "getAllPr");
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    Problem temp = ds.getValue(Problem.class);
//                    Log.d(DEBUG, temp._problemId);
//                    listOfProblems.add(ds.getValue(Problem.class));
//                }
//                cb.onSuccess(listOfProblems);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });

    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public User getUser() {
        return retrievedUser;
    }
    public static void destroyInstance() {
        instance = null;
    }

    public boolean increaseVote(Problem problem) {
        if (retrievedUser._voteStatusForEachProblem.containsKey(problem._problemId)) {
            int curVal = retrievedUser._voteStatusForEachProblem.get(problem._problemId);
            if (curVal == 1) {
                return false;
            } else {
                retrievedUser._voteStatusForEachProblem.put(problem._problemId, curVal+1);
                problem._ratings++;
            }
        } else {
            retrievedUser._voteStatusForEachProblem.put(problem._problemId, 1);
            problem._ratings++;
        }
        // save problem and user
        userRef.child(user.getUid()).setValue(retrievedUser);
        problemRef.child(problem._problemId).setValue(problem);
        return true;
    }


    public boolean decreaseVote(Problem problem) {
        if (retrievedUser._voteStatusForEachProblem.containsKey(problem._problemId)) {
            int curVal = retrievedUser._voteStatusForEachProblem.get(problem._problemId);
            if (curVal == -1) {
                return false;
            } else {
                retrievedUser._voteStatusForEachProblem.put(problem._problemId, curVal-1);
                problem._ratings--;
            }
        } else {
            retrievedUser._voteStatusForEachProblem.put(problem._problemId, -1);
            problem._ratings--;
        }
        // save problem and user
        String userId = user.getUid();
        userRef.child(userId).setValue(retrievedUser);
        problemRef.child(problem._problemId).setValue(problem);
        return true;
    }


    public boolean addFavourite(Problem problem) {
        if (!retrievedUser._idOfFavouriteProblems.contains(problem._problemId)) {
            retrievedUser._idOfFavouriteProblems.add(problem._problemId);
            userRef.child(user.getUid()).setValue(retrievedUser);
            return true;
        } else {
            return false;
        }
    }

    public boolean addActive(Problem problem) {
        if (retrievedUser._idOfActiveProblem == null) {
            retrievedUser._idOfActiveProblem = problem._problemId;
            retrievedUser._startTimeStamp = ServerValue.TIMESTAMP;
            userRef.child(user.getUid()).setValue(retrievedUser);
            return true;
        } else {
            return false;
        }

    }
    public void completeProblem() {
        if (retrievedUser._idOfActiveProblem != null) {
            getActiveMinute(new Callback() {
                @Override
                public void onSuccess(int activeMinute) {
                    retrievedUser._totalWorkMinutes += activeMinute;
                    retrievedUser._idOfActiveProblem = null;
                    retrievedUser._startTimeStamp = null;
                    if (!retrievedUser._idOfCompletedProblems.contains(retrievedUser._idOfActiveProblem)) {
                        retrievedUser._idOfCompletedProblems.add(retrievedUser._idOfActiveProblem);
                    }
                    userRef.child(user.getUid()).setValue(retrievedUser);
                }
            });
        }
    }

    public void uploadUserPhoto(final Uri file, final Context context) {
        final StorageReference storeRef = mStorageRef.child("images/" + user.getUid() + "/" + file.getPath());
        UploadTask uploadTask = storeRef.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return storeRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    final String downloadURL = downloadUri.toString();
                    Bitmap selectedImage = createThumbnail(context, file);
                    final String encodedImage = encodeImage(selectedImage);
                    retrievedUser._thumbnail = encodedImage;
                    retrievedUser._imageUri = downloadURL;
                    userRef.child(user.getUid()).setValue(retrievedUser);
                } else {
                }
            }
        });
    }



    public List<Problem> getAllProblems() {
//
//        final List<Problem> listOfProblems = new ArrayList<>();
//        problemRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(DEBUG, "getAllPr");
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    Problem temp = ds.getValue(Problem.class);
//                    Log.d(DEBUG, temp._problemId);
//                    listOfProblems.add(ds.getValue(Problem.class));
//                }
//                cb.onSuccess(listOfProblems);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
        return listOfProblems;
    }


    private List<Problem> retrieveProblems(List<String> queryList) {
        List<Problem> result = new ArrayList<>();
        for (String id : queryList) {
            result.add(mapOfAllProblems.get(id));
        }
        return result;
    }

    public List<Problem> getFavouriteProblems() {
        return retrieveProblems(retrievedUser._idOfFavouriteProblems);
    }
    public List<Problem> getCompletedProblems() {
        return retrieveProblems(retrievedUser._idOfCompletedProblems);
    }
    public List<Problem> getProblemsReportedByMe() {
        return retrieveProblems(retrievedUser._idOfProblemsReportedByMe);
    }
    public Problem getActiveProblem() {
        String id = retrievedUser._idOfActiveProblem;
        return mapOfAllProblems.containsKey(id) ? mapOfAllProblems.get(id) : null;
    }

    public void createProblem(final Uri file, final Double[] coord, final String title, final String description, final Context context) {
        final StorageReference storeRef = mStorageRef.child("images/" + user.getUid() + "/" + file.getPath());
        UploadTask uploadTask = storeRef.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return storeRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadURL = downloadUri.toString();
                    Bitmap selectedImage = createThumbnail(context, file);
                    String encodedImage = encodeImage(selectedImage);
                    String problemId = problemRef.push().getKey();
                    Problem problem = new Problem(problemId, user.getUid(), coord, title, description, downloadURL, encodedImage);
                    problemRef.child(problemId).setValue(problem);
                } else {
                }
            }
        });

    }

    public static String encodeImage(Bitmap bm)
    {
        if (bm == null) {
            return "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    public static Bitmap decodeImage(String input) {
        byte[] decodedString = Base64.decode(input, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    private static Bitmap createThumbnail(Context context, Uri file) {
        final int THUMBNAIL_SIZE = 64;

        final InputStream imageStream;
        Bitmap selectedImage;
        try {
            imageStream = context.getContentResolver().openInputStream(file);
            selectedImage = BitmapFactory.decodeStream(imageStream);
            selectedImage = Bitmap.createScaledBitmap(selectedImage, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
            return selectedImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

// Refer to https://stackoverflow.com/questions/37215071/firebase-android-how-to-read-from-different-references-sequentially/40557237
    public void getActiveMinute(final Callback cb) {
        getTimeSinceStartOfActiveProblem()
            .addOnSuccessListener(new OnSuccessListener<Long>() {
                @Override
                public void onSuccess(Long aLong) {
                    cb.onSuccess((int) (aLong/1000/60));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    cb.onFailure(e);
                }
            });

    }

    private Task<Long> getServerTimestamp() {
        return Tasks.call(new SubmitServerTime())
                .continueWithTask(new GetSystemTimestamp());
    }

    private Task<Long> getTimeSinceStartOfActiveProblem() {
        return Tasks.call(new SubmitServerTime())
                .continueWithTask(new GetSystemTimestamp())
                .continueWithTask(new CalculateTimestampDifference());
    }

    class SubmitServerTime implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            Map map = new HashMap();
            map.put("timestamp", ServerValue.TIMESTAMP);

            tempWorkingRef.setValue(map);
            Log.d(DEBUG, "submitServerTime");
            return null;
        }
    }
    class GetSystemTimestamp implements Continuation<Void, Task<Long>> {
        @Override
        public Task<Long> then(Task<Void> task) {
            final TaskCompletionSource<Long> tcs = new TaskCompletionSource();
            tempWorkingRef.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onCancelled(DatabaseError error) {
                    tcs.setException(error.toException());
                }
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    tcs.setResult(snapshot.getValue(Long.class));
                }
            });
            Log.d(DEBUG, "getSystemTimeStamp");
            return tcs.getTask();
        }
    }
    class CalculateTimestampDifference implements Continuation<Long, Task<Long>> {
        @Override
        public Task<Long> then(@NonNull Task<Long> task) {
            final TaskCompletionSource<Long> tcs = new TaskCompletionSource();
            final long systemTime = task.getResult();
            userRef.child(user.getUid()).child("_startTimeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onCancelled(DatabaseError error) {
                    tcs.setException(error.toException());
                }
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        tcs.setException(new Exception("No active problem"));
                    } else {
                        tcs.setResult(systemTime - snapshot.getValue(Long.class));
                    }
                }
            });
            Log.d(DEBUG, "calculateTimeStampDifference");
            return tcs.getTask();
        }
    }
}





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