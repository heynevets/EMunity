package com.tingyuyeh.a268demo.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tingyuyeh.a268demo.Data;
import com.tingyuyeh.a268demo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import static android.opengl.ETC1.encodeImage;

public class FirebaseHelper {

    static final FirebaseDatabase database = FirebaseDatabase.getInstance();;
    static final DatabaseReference userRef = database.getReference("UserData");;
    static final DatabaseReference problemRef = database.getReference("ProblemData");;
    static final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();;

    static private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser user = mAuth.getCurrentUser();

    static private User retrievedUser;

    static final String DEBUG = "FirebaseHelper";
    public static void storeUserToDatabase(String introduction) {
        // store user to database
        String userId = user.getUid();
        User customUser = new User(introduction);
        userRef.child(userId).setValue(customUser);
    }

    public static User getUser() {
        return retrievedUser;
    }

    public static void initialize() {
        // Retrieve User
//        userRef.child(user.getUid()).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                retrievedUser = dataSnapshot.getValue(User.class);
//                Log.d(DEBUG, "childadded user");
//            }
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                retrievedUser = dataSnapshot.getValue(User.class);
//                Log.d(DEBUG, "childchanged user");
//            }
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrievedUser = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void createProblem(final Uri file, final Double[] coord, final String title, final String description, final Context context) {
        // test of image
//        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        Log.d(DEBUG, file.getEncodedPath());
        Log.d(DEBUG, file.getPath());
        Log.d(DEBUG, file.toString());
        final StorageReference riversRef = mStorageRef.child("images/" + user.getUid() + "/" + file.getPath());
//
//        riversRef.putFile(file)
//            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // Get a URL to the uploaded content
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                }
//            })
//            .addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                    // ...
//                }
//            });

        UploadTask uploadTask = riversRef.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadURL = downloadUri.toString();


//                    final Uri imageUri = data.getData();

                    final InputStream imageStream;
                    String encodedImage = "";
                    try {

                        final int THUMBNAIL_SIZE = 64;
                        imageStream = context.getContentResolver().openInputStream(file);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        selectedImage = Bitmap.createScaledBitmap(selectedImage, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();

                        encodedImage = Base64.encodeToString(imageData, Base64.DEFAULT);


                        
//                        encodedImage = encodeImage(selectedImage);
//
//
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
//                        byte[] b = baos.toByteArray();
//                        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
//
//                        return encImage;


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


//
//
//                    FileInputStream fis = new FileInputStream(fileName);
//                    Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
//
//                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    imageData = baos.toByteArray();

                    Problem problem = new Problem(user.getUid(), coord, title, description, downloadURL, encodedImage);
                    problemRef.push().setValue(problem);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }
    private static String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }
}


//
//    List<Problem> getAllProblem()
//    User getUser()
//    boolean increaseVote(Problem)
//    boolean decreaseVote(Problem)
//    void completeProblem()
//    void addFavourite(Problem)
//    void addActive(Problem)
//    void serveMinute(int)





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