package com.tingyuyeh.a268demo;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataList extends ArrayAdapter<String> {
    private final Activity context;
    private List<String> senders;
    private List<String> msgs;
    String DEBUG = "Datalist";

    public DataList(Activity context,
                      List<String> senders, List<String> msgs) {
        super(context, R.layout.list_item, senders);

        this.context = context;
        this.senders = senders;
        this.msgs = msgs;
    }
    public void addEntry(String sender, String msg) {
        Log.w(DEBUG, "adding data to list Adapter");
        senders.add(sender);
        msgs.add(msg);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);
        TextView sender = rowView.findViewById(R.id.sender);
        TextView msg = rowView.findViewById(R.id.data);
        sender.setText(senders.get(position));
        msg.setText(msgs.get(position));
        return rowView;
    }
}

