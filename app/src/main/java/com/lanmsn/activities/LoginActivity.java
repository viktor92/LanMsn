package com.lanmsn.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lanmsn.R;
import com.lanmsn.recievers.WiFiDirectBroadcastReceiver;

public class LoginActivity extends Activity implements View.OnClickListener {

    public static final String PREFS_NAME ="lanMsnPrefsFile";

    private Button btnLogin;
    private EditText txtUsername;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver receiver = null;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtUsername = (EditText) findViewById(R.id.txtUsername);


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        txtUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                login();
                return true;
            }
        });

        //Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "");
        txtUsername.setText(username);
        btnLogin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {

        unregisterReceiver(receiver);

        super.onPause();
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==btnLogin.getId()){

            if(!txtUsername.getText().toString().equals("")) {

                login();
            }else{
                Toast.makeText(LoginActivity.this,"You must pick username",Toast.LENGTH_LONG).show();
            }

        }

    }

    private void login(){
        Intent intent = new Intent(LoginActivity.this, WiFiServiceDiscoveryActivity.class);
        String username = txtUsername.getText().toString();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username",username);

        editor.commit();
        intent.putExtra("username", username);
        startActivity(intent);
    }


}
