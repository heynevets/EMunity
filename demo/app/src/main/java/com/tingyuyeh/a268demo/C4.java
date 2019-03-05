package com.tingyuyeh.a268demo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tingyuyeh.a268demo.models.Callback;
import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.Problem;
import com.tingyuyeh.a268demo.models.User;

import java.io.IOException;

public class C4 extends AppCompatActivity {

    String problemId;
    Problem problem;
    Button button_up;
    Button button_down;
    TextView text_title;
    TextView text_description;
    TextView text_vote;
    ImageView image;

    Button button_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c4);

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
    }

    @Override
    protected void onStart() {
        super.onStart();


        button_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseHelper.getInstance().increaseVote(problem)) {
                    Toast.makeText(C4.this, "Up vote completed",
                            Toast.LENGTH_SHORT).show();
                    setVoteText();
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

    }
    void updateActionButton() {
        User user = FirebaseHelper.getInstance().getUser();
        if (problem._problemId.equals(user._idOfActiveProblem)) {
            setButtonCompleteProblem();
        } else {
            setButtonAddActive();
        }
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
                            setButtonAddActive();
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
}
