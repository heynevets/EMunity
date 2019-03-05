package com.tingyuyeh.a268demo;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProblemList extends ArrayAdapter<Problem> {
    private final Activity context;
    private List<Problem> problems;
    String DEBUG = "problemlist";

    public ProblemList(Activity context,
                    List<Problem> problems) {
        super(context, R.layout.list_item, problems);

        this.context = context;
        this.problems = problems;
    }
    public void addEntry(Problem p) {
        Log.w(DEBUG, "adding problem to list Adapter");
        problems.add(p);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.problem_list_item, null, true);
        ImageView thumbnail = rowView.findViewById(R.id.thumbnail);
        TextView title = rowView.findViewById(R.id.title);
        TextView description = rowView.findViewById(R.id.description);
        TextView vote = rowView.findViewById(R.id.textView_vote);




        Problem p = problems.get(position);
        title.setText(p._title);
        description.setText(p._description);
        thumbnail.setImageBitmap(FirebaseHelper.decodeImage(p._thumbnail));

        String voting = (p._ratings >= 0) ? "+" + p._ratings : "-" + p._ratings;
        vote.setText(voting);

        return rowView;
    }
}

