package com.tingyuyeh.a268demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tingyuyeh.a268demo.models.Callback;
import com.tingyuyeh.a268demo.models.FirebaseHelper;

// Log in page
public class B0 extends AppCompatActivity {
    Button logInButton;
    private FirebaseAuth mAuth;
    EditText emailEditText;
    EditText passwordEditText;
    KProgressHUD hud;
    private String DEBUG = "B0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b0);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        logInButton = findViewById(R.id.loginButton);

        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                validateLoginInfo();
            }
        });
        mAuth = FirebaseAuth.getInstance();

        hud = KProgressHUD.create(B0.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Logging in")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.nothing, R.anim.fade_out);
    }
    private void validateLoginInfo() {
        // Implement firebase
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();



        if (!email.equals("") && !password.equals("")) {
            hud.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(DEBUG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(DEBUG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(B0.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            hud.dismiss();
                        }
                    });
        } else {
            Toast.makeText(B0.this, "Email and password cannot be empty",
                    Toast.LENGTH_SHORT).show();
        }

    }
    void updateUI(FirebaseUser user) {
        if (user == null) {
            // do something
        } else {

            FirebaseHelper.initialize(new Callback() {
                @Override
                public void onComplete(boolean success) {
                    Intent intent = new Intent(getApplicationContext(), C0.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.nothing);
                }
            });

        }

    }

}
