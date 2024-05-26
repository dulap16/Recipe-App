package com.example.recipeai;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LeftoversAdapter extends ArrayAdapter<Leftover> {

    private List<Leftover> list;
    private Context context;

    TextView leftoverNameView, leftoverUsesView;

    public LeftoversAdapter(Context context, List<Leftover> myLeftovers) {
        super(context, 0, myLeftovers);
        this.list = myLeftovers;
        this.context = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.leftover_item, parent, false);

        final Leftover currentLeftover = list.get(position);

        leftoverNameView = convertView.findViewById(R.id.leftover_name);
        leftoverUsesView = convertView.findViewById(R.id.leftover_uses);

        leftoverNameView.setText(currentLeftover.getName());
        leftoverUsesView.setText(currentLeftover.getUses());

        return convertView;
    }
}
