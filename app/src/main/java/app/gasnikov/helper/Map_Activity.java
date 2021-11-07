package app.gasnikov.helper;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import android.provider.Settings;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

import app.gasnikov.helper.Notifications.ApiService;
import app.gasnikov.helper.Notifications.Client;
import app.gasnikov.helper.Notifications.Data;
import app.gasnikov.helper.Notifications.Response;
import app.gasnikov.helper.Notifications.Sender;
import app.gasnikov.helper.Notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;


public class Map_Activity extends AppCompatActivity {
    private Boolean lp = false;
    private Button button;


    private static final int L_P_REQUEST_CODE = 12345;

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    public static final int ERROR_DIALOG_REQUEST = 9001;

    private static final String TAG = "MapActivity";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap map;
    private ImageView getloc;
    ApiService apiService;
    private UserLocation userLocation;
    private MarkerOptions markerOptions;
    private LocationModel locinc;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        //search = (EditText) findViewById(R.id.input);
        getloc=(ImageView)findViewById(R.id.curloc);
        button=(Button)findViewById(R.id.help);
        fusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(),"AIzaSyCFWCLLJDeJ7_mSoDWc_mzq3HmupHs7yrQ");
        AutocompleteSupportFragment autocompleteSupportFragment= (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG,Place.Field.ID,Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getAddress() + ", " + place.getLatLng()+", " +place.getId()+", "+ place.getName());
                Place a =place;
                LatLng b=a.getLatLng();
                LatLng l=new LatLng(b.latitude,b.longitude);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(l,15));
                 markerOptions = new MarkerOptions();
                markerOptions.position(l).title(getResources().getString(R.string.incident));
                map.clear();
                map.addMarker(markerOptions);

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG,"ERROR"+status);
            }
        });
        getloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
        apiService= Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locinc=new LocationModel(markerOptions.getPosition().latitude,markerOptions.getPosition().longitude);
                db.getReference("Incidents").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(locinc);
                sendnotify();
                Intent intent=new Intent(Map_Activity.this,Menu2.class);
                startActivity(intent);
            }

        });






    }
    private void sendnotify() {
        DatabaseReference tokens =db.getReference("Tokens");
        Query query1=tokens.orderByKey().endBefore(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Query query=tokens.orderByKey().startAfter(FirebaseAuth.getInstance().getCurrentUser().getUid());
        notify(query);
        notify(query1);
    }
    private void notify(Query query){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Token token=snapshot1.getValue(Token.class);
                    Data data=new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),R.mipmap.ic_launcher,"Help","New incident");
                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendnotification(sender).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if(response.code()==235){
                                if(response.body().success!=1){
                                    Toast.makeText(Map_Activity.this,"Fail",Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /*private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
            if (android.os.Build.VERSION.SDK_INT >= 26){
                this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("app.gasnikov.helper".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }*/
    //step one(retrieve the user detailes)
    private void getUser(){
        if(userLocation==null){
            userLocation=new UserLocation();
            db.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                    userLocation.setUser(user);
                    getDeviceLocation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }
    //step two(getting the gps coord)
    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Location curloc=(Location)task.getResult();
                        LatLng latLng=new LatLng(curloc.getLatitude(),curloc.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                         markerOptions = new MarkerOptions();
                        markerOptions.position(latLng).title(getResources().getString(R.string.incident));
                        map.clear();
                        map.addMarker(markerOptions);
                        LocationModel geoPoint = new LocationModel(curloc.getLatitude(),curloc.getLongitude());
                        userLocation.setGeo_point(geoPoint);
                        saveUserLocation();
                        Intent serviceIntent = new Intent(Map_Activity.this,LocationService.class);
                        if (android.os.Build.VERSION.SDK_INT >= 26){

                            Map_Activity.this.startForegroundService(serviceIntent);

                        }else{
                            startService(serviceIntent);
                        }

                    }
                    else{
                        Toast.makeText(Map_Activity.this,"unable to get the current location",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
    //step three(upload into firestore)
    private void saveUserLocation(){
        if(userLocation!=null){
            db.getReference("User Locations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userLocation);

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case PERMISSIONS_REQUEST_ENABLE_GPS:{
                if(lp){
                    initMap();
                    getUser();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "CALLED");

        lp = false;
        switch (requestCode) {
            case L_P_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            lp = true;
                            initMap();
                            getUser();

                        }

                    }
                }

        }
    }
    private void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map = googleMap;
                if (ActivityCompat.checkSelfPermission(Map_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Map_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    // init();
                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng latLng) {
                            //когда нажали на карту
                            MarkerOptions markerOptions = new MarkerOptions();

                            //установка позиции маркера
                            markerOptions.position(latLng);
                            markerOptions.title(getResources().getString(R.string.incident));
                            map.clear();
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            map.addMarker(markerOptions);

                            return;
                        }


                    });
                }
            }
        });
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lp = true;
                initMap();

                getUser();

            } else {
                ActivityCompat.requestPermissions(this, permissions, L_P_REQUEST_CODE);

            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, L_P_REQUEST_CODE);

        }


    }
    private void buildAlertMessageNoGps(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gps=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(gps,PERMISSIONS_REQUEST_ENABLE_GPS);
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Map_Activity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Map_Activity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }







    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(lp){
                initMap();

                getUser();

            }
            else{
                getLocationPermission();
            }
        }
    }
}
