package com.lanmsn.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;


/**
 * Created by Viktor on 9/6/2015.
 */
public class WiFiAlertDialog {

    private Activity activity;
    private AlertDialog dialog;

    public WiFiAlertDialog(Activity activity){

        this.activity=activity;
    }


    public void createDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage("Please check your Wi-Fi settings!")
                .setTitle("Wi-Fi unavailable!");

        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.getApplicationContext().startActivity(intent);
            }
        });


        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public AlertDialog getDialog(){
        return dialog;
    }
}
