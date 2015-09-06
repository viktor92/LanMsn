package com.lanmsn.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import com.lanmsn.R;

public class ChatMessageAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> messages = null;


    public ChatMessageAdapter(Context context, int textViewResourceId,
                              List<String> items) {
        super(context, textViewResourceId, items);
        this.context=context;
        this.messages=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.message_row, null);
        }
        String message = messages.get(position);
        if (message != null && !message.isEmpty()) {
            TextView nameText = (TextView) v
                    .findViewById(R.id.messageTextView);

            if (nameText != null) {

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);

                if (message.startsWith(WiFiChatFragment.myMessage)) {
                    String msg = message.substring(WiFiChatFragment.myMessage.length());
                    nameText.setText(msg);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.leftMargin=10;
                    params.rightMargin=10;
                    params.topMargin=10;
                    nameText.setLayoutParams(params);
                    nameText.setBackgroundResource(R.drawable.green_rectangle);

                } else {
                    nameText.setText(message);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.leftMargin=10;
                    params.rightMargin=10;
                    params.topMargin=10;
                    nameText.setLayoutParams(params);
                    nameText.setBackgroundResource(R.drawable.blue_rectangle);
                }
            }
        }
        return v;
    }
}
