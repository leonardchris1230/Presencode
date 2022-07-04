package com.lazilette.presencode.ui.geofences;

import static com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_DWELL;
import static com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_ENTER;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
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
                .setInitialTrigger(INITIAL_TRIGGER_DWELL)
                .build();

    }

    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes){
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude,radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }



    public String getErrorMessage(Exception e){
        if(e instanceof ApiException){
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()){
                case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE : return "Geofence not available";
                case GeofenceStatusCodes.API_NOT_CONNECTED:  return "Geofence not connected";
                case GeofenceStatusCodes.ERROR:  return "Geofence Error";
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:  return "too many pending intent";
                case GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION: return "insufficient permissions";
                case GeofenceStatusCodes.GEOFENCE_REQUEST_TOO_FREQUENT:  return "too frequent";
                case GeofenceStatusCodes.INTERNAL_ERROR: return "Internal error";

            }

        }
        return  e.getLocalizedMessage();
    }
    public PendingIntent getPendingIntent(){

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,2607,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
