package com.tingyuyeh.a268demo.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    static final FirebaseDatabase database = FirebaseDatabase.getInstance();;
    static final DatabaseReference userRef = database.getReference("UserData");;
    static final DatabaseReference problemRef = database.getReference("ProblemData");;
    static final DatabaseReference tempWorkingRef = database.getReference("Temporary");;
    static final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();;

    static private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static private FirebaseUser user = mAuth.getCurrentUser();
    static final String DEBUG = "FH";

    public static void increaseVote(final Problem problem, final Callback cb) {
        getUser(new Callback(){
            @Override
            public void onSuccess(User cbUser) {
                if (cbUser._voteStatusForEachProblem.containsKey(problem._problemId)) {
                    int curVal = cbUser._voteStatusForEachProblem.get(problem._problemId);
                    if (curVal == 1) {
                        cb.onFailure();
                    } else {
                        cbUser._voteStatusForEachProblem.put(problem._problemId, curVal+1);
                        problem._ratings++;
                    }
                } else {
                    cbUser._voteStatusForEachProblem.put(problem._problemId, 1);
                    problem._ratings++;
                }
                // save problem and user
                userRef.child(user.getUid()).setValue(cbUser);
                problemRef.child(problem._problemId).setValue(problem);
                cb.onSuccess();
            }
        });
    }
    public static void decreateVote(final Problem problem, final Callback cb) {
        getUser(new Callback(){
            @Override
            public void onSuccess(User cbUser) {
                if (cbUser._voteStatusForEachProblem.containsKey(problem._problemId)) {
                    int curVal = cbUser._voteStatusForEachProblem.get(problem._problemId);
                    if (curVal == -1) {
                        cb.onFailure();
                    } else {
                        cbUser._voteStatusForEachProblem.put(problem._problemId, curVal-1);
                        problem._ratings--;
                    }
                } else {
                    cbUser._voteStatusForEachProblem.put(problem._problemId, -1);
                    problem._ratings--;
                }
                // save problem and user
                String userId = user.getUid();
                userRef.child(userId).setValue(cbUser);
                problemRef.child(problem._problemId).setValue(problem);
                cb.onSuccess();
            }
        });
    }

    public static void addFavourite(final Problem problem, final Callback cb) {

        getUser(new Callback(){
            @Override
            public void onSuccess(final User cbUser) {
                if (!cbUser._IdOfFavouriteProblems.contains(problem._problemId)) {
                    cbUser._IdOfFavouriteProblems.add(problem._problemId);
                    userRef.child(user.getUid()).setValue(cbUser);
                    cb.onSuccess();
                } else {
                    cb.onFailure();
                }
            }
        });

    }

    public static void addActive(final Problem problem) {
        getUser(new Callback(){
            @Override
            public void onSuccess(final User cbUser) {
                getServerTimestamp().addOnSuccessListener(new OnSuccessListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        cbUser._idOfActiveProblem = problem._problemId;
                        cbUser._startTimeStamp = ServerValue.TIMESTAMP;
                        userRef.child(user.getUid()).setValue(cbUser);
                    }
                });
            }
        });
    }
    public static void completeProblem(final Callback cb) {
        getUser(new Callback(){
            @Override
            public void onSuccess(final User cbUser) {
                if (cbUser._idOfActiveProblem != null) {
                    getActiveMinute(new Callback() {
                        @Override
                        public void onSuccess(int activeMinute) {
                            cbUser._totalWorkMinutes += activeMinute;
                            cbUser._idOfActiveProblem = null;
                            cbUser._startTimeStamp = null;
                            if (!cbUser._IdOfCompletedProblems.contains(cbUser._idOfActiveProblem)) {
                                cbUser._IdOfCompletedProblems.add(cbUser._idOfActiveProblem);
                            }
                            userRef.child(user.getUid()).setValue(cbUser);
                            cb.onSuccess();
                        }
                    });
                } else {
                    cb.onFailure();
                }
            }
        });
    }

    public static void uploadUserPhoto(final Uri file, final Context context) {
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
                    getUser(new Callback() {
                        @Override
                        public void onSuccess(User cbUser) {
                            cbUser._thumbnail = encodedImage;
                            cbUser._imageUri = downloadURL;
                            userRef.child(user.getUid()).setValue(cbUser);
                        }
                    });
                } else {
                }
            }
        });


    }
    public static void getAllProblems(final Callback cb) {

        final List<Problem> listOfProblems = new ArrayList<>();
        problemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                        listOfAllProblems = new ArrayList<>();
                Log.d(DEBUG, "getAllPr");
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Problem temp = ds.getValue(Problem.class);
                    Log.d(DEBUG, temp._problemId);
                    listOfProblems.add(ds.getValue(Problem.class));
                }
                cb.onSuccess(listOfProblems);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void storeUserToDatabase(String introduction) {
        // store user to database
        String userId = user.getUid();
        User customUser = new User(introduction);
        userRef.child(userId).setValue(customUser);
    }

    public static void getUser(final Callback cb) {

        Log.d(DEBUG, user.getEmail());
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cb.onSuccess(dataSnapshot.getValue(User.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void createProblem(final Uri file, final Double[] coord, final String title, final String description, final Context context) {
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
    static public void getActiveMinute(final Callback cb) {
        getTimeSinceStartOfActiveProblem().addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                cb.onSuccess((int) (aLong/1000/60));
            }
        });
    }

    static public Task<Long> getServerTimestamp() {
        return Tasks.call(new SubmitServerTime())
                .continueWithTask(new GetSystemTimestamp());
    }

    static public Task<Long> getTimeSinceStartOfActiveProblem() {
        return Tasks.call(new SubmitServerTime())
                .continueWithTask(new GetSystemTimestamp())
                .continueWithTask(new CalculateTimestampDifference());
    }

    static class SubmitServerTime implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            Map map = new HashMap();
            map.put("timestamp", ServerValue.TIMESTAMP);
            tempWorkingRef.setValue(map);
            Log.d(DEBUG, "submitServerTime");
            return null;
        }
    }
    static class GetSystemTimestamp implements Continuation<Void, Task<Long>> {
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
    static class CalculateTimestampDifference implements Continuation<Long, Task<Long>> {
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
                    tcs.setResult(systemTime - snapshot.getValue(Long.class));
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