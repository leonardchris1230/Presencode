package com.lazilette.presencode.ui.geofences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    GeofencingEvent geofencingEvent;
    private Object myEdit;

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.

            Toast.makeText(context, "User berada di area BKPSDM :)", Toast.LENGTH_SHORT).show();



    }
}