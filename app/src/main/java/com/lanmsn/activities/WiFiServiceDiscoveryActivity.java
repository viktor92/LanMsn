package com.lanmsn.activities;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mk.ukim.finki.mpip.lanmsn.R;
import mk.ukim.finki.mpip.lanmsn.fragments.WiFiChatFragment;
import mk.ukim.finki.mpip.lanmsn.fragments.WiFiDevicesAdapter;
import mk.ukim.finki.mpip.lanmsn.fragments.WiFiDirectServicesList;
import mk.ukim.finki.mpip.lanmsn.handlers.ClientSocketHandler;
import mk.ukim.finki.mpip.lanmsn.handlers.GroupOwnerSocketHandler;
import mk.ukim.finki.mpip.lanmsn.model.ChatManager;
import mk.ukim.finki.mpip.lanmsn.model.WiFiP2pService;
import mk.ukim.finki.mpip.lanmsn.recievers.WiFiDirectBroadcastReceiver;


public class WiFiServiceDiscoveryActivity extends Activity implements
        WiFiDirectServicesList.DeviceClickListener, Handler.Callback, WiFiChatFragment.MessageTarget,
        ConnectionInfoListener {

    public static final String TAG = "lanMsn";



    // TXT RECORD properties
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static String SERVICE_INSTANCE = "_lanmsn";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    public static final String USERNAME = "username";

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    private WifiP2pManager manager;

    public static final int SERVER_PORT = 4545;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;

    private Handler handler = new Handler(this);
    private WiFiChatFragment chatFragment;
    private WiFiDirectServicesList servicesList;

    private TextView statusTxtView;

    private WifiP2pDnsSdServiceInfo service;

    private String myUsername;

    private WiFiDirectServicesList fragment;

    private WiFiDevicesAdapter adapter;
    private WiFiP2pService wiFiP2pService= new WiFiP2pService();

    private Fragment currentVisibleFragment;

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_service_discovery);
        statusTxtView = (TextView) findViewById(R.id.status_text);


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        //SERVICE_INSTANCE = SERVICE_INSTANCE+" "+myUsername;

        servicesList = new WiFiDirectServicesList();
        getFragmentManager().beginTransaction()
                .add(R.id.container_root, servicesList, "services").commit();
        currentVisibleFragment=servicesList;
        myUsername = getIntent().getExtras().getString("username");


       // startRegistrationAndDiscovery();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Fragment frag = getFragmentManager().findFragmentByTag("services");

        if (frag != null) {
            getFragmentManager().beginTransaction().replace(R.id.container_root, frag);
        }
        if(adapter!= null)
            adapter.clear();
        startRegistrationAndDiscovery();
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }



    @Override
    public void onPause() {

        unregisterReceiver(receiver);

        super.onPause();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStop() {

        statusTxtView.setText("");
        stopRegistrationAndDiscovery();
        if (manager != null && channel != null) {

            manager.removeGroup(channel, new ActionListener() {

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                }

                @Override
                public void onSuccess() {
                }

            });
        }

        super.onStop();
    }



    /**
     * Registers a local service and then initiates a service discovery
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startRegistrationAndDiscovery() {
        Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");
        record.put(USERNAME,myUsername);
         service= WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new ActionListener() {

            @Override
            public void onSuccess() {
                appendStatus("Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                appendStatus("Failed to add a service");
            }
        });

        discoverService();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void stopRegistrationAndDiscovery(){
        if(adapter!=null)
        adapter.clear();

        manager.clearLocalServices(channel, new ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        manager.clearServiceRequests(channel, new ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });

    }

    private void refresh(){

        stopRegistrationAndDiscovery();
        startRegistrationAndDiscovery();
    }


    final HashMap<String, String> buddies = new HashMap<String, String>();

    @SuppressLint("NewApi")
    private void discoverService() {

        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */
        manager.setDnsSdResponseListeners(channel,
                new DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {

                        // A service has been discovered. Is this our app?

                        Toast.makeText(WiFiServiceDiscoveryActivity.this, "ServiceAvailable", Toast.LENGTH_LONG).show();
                        // update the UI and add the item the discovered
                        // device.
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                            fragment = (WiFiDirectServicesList) getFragmentManager()
                                    .findFragmentByTag("services");
                            currentVisibleFragment = fragment;
                            if (fragment != null) {
                                adapter = ((WiFiDevicesAdapter) fragment
                                        .getListAdapter());

                                String username = buddies
                                        .containsKey(srcDevice.deviceAddress) ? buddies
                                        .get(srcDevice.deviceAddress) : srcDevice.deviceName;

                                wiFiP2pService.setDevice(srcDevice);
                                wiFiP2pService.setInstanceName(instanceName);
                                wiFiP2pService.setUsername(username);
                                wiFiP2pService.setServiceRegistrationType(registrationType);
                                adapter.add(wiFiP2pService);
                                adapter.notifyDataSetChanged();
                            }

                        }


                    }


                }, new DnsSdTxtRecordListener() {

                    /**
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                        Toast.makeText(WiFiServiceDiscoveryActivity.this, "onDnsSdTxtRecordAvailable", Toast.LENGTH_LONG).show();
                        buddies.put(device.deviceAddress, record.get(USERNAME));
                    }
                });

        // After attaching listeners, create a service request and initiate
        // discovery.
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        manager.addServiceRequest(channel, serviceRequest,
                new ActionListener() {

                    @Override
                    public void onSuccess() {
                        appendStatus("Added service discovery request");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        appendStatus("Failed adding service discovery request");
                    }
                });
        manager.discoverServices(channel, new ActionListener() {

            @Override
            public void onSuccess() {

                appendStatus("Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                appendStatus("Service discovery failed "+ arg0);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void connectP2p(WiFiP2pService service) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.getDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new ActionListener() {

                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int arg0) {
                        }
                    });

        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                appendStatus("Connecting to service");
            }

            @Override
            public void onFailure(int errorCode) {
                appendStatus("Failed connecting to service");
            }
        });

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                String[] messageParts = readMessage.split("~~");
                Log.d(TAG, readMessage);
                (chatFragment).pushMessage(messageParts[0]+": " + messageParts[1]);
                break;

            case MY_HANDLE:
                Object obj = msg.obj;
                (chatFragment).setChatManager((ChatManager) obj);

        }
        return true;
    }




    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Thread handler = null;
        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */

        if (p2pInfo.isGroupOwner) {
            Log.d(TAG, "Connected as group owner");
            try {
                handler = new GroupOwnerSocketHandler(
                        ((WiFiChatFragment.MessageTarget) this).getHandler());
                handler.start();
            } catch (IOException e) {
                Log.d(TAG,
                        "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            handler = new ClientSocketHandler(
                    ((WiFiChatFragment.MessageTarget) this).getHandler(),
                    p2pInfo.groupOwnerAddress);
            handler.start();
        }

        chatFragment = new WiFiChatFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.container_root, chatFragment).addToBackStack(null).commit();
        statusTxtView.setVisibility(View.GONE);
        currentVisibleFragment=chatFragment;
    }

    public void appendStatus(String status) {
        String current = statusTxtView.getText().toString();
        statusTxtView.setText(current + "\n" + status);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(currentVisibleFragment instanceof WiFiChatFragment){
            onBackFromChat();
        }

    }


    private void onBackFromChat() {
        manager.removeGroup(channel, new ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        manager.cancelConnect(channel, new ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        statusTxtView.setText("");
        statusTxtView.setVisibility(View.VISIBLE);
        refresh();

    }

    public String getMyUsername() {
        return myUsername;
    }

}