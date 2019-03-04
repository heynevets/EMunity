package com.tingyuyeh.a268demo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.User;

// Sign up page
public class B1 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String DEBUG = "B1_Debug_Signup";
    Button signUpButton;
    EditText emailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b1);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);

        signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                trySignup();
            }
        });

        // Firebase
        mAuth = FirebaseAuth.getInstance();




    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.nothing, R.anim.fade_out);
    }
    private void trySignup() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        Log.d(DEBUG, "showPassword: " + password);
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        Log.d(DEBUG, "showConfirmPassword: " + password);


        if (!password.equals("") && password.equals(passwordConfirm)) {
            if (!email.equals("")) {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(DEBUG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    FirebaseHelper.storeUserToDatabase("Write something about yourself...");
                                    createNewUser(user);
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(DEBUG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(B1.this, "" + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            } else {
                Toast.makeText(B1.this, "Email cannot be empty",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(B1.this, "Passwords invalid",
                    Toast.LENGTH_SHORT).show();
        }

    }
    void updateUI(FirebaseUser user) {
        if (user == null) {
            // display error
        } else {
            FirebaseHelper.getInstance();
            Intent intent = new Intent(getApplicationContext(), C0.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.nothing);
        }
    }

    void createNewUser(FirebaseUser user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("UserData");
        String userId = user.getUid();
        User customUser = new User("Write something about yourself...");
        userRef.child(userId).setValue(customUser);
    }

}


