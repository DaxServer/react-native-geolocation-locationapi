package com.locationapi;

import android.location.Location;
import android.widget.Toast;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.unwiredlabs.locationapi.Location.LocationAdapter;
import com.unwiredlabs.locationapi.Location.UnwiredLocationListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by srihari on 17/4/16.
 */
public class LocationAPIModule extends ReactContextBaseJavaModule {
    private LocationAdapter locationAdapter;

    private static final String PRIORITY_ONLYGPS = "PRIORITY_ONLYGPS";
    private static final String PRIORITY_BALANCED_POWER_ACCURACY = "PRIORITY_BALANCED_POWER_ACCURACY";
    private static final String PRIORITY_HIGH_ACCURACY = "PRIORITY_HIGH_ACCURACY";
    private static final String PRIORITY_LOW_POWER = "PRIORITY_LOW_POWER";
    private static final String PRIORITY_NO_POWER = "PRIORITY_NO_POWER";

    public LocationAPIModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "LocationAPI";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        constants.put(PRIORITY_BALANCED_POWER_ACCURACY, LocationAdapter.PRIORITY_BALANCED_POWER_ACCURACY);
        constants.put(PRIORITY_HIGH_ACCURACY, LocationAdapter.PRIORITY_HIGH_ACCURACY);
        constants.put(PRIORITY_LOW_POWER, LocationAdapter.PRIORITY_LOW_POWER);
        constants.put(PRIORITY_NO_POWER, LocationAdapter.PRIORITY_NO_POWER);
        constants.put(PRIORITY_ONLYGPS, LocationAdapter.PRIORITY_ONLYGPS);

        return constants;
    }

    @ReactMethod
    public void init(String token) {
        final LocationAdapter locationAdapter = new LocationAdapter(getReactApplicationContext(), token);
        this.locationAdapter = locationAdapter;

        this.locationAdapter.setPriority(LocationAdapter.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @ReactMethod
    public void setPriority(int priority) {
        locationAdapter.setPriority(priority);
    }

    @ReactMethod
    public void getLastLocation() {
        Location lastLocation = locationAdapter.getLastLocation();

        if (lastLocation != null) {
            Toast.makeText(getReactApplicationContext(), lastLocation.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getReactApplicationContext(), "Unfortunate..!!", Toast.LENGTH_LONG).show();
        }
    }

    @ReactMethod
    public void getLocation(final Callback errorCallback, final Callback successCallback) {
        locationAdapter.setPriority(LocationAdapter.PRIORITY_HIGH_ACCURACY);
        locationAdapter.getLocation(new UnwiredLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    JSONObject geolocation = new JSONObject();

                    JSONObject coords = new JSONObject();

                    try {
                        coords.put("latitude", Double.valueOf(location.getLatitude()));
                        coords.put("longitude", Double.valueOf(location.getLongitude()));
                        coords.put("accuracy", Double.valueOf(location.getAccuracy()));

                        geolocation.put("coords", coords);
                        geolocation.put("timestamp", System.currentTimeMillis());

                        successCallback.invoke(geolocation.toString());
                    } catch (JSONException e) {
                        errorCallback.invoke(e.getStackTrace());
                    }
                } else {
                    errorCallback.invoke("Unfortunate from getLocation");
                }
            }
        });
    }
}
