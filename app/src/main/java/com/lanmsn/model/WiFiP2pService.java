package com.lanmsn.model;

import android.net.wifi.p2p.WifiP2pDevice;


public class WiFiP2pService {
    private  WifiP2pDevice device;
    private  String instanceName = null;
    private  String serviceRegistrationType = null;
    private  String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getServiceRegistrationType() {
        return serviceRegistrationType;
    }

    public void setServiceRegistrationType(String serviceRegistrationType) {
        this.serviceRegistrationType = serviceRegistrationType;
    }
}
