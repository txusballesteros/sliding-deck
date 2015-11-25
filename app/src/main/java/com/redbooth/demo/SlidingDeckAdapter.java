package com.redbooth.demo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SlidingDeckAdapter extends ArrayAdapter<SlidingDeckModel> {
    public SlidingDeckAdapter(Context context) {
        super(context, R.layout.sliding_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sliding_item, parent, false);
        }
        SlidingDeckModel item = getItem(position);
        ((TextView)view.findViewById(R.id.elementTitle)).setText(item.getTitle());
        view.findViewById(R.id.command).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Command", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
