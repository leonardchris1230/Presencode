package com.lazilette.presencode.ui.geofences;

import static com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_ENTER;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {
    PendingIntent pendingIntent;
    private static final String TAG = "GeofenceHelper";

    public GeofenceHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence){


        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

    }

    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes){
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude,radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)
                .build();
    }
    public PendingIntent getPendindIntent(){
        if(pendingIntent != null ){
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,2607,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
