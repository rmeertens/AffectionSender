package com.meertens.affection_sender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by roland on 26/01/15.
 */
public class LoveAdapter extends ArrayAdapter<String> {

    public LoveAdapter(Context context, ArrayList<String> love) {
        super(context, 0, love);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final String thisLove = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_love, parent, false);
        }

        // Get the layout elements that are used in this view
        TextView loveText = (TextView) convertView.findViewById(R.id.love_edit_text);
        Button removeButton = (Button) convertView.findViewById(R.id.remove_love_button);

        // Set the text and add a listener to the button
        loveText.setText(thisLove);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(thisLove);
                notifyDataSetChanged();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
