package com.tingyuyeh.coen268a4;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button button_addData;
    Button button_viewData;
    Button button_updateData;
    Button button_delete;

    EditText editText_name;
    EditText editText_email;
    EditText editText_favourite;
    EditText editText_id;

    String DEBUG = "MAIN";

    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHelper = new DatabaseHelper(this);

        button_addData = findViewById(R.id.button_addData);
        button_viewData = findViewById(R.id.button_viewData);
        button_updateData = findViewById(R.id.button_updateData);
        button_delete = findViewById(R.id.button_delete);

        editText_name = findViewById(R.id.editText_name);
        editText_email = findViewById(R.id.editText_email);
        editText_favourite = findViewById(R.id.editText_tvShow);
        editText_id = findViewById(R.id.editText_id);

        button_addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });
        button_viewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewData();
            }
        });
        button_updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

    }
    void addData() {
        Person p = new Person(editText_name.getText().toString(), editText_email.getText().toString(), editText_favourite.getText().toString());
        if (!mDatabaseHelper.addData(p)) {
            toastMessage("Add Data Failed");
        } else {
            toastMessage("Add Data Successful");
        }
    }
    void viewData() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);



        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_view, null);


        Cursor data = mDatabaseHelper.getData();
        ArrayList<Person> listData = new ArrayList<>();
        while(data.moveToNext()) {
            String name = data.getString(data.getColumnIndex("name"));
            String email= data.getString(data.getColumnIndex("email"));
            String favourite = data.getString(data.getColumnIndex("favourite"));
            Person p = new Person(name, email, favourite);
            p._id = data.getString(data.getColumnIndex("ID"));
            Log.d(DEBUG, p._id + " " + p._name + " " + p._email + " " + p._favourite);
            listData.add(p);
        }

        ListAdapter adapter = new ArrayAdapter<Person>(this, R.layout.item, listData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                Person p = getItem(position);
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);

                TextView idView = view.findViewById(R.id.id);
                TextView nameView = view.findViewById(R.id.name);
                TextView emailView = view.findViewById(R.id.email);
                TextView favouriteView = view.findViewById(R.id.favourite);

                idView.setText(p._id);
                nameView.setText(p._name);
                emailView.setText(p._email);
                favouriteView.setText(p._favourite);

                ViewGroup.LayoutParams params = view.getLayoutParams();
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
                params.height = height;
                view.setLayoutParams(params);

                return view;
            }

        };

        builder.setAdapter(adapter, null);
        AlertDialog alertDialog = builder.create(); //Read Update

        alertDialog.setView(v);
        alertDialog.show();  //<-- See This!
        alertDialog.getWindow().setLayout(600, 800); //Controlling width and height.
    }

    void updateData() {
        if (!editText_id.getText().toString().equals("")) {
            int id = Integer.parseInt(editText_id.getText().toString());
            Person p = new Person(editText_name.getText().toString(), editText_email.getText().toString(), editText_favourite.getText().toString());
            if (!mDatabaseHelper.updateData(id, p)) {
                toastMessage("Update Failed");
            } else {
                toastMessage("Update Successful");
            }
        }
    }
    void deleteRecord() {
        if (!editText_id.getText().toString().equals("")) {
            int id = Integer.parseInt(editText_id.getText().toString());
            if (!mDatabaseHelper.deleteData(id)) {
                toastMessage("Delete Failed");
            } else {
                toastMessage("Delete Successful");
            }
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
