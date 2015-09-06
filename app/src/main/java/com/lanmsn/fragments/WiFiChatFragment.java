package com.lanmsn.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.lanmsn.R;
import com.lanmsn.activities.WiFiServiceDiscoveryActivity;
import com.lanmsn.model.ChatManager;


public class WiFiChatFragment extends Fragment {

    public static final String myMessage ="MY_MEESAGE: ";

    private View view;
    private ChatManager chatManager;
    private TextView chatLine;
    private ListView listView;
    private ChatMessageAdapter adapter = null;
    private List<String> items = new ArrayList<String>();
    private WiFiServiceDiscoveryActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatLine = (TextView) view.findViewById(R.id.txtChatLine);
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        adapter = new ChatMessageAdapter(getActivity(), android.R.id.text1,
                items);
        listView.setAdapter(adapter);
        activity = (WiFiServiceDiscoveryActivity) getActivity();
        view.findViewById(R.id.button1).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (chatManager != null) {
                            String messageToSend = activity.getMyUsername()+"~~"+chatLine.getText().toString();
                            chatManager.write(messageToSend.getBytes());
                            pushMessage(myMessage+activity.getMyUsername()+": " + chatLine.getText().toString());
                            chatLine.setText("");
                        }
                    }
                });


        return view;
    }

    public interface MessageTarget {
        Handler getHandler();
    }

    public void setChatManager(ChatManager obj) {

        chatManager = obj;
    }

    public void pushMessage(String readMessage) {
        adapter.add(readMessage);
        adapter.notifyDataSetChanged();
    }




}