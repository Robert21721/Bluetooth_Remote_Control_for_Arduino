package com.example.bluetooth_remote_control_for_arduino.firstPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bluetooth_remote_control_for_arduino.R;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<BTDevice> {

    private final LayoutInflater inflater;

    public CustomListAdapter(Context context, List<BTDevice> data) {
        super(context, 0, data);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        BTDevice item = getItem(position);

        TextView itemTitleTextView = convertView.findViewById(R.id.itemTitle);
        TextView itemDescriptionTextView = convertView.findViewById(R.id.itemDescription);

        itemTitleTextView.setText(item.getDeviceName());
        itemDescriptionTextView.setText(item.getDeviceMACAddr());

        return convertView;
    }
}