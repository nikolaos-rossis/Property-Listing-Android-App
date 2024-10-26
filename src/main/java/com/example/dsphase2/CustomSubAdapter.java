package com.example.dsphase2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSubAdapter extends ArrayAdapter<ListItem> {

    private Context context;
    private List<ListItem> items;

    public CustomSubAdapter(Context context, List<ListItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_sub, parent, false);
        }

        // Get the data item for this position
        ListItem currentItem = items.get(position);

        // Lookup view for data population
        TextView primaryTextView = convertView.findViewById(R.id.primaryText);
        TextView subTextView = convertView.findViewById(R.id.subText);

        // Populate the data into the template view using the data object
        primaryTextView.setText(currentItem.getPrimaryText());
        subTextView.setText(currentItem.getSubText());

        // Return the completed view to render on screen
        return convertView;
    }
}
