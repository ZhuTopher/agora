package deltahacks3.agora;


import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import static android.R.attr.id;
import static android.R.id.list;
import static deltahacks3.agora.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    LocationManager locationManager;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    double longitude;
    double latitude;

    private class PolygonWrapper {
        String id;
        Polygon polygon;

        private PolygonWrapper(String id, Polygon polygon) {
            this.id = id;
            this.polygon = polygon;
        };
    }
    List<PolygonWrapper> polygonWrapperList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        if (googleServivcesAvailable()) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Toast.makeText(this, "Google service not present", Toast.LENGTH_LONG).show();
        }
    }

    public boolean googleServivcesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getApplicationContext());
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to google play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                    if (mGoogleApiClient == null) {
                        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();
                        mGoogleApiClient.connect();
                    }

                    //this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                    //Location location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //this.latitude =  location.getLatitude();
                    //this.longitude = location.getLongitude();


                    // create locationManager
                    // use network/gps provider for locationManager.requestLocationUpdates
                    // locationManager.getLastKnownLocation (get lat/lng)
                    // save lat/lng locally
                    // create Google Map using lap/lng for zoom




            }else{
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Error")
                        .setMessage("This app requires the Location permission.")
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();


            }
        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();


                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(map);
                mapFragment.getMapAsync(this);
            }
        }

    }

    public void drawCensus(GoogleMap mGoogleMap)
    {
        this.polygonWrapperList = new ArrayList<PolygonWrapper>();

        try {

            InputStream is = getAssets().open("workfile.txt");

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                String[] srcArr = line.split(",");
                String idName = srcArr[0];

                int lineCol = Color.argb(255, 0, 0, 100); //Set to some color declared w/in Android?
                int fillCol = Color.argb(125, 0, 0, 100); //As above? Incl. Transparency?
                PolygonOptions polyOpt = new PolygonOptions()
                        .strokeColor(lineCol)
                        .fillColor(fillCol)
                        .clickable(true);

                for (int i = 1; i < srcArr.length; i += 2) {
                    polyOpt.add(new LatLng(Double.parseDouble(srcArr[i]), Double.parseDouble(srcArr[i + 1])));
                }
                Polygon polygon = mGoogleMap.addPolygon(polyOpt);
                polygonWrapperList.add(new PolygonWrapper(idName, polygon));
            }
            br.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.toString());
            e.printStackTrace(System.out);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        mGoogleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                PolygonWrapper wrapper = getPolyWrapperForPoly(polygon);
                if (wrapper != null) {
                    Toast.makeText(getApplicationContext(),wrapper.id,Toast.LENGTH_SHORT).show();

                }
                /*
                System.out.println("I think you clicked a polygon :D");
                String textToPrint = "You clicked a polygon: "+polygon.getZIndex();
                Toast toast = Toast.makeText(getApplicationContext(),textToPrint,Toast.LENGTH_SHORT);
                toast.show();*/
            }
        });
    }

    private PolygonWrapper getPolyWrapperForPoly(Polygon polygon) {
        for (PolygonWrapper wrapper : this.polygonWrapperList) {
            if (wrapper.polygon.equals(polygon)) {
                return wrapper;
            } // else move on
        }

        return null; // default false value is null
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart () {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop () {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap= googleMap;
        goToLocationZoom(latitude,longitude,15);
        drawCensus(mGoogleMap);

    }
    private void goToLocationZoom(double lat,double lng, int zoom){
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update;
        update = CameraUpdateFactory.newLatLngZoom(ll,zoom);
        mGoogleMap.moveCamera(update);

    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}








