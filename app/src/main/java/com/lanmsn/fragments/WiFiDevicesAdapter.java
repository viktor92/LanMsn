package com.lanmsn.fragments;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.lanmsn.model.WiFiP2pService;

public class WiFiDevicesAdapter extends ArrayAdapter<WiFiP2pService> {

    private List<WiFiP2pService> items;
    private WiFiDirectServicesList wiFiDirectServicesList;

    public WiFiDevicesAdapter(Context context, int resource,
                              int textViewResourceId, List<WiFiP2pService> items,WiFiDirectServicesList wiFiDirectServicesList) {
        super(context, resource, textViewResourceId, items);
        this.items = items;
        this.wiFiDirectServicesList=wiFiDirectServicesList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)wiFiDirectServicesList.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(android.R.layout.simple_list_item_2, null);
        }
        WiFiP2pService service = items.get(position);
        if (service != null) {
            TextView nameText = (TextView) v
                    .findViewById(android.R.id.text1);

            if (nameText != null) {
                nameText.setText(service.getUsername()+" - "+service.getDevice().deviceName );
            }
            TextView statusText = (TextView) v
                    .findViewById(android.R.id.text2);
            statusText.setText(wiFiDirectServicesList.getDeviceStatus(service.getDevice().status));
        }
        return v;
    }

}